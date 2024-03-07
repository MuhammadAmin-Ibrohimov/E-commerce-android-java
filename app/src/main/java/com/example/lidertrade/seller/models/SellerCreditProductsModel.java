package com.example.lidertrade.seller.models;

public class SellerCreditProductsModel {
    private String productId, productName,  creditProductId, orderId;
    int creditProductQuantity;
    long creditProductPrice, creditProductBoughtPrice;

    public SellerCreditProductsModel(){}

    public SellerCreditProductsModel(String productId, String productName, String creditProductId, String orderId,
                                     int creditProductQuantity, long creditProductPrice, long creditProductBoughtPrice) {
        this.productId = productId;
        this.productName = productName;
        this.creditProductId = creditProductId;
        this.orderId = orderId;
        this.creditProductQuantity = creditProductQuantity;
        this.creditProductPrice = creditProductPrice;
        this.creditProductBoughtPrice = creditProductBoughtPrice;
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

    public String getCreditProductId() {
        return creditProductId;
    }

    public void setCreditProductId(String creditProductId) {
        this.creditProductId = creditProductId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getCreditProductQuantity() {
        return creditProductQuantity;
    }

    public void setCreditProductQuantity(int creditProductQuantity) {
        this.creditProductQuantity = creditProductQuantity;
    }

    public long getCreditProductPrice() {
        return creditProductPrice;
    }

    public void setCreditProductPrice(long creditProductPrice) {
        this.creditProductPrice = creditProductPrice;
    }

    public long getCreditProductBoughtPrice() {
        return creditProductBoughtPrice;
    }

    public void setCreditProductBoughtPrice(long creditProductBoughtPrice) {
        this.creditProductBoughtPrice = creditProductBoughtPrice;
    }
}
