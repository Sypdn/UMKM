package com.syaepuddin.umkm.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syaepuddin.umkm.data.entity.Product;
import com.syaepuddin.umkm.databinding.ItemHomeProductBinding;
import com.syaepuddin.umkm.util.CurrencyFormatter;

import java.util.ArrayList;
import java.util.List;

public class HomeProductAdapter extends RecyclerView.Adapter<HomeProductAdapter.ViewHolder> {

    private final List<Product> productList = new ArrayList<>();

    public void setProducts(List<Product> products) {
        this.productList.clear();
        if (products != null) {
            this.productList.addAll(products);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHomeProductBinding binding = ItemHomeProductBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product p = productList.get(position);
        holder.binding.tvProductName.setText(p.getName());
        holder.binding.tvProductCategory.setText(p.getCategoryName() != null ? p.getCategoryName() : "Umum");
        holder.binding.tvProductPrice.setText(CurrencyFormatter.formatRupiah(p.getSellPrice()));
        holder.binding.tvStockBadge.setText("Stok: " + p.getStock() + " " + (p.getUnit() != null ? p.getUnit() : ""));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemHomeProductBinding binding;
        ViewHolder(ItemHomeProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
