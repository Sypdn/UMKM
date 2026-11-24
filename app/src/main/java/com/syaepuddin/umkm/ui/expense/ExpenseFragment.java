package com.syaepuddin.umkm.ui.expense;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.syaepuddin.umkm.databinding.FragmentExpenseBinding;
import com.syaepuddin.umkm.util.AnimationHelper;
import com.syaepuddin.umkm.util.HapticUtil;

public class ExpenseFragment extends Fragment {

    private FragmentExpenseBinding binding;
    private ExpenseViewModel viewModel;
    private ExpenseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExpenseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        setupRecyclerView();
        observeViewModel();

        binding.fabAddExpense.setOnClickListener(v -> {
            AnimationHelper.animateSpringClick(v, () -> {
                AddExpenseDialog dialog = new AddExpenseDialog(requireContext(), expense -> {
                    viewModel.insertExpense(expense, () -> {
                        HapticUtil.performSuccessFeedback(getContext());
                        Toast.makeText(getContext(), "Pengeluaran berhasil dicatat", Toast.LENGTH_SHORT).show();
                    });
                });
                dialog.show();
            });
        });
    }

    private void setupRecyclerView() {
        adapter = new ExpenseAdapter();
        binding.rvExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvExpenses.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getAllExpenses().observe(getViewLifecycleOwner(), expenses -> {
            if (expenses != null && !expenses.isEmpty()) {
                binding.layoutEmpty.setVisibility(View.GONE);
                binding.rvExpenses.setVisibility(View.VISIBLE);
                adapter.setExpenses(expenses);
            } else {
                binding.layoutEmpty.setVisibility(View.VISIBLE);
                binding.rvExpenses.setVisibility(View.GONE);
            }
        });

        viewModel.getTotalExpenseToday().observe(getViewLifecycleOwner(), total -> {
            double amt = total != null ? total : 0.0;
            AnimationHelper.animateCountUpCurrency(binding.tvTotalExpenseToday, 0, amt, 800);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
