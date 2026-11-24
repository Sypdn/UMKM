package com.syaepuddin.umkm.ui.cashier;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.syaepuddin.umkm.data.entity.Category;
import com.syaepuddin.umkm.data.entity.Customer;
import com.syaepuddin.umkm.data.entity.Debt;
import com.syaepuddin.umkm.data.entity.Product;
import com.syaepuddin.umkm.data.entity.SaleItem;
import com.syaepuddin.umkm.data.entity.SaleTransaction;
import com.syaepuddin.umkm.data.model.CartItem;
import com.syaepuddin.umkm.data.repository.AppRepository;

import java.util.ArrayList;
import java.util.List;

public class CashierViewModel extends AndroidViewModel {
    private final AppRepository mRepository;

    private final LiveData<List<Product>> products;
    private final LiveData<List<Category>> categories;
    private final LiveData<List<Customer>> customers;

    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Double> totalAmount = new MutableLiveData<>(0.0);
    private final MutableLiveData<Integer> totalItemCount = new MutableLiveData<>(0);

    public CashierViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(application);
        products = mRepository.getAllProducts();
        categories = mRepository.getAllCategories();
        customers = mRepository.getAllCustomers();
    }

    public LiveData<List<Product>> getProducts() { return products; }
    public LiveData<List<Category>> getCategories() { return categories; }
    public LiveData<List<Customer>> getCustomers() { return customers; }

    public LiveData<List<CartItem>> getCartItems() { return cartItems; }
    public LiveData<Double> getTotalAmount() { return totalAmount; }
    public LiveData<Integer> getTotalItemCount() { return totalItemCount; }

    public void addToCart(Product product) {
        List<CartItem> current = cartItems.getValue();
        if (current == null) current = new ArrayList<>();

        boolean found = false;
        for (CartItem item : current) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            current.add(new CartItem(product, 1));
        }

        cartItems.setValue(current);
        recalculateTotals();
    }

    public void removeFromCart(Product product) {
        List<CartItem> current = cartItems.getValue();
        if (current != null) {
            current.removeIf(item -> item.getProduct().getId() == product.getId());
            cartItems.setValue(current);
            recalculateTotals();
        }
    }

    public void updateQuantity(Product product, int newQty) {
        List<CartItem> current = cartItems.getValue();
        if (current != null) {
            if (newQty <= 0) {
                current.removeIf(item -> item.getProduct().getId() == product.getId());
            } else {
                for (CartItem item : current) {
                    if (item.getProduct().getId() == product.getId()) {
                        item.setQuantity(newQty);
                        break;
                    }
                }
            }
            cartItems.setValue(current);
            recalculateTotals();
        }
    }

    public void clearCart() {
        cartItems.setValue(new ArrayList<>());
        recalculateTotals();
    }

    private void recalculateTotals() {
        List<CartItem> current = cartItems.getValue();
        double sum = 0.0;
        int count = 0;
        if (current != null) {
            for (CartItem item : current) {
                sum += item.getSubtotal();
                count += item.getQuantity();
            }
        }
        totalAmount.setValue(sum);
        totalItemCount.setValue(count);
    }

    public void processCheckout(String paymentMethod, double paidAmount, double discount, Customer selectedCustomer, Runnable onSuccess) {
        List<CartItem> items = cartItems.getValue();
        if (items == null || items.isEmpty()) return;

        double subtotal = totalAmount.getValue() != null ? totalAmount.getValue() : 0.0;
        double finalAmt = Math.max(0, subtotal - discount);
        double change = Math.max(0, paidAmount - finalAmt);

        String txNo = "TX" + System.currentTimeMillis();
        long now = System.currentTimeMillis();

        long customerId = selectedCustomer != null ? selectedCustomer.getId() : 0;
        String customerName = selectedCustomer != null ? selectedCustomer.getName() : "";
        String status = "TUNAI".equalsIgnoreCase(paymentMethod) || paidAmount >= finalAmt ? "LUNAS" : "KASBON";

        SaleTransaction tx = new SaleTransaction(
                txNo, now, subtotal, discount, finalAmt, paymentMethod, paidAmount, change, customerId, customerName, status, ""
        );

        List<SaleItem> saleItems = new ArrayList<>();
        for (CartItem ci : items) {
            saleItems.add(new SaleItem(
                    0, ci.getProduct().getId(), ci.getProduct().getName(), ci.getProduct().getUnit(),
                    ci.getProduct().getBuyPrice(), ci.getProduct().getSellPrice(), ci.getQuantity(), ci.getSubtotal()
            ));
        }

        Debt debt = null;
        if ("KASBON".equalsIgnoreCase(paymentMethod) || paidAmount < finalAmt) {
            double remaining = finalAmt - paidAmount;
            debt = new Debt(customerId, 0, finalAmt, remaining, "BELUM_LUNAS", now + (14L * 24 * 3600 * 1000));
        }

        mRepository.processSaleTransaction(tx, saleItems, debt, () -> {
            clearCart();
            if (onSuccess != null) {
                onSuccess.run();
            }
        });
    }
}
