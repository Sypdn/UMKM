package com.syaepuddin.umkm.ui.expense;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.syaepuddin.umkm.data.entity.Expense;
import com.syaepuddin.umkm.databinding.DialogAddExpenseBinding;
import com.syaepuddin.umkm.util.AnimationHelper;

public class AddExpenseDialog extends Dialog {

    public interface OnExpenseSubmittedListener {
        void onExpenseSubmitted(Expense expense);
    }

    private DialogAddExpenseBinding binding;
    private final OnExpenseSubmittedListener listener;

    private static final String[] EXPENSE_CATEGORIES = {
            "Belanja Stok", "Listrik & Air", "Parkir & Retribusi", "Gaji Karyawan", "Kebersihan", "Lain-lain"
    };

    public AddExpenseDialog(@NonNull Context context, OnExpenseSubmittedListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DialogAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, EXPENSE_CATEGORIES);
        binding.spinnerCategory.setAdapter(adapter);
        binding.spinnerCategory.setText(EXPENSE_CATEGORIES[0], false);

        binding.btnCancel.setOnClickListener(v -> dismiss());

        binding.btnSave.setOnClickListener(v -> {
            String category = binding.spinnerCategory.getText().toString();
            String amountStr = binding.etAmount.getText() != null ? binding.etAmount.getText().toString().trim() : "";
            String note = binding.etNote.getText() != null ? binding.etNote.getText().toString().trim() : "";

            if (amountStr.isEmpty()) {
                Toast.makeText(getContext(), "Masukkan nominal pengeluaran!", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = 0;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException ignored) {}

            if (amount <= 0) {
                Toast.makeText(getContext(), "Nominal harus lebih dari 0!", Toast.LENGTH_SHORT).show();
                return;
            }

            Expense expense = new Expense(category, amount, note, System.currentTimeMillis());
            AnimationHelper.animateSpringClick(binding.btnSave, () -> {
                if (listener != null) {
                    listener.onExpenseSubmitted(expense);
                }
                dismiss();
            });
        });
    }
}
