package com.abremiratesintl.KOT.dbHandler;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.abremiratesintl.KOT.models.Items;
import com.abremiratesintl.KOT.models.TempItems;

import java.util.List;

@Dao
public interface TempItemsDao {
    @Query("SELECT * FROM TempItems WHERE isDeleted =0") LiveData<List<TempItems>> getAllItems();

    @Query("SELECT * FROM TempItems WHERE itemName=:itemName and isDeleted =0") LiveData<TempItems> findItemsByName(String itemName);

    @Query("SELECT * FROM TempItems WHERE itemId=:itemName  and isDeleted =0") LiveData<TempItems> findItemsById(int itemName);

    @Query("SELECT * FROM TempItems WHERE categoryId=:categoryId  and isDeleted =0") LiveData<List<TempItems>> findItemsByCategoryId(int categoryId);

    @Insert() void insertNewItems(TempItems item);

    @Query("UPDATE TempItems SET itemName = :itemName WHERE itemId = :itemId") void editItemsNameById(String itemName, int itemId);

    @Query("UPDATE TempItems SET isDeleted = :isDelete WHERE itemId = :itemId") void editItemsDeleteById(boolean isDelete, int itemId);

   // @Update() void updateItem(Items item);

  @Query("UPDATE TempItems SET itemName = :itemName ,price=:price,cost=:cost,vat=:vat ,categoryId=:categoryId WHERE itemId = :itemId") void updateItem(int itemId, String itemName, float price, float cost, float vat, int categoryId);

}
