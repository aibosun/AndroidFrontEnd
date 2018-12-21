package com.usc.aibo.eventsearchmbl;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public  class SectionMainPageAdapter extends FragmentPagerAdapter{
    private  List<Fragment> fragList =new ArrayList<>();
    private final List<String> fragTitleList = new ArrayList<>();



    public SectionMainPageAdapter(FragmentManager fm) {

        super(fm);

    }

     public void addFragment(Fragment frg, String title){
         fragList.add(frg);
         fragTitleList.add(title);
     }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragTitleList.get(position);
    }
    @Override
    public Fragment getItem(int i) {
        return fragList.get(i);
    }

    @Override
    public int getCount() {
        return fragList.size();
    }
}
