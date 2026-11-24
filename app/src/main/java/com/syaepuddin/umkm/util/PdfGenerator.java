package com.syaepuddin.umkm.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.syaepuddin.umkm.data.entity.SaleItem;
import com.syaepuddin.umkm.data.entity.SaleTransaction;
import com.syaepuddin.umkm.data.entity.ShopProfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfGenerator {

    public static File generateReceiptPdf(Context context, ShopProfile shop, SaleTransaction tx, List<SaleItem> items) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        int width = 300;
        int height = 500 + (items.size() * 30);
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        canvas.drawColor(Color.WHITE);

        // Header - Shop Info
        titlePaint.setTextSize(14f);
        titlePaint.setFakeBoldText(true);
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextAlign(Paint.Align.CENTER);

        paint.setTextSize(9f);
        paint.setColor(Color.DKGRAY);
        paint.setTextAlign(Paint.Align.CENTER);

        int y = 30;
        canvas.drawText(shop != null ? shop.getShopName() : "WarungKu", width / 2f, y, titlePaint);
        y += 18;
        canvas.drawText(shop != null ? shop.getAddress() : "Pusat UMKM", width / 2f, y, paint);
        y += 14;
        canvas.drawText("Telp: " + (shop != null ? shop.getPhone() : "-"), width / 2f, y, paint);

        // Divider
        y += 15;
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setStrokeWidth(1f);
        canvas.drawLine(10, y, width - 10, y, paint);

        // Tx info
        y += 18;
        paint.setFakeBoldText(false);
        paint.setTextSize(9f);
        canvas.drawText("No: #" + tx.getTransactionNo(), 10, y, paint);
        y += 14;
        canvas.drawText("Tgl: " + DateUtil.formatDateTime(tx.getTimestamp()), 10, y, paint);
        y += 14;
        canvas.drawText("Kasir: " + (shop != null ? shop.getOwnerName() : "Kasir"), 10, y, paint);
        if (tx.getCustomerName() != null && !tx.getCustomerName().isEmpty()) {
            y += 14;
            canvas.drawText("Pelanggan: " + tx.getCustomerName(), 10, y, paint);
        }

        // Divider
        y += 15;
        canvas.drawLine(10, y, width - 10, y, paint);

        // Items
        y += 20;
        titlePaint.setTextSize(10f);
        titlePaint.setTextAlign(Paint.Align.LEFT);

        for (SaleItem item : items) {
            canvas.drawText(item.getProductName(), 10, y, titlePaint);
            y += 14;
            String detailStr = item.getQuantity() + " " + item.getUnit() + " x " + CurrencyFormatter.formatRupiah(item.getSellPrice());
            String subtotalStr = CurrencyFormatter.formatRupiah(item.getSubtotal());

            canvas.drawText(detailStr, 10, y, paint);

            Paint rightPaint = new Paint(paint);
            rightPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(subtotalStr, width - 10, y, rightPaint);

            y += 18;
        }

        // Divider
        canvas.drawLine(10, y, width - 10, y, paint);

        // Totals
        y += 20;
        Paint rightPaint = new Paint(paint);
        rightPaint.setTextAlign(Paint.Align.RIGHT);

        canvas.drawText("Total:", 10, y, paint);
        canvas.drawText(CurrencyFormatter.formatRupiah(tx.getTotalAmount()), width - 10, y, rightPaint);

        if (tx.getDiscountAmount() > 0) {
            y += 16;
            canvas.drawText("Diskon:", 10, y, paint);
            canvas.drawText("- " + CurrencyFormatter.formatRupiah(tx.getDiscountAmount()), width - 10, y, rightPaint);
        }

        y += 16;
        titlePaint.setTextSize(11f);
        canvas.drawText("Bayar (" + tx.getPaymentMethod() + "):", 10, y, titlePaint);
        canvas.drawText(CurrencyFormatter.formatRupiah(tx.getPaidAmount()), width - 10, y, titlePaint);

        y += 16;
        canvas.drawText("Kembalian:", 10, y, paint);
        canvas.drawText(CurrencyFormatter.formatRupiah(tx.getChangeAmount()), width - 10, y, rightPaint);

        // Footer
        y += 25;
        canvas.drawLine(10, y, width - 10, y, paint);
        y += 20;
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(8f);
        canvas.drawText(shop != null && shop.getReceiptFooter() != null ? shop.getReceiptFooter() : "Terima Kasih!", width / 2f, y, paint);

        pdfDocument.finishPage(page);

        File outputFile = new File(context.getCacheDir(), "Struk_" + tx.getTransactionNo() + ".pdf");
        try {
            pdfDocument.writeTo(new FileOutputStream(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pdfDocument.close();
        }

        return outputFile;
    }

    public static Uri getUriForFile(Context context, File file) {
        return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
    }
}
