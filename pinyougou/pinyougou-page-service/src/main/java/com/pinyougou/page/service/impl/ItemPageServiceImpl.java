package com.pinyougou.page.service.impl;


import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import javassist.runtime.Desc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {
    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public boolean genItemHtml(Long goodsId) {
        try {
            //创建configuration对象
            Configuration configuration = freeMarkerConfig.getConfiguration();
            //创建模板对象
            Template template = configuration.getTemplate("item.ftl");
            Map dataModel = new HashMap();
            //查询数据库
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc",goodsDesc);

            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods",goods);
            //通过goodid查出三级分类
            Long category1Id = goods.getCategory1Id();
            TbItemCat itemCat1 = itemCatMapper.selectByPrimaryKey(category1Id);
            dataModel.put("category1",itemCat1.getName());

            Long category2Id = goods.getCategory2Id();
            TbItemCat itemCat2 = itemCatMapper.selectByPrimaryKey(category2Id);
            dataModel.put("category2",itemCat2.getName());

            Long category3Id = goods.getCategory3Id();
            TbItemCat itemCat3 = itemCatMapper.selectByPrimaryKey(category3Id);
            dataModel.put("category3",itemCat3.getName());

            //通过goodid查询SKU集合
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");//商品状态为1
            criteria.andGoodsIdEqualTo(goodsId);
            example.setOrderByClause("is_default desc");//按默认排序
            List<TbItem> itemList = itemMapper.selectByExample(example);
            dataModel.put("itemList",itemList);
            //创建输出对象
            Writer out = new FileWriter(new File(pagedir+goodsId+".html"));
            template.process(dataModel,out);
            //关闭流
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (TemplateException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteItemHtml(Long[] ids) {
        try{
            for (Long goodsId:ids){
                new File(pagedir+goodsId+".html").delete();
            }
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
