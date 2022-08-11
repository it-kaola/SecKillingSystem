package com.bjpowernode.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/*
* 消息的发送者（提供者）
* */

@Component
@Slf4j
public class MQSender {

    @Resource
    RabbitTemplate rabbitTemplate;

    public void sendSeckillMessage(String message){
        log.info("发送消息：" + message);
        rabbitTemplate.convertAndSend("topicExchange", "seckill.mseeage", message);
    }

}
