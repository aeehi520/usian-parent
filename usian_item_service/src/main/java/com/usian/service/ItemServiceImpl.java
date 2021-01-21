package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemCatMapper;
import com.usian.mapper.TbItemDescMapper;
import com.usian.mapper.TbItemMapper;
import com.usian.mapper.TbItemParamItemMapper;
import com.usian.pojo.*;
import com.usian.redis.RedisClient;
import com.usian.utils.IDUtils;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName : ItemServiceImpl
 * @Author : lenovo
 * @Date: 2021/1/5 19:14
 */
@Service
@Transactional
public class ItemServiceImpl implements ItemService {
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private TbItemDescMapper tbItemDescMapper;
    @Autowired
    private TbItemParamItemMapper tbItemParamItemMapper;
    @Autowired
    private TbItemCatMapper tbItemCatMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private RedisClient redisClient;
    @Value("${ITEM_INFO_BASE}")
    private String ITEM_INFO_BASE;
    @Value("${ITEM_INFO_DESC}")
    private String ITEM_INFO_DESC;
    @Value("${ITEM_INFO_PARAM}")
    private String ITEM_INFO_PARAM;
    @Value("${ITEM_INFO_EXPIRE}")
    private Integer ITEM_INFO_EXPIRE;
    @Value("${SETNX_BASC_LOCK_KEY}")
    private String SETNX_BASC_LOCK_KEY;
    @Value("${SETNX_DESC_LOCK_KEY}")
    private String SETNX_DESC_LOCK_KEY;
    @Value("${SETNX_PARAM_LOCK_KEY}")
    private String SETNX_PARAM_LOCK_KEY;



    @Override
    public TbItem selectItemInfo(Long itemId) {
        TbItem item = (TbItem) redisClient.hget(ITEM_INFO_BASE, itemId.toString());
        if (item!=null){
            return item;
        }
        if (redisClient.setnx(SETNX_BASC_LOCK_KEY+":"+itemId,itemId,30)){
            item = tbItemMapper.selectByPrimaryKey(itemId);

            if (item==null){
                redisClient.hset(ITEM_INFO_BASE,itemId.toString(),new TbItem());
                redisClient.expire(ITEM_INFO_BASE,30);
            }else{
                redisClient.hset(ITEM_INFO_BASE,itemId.toString(),item);
                redisClient.expire(ITEM_INFO_BASE,ITEM_INFO_EXPIRE);
            }
            redisClient.del(SETNX_BASC_LOCK_KEY+":"+itemId);
            return item;
        }else{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return selectItemInfo(itemId);
        }
    }

    @Override
    public PageResult selectTbItemAllByPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        TbItemExample tbItemExample = new TbItemExample();
        tbItemExample.setOrderByClause("updated desc");
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andStatusEqualTo((byte)1);
        List<TbItem> tbItemList = tbItemMapper.selectByExample(tbItemExample);
        for (TbItem tbItem : tbItemList) {
            tbItem.setPrice(tbItem.getPrice()/100);
        }
        PageInfo<TbItem> tbItemPageInfo = new PageInfo<>(tbItemList);
        PageResult pageResult = new PageResult();
        pageResult.setResult(tbItemPageInfo.getList());
        pageResult.setPageIndex(tbItemPageInfo.getPageNum());
        pageResult.setTotalPage(Long.valueOf(tbItemPageInfo.getPages()));
        return pageResult;
    }

    @Override
    public Integer insertTbItem(TbItem tbItem, String desc, String itemParams) {
        Long itemId = IDUtils.genItemId();
        Date date = new Date();
        tbItem.setId(itemId);
        tbItem.setStatus((byte)1);
        tbItem.setUpdated(date);
        tbItem.setCreated(date);
        tbItem.setPrice(tbItem.getPrice()*100);
        int tbItemNum = tbItemMapper.insertSelective(tbItem);

        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemId(itemId);
        tbItemDesc.setItemDesc(desc);
        tbItemDesc.setUpdated(date);
        tbItemDesc.setCreated(date);
        int tbItemDescNum = tbItemDescMapper.insert(tbItemDesc);

        TbItemParamItem tbItemParamItem = new TbItemParamItem();
        tbItemParamItem.setItemId(itemId);
        tbItemParamItem.setParamData(itemParams);
        tbItemParamItem.setCreated(date);
        tbItemParamItem.setUpdated(date);
        int tbItemParamItemNum = tbItemParamItemMapper.insertSelective(tbItemParamItem);
        redisClient.hdel(ITEM_INFO_BASE,itemId.toString());
        redisClient.hdel(ITEM_INFO_DESC,itemId.toString());
        redisClient.hdel(ITEM_INFO_PARAM,itemId.toString());
        //添加商品发布消息到mq
        amqpTemplate.convertAndSend("item_exchange","item.add",itemId);
        return tbItemNum+tbItemDescNum+tbItemParamItemNum;
    }

    @Override
    public Integer deleteItemById(Long itemId) {
        int num1 = tbItemMapper.deleteByPrimaryKey(itemId);
        int num2 = tbItemDescMapper.deleteByPrimaryKey(itemId);
        TbItemParamItemExample example = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = example.createCriteria();
        criteria.andItemIdEqualTo(itemId);
        int num3 = tbItemParamItemMapper.deleteByExample(example);
        redisClient.hdel(ITEM_INFO_BASE,itemId.toString());
        redisClient.hdel(ITEM_INFO_DESC,itemId.toString());
        redisClient.hdel(ITEM_INFO_PARAM,itemId.toString());
        //添加商品发布消息到mq
        amqpTemplate.convertAndSend("item_exchange","item.delete",itemId);
        return num1+num2+num3;
    }

    @Override
    public Map<String, Object> preUpdateItem(Long itemId) {
        HashMap<String, Object> map = new HashMap<>();
        //查询商品
        TbItem item = tbItemMapper.selectByPrimaryKey(itemId);
        map.put("item",item);
        //查询商品描述
        TbItemDesc itemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);
        map.put("itemDesc",itemDesc.getItemDesc());
        //查询商品类目
        TbItemCat itemCat = tbItemCatMapper.selectByPrimaryKey(item.getCid());
        map.put("itemCat",itemCat.getName());
        //查询商品规格信息
        TbItemParamItemExample tbItemParamItemExample = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = tbItemParamItemExample.createCriteria();
        criteria.andItemIdEqualTo(itemId);
        List<TbItemParamItem> list = tbItemParamItemMapper.selectByExampleWithBLOBs(tbItemParamItemExample);
        if (list!=null&&list.size()>0){
            map.put("itemParamItem",list.get(0).getParamData());
        }
        return map;
    }

    @Override
    public Integer updateTbItem(TbItem tbItem, String desc, String itemParams) {
        Date date = new Date();
        //修改商品
        tbItem.setUpdated(date);
        int num1 = tbItemMapper.updateByPrimaryKeySelective(tbItem);
        //修改商品描述
        TbItemDesc itemDesc = new TbItemDesc();
        itemDesc.setUpdated(date);
        itemDesc.setItemId(tbItem.getId());
        itemDesc.setItemDesc(desc);
        int num2 = tbItemDescMapper.updateByPrimaryKeySelective(itemDesc);
        //修改商品规格
        TbItemParamItemExample example = new TbItemParamItemExample();
        TbItemParamItemExample.Criteria criteria = example.createCriteria();
        criteria.andItemIdEqualTo(tbItem.getId());
        List<TbItemParamItem> list = tbItemParamItemMapper.selectByExampleWithBLOBs(example);
        list.get(0).setParamData(itemParams);
        redisClient.hdel(ITEM_INFO_BASE,tbItem.getId().toString());
        redisClient.hdel(ITEM_INFO_DESC,tbItem.getId().toString());
        redisClient.hdel(ITEM_INFO_PARAM,tbItem.getId().toString());
        amqpTemplate.convertAndSend("item_exchange","item.update",tbItem.getId());
        int num3 = tbItemParamItemMapper.updateByPrimaryKeyWithBLOBs(list.get(0));
        return num1+num2+num3;
    }

    @Override
    public TbItemDesc selectItemDescByItemId(Long itemId) {
        TbItemDesc itemDesc = (TbItemDesc) redisClient.hget(ITEM_INFO_DESC, itemId.toString());
        if (itemDesc!=null){
            return itemDesc;
        }
        if (redisClient.setnx(SETNX_DESC_LOCK_KEY+":"+itemId,itemId,30)){
            itemDesc = tbItemDescMapper.selectByPrimaryKey(itemId);
            if (itemDesc==null){
                redisClient.hset(ITEM_INFO_DESC,itemId.toString(),new TbItemDesc());
                redisClient.expire(ITEM_INFO_DESC,30);
            }else{
                redisClient.hset(ITEM_INFO_DESC,itemId.toString(),itemDesc);
                redisClient.expire(ITEM_INFO_DESC,ITEM_INFO_EXPIRE);
            }
            redisClient.del(SETNX_DESC_LOCK_KEY+":"+itemId);
            return itemDesc;
        }else{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return selectItemDescByItemId(itemId);
        }

    }

    @Override
    public TbItemParamItem selectTbItemParamItemByItemId(Long itemId) {
        TbItemParamItem itemParamItem = (TbItemParamItem) redisClient.hget(ITEM_INFO_PARAM, itemId.toString());
        if (itemParamItem!=null){
            return itemParamItem;
        }
        if (redisClient.setnx(SETNX_PARAM_LOCK_KEY+":"+itemId,itemId,30)){
            TbItemParamItemExample example = new TbItemParamItemExample();
            TbItemParamItemExample.Criteria criteria = example.createCriteria();
            criteria.andItemIdEqualTo(itemId);
            List<TbItemParamItem> tbItemParamItems = tbItemParamItemMapper.selectByExampleWithBLOBs(example);
            if (tbItemParamItems.size()>0&&tbItemParamItems!=null){
                itemParamItem = tbItemParamItems.get(0);
            }
            if (itemParamItem==null){
                redisClient.hset(ITEM_INFO_PARAM,itemId.toString(),new TbItemParamItem());
                redisClient.expire(ITEM_INFO_PARAM,30);
            }else{
                redisClient.hset(ITEM_INFO_PARAM,itemId.toString(),itemParamItem);
                redisClient.expire(ITEM_INFO_PARAM,ITEM_INFO_EXPIRE);
            }
            redisClient.del(SETNX_PARAM_LOCK_KEY+":"+itemId);
            return itemParamItem;
        }else{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return selectTbItemParamItemByItemId(itemId);
        }

    }
}
