package com.example.lidertrade.deliverer.models;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class DelivererPendingFragmentModel implements Serializable {

    private String orderId, sellerId, customerName, customerAddress, customerPhone;
    private long orderPlacedTime, paymentStatus;
    private ArrayList<String> soldProductsList, productsList;
    private HashMap<String, Integer>  paymentMap;
    private int cartTotalPrice, cartTotalQuantity, packageStatus;

    public DelivererPendingFragmentModel(){}

    public DelivererPendingFragmentModel(String orderId, String sellerId, String customerName, String customerAddress, String customerPhone,
                                         long orderPlacedTime, long paymentStatus, ArrayList<String> soldProductsList, ArrayList<String> productsList,
                                         HashMap<String, Integer> paymentMap, int cartTotalPrice, int cartTotalQuantity, int packageStatus) {
        this.orderId = orderId;
        this.sellerId = sellerId;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerPhone = customerPhone;
        this.orderPlacedTime = orderPlacedTime;
        this.paymentStatus = paymentStatus;
        this.soldProductsList = soldProductsList;
        this.productsList = productsList;
        this.paymentMap = paymentMap;
        this.cartTotalPrice = cartTotalPrice;
        this.cartTotalQuantity = cartTotalQuantity;
        this.packageStatus = packageStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public long getOrderPlacedTime() {
        return orderPlacedTime;
    }

    public long getPaymentStatus() {
        return paymentStatus;
    }

    public ArrayList<String> getSoldProductsList() {
        return soldProductsList;
    }

    public ArrayList<String> getProductsList() {
        return productsList;
    }

    public HashMap<String, Integer> getPaymentMap() {
        return paymentMap;
    }

    public int getCartTotalPrice() {
        return cartTotalPrice;
    }

    public int getCartTotalQuantity() {
        return cartTotalQuantity;
    }

    public int getPackageStatus() {
        return packageStatus;
    }
}
