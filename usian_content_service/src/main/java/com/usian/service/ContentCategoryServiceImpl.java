package com.usian.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.usian.mapper.TbContentCategoryMapper;
import com.usian.mapper.TbContentMapper;
import com.usian.pojo.TbContent;
import com.usian.pojo.TbContentCategory;
import com.usian.pojo.TbContentCategoryExample;
import com.usian.pojo.TbContentExample;
import com.usian.redis.RedisClient;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @ClassName : ContentCategoryServiceImpl
 * @Author : lenovo
 * @Date: 2021/1/7 18:56
 */
@Service
@Transactional
public class ContentCategoryServiceImpl implements ContentCategoryService{
    @Autowired
    private TbContentCategoryMapper tbContentCategoryMapper;
    @Autowired
    private TbContentMapper tbContentMapper;
    @Autowired
    private RedisClient redisClient;
    @Value("${PORTAL_AD_KEY}")
    private String PORTAL_AD_KEY;
    @Value("${AD_CATEGORY_ID}")
    private Long AD_CATEGORY_ID;
    @Override
    public List<TbContentCategory> selectContentCategoryByParentId(Long id) {
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(id);
        List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
        return list;
    }

    @Override
    public Integer insertContentCategory(TbContentCategory tbContentCategory) {
        //添加内容分类
        tbContentCategory.setUpdated(new Date());
        tbContentCategory.setCreated(new Date());
        tbContentCategory.setIsParent(false);
        tbContentCategory.setSortOrder(1);
        tbContentCategory.setStatus(1);
        int num = tbContentCategoryMapper.insert(tbContentCategory);
        TbContentCategory contentCategory = tbContentCategoryMapper.selectByPrimaryKey(tbContentCategory.getParentId());
        if (!contentCategory.getIsParent()){
            contentCategory.setIsParent(true);
            contentCategory.setUpdated(new Date());
            tbContentCategoryMapper.updateByPrimaryKey(contentCategory);
        }
        return num;
    }

    @Override
    public Integer deleteContentCategoryById(Long categoryId) {
        TbContentCategory tbContentCategory = tbContentCategoryMapper.selectByPrimaryKey(categoryId);
        if (tbContentCategory.getIsParent()==true){
            return 0;
        }
        tbContentCategoryMapper.deleteByPrimaryKey(categoryId);
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(tbContentCategory.getParentId());
        List<TbContentCategory> list = tbContentCategoryMapper.selectByExample(example);
        if (list.size()==0){
            TbContentCategory category = new TbContentCategory();
            category.setUpdated(new Date());
            category.setId(tbContentCategory.getParentId());
            category.setIsParent(false);
            tbContentCategoryMapper.updateByPrimaryKeySelective(category);
        }
        return 1;
    }

    @Override
    public Integer updateContentCategory(TbContentCategory tbContentCategory) {
        tbContentCategory.setUpdated(new Date());
        return tbContentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);
    }

    @Override
    public PageResult selectTbContentAllByCategoryId(Integer page, Integer rows, Long categoryId) {
        PageHelper.startPage(page,rows);
        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        List<TbContent> tbContentList = tbContentMapper.selectByExample(example);
        PageInfo<TbContent> pageInfo = new PageInfo<>(tbContentList);
        PageResult pageResult = new PageResult();
        pageResult.setResult(pageInfo.getList());
        pageResult.setTotalPage(Long.valueOf(pageInfo.getTotal()));
        pageResult.setPageIndex(pageInfo.getPageNum());
        return pageResult;
    }

    @Override
    public Integer insertTbContent(TbContent tbContent) {
        tbContent.setCreated(new Date());
        tbContent.setUpdated(new Date());
        //缓存同步
        redisClient.hdel(PORTAL_AD_KEY,AD_CATEGORY_ID.toString());
        return tbContentMapper.insertSelective(tbContent);
    }

    @Override
    public Integer deleteContentByIds(Long ids) {
        //缓存同步
        redisClient.hdel(PORTAL_AD_KEY,AD_CATEGORY_ID.toString());
        return tbContentMapper.deleteByPrimaryKey(ids);
    }
}
