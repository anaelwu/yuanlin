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
* @ClassName: FirstCommentCountGetBaiChiDialog 
* @Description:点赞获得白吃机会，在点评详情里面
* @author wuyulong
* @date 2015-8-10 下午1:31:55 
*
 */
public class FirstCommentCountGetBaiChiDialog extends BaseFragmentDialog {
	private View firstGuideDianZan;
	@ViewInject(R.id.rl_dismiss)
	private RelativeLayout mRLDismiss;
	public FirstCommentCountGetBaiChiDialog() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		firstGuideDianZan = inflater.inflate(
				R.layout.first_guide_dianzan, null);
		ViewUtils.inject(this, firstGuideDianZan);
		return firstGuideDianZan;
	}

	@OnClick(R.id.rl_dismiss)
	public void dismissView(View v) {
		dismiss();
		SharedPreferenceHelper.setIsFirstDianzan(getActivity());
	}
}