package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderItemService;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import entity.PageResult;
import entity.Result;
import group.OrderAndOrderItem;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/order")
public class OrderController {

	@Reference
	private OrderService orderService;

	@Reference
	private OrderItemService orderItemService;
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbOrder> findAll(){			
		return orderService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return orderService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param order
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbOrder order){
		//获取登录人用户名
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		order.setUserId(userId);
		order.setSourceType("2");//订单来源 pc
		try {
			orderService.add(order);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param order
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbOrder order){
		try {
			orderService.update(order);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbOrder findOne(Long id){
		return orderService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			orderService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param order
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbOrder order, int page, int rows  ){
		return orderService.findPage(order, page, rows);		
	}

	@RequestMapping("/findOrder.do")
	public List<OrderAndOrderItem> findOrderAndOrderItem(){
		//获取登录名
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		List<OrderAndOrderItem> orderAndOrderItems = new ArrayList<>();
		List<TbOrder> orderList = orderService.findOrderList(userId);//获取该用户下所有订单
		for (TbOrder order:orderList){
			//创建对象
			OrderAndOrderItem orderAndOrderItem = new OrderAndOrderItem();
			//通过订单查询orderItem
			List<TbOrderItem> orderItemList = orderItemService.findOrderItemList(order.getOrderId());
			orderAndOrderItem.setOrder(order);
			orderAndOrderItem.setOrderItemList(orderItemList);
			orderAndOrderItems.add(orderAndOrderItem);
		}
		return orderAndOrderItems;
	}
	
}
