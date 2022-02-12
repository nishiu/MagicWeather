package com.frostsowner.magic.weather.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.frostsowner.magic.weather.base.BaseFragment;
import com.frostsowner.magic.weather.fragment.ConditionFragment;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yulijun on 2017/10/24.
 */

public class ConditionDetailsPagerAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private List<Fragment> fragments;

    public ConditionDetailsPagerAdapter(FragmentManager fm, Context context) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
        initData();
    }

    private void initData(){
        List<Fragment> list = new LinkedList<>();
        list.add(new ConditionFragment());
//        if(Constant.SHOW_AD){
//            list.add(NewsFragment.newInstance(100));
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

    public void restoreHomeFragment(BaseFragment savedFragment) {
        fragments.set(0, savedFragment);
    }

    public void replaceFragment(int index, Fragment target){
        if(index >= fragments.size() || index < 0)return;
        List<Fragment> temp = new LinkedList<>(fragments);
        temp.remove(index);
        temp.add(index, target);
        setFragments(temp);
    }
}
