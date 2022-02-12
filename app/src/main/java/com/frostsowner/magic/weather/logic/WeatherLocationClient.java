package com.frostsowner.magic.weather.logic;

import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.fm.commons.http.ContextHolder;
import com.fm.commons.util.ApkResources;
import com.frostsowner.magic.weather.impl.DebugLogger;
import com.frostsowner.magic.weather.utils.BaiduUtils;
import com.frostsowner.magic.weather.utils.LocationUtils;
import com.frostsowner.magic.weather.utils.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liyu on 2016/11/2.
 */

public class WeatherLocationClient implements DebugLogger {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public interface BDLocationCallBack{
        void locate(BDLocation bdLocation);
        void failed();
    }

    private LocationClient realClient;
    private BDLocationCallBack callBack;

    private static volatile WeatherLocationClient proxyClient;

    private WeatherLocationClient() {
        realClient = new LocationClient(ContextHolder.get());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType(BaiduUtils.CoorType_BD09LL);
        option.setWifiCacheTimeOut(5*60*1000);
//        option.setScanSpan(1100);
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        option.setOpenGps(true);
        //设置百度定位参数
        realClient.setLocOption(option);
        realClient.registerLocationListener(new MyBDLocationListener());
        Log.d("ijimu","baidu sdk version : "+realClient.getVersion());
    }

    private class MyBDLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
//            callBack.locate(bdLocation);
            logInfo("bdLocation : "+bdLocation);
            if(StringUtils.isEmpty(bdLocation.getCity())){
                logInfo("bdLocation is empty");
                logInfo(bdLocation.getLocType()+"");
                logInfo(bdLocation.getLocTypeDescription()+"");
                realClient.requestLocation();
                callBack.failed();
            }else{
                logInfo("当前坐标, 经度: "+bdLocation.getLongitude()+", 纬度: "+bdLocation.getLatitude());
                logInfo("当前城市 : "+bdLocation.getCity());
                logInfo("当前地区 : "+bdLocation.getDistrict());
                logInfo("主要信息 : "+bdLocation.getAddress().street+"\n"+
                        bdLocation.getAddress().streetNumber+"\n"+
                        bdLocation.getSemaAptag());
//                for(int i = 0 ; i < bdLocation.getPoiList().size();i++){
//                    logInfo("poi : "+bdLocation.getPoiList().get(i).getName());
//                }
                callBack.locate(bdLocation);
                stop();
            }
        }

        /**
         * 回调定位诊断信息，开发者可以根据相关信息解决定位遇到的一些问题
         * @param locType 当前定位类型
         * @param diagnosticType 诊断类型（1~9）
         * @param diagnosticMessage 具体的诊断信息释义
         */
        @Override
        public void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage) {
            super.onLocDiagnosticMessage(locType, diagnosticType, diagnosticMessage);
            int tag = 2;
            StringBuffer sb = new StringBuffer(256);
            sb.append("诊断结果: ");
            if (locType == BDLocation.TypeNetWorkLocation) {
                if (diagnosticType == 1) {
                    sb.append("网络定位成功，没有开启GPS，建议打开GPS会更好");
                    sb.append("\n" + diagnosticMessage);
                } else if (diagnosticType == 2) {
                    sb.append("网络定位成功，没有开启Wi-Fi，建议打开Wi-Fi会更好");
                    sb.append("\n" + diagnosticMessage);
                }
            } else if (locType == BDLocation.TypeOffLineLocationFail) {
                if (diagnosticType == 3) {
                    sb.append("定位失败，请您检查您的网络状态");
                    sb.append("\n" + diagnosticMessage);
                }
            } else if (locType == BDLocation.TypeCriteriaException) {
                if (diagnosticType == 4) {
                    sb.append("定位失败，无法获取任何有效定位依据");
                    sb.append("\n" + diagnosticMessage);
                } else if (diagnosticType == 5) {
                    sb.append("定位失败，无法获取有效定位依据，请检查运营商网络或者Wi-Fi网络是否正常开启，尝试重新请求定位");
                    sb.append(diagnosticMessage);
                } else if (diagnosticType == 6) {
                    sb.append("定位失败，无法获取有效定位依据，请尝试插入一张sim卡或打开Wi-Fi重试");
                    sb.append("\n" + diagnosticMessage);
                } else if (diagnosticType == 7) {
                    sb.append("定位失败，飞行模式下无法获取有效定位依据，请关闭飞行模式重试");
                    sb.append("\n" + diagnosticMessage);
                } else if (diagnosticType == 9) {
                    sb.append("定位失败，无法获取任何有效定位依据");
                    sb.append("\n" + diagnosticMessage);
                }
            } else if (locType == BDLocation.TypeServerError) {
                if (diagnosticType == 8) {
                    sb.append("定位失败，请确认您定位的开关打开状态，是否赋予APP定位权限");
                    sb.append("\n" + diagnosticMessage);
                }
            }
            logInfo("msg : "+sb.toString());
            if(callBack != null)callBack.failed();
            realClient.requestLocation();
        }
    }

    public static WeatherLocationClient get() {
        if (proxyClient == null) {
            synchronized (WeatherLocationClient.class) {
                if (proxyClient == null) {
                    proxyClient = new WeatherLocationClient();
                }
            }
        }
        return proxyClient;
    }

    public void locate(BDLocationCallBack callBack) {
        this.callBack = callBack;
        if(!LocationUtils.isLocServiceEnable(ContextHolder.get())){
            callBack.failed();
        }
        if(!realClient.isStarted()){
            realClient.start();
        }
        realClient.requestLocation();
    }

    public BDLocation getLastKnownLocation() {
        return realClient.getLastKnownLocation();
    }

    public void stop() {
        realClient.stop();
    }

    @Override
    public void logInfo(String msg) {
        if(ApkResources.isDebug(ContextHolder.get())){
            logger.info(msg);
        }
    }
}
