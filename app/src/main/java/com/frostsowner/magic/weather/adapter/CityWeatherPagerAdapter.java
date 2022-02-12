package com.frostsowner.magic.weather.adapter;


import android.annotation.SuppressLint;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class CityWeatherPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments;

    private int currentPosition;

    @SuppressLint("WrongConstant")
    public CityWeatherPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        setFragments(fragments);
    }

    @SuppressLint("WrongConstant")
    public CityWeatherPagerAdapter(FragmentManager fm) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public void setCurrentPosition(int position){
        this.currentPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    public void setFragments(List<Fragment> fragments){
        if(fragments == null)return;
        this.fragments = fragments;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(fragments == null)return 0;
        return fragments.size();
    }
}
