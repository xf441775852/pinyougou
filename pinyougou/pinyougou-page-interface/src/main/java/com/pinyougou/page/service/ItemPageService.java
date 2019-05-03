package com.pinyougou.page.service;
//生成商品详情页接口
public interface ItemPageService {

    public boolean genItemHtml(Long goodsId);

    /**
     * 删除商品详情页
     * @param ids
     * @return
     */
    public boolean deleteItemHtml(Long[] ids);
}
