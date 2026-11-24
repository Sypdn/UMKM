package com.syaepuddin.umkm.ui.cashier;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.syaepuddin.umkm.data.entity.Product;
import com.syaepuddin.umkm.data.model.CartItem;
import com.syaepuddin.umkm.databinding.ItemProductGridBinding;
import com.syaepuddin.umkm.util.AnimationHelper;
import com.syaepuddin.umkm.util.CurrencyFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.ViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product, View view);
    }

    private final List<Product> productList = new ArrayList<>();
    private final Map<Long, Integer> cartQuantityMap = new HashMap<>();
    private final OnProductClickListener listener;

    public ProductGridAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.productList.clear();
        if (products != null) {
            this.productList.addAll(products);
        }
        notifyDataSetChanged();
    }

    public void setCartItems(List<CartItem> items) {
        cartQuantityMap.clear();
        if (items != null) {
            for (CartItem item : items) {
                cartQuantityMap.put(item.getProduct().getId(), item.getQuantity());
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductGridBinding binding = ItemProductGridBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product p = productList.get(position);
        holder.binding.tvProductName.setText(p.getName());
        holder.binding.tvProductPrice.setText(CurrencyFormatter.formatRupiah(p.getSellPrice()));
        holder.binding.tvStock.setText("Stok: " + p.getStock() + " " + p.getUnit());

        if (p.getImagePath() != null && !p.getImagePath().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(p.getImagePath())
                    .centerCrop()
                    .into(holder.binding.ivProduct);
        }

        Integer qtyInCart = cartQuantityMap.get(p.getId());
        if (qtyInCart != null && qtyInCart > 0) {
            holder.binding.tvBadgeQuantity.setVisibility(View.VISIBLE);
            holder.binding.tvBadgeQuantity.setText(String.valueOf(qtyInCart));
        } else {
            holder.binding.tvBadgeQuantity.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            AnimationHelper.animateSpringClick(v, () -> {
                if (listener != null) {
                    listener.onProductClick(p, holder.binding.tvBadgeQuantity);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemProductGridBinding binding;
        ViewHolder(ItemProductGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
