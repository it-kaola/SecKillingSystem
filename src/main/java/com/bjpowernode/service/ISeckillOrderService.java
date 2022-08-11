package com.bjpowernode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bjpowernode.pojo.SeckillOrder;
import com.bjpowernode.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zwg
 * @since 2022-05-18
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    // 获取最终的抢购信息，得到orderId表示秒杀成功，-1表示秒杀失败，0表示正在排队中
    Long getResult(User user, Long goodsId);
}
