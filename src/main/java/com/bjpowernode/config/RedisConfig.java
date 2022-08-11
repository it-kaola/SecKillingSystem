package com.bjpowernode.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Controller;

@Controller
public class RedisConfig {

    @Bean
    public DefaultRedisScript<Long> script(){
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("stock.lua")); // 指定lua脚本的位置
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
