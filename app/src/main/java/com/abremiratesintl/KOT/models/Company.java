package com.abremiratesintl.KOT.models;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity
public class Company {
    @PrimaryKey
    private int id = 1;
    private String mCompanyName;
    private String mCompanyTel;
    private String mCompanyAddress;
    private String mCompanyTrn;
    private String mCompanyPrefix;
    private String mCompanyVat;


    public String getCompanyName() {
        return mCompanyName;
    }

    public void setCompanyName(String companyName) {
        mCompanyName = companyName;
    }

    public String getCompanyTel() {
        return mCompanyTel;
    }

    public void setCompanyTel(String companyTel) {
        mCompanyTel = companyTel;
    }

    public String getCompanyAddress() {
        return mCompanyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        mCompanyAddress = companyAddress;
    }

    public String getCompanyTrn() {
        return mCompanyTrn;
    }

    public void setCompanyTrn(String companyTrn) {
        mCompanyTrn = companyTrn;
    }

    public String getCompanyPrefix() {
        return mCompanyPrefix;
    }

    public void setCompanyPrefix(String companyPrefix) {
        mCompanyPrefix = companyPrefix;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getCompanyVat() {
        return mCompanyVat;
    }

    public void setCompanyVat(String mCompanyVat) {
        this.mCompanyVat = mCompanyVat;
    }
}
