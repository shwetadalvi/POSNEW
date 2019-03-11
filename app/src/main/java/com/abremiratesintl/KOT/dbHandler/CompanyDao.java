package com.abremiratesintl.KOT.dbHandler;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.abremiratesintl.KOT.models.Company;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface CompanyDao {
    @Insert(onConflict = REPLACE)
    void insertCompany(Company company);

    @Query("SELECT * FROM Company")
    LiveData<Company> getCompany();
}
