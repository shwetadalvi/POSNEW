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
    public static String COMPANY_NAME = "ABR Emirates";
    public static String COMPANY_TELE = "05-1234567";
    public static String COMPANY_ADDRESS = "Sharjah";
    public static String COMPANY_Email = "abr@gmail.com";
    public static String COMPANY_TRN = "1234567890";
    public static String COMPANY_PREFIX = "SJ";
    public static String COMPANY_VAT = "";
    public static final String COMPANY_TAX = "TAX Invoice";
    public static final String COMPANY_ORDER_NO = "Invoice No. ";
    public static final String COMPANY_DATE = "Invoice Date ";
    public static final String COMPANY_ITEM_DESCRIPTION = "Item";
    public static final String COMPANY_ITEM_QUANTITY = "Qty";
    public static final String COMPANY_ITEM_PRICE = "Price";
    public static final String COMPANY_ITEM_AMOUNT = "Amount";
    public static final String COMPANY_ITEM_TOTAL = "Total";
    public static final String COMPANY_ITEM_DISCOUNT = "Discount";
    public static final String TOTAL_DISCOUNT = "Total Discount";
    public static final String COMPANY_ITEM_GROSS_AMOUNT = "Gross Amount";
    public static final String COMPANY_ITEM_VAT = "VAT";
    public static final String COMPANY_ITEM_NET_AMOUNT = "Net Amount";
    public static final String COMPANY_ITEM_PAYMENT = "Payment";
    public static final String ITEM_PAYMENT_TYPE = "Type";
    public static final String COMPANY_TOTAL_ITEM = "Total Item";
    public static final String CATEGORY = "Categort";
    public static final String VAT_EXCLUSIVE = "";
    public static final String INVENTORY_VAT = "INVENTORY_VAT";
    public static final String PAID_AMOUNT = "Paid Amount";
    public static final String CHANGE = "Change";
    public static final String USER_TYPE = "User Type";
    public static final String ADMIN = "Admin";
    public static final String CASHIER = "Cashier";
    public static final String REPORT_DATE = "Date";
    public static final String Sl_NO = "No";
    public static final String PRINTER_TYPE = "Printer Type";
    public static final String FEASYCOM = "Feasycom";
    public static final String PRINTER = "Printer-3B26";
    public static final String IS_VERIFIED = "IS_VERIFIED";
    public static boolean disableDelete = false ;
    public static final String COMPANY_INV_NO = "Inv No";
    public static final String INV_NO = "InNo";
    public static final String COMPANY_SL_NO = "Sl No.";
    public static final String CARD = "Card";
    public static final String CASH = "Cash";
    public static final String TOTAL_CARD = "Total Card : ";
    public static final String TOTAL_CASH = "Total Cash : ";
    public static final String VATABLE_AMT = "Vatable Amt";
    public static final String VAT_AMT = "Vat Amt";
    public static final String TOTAL_VATABLE_AMT = "Total Vatable Amt";
    public static final String TOTAL_VAT_AMT = "Total Vat Amt";
    public static final String PERMISSION_STORAGE_WRITE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_STORAGE_READ = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final int REQUEST_CODE_PERMISSION_STORAGE = 0x123;
    public static final int REQUEST_CODE_IMAGE = 0x124;
    public static final int REQUEST_CODE_ENABLE_BLUETOOTH = 0x125;
    public static final String DISCOUNT = "Discount";
    public static final String TOTAL = "Total";
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
