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
 * @ClassName: FirstGetGroupMembersDialog
 * @Description: 完成组团的信息提交组团的提示
 * @author wuyulong
 * @date 2015-4-29 上午9:39:10
 * 
 */
public class FirstGetGroupMembersDialog extends BaseFragmentDialog {
	private View firstGetGroupMembersView;
	@ViewInject(R.id.rl_dismiss)
	private RelativeLayout mRLDismiss;
	public FirstGetGroupMembersDialog() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		firstGetGroupMembersView = inflater.inflate(
				R.layout.first_members_guide, null);
		ViewUtils.inject(this, firstGetGroupMembersView);
		return firstGetGroupMembersView;
	}

	@OnClick(R.id.rl_dismiss)
	public void dismissView(View v) {
		dismiss();
		SharedPreferenceHelper.setNoFirstGuideGetMembersGroup(getActivity());
	}
}
