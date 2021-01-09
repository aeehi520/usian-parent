package com.usian.service;

import com.usian.mapper.TbItemCatMapper;
import com.usian.pojo.TbItemCat;
import com.usian.pojo.TbItemCatExample;
import com.usian.utils.CatNode;
import com.usian.utils.CatResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName : ItemCategoryServiceImpl
 * @Author : lenovo
 * @Date: 2021/1/5 21:43
 */
@Service
@Transactional
public class ItemCategoryServiceImpl implements ItemCategoryService{
    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Override
    public List<TbItemCat> selectItemCategoryByParentId(Long id) {
        TbItemCatExample tbItemCatExample = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = tbItemCatExample.createCriteria();
        criteria.andParentIdEqualTo(id);
        criteria.andStatusEqualTo(1);
        List<TbItemCat> list = tbItemCatMapper.selectByExample(tbItemCatExample);
        return list;
    }

    @Override
    public CatResult selectItemCategoryAll() {
        CatResult catResult = new CatResult();
        catResult.setData(getCatList(0L));
        return null;
    }

    private List<?> getCatList(Long parentId) {
        TbItemCatExample example = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        List<TbItemCat> list = tbItemCatMapper.selectByExample(example);
        List resultList = new ArrayList();
        int count=0;
        for (TbItemCat tbItemCat : list) {
            if (tbItemCat.getIsParent()){
                CatNode catNode = new CatNode();
                catNode.setName(tbItemCat.getName());
                catNode.setItem(getCatList(tbItemCat.getId()));
                resultList.add(catNode);
                count++;
                if (count==18){
                    break;
                }
            }else{
                resultList.add(tbItemCat.getName());
            }
        }
        return resultList;
    }
}
