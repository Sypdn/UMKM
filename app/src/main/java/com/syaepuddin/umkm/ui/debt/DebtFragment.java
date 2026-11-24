package com.syaepuddin.umkm.ui.debt;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.syaepuddin.umkm.data.entity.Customer;
import com.syaepuddin.umkm.databinding.FragmentDebtBinding;
import com.syaepuddin.umkm.util.AnimationHelper;
import com.syaepuddin.umkm.util.CurrencyFormatter;

import java.util.List;

public class DebtFragment extends Fragment {

    private FragmentDebtBinding binding;
    private DebtViewModel viewModel;
    private CustomerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDebtBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DebtViewModel.class);

        setupRecyclerView();
        observeViewModel();

        binding.fabAddCustomer.setOnClickListener(v -> {
            AnimationHelper.animateSpringClick(v, () -> {
                Intent intent = new Intent(getContext(), AddEditCustomerActivity.class);
                startActivity(intent);
            });
        });
    }

    private void setupRecyclerView() {
        adapter = new CustomerAdapter(customer -> {
            Intent intent = new Intent(getContext(), DebtDetailActivity.class);
            intent.putExtra("CUSTOMER_ID", customer.getId());
            intent.putExtra("CUSTOMER_NAME", customer.getName());
            intent.putExtra("CUSTOMER_PHONE", customer.getPhone());
            intent.putExtra("CUSTOMER_DEBT", customer.getTotalDebt());
            startActivity(intent);
        });

        binding.rvCustomers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCustomers.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getCustomers().observe(getViewLifecycleOwner(), customers -> {
            if (customers != null && !customers.isEmpty()) {
                binding.layoutEmpty.setVisibility(View.GONE);
                binding.rvCustomers.setVisibility(View.VISIBLE);
                adapter.setCustomers(customers);
            } else {
                binding.layoutEmpty.setVisibility(View.VISIBLE);
                binding.rvCustomers.setVisibility(View.GONE);
            }
        });

        viewModel.getTotalUnpaidDebtAmount().observe(getViewLifecycleOwner(), total -> {
            double amount = total != null ? total : 0.0;
            AnimationHelper.animateCountUpCurrency(binding.tvTotalDebtAmount, 0, amount, 800);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
