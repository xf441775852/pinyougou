package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;
import entity.Result;

import java.util.List;
import java.util.Map;

public interface TbBrandService {
    /**
     * 查询所有品牌
     * @return
     */
    public List<TbBrand> findAll();

    /**
     * 分页查询
     * @param currentPage
     * @param pageSize
     * @return
     */
    public PageResult findPage(int currentPage,int pageSize);

    /**
     * 添加品牌
     * @param tbBrand
     * @return
     */
    public Result add(TbBrand tbBrand);

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    public TbBrand findOne(Long id);

    /**
     * 修改品牌信息
     * @param tbBrand
     * @return
     */
    public Result update(TbBrand tbBrand);

    /**
     * 通过id删除
     * @param ids
     * @return
     */
    public Result delete(long[] ids);

    /**
     * 条件查询
     * @param tbBrand
     * @param page
     * @param pageSize
     * @return
     */
    public PageResult search(TbBrand tbBrand,int page,int pageSize);

    public List<Map> selectOptionList();
}
