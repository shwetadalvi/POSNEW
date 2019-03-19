package com.abremiratesintl.KOT.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class InventoryMaster {
    @PrimaryKey(autoGenerate = true)
    private int transMasterId;
    private float itemTotalAmount;
    public String createdDate;
    private String supplier;
    private String refference;
    private String invoiceNo;
    private String purchaseDate;

    public int getTransMasterId() {
        return transMasterId;
    }

    public void setTransMasterId(int transMasterId) {
        this.transMasterId = transMasterId;
    }

    public float getItemTotalAmount() {
        return itemTotalAmount;
    }

    public void setItemTotalAmount(float itemTotalAmount) {
        this.itemTotalAmount = itemTotalAmount;
    }


    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date().getTime());
    }


    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getRefference() {
        return refference;
    }

    public void setRefference(String refference) {
        this.refference = refference;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
