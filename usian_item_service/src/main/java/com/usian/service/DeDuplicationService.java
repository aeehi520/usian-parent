package com.usian.service;

import com.usian.pojo.DeDuplication;

/**
 * @ClassName : DeDuplicationService
 * @Author : lenovo
 * @Date: 2021/2/8 14:43
 */
public interface DeDuplicationService {
    DeDuplication selectDeDuplicationByTxNo(String txNo);

    void insertDeDuplication(String txNo);
}
