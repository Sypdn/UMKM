package com.syaepuddin.umkm.ui.product;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.syaepuddin.umkm.R;
import com.syaepuddin.umkm.data.entity.Product;
import com.syaepuddin.umkm.databinding.ItemProductListBinding;
import com.syaepuddin.umkm.util.AnimationHelper;
import com.syaepuddin.umkm.util.CurrencyFormatter;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    public interface OnProductItemClickListener {
        void onProductClick(Product product);
        void onRestockClick(Product product);
        void onDeleteClick(Product product);
    }

    private final List<Product> productList = new ArrayList<>();
    private final OnProductItemClickListener listener;

    public ProductAdapter(OnProductItemClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.productList.clear();
        if (products != null) {
            this.productList.addAll(products);
        }
        notifyDataSetChanged();
    }

    public Product getProductAt(int position) {
        if (position >= 0 && position < productList.size()) {
            return productList.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductListBinding binding = ItemProductListBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product p = productList.get(position);
        holder.binding.tvProductName.setText(p.getName());
        holder.binding.tvCategoryName.setText(p.getCategoryName() != null ? p.getCategoryName() : "Umum");
        holder.binding.tvSellPrice.setText(CurrencyFormatter.formatRupiah(p.getSellPrice()));
        holder.binding.tvBuyPrice.setText("Modal: " + CurrencyFormatter.formatRupiah(p.getBuyPrice()));
        holder.binding.tvStockStatus.setText("Stok: " + p.getStock() + " " + p.getUnit());

        if (p.getStock() <= p.getMinStock()) {
            holder.binding.tvStockStatus.setBackgroundResource(R.drawable.bg_glass_card_orange);
            holder.binding.tvStockStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_danger));
        } else {
            holder.binding.tvStockStatus.setBackgroundResource(R.drawable.bg_glass_card_emerald);
            holder.binding.tvStockStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.emerald_secondary_dark));
        }

        if (p.getImagePath() != null && !p.getImagePath().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(p.getImagePath())
                    .centerCrop()
                    .into(holder.binding.ivProductIcon);
        }

        holder.itemView.setOnClickListener(v -> {
            AnimationHelper.animateSpringClick(v, () -> {
                if (listener != null) listener.onProductClick(p);
            });
        });

        holder.binding.btnRestock.setOnClickListener(v -> {
            AnimationHelper.animateSpringClick(v, () -> {
                if (listener != null) listener.onRestockClick(p);
            });
        });

        holder.binding.btnDelete.setOnClickListener(v -> {
            AnimationHelper.animateSpringClick(v, () -> {
                if (listener != null) listener.onDeleteClick(p);
            });
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemProductListBinding binding;
        ViewHolder(ItemProductListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
