package com.syaepuddin.umkm.ui.cashier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.chip.Chip;
import com.syaepuddin.umkm.R;
import com.syaepuddin.umkm.data.entity.Category;
import com.syaepuddin.umkm.data.entity.Product;
import com.syaepuddin.umkm.databinding.FragmentCashierBinding;
import com.syaepuddin.umkm.util.AnimationHelper;
import com.syaepuddin.umkm.util.CurrencyFormatter;
import com.syaepuddin.umkm.util.HapticUtil;

import java.util.ArrayList;
import java.util.List;

public class CashierFragment extends Fragment {

    private FragmentCashierBinding binding;
    private CashierViewModel viewModel;
    private ProductGridAdapter gridAdapter;

    private List<Product> allProducts = new ArrayList<>();
    private String currentQuery = "";
    private String selectedCategory = "Semua";

    private final ActivityResultLauncher<Intent> barcodeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    String barcode = result.getData().getStringExtra("SCANNED_BARCODE");
                    if (barcode != null) {
                        binding.etSearchProduct.setText(barcode);
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCashierBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(CashierViewModel.class);

        setupRecyclerView();
        observeViewModel();

        binding.btnScanBarcode.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ScanBarcodeActivity.class);
            barcodeLauncher.launch(intent);
        });

        binding.etSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s != null ? s.toString().trim().toLowerCase() : "";
                filterProducts();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.btnOpenCart.setOnClickListener(v -> openCartSheet());
        binding.cardCartSummary.setOnClickListener(v -> openCartSheet());
    }

    private void setupRecyclerView() {
        gridAdapter = new ProductGridAdapter((product, badgeView) -> {
            HapticUtil.performClickFeedback(badgeView);
            viewModel.addToCart(product);
            AnimationHelper.bounceBadge(binding.tvCartBadge);
        });
        binding.rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvProducts.setAdapter(gridAdapter);
    }

    private void observeViewModel() {
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                allProducts = products;
                filterProducts();
            }
        });

        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                populateCategoryChips(categories);
            }
        });

        viewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            gridAdapter.setCartItems(items);
        });

        viewModel.getTotalItemCount().observe(getViewLifecycleOwner(), count -> {
            int itemCount = count != null ? count : 0;
            binding.tvCartBadge.setText(String.valueOf(itemCount));
            binding.tvCartTotalItems.setText(itemCount + " Produk");

            if (itemCount > 0) {
                if (binding.cardCartSummary.getVisibility() != View.VISIBLE) {
                    AnimationHelper.fadeIn(binding.cardCartSummary, 250);
                }
            } else {
                if (binding.cardCartSummary.getVisibility() == View.VISIBLE) {
                    AnimationHelper.fadeOut(binding.cardCartSummary, 250);
                }
            }
        });

        viewModel.getTotalAmount().observe(getViewLifecycleOwner(), total -> {
            double sum = total != null ? total : 0.0;
            binding.tvCartTotalAmount.setText(CurrencyFormatter.formatRupiah(sum));
        });
    }

    private void populateCategoryChips(List<Category> categories) {
        binding.chipGroupCategories.removeAllViews();

        Chip allChip = new Chip(requireContext());
        allChip.setText(getString(R.string.all_categories));
        allChip.setCheckable(true);
        allChip.setChecked(true);
        allChip.setOnClickListener(v -> {
            selectedCategory = "Semua";
            filterProducts();
        });
        binding.chipGroupCategories.addView(allChip);

        for (Category cat : categories) {
            Chip chip = new Chip(requireContext());
            chip.setText(cat.getName());
            chip.setCheckable(true);
            chip.setOnClickListener(v -> {
                selectedCategory = cat.getName();
                filterProducts();
            });
            binding.chipGroupCategories.addView(chip);
        }
    }

    private void filterProducts() {
        List<Product> filtered = new ArrayList<>();
        for (Product p : allProducts) {
            boolean matchesQuery = currentQuery.isEmpty() ||
                    (p.getName() != null && p.getName().toLowerCase().contains(currentQuery)) ||
                    (p.getBarcode() != null && p.getBarcode().toLowerCase().contains(currentQuery));

            boolean matchesCategory = "Semua".equalsIgnoreCase(selectedCategory) ||
                    (p.getCategoryName() != null && p.getCategoryName().equalsIgnoreCase(selectedCategory));

            if (matchesQuery && matchesCategory) {
                filtered.add(p);
            }
        }

        gridAdapter.setProducts(filtered);

        if (filtered.isEmpty()) {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.rvProducts.setVisibility(View.GONE);
        } else {
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.rvProducts.setVisibility(View.VISIBLE);
        }
    }

    private void openCartSheet() {
        CartBottomSheetDialog dialog = new CartBottomSheetDialog();
        dialog.show(getChildFragmentManager(), "CART_SHEET");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
