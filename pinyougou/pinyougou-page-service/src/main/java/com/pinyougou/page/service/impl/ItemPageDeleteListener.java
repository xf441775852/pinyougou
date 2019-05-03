package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

public class ItemPageDeleteListener implements MessageListener {
    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage object = (ObjectMessage)message;
        try {
            Long[] ids = (Long[])object.getObject();
            itemPageService.deleteItemHtml(ids);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
