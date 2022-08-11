package com.bjpowernode.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bjpowernode.mapper.SeckillOrderMapper;
import com.bjpowernode.pojo.SeckillOrder;
import com.bjpowernode.pojo.User;
import com.bjpowernode.service.ISeckillOrderService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zwg
 * @since 2022-05-18
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {

    @Resource
    RedisTemplate redisTemplate;

    private RedisTemplate getRedisTemplate(){
        this.redisTemplate.setKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        this.redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return this.redisTemplate;
    }


    // 获取最终的抢购信息，得到orderId表示秒杀成功，-1表示秒杀失败，0表示正在排队中
    @Override
    public Long getResult(User user, Long goodsId) {
        QueryWrapper<SeckillOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        queryWrapper.eq("goods_id", goodsId);

        SeckillOrder seckillOrder = baseMapper.selectOne(queryWrapper);


        if(seckillOrder != null){
            return seckillOrder.getOrderId();
        }else if(getRedisTemplate().hasKey("isStockEmpty:" + goodsId)){
            return -1L;
        }else{
            return 0L;
        }
    }
}
