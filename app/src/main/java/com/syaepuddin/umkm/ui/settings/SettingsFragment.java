package com.syaepuddin.umkm.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.syaepuddin.umkm.databinding.FragmentSettingsBinding;
import com.syaepuddin.umkm.util.AnimationHelper;
import com.syaepuddin.umkm.util.BackupRestoreUtil;
import com.syaepuddin.umkm.util.HapticUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;

    private final ActivityResultLauncher<String> restoreLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    restoreDatabaseFromUri(uri);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        observeViewModel();

        binding.cardShopProfile.setOnClickListener(v -> {
            AnimationHelper.animateSpringClick(v, () -> {
                Intent intent = new Intent(getContext(), EditShopProfileActivity.class);
                startActivity(intent);
            });
        });

        binding.cardSecurity.setOnClickListener(v -> {
            AnimationHelper.animateSpringClick(v, () -> {
                Intent intent = new Intent(getContext(), PinLockActivity.class);
                startActivity(intent);
            });
        });

        binding.btnBackup.setOnClickListener(v -> {
            AnimationHelper.animateSpringClick(v, () -> {
                File backupDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                boolean success = BackupRestoreUtil.backupDatabase(requireContext(), backupDir);
                if (success && backupDir != null) {
                    HapticUtil.performSuccessFeedback(getContext());
                    Toast.makeText(getContext(), "Backup database berhasil dibuat!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Gagal membuat backup database", Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.btnRestore.setOnClickListener(v -> {
            AnimationHelper.animateSpringClick(v, () -> restoreLauncher.launch("*/*"));
        });
    }

    private void restoreDatabaseFromUri(Uri uri) {
        try {
            File tempFile = new File(requireContext().getCacheDir(), "temp_restore.db");
            InputStream is = requireContext().getContentResolver().openInputStream(uri);
            FileOutputStream fos = new FileOutputStream(tempFile);
            if (is != null) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                fos.flush();
                fos.close();
                is.close();
            }

            boolean success = BackupRestoreUtil.restoreDatabase(requireContext(), tempFile);
            if (success) {
                HapticUtil.performSuccessFeedback(getContext());
                Toast.makeText(getContext(), "Database berhasil dipulihkan!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Gagal memulihkan database", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Format file tidak valid", Toast.LENGTH_SHORT).show();
        }
    }

    private void observeViewModel() {
        viewModel.getShopProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                if (profile.getShopName() != null) binding.tvShopName.setText(profile.getShopName());
                if (profile.getOwnerName() != null) binding.tvOwnerName.setText(profile.getOwnerName());

                if (profile.isPinEnabled()) {
                    binding.tvPinStatus.setText("PIN Aktif ✓");
                } else {
                    binding.tvPinStatus.setText("PIN Belum Aktif");
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
