package com.bjpowernode.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bjpowernode.pojo.Goods;
import com.bjpowernode.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zwg
 * @since 2022-05-18
 */
public interface IGoodsService extends IService<Goods> {

    // 获取商品展示列表
    List<GoodsVo> getGoodsVoList();

    // 跳转到商品的详情页
    GoodsVo getGoodsVoById(Long goodsId);
}
