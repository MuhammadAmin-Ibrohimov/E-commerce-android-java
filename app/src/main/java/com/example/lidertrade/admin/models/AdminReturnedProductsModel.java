package com.example.lidertrade.admin.models;

import java.util.Map;

public class AdminReturnedProductsModel {
    private String id;
    private long quantity;
    private Map<String,Object> detailedData;
    public AdminReturnedProductsModel(){}
    public AdminReturnedProductsModel(String id, long quantity, Map<String, Object> detailedData) {
        this.id = id;
        this.quantity = quantity;
        this.detailedData = detailedData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public Map<String, Object> getDetailedData() {
        return detailedData;
    }

    public void setDetailedData(Map<String, Object> detailedData) {
        this.detailedData = detailedData;
    }
}
