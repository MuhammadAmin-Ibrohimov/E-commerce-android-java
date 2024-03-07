package com.example.lidertrade.admin.models;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class AdminOrderModel implements Serializable {
        private String sellerId, orderId, customerName, customerAddress, customerPhone;
        private long orderPlacedTime, paymentStatus, orderCompletedTime;
        ArrayList<String> soldProductsList, productsList;
        private HashMap<String, Integer> paymentMap;
        private int cartTotalPrice, cartTotalQuantity, packageStatus;

        public AdminOrderModel(){}

        public AdminOrderModel(String sellerId, String orderId, String customerName, String customerAddress, String customerPhone, long orderPlacedTime,
                                long paymentStatus, long orderCompletedTime, ArrayList<String> soldProductsList,
                               HashMap<String, Integer> paymentMap, int cartTotalPrice, int cartTotalQuantity, int packageStatus, ArrayList<String> productsList) {
            this.sellerId = sellerId;
            this.orderId = orderId;
            this.customerName = customerName;
            this.customerAddress = customerAddress;
            this.customerPhone = customerPhone;
            this.orderPlacedTime = orderPlacedTime;
            this.paymentStatus = paymentStatus;
            this.orderCompletedTime = orderCompletedTime;
            this.soldProductsList = soldProductsList;
            this.paymentMap = paymentMap;
            this.cartTotalPrice = cartTotalPrice;
            this.cartTotalQuantity = cartTotalQuantity;
            this.packageStatus = packageStatus;
            this.productsList = productsList;
        }

    public ArrayList<String> getProductsList() {
        return productsList;
    }

    public void setProductsList(ArrayList<String> productsList) {
        this.productsList = productsList;
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

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getCustomerAddress() {
            return customerAddress;
        }

        public void setCustomerAddress(String customerAddress) {
            this.customerAddress = customerAddress;
        }

        public String getCustomerPhone() {
            return customerPhone;
        }

        public void setCustomerPhone(String customerPhone) {
            this.customerPhone = customerPhone;
        }

        public long getOrderPlacedTime() {
            return orderPlacedTime;
        }

        public void setOrderPlacedTime(long orderPlacedTime) {
            this.orderPlacedTime = orderPlacedTime;
        }

        public long getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(long paymentStatus) {
            this.paymentStatus = paymentStatus;
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


        public HashMap<String, Integer> getPaymentMap() {
            return paymentMap;
        }

        public void setPaymentMap(HashMap<String, Integer> paymentMap) {
            this.paymentMap = paymentMap;
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

