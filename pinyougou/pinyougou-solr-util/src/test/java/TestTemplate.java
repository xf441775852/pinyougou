import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:spring/applicationContext-solr.xml")
public class TestTemplate {
    @Autowired
    private SolrTemplate solrTemplate;
    @Test
    public void testAdd(){
        TbItem item = new TbItem();
        item.setId(1L);
        item.setTitle("三星 Note II (N7100) 钻石粉 联通3G手机");
        item.setPrice(new BigDecimal(8000.00));
        item.setSeller("三星旗舰店");
        item.setCategory("手机");
        item.setBrand("三星");
        item.setGoodsId(9527L);

        solrTemplate.saveBean(item);
        solrTemplate.commit();
    }
    @Test
    public void select(){

        TbItem item = solrTemplate.getById(1L, TbItem.class);
        System.out.println(item.getTitle());
    }
    @Test
    public void deletById(){
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }
    @Test
    public void addList(){
        List<TbItem> list = new ArrayList<TbItem>();
        for (int i=0;i<100;i++){
            TbItem item = new TbItem();
            item.setId(i+1L);
            item.setTitle("三星 Note II (N7100) 钻石粉 联通3G手机"+i);
            item.setPrice(new BigDecimal(8000.00+i*20));
            item.setSeller("三星旗舰店");
            item.setCategory("手机");
            item.setBrand("三星");
            item.setGoodsId(9527L+i);
            list.add(item);
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }
    @Test
    public void findPage(){
        Query query = new SimpleQuery("*:*");
        query.setOffset(10);
        query.setRows(10);
        ScoredPage<TbItem> tbItems = solrTemplate.queryForPage(query, TbItem.class);
        List<TbItem> content = tbItems.getContent();
        System.out.println(tbItems.getTotalPages());
        for (TbItem item:content){
            System.out.println(item);
        }
    }
    @Test
    public void selectByCondition(){
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_title").contains("2");
        criteria.and("item_title").contains("5");
        query.addCriteria(criteria);
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        System.out.println(page.getTotalPages());
        List<TbItem> tbItems = page.getContent();
        for (TbItem item:tbItems){
            System.out.println(item);
        }
    }
    @Test
    public void dele(){
        Query query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
