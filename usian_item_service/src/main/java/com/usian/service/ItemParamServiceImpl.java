package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbItemParamItemMapper;
import com.usian.mapper.TbItemParamMapper;
import com.usian.pojo.TbItemParam;
import com.usian.pojo.TbItemParamExample;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @ClassName : ItemParamServiceImpl
 * @Author : lenovo
 * @Date: 2021/1/6 9:43
 */
@Service
@Transactional
public class ItemParamServiceImpl implements ItemParamService{
    @Autowired
    private TbItemParamMapper tbItemParamMapper;

    @Override
    public TbItemParam selectItemParamByItemCatId(Long itemCatId) {
        TbItemParamExample tbItemParamExample = new TbItemParamExample();
        TbItemParamExample.Criteria criteria = tbItemParamExample.createCriteria();
        criteria.andItemCatIdEqualTo(itemCatId);
        List<TbItemParam> tbItemParams = tbItemParamMapper.selectByExampleWithBLOBs(tbItemParamExample);
        if (tbItemParams!=null &&tbItemParams.size()>0){
            return tbItemParams.get(0);
        }
        return null;
    }

    @Override
    public PageResult selectItemParamAll(Integer page, Integer rows) {
        //开始分页
        PageHelper.startPage(page,rows);
        TbItemParamExample tbItemParamExample = new TbItemParamExample();
        //按修改时间降序排序
        tbItemParamExample.setOrderByClause("updated desc");
        //查询所有
        List<TbItemParam> list = tbItemParamMapper.selectByExampleWithBLOBs(tbItemParamExample);
        PageInfo<TbItemParam> pageInfo = new PageInfo<>(list);
        PageResult pageResult = new PageResult();
        pageResult.setResult(pageInfo.getList());
        pageResult.setPageIndex(page);
        pageResult.setTotalPage(Long.valueOf(pageInfo.getPages()));
        return pageResult;
    }

    @Override
    public Integer insertItemParam(Long itemCatId, String paramData) {
        //判断是否有这个模板了
        TbItemParamExample example = new TbItemParamExample();
        TbItemParamExample.Criteria criteria = example.createCriteria();
        criteria.andItemCatIdEqualTo(itemCatId);
        List<TbItemParam> list = tbItemParamMapper.selectByExampleWithBLOBs(example);
        if (list.size()>0){
            return 0;
        }
        //保存规格模板
        Date date = new Date();
        TbItemParam tbItemParam = new TbItemParam();
        tbItemParam.setCreated(date);
        tbItemParam.setUpdated(date);
        tbItemParam.setItemCatId(itemCatId);
        tbItemParam.setParamData(paramData);
        int num = tbItemParamMapper.insertSelective(tbItemParam);
        return num;
    }

    @Override
    public Integer deleteItemParamById(Long id) {
        return tbItemParamMapper.deleteByPrimaryKey(id);
    }
}
