package com.pinyougou.cart.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbSeller;
import group.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbSellerMapper sellerMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //通过SKUId来查询SKU列表
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        //判断是否存在SKU列表,或者SKU状态不为1
        if (item!=null || item.getStatus()!="1"){
            //通过SKU列表查询商家名称（购物车）
            String sellerId = item.getSellerId();
            String sellerName = (sellerMapper.selectByPrimaryKey(sellerId)).getNickName();
            //通过商家名查询购物车
            Cart cart = searchCartBySellerName(cartList, sellerName);
            //判断购物车列表中是否存在此购物车
            if (cart==null){//不存在此购物车
                //添加购物车
                cart = new Cart();
                cart.setSellerId(sellerId);
                cart.setSellerName(sellerName);
                List<TbOrderItem> orderItemList = new ArrayList<>();
                orderItemList.add(createTbOrderItem(item, num));
                cart.setOrderItemList(orderItemList);//将商品添加到购物车中
                cart.setSellerName(sellerName);
                cart.setSellerId(sellerId);
                cartList.add(cart);
            }else {//存在此购物车
                //继续判断该购物车明细中是否存在加入的商品
                TbOrderItem orderItem = searchOrderItemByItemId(cart, itemId);
                if (orderItem==null){//不存在此商品
                    //加入商品明细
                    cart.getOrderItemList().add(createTbOrderItem(item,num));
                }else {//存在此商品,增加商品数量
                    orderItem.setNum(orderItem.getNum()+num);
                    //更改总金额
                    orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()) );
                    //如果商品数量小于等于0就删除商品
                    if (orderItem.getNum()<=0){
                        cart.getOrderItemList().remove(orderItem);
                    }
                }
                //如果购物车没有商品，删除此购物车
                if (cart.getOrderItemList().size()==0){
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }
    //通过用户名从redis中查询购物车
    @Override
    public List<Cart> getCartListFromRedis(String username) {
        System.out.println("从redis中取出");
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        return cartList;
    }
    //保存购物车到redis中
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("存入redis中");
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }
    //合并购物车
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        if (cartList2.size()==0 || cartList2 ==null){
            return cartList1;
        }
        for (Cart cart:cartList2){
            for (TbOrderItem tbOrderItem:cart.getOrderItemList()){
                cartList1 = addGoodsToCartList(cartList1,tbOrderItem.getItemId(),tbOrderItem.getNum());
            }
        }

        return cartList1;
    }



    public TbOrderItem createTbOrderItem(TbItem item,Integer num){
        if (num<=0){
            throw new RuntimeException("数量非法");
        }
        TbOrderItem tbOrderItem = new TbOrderItem();
        tbOrderItem.setItemId(item.getId());
        tbOrderItem.setTitle(item.getTitle());
        tbOrderItem.setGoodsId(item.getGoodsId());
        tbOrderItem.setPrice(item.getPrice());
        tbOrderItem.setNum(num);
        tbOrderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        tbOrderItem.setPicPath(item.getImage());
        tbOrderItem.setSellerId(item.getSellerId());
        return tbOrderItem;
    }
    /**
     * 判断购物车列表中是否存在此购物车
     * @return
     */
    public Cart searchCartBySellerName(List<Cart> cartList,String sellerName){
            for (Cart cart:cartList){
                if (sellerName.equals(cart.getSellerName())){//存在此购物车
                    return cart;
                }
            }
        //不存在此购物车
        return null;
    }
    public TbOrderItem searchOrderItemByItemId(Cart cart,Long itemId){
        //继续判断该购物车中是否存在加入的商品明细
        for(TbOrderItem orderItem:cart.getOrderItemList()){
            if (itemId.longValue()==orderItem.getItemId()){//购物车中存在加入的商品明细
                return orderItem;
            }
        }
        return null;
    }
}
