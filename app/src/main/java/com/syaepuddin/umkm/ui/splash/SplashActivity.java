package com.syaepuddin.umkm.ui.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.syaepuddin.umkm.databinding.ActivitySplashBinding;
import com.syaepuddin.umkm.ui.main.MainActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private final Handler splashHandler = new Handler(Looper.getMainLooper());
    private Runnable navigateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivLogo.setScaleX(0.5f);
        binding.ivLogo.setScaleY(0.5f);
        binding.ivLogo.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(700)
                .start();

        navigateRunnable = () -> {
            if (!isFinishing() && !isDestroyed()) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                try {
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } catch (Exception ignored) {}
                finish();
            }
        };

        splashHandler.postDelayed(navigateRunnable, 1200);
    }

    @Override
    protected void onDestroy() {
        if (splashHandler != null && navigateRunnable != null) {
            splashHandler.removeCallbacks(navigateRunnable);
        }
        super.onDestroy();
        binding = null;
    }
}
