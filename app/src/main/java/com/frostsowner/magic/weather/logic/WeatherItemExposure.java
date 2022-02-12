package com.frostsowner.magic.weather.logic;

public class WeatherItemExposure {

    private boolean[] items;

    public void release(){
        items = null;
    }

    public void init(){
        if(items == null)
        items = new boolean[11];
        for(boolean item : items){
            item = false;
        }
    }

    public boolean[] getData(){
        return items;
    }

    public boolean exposure(int position){
        if(items == null)return false;
        if(position < 0 || position >= items.length)return false;
        if(!items[position]){
            items[position] = true;
            return true;
        }
        return false;
    }
}
