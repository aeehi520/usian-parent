package com.usian.service;

import com.usian.pojo.SearchItem;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName : SearchItemService
 * @Author : lenovo
 * @Date: 2021/1/18 18:57
 */
public interface SearchItemService {
    boolean importAll();

    List<SearchItem> selectByQ(String q, Long page, Integer pageSize);

    int insertDocument(String itemId);

    int deleteDocument(String itemId);

    int updateDocument(String itemId);
}
