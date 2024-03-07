package com.example.lidertrade.seller.models;

public class SellerCustomStatModel {
    String sellerId,id;
    long totalCashSalary, totalCreditSalary;
    int totalCashQuantity, totalCreditQuantity;
    public SellerCustomStatModel(){}
    public SellerCustomStatModel(String sellerId, String id, long totalCashSalary,
                                 long totalCreditSalary, int totalCashQuantity, int totalCreditQuantity) {
        this.sellerId = sellerId;
        this.id = id;
        this.totalCashSalary = totalCashSalary;
        this.totalCreditSalary = totalCreditSalary;
        this.totalCashQuantity = totalCashQuantity;
        this.totalCreditQuantity = totalCreditQuantity;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTotalCashSalary() {
        return totalCashSalary;
    }

    public void setTotalCashSalary(long totalCashSalary) {
        this.totalCashSalary = totalCashSalary;
    }

    public long getTotalCreditSalary() {
        return totalCreditSalary;
    }

    public void setTotalCreditSalary(long totalCreditSalary) {
        this.totalCreditSalary = totalCreditSalary;
    }

    public int getTotalCashQuantity() {
        return totalCashQuantity;
    }

    public void setTotalCashQuantity(int totalCashQuantity) {
        this.totalCashQuantity = totalCashQuantity;
    }

    public int getTotalCreditQuantity() {
        return totalCreditQuantity;
    }

    public void setTotalCreditQuantity(int totalCreditQuantity) {
        this.totalCreditQuantity = totalCreditQuantity;
    }
}
