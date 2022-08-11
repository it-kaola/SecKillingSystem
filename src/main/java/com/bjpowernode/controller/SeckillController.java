package com.bjpowernode.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bjpowernode.pojo.*;
import com.bjpowernode.rabbitmq.MQSender;
import com.bjpowernode.service.IGoodsService;
import com.bjpowernode.service.IOrderService;
import com.bjpowernode.service.ISeckillOrderService;
import com.bjpowernode.utils.JsonUtil;
import com.bjpowernode.vo.GoodsVo;
import com.bjpowernode.vo.Result;
import com.bjpowernode.vo.ResultEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Resource
    private IGoodsService goodsService;
    @Resource
    private ISeckillOrderService seckillOrderService;
    @Resource
    private IOrderService orderService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private MQSender mqSender;
    @Resource
    private DefaultRedisScript<Long> redisScript;

    // 用来标记秒杀商品库存是否已经空了，true表示库存已空，false表示还有库存
    private Map<Long, Boolean> emptyStockMap = new HashMap<>();

    private RedisTemplate getRedisTemplate(){
        this.redisTemplate.setKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        this.redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return this.redisTemplate;
    }


    /**
     * 优化前：
     * windows QPS：785
     * linux QPS：172
     */

    // 实现秒杀功能
    @RequestMapping(value = "/doSeckill")
    public String doSeckill(Model model, User user, Long goodsId){
        if(user == null){
            return "login";
        }
        model.addAttribute("user", user);
        // 根据商品id查询库存数量
        Goods goods = goodsService.getById(goodsId);
        // 库存小于0，跳转到失败页面
        if(goods.getGoodsStock() < 0){
            model.addAttribute("errorMsg", ResultEnum.EMPTY_STOCK.getMessage());
            return "seckillFail";
        }
        // 判断用户是否重复抢购
        QueryWrapper<SeckillOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        queryWrapper.eq("goods_id", goodsId);
        SeckillOrder seckillOrder = seckillOrderService.getOne(queryWrapper);
        if(seckillOrder != null){
            model.addAttribute("errorMsg", ResultEnum.REPEATE_ERROR.getMessage());
            return "seckillFail";
        }
        // 库存数目减1并生成抢购订单
        Order order = orderService.createOrder(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);

        return "orderDetail";
    }


    /*
    * windows 优化前
    *   QPS：1356
    *   QPS：2456
    * */
    // 实现秒杀功能（优化）
    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public Result doSeckill(User user, Long goodsId){

        ValueOperations valueOperations = getRedisTemplate().opsForValue();

        if(user == null){
            return Result.error(ResultEnum.USER_NOT_EXIST);
        }

        // 从redis缓存中获取秒杀订单
        SeckillOrder seckillOrder = (SeckillOrder) valueOperations.get("order:"+user.getId()+":"+goodsId);
        if(seckillOrder != null){
            return Result.error(ResultEnum.REPEATE_ERROR);
        }

        // 通过标记位，减少对redis的访问
        if(emptyStockMap.get(goodsId)){
            return Result.error(ResultEnum.EMPTY_STOCK);
        }

        // decrement()方法具有原子性，返回的是对应key的value值自减1后的结果
        // Long productStock = valueOperations.decrement("seckillGoods:" + goodsId);

        // 使用redis分布式锁
        Long productStock = (Long) redisTemplate.execute(redisScript, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);

        if(productStock < 0){
            // 程序执行到此处表示商品已被秒杀完
            emptyStockMap.put(goodsId, true);
            // valueOperations.increment("seckillGoods:" + goodsId); // increment()方法也具有原子性
            return Result.error(ResultEnum.EMPTY_STOCK);
        }

        CreateOrderMessage createOrderMessage = new CreateOrderMessage(user, goodsId);
        // 将消息放入消息队列中，异步处理请求
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(createOrderMessage));

        return Result.success(0);

        /*// 根据商品id查询库存数量
        Goods goods = goodsService.getById(goodsId);
        // 库存小于0，跳转到失败页面
        if(goods.getGoodsStock() < 0){
            // model.addAttribute("errorMsg", ResultEnum.EMPTY_STOCK.getMessage());
            return Result.error(ResultEnum.EMPTY_STOCK);
        }

        // 从redis缓存中获取秒杀订单
        SeckillOrder seckillOrder = (SeckillOrder) getRedisTemplate().opsForValue().get("order:"+user.getId()+":"+goodsId);
        if(seckillOrder != null){
            // model.addAttribute("errorMsg", ResultEnum.REPEATE_ERROR.getMessage());
            return Result.error(ResultEnum.REPEATE_ERROR);
        }
        // 库存数目减1并生成抢购订单
        Order order = orderService.createOrder(user, goods);*/

    }

    // 获取最终的抢购信息，得到orderId表示秒杀成功，-1表示秒杀失败，0表示正在排队中
    @RequestMapping(value = "/getResult")
    @ResponseBody
    public Result getResult(User user, Long goodsId){
        if(user == null){
            return Result.error(ResultEnum.USER_NOT_EXIST);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return Result.success(orderId);

    }

    /*
    * 系统初始化时，把商品库存数量加载到redis中
    * */
    @Override
    public void afterPropertiesSet() throws Exception {
        ValueOperations valueOperations = getRedisTemplate().opsForValue();
        List<GoodsVo> goodsVoList = goodsService.getGoodsVoList();
        if(CollectionUtils.isEmpty(goodsVoList)){
            return;
        }
        for(GoodsVo goodsVo : goodsVoList){
            // 将所有秒杀商品的库存预存到redis中
            valueOperations.set("seckillGoods:"+goodsVo.getId(), goodsVo.getStockCount());
            emptyStockMap.put(goodsVo.getId(), false);
        }
    }
}
