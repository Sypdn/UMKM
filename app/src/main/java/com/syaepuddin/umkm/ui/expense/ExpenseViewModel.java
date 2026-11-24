package com.syaepuddin.umkm.ui.expense;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.syaepuddin.umkm.data.entity.Expense;
import com.syaepuddin.umkm.data.repository.AppRepository;
import com.syaepuddin.umkm.util.DateUtil;

import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {

    private final AppRepository mRepository;
    private final LiveData<List<Expense>> allExpenses;
    private final LiveData<Double> totalExpenseToday;

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(application);
        allExpenses = mRepository.getAllExpenses();

        long start = DateUtil.getStartOfDay(System.currentTimeMillis());
        long end = DateUtil.getEndOfDay(System.currentTimeMillis());
        totalExpenseToday = mRepository.getTotalExpensesBetween(start, end);
    }

    public LiveData<List<Expense>> getAllExpenses() { return allExpenses; }
    public LiveData<Double> getTotalExpenseToday() { return totalExpenseToday; }

    public void insertExpense(Expense expense, Runnable onDone) {
        mRepository.insertExpense(expense, onDone);
    }
}
