package com.abremiratesintl.KOT.dbHandler;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.abremiratesintl.KOT.models.Supplier;

import java.util.List;

@Dao
public interface SupplierDao {
    @Query("DELETE FROM Supplier") void deleteAll();

    @Query("SELECT * FROM Supplier WHERE isDeleted =0") LiveData<List<Supplier>> getAllSupplier();

    @Query("SELECT * FROM Supplier WHERE SupplierName=:SupplierName ") LiveData<Supplier> findSupplierByName(String SupplierName);

    @Query("SELECT * FROM Supplier WHERE SupplierId=:SupplierId ") LiveData<Supplier> findSupplierById(int SupplierId);

    @Insert() void insertNewSupplier(Supplier Supplier);

    @Query("UPDATE Supplier SET SupplierName = :SupplierName WHERE SupplierId = :SupplierId ") void editSupplierNameById(String SupplierName, int SupplierId);

    @Query("UPDATE Supplier SET isDeleted = :isDelete WHERE SupplierId = :SupplierId ") void editSupplierDeleteById(boolean isDelete, int SupplierId);

    @Query("SELECT  SupplierName FROM Supplier WHERE SupplierId=:SupplierId AND isDeleted =0") String getSupplierById(int SupplierId);

}
