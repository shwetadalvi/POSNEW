package com.abremiratesintl.KOT.dbHandler;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.abremiratesintl.KOT.models.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {
    @Query("SELECT * FROM Transactions ") LiveData<List<Transaction>> getAllItems();

    @Query("SELECT * FROM Transactions WHERE transMasterId = :trId") LiveData<List<Transaction>> findByTransactionMasterId(int trId);

//    @Query("SELECT * FROM Transactions  WHERE itemName=:itemName") LiveData<Transaction> findItemsByName(String itemName);
//
//    @Query("SELECT * FROM Transactions  WHERE categoryId=:categoryId") LiveData<List<Transaction>> findItemsByCategoryId(int categoryId);

    @Insert() void insertNewItems(Transaction category);

//    @Query("UPDATE Transactions SET itemName = :itemName WHERE itemId = :itemId") void editItemsNameById(String itemName, int itemId);
//
//    @Query("UPDATE Transactions SET isDeleted = :isDelete WHERE itemId = :itemId") void editItemsDeleteById(boolean isDelete, int itemId);
}