package com.abremiratesintl.KOT.dbHandler;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.abremiratesintl.KOT.models.InventoryTransaction;
import com.abremiratesintl.KOT.models.Transaction;

import java.util.List;

@Dao
public interface InventoryTransactionDao {
    @Query("DELETE FROM InventoryTransaction") void deleteAll();

    @Query("SELECT * FROM InventoryTransaction") LiveData<List<InventoryTransaction>> getAllItems();
    @Query("SELECT * FROM InventoryTransaction where invoiceDate =:date") LiveData<List<InventoryTransaction>> getTodaysAllItems(String date);
    @Query("SELECT * FROM InventoryTransaction") List<InventoryTransaction> getAllItemsforExport();

    @Query("SELECT * FROM InventoryTransaction WHERE transMasterId = :trId") LiveData<List<InventoryTransaction>> findByTransactionMasterId(int trId);

//    @Query("SELECT * FROM Transactions  WHERE itemName=:itemName") LiveData<InventoryTransaction> findItemsByName(String itemName);
//
//    @Query("SELECT * FROM Transactions  WHERE categoryId=:categoryId") LiveData<List<InventoryTransaction>> findItemsByCategoryId(int categoryId);

    @Insert() void insertNewItems(InventoryTransaction category);
    @Query("SELECT * FROM InventoryTransaction  WHERE itemId=:itemId") LiveData<List<InventoryTransaction>> getItemsByItemId(int itemId);
    @Query("SELECT * FROM InventoryTransaction WHERE (itemId=:itemId) AND invoiceDate BETWEEN date(:from) AND date(:to)") LiveData<List<InventoryTransaction>> findItemsByItemIdBetween(String from, String to, int itemId);
//    @Query("UPDATE Transactions SET itemName = :itemName WHERE itemId = :itemId") void editItemsNameById(String itemName, int itemId);
//
//    @Query("UPDATE Transactions SET isDeleted = :isDelete WHERE itemId = :itemId") void editItemsDeleteById(boolean isDelete, int itemId);
@Query("SELECT * FROM InventoryTransaction WHERE category=:category AND invoiceDate BETWEEN date(:from) AND date(:to)") LiveData<List<InventoryTransaction>> findItemsByCategoryBetween(String from, String to, String category);

    @Query("SELECT * FROM InventoryTransaction WHERE category=:category") LiveData<List<InventoryTransaction>> getAllItemsByCategoryName(String category);
}
