package com.syaepuddin.umkm.ui.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.syaepuddin.umkm.data.entity.ShopProfile;
import com.syaepuddin.umkm.data.repository.AppRepository;

public class SettingsViewModel extends AndroidViewModel {

    private final AppRepository mRepository;
    private final LiveData<ShopProfile> shopProfile;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(application);
        shopProfile = mRepository.getShopProfile();
    }

    public LiveData<ShopProfile> getShopProfile() { return shopProfile; }

    public void updateShopProfile(ShopProfile profile, Runnable onDone) {
        mRepository.updateShopProfile(profile, onDone);
    }
}
