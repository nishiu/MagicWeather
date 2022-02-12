package com.frostsowner.magic.weather.logic;

import static com.frostsowner.magic.weather.Constant.YUN_LOCAL_DATA;

import android.util.Log;

import com.fm.commons.logic.BeanFactory;
import com.fm.commons.logic.LocalStorage;
import com.frostsowner.magic.weather.domain.weather.YunWeather;
import com.frostsowner.magic.weather.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class CityManager {

    private List<YunWeather> cityWeatherList;

    private LocalStorage localStorage;
    private YunCityIdManager yunCityIdManager;
    private Gson gson;
    private int currentCityIndex;

    public CityManager() {
        localStorage = BeanFactory.getBean(LocalStorage.class);
        yunCityIdManager = BeanFactory.getBean(YunCityIdManager.class);
        cityWeatherList = new LinkedList<>();
        gson = new Gson();
        initLocalData();
    }

    private void initLocalData(){
        String mjLocalJson = localStorage.get(YUN_LOCAL_DATA,"");
        if(!StringUtils.isEmpty(mjLocalJson)){
            Type type = new TypeToken<List<YunWeather>>(){}.getType();
            cityWeatherList = gson.fromJson(mjLocalJson,type);
        }
    }

    public void setCityWeatherList(List<YunWeather> cityWeatherList){
        if(cityWeatherList == null)return;
        this.cityWeatherList = new LinkedList<>(cityWeatherList);
        saveLocalData();
    }

    public YunWeather createData(String city, String district,String address,String semaAptag,boolean isLocate){
        YunWeather cityWeather = new YunWeather();
        cityWeather.setCity(city);
        cityWeather.setCityName(city+district);
        cityWeather.setLocate(isLocate);
        cityWeather.setDistrict(district);
        cityWeather.setAddress(address);
        cityWeather.setSemaAptag(semaAptag);
        cityWeather.setCityId(yunCityIdManager.getYunCityCode(city,district));
        return cityWeather;
    }

    public boolean isEmpty(String cityName){
        for(YunWeather cityWeather : cityWeatherList){
            if(cityWeather != null && cityName.equals(cityWeather.getCityName())){
                return false;
            }
        }
        return true;
    }

    public YunWeather addData(String city, String district,String address,String semaAptag,boolean isLocate){
        YunWeather  cityWeather = createData(city,district,address,semaAptag,isLocate);
        if(isLocate && cityWeatherList.size() > 0){
            cityWeatherList.set(0,cityWeather);
            Log.e("ijimu","addData set 0 , cityName : "+cityWeather.getCityName());
        }else{
            cityWeatherList.add(cityWeather);
            Log.e("ijimu","addData add , cityName : "+cityWeather.getCityName());
        }
        saveLocalData();
        return cityWeather;
    }

    public YunWeather addData(String city){
        return addData(city,false);
    }

    public YunWeather addData(String city, boolean isLocate){
        return addData(city,"","","",isLocate);
    }

    public void updateData(YunWeather cityWeather){
        if(cityWeather.isLocate()){
            cityWeatherList.set(0,cityWeather);
        }else{
            List<YunWeather> tempList = new LinkedList<>(cityWeatherList);
            for(int i = 0; i < tempList.size();i++){
                YunWeather temp = tempList.get(i);
                if(temp.getCityName().equals(cityWeather.getCityName())){
                    cityWeatherList.set(i,cityWeather);
                }
            }
        }
        saveLocalData();
    }

    public List<YunWeather> getCityWeatherList(){
        return cityWeatherList;
    }

    public YunWeather getTarget(int index){
        if(cityWeatherList == null)return null;
        if(cityWeatherList.size() == 0)return null;
        if(index > cityWeatherList.size()-1)return null;
        return cityWeatherList.get(index);
    }

    public YunWeather getTarget(String cityName){
        if(StringUtils.isEmpty(cityName))return null;
        for(YunWeather cityWeather : cityWeatherList){
            if(cityWeather.getCityName().equals(cityName)){
                return cityWeather;
            }
        }
        return null;
    }

    public int getTargetIndex(String cityName){
        if(StringUtils.isEmpty(cityName))return 0;
        for(int i = 0; i < cityWeatherList.size();i++){
            YunWeather cityWeather = cityWeatherList.get(i);
            if(cityWeather.getCityName().equals(cityName)){
                return i;
            }
        }
        return 0;
    }

    public YunWeather getCurrentCityWeather(){
        return getTarget(currentCityIndex);
    }

    public int getCurrentCityIndex() {
        return currentCityIndex;
    }

    public void setCurrentCityIndex(int currentCityIndex) {
        this.currentCityIndex = currentCityIndex;
    }

    private void saveLocalData(){
        String localJson = gson.toJson(gson.toJson(cityWeatherList));
        localStorage.putString(YUN_LOCAL_DATA,localJson);
    }
}
