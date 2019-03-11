package com.abremiratesintl.KOT.dbHandler;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.abremiratesintl.KOT.models.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM Category WHERE isDeleted =0") LiveData<List<Category>> getAllCategory();

    @Query("SELECT * FROM Category WHERE categoryName=:categoryName") LiveData<Category> findCategoryByName(String categoryName);

    @Query("SELECT * FROM Category WHERE categoryId=:categoryId") LiveData<Category> findCategoryById(int categoryId);

    @Insert() void insertNewCategory(Category category);

    @Query("UPDATE Category SET categoryName = :categoryName WHERE categoryId = :categoryId") void editCategoryNameById(String categoryName, int categoryId);

    @Query("UPDATE Category SET isDeleted = :isDelete WHERE categoryId = :categoryId") void editCategoryDeleteById(boolean isDelete, int categoryId);
}
