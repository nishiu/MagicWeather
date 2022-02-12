package com.frostsowner.magic.weather.utils;

public class AddressUtils {

    public static String detailsAddress(String fullAddress,String city){
        if(StringUtils.isEmpty(fullAddress)){
            if(StringUtils.isEmpty(city))return "--";
            else return city;
        }
        String detailsAddress = fullAddress;
        int end = fullAddress.lastIndexOf(city);
        if(end == -1){
            detailsAddress = detailsAddress.replace("中国","");
            detailsAddress = detailsAddress.replace(city,"");
            return detailsAddress;
        }else{
            detailsAddress = detailsAddress.substring(end+city.length(),detailsAddress.length());
            return detailsAddress;
        }
    }
}
