package com.syaepuddin.umkm.ui.debt;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.syaepuddin.umkm.R;
import com.syaepuddin.umkm.data.entity.Customer;
import com.syaepuddin.umkm.databinding.ItemCustomerDebtBinding;
import com.syaepuddin.umkm.util.AnimationHelper;
import com.syaepuddin.umkm.util.CurrencyFormatter;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    public interface OnCustomerClickListener {
        void onCustomerClick(Customer customer);
    }

    private final List<Customer> customerList = new ArrayList<>();
    private final OnCustomerClickListener listener;

    public CustomerAdapter(OnCustomerClickListener listener) {
        this.listener = listener;
    }

    public void setCustomers(List<Customer> customers) {
        this.customerList.clear();
        if (customers != null) {
            this.customerList.addAll(customers);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCustomerDebtBinding binding = ItemCustomerDebtBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Customer c = customerList.get(position);
        holder.binding.tvCustomerName.setText(c.getName());
        holder.binding.tvCustomerPhone.setText(c.getPhone() != null && !c.getPhone().isEmpty() ? c.getPhone() : "-");
        holder.binding.tvTotalDebt.setText(CurrencyFormatter.formatRupiah(c.getTotalDebt()));

        if (c.getTotalDebt() > 0) {
            holder.binding.tvTotalDebt.setBackgroundResource(R.drawable.bg_glass_card_orange);
            holder.binding.tvTotalDebt.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_danger));
        } else {
            holder.binding.tvTotalDebt.setBackgroundResource(R.drawable.bg_glass_card_emerald);
            holder.binding.tvTotalDebt.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.emerald_secondary_dark));
            holder.binding.tvTotalDebt.setText("Lunas");
        }

        holder.itemView.setOnClickListener(v -> {
            AnimationHelper.animateSpringClick(v, () -> {
                if (listener != null) listener.onCustomerClick(c);
            });
        });
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemCustomerDebtBinding binding;
        ViewHolder(ItemCustomerDebtBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
