package com.syaepuddin.umkm.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.syaepuddin.umkm.data.entity.StockHistory;

import java.util.List;

@Dao
public interface StockHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(StockHistory history);

    @Query("SELECT * FROM stock_histories WHERE productId = :productId ORDER BY timestamp DESC")
    LiveData<List<StockHistory>> getHistoryForProduct(long productId);

    @Query("SELECT * FROM stock_histories ORDER BY timestamp DESC LIMIT 50")
    LiveData<List<StockHistory>> getRecentHistory();
}
