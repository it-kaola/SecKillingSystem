package com.bjpowernode.exception;

import com.bjpowernode.vo.Result;
import com.bjpowernode.vo.ResultEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /*@ExceptionHandler(Exception.class)
    public Result ExceptionHandler(Exception e){
        if(e instanceof GlobalException){
            GlobalException exception = (GlobalException) e;
            return Result.error(exception.getResultEnum());
        }else if(e instanceof BindException){
            BindException exception = (BindException) e;
            Result result = Result.error(ResultEnum.BIND_ERROR);
            result.setMessage("参数校验异常：" + exception.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return result;
        }
        return Result.error(ResultEnum.ERROR);
    }*/

    @ExceptionHandler(GlobalException.class)
    public Result doGlobalException(GlobalException exception){
        return Result.error(exception.getResultEnum());
    }

    @ExceptionHandler(BindException.class)
    public Result doGlobalException(BindException exception){
        Result result = Result.error(ResultEnum.BIND_ERROR);
        result.setMessage("参数校验异常：" + exception.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return result;
    }


}
