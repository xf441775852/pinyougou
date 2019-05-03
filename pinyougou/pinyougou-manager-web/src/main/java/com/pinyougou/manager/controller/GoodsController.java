package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import entity.Result;
import group.GoodsAndGoodsDesc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.*;
import java.util.List;

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
	@Autowired
	private Destination queueSolrDeleteDestination;
	@RequestMapping("/delete.do")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);

			//itemSearchService.deleItemList(ids);
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					ObjectMessage objectMessage = session.createObjectMessage(ids);
					return objectMessage;
				}
			});


			//删除静态页面
			for (Long goodsId:ids){
				jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createObjectMessage(ids);
					}
				});
			}
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
		return goodsService.findPage(goods, page, rows);		
	}

	/**
	 * 修改商品状态
	 * @param ids
	 * @param status
	 */
	/*@Reference(timeout = 10000)
	private ItemSearchService itemSearchService;
	@Reference(timeout = 50000)
	private ItemPageService itemPageService;*/
	@Autowired
	private Destination topicPageDestination;
	@Autowired
	private Destination topicPageDeleteDestination;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Queue queueSolrDestination;
	@RequestMapping("/updateStatus.do")
	public Result updateStatus(Long[] ids, String status){
		try {
			goodsService.updateStatus(ids,status);
			//审核后取出SKU
			if ("1".equals(status)){//审核通过
				List<TbItem> itemList = goodsService.findItemListByGoodsIdandStatus(ids, status);
				if (itemList.size()>0){
					//取出SKU将其存入solr中
					//itemSearchService.addItemList(itemList);

					//将itemList转化成json字符串
					final String items = JSON.toJSONString(itemList);
					jmsTemplate.send(queueSolrDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							TextMessage textMessage = session.createTextMessage(items);
							return textMessage;
						}
					});
				}else{
					System.out.println("没有明细数据");
				}
			}
			//静态页面生成
			//每一个审核通过的商品都生成详情页面
			for(Long goodsId:ids){
				jmsTemplate.send(topicPageDestination, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return	session.createTextMessage(goodsId+"");

					}
				});
			}

			return new Result(true,"成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"失败");
		}
	}
}
