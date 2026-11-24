package com.syaepuddin.umkm.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sale_transactions")
public class SaleTransaction {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String transactionNo;
    private long timestamp;
    private double totalAmount;
    private double discountAmount;
    private double finalAmount;
    private String paymentMethod;
    private double paidAmount;
    private double changeAmount;
    private long customerId;
    private String customerName;
    private String status;
    private String note;

    public SaleTransaction(String transactionNo, long timestamp, double totalAmount, double discountAmount, double finalAmount, String paymentMethod, double paidAmount, double changeAmount, long customerId, String customerName, String status, String note) {
        this.transactionNo = transactionNo;
        this.timestamp = timestamp;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.paymentMethod = paymentMethod;
        this.paidAmount = paidAmount;
        this.changeAmount = changeAmount;
        this.customerId = customerId;
        this.customerName = customerName;
        this.status = status;
        this.note = note;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTransactionNo() { return transactionNo; }
    public void setTransactionNo(String transactionNo) { this.transactionNo = transactionNo; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public double getFinalAmount() { return finalAmount; }
    public void setFinalAmount(double finalAmount) { this.finalAmount = finalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }

    public double getChangeAmount() { return changeAmount; }
    public void setChangeAmount(double changeAmount) { this.changeAmount = changeAmount; }

    public long getCustomerId() { return customerId; }
    public void setCustomerId(long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
