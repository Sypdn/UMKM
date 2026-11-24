package com.syaepuddin.umkm.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shop_profile")
public class ShopProfile {
    @PrimaryKey
    private int id = 1;
    private String shopName;
    private String ownerName;
    private String address;
    private String phone;
    private String logoPath;
    private String receiptFooter;
    private String pinCode;
    private boolean isPinEnabled;
    private boolean isDarkMode;

    public ShopProfile(String shopName, String ownerName, String address, String phone, String receiptFooter) {
        this.id = 1;
        this.shopName = shopName;
        this.ownerName = ownerName;
        this.address = address;
        this.phone = phone;
        this.receiptFooter = receiptFooter;
        this.isPinEnabled = false;
        this.isDarkMode = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }

    public String getReceiptFooter() { return receiptFooter; }
    public void setReceiptFooter(String receiptFooter) { this.receiptFooter = receiptFooter; }

    public String getPinCode() { return pinCode; }
    public void setPinCode(String pinCode) { this.pinCode = pinCode; }

    public boolean isPinEnabled() { return isPinEnabled; }
    public void setPinEnabled(boolean pinEnabled) { isPinEnabled = pinEnabled; }

    public boolean isDarkMode() { return isDarkMode; }
    public void setDarkMode(boolean darkMode) { isDarkMode = darkMode; }
}
