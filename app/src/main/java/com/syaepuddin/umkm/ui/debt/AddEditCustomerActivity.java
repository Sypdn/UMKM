package com.syaepuddin.umkm.ui.debt;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.syaepuddin.umkm.data.entity.Customer;
import com.syaepuddin.umkm.databinding.ActivityAddEditCustomerBinding;
import com.syaepuddin.umkm.util.AnimationHelper;
import com.syaepuddin.umkm.util.HapticUtil;

public class AddEditCustomerActivity extends AppCompatActivity {

    private ActivityAddEditCustomerBinding binding;
    private DebtViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(DebtViewModel.class);

        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etName.getText() != null ? binding.etName.getText().toString().trim() : "";
            String phone = binding.etPhone.getText() != null ? binding.etPhone.getText().toString().trim() : "";
            String address = binding.etAddress.getText() != null ? binding.etAddress.getText().toString().trim() : "";

            if (name.isEmpty()) {
                binding.etName.setError("Nama pelanggan wajib diisi");
                return;
            }

            Customer newCustomer = new Customer(name, phone, address);
            AnimationHelper.animateSpringClick(binding.btnSave, () -> {
                viewModel.insertCustomer(newCustomer, () -> {
                    HapticUtil.performSuccessFeedback(this);
                    Toast.makeText(this, "Pelanggan berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        });
    }
}
