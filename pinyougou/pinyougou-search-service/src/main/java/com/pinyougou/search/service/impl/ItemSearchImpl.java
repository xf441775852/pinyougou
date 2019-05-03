package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.jms.core.JmsTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Filter;

@Service(timeout = 5000)
public class ItemSearchImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        Map<String, Object> map = new HashMap();
        //高亮查询
        map.putAll(searchList(searchMap));
        //分组查询分类列表
        //通过显示的模板名称获取模板id
        List<String> categoryList = searchCategory(searchMap);
        map.put("categoryList", categoryList);

        //查询品牌和规格列表
        if (!"".equals(searchMap.get("category"))){//条件中有商品分类
            map.putAll(getBrandListAndSpecList((String)searchMap.get("category")));
        } else{
            if (categoryList.size() > 0) {
                map.putAll(getBrandListAndSpecList(categoryList.get(0)));
            }
        }

        return map;


    }

    @Override
    public void addItemList(List itemList) {
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    @Override
    public void deleItemList(Long[] ids) {
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(ids);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    private Map searchList(Map searchMap) {
        //去除搜索关键词的空字符串
        String keywords = (String) searchMap.get("keywords");
        if (!"".equals(keywords)&&keywords!=null){
        searchMap.put("keywords",keywords.replace(" ", ""));
        }
        Map<String, Object> map = new HashMap();

        HighlightQuery query = new SimpleHighlightQuery();
        //设置高亮的域
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        //设置高亮前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //设置高亮后缀
        highlightOptions.setSimplePostfix("</em>");
        //设置高亮选项
        query.setHighlightOptions(highlightOptions);
        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //商品分类过滤
        if (!"".equals(searchMap.get("category"))){
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(filterCriteria);
            //过滤查询
            query.addFilterQuery(filterQuery);
        }

        //商品品牌过滤
        if (!"".equals(searchMap.get("brand"))){
            FilterQuery filterQuery = new SimpleFilterQuery();
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(filterCriteria);
            //过滤查询
            query.addFilterQuery(filterQuery);
        }

        //商品规格过滤
        if (searchMap.get("spec")!=null){
            FilterQuery filterQuery = new SimpleFilterQuery();
            //遍历规格列表
            Map<String,String> specMap = (Map) searchMap.get("spec");
            for (String key:specMap.keySet()){
                Criteria filterCriteria = new Criteria("item_spec_" + key).is(specMap.get(key));
                filterQuery.addCriteria(filterCriteria);
                //过滤查询
                query.addFilterQuery(filterQuery);
            }
        }

        //价格过滤,priceStr='0-500'...
        String priceStr = (String) searchMap.get("price");
        //分割字符串
        String[] price = priceStr.split("-");
        //判断如果price[0]不为0时
        if (!"".equals(searchMap.get("price"))){
            if (!"0".equals(price[0])){//如果为0，就不设置下线
                FilterQuery filterQuery = new SimpleFilterQuery();
                //增加条件小于这个价格
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if (!"*".equals(price[1])){//如果为*，就不设置上线
                FilterQuery filterQuery = new SimpleFilterQuery();
                //增加条件大于这个价格
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //按价格排序
        //判断是否需要按排序查询
        String sortValue = (String) searchMap.get("sort");//排序方式
        String sortField = (String) searchMap.get("sortField");//排序的字段
        if(!"".equals(sortValue)){
            if("ASC".equals(sortValue)){//升序
                Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);
            }
            if ("DESC".equals(sortValue)){//降序
                Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort);
            }
        }



        /****************   分页   ****************/
        Integer pageNum = (Integer) searchMap.get("pageNum");//当前页
        Integer pageSize = (Integer) searchMap.get("pageSize");//每页记录数
        if (searchMap.get("pageNum")==null){
            pageNum = 1;
        }
        if (searchMap.get("pageSize")==null){
            pageSize = 5;
        }
        //分页设置
        query.setOffset((pageNum-1)*2);
        query.setRows(pageSize);



        /***************** 获取高亮结果集 ********************/
        //高亮页对象，包含所有的高亮记录，一条记录为一个TbItem
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //高亮入口集合，获取所有的高亮记录
        //查询有keyword的所有高亮域
        List<HighlightEntry<TbItem>> entryList = page.getHighlighted();
        for (HighlightEntry<TbItem> h : entryList) {
            //获取原实体类
            TbItem item = h.getEntity();
            //设置高亮的结果
            /**
             * h.getHighlights()-----获取每条高亮记录的所有高亮域
             * h.getHighlights().get(0).getSnipplets()------获取第一个高亮域的内容
             * h.getHighlights().get(0).getSnipplets().get(0)-------一个高亮域中可能存在多值
             **/
            if (h.getHighlights().size()>0&&h.getHighlights().get(0).getSnipplets().size()>0) {
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
            }
        }
        map.put("rows", page.getContent());
        map.put("totalPages",page.getTotalPages());//总页数
        map.put("total",page.getTotalElements());//总记录数
        return map;
    }

    private List<String> searchCategory(Map searchMap) {
        List<String> list = new ArrayList<>();

        Query query = new SimpleQuery();
        //条件查询，相当于where
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);


        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        //分组查询,获得分组页对象
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集（有可能有多个列分组，要指定是哪一列）
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : content) {
            //分组结果
            String groupValue = entry.getGroupValue();
            list.add(groupValue);
        }
        return list;
    }

    //从缓存中取出品牌和规格
    @Autowired
    private RedisTemplate redisTemplate;

    private Map getBrandListAndSpecList(String category) {
        Map map = new HashMap();

        Long categoryId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        //通过模板id获取品牌列表
        if (categoryId != null) {
            List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(categoryId);
            //将品牌列表存入map中
            map.put("brandList", brandList);
        }
        //获取规格列表
        if (categoryId != null) {
            List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(categoryId);
            map.put("specList", specList);
        }
        return map;
    }
}
