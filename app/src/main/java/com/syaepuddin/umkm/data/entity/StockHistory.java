package com.syaepuddin.umkm.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "stock_histories")
public class StockHistory {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long productId;
    private String productName;
    private String type; // "IN", "OUT", "ADJUSTMENT"
    private int quantity;
    private String note;
    private long timestamp;

    public StockHistory(long productId, String productName, String type, int quantity, String note, long timestamp) {
        this.productId = productId;
        this.productName = productName;
        this.type = type;
        this.quantity = quantity;
        this.note = note;
        this.timestamp = timestamp;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
