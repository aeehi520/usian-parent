package com.usian.feign;
import com.usian.fallback.ItemServiceFallback;
import com.usian.pojo.*;
import com.usian.utils.CatResult;
import com.usian.utils.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @ClassName : ItemServiceFeign
 * @Author : lenovo
 * @Date: 2021/1/5 19:19
 */
@FeignClient(value = "usian-item-service",fallbackFactory = ItemServiceFallback.class)
public interface ItemServiceFeign {
    @RequestMapping("/service/item/selectItemInfo")
    TbItem selectItemInfo(@RequestParam("itemId") Long itemId);
    @RequestMapping("/service/item/selectTbItemAllByPage")
    PageResult selectTbItemAllByPage(@RequestParam Integer page,@RequestParam Integer rows);
    @RequestMapping("/service/itemCategory/selectItemCategoryByParentId")
    List<TbItemCat> selectItemCategoryByParentId(@RequestParam Long id);
    @RequestMapping("/service/itemParam/selectItemParamByItemCatId")
    TbItemParam selectItemParamByItemCatId(@RequestParam Long itemCatId);
    @RequestMapping("/service/item/insertTbItem")
    Integer insertTbItem(@RequestBody TbItem tbItem,@RequestParam String desc,@RequestParam String itemParams);
    @RequestMapping("/service/item/deleteItemById")
    Integer deleteItemById(@RequestParam("itemId") Long itemId);
    @RequestMapping("service/item/preUpdateItem")
    Map<String, Object> preUpdateItem(@RequestParam("itemId") Long itemId);
    @RequestMapping("/service/item/updateTbItem")
    Integer updateTbItem(@RequestBody TbItem tbItem,@RequestParam String desc,@RequestParam String itemParams);
    @RequestMapping("/service/itemParam/selectItemParamAll")
    PageResult selectItemParamAll(@RequestParam Integer page,@RequestParam Integer rows);
    @RequestMapping("/service/itemParam/insertItemParam")
    Integer insertItemParam(@RequestParam Long itemCatId,@RequestParam String paramData);
    @RequestMapping("/service/itemParam/deleteItemParamById")
    Integer deleteItemParamById(@RequestParam Long id);
    @RequestMapping("/service/itemCategory/selectItemCategoryAll")
    CatResult selectItemCategoryAll();
    @RequestMapping("/service/item/selectItemDescByItemId")
    TbItemDesc selectItemDescByItemId(@RequestParam Long itemId);
    @RequestMapping("/service/item/selectTbItemParamItemByItemId")
    TbItemParamItem selectTbItemParamItemByItemId(@RequestParam Long itemId);
}
