package com.syaepuddin.umkm.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.syaepuddin.umkm.data.entity.SaleItem;
import com.syaepuddin.umkm.data.entity.SaleTransaction;

import java.util.List;

@Dao
public interface SaleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTransaction(SaleTransaction transaction);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSaleItems(List<SaleItem> items);

    @Query("SELECT * FROM sale_transactions ORDER BY timestamp DESC")
    LiveData<List<SaleTransaction>> getAllTransactions();

    @Query("SELECT * FROM sale_transactions WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    LiveData<List<SaleTransaction>> getTransactionsBetween(long startTime, long endTime);

    @Query("SELECT * FROM sale_transactions WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    List<SaleTransaction> getTransactionsBetweenSync(long startTime, long endTime);

    @Query("SELECT SUM(finalAmount) FROM sale_transactions WHERE timestamp >= :startTime AND timestamp <= :endTime")
    LiveData<Double> getTotalSalesBetween(long startTime, long endTime);

    @Query("SELECT SUM(finalAmount) FROM sale_transactions WHERE timestamp >= :startTime AND timestamp <= :endTime")
    Double getTotalSalesBetweenSync(long startTime, long endTime);

    @Query("SELECT COUNT(*) FROM sale_transactions WHERE timestamp >= :startTime AND timestamp <= :endTime")
    LiveData<Integer> getTransactionCountBetween(long startTime, long endTime);

    @Query("SELECT * FROM sale_items WHERE transactionId = :transactionId")
    LiveData<List<SaleItem>> getSaleItemsForTransaction(long transactionId);

    @Query("SELECT * FROM sale_items WHERE transactionId = :transactionId")
    List<SaleItem> getSaleItemsForTransactionSync(long transactionId);
}
