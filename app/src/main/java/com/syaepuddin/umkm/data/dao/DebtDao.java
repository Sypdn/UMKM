package com.syaepuddin.umkm.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.syaepuddin.umkm.data.entity.Debt;
import com.syaepuddin.umkm.data.entity.DebtPayment;

import java.util.List;

@Dao
public interface DebtDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDebt(Debt debt);

    @Update
    void updateDebt(Debt debt);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertPayment(DebtPayment payment);

    @Query("SELECT * FROM debts WHERE customerId = :customerId ORDER BY createdAt DESC")
    LiveData<List<Debt>> getDebtsForCustomer(long customerId);

    @Query("SELECT * FROM debts WHERE status = 'BELUM_LUNAS' ORDER BY createdAt DESC")
    LiveData<List<Debt>> getAllUnpaidDebts();

    @Query("SELECT SUM(remainingAmount) FROM debts WHERE status = 'BELUM_LUNAS'")
    LiveData<Double> getTotalUnpaidDebtAmount();

    @Query("SELECT * FROM debt_payments WHERE debtId = :debtId ORDER BY paymentDate DESC")
    LiveData<List<DebtPayment>> getPaymentsForDebt(long debtId);

    @Query("SELECT * FROM debts WHERE id = :debtId LIMIT 1")
    Debt getDebtByIdSync(long debtId);
}
