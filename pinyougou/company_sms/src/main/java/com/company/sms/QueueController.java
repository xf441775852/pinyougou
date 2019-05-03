package com.company.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class QueueController {
    @Autowired
    private JmsTemplate jmsTemplate;
    @RequestMapping("/send")
   public void send(){
        Map map=new HashMap<>();
        map.put("phone", "18080797769");
        map.put("template_code", "SMS_161598215");
        map.put("sign_name", "熊锋的小店");
        map.put("param", "{\"code\":\"5200\"}");
        jmsTemplate.convertAndSend("sms",map);
   }
}
