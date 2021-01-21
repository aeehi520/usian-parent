package com.usian.controller;

import com.usian.feign.ItemServiceFeign;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemDesc;
import com.usian.pojo.TbItemParam;
import com.usian.pojo.TbItemParamItem;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName : DetailController
 * @Author : lenovo
 * @Date: 2021/1/20 10:00
 */
@RestController
@RequestMapping("/frontend/detail")
public class DetailController {
    @Autowired
    private ItemServiceFeign itemServiceFeign;
    @RequestMapping("/selectItemInfo")
    public Result selectItemInfo(Long itemId){
        TbItem item = itemServiceFeign.selectItemInfo(itemId);
        if (item!=null){
            return Result.ok(item);
        }
        return Result.error("查询失败");
    }
    @RequestMapping("/selectItemDescByItemId")
    public Result selectItemDescByItemId(Long itemId){
        TbItemDesc itemDesc = itemServiceFeign.selectItemDescByItemId(itemId);
        if (itemDesc!=null){
            return Result.ok(itemDesc);
        }
        return Result.error("error");
    }
    @RequestMapping("/selectTbItemParamItemByItemId")
    public Result selectTbItemParamItemByItemId(Long itemId){
        TbItemParamItem itemParam = itemServiceFeign.selectTbItemParamItemByItemId(itemId);
        if (itemParam!=null){
            return Result.ok(itemParam);
        }
        return Result.error("error");
    }
}
