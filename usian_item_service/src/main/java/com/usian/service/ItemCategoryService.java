package com.usian.service;

import com.usian.pojo.TbItemCat;
import com.usian.utils.CatResult;

import java.util.List;

/**
 * @ClassName : ItemCategoryService
 * @Author : lenovo
 * @Date: 2021/1/5 21:43
 */
public interface ItemCategoryService {
    List<TbItemCat> selectItemCategoryByParentId(Long id);

    CatResult selectItemCategoryAll();
}
