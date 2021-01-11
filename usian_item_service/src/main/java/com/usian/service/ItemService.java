package com.usian.service;

import com.usian.pojo.TbItem;
import com.usian.utils.PageResult;

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
}