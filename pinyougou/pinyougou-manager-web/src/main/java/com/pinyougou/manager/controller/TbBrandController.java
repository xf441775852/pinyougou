package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.container.page.Page;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.TbBrandService;

import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tbBrand")
public class TbBrandController {
    @Reference
    private TbBrandService tbBrandService;
    @RequestMapping("/findAll.do")
    public List<TbBrand> findAll(){
        return tbBrandService.findAll();
    }

    @RequestMapping("/findPage.do")
    public PageResult findPage(int currentPage,int pageSize){
        return tbBrandService.findPage(currentPage,pageSize);
    }
    @RequestMapping("/add.do")
    public Result add(@RequestBody TbBrand tbBrand){
        return tbBrandService.add(tbBrand);
    }

    @RequestMapping("/findOne.do")
    public TbBrand findOne(long id){
        return tbBrandService.findOne(id);
    }

    @RequestMapping("/update.do")
    public Result update(@RequestBody TbBrand tbBrand){
        return tbBrandService.update(tbBrand);
    }
    @RequestMapping("/delete.do")
    public Result delete(long[] ids){
        return tbBrandService.delete(ids);
    }
    @RequestMapping("/search.do")
    public PageResult search(@RequestBody TbBrand tbBrand,int page,int pageSize){
        return tbBrandService.search(tbBrand,page,pageSize);
    }
    @RequestMapping("/selectOptionList.do")
    public List<Map> selectOptionList(){
        return tbBrandService.selectOptionList();
    }
}
