package com.usian.service;

import com.usian.pojo.TbItemParam;
import com.usian.utils.PageResult;

/**
 * @ClassName : ItemParamService
 * @Author : lenovo
 * @Date: 2021/1/6 9:43
 */
public interface ItemParamService {
    TbItemParam selectItemParamByItemCatId(Long itemCatId);

    PageResult selectItemParamAll(Integer page, Integer rows);

    Integer insertItemParam(Long itemCatId, String paramData);

    Integer deleteItemParamById(Long id);
}
