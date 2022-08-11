package com.bjpowernode.controller;

import com.bjpowernode.pojo.User;
import com.bjpowernode.service.IGoodsService;
import com.bjpowernode.vo.DetailVo;
import com.bjpowernode.vo.GoodsVo;
import com.bjpowernode.vo.Result;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Resource
    private IGoodsService iGoodsService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ThymeleafViewResolver thymeleafViewResolver;


    /*
    * 显示商品列表接口
    * windows优化前QPS：1332
    * Windows优化后（加页面有缓存）QPS：2342
    * */
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response){

        // 将页面缓存到redis中，如果不为空，直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if(! StringUtils.isEmpty(html)){
            return html;
        }

        model.addAttribute("user", user);
        List<GoodsVo> goodsVoList = iGoodsService.getGoodsVoList();
        model.addAttribute("goodsList", goodsVoList);

        // 如果为空，手动渲染，再存到redis中
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if(! StringUtils.isEmpty(html)){
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }
        return html;
    }


    // 跳转到商品的详情页
    @RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail(@PathVariable("goodsId") Long goodsId, User user, Model model, HttpServletRequest request, HttpServletResponse response){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
        if(! StringUtils.isEmpty(html)){
            return html;
        }
        model.addAttribute("user", user);
        GoodsVo goodsVo = iGoodsService.getGoodsVoById(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int secKillStatus; // 秒杀状态
        int countdown = 0; // 秒杀倒计时
        if(nowDate.before(startDate)){
            // 程序执行到此处说明秒杀还未开始
            secKillStatus = 0;
            countdown = (int) ((startDate.getTime()-nowDate.getTime())/1000);
        }else if(nowDate.after(endDate)){
            // 程序执行到此处说明秒杀已结束
            secKillStatus = 2;
            countdown = -1;
        }else{
            // 程序执行到此处说明秒杀正在进行
            secKillStatus = 1;
            countdown = 0;
        }
        model.addAttribute("goods", goodsVo);
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", countdown);
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);
        if(! StringUtils.isEmpty(html)){
            valueOperations.set("goodsDetail:" + goodsId, html, 60, TimeUnit.SECONDS);
        }
        return html;

    }



    // 跳转到商品的详情页（静态页面）
    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result toDetail(@PathVariable("goodsId") Long goodsId, User user){
        GoodsVo goodsVo = iGoodsService.getGoodsVoById(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int secKillStatus; // 秒杀状态
        int countdown = 0; // 秒杀倒计时
        if(nowDate.before(startDate)){
            // 程序执行到此处说明秒杀还未开始
            secKillStatus = 0;
            countdown = (int) ((startDate.getTime()-nowDate.getTime())/1000);
        }else if(nowDate.after(endDate)){
            // 程序执行到此处说明秒杀已结束
            secKillStatus = 2;
            countdown = -1;
        }else{
            // 程序执行到此处说明秒杀正在进行
            secKillStatus = 1;
            countdown = 0;
        }
        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSecKillStatus(secKillStatus);
        detailVo.setRemainSeconds(countdown);
        return Result.success(detailVo);

    }

}
