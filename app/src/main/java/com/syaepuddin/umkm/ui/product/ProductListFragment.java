package com.syaepuddin.umkm.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.syaepuddin.umkm.data.entity.Product;
import com.syaepuddin.umkm.databinding.FragmentProductListBinding;
import com.syaepuddin.umkm.util.AnimationHelper;
import com.syaepuddin.umkm.util.HapticUtil;

import java.util.ArrayList;
import java.util.List;

public class ProductListFragment extends Fragment {

    private FragmentProductListBinding binding;
    private ProductViewModel viewModel;
    private ProductAdapter adapter;

    private List<Product> allProducts = new ArrayList<>();
    private String currentSearch = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProductListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        setupRecyclerView();
        setupSwipeToDelete();
        observeViewModel();

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearch = s != null ? s.toString().trim().toLowerCase() : "";
                filterProducts();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.fabAddProduct.setOnClickListener(v -> {
            AnimationHelper.animateSpringClick(v, () -> {
                Intent intent = new Intent(getContext(), AddEditProductActivity.class);
                startActivity(intent);
            });
        });
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(new ProductAdapter.OnProductItemClickListener() {
            @Override
            public void onProductClick(Product product) {
                Intent intent = new Intent(getContext(), AddEditProductActivity.class);
                intent.putExtra("PRODUCT_ID", product.getId());
                startActivity(intent);
            }

            @Override
            public void onRestockClick(Product product) {
                RestockDialog dialog = new RestockDialog(requireContext(), product, (addQty, note) -> {
                    viewModel.restockProduct(product.getId(), product.getName(), addQty, note, () -> {
                        HapticUtil.performSuccessFeedback(getContext());
                        Toast.makeText(getContext(), "Stok berhasil ditambahkan +" + addQty, Toast.LENGTH_SHORT).show();
                    });
                });
                dialog.show();
            }

            @Override
            public void onDeleteClick(Product product) {
                showDeleteConfirmationDialog(product);
            }
        });

        binding.rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvProducts.setAdapter(adapter);
    }

    private void showDeleteConfirmationDialog(Product product) {
        if (getContext() == null || product == null) return;
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hapus Produk?")
                .setMessage("Apakah Anda yakin ingin menghapus produk \"" + product.getName() + "\"?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    HapticUtil.performClickFeedback(getView());
                    viewModel.deleteProduct(product, null);
                    Snackbar.make(binding.getRoot(), "Produk \"" + product.getName() + "\" berhasil dihapus", Snackbar.LENGTH_LONG)
                            .setAction("BATAL", v -> viewModel.insertProduct(product, null))
                            .show();
                })
                .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Product productToDelete = adapter.getProductAt(position);
                if (productToDelete != null) {
                    HapticUtil.performClickFeedback(viewHolder.itemView);
                    viewModel.deleteProduct(productToDelete, null);

                    Snackbar.make(binding.getRoot(), "Produk \"" + productToDelete.getName() + "\" dihapus", Snackbar.LENGTH_LONG)
                            .setAction("BATAL", v -> viewModel.insertProduct(productToDelete, null))
                            .show();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(binding.rvProducts);
    }

    private void observeViewModel() {
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                allProducts = products;
                filterProducts();
            }
        });
    }

    private void filterProducts() {
        List<Product> filtered = new ArrayList<>();
        for (Product p : allProducts) {
            if (currentSearch.isEmpty() ||
                    (p.getName() != null && p.getName().toLowerCase().contains(currentSearch)) ||
                    (p.getBarcode() != null && p.getBarcode().toLowerCase().contains(currentSearch))) {
                filtered.add(p);
            }
        }

        adapter.setProducts(filtered);

        if (filtered.isEmpty()) {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.rvProducts.setVisibility(View.GONE);
        } else {
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.rvProducts.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
