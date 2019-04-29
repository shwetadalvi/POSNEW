package com.abremiratesintl.KOT.dbHandler;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.abremiratesintl.KOT.models.TransactionMaster;

import java.util.List;

@Dao
public interface TransactionMasterDao {
    @Query("DELETE FROM TransactionMaster") void deleteAll();

    @Query("SELECT * FROM TransactionMaster") LiveData<List<TransactionMaster>> getAllItems();
    @Query("SELECT * FROM TransactionMaster where invoiceDate =:date") LiveData<List<TransactionMaster>> getTodaysAllItems(String date);

    @Query("SELECT * FROM  TransactionMaster WHERE invoiceNo=:invNo") LiveData<TransactionMaster> findByInvNo(String invNo) ;

  //  @Query("SELECT * FROM  TransactionMaster WHERE invoiceNo=:invNo") LiveData<TransactionMaster> findByInvNo(String invNo);

    //
    @Query("SELECT MAX(transMasterId) FROM TRANSACTIONMASTER") int findTransMasterOfMaxId();

    //
    @Query("SELECT * FROM TransactionMaster WHERE invoiceDate BETWEEN date(:from) AND date(:to)") LiveData<List<TransactionMaster>> findItemsByBetween(String from, String to);

    @Insert() void insertNewItems(TransactionMaster category);
    @Query("SELECT COUNT(transMasterId) FROM TRANSACTIONMASTER ")
    int getCount();
    @Query("SELECT * FROM TransactionMaster WHERE invoiceDate =  date(:to)") LiveData<List<TransactionMaster>> findItemsByDate( String to);
//    @Query("UPDATE TransactionMaster SET itemName = :itemName WHERE itemId = :itemId") void editItemsNameById(String itemName, int itemId);
//
//    @Query("UPDATE TransactionMaster SET isDeleted = :isDelete WHERE itemId = :itemId") void editItemsDeleteById(boolean isDelete, int itemId);


}
