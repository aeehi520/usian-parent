package com.usian.pojo;

/**
 * @ClassName : OrderInfo
 * @Author : lenovo
 * @Date: 2021/2/8 9:59
 */
public class OrderInfo {
    private TbOrder tbOrder;
    private TbOrderShipping tbOrderShipping;
    private String orderItem;

    public OrderInfo(TbOrder tbOrder, TbOrderShipping tbOrderShipping, String orderItem) {
        this.tbOrder = tbOrder;
        this.tbOrderShipping = tbOrderShipping;
        this.orderItem = orderItem;
    }

    public OrderInfo() {
    }

    public TbOrder getTbOrder() {
        return tbOrder;
    }

    public void setTbOrder(TbOrder tbOrder) {
        this.tbOrder = tbOrder;
    }

    public TbOrderShipping getTbOrderShipping() {
        return tbOrderShipping;
    }

    public void setTbOrderShipping(TbOrderShipping tbOrderShipping) {
        this.tbOrderShipping = tbOrderShipping;
    }

    public String getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(String orderItem) {
        this.orderItem = orderItem;
    }
}
