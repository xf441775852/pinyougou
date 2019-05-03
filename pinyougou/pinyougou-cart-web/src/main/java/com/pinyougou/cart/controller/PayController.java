package com.pinyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPay;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.IdWorker;

import javax.rmi.CORBA.Tie;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference(timeout = 6000)
    private WeixinPay weixinPay;
    @RequestMapping("/createNative.do")
    public Map createNative(){
        //雪花算法生成商户订单号
        IdWorker idWorker = new IdWorker();
        return weixinPay.createNative(idWorker.nextId()+"","1");
    }
}
