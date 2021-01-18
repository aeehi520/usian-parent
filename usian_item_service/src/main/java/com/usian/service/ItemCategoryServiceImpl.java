package com.usian.service;

import com.usian.mapper.TbItemCatMapper;
import com.usian.pojo.TbItemCat;
import com.usian.pojo.TbItemCatExample;
import com.usian.redis.RedisClient;
import com.usian.utils.CatNode;
import com.usian.utils.CatResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${PORTAL_CATRESULT_KEY}")
    private String PORTAL_CATRESULT_KEY;
    @Autowired
    private RedisClient redisClient;

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
        CatResult catResultRedis = (CatResult) redisClient.get(PORTAL_CATRESULT_KEY);
        if (catResultRedis!=null){
            return catResultRedis;
        }
        CatResult catResult = new CatResult();
        catResult.setData(getCatList(0L));
        //添加到缓存
        redisClient.set(PORTAL_CATRESULT_KEY,catResult);
        return catResult;
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
