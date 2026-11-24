package com.syaepuddin.umkm.ui.main;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationBarView;
import com.syaepuddin.umkm.R;
import com.syaepuddin.umkm.databinding.ActivityMainBinding;
import com.syaepuddin.umkm.ui.cashier.CashierFragment;
import com.syaepuddin.umkm.ui.debt.DebtFragment;
import com.syaepuddin.umkm.ui.home.HomeFragment;
import com.syaepuddin.umkm.ui.product.ProductListFragment;
import com.syaepuddin.umkm.ui.report.ReportFragment;
import com.syaepuddin.umkm.ui.settings.SettingsFragment;
import com.syaepuddin.umkm.util.HapticUtil;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private ActivityMainBinding binding;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigation.setOnItemSelectedListener(this);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), "HOME");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        HapticUtil.performClickFeedback(binding.bottomNavigation);
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_home) {
            loadFragment(new HomeFragment(), "HOME");
            return true;
        } else if (itemId == R.id.navigation_cashier) {
            loadFragment(new CashierFragment(), "CASHIER");
            return true;
        } else if (itemId == R.id.navigation_products) {
            loadFragment(new ProductListFragment(), "PRODUCTS");
            return true;
        } else if (itemId == R.id.navigation_debt) {
            loadFragment(new DebtFragment(), "DEBT");
            return true;
        } else if (itemId == R.id.navigation_report) {
            loadFragment(new ReportFragment(), "REPORT");
            return true;
        }
        return false;
    }

    public void navigateToFragment(int navId) {
        binding.bottomNavigation.setSelectedItemId(navId);
    }

    public void openSettings() {
        loadFragment(new SettingsFragment(), "SETTINGS");
    }

    private void loadFragment(Fragment fragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragmentContainer, fragment, tag)
                .commit();
        activeFragment = fragment;
    }
}
