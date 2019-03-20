package com.abremiratesintl.KOT.dbHandler;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.util.Log;

import com.abremiratesintl.KOT.models.Items;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ItemsDao {
    @Query("DELETE FROM Items") void deleteAll();

    @Query("SELECT * FROM Items WHERE isDeleted =0") LiveData<List<Items>> getAllItems ();

    @Query("SELECT * FROM Items WHERE itemName=:itemName and isDeleted =0") LiveData<Items> findItemsByName(String itemName);

    @Query("SELECT * FROM Items WHERE itemId=:itemName  and isDeleted =0") LiveData<Items> findItemsById(int itemName);

    @Query("SELECT * FROM Items WHERE categoryId=:categoryId  and isDeleted =0") LiveData<List<Items>> findItemsByCategoryId(int categoryId);
    @Insert(onConflict = REPLACE)
    void insertNewItems(Items category);

    @Query("UPDATE Items SET itemName = :itemName WHERE itemId = :itemId") void editItemsNameById(String itemName, int itemId);

    @Query("UPDATE Items SET isDeleted = :isDelete WHERE itemId = :itemId") void editItemsDeleteById(boolean isDelete, int itemId);

   // @Update() void updateItem(Items item);

  @Query("UPDATE Items SET itemName = :itemName ,price=:price,cost=:cost,vat=:vat ,categoryId=:categoryId WHERE itemId = :itemId") void updateItem( int itemId,String itemName,float price,float cost,float vat,int categoryId);

}
