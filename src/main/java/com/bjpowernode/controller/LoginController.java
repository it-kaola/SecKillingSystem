package com.bjpowernode.controller;

import com.bjpowernode.service.IUserService;
import com.bjpowernode.vo.LoginVo;
import com.bjpowernode.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {

    @Resource
    private IUserService iUserService;

    // 跳转到登录页面
    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    // 登录
    @RequestMapping("/doLogin")
    @ResponseBody
    public Result doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){
        return iUserService.doLogin(loginVo, request, response);
    }

}
