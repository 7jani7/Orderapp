package com.app.order.data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
public class OrderItem implements Serializable {

    @Id
    private Long orderItemId;
    private Long orderId;
    private Double salePrice;
    private Double shippingPrice;
    private Double totalItemPrice;
    private String sku;
    private Stat status;

    public OrderItem(Long orderId, Double salePrice, Double shippingPrice, Double totalItemPrice, String sku, Stat status) {
        this.orderId = orderId;
        this.salePrice = salePrice;
        this.shippingPrice = shippingPrice;
        this.totalItemPrice = totalItemPrice;
        this.sku = sku;
        this.status = status;
    }

    public OrderItem() {
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public Double getShippingPrice() {
        return shippingPrice;
    }

    public void setShippingPrice(Double shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

    public Double getTotalItemPrice() {
        return totalItemPrice;
    }

    public void setTotalItemPrice(Double totalItemPrice) {
        this.totalItemPrice = totalItemPrice;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Stat getStatus() {
        return status;
    }

    public void setStatus(Stat status) {
        this.status = status;
    }
}
