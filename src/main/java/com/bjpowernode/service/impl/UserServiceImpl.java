package com.bjpowernode.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bjpowernode.exception.GlobalException;
import com.bjpowernode.mapper.UserMapper;
import com.bjpowernode.pojo.User;
import com.bjpowernode.service.IUserService;
import com.bjpowernode.utils.CookieUtil;
import com.bjpowernode.utils.MD5Utils;
import com.bjpowernode.utils.UUIDUtil;
import com.bjpowernode.vo.LoginVo;
import com.bjpowernode.vo.Result;
import com.bjpowernode.vo.ResultEnum;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zwg
 * @since 2022-05-16
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {


    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private RedisTemplate getRedisTemplate(){
        this.redisTemplate.setKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        this.redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return this.redisTemplate;
    }

    // 实现登录验证与用户登录
    @Override
    public Result doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        User user = baseMapper.selectById(mobile);
        if(null == user){
           throw new GlobalException(ResultEnum.LOGIN_ERROR);
        }
        // 判断密码是否正确
        if(! MD5Utils.formPassToDBPass(password, user.getSalt()).equals(user.getPassword())){
            throw new GlobalException(ResultEnum.LOGIN_ERROR);
        }
        // 生成cookie
        String ticket = UUIDUtil.uuid();
        // 将用户信息存入redis中，其中key为cookie值，value为user对象
        getRedisTemplate().opsForValue().set("user"+ticket, user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);

        return Result.success(ticket);
    }


    // 根据cookie值，在redis缓存中获取User对象
    @Override
    public User getUser(String key, HttpServletRequest request, HttpServletResponse response) {
        if(StringUtils.isEmpty(key)){
            return null;
        }
        User user = (User) getRedisTemplate().opsForValue().get("user" + key);
        if(user != null){
            CookieUtil.setCookie(request, response, "userTicket", key);
        }
        return user;
    }


    // 用户更新密码
    @Override
    public Result updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = this.getUser(userTicket, request, response);
        if(user == null){
            throw new GlobalException(ResultEnum.USER_NOT_EXIST);
        }
        user.setPassword(MD5Utils.inputPassToDBPass(password, user.getSalt()));
        int count = baseMapper.updateById(user);
        if(count == 1){
            // 程序执行到此处说明用户信息更新成功，需要删除缓存，保持数据库与redis缓存数据的一致性
            redisTemplate.delete("user"+userTicket);
        }
        return Result.error(ResultEnum.PASSWORD_UPDATE_FAIL);
    }


}
