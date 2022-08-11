package com.bjpowernode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bjpowernode.pojo.Goods;
import com.bjpowernode.pojo.Order;
import com.bjpowernode.pojo.User;
import com.bjpowernode.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zwg
 * @since 2022-05-18
 */
public interface IOrderService extends IService<Order> {

    // 库存数目减1并生成抢购订单
    Order createOrder(User user, Goods goods);

    // 获取订单详情
    OrderDetailVo getOrderDetail(Long orderId);
}
