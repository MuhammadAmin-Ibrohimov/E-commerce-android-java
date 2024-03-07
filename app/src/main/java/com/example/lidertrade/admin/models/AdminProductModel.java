package com.example.lidertrade.admin.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class AdminProductModel implements Serializable{
        String brand, category, subCategory, productName, productModel, productId, productDescription;
        ArrayList<String> imageUrl;
        int boughtPrice, cashPricePercent, cashPrice, creditPricePercent, creditPrice, productQuantity;
        Map<String,Long>creditPayment;
        ArrayList<AdminProductSpecificationsModel> productSpecification;
        boolean visibility;

        public AdminProductModel() {
        }

    public AdminProductModel(String brand, String category, String subCategory, String productName, String productModel, String productId, String productDescription, ArrayList<String> imageUrl, int boughtPrice, int cashPricePercent, int cashPrice, int creditPricePercent, int creditPrice, int productQuantity, Map<String, Long> creditPayment, ArrayList<AdminProductSpecificationsModel> productSpecification, boolean visibility) {
        this.brand = brand;
        this.category = category;
        this.subCategory = subCategory;
        this.productName = productName;
        this.productModel = productModel;
        this.productId = productId;
        this.productDescription = productDescription;
        this.imageUrl = imageUrl;
        this.boughtPrice = boughtPrice;
        this.cashPricePercent = cashPricePercent;
        this.cashPrice = cashPrice;
        this.creditPricePercent = creditPricePercent;
        this.creditPrice = creditPrice;
        this.productQuantity = productQuantity;
        this.creditPayment = creditPayment;
        this.productSpecification = productSpecification;
        this.visibility = visibility;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public ArrayList<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(ArrayList<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getBoughtPrice() {
        return boughtPrice;
    }

    public void setBoughtPrice(int boughtPrice) {
        this.boughtPrice = boughtPrice;
    }

    public int getCashPricePercent() {
        return cashPricePercent;
    }

    public void setCashPricePercent(int cashPricePercent) {
        this.cashPricePercent = cashPricePercent;
    }

    public int getCashPrice() {
        return cashPrice;
    }

    public void setCashPrice(int cashPrice) {
        this.cashPrice = cashPrice;
    }

    public int getCreditPricePercent() {
        return creditPricePercent;
    }

    public void setCreditPricePercent(int creditPricePercent) {
        this.creditPricePercent = creditPricePercent;
    }

    public int getCreditPrice() {
        return creditPrice;
    }

    public void setCreditPrice(int creditPrice) {
        this.creditPrice = creditPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Map<String, Long> getCreditPayment() {
        return creditPayment;
    }

    public void setCreditPayment(Map<String, Long> creditPayment) {
        this.creditPayment = creditPayment;
    }

    public ArrayList<AdminProductSpecificationsModel> getProductSpecification() {
        return productSpecification;
    }

    public void setProductSpecification(ArrayList<AdminProductSpecificationsModel> productSpecification) {
        this.productSpecification = productSpecification;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }
}
