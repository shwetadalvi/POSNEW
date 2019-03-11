package com.abremiratesintl.KOT.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {
    Context mContext;

    public PrefUtils(Context context) {
        mContext = context;
    }

    public void putStringPreference(String prefName, String key, String value) {
        getPrefernce(prefName).edit().putString(key, value).apply();
    }

    private SharedPreferences getPrefernce(String preferenceName) {
        return mContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    public String getStringPrefrence(String prefName, String key, String value) {
        return getPrefernce(prefName).getString(key, value);
    }

    public void putBooleanPreference(String prefName, String key, boolean value) {
        getPrefernce(prefName).edit().putBoolean(key, value).apply();
    }

    public boolean getBooleanPrefrence(String prefName, String key, boolean value) {
        return getPrefernce(prefName).getBoolean(key, value);
    }
}
