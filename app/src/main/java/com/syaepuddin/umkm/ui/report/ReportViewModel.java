package com.syaepuddin.umkm.ui.report;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.syaepuddin.umkm.data.entity.SaleTransaction;
import com.syaepuddin.umkm.data.repository.AppRepository;

import java.util.List;

public class ReportViewModel extends AndroidViewModel {

    private final AppRepository mRepository;

    public ReportViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(application);
    }

    public LiveData<List<SaleTransaction>> getTransactionsBetween(long start, long end) {
        return mRepository.getTransactionsBetween(start, end);
    }

    public LiveData<Double> getTotalSalesBetween(long start, long end) {
        return mRepository.getTotalSalesBetween(start, end);
    }

    public LiveData<Integer> getTransactionCountBetween(long start, long end) {
        return mRepository.getTransactionCountBetween(start, end);
    }

    public LiveData<Double> getTotalExpensesBetween(long start, long end) {
        return mRepository.getTotalExpensesBetween(start, end);
    }
}
