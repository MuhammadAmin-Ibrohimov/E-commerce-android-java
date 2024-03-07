package com.example.lidertrade.seller.models;

public class SellerSoldProductsModel {
    private String productId, productName,  soldProductId, orderId;
    int soldProductQuantity;
    long soldProductPrice, boughtProductPrice;
    public SellerSoldProductsModel(){}

    public SellerSoldProductsModel(String productId, String productName, String soldProductId,
                                   String orderId, int soldProductQuantity, long soldProductPrice, long boughtProductPrice) {
        this.productId = productId;
        this.productName = productName;
        this.soldProductId = soldProductId;
        this.orderId = orderId;
        this.soldProductQuantity = soldProductQuantity;
        this.soldProductPrice = soldProductPrice;
        this.boughtProductPrice = boughtProductPrice;
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

    public String getSoldProductId() {
        return soldProductId;
    }

    public void setSoldProductId(String soldProductId) {
        this.soldProductId = soldProductId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getSoldProductQuantity() {
        return soldProductQuantity;
    }

    public void setSoldProductQuantity(int soldProductQuantity) {
        this.soldProductQuantity = soldProductQuantity;
    }

    public long getSoldProductPrice() {
        return soldProductPrice;
    }

    public void setSoldProductPrice(long soldProductPrice) {
        this.soldProductPrice = soldProductPrice;
    }

    public long getBoughtProductPrice() {
        return boughtProductPrice;
    }

    public void setBoughtProductPrice(long boughtProductPrice) {
        this.boughtProductPrice = boughtProductPrice;
    }
}
