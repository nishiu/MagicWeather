package com.frostsowner.magic.weather;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.Map;
import java.util.Properties;

/**
 * Created by yulijun on 2018/1/8.
 */

public class TrackPlugin {

    public static void track(Context context, String trackId, Map<String,String> parmas){
        Properties properties = new Properties();
        if(parmas != null && parmas.size() > 0){
            for(Map.Entry<String,String> entry : parmas.entrySet()){
                String key = entry.getKey();
                String value = entry.getValue();
                properties.put(key,value);
            }
        }
//        //腾讯MTA移动事件统计
//        StatService.trackCustomKVEvent(context,trackId,properties);
        //友盟移动事件统计
        if(parmas == null){
            MobclickAgent.onEvent(context,trackId,"null");
        }else{
            MobclickAgent.onEvent(context,trackId,parmas);
        }
    }
}
