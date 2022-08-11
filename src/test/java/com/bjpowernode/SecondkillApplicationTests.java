package com.bjpowernode;

import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SecondkillApplicationTests {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private DefaultRedisScript<Boolean> redisScript;

    private RedisTemplate getRedisTemplate(){
        this.redisTemplate.setKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        this.redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return this.redisTemplate;
    }

    @Test
    void testLock01() {
        ValueOperations valueOperations = getRedisTemplate().opsForValue();
        // setIfAbsent()当redis中没有对应key时才返回true，否则返回false，true相当于锁抢占成功，false表示当前锁被占用，抢占失败
        Boolean isLock = valueOperations.setIfAbsent("k1", "v1");
        if(isLock){
            /*
            * 当在释放锁之前出现异常时，锁无法正常释放，会造成死锁现象
            * */
            System.out.println("当前线程抢占redis分布式锁成功");
            // 删除对应key，相当于释放锁
            getRedisTemplate().delete("k1");
        }else{
            System.out.println("有线程再使用，请稍后再试");
        }
    }


    @Test
    void testLock02() {
        ValueOperations valueOperations = getRedisTemplate().opsForValue();
        // 设置一个期限，假设线程抢占锁成功后需要3秒的执行时间，锁设置5秒过期，无论当前线程执行过程是否会抛出异常，都会释放锁，避免死锁现象
        Boolean isLock = valueOperations.setIfAbsent("k1", "v1", 5, TimeUnit.SECONDS);
        if(isLock){
            System.out.println("当前线程抢占redis分布式锁成功");
            // 删除对应key，相当于释放锁
            getRedisTemplate().delete("k1");
            Integer.parseInt("xcxxx");
        }else{
            System.out.println("有线程再使用，请稍后再试");
        }
    }


    @Test
    void testLock03() {
        ValueOperations valueOperations = getRedisTemplate().opsForValue();
        // 设置一个期限，假设线程抢占锁成功后需要3秒的执行时间，锁设置5秒过期，无论当前线程执行过程是否会抛出异常，都会释放锁，避免死锁现象
        // 但在实际开发中，无法确切知道线程抢占锁后需要的执行时间是多少；可以将value值设置为随机值
        // 释放锁时要去查看所得value，比较value是否一致，释放锁总共三个步骤，这三个步骤不具备原子性，要使用lua脚本。
        String value = UUID.randomUUID().toString();
        Boolean isLock = valueOperations.setIfAbsent("k1", value, 5, TimeUnit.SECONDS);
        if(isLock){
            System.out.println("当前线程抢占redis分布式锁成功");
            System.out.println(valueOperations.get("k1")); // 获取随机值
            Boolean result = (Boolean) redisTemplate.execute(redisScript, Collections.singletonList("k1"), value);
            System.out.println(result);
        }else{
            System.out.println("有线程再使用，请稍后再试");
        }
    }

}
