package com.syaepuddin.umkm.ui.product;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.syaepuddin.umkm.R;
import com.syaepuddin.umkm.data.entity.Category;
import com.syaepuddin.umkm.data.entity.Product;
import com.syaepuddin.umkm.databinding.ActivityAddEditProductBinding;
import com.syaepuddin.umkm.ui.cashier.ScanBarcodeActivity;
import com.syaepuddin.umkm.util.AnimationHelper;
import com.syaepuddin.umkm.util.HapticUtil;

import java.util.ArrayList;
import java.util.List;

public class AddEditProductActivity extends AppCompatActivity {

    private ActivityAddEditProductBinding binding;
    private ProductViewModel viewModel;

    private List<Category> categoryList = new ArrayList<>();
    private Category selectedCategory = null;
    private long existingProductId = -1;
    private Product existingProduct = null;

    private final String[] UNITS = {"pcs", "bungkus", "sachet", "gelas", "kg", "liter", "pack", "karung", "lusin"};

    private final ActivityResultLauncher<Intent> barcodeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    String barcode = result.getData().getStringExtra("SCANNED_BARCODE");
                    if (barcode != null) {
                        binding.etBarcode.setText(barcode);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        existingProductId = getIntent().getLongExtra("PRODUCT_ID", -1);

        setupUnitSpinner();
        observeCategories();

        if (existingProductId > 0) {
            binding.tvTitle.setText(R.string.edit_product);
            binding.btnDeleteProduct.setVisibility(android.view.View.VISIBLE);
            binding.btnDeleteProduct.setOnClickListener(v -> confirmDeleteProduct());
            observeProductForEdit();
        }

        binding.btnScanBarcode.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScanBarcodeActivity.class);
            barcodeLauncher.launch(intent);
        });

        binding.btnSave.setOnClickListener(v -> handleSaveProduct());
    }

    private void setupUnitSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, UNITS);
        binding.spinnerUnit.setAdapter(adapter);
        binding.spinnerUnit.setText(UNITS[0], false);
    }

    private void observeCategories() {
        viewModel.getCategories().observe(this, categories -> {
            if (categories != null && !categories.isEmpty()) {
                categoryList = categories;
                List<String> names = new ArrayList<>();
                for (Category c : categories) {
                    names.add(c.getName());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, names);
                binding.spinnerCategory.setAdapter(adapter);
                binding.spinnerCategory.setOnItemClickListener((parent, view, position, id) -> selectedCategory = categoryList.get(position));

                if (selectedCategory == null && !categoryList.isEmpty()) {
                    selectedCategory = categoryList.get(0);
                    binding.spinnerCategory.setText(selectedCategory.getName(), false);
                }
            }
        });
    }

    private void observeProductForEdit() {
        viewModel.getProducts().observe(this, products -> {
            if (products != null) {
                for (Product p : products) {
                    if (p.getId() == existingProductId) {
                        existingProduct = p;
                        fillProductData(p);
                        break;
                    }
                }
            }
        });
    }

    private void fillProductData(Product p) {
        binding.etProductName.setText(p.getName());
        binding.etBuyPrice.setText(String.valueOf((long) p.getBuyPrice()));
        binding.etSellPrice.setText(String.valueOf((long) p.getSellPrice()));
        binding.etStock.setText(String.valueOf(p.getStock()));
        binding.etMinStock.setText(String.valueOf(p.getMinStock()));
        binding.etBarcode.setText(p.getBarcode());

        if (p.getUnit() != null) {
            binding.spinnerUnit.setText(p.getUnit(), false);
        }

        if (p.getCategoryName() != null) {
            binding.spinnerCategory.setText(p.getCategoryName(), false);
        }
    }

    private void confirmDeleteProduct() {
        if (existingProduct == null) return;
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Hapus Produk?")
                .setMessage("Apakah Anda yakin ingin menghapus produk \"" + existingProduct.getName() + "\"?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    viewModel.deleteProduct(existingProduct, () -> {
                        HapticUtil.performSuccessFeedback(this);
                        Toast.makeText(this, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                })
                .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void handleSaveProduct() {
        String name = binding.etProductName.getText() != null ? binding.etProductName.getText().toString().trim() : "";
        String buyPriceStr = binding.etBuyPrice.getText() != null ? binding.etBuyPrice.getText().toString().trim() : "";
        String sellPriceStr = binding.etSellPrice.getText() != null ? binding.etSellPrice.getText().toString().trim() : "";
        String stockStr = binding.etStock.getText() != null ? binding.etStock.getText().toString().trim() : "";
        String minStockStr = binding.etMinStock.getText() != null ? binding.etMinStock.getText().toString().trim() : "";
        String unit = binding.spinnerUnit.getText() != null ? binding.spinnerUnit.getText().toString().trim() : "pcs";
        String barcode = binding.etBarcode.getText() != null ? binding.etBarcode.getText().toString().trim() : "";

        if (name.isEmpty()) {
            binding.etProductName.setError("Nama produk tidak boleh kosong");
            return;
        }

        if (sellPriceStr.isEmpty()) {
            binding.etSellPrice.setError("Harga jual tidak boleh kosong");
            return;
        }

        double buyPrice = 0.0;
        double sellPrice = 0.0;
        int stock = 0;
        int minStock = 5;

        try {
            if (!buyPriceStr.isEmpty()) buyPrice = Double.parseDouble(buyPriceStr);
            sellPrice = Double.parseDouble(sellPriceStr);
            if (!stockStr.isEmpty()) stock = Integer.parseInt(stockStr);
            if (!minStockStr.isEmpty()) minStock = Integer.parseInt(minStockStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Format angka tidak valid!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sellPrice < buyPrice) {
            Toast.makeText(this, "Peringatan: Harga jual lebih kecil dari modal!", Toast.LENGTH_LONG).show();
        }

        long catId = selectedCategory != null ? selectedCategory.getId() : 1;
        String catName = selectedCategory != null ? selectedCategory.getName() : "Sembako";

        if (existingProduct != null) {
            existingProduct.setName(name);
            existingProduct.setCategoryId(catId);
            existingProduct.setCategoryName(catName);
            existingProduct.setBuyPrice(buyPrice);
            existingProduct.setSellPrice(sellPrice);
            existingProduct.setStock(stock);
            existingProduct.setMinStock(minStock);
            existingProduct.setUnit(unit);
            existingProduct.setBarcode(barcode);

            AnimationHelper.animateSpringClick(binding.btnSave, () -> {
                viewModel.updateProduct(existingProduct, () -> {
                    HapticUtil.performSuccessFeedback(this);
                    Toast.makeText(this, "Produk berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        } else {
            Product newProduct = new Product(name, catId, catName, buyPrice, sellPrice, stock, minStock, unit, null, barcode);
            AnimationHelper.animateSpringClick(binding.btnSave, () -> {
                viewModel.insertProduct(newProduct, () -> {
                    HapticUtil.performSuccessFeedback(this);
                    Toast.makeText(this, "Produk berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
    }
}
