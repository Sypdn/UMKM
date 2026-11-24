package com.syaepuddin.umkm.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.syaepuddin.umkm.data.entity.Expense;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Expense expense);

    @Delete
    void delete(Expense expense);

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    LiveData<List<Expense>> getAllExpenses();

    @Query("SELECT * FROM expenses WHERE date >= :startTime AND date <= :endTime ORDER BY date DESC")
    LiveData<List<Expense>> getExpensesBetween(long startTime, long endTime);

    @Query("SELECT SUM(amount) FROM expenses WHERE date >= :startTime AND date <= :endTime")
    LiveData<Double> getTotalExpensesBetween(long startTime, long endTime);

    @Query("SELECT SUM(amount) FROM expenses WHERE date >= :startTime AND date <= :endTime")
    Double getTotalExpensesBetweenSync(long startTime, long endTime);
}
