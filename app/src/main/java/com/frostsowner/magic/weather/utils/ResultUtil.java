package com.frostsowner.magic.weather.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by yulijun on 2017/12/5.
 */

public class ResultUtil {

    private static Gson gson = new Gson();

    public static <T> T getResult (String json,String key){
        Type type = new TypeToken<Map<String,Object>>(){}.getType();
        Map<String,Object> map = gson.fromJson(json,type);
        Object obj = map.get(key);
        return ((T)obj);
    }

    public static <T> T getDomain(String json,Type type){
        T t = gson.fromJson(json,type);
        return t;
    }

    public static String toJson(Object object){
        return gson.toJson(object);
    }
}
