package com.bjpowernode.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/*
* 结果的枚举类
* */


@Getter
@ToString
@AllArgsConstructor
public enum ResultEnum {

    // 通用的
    SUCCESS(200, "成功"),
    ERROR(500, "服务端异常"),
    // 登录模块的具体错误
    LOGIN_ERROR(451, "用户名或密码错误"),
    MOBIL_ERROR(452, "手机不合法"),
    BIND_ERROR(453, "参数校验异常"),
    USER_NOT_EXIST(454, "用户不存在"),
    PASSWORD_UPDATE_FAIL(455, "用户不存在"),
    // 秒杀模块
    EMPTY_STOCK(461, "库存已空，抢购结束"),
    REPEATE_ERROR(462, "每人限购一件，请勿重复抢购"),
    // 订单模块
    ORDER_NOT_EXIST(471, "订单信息不存在");


    // 状态码
    private final Integer code;
    // 提示信息
    private final String message;



}
