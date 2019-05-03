package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.soap.Text;

public class ItemPageListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage)message;
            String idsStr = textMessage.getText();
            itemPageService.genItemHtml(Long.parseLong(idsStr));
            System.out.println("成功生成静态页面");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
