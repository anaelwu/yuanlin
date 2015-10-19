package com.ninetowns.tootooplus.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.fragment.FirstViewPagerGuideDialog.OnViewPagerDialogStatus;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.ui.widget.dialog.BaseFragmentDialog;

/**
 * 
* @ClassName: FirstGuideFaceToFace 
* @Description: 面对面提示
* @author wuyulong
* @date 2015-7-17 下午5:26:53 
*
 */
public class FirstGuideFaceToFace extends BaseFragmentDialog {
	private View first_guide_face_to_face;
	@ViewInject(R.id.rl_dismiss)
	private RelativeLayout mRLDismiss;
	@ViewInject(R.id.fl_act)
	private FrameLayout mFLACT;
	private boolean isRight;
	public FirstGuideFaceToFace() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		first_guide_face_to_face = inflater.inflate(
				R.layout.first_guide_face_to_face, null);
		ViewUtils.inject(this, first_guide_face_to_face);

		return first_guide_face_to_face;
	}
	

	@OnClick(R.id.rl_dismiss)
	public void dismissView(View v) {
		dismiss();
		SharedPreferenceHelper.setNoFirstGuideGuideFaceToFace(getActivity());
		if(onDialogStatus!=null){
			onDialogStatus.onDialogStatusListener(true);
		}
	}
	
	public interface OnFaceToFaceDialogStatus{
		public void onDialogStatusListener(boolean isDismiss);
		
	}
	private OnFaceToFaceDialogStatus onDialogStatus;
	
	public void setOnDialogStatus(OnFaceToFaceDialogStatus onDialogStatus){
		this.onDialogStatus=onDialogStatus;
		
	}
}