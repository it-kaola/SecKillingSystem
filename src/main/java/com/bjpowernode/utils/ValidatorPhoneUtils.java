package com.bjpowernode.utils;

/*
* 验证手机是否合法的工具类
* */

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorPhoneUtils {

    private static final Pattern mobilPattern = Pattern.compile("0?(13|14|15|18|17)[0-9]{9}");


    public static boolean isLegalPhone(String mobile){
        if(StringUtils.isEmpty(mobile)){
            return false;
        }
        Matcher matcher = mobilPattern.matcher(mobile);
        return matcher.matches();
    }


}
