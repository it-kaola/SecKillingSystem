package com.bjpowernode.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Utils {

    private static final String salt = "1a2b3c4d";

    // md5加密
    public static String md5(String str){
        return DigestUtils.md5Hex(str);
    }

    // 第一次加密
    public static String inputPassToFromPass(String inputPassWord){
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPassWord + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    // 第二次加密
    public static String formPassToDBPass(String formPass, String salt){
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDBPass(String inputPass, String salt){
        return formPassToDBPass(inputPassToFromPass(inputPass), salt);
    }

    public static void main(String[] args) {
        // d3b1294a61a07da9b49b6e22b2cbd7f9
        System.out.println(inputPassToFromPass("123456"));
        System.out.println(formPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9", "1a2b3c4d"));
        System.out.println(inputPassToDBPass("123456", "1a2b3c4d"));
    }

}
