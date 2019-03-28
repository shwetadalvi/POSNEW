package com.abremiratesintl.KOT.dbHandler;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.abremiratesintl.KOT.models.Cashier;
import com.abremiratesintl.KOT.models.Company;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface CashierDao {
    @Insert(onConflict = REPLACE)
    void insertCashier(Cashier cashier);

    @Query("SELECT * FROM Cashier")
    LiveData<Cashier> getCashier();

    @Query("DELETE FROM Cashier") void deleteAll();
}
