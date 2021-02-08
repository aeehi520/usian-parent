package com.usian.service;

import com.usian.pojo.OrderInfo;
import com.usian.pojo.TbOrder;

import java.util.List;

/**
 * @ClassName : OrderService
 * @Author : lenovo
 * @Date: 2021/2/8 10:04
 */
public interface OrderService {
    Long insertOrder(OrderInfo orderInfo);

    List<TbOrder> selectOverTimeTbOrder();

    void updateOverTimeTbOrder(TbOrder tbOrder);

    void updateTbItemByOrderId(String orderId);
}
