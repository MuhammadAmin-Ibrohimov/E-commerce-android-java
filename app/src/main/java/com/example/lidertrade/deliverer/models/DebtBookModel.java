package com.example.lidertrade.deliverer.models;

public class DebtBookModel {
    private String debtId, customerName, customerAddress, customerPhone;
    private int debtDeadline;
    private long totalDebt, debtDate;
    public DebtBookModel(){}

    public DebtBookModel(String debtId, String customerName, String customerAddress, String customerPhone,
                         long debtDate, int debtDeadline, long totalDebt) {
        this.debtId = debtId;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerPhone = customerPhone;
        this.debtDate = debtDate;
        this.debtDeadline = debtDeadline;
        this.totalDebt = totalDebt;
    }

    public String getDebtId() {
        return debtId;
    }

    public void setDebtId(String debtId) {
        this.debtId = debtId;
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

    public long getDebtDate() {
        return debtDate;
    }

    public void setDebtDate(long debtDate) {
        this.debtDate = debtDate;
    }

    public int getDebtDeadline() {
        return debtDeadline;
    }

    public void setDebtDeadline(int debtDeadline) {
        this.debtDeadline = debtDeadline;
    }

    public long getTotalDebt() {
        return totalDebt;
    }

    public void setTotalDebt(long totalDebt) {
        this.totalDebt = totalDebt;
    }
}
