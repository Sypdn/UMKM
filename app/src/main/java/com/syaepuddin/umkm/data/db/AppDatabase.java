package com.syaepuddin.umkm.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.syaepuddin.umkm.data.dao.CategoryDao;
import com.syaepuddin.umkm.data.dao.CustomerDao;
import com.syaepuddin.umkm.data.dao.DebtDao;
import com.syaepuddin.umkm.data.dao.ExpenseDao;
import com.syaepuddin.umkm.data.dao.ProductDao;
import com.syaepuddin.umkm.data.dao.SaleDao;
import com.syaepuddin.umkm.data.dao.ShopProfileDao;
import com.syaepuddin.umkm.data.dao.StockHistoryDao;
import com.syaepuddin.umkm.data.entity.Category;
import com.syaepuddin.umkm.data.entity.Customer;
import com.syaepuddin.umkm.data.entity.Debt;
import com.syaepuddin.umkm.data.entity.DebtPayment;
import com.syaepuddin.umkm.data.entity.Expense;
import com.syaepuddin.umkm.data.entity.Product;
import com.syaepuddin.umkm.data.entity.SaleItem;
import com.syaepuddin.umkm.data.entity.SaleTransaction;
import com.syaepuddin.umkm.data.entity.ShopProfile;
import com.syaepuddin.umkm.data.entity.StockHistory;

@Database(entities = {
        Product.class, Category.class, Customer.class,
        SaleTransaction.class, SaleItem.class, Debt.class,
        DebtPayment.class, Expense.class, StockHistory.class,
        ShopProfile.class
}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "umkm_kasir.db";
    private static volatile AppDatabase sInstance;

    public abstract ProductDao productDao();
    public abstract CategoryDao categoryDao();
    public abstract CustomerDao customerDao();
    public abstract SaleDao saleDao();
    public abstract DebtDao debtDao();
    public abstract ExpenseDao expenseDao();
    public abstract StockHistoryDao stockHistoryDao();
    public abstract ShopProfileDao shopProfileDao();

    public static AppDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DATABASE_NAME
                            )
                            .fallbackToDestructiveMigration()
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    // Seed default categories
                                    db.execSQL("INSERT INTO categories (name, colorHex, iconName) VALUES ('Sembako', '#F26522', 'ic_sembako');");
                                    db.execSQL("INSERT INTO categories (name, colorHex, iconName) VALUES ('Minuman', '#10B981', 'ic_drink');");
                                    db.execSQL("INSERT INTO categories (name, colorHex, iconName) VALUES ('Makanan', '#2563EB', 'ic_food');");
                                    db.execSQL("INSERT INTO categories (name, colorHex, iconName) VALUES ('Jajanan & Snack', '#F59E0B', 'ic_snack');");
                                    db.execSQL("INSERT INTO categories (name, colorHex, iconName) VALUES ('Rokok', '#64748B', 'ic_cigarette');");
                                    db.execSQL("INSERT INTO categories (name, colorHex, iconName) VALUES ('Lain-lain', '#8B5CF6', 'ic_other');");

                                    // Seed default Shop Profile
                                    db.execSQL("INSERT INTO shop_profile (id, shopName, ownerName, address, phone, receiptFooter, isPinEnabled, isDarkMode) " +
                                            "VALUES (1, 'Warung Berkah', 'Bapak Syaepuddin', 'Jl. Raya Pinggir Jalan No. 12', '081234567890', " +
                                            "'Terima Kasih Atas Kunjungan Anda!\nBarang Yang Sudah Dibeli Tidak Dapat Ditukar.', 0, 0);");

                                    // Seed sample products
                                    long now = System.currentTimeMillis();
                                    db.execSQL("INSERT INTO products (name, categoryId, categoryName, buyPrice, sellPrice, stock, minStock, unit, imagePath, barcode, createdAt, updatedAt) " +
                                            "VALUES ('Minyak Goreng 1L', 1, 'Sembako', 14000, 16000, 20, 5, 'botol', NULL, '8991001001', " + now + ", " + now + ");");
                                    db.execSQL("INSERT INTO products (name, categoryId, categoryName, buyPrice, sellPrice, stock, minStock, unit, imagePath, barcode, createdAt, updatedAt) " +
                                            "VALUES ('Beras Premium 5kg', 1, 'Sembako', 60000, 68000, 10, 2, 'karung', NULL, '8991001002', " + now + ", " + now + ");");
                                    db.execSQL("INSERT INTO products (name, categoryId, categoryName, buyPrice, sellPrice, stock, minStock, unit, imagePath, barcode, createdAt, updatedAt) " +
                                            "VALUES ('Kopi Hitam Sachet', 2, 'Minuman', 1200, 1500, 50, 10, 'sachet', NULL, '8991001003', " + now + ", " + now + ");");
                                    db.execSQL("INSERT INTO products (name, categoryId, categoryName, buyPrice, sellPrice, stock, minStock, unit, imagePath, barcode, createdAt, updatedAt) " +
                                            "VALUES ('Teh Manis Dingin', 2, 'Minuman', 3000, 5000, 15, 3, 'gelas', NULL, '', " + now + ", " + now + ");");
                                }
                            })
                            .build();
                }
            }
        }
        return sInstance;
    }
}
