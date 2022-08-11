package com.bjpowernode.vo;

import com.bjpowernode.pojo.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVo extends Goods {

    // 秒杀价
    private BigDecimal seckillPrice;

    // 库存数量
    private Integer stockCount;

    // 秒杀开始时间
    private Date startDate;

    // 秒杀结束时间
    private Date endDate;

}
