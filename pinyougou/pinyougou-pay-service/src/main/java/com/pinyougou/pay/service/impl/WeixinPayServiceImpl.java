package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPay;
import org.springframework.beans.factory.annotation.Value;
import util.HttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
@Service
public class WeixinPayServiceImpl implements WeixinPay {
    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${notifyurl}")
    private String notifyurl;
    @Value("${partnerkey}")
    private String partnerkey;
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        Map map = new HashMap();
        //封裝參數
        map.put("appid",appid);//公众账号ID
        map.put("mch_id",partner);//商户号
            //生成随机字符串
            String nonce_str = WXPayUtil.generateNonceStr();
        map.put("nonce_str",nonce_str);//随机字符串
        map.put("body","品优购");//商品描述
        map.put("out_trade_no",out_trade_no);//商户订单号
        map.put("total_fee",total_fee);//商户金额
        map.put("spbill_create_ip","127.0.0.1");//终端IP
        map.put("notify_url","http://www.itcast.com");//通知地址
        map.put("trade_type","NATIVE");//交易类型

        try {
            //将map转化为xml
            String xmlParam = WXPayUtil.generateSignedXml(map, partnerkey);


            //发送数据
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);

            httpClient.setXmlParam(xmlParam);
            //发送
            httpClient.post();
            //返回结果
            String content = httpClient.getContent();
            //从结果中取出需要返回的值
            Map<String, String> result = WXPayUtil.xmlToMap(content);
            Map resultMap = new HashMap();
            resultMap.put("code_url",result.get("code_url"));//支付地址
            resultMap.put("out_trade_no",out_trade_no);
            resultMap.put("total_fee",total_fee);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        //1.封装参数
        Map param=new HashMap();
        param.put("appid", appid);
        param.put("mch_id", partner);
        param.put("out_trade_no", out_trade_no);
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            //2.发送请求
            HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();

            //3.获取结果
            String xmlResult = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(xmlResult);
            System.out.println("调动查询API返回结果："+xmlResult);

            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map closePay(String out_trade_no) {
        Map param=new HashMap();
        param.put("appid", appid);//公众账号ID
        param.put("mch_id", partner);//商户号
        param.put("out_trade_no", out_trade_no);//订单号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        String url="https://api.mch.weixin.qq.com/pay/closeorder";
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client=new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();
            String result = client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            System.out.println(map);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
