package com.usian.service;

import com.usian.pojo.TbItem;
import com.usian.redis.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName : CartServiceImpl
 * @Author : lenovo
 * @Date: 2021/1/30 9:01
 */
@Service
@Transactional
public class CartServiceImpl implements CartService{
    @Value("${CART_REDIS_KEY}")
    private String CART_REDIS_KEY;
    @Autowired
    private RedisClient redisClient;
    @Override
    public void addCartToRedis(String userId, Map<String, TbItem> cart) {
        redisClient.hset(CART_REDIS_KEY,userId,cart);
    }

    @Override
    public Map<String, TbItem> getCartFromRedis(String userId) {
        Map<String,TbItem> cart= (Map<String, TbItem>) redisClient.hget(CART_REDIS_KEY, userId);
        if (cart!=null&&cart.size()>0){
            return cart;
        }
        return new HashMap<String, TbItem>();
    }
}
