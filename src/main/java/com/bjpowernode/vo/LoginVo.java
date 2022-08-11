package com.bjpowernode.vo;

/*
* 登录所用的vo对象
* */

import com.bjpowernode.validator.IsMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.NonNull;

@Data
public class LoginVo {

    @NonNull
    @IsMobile
    private String mobile;

    @NonNull
    @Length(min = 32)
    private String password;
}
