package com.bjpowernode.controller;


import com.bjpowernode.pojo.User;
import com.bjpowernode.rabbitmq.MQSender;
import com.bjpowernode.vo.Result;
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
 * @since 2022-05-16
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private MQSender mqSender;

    // 测试使用
    @RequestMapping("/info")
    @ResponseBody
    public Result info(User user){
        return Result.success(user);
    }
}
