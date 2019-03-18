package com.abremiratesintl.KOT.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Items {
    @PrimaryKey(autoGenerate = true)
    private int itemId;
    private int categoryId;
    private String itemName;
    private float price;
    private float cost;
    private float vat;
    private String imagePath;
    private boolean isDeleted;
    @Ignore
    private int qty;
    @Ignore
    private float totalItemPrice;
    @Ignore
    private boolean isChecked;
    private boolean isOpen;
    public String createdDate;
    private boolean isSaleReturned;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public float getVat() {
        return vat;
    }

    public void setVat(float vat) {
        this.vat = vat;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        this.isDeleted = deleted;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public float getTotalItemPrice() {
        return totalItemPrice;
    }

    public void setTotalItemPrice(float totalItemPrice) {
        this.totalItemPrice = totalItemPrice;
    }
    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date().getTime());
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isSaleReturned() {
        return isSaleReturned;
    }

    public void setSaleReturned(boolean saleReturned) {
        isSaleReturned = saleReturned;
    }
}
