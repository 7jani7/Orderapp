package com.app.order.data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "order_table")
public class Order implements Serializable {
    @Id
    private Long orderId;
    private String buyerName;
    private String buyerEmail;
    private Date orderDate;
    private Double orderTotalValue;
    private String address;
    private Integer postcode;

    public Order(String buyerName, String buyerEmail, Date orderDate, Double orderTotalValue, String address, Integer postcode) {
        this.buyerName = buyerName;
        this.buyerEmail = buyerEmail;
        this.orderDate = orderDate;
        this.orderTotalValue = orderTotalValue;
        this.address = address;
        this.postcode = postcode;
    }

    public Order() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Double getOrderTotalValue() {
        return orderTotalValue;
    }

    public void setOrderTotalValue(Double orderTotalValue) {
        this.orderTotalValue = orderTotalValue;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPostcode() {
        return postcode;
    }

    public void setPostcode(Integer postcode) {
        this.postcode = postcode;
    }
}
