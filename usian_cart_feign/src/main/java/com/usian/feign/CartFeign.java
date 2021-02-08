package com.usian.feign;

import com.usian.pojo.OrderInfo;
import com.usian.pojo.TbItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @ClassName : CartFeign
 * @Author : lenovo
 * @Date: 2021/1/28 9:36
 */
@FeignClient("usian-cart-service")
public interface CartFeign {
    @RequestMapping("/service/cart/addCartToRedis")
    void addCartToRedis(@RequestParam String userId,@RequestBody Map<String, TbItem> cart);
    @RequestMapping("/service/cart/getCartFromRedis")
    Map<String, TbItem> getCartFromRedis(@RequestParam String userId);

}
