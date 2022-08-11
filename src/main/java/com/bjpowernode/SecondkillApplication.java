package com.bjpowernode;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.bjpowernode.mapper")
public class SecondkillApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecondkillApplication.class, args);
    }

}
