package com.example.lidertrade.seller.models;

public class SellerProfileFragmentDailyStatCreditModel {
    private String customerName, customerPhoneNumber1;
    private int packageStatus;
    public SellerProfileFragmentDailyStatCreditModel(){ }

    public SellerProfileFragmentDailyStatCreditModel(String customerName, String customerPhoneNumber1, int packageStatus) {
        this.customerName = customerName;
        this.customerPhoneNumber1 = customerPhoneNumber1;
        this.packageStatus = packageStatus;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhoneNumber1() {
        return customerPhoneNumber1;
    }

    public int getPackageStatus() {
        return packageStatus;
    }
}
