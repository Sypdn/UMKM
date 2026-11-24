package com.syaepuddin.umkm.ui.expense;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.syaepuddin.umkm.data.entity.Expense;
import com.syaepuddin.umkm.databinding.ItemExpenseBinding;
import com.syaepuddin.umkm.util.CurrencyFormatter;
import com.syaepuddin.umkm.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private final List<Expense> expenseList = new ArrayList<>();

    public void setExpenses(List<Expense> expenses) {
        this.expenseList.clear();
        if (expenses != null) {
            this.expenseList.addAll(expenses);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExpenseBinding binding = ItemExpenseBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense e = expenseList.get(position);
        holder.binding.tvCategory.setText(e.getCategory());
        String noteStr = (e.getNote() != null && !e.getNote().isEmpty() ? e.getNote() + " • " : "") + DateUtil.formatDate(e.getDate());
        holder.binding.tvNoteAndDate.setText(noteStr);
        holder.binding.tvAmount.setText(CurrencyFormatter.formatRupiah(e.getAmount()));
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemExpenseBinding binding;
        ViewHolder(ItemExpenseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
