package com.frostsowner.magic.weather;

import static com.frostsowner.magic.weather.Constant.UM_AD_FLAG_KEY;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.fm.commons.util.ApkResources;
import com.fm.commons.util.SystemUtils;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.umeng.cconfig.RemoteConfigSettings;
import com.umeng.cconfig.UMRemoteConfig;
import com.umeng.cconfig.listener.OnConfigStatusChangedListener;
import com.umeng.commonsdk.UMConfigure;

import java.util.HashMap;
import java.util.Map;

public class UMConfig {

    public static void preInit(Context context){
        initRemoteConfig();
        initPlatformData(context);
        UMConfigure.preInit(context,"5b0659388f4a9d6d42000012",Constant.CHANNEL);
        UMConfigure.setEncryptEnabled(true);
        UMConfigure.setLogEnabled(true);
        UMConfigure.setProcessEvent(true);
        MobclickAgent.setSessionContinueMillis(1000*10);
        String[] msgList = UMConfigure.getTestDeviceInfo(context);
        UMConfigure.getOaid(context, s -> {
            ApkResources.setOaid(s);
            Log.d("ijimu","get oaid : "+s);
        });
        ApkResources.setDeviceId(msgList[0]);
        Log.d("ijimu","get deviceId : "+msgList[0]);
        if(ApkResources.isDebug(context)){
            Map<String,String> device = new HashMap<>();
            device.put("mac",msgList[1]);
            device.put("device_id",msgList[0]);
            String deviceInfo = new Gson().toJson(device);
            Log.d("ijimu",deviceInfo);
            Log.d("ijimu", SystemUtils.getSystemModel());
            Log.d("ijimu",SystemUtils.getDeviceInfo());
        }
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_AUTO);
    }

    public static void init(Application context){
        initRemoteConfig();
        UMConfigure.init(context,UMConfigure.DEVICE_TYPE_PHONE,"4117bb3792cd3708dbb1a67b4fd14266");
        UMConfigure.setEncryptEnabled(true);
        UMConfigure.setLogEnabled(true);
        UMConfigure.setProcessEvent(true);
        MobclickAgent.setSessionContinueMillis(1000*10);
        initPlatformData(context);
        String[] msgList = UMConfigure.getTestDeviceInfo(context);
        UMConfigure.getOaid(context, s -> {
            ApkResources.setOaid(s);
            Log.d("ijimu","get oaid : "+s);
        });
        ApkResources.setDeviceId(msgList[0]);
        Log.d("ijimu","get deviceId : "+msgList[0]);
        if(ApkResources.isDebug(context)){
            Map<String,String> device = new HashMap<>();
            device.put("mac",msgList[1]);
            device.put("device_id",msgList[0]);
            String deviceInfo = new Gson().toJson(device);
            Log.d("ijimu",deviceInfo);
            Log.d("ijimu", SystemUtils.getSystemModel());
            Log.d("ijimu",SystemUtils.getDeviceInfo());
        }
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_AUTO);
    }

    private static void initPlatformData(Context context){
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            String value = info.metaData.getString("UMENG_CHANNEL");
            Log.d("ijimu","init platform name : "+value);
            Constant.CHANNEL = value;
//            CrashReport.UserStrategy userStrategy = new CrashReport.UserStrategy(context);
//            userStrategy.setAppChannel(value);
//            Constant.PUSH_CHANEL = value;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void initRemoteConfig(){
        final UMRemoteConfig umRemoteConfig = UMRemoteConfig.getInstance();
        RemoteConfigSettings.Builder remoteConfigSettingBuilder = new RemoteConfigSettings.Builder();
        remoteConfigSettingBuilder.setAutoUpdateModeEnabled(true);
        umRemoteConfig.setConfigSettings(remoteConfigSettingBuilder.build());
//        umRemoteConfig.setDefaults(R.xml.ad_detault);
        umRemoteConfig.setOnNewConfigfecthed(new OnConfigStatusChangedListener(){
            @Override
            public void onFetchComplete(){
                Log.d("ijimu","um remote config onFetchComplete");
                umRemoteConfig.activeFetchConfig();
            }

            @Override
            public void onActiveComplete(){
                Log.d("ijimu","um remote config onActiveComplete");
                Log.d("ijimu","um remote config value : "+umRemoteConfig.getConfigValue(UM_AD_FLAG_KEY));
            }
        });
    }
}
