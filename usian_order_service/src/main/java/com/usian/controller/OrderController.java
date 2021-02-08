package com.usian.controller;

import com.usian.pojo.OrderInfo;
import com.usian.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName : OrderCOntroller
 * @Author : lenovo
 * @Date: 2021/2/8 10:03
 */
@RestController
@RequestMapping("/service/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @RequestMapping("/insertOrder")
    public Long insertOrder(@RequestBody OrderInfo orderInfo){
        return orderService.insertOrder(orderInfo);
    }
}
