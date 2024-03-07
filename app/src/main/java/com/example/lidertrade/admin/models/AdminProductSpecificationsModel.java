package com.example.lidertrade.admin.models;

import java.io.Serializable;

public class AdminProductSpecificationsModel implements Serializable {

    String name;
    String field;

    public AdminProductSpecificationsModel() {
    }

    public AdminProductSpecificationsModel(String name, String field) {
        this.name = name;
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
