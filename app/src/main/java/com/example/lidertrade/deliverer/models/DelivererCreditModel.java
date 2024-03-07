package com.example.lidertrade.deliverer.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class DelivererCreditModel implements Serializable {
    String customerPhoneNumber1,customerPhoneNumber2,customerPassportGiven,customerPassportGivenDate,
            customerPassport, customerName, customerHouse ,customerStreet , customerVillage, customerDistrict,
            customerJob, customerStatus, customerSalary, creditType,customerBirthDay;
    private String sellerId, orderId;
    private long orderPlacedTime, orderCompletedTime,customerInitialPayment;
    private ArrayList<String> soldProductsList, productsList;
    private int cartTotalPrice, cartTotalQuantity, packageStatus;

    public DelivererCreditModel(){}

    public DelivererCreditModel(String customerPhoneNumber1, String customerPhoneNumber2, String customerPassportGiven,
                                String customerPassportGivenDate, String customerPassport, String customerName,
                                String customerHouse, String customerStreet, String customerVillage,
                                String customerDistrict, String customerJob, String customerStatus,
                                String customerSalary, String creditType, String sellerId, String orderId,
                                long orderPlacedTime, long orderCompletedTime, ArrayList<String> soldProductsList,
                                ArrayList<String> productsList, int cartTotalPrice, int cartTotalQuantity,
                                int packageStatus, String customerBirthDay,long customerInitialPayment) {
        this.customerPhoneNumber1 = customerPhoneNumber1;
        this.customerPhoneNumber2 = customerPhoneNumber2;
        this.customerPassportGiven = customerPassportGiven;
        this.customerPassportGivenDate = customerPassportGivenDate;
        this.customerPassport = customerPassport;
        this.customerName = customerName;
        this.customerHouse = customerHouse;
        this.customerStreet = customerStreet;
        this.customerVillage = customerVillage;
        this.customerDistrict = customerDistrict;
        this.customerJob = customerJob;
        this.customerStatus = customerStatus;
        this.customerSalary = customerSalary;
        this.creditType = creditType;
        this.sellerId = sellerId;
        this.orderId = orderId;
        this.orderPlacedTime = orderPlacedTime;
        this.orderCompletedTime = orderCompletedTime;
        this.soldProductsList = soldProductsList;
        this.productsList = productsList;
        this.cartTotalPrice = cartTotalPrice;
        this.cartTotalQuantity = cartTotalQuantity;
        this.packageStatus = packageStatus;
        this.customerBirthDay = customerBirthDay;
        this.customerInitialPayment=customerInitialPayment;
    }

    public long getCustomerInitialPayment() {
        return customerInitialPayment;
    }

    public void setCustomerInitialPayment(long customerInitialPayment) {
        this.customerInitialPayment = customerInitialPayment;
    }

    public String getCustomerBirthDay() {
        return customerBirthDay;
    }

    public void setCustomerBirthDay(String customerBirthDay) {
        this.customerBirthDay = customerBirthDay;
    }

    public String getCustomerPhoneNumber1() {
        return customerPhoneNumber1;
    }

    public void setCustomerPhoneNumber1(String customerPhoneNumber1) {
        this.customerPhoneNumber1 = customerPhoneNumber1;
    }

    public String getCustomerPhoneNumber2() {
        return customerPhoneNumber2;
    }

    public void setCustomerPhoneNumber2(String customerPhoneNumber2) {
        this.customerPhoneNumber2 = customerPhoneNumber2;
    }

    public String getCustomerPassportGiven() {
        return customerPassportGiven;
    }

    public void setCustomerPassportGiven(String customerPassportGiven) {
        this.customerPassportGiven = customerPassportGiven;
    }

    public String getCustomerPassportGivenDate() {
        return customerPassportGivenDate;
    }

    public void setCustomerPassportGivenDate(String customerPassportGivenDate) {
        this.customerPassportGivenDate = customerPassportGivenDate;
    }

    public String getCustomerPassport() {
        return customerPassport;
    }

    public void setCustomerPassport(String customerPassport) {
        this.customerPassport = customerPassport;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerHouse() {
        return customerHouse;
    }

    public void setCustomerHouse(String customerHouse) {
        this.customerHouse = customerHouse;
    }

    public String getCustomerStreet() {
        return customerStreet;
    }

    public void setCustomerStreet(String customerStreet) {
        this.customerStreet = customerStreet;
    }

    public String getCustomerVillage() {
        return customerVillage;
    }

    public void setCustomerVillage(String customerVillage) {
        this.customerVillage = customerVillage;
    }

    public String getCustomerDistrict() {
        return customerDistrict;
    }

    public void setCustomerDistrict(String customerDistrict) {
        this.customerDistrict = customerDistrict;
    }

    public String getCustomerJob() {
        return customerJob;
    }

    public void setCustomerJob(String customerJob) {
        this.customerJob = customerJob;
    }

    public String getCustomerStatus() {
        return customerStatus;
    }

    public void setCustomerStatus(String customerStatus) {
        this.customerStatus = customerStatus;
    }

    public String getCustomerSalary() {
        return customerSalary;
    }

    public void setCustomerSalary(String customerSalary) {
        this.customerSalary = customerSalary;
    }

    public String getCreditType() {
        return creditType;
    }

    public void setCreditType(String creditType) {
        this.creditType = creditType;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getOrderPlacedTime() {
        return orderPlacedTime;
    }

    public void setOrderPlacedTime(long orderPlacedTime) {
        this.orderPlacedTime = orderPlacedTime;
    }

    public long getOrderCompletedTime() {
        return orderCompletedTime;
    }

    public void setOrderCompletedTime(long orderCompletedTime) {
        this.orderCompletedTime = orderCompletedTime;
    }

    public ArrayList<String> getSoldProductsList() {
        return soldProductsList;
    }

    public void setSoldProductsList(ArrayList<String> soldProductsList) {
        this.soldProductsList = soldProductsList;
    }

    public ArrayList<String> getProductsList() {
        return productsList;
    }

    public void setProductsList(ArrayList<String> productsList) {
        this.productsList = productsList;
    }

    public int getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(int cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public int getCartTotalQuantity() {
        return cartTotalQuantity;
    }

    public void setCartTotalQuantity(int cartTotalQuantity) {
        this.cartTotalQuantity = cartTotalQuantity;
    }

    public int getPackageStatus() {
        return packageStatus;
    }

    public void setPackageStatus(int packageStatus) {
        this.packageStatus = packageStatus;
    }
}
