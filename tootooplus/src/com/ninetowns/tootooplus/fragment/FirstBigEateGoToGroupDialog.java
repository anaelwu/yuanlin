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
 * @ClassName: FirstGoToGroupDialog
 * @Description:大白吃去组团
 * @author wuyulong
 * @date 2015-4-29 上午9:39:10
 * 
 */
public class FirstBigEateGoToGroupDialog extends BaseFragmentDialog {
	private View firstGuideGotoGroupView;
	@ViewInject(R.id.rl_dismiss)
	private RelativeLayout mRLDismiss;
	public FirstBigEateGoToGroupDialog() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		firstGuideGotoGroupView = inflater.inflate(
				R.layout.first_guide_biggotogroup, null);
		ViewUtils.inject(this, firstGuideGotoGroupView);
		return firstGuideGotoGroupView;
	}

	@OnClick(R.id.rl_dismiss)
	public void dismissView(View v) {
		dismiss();
		SharedPreferenceHelper.setNoFirstGuideGoToGroup(getActivity());
	}
}