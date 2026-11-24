package com.syaepuddin.umkm.ui.cashier;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.syaepuddin.umkm.R;
import com.syaepuddin.umkm.data.entity.Customer;
import com.syaepuddin.umkm.databinding.DialogCartBottomSheetBinding;
import com.syaepuddin.umkm.util.AnimationHelper;
import com.syaepuddin.umkm.util.CurrencyFormatter;
import com.syaepuddin.umkm.util.HapticUtil;

import java.util.ArrayList;
import java.util.List;

public class CartBottomSheetDialog extends BottomSheetDialogFragment {

    private DialogCartBottomSheetBinding binding;
    private CashierViewModel viewModel;
    private CartAdapter cartAdapter;

    private List<Customer> customerList = new ArrayList<>();
    private Customer selectedCustomer = null;
    private String selectedPaymentMethod = "TUNAI";
    private double currentTotal = 0.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogCartBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(CashierViewModel.class);

        setupRecyclerView();
        setupPaymentChips();
        setupQuickMoneyButtons();
        observeViewModel();

        binding.btnClearCart.setOnClickListener(v -> viewModel.clearCart());

        binding.etPaidAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateChange();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.btnPayNow.setOnClickListener(v -> handleCheckout());
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter((product, newQty) -> viewModel.updateQuantity(product, newQty));
        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCartItems.setAdapter(cartAdapter);
    }

    private void setupPaymentChips() {
        binding.chipGroupPayment.setOnCheckedStateChangeListener((group, checkedIds) -> {
            HapticUtil.performClickFeedback(group);
            if (checkedIds.contains(R.id.chipKasbon)) {
                selectedPaymentMethod = "KASBON";
                binding.layoutCustomerSpinner.setVisibility(View.VISIBLE);
            } else if (checkedIds.contains(R.id.chipQris)) {
                selectedPaymentMethod = "QRIS";
                binding.layoutCustomerSpinner.setVisibility(View.GONE);
                setExactPaidAmount();
            } else if (checkedIds.contains(R.id.chipTransfer)) {
                selectedPaymentMethod = "TRANSFER";
                binding.layoutCustomerSpinner.setVisibility(View.GONE);
                setExactPaidAmount();
            } else {
                selectedPaymentMethod = "TUNAI";
                binding.layoutCustomerSpinner.setVisibility(View.GONE);
            }
        });
    }

    private void setupQuickMoneyButtons() {
        binding.btnExactMoney.setOnClickListener(v -> setExactPaidAmount());
        binding.btn2k.setOnClickListener(v -> setPaidAmount(2000));
        binding.btn5k.setOnClickListener(v -> setPaidAmount(5000));
        binding.btn10k.setOnClickListener(v -> setPaidAmount(10000));
        binding.btn20k.setOnClickListener(v -> setPaidAmount(20000));
        binding.btn50k.setOnClickListener(v -> setPaidAmount(50000));
        binding.btn100k.setOnClickListener(v -> setPaidAmount(100000));
    }

    private void setExactPaidAmount() {
        binding.etPaidAmount.setText(String.valueOf((long) currentTotal));
    }

    private void setPaidAmount(long amount) {
        binding.etPaidAmount.setText(String.valueOf(amount));
    }

    private void observeViewModel() {
        viewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            cartAdapter.setItems(items);
            if (items == null || items.isEmpty()) {
                dismiss();
            }
        });

        viewModel.getTotalAmount().observe(getViewLifecycleOwner(), total -> {
            currentTotal = total != null ? total : 0.0;
            binding.tvTotalAmount.setText(CurrencyFormatter.formatRupiah(currentTotal));
            calculateChange();
        });

        viewModel.getCustomers().observe(getViewLifecycleOwner(), customers -> {
            if (customers != null) {
                customerList = customers;
                List<String> names = new ArrayList<>();
                for (Customer c : customers) {
                    names.add(c.getName() + " (" + CurrencyFormatter.formatRupiah(c.getTotalDebt()) + ")");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, names);
                binding.spinnerCustomer.setAdapter(adapter);
                binding.spinnerCustomer.setOnItemClickListener((parent, view, position, id) -> selectedCustomer = customerList.get(position));
            }
        });
    }

    private void calculateChange() {
        String input = binding.etPaidAmount.getText() != null ? binding.etPaidAmount.getText().toString().trim() : "";
        double paid = 0.0;
        if (!input.isEmpty()) {
            try {
                paid = Double.parseDouble(input);
            } catch (NumberFormatException ignored) {}
        }
        double change = paid - currentTotal;
        if (change >= 0) {
            binding.tvChangeAmount.setText(CurrencyFormatter.formatRupiah(change));
            binding.tvChangeAmount.setTextColor(getResources().getColor(R.color.emerald_secondary, null));
        } else {
            binding.tvChangeAmount.setText("Kurang: " + CurrencyFormatter.formatRupiah(Math.abs(change)));
            binding.tvChangeAmount.setTextColor(getResources().getColor(R.color.status_danger, null));
        }
    }

    private void handleCheckout() {
        String input = binding.etPaidAmount.getText() != null ? binding.etPaidAmount.getText().toString().trim() : "";
        double paid = 0.0;
        if (!input.isEmpty()) {
            try {
                paid = Double.parseDouble(input);
            } catch (NumberFormatException ignored) {}
        }

        if ("KASBON".equalsIgnoreCase(selectedPaymentMethod) && selectedCustomer == null) {
            Toast.makeText(getContext(), "Pilih pelanggan untuk pembayaran kasbon!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!"KASBON".equalsIgnoreCase(selectedPaymentMethod) && paid < currentTotal) {
            Toast.makeText(getContext(), "Jumlah pembayaran kurang!", Toast.LENGTH_SHORT).show();
            return;
        }

        final double finalPaid = paid;
        AnimationHelper.animateSpringClick(binding.btnPayNow, () -> {
            viewModel.processCheckout(selectedPaymentMethod, finalPaid, 0.0, selectedCustomer, () -> {
                HapticUtil.performSuccessFeedback(getContext());
                dismiss();
                Intent intent = new Intent(getContext(), TransactionSuccessActivity.class);
                intent.putExtra("TOTAL_AMOUNT", currentTotal);
                intent.putExtra("PAID_AMOUNT", finalPaid);
                intent.putExtra("PAYMENT_METHOD", selectedPaymentMethod);
                intent.putExtra("CUSTOMER_NAME", selectedCustomer != null ? selectedCustomer.getName() : "");
                startActivity(intent);
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
