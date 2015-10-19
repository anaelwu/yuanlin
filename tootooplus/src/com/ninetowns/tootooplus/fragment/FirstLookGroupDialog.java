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
 * @ClassName: FirstLookGroupDialog
 * @Description: 查看组团信息
 * @author wuyulong
 * @date 2015-4-29 上午9:39:10
 * 
 */
public class FirstLookGroupDialog extends BaseFragmentDialog {
	private View firstGuideLookGroupView;
	@ViewInject(R.id.rl_dismiss)
	private RelativeLayout mRLDismiss;
	public FirstLookGroupDialog() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		firstGuideLookGroupView = inflater.inflate(
				R.layout.first_guide_look_group, null);
		ViewUtils.inject(this, firstGuideLookGroupView);
		return firstGuideLookGroupView;
	}

	@OnClick(R.id.rl_dismiss)
	public void dismissView(View v) {
		dismiss();
		SharedPreferenceHelper.setNoFirstGuideLookGroup(getActivity());
	}
}
