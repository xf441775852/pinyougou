package com.pinyougou.shop.controller;
import java.util.List;

import group.GoodsAndGoodsDesc;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goodsAndGoodsDesc
	 * @return
	 */
	@RequestMapping("/add.do")
	public Result add(@RequestBody GoodsAndGoodsDesc goodsAndGoodsDesc){
		try {
			//设置商家登录名
			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			goodsAndGoodsDesc.getTbGoods().setSellerId(name);
			goodsService.add(goodsAndGoodsDesc);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goodsAndGoodsDesc
	 * @return
	 */
	@RequestMapping("/update.do")
	public Result update(@RequestBody GoodsAndGoodsDesc goodsAndGoodsDesc){
		//登录的用户
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();

		try {
			//其他商家
			TbGoods tbGoods2 = goodsAndGoodsDesc.getTbGoods();
			if(!tbGoods2.getSellerId().equals(sellerId) ||
			!goodsAndGoodsDesc.getTbGoods().getSellerId().equals(sellerId)){

					return new Result(false, "非法操作");
			}
			goodsService.update(goodsAndGoodsDesc);
			return new Result(true, "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "保存失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne.do")
	public GoodsAndGoodsDesc findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search.do")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		//获取登录商家名
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(name);
		return goodsService.findPage(goods, page, rows);		
	}
	
}
