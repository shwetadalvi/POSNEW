package com.abremiratesintl.KOT.dbHandler;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.abremiratesintl.KOT.models.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {
    @Query("SELECT * FROM Transactions") LiveData<List<Transaction>> getAllItems ();
    @Query("SELECT * FROM Transactions") List<Transaction> getAllItemsforExport ();

    @Query("SELECT * FROM Transactions WHERE transMasterId = :trId") LiveData<List<Transaction>> findByTransactionMasterId(int trId);

//    @Query("SELECT * FROM Transactions  WHERE itemName=:itemName") LiveData<Transaction> findItemsByName(String itemName);
//
//    @Query("SELECT * FROM Transactions  WHERE categoryId=:categoryId") LiveData<List<Transaction>> findItemsByCategoryId(int categoryId);

    @Insert() void insertNewItems(Transaction category);
    @Query("SELECT * FROM Transactions  WHERE itemId=:itemId") LiveData<List<Transaction>> getItemsByItemId(int itemId);
    @Query("SELECT * FROM Transactions WHERE (itemId=:itemId) AND invoiceDate BETWEEN date(:from) AND date(:to)") LiveData<List<Transaction>> findItemsByItemIdBetween(String from, String to,int itemId);
//    @Query("UPDATE Transactions SET itemName = :itemName WHERE itemId = :itemId") void editItemsNameById(String itemName, int itemId);
//
//    @Query("UPDATE Transactions SET isDeleted = :isDelete WHERE itemId = :itemId") void editItemsDeleteById(boolean isDelete, int itemId);
@Query("SELECT * FROM Transactions WHERE category=:category AND invoiceDate BETWEEN date(:from) AND date(:to)") LiveData<List<Transaction>> findItemsByCategoryBetween(String from, String to,String category );

    @Query("SELECT * FROM Transactions WHERE category=:category") LiveData<List<Transaction>> getAllItemsByCategoryName (String category);
}
