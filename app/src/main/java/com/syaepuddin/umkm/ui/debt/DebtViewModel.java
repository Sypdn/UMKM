package com.syaepuddin.umkm.ui.debt;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.syaepuddin.umkm.data.entity.Customer;
import com.syaepuddin.umkm.data.entity.Debt;
import com.syaepuddin.umkm.data.entity.DebtPayment;
import com.syaepuddin.umkm.data.repository.AppRepository;

import java.util.List;

public class DebtViewModel extends AndroidViewModel {

    private final AppRepository mRepository;
    private final LiveData<List<Customer>> customers;
    private final LiveData<List<Debt>> unpaidDebts;
    private final LiveData<Double> totalUnpaidDebtAmount;

    public DebtViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(application);
        customers = mRepository.getAllCustomers();
        unpaidDebts = mRepository.getAllUnpaidDebts();
        totalUnpaidDebtAmount = mRepository.getTotalUnpaidDebtAmount();
    }

    public LiveData<List<Customer>> getCustomers() { return customers; }
    public LiveData<List<Debt>> getUnpaidDebts() { return unpaidDebts; }
    public LiveData<Double> getTotalUnpaidDebtAmount() { return totalUnpaidDebtAmount; }
    public LiveData<List<Debt>> getDebtsForCustomer(long customerId) { return mRepository.getDebtsForCustomer(customerId); }
    public LiveData<List<DebtPayment>> getPaymentsForDebt(long debtId) { return mRepository.getPaymentsForDebt(debtId); }

    public void insertCustomer(Customer customer, Runnable onDone) {
        mRepository.insertCustomer(customer, onDone);
    }

    public void payDebt(long debtId, double payAmount, String note, Runnable onDone) {
        mRepository.payDebt(debtId, payAmount, note, onDone);
    }
}
