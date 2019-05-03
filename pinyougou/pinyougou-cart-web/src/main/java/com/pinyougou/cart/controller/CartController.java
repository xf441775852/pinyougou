package com.pinyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.cart.service.CartService;
import entity.Result;
import group.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.rmi.CORBA.Util;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference(timeout = 60000)
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @RequestMapping("/getCartList.do")
    public List<Cart> getCartList(){
        //获取当前登陆人用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //未登录，从cookie中获取购物车信息
        String cartListStr = CookieUtil.getCookieValue(request, "cartList","UTF-8");
        if (cartListStr==null || cartListStr.equals("")){
            cartListStr="[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListStr,Cart.class);
        //判断是否登录
        if ("anonymousUser".equals(name)){//未登录,返回cookie购物车信息
            return cartList_cookie;
        }else{//已登录
            List<Cart> cartList_redis = cartService.getCartListFromRedis(name);
            if (cartList_redis==null){
                cartList_redis = new ArrayList<>();
            }
            if (cartList_cookie.size()>0){//本地存在购物车
                //合并购物车
                cartList_redis = cartService.mergeCartList(cartList_redis, cartList_cookie);
              //合并后删除cookie数据
                CookieUtil.deleteCookie(request,response,"cartList");
                //合并后的传入redis
                cartService.saveCartListToRedis(name,cartList_redis);
            }
            return cartList_redis;
        }




    }

    @RequestMapping("/addGoodsToCart.do")
    @CrossOrigin(origins = "http://localhost:9009",allowCredentials = "true")
    public Result addGoodsToCart(Long itemId, Integer num){
        //获取当前登陆人用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            //将购物车列表更新
            List<Cart> cartList = getCartList();
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            //判断是否登录
            if (!"anonymousUser".equals(name)){//已登录,将更新后的购物车列表存入redis中
                cartService.saveCartListToRedis(name,cartList);
            }else{
                System.out.println("存入cookie中");
                String cartList_cookie = JSON.toJSONString(cartList);
                CookieUtil.setCookie(request,response,"cartList",cartList_cookie,3600*24,"utf-8");
            }
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
}
