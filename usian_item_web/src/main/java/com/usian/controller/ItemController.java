package com.usian.controller;

import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName : ItemController
 * @Author : lenovo
 * @Date: 2021/1/5 19:21
 */
@RestController
@RequestMapping("/backend/item")
public class ItemController {
    @Autowired
    private ItemServiceFeign itemServiceFeign;
    //查询商品基本信息
    @RequestMapping("/selectItemInfo")
    public Result selectItemInfo(Long itemId){
        TbItem tbItem = itemServiceFeign.selectItemInfo(itemId);
        if (tbItem!=null){
            return Result.ok(tbItem);
        }
        return Result.error("查无结果");
    }
    @RequestMapping("/selectTbItemAllByPage")
    public Result selectTbItemAllByPage(@RequestParam(defaultValue = "1")Integer page,
                                        @RequestParam(defaultValue = "10")Integer rows){
        PageResult pageResult = itemServiceFeign.selectTbItemAllByPage(page,rows);
        if (pageResult.getResult()!=null && pageResult.getResult().size()>0){
            return Result.ok(pageResult);
        }
        return Result.error("查无结果");
    }
    @RequestMapping("/insertTbItem")
    public Result insertTbItem(TbItem tbItem,String desc,String itemParams){
        Integer insertTbItemNumber = itemServiceFeign.insertTbItem(tbItem,desc,itemParams);
        if (insertTbItemNumber==3){
            return Result.ok();
        }
        return Result.error("添加失败");
    }
    @RequestMapping("/updateTbItem")
    public Result updateTbItem(TbItem tbItem,String desc,String itemParams){
        Integer insertTbItemNumber = itemServiceFeign.updateTbItem(tbItem,desc,itemParams);
        if (insertTbItemNumber==3){
            return Result.ok();
        }
        return Result.error("修改失败");
    }
    @RequestMapping("/deleteItemById")
    public Result deleteItemById(Long itemId){
        Integer count = itemServiceFeign.deleteItemById(itemId);
        if (count==3){
            return Result.ok();
        }
        return Result.error("删除失败");
    }
    @RequestMapping("/preUpdateItem")
    public Result preUpdateItem(Long itemId){
        Map<String,Object> map = itemServiceFeign.preUpdateItem(itemId);
        if (map.size()>0){
            return Result.ok(map);
        }
        return Result.error("查无结果");
    }
}
