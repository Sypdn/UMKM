package com.syaepuddin.umkm.ui.settings;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.syaepuddin.umkm.data.entity.ShopProfile;
import com.syaepuddin.umkm.databinding.ActivityEditShopProfileBinding;
import com.syaepuddin.umkm.util.AnimationHelper;
import com.syaepuddin.umkm.util.HapticUtil;

public class EditShopProfileActivity extends AppCompatActivity {

    private ActivityEditShopProfileBinding binding;
    private SettingsViewModel viewModel;
    private ShopProfile currentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditShopProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        viewModel.getShopProfile().observe(this, profile -> {
            if (profile != null) {
                currentProfile = profile;
                binding.etShopName.setText(profile.getShopName());
                binding.etOwnerName.setText(profile.getOwnerName());
                binding.etAddress.setText(profile.getAddress());
                binding.etPhone.setText(profile.getPhone());
                binding.etReceiptFooter.setText(profile.getReceiptFooter());
            }
        });

        binding.btnSave.setOnClickListener(v -> {
            String shopName = binding.etShopName.getText() != null ? binding.etShopName.getText().toString().trim() : "";
            String ownerName = binding.etOwnerName.getText() != null ? binding.etOwnerName.getText().toString().trim() : "";
            String address = binding.etAddress.getText() != null ? binding.etAddress.getText().toString().trim() : "";
            String phone = binding.etPhone.getText() != null ? binding.etPhone.getText().toString().trim() : "";
            String footer = binding.etReceiptFooter.getText() != null ? binding.etReceiptFooter.getText().toString().trim() : "";

            if (shopName.isEmpty()) {
                binding.etShopName.setError("Nama toko wajib diisi");
                return;
            }

            if (currentProfile == null) {
                currentProfile = new ShopProfile(shopName, ownerName, address, phone, footer);
            } else {
                currentProfile.setShopName(shopName);
                currentProfile.setOwnerName(ownerName);
                currentProfile.setAddress(address);
                currentProfile.setPhone(phone);
                currentProfile.setReceiptFooter(footer);
            }

            AnimationHelper.animateSpringClick(binding.btnSave, () -> {
                viewModel.updateShopProfile(currentProfile, () -> {
                    HapticUtil.performSuccessFeedback(this);
                    Toast.makeText(this, "Profil toko berhasil disimpan", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        });
    }
}
