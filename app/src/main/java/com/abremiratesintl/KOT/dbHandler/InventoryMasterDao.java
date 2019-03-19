package com.abremiratesintl.KOT.dbHandler;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.abremiratesintl.KOT.models.InventoryMaster;

import java.util.List;

@Dao
public interface InventoryMasterDao {
    @Query("DELETE FROM InventoryMaster") void deleteAll();

    @Query("SELECT * FROM InventoryMaster") LiveData<List<InventoryMaster>> getAllItems();

    @Query("SELECT * FROM  InventoryMaster WHERE invoiceNo=:invNo") LiveData<InventoryMaster> findByInvNo(String invNo) ;

  //  @Query("SELECT * FROM  InventoryMaster WHERE invoiceNo=:invNo") LiveData<InventoryMaster> findByInvNo(String invNo);

    //
    @Query("SELECT MAX(transMasterId) FROM InventoryMaster") int findTransMasterOfMaxId();

    //
    @Query("SELECT * FROM InventoryMaster WHERE purchaseDate BETWEEN date(:from) AND date(:to)") LiveData<List<InventoryMaster>> findItemsByBetween(String from, String to);

    @Insert() void insertNewItems(InventoryMaster category);


}
