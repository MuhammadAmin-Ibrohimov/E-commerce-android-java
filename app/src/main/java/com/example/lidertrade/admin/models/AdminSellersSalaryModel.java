package com.example.lidertrade.admin.models;

public class AdminSellersSalaryModel {
    String sellerId, id;
    Long cashSalary, creditSalary, givenCreditSalary, givenCashSalary;
    public AdminSellersSalaryModel(){}

    public AdminSellersSalaryModel(String sellerId, String id, Long cashSalary, Long creditSalary, Long givenCreditSalary, Long givenCashSalary) {
        this.sellerId = sellerId;
        this.id = id;
        this.cashSalary = cashSalary;
        this.creditSalary = creditSalary;
        this.givenCreditSalary = givenCreditSalary;
        this.givenCashSalary = givenCashSalary;
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

    public Long getCashSalary() {
        return cashSalary;
    }

    public void setCashSalary(Long cashSalary) {
        this.cashSalary = cashSalary;
    }

    public Long getCreditSalary() {
        return creditSalary;
    }

    public void setCreditSalary(Long creditSalary) {
        this.creditSalary = creditSalary;
    }

    public Long getGivenCreditSalary() {
        return givenCreditSalary;
    }

    public void setGivenCreditSalary(Long givenCreditSalary) {
        this.givenCreditSalary = givenCreditSalary;
    }

    public Long getGivenCashSalary() {
        return givenCashSalary;
    }

    public void setGivenCashSalary(Long givenCashSalary) {
        this.givenCashSalary = givenCashSalary;
    }
}

