package com.example.lidertrade.admin.models;

public class AdminTotalStatsModel {
    long boughtPrice,cashPrice, creditPrice, cashSoldQuantity, creditSoldQuantity;
    String id, productName;
    public AdminTotalStatsModel(){}
    public AdminTotalStatsModel(long boughtPrice, long cashPrice, long creditPrice, long cashSoldQuantity, long creditSoldQuantity, String id, String productName) {
        this.boughtPrice = boughtPrice;
        this.cashPrice = cashPrice;
        this.creditPrice = creditPrice;
        this.cashSoldQuantity = cashSoldQuantity;
        this.creditSoldQuantity = creditSoldQuantity;
        this.id = id;
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public long getBoughtPrice() {
        return boughtPrice;
    }

    public void setBoughtPrice(long boughtPrice) {
        this.boughtPrice = boughtPrice;
    }

    public long getCashPrice() {
        return cashPrice;
    }

    public void setCashPrice(long cashPrice) {
        this.cashPrice = cashPrice;
    }

    public long getCreditPrice() {
        return creditPrice;
    }

    public void setCreditPrice(long creditPrice) {
        this.creditPrice = creditPrice;
    }

    public long getCashSoldQuantity() {
        return cashSoldQuantity;
    }

    public void setCashSoldQuantity(long cashSoldQuantity) {
        this.cashSoldQuantity = cashSoldQuantity;
    }

    public long getCreditSoldQuantity() {
        return creditSoldQuantity;
    }

    public void setCreditSoldQuantity(long creditSoldQuantity) {
        this.creditSoldQuantity = creditSoldQuantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
