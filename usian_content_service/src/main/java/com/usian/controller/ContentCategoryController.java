package com.usian.controller;

import com.usian.pojo.TbContent;
import com.usian.pojo.TbContentCategory;
import com.usian.service.ContentCategoryService;
import com.usian.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName : ContentCategoryController
 * @Author : lenovo
 * @Date: 2021/1/7 18:54
 */
@RestController
@RequestMapping("/service/contentCategory")
public class ContentCategoryController {
    @Autowired

    private ContentCategoryService contentCategoryService;

    @RequestMapping("/selectContentCategoryByParentId")
    public List<TbContentCategory> selectContentCategoryByParentId(Long id){
        return contentCategoryService.selectContentCategoryByParentId(id);
    }
    @RequestMapping("/insertContentCategory")
    public Integer insertContentCategory(@RequestBody TbContentCategory tbContentCategory){
        return contentCategoryService.insertContentCategory(tbContentCategory);
    }
    @RequestMapping("/deleteContentCategoryById")
    public Integer deleteContentCategoryById(Long categoryId){
        return contentCategoryService.deleteContentCategoryById(categoryId);
    }
    @RequestMapping("/updateContentCategory")
    public Integer updateContentCategory(@RequestBody TbContentCategory tbContentCategory){
        return contentCategoryService.updateContentCategory(tbContentCategory);
    }
    @RequestMapping("/selectTbContentAllByCategoryId")
    public PageResult selectTbContentAllByCategoryId(Integer page,Integer rows,Long categoryId){
        return contentCategoryService.selectTbContentAllByCategoryId(page,rows,categoryId);
    }
    @RequestMapping("/insertTbContent")
    public Integer insertTbContent(@RequestBody TbContent tbContent){
        return contentCategoryService.insertTbContent(tbContent);
    }
    @RequestMapping("/deleteContentByIds")
    public Integer deleteContentByIds(Long ids){
        return contentCategoryService.deleteContentByIds(ids);
    }
}
