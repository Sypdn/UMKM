package com.syaepuddin.umkm.util;

import android.content.Context;

import com.syaepuddin.umkm.data.db.AppDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class BackupRestoreUtil {
    private static final String DB_NAME = "umkm_kasir.db";

    public static boolean backupDatabase(Context context, File targetFolder) {
        try {
            File currentDb = context.getDatabasePath(DB_NAME);
            if (!currentDb.exists()) return false;

            if (!targetFolder.exists()) {
                targetFolder.mkdirs();
            }

            File backupFile = new File(targetFolder, "Backup_WarungKu_" + System.currentTimeMillis() + ".db");

            FileInputStream fis = new FileInputStream(currentDb);
            FileOutputStream fos = new FileOutputStream(backupFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

            fos.flush();
            fos.close();
            fis.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean restoreDatabase(Context context, File backupFile) {
        try {
            File currentDb = context.getDatabasePath(DB_NAME);

            // Close existing DB connection first
            AppDatabase.getInstance(context).close();

            FileInputStream fis = new FileInputStream(backupFile);
            FileOutputStream fos = new FileOutputStream(currentDb);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

            fos.flush();
            fos.close();
            fis.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
