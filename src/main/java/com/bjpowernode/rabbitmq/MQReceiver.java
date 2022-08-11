package com.bjpowernode.rabbitmq;

/*
* 消息的消费者
* */

import com.bjpowernode.pojo.CreateOrderMessage;
import com.bjpowernode.pojo.SeckillOrder;
import com.bjpowernode.pojo.User;
import com.bjpowernode.service.IGoodsService;
import com.bjpowernode.service.IOrderService;
import com.bjpowernode.utils.JsonUtil;
import com.bjpowernode.vo.GoodsVo;
import com.bjpowernode.vo.Result;
import com.bjpowernode.vo.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MQReceiver {

    @Resource
    private IGoodsService goodsService;
    @Resource
    private IOrderService orderService;
    @Resource
    private RedisTemplate redisTemplate;

    private RedisTemplate getRedisTemplate(){
        this.redisTemplate.setKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        this.redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return this.redisTemplate;
    }

    @RabbitListener(queues = "seckillQueue")
    public void receive(String message){
        log.info("接收到消息：" + message);

        ValueOperations valueOperations = getRedisTemplate().opsForValue();

        CreateOrderMessage createOrderMessage = JsonUtil.jsonStr2Object(message, CreateOrderMessage.class);
        User user = createOrderMessage.getUser();
        Long goodsId = createOrderMessage.getGoodsId();
        GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
        if(goodsVo.getStockCount()<0){
            return;
        }

        // 判断用户是否重复抢购
        // 从redis缓存中获取秒杀订单
        SeckillOrder seckillOrder = (SeckillOrder) valueOperations.get("order:"+user.getId()+":"+goodsId);
        if(seckillOrder != null){
            return;
        }
        // 生成订单
        orderService.createOrder(user, goodsVo);
    }

}
