package com.bjpowernode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bjpowernode.pojo.User;
import com.bjpowernode.vo.LoginVo;
import com.bjpowernode.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zwg
 * @since 2022-05-16
 */
public interface IUserService extends IService<User> {

    // 实现登录验证与用户登录
    Result doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);


    // 通过redis，根据key获取用户信息
    User getUser(String key, HttpServletRequest request, HttpServletResponse response);


    // 用户更新密码
    Result updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);
}
