package com.syaepuddin.umkm.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.syaepuddin.umkm.data.entity.Product;
import com.syaepuddin.umkm.data.entity.SaleTransaction;
import com.syaepuddin.umkm.data.entity.ShopProfile;
import com.syaepuddin.umkm.data.repository.AppRepository;
import com.syaepuddin.umkm.util.DateUtil;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final AppRepository mRepository;
    private final LiveData<ShopProfile> shopProfile;
    private final LiveData<List<Product>> lowStockProducts;
    private final LiveData<List<Product>> allProducts;
    private final LiveData<Double> salesToday;
    private final LiveData<Integer> txCountToday;
    private final LiveData<List<SaleTransaction>> recentTransactions;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(application);
        shopProfile = mRepository.getShopProfile();
        lowStockProducts = mRepository.getLowStockProducts();
        allProducts = mRepository.getAllProducts();

        long todayStart = DateUtil.getStartOfDay(System.currentTimeMillis());
        long todayEnd = DateUtil.getEndOfDay(System.currentTimeMillis());

        salesToday = mRepository.getTotalSalesBetween(todayStart, todayEnd);
        txCountToday = mRepository.getTransactionCountBetween(todayStart, todayEnd);
        recentTransactions = mRepository.getTransactionsBetween(todayStart, todayEnd);
    }

    public LiveData<ShopProfile> getShopProfile() { return shopProfile; }
    public LiveData<List<Product>> getLowStockProducts() { return lowStockProducts; }
    public LiveData<List<Product>> getAllProducts() { return allProducts; }
    public LiveData<Double> getSalesToday() { return salesToday; }
    public LiveData<Integer> getTxCountToday() { return txCountToday; }
    public LiveData<List<SaleTransaction>> getRecentTransactions() { return recentTransactions; }
}
