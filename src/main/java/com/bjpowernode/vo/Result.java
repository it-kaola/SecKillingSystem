package com.bjpowernode.vo;

/*
* 统一的返回对象
* */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    // 状态码
    private Integer code;
    // 提示信息
    private String message;
    // 相应对象
    private Object obj;

    public static Result success(){
        return new Result(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), null);
    }

    public static Result success(Object obj){
        return new Result(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), obj);
    }

    public static Result error(ResultEnum resultEnum){
        return new Result(resultEnum.getCode(), resultEnum.getMessage(), null);
    }

    public static Result error(ResultEnum resultEnum, Object obj){
        return new Result(resultEnum.getCode(), resultEnum.getMessage(), obj);
    }

}
