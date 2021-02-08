package com.usian.controller;

import com.usian.pojo.TbItem;
import com.usian.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @ClassName : CartController
 * @Author : lenovo
 * @Date: 2021/1/30 9:01
 */
@RestController
@RequestMapping("/service/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @RequestMapping("/addCartToRedis")
    public void addCartToRedis(String userId,@RequestBody Map<String, TbItem> cart){
        cartService.addCartToRedis(userId,cart);
    }
    @RequestMapping("/getCartFromRedis")
    public Map<String,TbItem> getCartFromRedis(String userId){
        return cartService.getCartFromRedis(userId);
    }
}
