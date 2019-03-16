package com.abremiratesintl.KOT.dbHandler;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.abremiratesintl.KOT.models.Category;
import com.abremiratesintl.KOT.models.Company;
import com.abremiratesintl.KOT.models.Items;
import com.abremiratesintl.KOT.models.TempItems;
import com.abremiratesintl.KOT.models.Transaction;
import com.abremiratesintl.KOT.models.TransactionMaster;
import com.abremiratesintl.KOT.models.User;

@Database(entities = {Category.class, Items.class, User.class, TransactionMaster.class, Transaction.class, Company.class,TempItems.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase mInstance;

    public static AppDatabase getInstance(Context context) {
        if (mInstance == null) {
            mInstance = Room.databaseBuilder(context, AppDatabase.class, "KOT").build();
        }
        return mInstance;
    }

    public static void closeDb(){
        if (mInstance.isOpen()){
            mInstance.close();
        }
    }
   public abstract CategoryDao mCategoryDao();
   public abstract ItemsDao mItemsDao();
   public abstract TransactionDao mTransactionDao();
   public abstract TransactionMasterDao mTransactionMasterDao();
   public abstract CompanyDao mCompanyDao();
    public abstract TempItemsDao mTempItemsDao();
}
