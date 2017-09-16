package com.tianpingpai.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import com.tianpingpai.core.ContextProvider;

import java.util.UUID;

public class Settings {

    public static Settings getInstance() {
        return SingletonFactory.getInstance(Settings.class);
    }

    private static final String KEY_GLOBAL = "global";
    private static final String KEY_UUID = "uuid";
    private static final String KEY_VERSION = "versionCode";

    SharedPreferences global = ContextProvider.getContext().getSharedPreferences(KEY_GLOBAL, Context.MODE_PRIVATE);

    public String getUUID() {
        String uuid;
        try {
            TelephonyManager tManager = (TelephonyManager) ContextProvider.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            uuid = tManager.getSimSerialNumber();
            if (uuid != null && uuid.length() == 20) {
                return "ASIM_" + uuid;
            }
            uuid = tManager.getDeviceId();
            if (uuid != null) {
                return "AIMEI_" + uuid;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        uuid = global.getString(KEY_UUID, null);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            global.edit().putString(KEY_UUID, uuid).apply();
        }
        return uuid;
    }

    public boolean isFirstOpen() {
        boolean result;
        int version = 200;
        PackageManager pm = ContextProvider.getContext().getPackageManager();//context为当前Activity上下文
        try {
            PackageInfo pi = pm.getPackageInfo(ContextProvider.getContext().getPackageName(), 0);
            version = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        result = version != global.getInt(KEY_VERSION, 0);
        if (result) {
            global.edit().putInt(KEY_VERSION, version).apply();
        }
        return result;
    }
}
