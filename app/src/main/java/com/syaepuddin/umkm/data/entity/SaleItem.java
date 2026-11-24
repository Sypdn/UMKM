package com.syaepuddin.umkm.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sale_items")
public class SaleItem {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long transactionId;
    private long productId;
    private String productName;
    private String unit;
    private double buyPrice;
    private double sellPrice;
    private int quantity;
    private double subtotal;

    public SaleItem(long transactionId, long productId, String productName, String unit, double buyPrice, double sellPrice, int quantity, double subtotal) {
        this.transactionId = transactionId;
        this.productId = productId;
        this.productName = productName;
        this.unit = unit;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getTransactionId() { return transactionId; }
    public void setTransactionId(long transactionId) { this.transactionId = transactionId; }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getBuyPrice() { return buyPrice; }
    public void setBuyPrice(double buyPrice) { this.buyPrice = buyPrice; }

    public double getSellPrice() { return sellPrice; }
    public void setSellPrice(double sellPrice) { this.sellPrice = sellPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
