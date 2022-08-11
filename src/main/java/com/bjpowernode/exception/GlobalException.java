package com.bjpowernode.exception;

/*
* 全局异常
* */

import com.bjpowernode.vo.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalException extends RuntimeException {

    private ResultEnum resultEnum;
}
