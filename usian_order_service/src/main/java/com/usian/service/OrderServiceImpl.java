package com.usian.service;

import com.usian.mapper.TbItemMapper;
import com.usian.mapper.TbOrderItemMapper;
import com.usian.mapper.TbOrderMapper;
import com.usian.mapper.TbOrderShippingMapper;
import com.usian.pojo.*;
import com.usian.redis.RedisClient;
import com.usian.utils.JsonUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @ClassName : OrderServiceImpl
 * @Author : lenovo
 * @Date: 2021/2/8 10:04
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService{
    @Value("${ORDER_ID_KEY}")
    private String ORDER_ID_KEY;

    @Value("${ORDER_ID_BEGIN}")
    private Long ORDER_ID_BEGIN;

    @Value("${ORDER_ITEM_ID_KEY}")
    private String ORDER_ITEM_ID_KEY;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;
    @Autowired
    private TbOrderShippingMapper tbOrderShippingMapper;
    @Autowired
    private TbOrderMapper tbOrderMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private TbItemMapper tbItemMapper;
    @Override
    public Long insertOrder(OrderInfo orderInfo) {
        String orderItem = orderInfo.getOrderItem();
        TbOrder tbOrder = orderInfo.getTbOrder();
        TbOrderShipping tbOrderShipping = orderInfo.getTbOrderShipping();
        List<TbOrderItem> tbOrderItemList = JsonUtils.jsonToList(orderItem, TbOrderItem.class);
        //保存订单
        if (!redisClient.exists(ORDER_ID_KEY)){
            redisClient.set(ORDER_ID_KEY,ORDER_ID_BEGIN);
        }
        Long orderId = redisClient.incr(ORDER_ID_KEY, 1L);
        tbOrder.setOrderId(orderId.toString());
        tbOrder.setUpdateTime(new Date());
        tbOrder.setCreateTime(new Date());
        tbOrder.setStatus(1);
        tbOrderMapper.insertSelective(tbOrder);
        //保存订单明细
        if(!redisClient.exists(ORDER_ITEM_ID_KEY)){
            redisClient.set(ORDER_ITEM_ID_KEY,1);
        }
        for (int i = 0; i < tbOrderItemList.size(); i++) {
            Long oderItemId = redisClient.incr(ORDER_ITEM_ID_KEY, 1L);
            TbOrderItem tbOrderItem =  tbOrderItemList.get(i);
            tbOrderItem.setId(oderItemId.toString());
            tbOrderItem.setOrderId(orderId.toString());
            tbOrderItemMapper.insertSelective(tbOrderItem);
        }
        //保存订单物流
        tbOrderShipping.setCreated(new Date());
        tbOrderShipping.setUpdated(new Date());
        tbOrderShipping.setOrderId(orderId.toString());
        tbOrderShippingMapper.insertSelective(tbOrderShipping);
        amqpTemplate.convertAndSend("order_exchange","order.add",orderId.toString());
        return orderId;
    }

    @Override
    public List<TbOrder> selectOverTimeTbOrder() {
        return tbOrderMapper.selectOvertimeOrder();
    }

    @Override
    public void updateOverTimeTbOrder(TbOrder tbOrder) {
        tbOrder.setStatus(6);
        tbOrder.setCloseTime(new Date());
        tbOrder.setEndTime(new Date());
        tbOrder.setUpdateTime(new Date());
        tbOrderMapper.updateByPrimaryKeySelective(tbOrder);
    }

    @Override
    public void updateTbItemByOrderId(String orderId) {
        TbOrderItemExample tbOrderItemExample = new TbOrderItemExample();
        TbOrderItemExample.Criteria criteria = tbOrderItemExample.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<TbOrderItem> tbOrderItemList = tbOrderItemMapper.selectByExample(tbOrderItemExample);
        for (int i = 0; i < tbOrderItemList.size(); i++) {
            TbOrderItem tbOrderItem =  tbOrderItemList.get(i);
            //2、修改商品库存
            TbItem tbItem = tbItemMapper.selectByPrimaryKey(Long.valueOf(tbOrderItem.getItemId()));
            tbItem.setNum(tbItem.getNum()+tbOrderItem.getNum());
            tbItem.setUpdated(new Date());
            tbItemMapper.updateByPrimaryKey(tbItem);
        }
    }
}
