package com.syaepuddin.umkm.ui.product;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.syaepuddin.umkm.data.entity.Product;
import com.syaepuddin.umkm.databinding.DialogRestockBinding;
import com.syaepuddin.umkm.util.AnimationHelper;

public class RestockDialog extends Dialog {

    public interface OnRestockListener {
        void onRestockSubmitted(int addQty, String note);
    }

    private DialogRestockBinding binding;
    private final Product product;
    private final OnRestockListener listener;

    public RestockDialog(@NonNull Context context, Product product, OnRestockListener listener) {
        super(context);
        this.product = product;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DialogRestockBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (product != null) {
            binding.tvProductName.setText(product.getName());
        }

        binding.btnCancel.setOnClickListener(v -> dismiss());

        binding.btnSave.setOnClickListener(v -> {
            String qtyStr = binding.etQuantity.getText() != null ? binding.etQuantity.getText().toString().trim() : "";
            if (qtyStr.isEmpty()) {
                Toast.makeText(getContext(), "Masukkan jumlah stok!", Toast.LENGTH_SHORT).show();
                return;
            }

            int qty = 0;
            try {
                qty = Integer.parseInt(qtyStr);
            } catch (NumberFormatException ignored) {}

            if (qty <= 0) {
                Toast.makeText(getContext(), "Jumlah stok harus lebih dari 0!", Toast.LENGTH_SHORT).show();
                return;
            }

            String note = binding.etNote.getText() != null ? binding.etNote.getText().toString().trim() : "Tambah Stok";

            final int finalQty = qty;
            AnimationHelper.animateSpringClick(binding.btnSave, () -> {
                if (listener != null) {
                    listener.onRestockSubmitted(finalQty, note);
                }
                dismiss();
            });
        });
    }
}
