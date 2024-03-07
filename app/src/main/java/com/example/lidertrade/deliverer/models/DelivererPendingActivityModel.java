package com.example.lidertrade.deliverer.models;

public class DelivererPendingActivityModel {
    private String productId, productName, orderId, soldProductId ;
    private int soldProductQuantity, soldProductPrice;

    public DelivererPendingActivityModel(){}

    public DelivererPendingActivityModel(String productId, String productName, String orderId,
                                         String soldProductId, int soldProductQuantity, int soldProductPrice) {
        this.productId = productId;
        this.productName = productName;
        this.orderId = orderId;
        this.soldProductId = soldProductId;
        this.soldProductQuantity = soldProductQuantity;
        this.soldProductPrice = soldProductPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSoldProductId() {
        return soldProductId;
    }

    public void setSoldProductId(String soldProductId) {
        this.soldProductId = soldProductId;
    }

    public int getSoldProductQuantity() {
        return soldProductQuantity;
    }

    public void setSoldProductQuantity(int soldProductQuantity) {
        this.soldProductQuantity = soldProductQuantity;
    }

    public int getSoldProductPrice() {
        return soldProductPrice;
    }

    public void setSoldProductPrice(int soldProductPrice) {
        this.soldProductPrice = soldProductPrice;
    }
}
