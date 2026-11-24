package com.syaepuddin.umkm.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.syaepuddin.umkm.data.entity.ShopProfile;

@Dao
public interface ShopProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(ShopProfile profile);

    @Update
    void update(ShopProfile profile);

    @Query("SELECT * FROM shop_profile WHERE id = 1 LIMIT 1")
    LiveData<ShopProfile> getShopProfile();

    @Query("SELECT * FROM shop_profile WHERE id = 1 LIMIT 1")
    ShopProfile getShopProfileSync();
}
