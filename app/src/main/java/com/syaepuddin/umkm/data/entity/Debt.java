package com.syaepuddin.umkm.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "debts")
public class Debt {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long customerId;
    private long transactionId;
    private double totalAmount;
    private double remainingAmount;
    private String status;
    private long dueDate;
    private long createdAt;

    public Debt(long customerId, long transactionId, double totalAmount, double remainingAmount, String status, long dueDate) {
        this.customerId = customerId;
        this.transactionId = transactionId;
        this.totalAmount = totalAmount;
        this.remainingAmount = remainingAmount;
        this.status = status != null ? status : "BELUM_LUNAS";
        this.dueDate = dueDate;
        this.createdAt = System.currentTimeMillis();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getCustomerId() { return customerId; }
    public void setCustomerId(long customerId) { this.customerId = customerId; }

    public long getTransactionId() { return transactionId; }
    public void setTransactionId(long transactionId) { this.transactionId = transactionId; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(double remainingAmount) { this.remainingAmount = remainingAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getDueDate() { return dueDate; }
    public void setDueDate(long dueDate) { this.dueDate = dueDate; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
