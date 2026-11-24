package com.syaepuddin.umkm.ui.settings;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.syaepuddin.umkm.data.entity.ShopProfile;
import com.syaepuddin.umkm.databinding.ActivityPinLockBinding;
import com.syaepuddin.umkm.util.AnimationHelper;
import com.syaepuddin.umkm.util.HapticUtil;

public class PinLockActivity extends AppCompatActivity {

    private ActivityPinLockBinding binding;
    private SettingsViewModel viewModel;
    private ShopProfile shopProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPinLockBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        viewModel.getShopProfile().observe(this, profile -> shopProfile = profile);

        binding.btnSubmitPin.setOnClickListener(v -> {
            String pin = binding.etPin.getText() != null ? binding.etPin.getText().toString().trim() : "";
            if (pin.length() < 4) {
                Toast.makeText(this, "PIN minimal 4 digit!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (shopProfile != null) {
                shopProfile.setPinCode(pin);
                shopProfile.setPinEnabled(true);
                AnimationHelper.animateSpringClick(binding.btnSubmitPin, () -> {
                    viewModel.updateShopProfile(shopProfile, () -> {
                        HapticUtil.performSuccessFeedback(this);
                        Toast.makeText(this, "PIN Keamanan berhasil disimpan!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                });
            }
        });
    }
}
