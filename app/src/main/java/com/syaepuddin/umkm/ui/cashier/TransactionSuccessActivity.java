package com.syaepuddin.umkm.ui.cashier;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.syaepuddin.umkm.data.entity.SaleTransaction;
import com.syaepuddin.umkm.data.entity.ShopProfile;
import com.syaepuddin.umkm.databinding.ActivityTransactionSuccessBinding;
import com.syaepuddin.umkm.util.AnimationHelper;
import com.syaepuddin.umkm.util.CurrencyFormatter;
import com.syaepuddin.umkm.util.DateUtil;
import com.syaepuddin.umkm.util.PdfGenerator;

import java.io.File;
import java.util.ArrayList;

public class TransactionSuccessActivity extends AppCompatActivity {

    private ActivityTransactionSuccessBinding binding;
    private double totalAmount;
    private double paidAmount;
    private String paymentMethod;
    private String customerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        totalAmount = getIntent().getDoubleExtra("TOTAL_AMOUNT", 0.0);
        paidAmount = getIntent().getDoubleExtra("PAID_AMOUNT", 0.0);
        paymentMethod = getIntent().getStringExtra("PAYMENT_METHOD");
        customerName = getIntent().getStringExtra("CUSTOMER_NAME");

        double change = Math.max(0, paidAmount - totalAmount);

        binding.tvTotalAmount.setText(CurrencyFormatter.formatRupiah(totalAmount));
        binding.tvPaidAmount.setText(CurrencyFormatter.formatRupiah(paidAmount));
        binding.tvChangeAmount.setText(CurrencyFormatter.formatRupiah(change));
        binding.tvPaymentMethod.setText(paymentMethod != null ? paymentMethod : "TUNAI");
        binding.tvTxDate.setText(DateUtil.formatDateTime(System.currentTimeMillis()));

        binding.btnShareWhatsapp.setOnClickListener(v -> {
            AnimationHelper.animateSpringClick(v, this::shareReceiptPdf);
        });

        binding.btnBackToCashier.setOnClickListener(v -> {
            AnimationHelper.animateSpringClick(v, this::finish);
        });
    }

    private void shareReceiptPdf() {
        try {
            ShopProfile shop = new ShopProfile("Warung Berkah", "Bapak Syaepuddin", "Jl. Raya Pinggir Jalan No. 12", "081234567890", "Terima Kasih!");
            SaleTransaction tx = new SaleTransaction(
                    "TX" + System.currentTimeMillis(), System.currentTimeMillis(),
                    totalAmount, 0.0, totalAmount, paymentMethod, paidAmount,
                    Math.max(0, paidAmount - totalAmount), 0, customerName, "LUNAS", ""
            );

            File pdfFile = PdfGenerator.generateReceiptPdf(this, shop, tx, new ArrayList<>());
            Uri pdfUri = PdfGenerator.getUriForFile(this, pdfFile);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Struk Pembelian " + shop.getShopName() + "\nTotal: " + CurrencyFormatter.formatRupiah(totalAmount));
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Bagikan Struk PDF"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal membuat PDF struk", Toast.LENGTH_SHORT).show();
        }
    }
}
