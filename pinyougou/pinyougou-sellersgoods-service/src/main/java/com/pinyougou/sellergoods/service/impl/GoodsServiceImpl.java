package com.pinyougou.sellergoods.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import group.GoodsAndGoodsDesc;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbBrandMapper tbBrandMapper;
    @Autowired
    private TbTypeTemplateMapper tbTypeTemplateMapper;
    @Autowired
    private TbSellerMapper tbSellerMapper;

    @Override
    public void add(GoodsAndGoodsDesc goodsAndGoodsDesc) {
        //设置商品申请状态
        goodsAndGoodsDesc.getTbGoods().setAuditStatus("0");
        goodsMapper.insert(goodsAndGoodsDesc.getTbGoods());

        goodsAndGoodsDesc.getTbGoodsDesc().setGoodsId((goodsAndGoodsDesc.getTbGoods().getId()));
        goodsDescMapper.insert(goodsAndGoodsDesc.getTbGoodsDesc());
        //添加item列表
        addItemList(goodsAndGoodsDesc);

    }

    //添加item列表
    private void addItemList(GoodsAndGoodsDesc goodsAndGoodsDesc){
        if (goodsAndGoodsDesc.getTbGoods().getIsEnableSpec() == "1") {
            //获取itemList
            List<TbItem> itemList = goodsAndGoodsDesc.getItemList();
            for (TbItem item : itemList) {
                //商品标题  SPU+SKU
                //SPU
                String title = goodsAndGoodsDesc.getTbGoods().getGoodsName();
                //SKU
                Map<String, Object> spec = JSON.parseObject(item.getSpec());
                for (String key : spec.keySet()) {
                    title += " " + key;
                }
                item.setTitle(title);
                setItemsValues(item,goodsAndGoodsDesc);
                tbItemMapper.insert(item);
            }
        } else {
            //只有一个item
            TbItem item = new TbItem();
            //商品标题SPU
            String title = goodsAndGoodsDesc.getTbGoods().getGoodsName();
            item.setTitle(title);
            //价格
            item.setPrice(goodsAndGoodsDesc.getTbGoods().getPrice());
            //库存量
            item.setNum(9000);
            //是否启用
            item.setStatus("1");
            //是否默认
            item.setIsDefault("1");
            setItemsValues(item,goodsAndGoodsDesc);
        }
    }

    //共用的item属性
    private void setItemsValues(TbItem item,GoodsAndGoodsDesc goodsAndGoodsDesc){
        //商品图片
        String itemImages = goodsAndGoodsDesc.getTbGoodsDesc().getItemImages();
        List<Map> maps = JSON.parseArray(itemImages, Map.class);
        if (maps.size() > 0) {
            String url = (String) maps.get(0).get("url");
            item.setImage(url);
        }
        //所属类目
        Long category3Id = goodsAndGoodsDesc.getTbGoods().getCategory3Id();
        item.setCategoryid(category3Id);
        //创建时间和更新时间
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        //商品id
        item.setGoodsId(goodsAndGoodsDesc.getTbGoods().getId());
        //商家id
        item.setSellerId(goodsAndGoodsDesc.getTbGoods().getSellerId());
        //品牌名称
        TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(goodsAndGoodsDesc.getTbGoods().getBrandId());
        item.setBrand(tbBrand.getName());
        //分类名称
        TbTypeTemplate tbTypeTemplate = tbTypeTemplateMapper.selectByPrimaryKey(goodsAndGoodsDesc.getTbGoods().getTypeTemplateId());
        item.setCategory(tbTypeTemplate.getName());
        //商家名称
        TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(goodsAndGoodsDesc.getTbGoods().getSellerId());
        item.setSeller(tbSeller.getNickName());
        tbItemMapper.insert(item);
    }
    /**
     * 修改
     */
    @Override
    public void update(GoodsAndGoodsDesc goodsAndGoodsDesc) {
        //修改商品基本信息
        goodsMapper.updateByPrimaryKey(goodsAndGoodsDesc.getTbGoods());
        //修改商品扩展属性
        goodsDescMapper.updateByPrimaryKey(goodsAndGoodsDesc.getTbGoodsDesc());
        //先删除itemList
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goodsAndGoodsDesc.getTbGoods().getId());
        tbItemMapper.deleteByExample(example);
        //再添加itemList
        addItemList(goodsAndGoodsDesc);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public GoodsAndGoodsDesc findOne(Long id) {
        GoodsAndGoodsDesc goodsAndGoodsDesc = new GoodsAndGoodsDesc();
        //商品基本信息
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goodsAndGoodsDesc.setTbGoods(tbGoods);
        //商品扩展属性
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goodsAndGoodsDesc.setTbGoodsDesc(tbGoodsDesc);
        //SKU列表
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> itemList = tbItemMapper.selectByExample(example);
        goodsAndGoodsDesc.setItemList(itemList);
        return goodsAndGoodsDesc;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsDelete("1");
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsDeleteIsNull();//只查询iddelete字段为null值的
        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdLike(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }
    //修改商品状态
    @Override
    public void updateStatus(Long[] ids, String status) {
        for(Long id:ids){
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

    @Override
    public List<TbItem> findItemListByGoodsIdandStatus(Long[] ids, String status) {

        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(status);//商品状态
        criteria.andGoodsIdIn(Arrays.asList(ids));

        List<TbItem> itemList = tbItemMapper.selectByExample(example);
        return itemList;
    }

}
