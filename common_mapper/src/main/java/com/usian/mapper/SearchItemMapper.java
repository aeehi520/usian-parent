package com.usian.mapper;

import com.usian.pojo.SearchItem;

import java.util.List;

/**
 * @ClassName : SearchItemMapper
 * @Author : lenovo
 * @Date: 2021/1/18 18:43
 */
public interface SearchItemMapper {
    List<SearchItem> getItemList();
    SearchItem getItemById(Long itemId);
}
