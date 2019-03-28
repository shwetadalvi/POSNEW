package com.abremiratesintl.KOT.models;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity
public class Cashier {
    @PrimaryKey
    private int id = 1;
    private String mCashierName;
    private boolean isItemView;
    private boolean isItemInsert;
    private boolean isItemUpdate;
    private boolean isItemDelete;
    private boolean isCategoryView;
    private boolean isCategoryInsert;
    private boolean isCategoryUpdate;
    private boolean isCategoryDelete;
    private boolean isPOSView;
    private boolean isPOSInsert;
    private boolean isPOSPrint;
    private boolean isPOSDelete;
    private boolean isInventoryView;
    private boolean isInventoryInsert;
    private boolean isInventoryUpdate;
    private boolean isInventoryDelete;
    private boolean isDailyReportView;
    private boolean isDailyReportExport;
    private boolean isSaleReportView;
    private boolean isSaleReportExport;
    private boolean isItemReportView;
    private boolean isItemReportExport;
    private boolean isVatReportView;
    private boolean isVatReportExport;
    private boolean isCategoryReportView;
    private boolean isCategoryReportExport;
    private boolean isInventoryReportView;
    private boolean isInventoryReportExport;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCashierName() {
        return mCashierName;
    }

    public void setCashierName(String companyName) {
        mCashierName = companyName;
    }


    public boolean isItemView() {
        return isItemView;
    }

    public void setItemView(boolean itemView) {
        isItemView = itemView;
    }

    public boolean isItemInsert() {
        return isItemInsert;
    }

    public void setItemInsert(boolean itemInsert) {
        isItemInsert = itemInsert;
    }

    public boolean isItemUpdate() {
        return isItemUpdate;
    }

    public void setItemUpdate(boolean itemUpdate) {
        isItemUpdate = itemUpdate;
    }

    public boolean isItemDelete() {
        return isItemDelete;
    }

    public void setItemDelete(boolean itemDelete) {
        isItemDelete = itemDelete;
    }

    public boolean isCategoryView() {
        return isCategoryView;
    }

    public void setCategoryView(boolean categoryView) {
        isCategoryView = categoryView;
    }

    public boolean isCategoryInsert() {
        return isCategoryInsert;
    }

    public void setCategoryInsert(boolean categoryInsert) {
        isCategoryInsert = categoryInsert;
    }

    public boolean isCategoryUpdate() {
        return isCategoryUpdate;
    }

    public void setCategoryUpdate(boolean categoryUpdate) {
        isCategoryUpdate = categoryUpdate;
    }

    public boolean isCategoryDelete() {
        return isCategoryDelete;
    }

    public void setCategoryDelete(boolean categoryDelete) {
        isCategoryDelete = categoryDelete;
    }

    public boolean isPOSView() {
        return isPOSView;
    }

    public void setPOSView(boolean POSView) {
        isPOSView = POSView;
    }

    public boolean isPOSInsert() {
        return isPOSInsert;
    }

    public void setPOSInsert(boolean POSInsert) {
        isPOSInsert = POSInsert;
    }

    public boolean isPOSPrint() {
        return isPOSPrint;
    }

    public void setPOSPrint(boolean POSPrint) {
        isPOSPrint = POSPrint;
    }

    public boolean isPOSDelete() {
        return isPOSDelete;
    }

    public void setPOSDelete(boolean POSDelete) {
        isPOSDelete = POSDelete;
    }

    public boolean isInventoryView() {
        return isInventoryView;
    }

    public void setInventoryView(boolean inventoryView) {
        isInventoryView = inventoryView;
    }

    public boolean isInventoryInsert() {
        return isInventoryInsert;
    }

    public void setInventoryInsert(boolean inventoryInsert) {
        isInventoryInsert = inventoryInsert;
    }

    public boolean isInventoryUpdate() {
        return isInventoryUpdate;
    }

    public void setInventoryUpdate(boolean inventoryUpdate) {
        isInventoryUpdate = inventoryUpdate;
    }

    public boolean isInventoryDelete() {
        return isInventoryDelete;
    }

    public void setInventoryDelete(boolean inventoryDelete) {
        isInventoryDelete = inventoryDelete;
    }

    public boolean isDailyReportView() {
        return isDailyReportView;
    }

    public void setDailyReportView(boolean dailyReportView) {
        isDailyReportView = dailyReportView;
    }

    public boolean isDailyReportExport() {
        return isDailyReportExport;
    }

    public void setDailyReportExport(boolean dailyReportExport) {
        isDailyReportExport = dailyReportExport;
    }

    public boolean isSaleReportView() {
        return isSaleReportView;
    }

    public void setSaleReportView(boolean saleReportView) {
        isSaleReportView = saleReportView;
    }

    public boolean isSaleReportExport() {
        return isSaleReportExport;
    }

    public void setSaleReportExport(boolean saleReportExport) {
        isSaleReportExport = saleReportExport;
    }

    public boolean isItemReportView() {
        return isItemReportView;
    }

    public void setItemReportView(boolean itemReportView) {
        isItemReportView = itemReportView;
    }

    public boolean isItemReportExport() {
        return isItemReportExport;
    }

    public void setItemReportExport(boolean itemReportExport) {
        isItemReportExport = itemReportExport;
    }

    public boolean isVatReportView() {
        return isVatReportView;
    }

    public void setVatReportView(boolean vatReportView) {
        isVatReportView = vatReportView;
    }

    public boolean isVatReportExport() {
        return isVatReportExport;
    }

    public void setVatReportExport(boolean vatReportExport) {
        isVatReportExport = vatReportExport;
    }

    public boolean isCategoryReportView() {
        return isCategoryReportView;
    }

    public void setCategoryReportView(boolean categoryReportView) {
        isCategoryReportView = categoryReportView;
    }

    public boolean isCategoryReportExport() {
        return isCategoryReportExport;
    }

    public void setCategoryReportExport(boolean categoryReportExport) {
        isCategoryReportExport = categoryReportExport;
    }

    public boolean isInventoryReportView() {
        return isInventoryReportView;
    }

    public void setInventoryReportView(boolean inventoryReportView) {
        isInventoryReportView = inventoryReportView;
    }

    public boolean isInventoryReportExport() {
        return isInventoryReportExport;
    }

    public void setInventoryReportExport(boolean inventoryReportExport) {
        isInventoryReportExport = inventoryReportExport;
    }
}
