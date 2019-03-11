package com.abremiratesintl.KOT.dbHandler;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.abremiratesintl.KOT.MainActivity;

import java.io.File;

public class UserDbHandler {

    /*private SQLiteDatabase mDb;

    public SQLiteDatabase getWritableDb() {
        File file = new File(Environment.getExternalStorageDirectory() + "/ABR/files/users.db");
        if (file.exists()) {
            SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
                public void preKey(SQLiteDatabase database) {
                    database.rawExecSQL("PRAGMA kdf_iter = 4000");
                }

                public void postKey(SQLiteDatabase database) {
                }
            };
            return openOrCreateDatabase(file.getAbsolutePath(), MainActivity.getPassword(), null, hook);
        } else {
            return null;
        }
    }

    public void closeDb() {
        mDb.close();
    }
/*
    public HashMap<String, String> matchPassword(String colomnName, String password) {
        HashMap<String, String> map = new HashMap<>();
        mDb = getWritableDb();
        if (!mDb.isOpen()) {
            mDb = getWritableDb();
        }
        Cursor cursor = mDb.query("users", new String[]{"username", "userType"}, "POSKey = ? and isDeleted=?", new String[]{password, "0"}, null, null, null);
        if (cursor.moveToNext()) {
            map.put("username", cursor.getString(cursor.getColumnIndex("username")));
            map.put("userType", cursor.getString(cursor.getColumnIndex("userType")));
        }
        return map;
    }

    public HashMap<String, String> adminLogin(String colomnName, String password) {
        HashMap<String, String> map = new HashMap<>();
        Thread thread = new Thread(() -> {
            if (mDb == null || !mDb.isOpen()) {
                mDb = getWritableDb();
            }
            Cursor cursor = mDb.query("users", new String[]{"username", "userType"}, "POSKey = ? and isDeleted=?", new String[]{password, "0"}, null, null, null);
            if (cursor.moveToNext()) {
                map.put("username", cursor.getString(cursor.getColumnIndex("username")));
                map.put("userType", cursor.getString(cursor.getColumnIndex("userType")));
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return map;
    }
*/
    private static final String TAG = "UserDbHandler";
}
