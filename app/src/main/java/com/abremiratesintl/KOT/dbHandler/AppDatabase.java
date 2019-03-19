package com.abremiratesintl.KOT.dbHandler;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.abremiratesintl.KOT.models.Category;
import com.abremiratesintl.KOT.models.Company;
import com.abremiratesintl.KOT.models.InventoryMaster;
import com.abremiratesintl.KOT.models.InventoryTransaction;
import com.abremiratesintl.KOT.models.Items;
import com.abremiratesintl.KOT.models.Transaction;
import com.abremiratesintl.KOT.models.TransactionMaster;
import com.abremiratesintl.KOT.models.User;

@Database(entities = {Category.class, Items.class, User.class, TransactionMaster.class, Transaction.class, Company.class, InventoryMaster.class, InventoryTransaction.class}, version = 3,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase mInstance;

    public static AppDatabase getInstance(Context context) {
        if (mInstance == null) {
            mInstance = Room.databaseBuilder(context, AppDatabase.class, "KOT")
                    .fallbackToDestructiveMigration()
                    //.addMigrations(MIGRATION_1_2)
                   .addMigrations(MIGRATION_1_2,MIGRATION_2_3)
                    .build();
        }
        return mInstance;
    }

    public static void closeDb(){
        if (mInstance.isOpen()){
            mInstance.close();
        }
    }
  static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

        }
    };
      static final Migration MIGRATION_2_3 = new Migration(2, 3) {
         @Override
         public void migrate(SupportSQLiteDatabase database) {
          //  database.execSQL("ALTER TABLE Company ADD COLUMN  gst INTEGER");

         }
     };

   public abstract CategoryDao mCategoryDao();
   public abstract ItemsDao mItemsDao();
   public abstract TransactionDao mTransactionDao();
   public abstract TransactionMasterDao mTransactionMasterDao();
   public abstract CompanyDao mCompanyDao();
   public abstract InventoryMasterDao mInventoryMasterDao();
   public abstract InventoryTransactionDao mInventoryTransactionDao();

}
