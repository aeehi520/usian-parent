package com.usian.listener;

import com.usian.service.ItemService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName : ItemMQListener
 * @Author : lenovo
 * @Date: 2021/2/8 12:40
 */
@Component
public class ItemMQListener {
    @Autowired
    private ItemService itemService;
    @RabbitListener(bindings=@QueueBinding(
            value = @Queue(value = "item_queue",durable = "true"),
            exchange = @Exchange(value = "order_exchange",type = ExchangeTypes.TOPIC),
            key = {"order.*"}
    ))
    public void listen(String orderId){
        Integer result = itemService.updateTbitemByOrderId(orderId);
        if (!(result>0)){
            throw new RuntimeException("扣减失败");
        }
    }
}
