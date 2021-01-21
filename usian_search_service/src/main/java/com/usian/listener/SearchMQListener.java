package com.usian.listener;

import com.rabbitmq.client.Channel;
import com.usian.service.SearchItemService;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @ClassName : SearchMQListener
 * @Author : lenovo
 * @Date: 2021/1/19 20:26
 */
@Component
public class SearchMQListener {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private SearchItemService searchItemService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "search_queue",durable = "true"),
            exchange = @Exchange(value = "item_exchange",type= ExchangeTypes.TOPIC),
            key = {"item.add"}
    ))
    public void add(String msg, Channel channel, Message message) {
        int result = searchItemService.insertDocument(msg);
        if (result>0){
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "search_queue",durable = "true"),
            exchange = @Exchange(value = "item_exchange",type= ExchangeTypes.TOPIC),
            key = {"item.delete"}
    ))
    public void delete(String msg, Channel channel, Message message) {
        int result = searchItemService.deleteDocument(msg);
        if (result>0){
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "search_queue",durable = "true"),
            exchange = @Exchange(value = "item_exchange",type= ExchangeTypes.TOPIC),
            key = {"item.update"}
    ))
    public void update(String msg, Channel channel, Message message) {
        int result = searchItemService.insertDocument(msg);
        if (result>0){
            try {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
