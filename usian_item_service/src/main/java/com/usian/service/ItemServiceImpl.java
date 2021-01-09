package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemCatMapper;
import com.usian.mapper.TbItemDescMapper;
import com.usian.mapper.TbItemMapper;
import com.usian.mapper.TbItemParamItemMapper;
import com.usian.pojo.*;
import com.usian.utils.IDUtils;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public TbItem selectItemInfo(Long itemId) {
        return tbItemMapper.selectByPrimaryKey(itemId);
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
        long itemId = IDUtils.genItemId();
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
        int num3 = tbItemParamItemMapper.updateByPrimaryKeyWithBLOBs(list.get(0));
        return num1+num2+num3;
    }
}
