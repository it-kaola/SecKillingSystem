package com.bjpowernode.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bjpowernode.mapper.GoodsMapper;
import com.bjpowernode.pojo.Goods;
import com.bjpowernode.service.IGoodsService;
import com.bjpowernode.vo.GoodsVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zwg
 * @since 2022-05-18
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Resource
    GoodsMapper goodsMapper;


    @Override
    public List<GoodsVo> getGoodsVoList() {
        return goodsMapper.getGoodsVoList();
    }


    @Override
    public GoodsVo getGoodsVoById(Long goodsId) {
        return goodsMapper.getGoodsVoById(goodsId);
    }
}
