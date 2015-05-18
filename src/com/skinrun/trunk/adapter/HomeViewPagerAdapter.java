package com.skinrun.trunk.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HomeViewPagerAdapter extends FragmentPagerAdapter {
	
	private ArrayList<Fragment> frags;
	

	public HomeViewPagerAdapter(FragmentManager fm,ArrayList<Fragment> frags) {
		super(fm);
		this.frags=frags;
	}

	@Override
	public Fragment getItem(int index) {
		return frags.get(index);
	}

	@Override
	public int getCount() {
		
		if(frags==null||frags.size()<=0){
			return 0;
		}
		return frags.size();
	}

}
