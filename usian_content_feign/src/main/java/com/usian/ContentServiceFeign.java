package com.usian;

import com.usian.pojo.TbContent;
import com.usian.pojo.TbContentCategory;
import com.usian.utils.AdNode;
import com.usian.utils.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @ClassName : ContentServiceFeign
 * @Author : lenovo
 * @Date: 2021/1/7 18:35
 */
@FeignClient("usian-content-service")
public interface ContentServiceFeign {
    @RequestMapping("/service/content/selectFrontendContentByAD")
    List<AdNode> selectFrontendContentByAD();

    @RequestMapping("/service/contentCategory/selectContentCategoryByParentId")
    List<TbContentCategory> selectContentCategoryByParentId(@RequestParam("id") Long id);
    @RequestMapping("/service/contentCategory/insertContentCategory")
    Integer insertContentCategory(TbContentCategory tbContentCategory);
    @RequestMapping("/service/contentCategory/deleteContentCategoryById")
    Integer deleteContentCategoryById(@RequestParam("categoryId") Long categoryId);
    @RequestMapping("/service/contentCategory/updateContentCategory")
    Integer updateContentCategory(TbContentCategory tbContentCategory);
    @RequestMapping("/service/contentCategory/selectTbContentAllByCategoryId")
    PageResult selectTbContentAllByCategoryId(@RequestParam("page") Integer page,@RequestParam("rows") Integer rows, @RequestParam("categoryId") Long categoryId);
    @RequestMapping("/service/contentCategory/insertTbContent")
    Integer insertTbContent(TbContent tbContent);
    @RequestMapping("/service/contentCategory/deleteContentByIds")
    Integer deleteContentByIds(@RequestParam("ids") Long ids);
}
