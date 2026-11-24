package com.syaepuddin.umkm.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.syaepuddin.umkm.data.entity.Customer;

import java.util.List;

@Dao
public interface CustomerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Customer customer);

    @Update
    void update(Customer customer);

    @Delete
    void delete(Customer customer);

    @Query("SELECT * FROM customers ORDER BY name ASC")
    LiveData<List<Customer>> getAllCustomers();

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    LiveData<Customer> getCustomerById(long id);

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    Customer getCustomerByIdSync(long id);

    @Query("UPDATE customers SET totalDebt = totalDebt + :amount WHERE id = :id")
    void updateCustomerDebt(long id, double amount);
}
