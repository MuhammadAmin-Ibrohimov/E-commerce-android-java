package com.example.lidertrade.seller.models;

public class SellerProfileFragmentDailyStatModel {
    private String customerName, customerPhone;
    private int packageStatus;
    public SellerProfileFragmentDailyStatModel(){

    }
    public SellerProfileFragmentDailyStatModel(String customerName, String customerPhone, int packageStatus) {
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.packageStatus = packageStatus;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public int getPackageStatus() {
        return packageStatus;
    }

    public void setPackageStatus(int packageStatus) {
        this.packageStatus = packageStatus;
    }
}
