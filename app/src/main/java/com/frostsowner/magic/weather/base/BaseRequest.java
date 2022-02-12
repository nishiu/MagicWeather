package com.frostsowner.magic.weather.base;

import android.util.Log;

import com.fm.commons.http.NetworkCheckInterceptor;
import com.fm.commons.http.OkHttpPoster;
import com.fm.commons.util.ApkResources;
import com.fm.commons.util.StringUtils;
import com.frostsowner.magic.weather.impl.DebugLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class BaseRequest implements DebugLogger {

    protected OkHttpPoster okHttpPoster;
    protected Map<String,String> headers;
    protected boolean useTestServer;

    public BaseRequest(){
        okHttpPoster = new OkHttpPoster();
        okHttpPoster.setEncoding(false);
        okHttpPoster.setOkhttpClient(build(60000));
        okHttpPoster.setDebug(false);
        setUseTestServer(false);
        headers = new HashMap<>();
        initHeaders();
    }

    protected void initHeaders(){
        headers.put("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
    }

    protected OkHttpClient build(long timeout){
        return new OkHttpClient.Builder().
               readTimeout(timeout, TimeUnit.SECONDS).
               connectTimeout(timeout,TimeUnit.SECONDS).
               writeTimeout(timeout, TimeUnit.SECONDS).
               addInterceptor(new NetworkCheckInterceptor()).
//               addInterceptor(new RequestLogInterceptor()).
        build();
    }

    protected Map<String,Object> toMap(String[] keys,Object[] objects){
        Map<String,Object> data = new TreeMap<>();
        for(int i = 0; i < keys.length;i++){
            data.put(keys[i],objects[i]);
        }
        return data;
    }

    protected String getSignature(String[] keys,Object[] objs,String salt){
        StringBuffer buffer = new StringBuffer();
        Map<String,Object> parmas = toMap(keys,objs);
        for(Map.Entry<String,Object> entry:parmas.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            buffer.append(key+value);
            logInfo("key = "+key+" , value = "+value);
        }
        Log.d("ijimu","salt = "+salt);
        logInfo("salt = "+salt);
        buffer.append(salt);
        logInfo("signature = "+buffer.toString());
        String signatureMD5 = StringUtils.getMD5(buffer.toString());
        logInfo("signature to md5 = "+signatureMD5);
        return signatureMD5;
    }

    public String toParams(String[] keys,Object[] objects){
        StringBuffer buffer = new StringBuffer();
        for(int i = 0;i < keys.length;i++){
            buffer.append(keys[i]+"="+objects[i]+"&");
        }
        return buffer.toString();
    }

    public boolean isUseTestServer() {
        return useTestServer;
    }

    public void setUseTestServer(boolean useTestServer) {
        this.useTestServer = useTestServer;
    }

    @Override
    public void logInfo(String msg) {
        if(ApkResources.isDebug()){
            Log.d("request",msg);
        }
    }
}
