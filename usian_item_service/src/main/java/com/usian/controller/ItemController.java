package com.usian.controller;

import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemDesc;
import com.usian.pojo.TbItemParamItem;
import com.usian.service.ItemService;
import com.usian.utils.PageResult;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName : ItemController
 * @Author : lenovo
 * @Date: 2021/1/5 19:17
 */
@RestController
@RequestMapping("/service/item")
public class ItemController {
    @Autowired
    private ItemService itemService;
    @RequestMapping("/selectItemInfo")
    public TbItem selectItemInfo(Long itemId){
        return this.itemService.selectItemInfo(itemId);
    }
    @RequestMapping("/selectTbItemAllByPage")
    public PageResult selectTbItemAllByPage(Integer page,Integer rows){
        return itemService.selectTbItemAllByPage(page,rows);
    }
    @RequestMapping("/insertTbItem")
    public Integer insertTbItem(@RequestBody TbItem tbItem,String desc,String itemParams){
        return itemService.insertTbItem(tbItem,desc,itemParams);
    }@RequestMapping("/updateTbItem")
    public Integer updateTbItem(@RequestBody TbItem tbItem,String desc,String itemParams){
        return itemService.updateTbItem(tbItem,desc,itemParams);
    }
    @RequestMapping("/deleteItemById")
    public Integer deleteItemById(Long itemId){
        return itemService.deleteItemById(itemId);
    }
    @RequestMapping("/preUpdateItem")
    public Map<String,Object> preUpdateItem(Long itemId){
        return itemService.preUpdateItem(itemId);
    }
    @RequestMapping("/selectItemDescByItemId")
    public TbItemDesc selectItemDescByItemId(Long itemId){
        return itemService.selectItemDescByItemId(itemId);
    }
    @RequestMapping("/selectTbItemParamItemByItemId")
    public TbItemParamItem selectTbItemParamItemByItemId(Long itemId){
        return itemService.selectTbItemParamItemByItemId(itemId);
    }
}
