package com.bjpowernode.pojo;

/*
* 生成订单所需要的信息，封装成一个对象，方便发送到消息队列中
* */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderMessage {

    private User user;
    private Long goodsId;

}
