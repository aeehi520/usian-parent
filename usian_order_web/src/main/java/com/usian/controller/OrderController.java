package com.usian.controller;

import com.netflix.discovery.converters.Auto;
import com.usian.feign.CartFeign;
import com.usian.feign.OrderServiceFeign;
import com.usian.pojo.OrderInfo;
import com.usian.pojo.TbItem;
import com.usian.pojo.TbOrder;
import com.usian.pojo.TbOrderShipping;
import com.usian.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName : OrderController
 * @Author : lenovo
 * @Date: 2021/2/8 9:37
 */
@RestController
@RequestMapping("/frontend/order")
public class OrderController {
    @Autowired
    private CartFeign cartFeign;
    @Autowired
    private OrderServiceFeign orderServiceFeign;
    @RequestMapping("/goSettlement")
    public Result goSettlement(String[] ids,String userId){
        Map<String, TbItem> cart = cartFeign.getCartFromRedis(userId);
        List<TbItem> list = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            list.add(cart.get(ids[i]));
        }
        if (list.size()>0){
            return Result.ok(list);
        }
        return Result.error("error");
    }
    @RequestMapping("/insertOrder")
    public Result insertOrder(String orderItem, TbOrderShipping tbOrderShipping, TbOrder tbOrder){
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderItem(orderItem);
        orderInfo.setTbOrder(tbOrder);
        orderInfo.setTbOrderShipping(tbOrderShipping);
        Long orderId = orderServiceFeign.insertOrder(orderInfo);
        if (orderId!=null){
            return Result.ok(orderId);
        }
        return Result.error("error");
    }
}
