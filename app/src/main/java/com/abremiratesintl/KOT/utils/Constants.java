package com.abremiratesintl.KOT.utils;

import android.Manifest;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Constants {
    public static final String DEAFULT_PREFS = "KOT-PREFS";
    public static final String LOGIN_PREF_KEY = "isLoggedIn";
    public static final String FIRST_RUN_PREF_KEY = "isFirst";
    public static final String PRINTER_PREF_KEY = "printer";
    public static final String COMPANY_ID_PREF = "shopId";
    public static final String COMPANY_NAME = "ABR Emirates";
    public static final String COMPANY_TELE = "05-1234567";
    public static final String COMPANY_ADDRESS = "Sharjah";
    public static final String COMPANY_TAX = "TAX Invoice";
    public static final String COMPANY_ORDER_NO = "Invoice No.  ";
    public static final String COMPANY_DATE = "Invoice Date  ";
    public static final String COMPANY_ITEM_DESCRIPTION = "Item";
    public static final String COMPANY_ITEM_QUANTITY = "Qty";
    public static final String COMPANY_ITEM_PRICE = "Price";
    public static final String COMPANY_ITEM_AMOUNT = "Amount";
    public static final String COMPANY_ITEM_TOTAL = "Total";
    public static final String COMPANY_ITEM_DISCOUNT = "Discount";
    public static final String COMPANY_ITEM_GROSS_AMOUNT = "Gross Amount";
    public static final String COMPANY_ITEM_VAT = "VAT";
    public static final String COMPANY_ITEM_NET_AMOUNT = "Net Amount";
    public static final String PERMISSION_STORAGE_WRITE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_STORAGE_READ = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final int REQUEST_CODE_PERMISSION_STORAGE = 0x123;
    public static final int REQUEST_CODE_IMAGE = 0x124;
    public static final int REQUEST_CODE_ENABLE_BLUETOOTH = 0x125;

    public static String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date().getTime());
    }

    public static String getCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date().getTime());
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}