package com.pinyougou.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.pojo.TbSeckillOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class SeckillTask {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    //每天上午10点，下午2点，4点  (cron = "0 0 10,14,16 * * ?")
    //朝九晚五工作时间内每半小时  (cron = "0 0/30 9-17 w * ?")
    //每天上午10:15触发       (cron = "0 15 10 * * ?")
    //每年三月的星期三的下午2:10和2:44触发   (cron = "0 10,44 14 ? 3 4")
    @Scheduled(cron = "0 * * * * ?")
    public void refreshSeckillGoods(){
        //查询缓存中所有参加秒杀商品的id
        List ids =new ArrayList(redisTemplate.boundHashOps("seckillGoods").keys());
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//商品状态为1
        criteria.andStockCountGreaterThan(0);//商品库存要大于0
        criteria.andStartTimeLessThanOrEqualTo(new Date());//当前时间要大于等于开始时间
        criteria.andEndTimeGreaterThanOrEqualTo(new Date());//当前时间要小于等于开始时间
        criteria.andIdNotIn(ids);//查询缓存中没有的id商品
        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
        //将商品存入redis中
        for (TbSeckillGoods seckillGoods:seckillGoodsList){
            redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(),seckillGoods);
        }
    }
    //过期秒杀商品的移除
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeSeckillGoods(){
        //在内存中查询所有商品
        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
        //判断结束时间
        for (TbSeckillGoods seckillGoods:seckillGoodsList){
            if (seckillGoods.getEndTime().getTime() < System.currentTimeMillis()){//过期
                //向数据库中存储
                seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
                //从缓存中删除
                redisTemplate.boundHashOps("seckillGoods").delete(seckillGoods.getId());
            }
        }

    }
}
