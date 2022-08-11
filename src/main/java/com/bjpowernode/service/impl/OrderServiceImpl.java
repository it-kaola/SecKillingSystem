package com.bjpowernode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bjpowernode.exception.GlobalException;
import com.bjpowernode.mapper.OrderMapper;
import com.bjpowernode.pojo.*;
import com.bjpowernode.service.IGoodsService;
import com.bjpowernode.service.IOrderService;
import com.bjpowernode.service.ISeckillGoodsService;
import com.bjpowernode.service.ISeckillOrderService;
import com.bjpowernode.vo.GoodsVo;
import com.bjpowernode.vo.OrderDetailVo;
import com.bjpowernode.vo.ResultEnum;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zwg
 * @since 2022-05-18
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Resource
    private ISeckillGoodsService seckillGoodsService;

    @Resource
    private ISeckillOrderService seckillOrderService;

    @Resource
    private IGoodsService iGoodsService;

    @Resource
    private RedisTemplate redisTemplate;


    private RedisTemplate getRedisTemplate(){
        this.redisTemplate.setKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        this.redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return this.redisTemplate;
    }


    @Transactional
    @Override
    public Order createOrder(User user, Goods goods) {

        // 减少库存数量
        // 先根据商品id获得被抢购的商品
        QueryWrapper<SeckillGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("goods_id", goods.getId());
        SeckillGoods seckillGoods = seckillGoodsService.getOne(queryWrapper);
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);

        UpdateWrapper<SeckillGoods> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("stock_count=stock_count-1");
        updateWrapper.eq("goods_id", goods.getId());
        updateWrapper.gt("stock_count", 0);
        // result 为boolean值，表示更新成功或更新失败
        boolean result = seckillGoodsService.update(updateWrapper);

        if(seckillGoods.getStockCount() < 0){
            // 判断是否还有库存
            getRedisTemplate().opsForValue().set("isStockEmpty:" + seckillGoods.getGoodsId(), "0");
            return null;
        }

        // 生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(goods.getGoodsPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        baseMapper.insert(order);

        // 生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrderService.save(seckillOrder);
        getRedisTemplate().opsForValue().set("order:"+user.getId()+":"+seckillOrder.getGoodsId(), seckillOrder);

        return order;

    }


    // 获取订单详情
    @Override
    public OrderDetailVo getOrderDetail(Long orderId) {
        if(orderId == null){
            throw new GlobalException(ResultEnum.ORDER_NOT_EXIST);
        }

        Order order = baseMapper.selectById(orderId);
        GoodsVo goodsVo = iGoodsService.getGoodsVoById(order.getGoodsId());

        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrder(order);
        orderDetailVo.setGoodsVo(goodsVo);

        return orderDetailVo;
    }
}
