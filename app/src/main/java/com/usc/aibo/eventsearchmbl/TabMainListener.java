package com.usc.aibo.eventsearchmbl;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.Fragment;

public class TabMainListener implements  ActionBar.TabListener{
    Fragment fragment;

    public TabMainListener(Fragment fragment){
        this.fragment = fragment;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        ft.replace(R.id.container,fragment);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        ft.remove(fragment);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
