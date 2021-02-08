package com.usian.quartz;

import com.usian.pojo.TbOrder;
import com.usian.redis.RedisClient;
import com.usian.service.OrderService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCache;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @ClassName : OrderQuartz
 * @Author : lenovo
 * @Date: 2021/2/8 13:34
 */
public class OrderQuartz implements Job {
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisClient redisClient;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //解决quartz集群任务重复执行
        if(redisClient.setnx("SETNX_LOCK_ORDER_KEY",ip,30)) {
            //... ... ... 关闭超时订单业务
            redisClient.del("SETNX_LOCK_ORDER_KEY");
        }else{
            System.out.println(
                    "===========任务正在执行=======================");
        }
        List<TbOrder> tbOrderList = orderService.selectOverTimeTbOrder();
        for (int i = 0; i < tbOrderList.size(); i++) {
            TbOrder tbOrder = tbOrderList.get(i);
            orderService.updateOverTimeTbOrder(tbOrder);
            orderService.updateTbItemByOrderId(tbOrder.getOrderId());
        }
    }
}
