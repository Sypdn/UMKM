package com.syaepuddin.umkm.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.syaepuddin.umkm.data.entity.Product;

import java.util.List;

@Dao
public interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM products ORDER BY name ASC")
    LiveData<List<Product>> getAllProducts();

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    LiveData<Product> getProductById(long id);

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    Product getProductByIdSync(long id);

    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    Product getProductByBarcodeSync(String barcode);

    @Query("SELECT * FROM products WHERE stock <= minStock ORDER BY stock ASC")
    LiveData<List<Product>> getLowStockProducts();

    @Query("SELECT COUNT(*) FROM products")
    LiveData<Integer> getProductCount();

    @Query("UPDATE products SET stock = stock + :qty, updatedAt = :time WHERE id = :id")
    void updateStock(long id, int qty, long time);
}
