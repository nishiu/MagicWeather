package com.frostsowner.magic.weather.net;

import static com.frostsowner.magic.weather.Constant.YIYUNKE_APPID;
import static com.frostsowner.magic.weather.Constant.YIYUNKE_SECRET;

import com.fm.commons.util.DeviceInfoUtils;
import com.frostsowner.magic.weather.base.BaseRequest;

import java.util.HashMap;

/**
 * Created by yulijun on 2017/4/19.
 */

public class WeatherRequestManager extends BaseRequest {

    private static final String YUNYIDA_WEATHER = "https://v0.yiketianqi.com/api?";

    private HashMap<String,String> testHeader;

    public WeatherRequestManager() {
        super();
        testHeader = new HashMap<>();
        initTestHeader();
    }

    private void initTestHeader(){
        testHeader.put("Content-Type","application/x-www-form-urlencoded");
    }


    public String getYunYiDaWeather(String version,String cityId){
        String[] keys = new String[]{"appid","appsecret","version","cityid"};
        Object[] objs = new Object[]{YIYUNKE_APPID,YIYUNKE_SECRET,version,cityId};
        String str = okHttpPoster.get(YUNYIDA_WEATHER+toParams(keys,objs));
        return str;
    }
}
