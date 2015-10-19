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
 * @ClassName: FirstLooknumberGroupDialog
 * @Description:查看招募团员的信息和再次发起组团
 * @author wuyulong
 * @date 2015-4-29 上午9:39:10
 * 
 */
public class FirstLooknumberGroupDialog extends BaseFragmentDialog {
	private View firstGuideLookNumView;
	@ViewInject(R.id.rl_dismiss)
	private RelativeLayout mRLDismiss;
	public FirstLooknumberGroupDialog() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		firstGuideLookNumView = inflater.inflate(
				R.layout.first_guide_looknumbergroup, null);
		ViewUtils.inject(this, firstGuideLookNumView);
		return firstGuideLookNumView;
	}

	@OnClick(R.id.rl_dismiss)
	public void dismissView(View v) {
		dismiss();
		SharedPreferenceHelper.setNoFirstGuideLookNumberGroup(getActivity());
	}
}