package com.example.lidertrade.admin.models;


import java.io.Serializable;

public class AdminUserModel implements Serializable {
    private String userName, userStatus, userPhone, userEmail, userUsername, userPassword, userImage, userAddress,userId;
    private int cashSalaryPercent,creditSalaryPercent;

    public AdminUserModel(){
    }

    public AdminUserModel(String userName, String userStatus, String userPhone,
                                         String userEmail, String userUsername, String userPassword,
                                         String userImage, String userAddress, String userId, int cashSalaryPercent, int creditSalaryPercent) {
        this.userName = userName;
        this.userStatus = userStatus;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
        this.userUsername = userUsername;
        this.userPassword = userPassword;
        this.userImage = userImage;
        this.userAddress = userAddress;
        this.userId = userId;
        this.cashSalaryPercent = cashSalaryPercent;
        this.creditSalaryPercent = creditSalaryPercent;
    }

    public int getCashSalaryPercent() {
        return cashSalaryPercent;
    }

    public void setCashSalaryPercent(int cashSalaryPercent) {
        this.cashSalaryPercent = cashSalaryPercent;
    }

    public int getCreditSalaryPercent() {
        return creditSalaryPercent;
    }

    public void setCreditSalaryPercent(int creditSalaryPercent) {
        this.creditSalaryPercent = creditSalaryPercent;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserImage() {
        return userImage;
    }
}
