package com.frostsowner.magic.weather.adapter;

import android.annotation.SuppressLint;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.frostsowner.magic.weather.fragment.WeatherFragment;

import java.util.LinkedList;
import java.util.List;

public class HomeAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments;

    @SuppressLint("WrongConstant")
    public HomeAdapter(FragmentManager fm) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        initData();
    }

    private void initData(){
        List<Fragment> list = new LinkedList<>();
        list.add(new WeatherFragment());
//        if(Constant.SHOW_AD && !Constant.isEmulator){
//            list.add(new WanYearFragment());
//            list.add(new EmPowerFragment());
//            list.add(new GiftScoreFragment());
//            list.add(new MinFragment());
//        }
        setFragments(list);
    }

    public void setFragments(List<Fragment> fragments){
        if(fragments == null)return;
        this.fragments = fragments;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }


    @Override
    public int getItemPosition(Object object) {
        // TODO Auto-generated method stub
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void restoreHomeFragment(Fragment savedFragment) {
        fragments.set(0, savedFragment);
    }

    public void replaceFragment(int index, Fragment target){
        if(index >= fragments.size() || index < 0)return;
        List<Fragment> temp = new LinkedList<Fragment>(fragments);
        temp.remove(index);
        temp.add(index, target);
        setFragments(temp);
    }
}
