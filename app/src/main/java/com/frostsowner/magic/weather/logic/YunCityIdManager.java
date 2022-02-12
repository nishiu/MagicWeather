package com.frostsowner.magic.weather.logic;

import com.fm.commons.logic.BeanFactory;
import com.fm.commons.util.ApkResources;
import com.fm.commons.util.ResultUtil;
import com.frostsowner.magic.weather.domain.weather.YunCity;
import com.frostsowner.magic.weather.handle.CitySearchHandler;
import com.frostsowner.magic.weather.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class YunCityIdManager {

    private List<YunCity> cities;
    private List<String> provinceList;
    private CitySearchHandler citySearchHandler;

    public YunCityIdManager() {
        citySearchHandler = BeanFactory.getBean(CitySearchHandler.class);
        init();
    }

    private void init(){
        InputStream is = null;
        try{
            is = ApkResources.getInputStream("yid_city.json");
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            is.close();
            String data = new String(bytes,"utf-8");
            cities = ResultUtil.getDomain(data,new TypeToken<List<YunCity>>(){}.getType());
            setPickCityData();
            setProvinceList();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(is != null)is.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 省、市、县级的城市名称,返回云易达天气的专有cityId
     * @param cityName
     * @return
     */
    public String getYunCityCode(String cityName){
        if(StringUtils.isEmpty(cityName))return "";
        for(int i = 0; i < cities.size();i++){
            YunCity item = cities.get(i);
            if(item != null && (item.getLeaderZh().contains(cityName)||item.getCityZh().equals(cityName))){
                return item.getId();
            }
        }
        return "";
    }

    /**
     * 精确到县级、返回云易达天气的专有cityId
     * @param city
     * @param district
     * @return
     */
    public String getYunCityCode(String city, String district){
        if(StringUtils.isEmpty(city))return "";
        if(StringUtils.isEmpty(district)){
            return getYunCityCode(city);
        }
        List<YunCity> specialData = new LinkedList<>();
        for(int i = 0; i < cities.size();i++){
            YunCity item = cities.get(i);
            if(item.getLeaderZh().contains(city)||item.getCityZh().equals(city)){
                specialData.add(item);
            }
        }
        if(specialData.size() > 0){
            for(int i = 0; i < specialData.size();i++){
                YunCity item = specialData.get(i);
//                Log.d("ijimu","遍历 : cityId : "+data[0]+" , 区县 : "+district+" , "+data[3]);
                if(item.getCityZh().equals(district)||item.getCityZh().contains(district)){
//                    Log.d("ijimu", "选中了 : "+data[3]+" , cityId : "+data[0]);
                    return item.getId();
                }
            }
            return specialData.get(0).getId();
        }
        return "";
    }

    private void setPickCityData(){
        if(cities == null || cities.size() == 0)return;
        citySearchHandler.setCityId(cities);
    }

    private void setProvinceList(){
        if(cities == null || cities.size() == 0)return;
        if(provinceList == null)provinceList = new LinkedList<>();
        for(YunCity yunCity:cities){
            if(yunCity != null && !provinceList.contains(yunCity.getProvinceZh())){
                provinceList.add(yunCity.getProvinceZh());
            }
        }
    }

    public List<String> getCityList(String province){
        if(cities == null || cities.size() == 0)return null;
        List<String> cityList = new LinkedList<>();
        for(YunCity item:cities){
            if(item != null && province.equals(item.getProvinceZh())){
                if(!cityList.contains(item.getLeaderZh())){
                    cityList.add(item.getLeaderZh());
                }
            }
        }
        return cityList;
    }

    public List<String> getZoneList(String province,String city){
        if(cities == null || cities.size() == 0)return null;
        List<String> zoneList = new LinkedList<>();
        for(YunCity item:cities){
            if(item != null && province.equals(item.getProvinceZh()) && city.equals(item.getLeaderZh())){
                if(!zoneList.contains(item.getCityZh())){
                    zoneList.add(item.getCityZh());
                }
            }
        }
        return zoneList;
    }

    public List<String> getProvinceList(){
        return provinceList;
    }
}
