# 🏪 KasirKu UMKM - Aplikasi Kasir & Pembukuan Warung (Offline-First)

**KasirKu UMKM** adalah aplikasi kasir (Point of Sales / POS) dan pencatatan keuangan harian yang dirancang khusus untuk pemilik UMKM, pedagang kelontong, dan warung di Indonesia. Aplikasi ini dibuat menggunakan bahasa **Pure Java** dengan arsitektur **MVVM (Model-View-ViewModel)** dan berjalan **100% Offline** tanpa memerlukan koneksi internet.

![KasirKu UMKM](https://img.shields.io/badge/Platform-Android-green.svg)
![Language](https://img.shields.io/badge/Language-Java-orange.svg)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-blue.svg)
![Database](https://img.shields.io/badge/Database-Room_SQLite-purple.svg)

---

## 🎨 Fitur Utama

- 🛒 **Point of Sales (Kasir & Transaksi)**
  - Pencarian produk cepat & integrasi **CameraX + ML Kit Barcode Scanner**.
  - Keranjang belanja interaktif dengan kalkulasi otomatis & tombol uang pas/pecahan cepat.
  - Struk belanja digital (Export PDF & Bagikan via WhatsApp).

- 📦 **Kelola Produk & Stok**
  - Multi-kategori produk (Sembako, Minuman, Snack, dll.).
  - Peringatan otomatis untuk produk dengan **stok menipis**.
  - Fitur restok cepat, edit produk, serta **hapus produk** (dengan konfirmasi & fitur Undo/Batal).

- 👥 **Catatan Kasbon & Piutang Pelanggan**
  - Catat piutang/kasbon pelanggan lengkap dengan nama dan nomor WhatsApp.
  - Pembayaran cicilan maupun pelunasan kasbon.
  - Generator pesan pengingat tagihan otomatis via **WhatsApp**.

- 💸 **Pengeluaran Operasional**
  - Catat pengeluaran harian toko (belanja modal, listrik, sewa, gaji).

- 📊 **Laporan Keuangan & Grafik Analytics**
  - Dashboard analitik dengan **MPAndroidChart** (Grafik Penjualan 7 Hari Terakhir).
  - Ringkasan Otomatis: **Total Omset**, **Estimasi Laba Bersih**, dan **Total Transaksi**.
  - Ekspor ringkasan laporan keuangan ke WhatsApp.

- 🔒 **Keamanan & Backup Data**
  - Mode Gelap (**Dark Mode**) modern & kontras tinggi.
  - Proteksi **PIN Keamanan 6 Digit**.
  - **Local Backup & Restore Database** untuk mengamankan data transaksi toko Anda.

---

## 🛠️ Teknologi & Library

- **Bahasa**: Java (JDK 11)
- **Min SDK**: API 29 (Android 10) | **Target SDK**: API 35/37
- **Architecture**: MVVM (LiveData, ViewModel, ViewBinding)
- **Database**: Room Database (SQLite)
- **UI Toolkit**: Material Design 3 (Dark Theme)
- **Charts**: MPAndroidChart
- **Barcode & Camera**: Google ML Kit Barcode Scanning & Android CameraX
- **Image Loader**: Glide
- **PDF Generator**: Native Android Canvas / iText PDF

---

## 🚀 Panduan Membuka Proyek

1. Clone repositori ini:
   ```bash
   git clone https://github.com/Sypdn/UMKM.git
   ```
2. Buka Android Studio (versi Jellyfish / Ladybug / 2026+).
3. Pilih **Open** dan pilih folder proyek `UMKM`.
4. Tunggu proses Gradle Sync selesai, lalu jalankan aplikasi di Emulator atau Perangkat Android (min. Android 10).

## 📷 Hak Cipta & Lisensi

Dibuat khusus untuk UMKM **UNTUK MEMBANTU UMKM**.
© 2026  Sypdn (Syaepuddin). Hak Cipta Dilindungi Undang-Undang.
