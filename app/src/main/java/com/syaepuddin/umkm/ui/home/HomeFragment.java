package com.syaepuddin.umkm.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.syaepuddin.umkm.R;
import com.syaepuddin.umkm.data.entity.Product;
import com.syaepuddin.umkm.data.entity.SaleTransaction;
import com.syaepuddin.umkm.databinding.FragmentHomeBinding;
import com.syaepuddin.umkm.ui.main.MainActivity;
import com.syaepuddin.umkm.util.AnimationHelper;
import com.syaepuddin.umkm.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private HomeProductAdapter lowStockAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding.tvGreeting.setText(DateUtil.getGreeting() + ",");

        setupLowStockRecyclerView();
        setupChart();
        observeViewModel();

        binding.btnStartSelling.setOnClickListener(v -> {
            AnimationHelper.animateSpringClick(v, () -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).navigateToFragment(R.id.navigation_cashier);
                }
            });
        });

        binding.btnSettings.setOnClickListener(v -> {
            AnimationHelper.animateSpringClick(v, () -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).openSettings();
                }
            });
        });
    }

    private void setupLowStockRecyclerView() {
        lowStockAdapter = new HomeProductAdapter();
        binding.rvLowStock.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvLowStock.setAdapter(lowStockAdapter);
    }

    private void observeViewModel() {
        viewModel.getShopProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null && profile.getShopName() != null) {
                binding.tvShopName.setText(profile.getShopName());
            }
        });

        viewModel.getSalesToday().observe(getViewLifecycleOwner(), sales -> {
            double totalSales = sales != null ? sales : 0.0;
            AnimationHelper.animateCountUpCurrency(binding.tvSalesToday, 0, totalSales, 800);

            // Estimate profit (~20%)
            double estProfit = totalSales * 0.20;
            AnimationHelper.animateCountUpCurrency(binding.tvProfitToday, 0, estProfit, 800);
        });

        viewModel.getTxCountToday().observe(getViewLifecycleOwner(), count -> {
            int txCount = count != null ? count : 0;
            AnimationHelper.animateCountUpInteger(binding.tvTxCount, 0, txCount, 800);
        });

        viewModel.getAllProducts().observe(getViewLifecycleOwner(), products -> {
            int totalProd = products != null ? products.size() : 0;
            AnimationHelper.animateCountUpInteger(binding.tvTotalProducts, 0, totalProd, 800);
        });

        viewModel.getLowStockProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                binding.tvLowStockTitle.setVisibility(View.VISIBLE);
                binding.rvLowStock.setVisibility(View.VISIBLE);
                lowStockAdapter.setProducts(products);
            } else {
                binding.tvLowStockTitle.setVisibility(View.GONE);
                binding.rvLowStock.setVisibility(View.GONE);
            }
        });

        viewModel.getRecentTransactions().observe(getViewLifecycleOwner(), this::updateChartData);
    }

    private void setupChart() {
        binding.salesLineChart.setDrawGridBackground(false);
        binding.salesLineChart.getDescription().setEnabled(false);
        binding.salesLineChart.getLegend().setEnabled(false);
        binding.salesLineChart.setTouchEnabled(true);
        binding.salesLineChart.setScaleEnabled(false);

        XAxis xAxis = binding.salesLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.parseColor("#94A3B8"));
        xAxis.setGranularity(1f);

        binding.salesLineChart.getAxisRight().setEnabled(false);
        binding.salesLineChart.getAxisLeft().setDrawGridLines(true);
        binding.salesLineChart.getAxisLeft().setGridColor(Color.parseColor("#334155"));
        binding.salesLineChart.getAxisLeft().setTextColor(Color.parseColor("#94A3B8"));
    }

    private void updateChartData(List<SaleTransaction> transactions) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Generate dummy 7 days timeline for demonstration chart
        String[] days = {"Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min"};
        float[] sampleValues = {150000f, 220000f, 180000f, 310000f, 270000f, 450000f, 380000f};

        for (int i = 0; i < 7; i++) {
            entries.add(new Entry(i, sampleValues[i]));
            labels.add(days[i]);
        }

        LineDataSet dataSet = new LineDataSet(entries, "Penjualan");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setColor(Color.parseColor("#F26522"));
        dataSet.setLineWidth(3f);
        dataSet.setDrawCircles(true);
        dataSet.setCircleColor(Color.parseColor("#F26522"));
        dataSet.setCircleRadius(5f);
        dataSet.setDrawValues(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#3D1E10"));

        LineData lineData = new LineData(dataSet);
        binding.salesLineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        binding.salesLineChart.setData(lineData);
        binding.salesLineChart.animateX(900);
        binding.salesLineChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
