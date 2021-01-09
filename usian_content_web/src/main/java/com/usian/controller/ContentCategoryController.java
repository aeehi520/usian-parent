package com.usian.controller;
import com.usian.ContentServiceFeign;
import com.usian.pojo.TbContent;
import com.usian.pojo.TbContentCategory;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.parser.ContentModel;
import java.sql.ResultSet;
import java.util.List;

/**
 * @ClassName : ContentController
 * @Author : lenovo
 * @Date: 2021/1/7 18:34
 */
@RestController
@RequestMapping("/backend/content")
public class ContentCategoryController {
    @Autowired
    private ContentServiceFeign contentServiceFeign;
    @RequestMapping("/selectContentCategoryByParentId")
    public Result selectContentCategoryByParentId(@RequestParam(defaultValue = "0") Long id){
        List<TbContentCategory> list = contentServiceFeign.selectContentCategoryByParentId(id);
        if (list!=null&&list.size()>0){
            return Result.ok(list);
        }
        return Result.error("查无结果");
    }
    @RequestMapping("/insertContentCategory")
    public Result insertContentCategory(TbContentCategory tbContentCategory){
        Integer num = contentServiceFeign.insertContentCategory(tbContentCategory);
        if (num==1){
            return Result.ok();
        }
        return Result.error("添加失败");
    }
    @RequestMapping("/deleteContentCategoryById")
    public Result deleteContentCategoryById(Long categoryId){
        Integer num = contentServiceFeign.deleteContentCategoryById(categoryId);
        if (num==1){
            return Result.ok();
        }
        return Result.error("删除失败");
    }
    @RequestMapping("/updateContentCategory")
    public Result updateContentCategory(TbContentCategory tbContentCategory){
        Integer num = contentServiceFeign.updateContentCategory(tbContentCategory);
        if (num==1){
            return Result.ok();
        }
        return Result.error("修改失败");
    }
    @RequestMapping("/selectTbContentAllByCategoryId")
    public Result selectTbContentAllByCategoryId(
            @RequestParam(defaultValue = "1")Integer page,
            @RequestParam(defaultValue = "10")Integer rows,
            Long categoryId){
        PageResult pageResult = contentServiceFeign.selectTbContentAllByCategoryId(page,rows,categoryId);
        if (pageResult!=null&&pageResult.getResult().size()>0){
            return Result.ok(pageResult);
        }
        return Result.error("查无结果");
    }
    @RequestMapping("/insertTbContent")
    public Result insertTbContent(TbContent tbContent){
        Integer num = contentServiceFeign.insertTbContent(tbContent);
        if (num==1){
            return Result.ok();
        }
        return Result.error("添加失败");
    }
    @RequestMapping("/deleteContentByIds")
    public Result deleteContentByIds(Long ids){
        Integer num = contentServiceFeign.deleteContentByIds(ids);
        if (num==1){
            return Result.ok();
        }
        return Result.error("删除失败");
    }
}
