package com.pinyougou.cart.service;

import group.Cart;

import java.util.List;

public interface CartService {

    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

    /**
     * 通过用户名从redis中查询购物车
     * @param username
     * @return
     */
    public List<Cart> getCartListFromRedis(String username);

    /**
     * 保存购物车到redis中
     * @param username
     * @param cartList
     * @return
     */
    public void saveCartListToRedis(String username,List<Cart> cartList);

    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);
}
