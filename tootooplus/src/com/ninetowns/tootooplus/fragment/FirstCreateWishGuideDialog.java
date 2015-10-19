package com.ninetowns.tootooplus.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.ui.widget.dialog.BaseFragmentDialog;

/**
 * 
 * @ClassName: FirstCreateWishGuideDialog
 * @Description: 创建心愿的引导页
 * @author wuyulong
 * @date 2015-4-29 上午9:39:10
 * 
 */
public class FirstCreateWishGuideDialog extends BaseFragmentDialog {
	private View firstGuideCreateWishView;
	@ViewInject(R.id.rl_dismiss)
	private RelativeLayout mRLDismiss;
	public FirstCreateWishGuideDialog() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		firstGuideCreateWishView = inflater.inflate(
				R.layout.first_guide_create_wish, null);
		ViewUtils.inject(this, firstGuideCreateWishView);
		return firstGuideCreateWishView;
	}

	@OnClick(R.id.rl_dismiss)
	public void dismissView(View v) {
		dismiss();
		SharedPreferenceHelper.setNoFirstGuideCreateWish(getActivity());
	}
}
