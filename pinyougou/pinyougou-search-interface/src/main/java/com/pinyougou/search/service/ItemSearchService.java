package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     * 搜索方法
     * @param searchMap
     * @return
     */
    public Map<String,Object> search(Map searchMap);

    /**
     * 添加SKU
     * @param itemList
     */
    public void addItemList(List itemList);

    /**
     * 删除solr中SKU
     * @param ids
     */
    public void deleItemList(Long[] ids);
}
