package com.syaepuddin.umkm.ui.debt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.syaepuddin.umkm.R;
import com.syaepuddin.umkm.data.entity.Debt;
import com.syaepuddin.umkm.databinding.ItemDebtHistoryBinding;
import com.syaepuddin.umkm.util.AnimationHelper;
import com.syaepuddin.umkm.util.CurrencyFormatter;
import com.syaepuddin.umkm.util.DateUtil;
import com.syaepuddin.umkm.util.HapticUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class DebtDetailActivity extends AppCompatActivity {

    private DebtViewModel viewModel;
    private long customerId;
    private String customerName;
    private String customerPhone;
    private double currentTotalDebt;

    private RecyclerView rvDebts;
    private DebtAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt_detail);

        viewModel = new ViewModelProvider(this).get(DebtViewModel.class);

        customerId = getIntent().getLongExtra("CUSTOMER_ID", 0);
        customerName = getIntent().getStringExtra("CUSTOMER_NAME");
        customerPhone = getIntent().getStringExtra("CUSTOMER_PHONE");
        currentTotalDebt = getIntent().getDoubleExtra("CUSTOMER_DEBT", 0.0);

        View btnWhatsapp = findViewById(R.id.btnSendWhatsapp);
        if (btnWhatsapp != null) {
            btnWhatsapp.setOnClickListener(v -> sendWhatsAppReminder());
        }

        rvDebts = findViewById(R.id.rvDebts);
        if (rvDebts != null) {
            adapter = new DebtAdapter();
            rvDebts.setLayoutManager(new LinearLayoutManager(this));
            rvDebts.setAdapter(adapter);
        }

        observeDebts();
    }

    private void observeDebts() {
        viewModel.getDebtsForCustomer(customerId).observe(this, debts -> {
            if (debts != null && adapter != null) {
                adapter.setDebts(debts);
            }
        });
    }

    private void sendWhatsAppReminder() {
        if (customerPhone == null || customerPhone.isEmpty()) {
            Toast.makeText(this, "Nomor telepon pelanggan tidak tersedia", Toast.LENGTH_SHORT).show();
            return;
        }

        String formattedPhone = customerPhone.replaceAll("[^0-9]", "");
        if (formattedPhone.startsWith("0")) {
            formattedPhone = "62" + formattedPhone.substring(1);
        }

        String msg = "Halo Kak " + customerName + ", sekadar mengingatkan catatan kasbon sebesar " +
                CurrencyFormatter.formatRupiah(currentTotalDebt) + " di Warung Berkah. Terima kasih banyak Kak! 🙏";

        try {
            String url = "https://api.whatsapp.com/send?phone=" + formattedPhone + "&text=" + URLEncoder.encode(msg, "UTF-8");
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "Aplikasi WhatsApp tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private class DebtAdapter extends RecyclerView.Adapter<DebtAdapter.ViewHolder> {
        private final List<Debt> debtList = new ArrayList<>();

        public void setDebts(List<Debt> debts) {
            this.debtList.clear();
            if (debts != null) {
                this.debtList.addAll(debts);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemDebtHistoryBinding binding = ItemDebtHistoryBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false
            );
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Debt d = debtList.get(position);
            holder.binding.tvDate.setText(DateUtil.formatDateTime(d.getCreatedAt()));
            holder.binding.tvRemainingAmount.setText(CurrencyFormatter.formatRupiah(d.getRemainingAmount()));
            holder.binding.tvStatus.setText("Status: " + d.getStatus());

            if ("LUNAS".equalsIgnoreCase(d.getStatus()) || d.getRemainingAmount() <= 0) {
                holder.binding.btnPayDebt.setVisibility(View.GONE);
            } else {
                holder.binding.btnPayDebt.setVisibility(View.VISIBLE);
                holder.binding.btnPayDebt.setOnClickListener(v -> showPayDialog(d));
            }
        }

        @Override
        public int getItemCount() { return debtList.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            final ItemDebtHistoryBinding binding;
            ViewHolder(ItemDebtHistoryBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    private void showPayDialog(Debt debt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bayar / Cicil Kasbon");

        final EditText input = new EditText(this);
        input.setHint("Masukkan Jumlah Pembayaran (Rp)");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String val = input.getText().toString().trim();
            if (!val.isEmpty()) {
                try {
                    double amt = Double.parseDouble(val);
                    if (amt > 0) {
                        viewModel.payDebt(debt.getId(), amt, "Bayar Cicilan", () -> {
                            HapticUtil.performSuccessFeedback(this);
                            Toast.makeText(this, "Pembayaran kasbon berhasil dicatat", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (NumberFormatException ignored) {}
            }
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
