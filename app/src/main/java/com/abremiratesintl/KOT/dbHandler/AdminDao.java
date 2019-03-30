package com.abremiratesintl.KOT.dbHandler;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.abremiratesintl.KOT.models.Admin;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface AdminDao {
    @Insert(onConflict = REPLACE)
    void insertAdmin(Admin admin);

    @Query("SELECT * FROM Admin")
    LiveData<Admin> getAdmin();

    @Query("SELECT * FROM Admin")
    Admin getAdmin1();

    @Query("DELETE FROM Admin") void deleteAll();
}
