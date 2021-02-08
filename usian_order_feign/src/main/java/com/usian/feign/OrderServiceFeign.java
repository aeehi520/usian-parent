package com.usian.feign;

import com.usian.pojo.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName : OrderServiceFeign
 * @Author : lenovo
 * @Date: 2021/2/8 10:35
 */
@FeignClient("usian-order-service")
public interface OrderServiceFeign {
    @RequestMapping("/service/order/insertOrder")
    Long insertOrder(@RequestBody OrderInfo orderInfo);
}
