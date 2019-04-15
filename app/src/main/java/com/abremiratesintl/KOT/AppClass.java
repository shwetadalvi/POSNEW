package com.abremiratesintl.KOT;

import android.app.Application;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.mazenrashed.printooth.Printooth;

public class AppClass extends Application {
    @Override public void onCreate() {
        super.onCreate();
        Printooth.INSTANCE.init(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
//        SQLiteDatabase.loadLibs(this);
        MultiDex.install(this);
    }
}
