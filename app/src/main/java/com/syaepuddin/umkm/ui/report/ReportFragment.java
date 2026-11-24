package com.syaepuddin.umkm.ui.report;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.syaepuddin.umkm.R;
import com.syaepuddin.umkm.databinding.FragmentReportBinding;
import com.syaepuddin.umkm.util.AnimationHelper;
import com.syaepuddin.umkm.util.CurrencyFormatter;
import com.syaepuddin.umkm.util.DateUtil;

public class ReportFragment extends Fragment {

    private FragmentReportBinding binding;
    private ReportViewModel viewModel;

    private long startTime = 0;
    private long endTime = 0;
    private String periodLabel = "Hari Ini";

    private double currentSales = 0.0;
    private double currentExpenses = 0.0;
    private int currentTxCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ReportViewModel.class);

        setPeriodToday();
        setupChips();

        binding.btnShareReport.setOnClickListener(v -> {
            AnimationHelper.animateSpringClick(v, this::shareReport);
        });
    }

    private void setupChips() {
        binding.chipGroupPeriod.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chipToday)) {
                setPeriodToday();
            } else if (checkedIds.contains(R.id.chip7Days)) {
                setPeriod7Days();
            } else if (checkedIds.contains(R.id.chipMonth)) {
                setPeriodMonth();
            }
            observeData();
        });
    }

    private void setPeriodToday() {
        periodLabel = "Hari Ini";
        startTime = DateUtil.getStartOfDay(System.currentTimeMillis());
        endTime = DateUtil.getEndOfDay(System.currentTimeMillis());
        observeData();
    }

    private void setPeriod7Days() {
        periodLabel = "7 Hari Terakhir";
        startTime = DateUtil.getSevenDaysAgoStart();
        endTime = DateUtil.getEndOfDay(System.currentTimeMillis());
        observeData();
    }

    private void setPeriodMonth() {
        periodLabel = "Bulan Ini";
        startTime = DateUtil.getStartOfDay(System.currentTimeMillis() - (30L * 24 * 3600 * 1000));
        endTime = DateUtil.getEndOfDay(System.currentTimeMillis());
        observeData();
    }

    private void observeData() {
        viewModel.getTotalSalesBetween(startTime, endTime).observe(getViewLifecycleOwner(), sales -> {
            currentSales = sales != null ? sales : 0.0;
            AnimationHelper.animateCountUpCurrency(binding.tvTotalSales, 0, currentSales, 600);
            updateNetProfit();
        });

        viewModel.getTotalExpensesBetween(startTime, endTime).observe(getViewLifecycleOwner(), expenses -> {
            currentExpenses = expenses != null ? expenses : 0.0;
            AnimationHelper.animateCountUpCurrency(binding.tvTotalExpenses, 0, currentExpenses, 600);
            updateNetProfit();
        });

        viewModel.getTransactionCountBetween(startTime, endTime).observe(getViewLifecycleOwner(), count -> {
            currentTxCount = count != null ? count : 0;
            AnimationHelper.animateCountUpInteger(binding.tvTotalTxCount, 0, currentTxCount, 600);
        });
    }

    private void updateNetProfit() {
        double netProfit = Math.max(0, currentSales - currentExpenses);
        AnimationHelper.animateCountUpCurrency(binding.tvNetProfit, 0, netProfit, 600);
    }

    private void shareReport() {
        String msg = "📊 *Laporan Keuangan Warung (" + periodLabel + ")*\n\n" +
                "💰 Total Omset: " + CurrencyFormatter.formatRupiah(currentSales) + "\n" +
                "📉 Total Pengeluaran: " + CurrencyFormatter.formatRupiah(currentExpenses) + "\n" +
                "💵 Estimasi Laba Bersih: " + CurrencyFormatter.formatRupiah(Math.max(0, currentSales - currentExpenses)) + "\n" +
                "🛒 Jumlah Transaksi: " + currentTxCount + " transaksi\n\n" +
                "_Dikirim dari Aplikasi KasirKu UMKM_";

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, msg);
        startActivity(Intent.createChooser(i, "Bagikan Laporan Keuangan"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
