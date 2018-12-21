package com.usc.aibo.eventsearchmbl;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public  class SectionDetailPageAdapter extends FragmentPagerAdapter{
    private final List<Fragment> fragList = new ArrayList<>();
    private final List<String> fragTitleList = new ArrayList<>();



    public SectionDetailPageAdapter(FragmentManager fm) {
        super(fm);
    }

     public void addFragment(int posi,Fragment frg, String title){
         fragList.add(posi,frg);
         fragTitleList.add(posi,title);
     }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragTitleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return fragList.get(position);
    }

    @Override
    public int getCount() {
        return fragList.size();
    }
}
