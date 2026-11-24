package com.syaepuddin.umkm.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "debt_payments")
public class DebtPayment {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long debtId;
    private double amountPaid;
    private long paymentDate;
    private String note;

    public DebtPayment(long debtId, double amountPaid, long paymentDate, String note) {
        this.debtId = debtId;
        this.amountPaid = amountPaid;
        this.paymentDate = paymentDate;
        this.note = note;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getDebtId() { return debtId; }
    public void setDebtId(long debtId) { this.debtId = debtId; }

    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }

    public long getPaymentDate() { return paymentDate; }
    public void setPaymentDate(long paymentDate) { this.paymentDate = paymentDate; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
