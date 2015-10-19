package com.ninetowns.tootooplus.adapter;

import java.util.List;

import android.app.Activity;
import android.view.ViewGroup.LayoutParams;

import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.bean.HomePageBean;

public class BigOrSmallEateAdapter extends HomePageAdapter {

	public BigOrSmallEateAdapter(Activity activity,
			List<HomePageBean> homePageListBean) {
		super(activity, homePageListBean);
	}
/**
 * 设置顶部的高度
 */
	protected void setMarginTop(int position,
			HomePageAdatperHolder homePageAdatperHolder) {
		LayoutParams layoutParView = homePageAdatperHolder.vMaragin
				.getLayoutParams();
		if (layoutParView != null) {
			float nofirstMarginTop = TootooPlusApplication.getAppContext()
					.getResources()
					.getDimension(R.dimen.activity_horizontal_margin);//15
			float firstMarginTop = TootooPlusApplication.getAppContext()
					.getResources()
					.getDimension(R.dimen.button_horizontal_margin);
		
			
//			int noFirstMarTop = UIUtils.px2dip(nofirstMarginTop);
//			int firstMarTop = UIUtils.px2dip(firstMarginTop);
			if (position == 0) {
				layoutParView.height = (int) firstMarginTop;
			} else {
				layoutParView.height = (int) nofirstMarginTop;
			}
			homePageAdatperHolder.vMaragin.setLayoutParams(layoutParView);

		}
	}

}
