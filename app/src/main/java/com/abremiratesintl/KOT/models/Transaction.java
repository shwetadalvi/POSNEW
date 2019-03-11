package com.abremiratesintl.KOT.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "Transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private int transactionId;
    private int transMasterId;
    private int itemId;
    private int qty;
    private float price;
    private float vatPercentage;
    private float vat;
    private float grandTotal;
    public String createdDate;

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getTransMasterId() {
        return transMasterId;
    }

    public void setTransMasterId(int transMasterId) {
        this.transMasterId = transMasterId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getVatPercentage() {
        return vatPercentage;
    }

    public void setVatPercentage(float vatPercentage) {
        this.vatPercentage = vatPercentage;
    }

    public float getVat() {
        return vat;
    }

    public void setVat(float vat) {
        this.vat = vat;
    }

    public float getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(float grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date().getTime());
    }
}