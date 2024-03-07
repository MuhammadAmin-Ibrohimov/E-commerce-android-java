package com.example.lidertrade.admin.models;

public class AdminSellerStatsModel {
    String id;
    Long totalCashBoughtPrice, totalCreditBoughtPrice, totalCashPrice, totalCreditPrice;
    int totalCashQuantity, totalCreditQuantity, creditSalaryPercent, cashSalaryPercent;
    public AdminSellerStatsModel(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTotalCashBoughtPrice() {
        return totalCashBoughtPrice;
    }

    public void setTotalCashBoughtPrice(Long totalCashBoughtPrice) {
        this.totalCashBoughtPrice = totalCashBoughtPrice;
    }

    public Long getTotalCreditBoughtPrice() {
        return totalCreditBoughtPrice;
    }

    public void setTotalCreditBoughtPrice(Long totalCreditBoughtPrice) {
        this.totalCreditBoughtPrice = totalCreditBoughtPrice;
    }

    public Long getTotalCashPrice() {
        return totalCashPrice;
    }

    public void setTotalCashPrice(Long totalCashPrice) {
        this.totalCashPrice = totalCashPrice;
    }

    public Long getTotalCreditPrice() {
        return totalCreditPrice;
    }

    public void setTotalCreditPrice(Long totalCreditPrice) {
        this.totalCreditPrice = totalCreditPrice;
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

    public int getCreditSalaryPercent() {
        return creditSalaryPercent;
    }

    public void setCreditSalaryPercent(int creditSalaryPercent) {
        this.creditSalaryPercent = creditSalaryPercent;
    }

    public int getCashSalaryPercent() {
        return cashSalaryPercent;
    }

    public void setCashSalaryPercent(int cashSalaryPercent) {
        this.cashSalaryPercent = cashSalaryPercent;
    }

    public AdminSellerStatsModel(String id, Long totalCashBoughtPrice, Long totalCreditBoughtPrice, Long totalCashPrice,
                                 Long totalCreditPrice, int totalCashQuantity, int totalCreditQuantity, int creditSalaryPercent, int cashSalaryPercent) {
        this.id = id;
        this.totalCashBoughtPrice = totalCashBoughtPrice;
        this.totalCreditBoughtPrice = totalCreditBoughtPrice;
        this.totalCashPrice = totalCashPrice;
        this.totalCreditPrice = totalCreditPrice;
        this.totalCashQuantity = totalCashQuantity;
        this.totalCreditQuantity = totalCreditQuantity;
        this.creditSalaryPercent = creditSalaryPercent;
        this.cashSalaryPercent = cashSalaryPercent;
    }
}
