package com.syaepuddin.umkm.util;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatter {
    private static final Locale ID_LOCALE = new Locale("id", "ID");

    public static String formatRupiah(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(ID_LOCALE);
        formatter.setMaximumFractionDigits(0);
        String formatted = formatter.format(amount);
        return formatted.replace("Rp", "Rp ");
    }

    public static String formatNumber(double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(ID_LOCALE);
        formatter.setMaximumFractionDigits(0);
        return formatter.format(amount);
    }
}
