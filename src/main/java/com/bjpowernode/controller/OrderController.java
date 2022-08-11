package com.bjpowernode.controller;


import com.bjpowernode.pojo.User;
import com.bjpowernode.service.IOrderService;
import com.bjpowernode.vo.OrderDetailVo;
import com.bjpowernode.vo.Result;
import com.bjpowernode.vo.ResultEnum;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zwg
 * @since 2022-05-18
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Resource
    private IOrderService orderService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result orderDetail(User user, long orderId){
        if(user == null){
            return Result.error(ResultEnum.USER_NOT_EXIST);
        }
        OrderDetailVo orderDetailVo = orderService.getOrderDetail(orderId);
        return Result.success(orderDetailVo);
    }

}
