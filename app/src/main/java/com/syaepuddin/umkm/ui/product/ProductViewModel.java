package com.syaepuddin.umkm.ui.product;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.syaepuddin.umkm.data.entity.Category;
import com.syaepuddin.umkm.data.entity.Product;
import com.syaepuddin.umkm.data.repository.AppRepository;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {

    private final AppRepository mRepository;
    private final LiveData<List<Product>> products;
    private final LiveData<List<Category>> categories;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(application);
        products = mRepository.getAllProducts();
        categories = mRepository.getAllCategories();
    }

    public LiveData<List<Product>> getProducts() { return products; }
    public LiveData<List<Category>> getCategories() { return categories; }

    public void insertProduct(Product product, Runnable onDone) {
        mRepository.insertProduct(product, onDone);
    }

    public void updateProduct(Product product, Runnable onDone) {
        mRepository.updateProduct(product, onDone);
    }

    public void deleteProduct(Product product, Runnable onDone) {
        mRepository.deleteProduct(product, onDone);
    }

    public void restockProduct(long productId, String productName, int addQty, String note, Runnable onDone) {
        mRepository.updateStock(productId, productName, addQty, note, onDone);
    }
}
