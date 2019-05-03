package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.sun.tools.javac.jvm.Items;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    public void addList(){
        //从数据库输出列表数据
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//有效状态
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        //遍历tbItems
        for (TbItem tbItem:tbItems){
            //获取spec，将字符串转化成json对象
            Map specMap = JSON.parseObject(tbItem.getSpec(), Map.class);
            tbItem.setSpecMap(specMap);//给带注解的字段复制
        }
        //将数据输入到solr中
        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();
    }

    public static void main(String[] args) {
        //获取容器
        ApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        //获取对象
        SolrUtil solrUtil = (SolrUtil) applicationContext.getBean("solrUtil");
        solrUtil.addList();
    }
}
