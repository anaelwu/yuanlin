package com.ninetowns.tootooplus.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyMsgFragmentPagerAdapter extends FragmentPagerAdapter {

	private ArrayList<Fragment> mFragList;

	public MyMsgFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> mFragList2) {
		super(fm);
		this.mFragList=mFragList2;
	}

	@Override
	public int getItemPosition(Object object) {
//		if (object instanceof ParentAuthingFragment
//				&& mAuthFragment instanceof SubOneProjectFragment)
//			return POSITION_NONE;
		return POSITION_NONE;
	}
	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return mFragList.get(arg0) ;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFragList==null?0:mFragList.size();
	}

}