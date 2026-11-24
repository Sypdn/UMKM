package com.syaepuddin.umkm.ui.cashier;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syaepuddin.umkm.data.entity.Product;
import com.syaepuddin.umkm.data.model.CartItem;
import com.syaepuddin.umkm.databinding.ItemCartProductBinding;
import com.syaepuddin.umkm.util.AnimationHelper;
import com.syaepuddin.umkm.util.CurrencyFormatter;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    public interface OnCartQuantityChangeListener {
        void onQuantityChanged(Product product, int newQty);
    }

    private final List<CartItem> cartItems = new ArrayList<>();
    private final OnCartQuantityChangeListener listener;

    public CartAdapter(OnCartQuantityChangeListener listener) {
        this.listener = listener;
    }

    public void setItems(List<CartItem> items) {
        cartItems.clear();
        if (items != null) {
            cartItems.addAll(items);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartProductBinding binding = ItemCartProductBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.binding.tvProductName.setText(item.getProduct().getName());
        holder.binding.tvProductPrice.setText(CurrencyFormatter.formatRupiah(item.getProduct().getSellPrice()));
        holder.binding.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.binding.tvSubtotal.setText(CurrencyFormatter.formatRupiah(item.getSubtotal()));

        holder.binding.btnMinus.setOnClickListener(v -> {
            AnimationHelper.animateSpringClick(v, () -> {
                if (listener != null) {
                    listener.onQuantityChanged(item.getProduct(), item.getQuantity() - 1);
                }
            });
        });

        holder.binding.btnPlus.setOnClickListener(v -> {
            AnimationHelper.animateSpringClick(v, () -> {
                if (listener != null) {
                    listener.onQuantityChanged(item.getProduct(), item.getQuantity() + 1);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemCartProductBinding binding;
        ViewHolder(ItemCartProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
