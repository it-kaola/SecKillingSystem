package com.bjpowernode.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjpowernode.pojo.Goods;
import com.bjpowernode.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zwg
 * @since 2022-05-18
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVo> getGoodsVoList();

    GoodsVo getGoodsVoById(Long goodsId);

}
