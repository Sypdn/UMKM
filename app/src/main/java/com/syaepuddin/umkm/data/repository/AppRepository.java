package com.syaepuddin.umkm.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.syaepuddin.umkm.data.db.AppDatabase;
import com.syaepuddin.umkm.data.entity.Category;
import com.syaepuddin.umkm.data.entity.Customer;
import com.syaepuddin.umkm.data.entity.Debt;
import com.syaepuddin.umkm.data.entity.DebtPayment;
import com.syaepuddin.umkm.data.entity.Expense;
import com.syaepuddin.umkm.data.entity.Product;
import com.syaepuddin.umkm.data.entity.SaleItem;
import com.syaepuddin.umkm.data.entity.SaleTransaction;
import com.syaepuddin.umkm.data.entity.ShopProfile;
import com.syaepuddin.umkm.data.entity.StockHistory;
import com.syaepuddin.umkm.util.AppExecutors;

import java.util.List;

public class AppRepository {
    private static volatile AppRepository sInstance;
    private final AppDatabase mDatabase;
    private final AppExecutors mExecutors;

    private AppRepository(Context context) {
        mDatabase = AppDatabase.getInstance(context);
        mExecutors = AppExecutors.getInstance();
    }

    public static AppRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppRepository.class) {
                if (sInstance == null) {
                    sInstance = new AppRepository(context);
                }
            }
        }
        return sInstance;
    }

    // Products
    public LiveData<List<Product>> getAllProducts() { return mDatabase.productDao().getAllProducts(); }
    public LiveData<Product> getProductById(long id) { return mDatabase.productDao().getProductById(id); }
    public LiveData<List<Product>> getLowStockProducts() { return mDatabase.productDao().getLowStockProducts(); }
    public LiveData<Integer> getProductCount() { return mDatabase.productDao().getProductCount(); }

    public void insertProduct(Product product, Runnable onDone) {
        mExecutors.diskIO().execute(() -> {
            long id = mDatabase.productDao().insert(product);
            product.setId(id);
            // Stock history IN
            mDatabase.stockHistoryDao().insert(new StockHistory(id, product.getName(), "IN", product.getStock(), "Stok Awal", System.currentTimeMillis()));
            if (onDone != null) mExecutors.mainThread().execute(onDone);
        });
    }

    public void updateProduct(Product product, Runnable onDone) {
        mExecutors.diskIO().execute(() -> {
            mDatabase.productDao().update(product);
            if (onDone != null) mExecutors.mainThread().execute(onDone);
        });
    }

    public void deleteProduct(Product product, Runnable onDone) {
        mExecutors.diskIO().execute(() -> {
            mDatabase.productDao().delete(product);
            if (onDone != null) mExecutors.mainThread().execute(onDone);
        });
    }

    public void updateStock(long productId, String productName, int addQty, String note, Runnable onDone) {
        mExecutors.diskIO().execute(() -> {
            mDatabase.productDao().updateStock(productId, addQty, System.currentTimeMillis());
            mDatabase.stockHistoryDao().insert(new StockHistory(productId, productName, addQty >= 0 ? "IN" : "OUT", Math.abs(addQty), note, System.currentTimeMillis()));
            if (onDone != null) mExecutors.mainThread().execute(onDone);
        });
    }

    // Categories
    public LiveData<List<Category>> getAllCategories() { return mDatabase.categoryDao().getAllCategories(); }
    public void insertCategory(Category category) {
        mExecutors.diskIO().execute(() -> mDatabase.categoryDao().insert(category));
    }

    // Transactions
    public LiveData<List<SaleTransaction>> getAllTransactions() { return mDatabase.saleDao().getAllTransactions(); }
    public LiveData<List<SaleTransaction>> getTransactionsBetween(long start, long end) { return mDatabase.saleDao().getTransactionsBetween(start, end); }
    public LiveData<Double> getTotalSalesBetween(long start, long end) { return mDatabase.saleDao().getTotalSalesBetween(start, end); }
    public LiveData<Integer> getTransactionCountBetween(long start, long end) { return mDatabase.saleDao().getTransactionCountBetween(start, end); }
    public LiveData<List<SaleItem>> getSaleItemsForTransaction(long txId) { return mDatabase.saleDao().getSaleItemsForTransaction(txId); }

    public void processSaleTransaction(SaleTransaction transaction, List<SaleItem> items, Debt debt, Runnable onSuccess) {
        mExecutors.diskIO().execute(() -> {
            // 1. Insert Transaction
            long txId = mDatabase.saleDao().insertTransaction(transaction);
            transaction.setId(txId);

            // 2. Set txId to sale items and insert
            for (SaleItem item : items) {
                item.setTransactionId(txId);
                // Reduce stock
                mDatabase.productDao().updateStock(item.getProductId(), -item.getQuantity(), transaction.getTimestamp());
                // Stock history OUT
                mDatabase.stockHistoryDao().insert(new StockHistory(item.getProductId(), item.getProductName(), "OUT", item.getQuantity(), "Penjualan #" + transaction.getTransactionNo(), transaction.getTimestamp()));
            }
            mDatabase.saleDao().insertSaleItems(items);

            // 3. Handle Kasbon if applicable
            if (debt != null && transaction.getCustomerId() > 0) {
                debt.setTransactionId(txId);
                mDatabase.debtDao().insertDebt(debt);
                mDatabase.customerDao().updateCustomerDebt(transaction.getCustomerId(), debt.getRemainingAmount());
            }

            if (onSuccess != null) {
                mExecutors.mainThread().execute(onSuccess);
            }
        });
    }

    // Customers & Kasbon
    public LiveData<List<Customer>> getAllCustomers() { return mDatabase.customerDao().getAllCustomers(); }
    public LiveData<List<Debt>> getAllUnpaidDebts() { return mDatabase.debtDao().getAllUnpaidDebts(); }
    public LiveData<Double> getTotalUnpaidDebtAmount() { return mDatabase.debtDao().getTotalUnpaidDebtAmount(); }
    public LiveData<List<Debt>> getDebtsForCustomer(long customerId) { return mDatabase.debtDao().getDebtsForCustomer(customerId); }
    public LiveData<List<DebtPayment>> getPaymentsForDebt(long debtId) { return mDatabase.debtDao().getPaymentsForDebt(debtId); }

    public void insertCustomer(Customer customer, Runnable onDone) {
        mExecutors.diskIO().execute(() -> {
            long id = mDatabase.customerDao().insert(customer);
            customer.setId(id);
            if (onDone != null) mExecutors.mainThread().execute(onDone);
        });
    }

    public void payDebt(long debtId, double payAmount, String note, Runnable onDone) {
        mExecutors.diskIO().execute(() -> {
            Debt debt = mDatabase.debtDao().getDebtByIdSync(debtId);
            if (debt != null) {
                double newRemaining = Math.max(0, debt.getRemainingAmount() - payAmount);
                debt.setRemainingAmount(newRemaining);
                if (newRemaining == 0) {
                    debt.setStatus("LUNAS");
                }
                mDatabase.debtDao().updateDebt(debt);
                mDatabase.debtDao().insertPayment(new DebtPayment(debtId, payAmount, System.currentTimeMillis(), note));
                mDatabase.customerDao().updateCustomerDebt(debt.getCustomerId(), -payAmount);
            }
            if (onDone != null) mExecutors.mainThread().execute(onDone);
        });
    }

    // Expenses
    public LiveData<List<Expense>> getAllExpenses() { return mDatabase.expenseDao().getAllExpenses(); }
    public LiveData<List<Expense>> getExpensesBetween(long start, long end) { return mDatabase.expenseDao().getExpensesBetween(start, end); }
    public LiveData<Double> getTotalExpensesBetween(long start, long end) { return mDatabase.expenseDao().getTotalExpensesBetween(start, end); }
    public void insertExpense(Expense expense, Runnable onDone) {
        mExecutors.diskIO().execute(() -> {
            mDatabase.expenseDao().insert(expense);
            if (onDone != null) mExecutors.mainThread().execute(onDone);
        });
    }

    // Stock History
    public LiveData<List<StockHistory>> getRecentStockHistory() { return mDatabase.stockHistoryDao().getRecentHistory(); }

    // Shop Profile
    public LiveData<ShopProfile> getShopProfile() { return mDatabase.shopProfileDao().getShopProfile(); }
    public void updateShopProfile(ShopProfile profile, Runnable onDone) {
        mExecutors.diskIO().execute(() -> {
            mDatabase.shopProfileDao().insertOrUpdate(profile);
            if (onDone != null) mExecutors.mainThread().execute(onDone);
        });
    }

    public AppDatabase getDatabase() {
        return mDatabase;
    }
}
