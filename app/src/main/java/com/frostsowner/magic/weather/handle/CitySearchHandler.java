package com.frostsowner.magic.weather.handle;

import com.frostsowner.magic.weather.domain.weather.YunCity;

import java.util.LinkedList;
import java.util.List;

public class CitySearchHandler {

    private List<YunCity> cities;

    private List<String> totalCityData;

    public List<String> search(String content){
        return checkResult(content);
    }

    public List<String> checkResult(String content){
        for(int i = 0; i < cities.size();i++){
            YunCity item = cities.get(i);
            if(item.getProvinceZh().contains(content)){
                return getResultByProvince(item.getProvinceZh());
            }
            else if(item.getLeaderZh().contains(content)){
                return getResultByCity(item.getLeaderZh());
            }
            else if(item.getCityZh().contains(content)){
                return getResultByDistrict(item.getCityZh());
            }
        }
        return null;
    }

    public List<String> getResultByProvince(String province){
        List<String> result = new LinkedList<>();
        for(int i = 0; i < cities.size();i++){
            YunCity item = cities.get(i);
            if(item.getProvinceZh().contains(province)||item.getLeaderZh().contains(province)||item.getCityZh().contains(province)){
                if(!result.contains(item.getProvinceZh()))result.add(item.getProvinceZh());
                if(!result.contains(item.getLeaderZh()))result.add(item.getLeaderZh());
                if(!result.contains(item.getCityZh()))result.add(item.getCityZh());
            }
        }
        return result;
    }

    private List<String> getResultByDistrict(String district){
        List<String> result = new LinkedList<>();
        for(int i = 0; i < cities.size();i++){
            YunCity item = cities.get(i);
            if(item.getCityZh().contains(district)){
                if(!result.contains(item.getCityZh()))result.add(item.getCityZh());
            }
        }
        return result;
    }

    private List<String> getResultByCity(String city){
        List<String> result = new LinkedList<>();
        for(int i = 0; i < cities.size();i++){
            YunCity item = cities.get(i);
            if(item.getLeaderZh().contains(city)||item.getCityZh().contains(city)){
                if(!result.contains(item.getLeaderZh()))result.add(item.getLeaderZh());
                if(!result.contains(item.getCityZh()))result.add(item.getCityZh());
            }
        }
        return result;
    }

    public void setCityId(List<YunCity> cities){
        this.cities = cities;
        totalCityData = new LinkedList<>();
        for(int i = 0; i < cities.size();i++){
            YunCity yunCity = cities.get(i);
            if(!totalCityData.contains(yunCity.getProvinceZh()))totalCityData.add(yunCity.getProvinceZh());
            if(!totalCityData.contains(yunCity.getLeaderZh()))totalCityData.add(yunCity.getLeaderZh());
            if(!totalCityData.contains(yunCity.getCityZh()))totalCityData.add(yunCity.getCityZh());
        }
    }

    public List<String> getTotalCityData(){
        return totalCityData;
    }
}
