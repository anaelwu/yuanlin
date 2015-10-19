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
* @ClassName: FirstDetailFaceToFaceGuideDialog 
* @Description: 活动详情中的面对面邀请
* @author wuyulong
* @date 2015-7-21 下午1:52:44 
*
 */
public class FirstDetailFaceToFaceGuideDialog extends BaseFragmentDialog {
	private View firstGuideDetailFaceToFace;
	@ViewInject(R.id.rl_dismiss)
	private RelativeLayout mRLDismiss;
	public FirstDetailFaceToFaceGuideDialog() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		firstGuideDetailFaceToFace= inflater.inflate(
				R.layout.first_guide_detail_act_facetoface_wish, null);
		ViewUtils.inject(this, firstGuideDetailFaceToFace);
		return firstGuideDetailFaceToFace;
	}

	@OnClick(R.id.rl_dismiss)
	public void dismissView(View v) {
		dismiss();
		SharedPreferenceHelper.setNoFirstGuideFaceToFaceDetailAct(getActivity());
	}
}
