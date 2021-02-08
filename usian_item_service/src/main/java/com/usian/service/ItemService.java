package com.usian.service;

import com.usian.pojo.TbItem;
import com.usian.pojo.TbItemDesc;
import com.usian.pojo.TbItemParamItem;
import com.usian.utils.PageResult;
import com.usian.utils.Result;

import java.util.Map;

/**
 * @ClassName : ItemService
 * @Author : lenovo
 * @Date: 2021/1/5 19:14
 */
public interface ItemService {
    TbItem selectItemInfo(Long itemId);

    PageResult selectTbItemAllByPage(Integer page, Integer rows);

    Integer insertTbItem(TbItem tbItem, String desc, String itemParams);

    Integer deleteItemById(Long itemId);

    Map<String, Object> preUpdateItem(Long itemId);

    Integer updateTbItem(TbItem tbItem, String desc, String itemParams);

    TbItemDesc selectItemDescByItemId(Long itemId);

    TbItemParamItem selectTbItemParamItemByItemId(Long itemId);

    Integer updateTbitemByOrderId(String orderId);
}
