package com.usian.service;

import com.usian.pojo.TbItem;

import java.util.Map;

/**
 * @ClassName : CartService
 * @Author : lenovo
 * @Date: 2021/1/30 9:01
 */
public interface CartService {
    void addCartToRedis(String userId, Map<String, TbItem> cart);

    Map<String, TbItem> getCartFromRedis(String userId);
}
