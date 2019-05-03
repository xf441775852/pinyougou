package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

public class ItemSearchListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage)message;
        try {
            String itemListJson = textMessage.getText();
            //将json字符串转化为集合
            List<TbItem> itemList = JSON.parseArray(itemListJson,TbItem.class);
            //导入索引库
            itemSearchService.addItemList(itemList);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
