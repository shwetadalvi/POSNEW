package com.abremiratesintl.KOT.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class TransactionMaster {
    @PrimaryKey(autoGenerate = true)
    private int transMasterId;
    private float itemTotalAmount;
    private float vatAmount;
    private float discountAmount;
    private int totalQty;
    private float grandTotal;
    private String invoiceNo;
    private String invoiceDate;
    private boolean isSale; //false if the item return after sold
    private int isCashBankOrBoth;//0->cash  1-> bank   2-> both(cash+card)
    public String createdDate;
    private int shopId;
    private float cash;
    private float card;
    private String type;
    private String name;


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

    public float getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(float vatAmount) {
        this.vatAmount = vatAmount;
    }

    public float getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(float discountAmount) {
        this.discountAmount = discountAmount;
    }

    public float getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(float grandTotal) {
        this.grandTotal = grandTotal;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public boolean isSale() {
        return isSale;
    }

    public void setSale(boolean sale) {
        isSale = sale;
    }

    public int getIsCashBankOrBoth() {
        return isCashBankOrBoth;
    }

    public void setIsCashBankOrBoth(int isCashBankOrBoth) {
        this.isCashBankOrBoth = isCashBankOrBoth;
    }
    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date().getTime());
    }

    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

   public float getCard() {
        return card;
    }

    public void setCard(float card) {
        this.card = card;
    }

    public float getCash() {
        return cash;
    }

    public void setCash(float cash) {
        this.cash = cash;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
