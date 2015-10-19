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
* @ClassName: FirstActChatDialog 
* @Description: 活动白吃的提示
* @author wuyulong
* @date 2015-6-30 下午1:30:38 
*
 */
public class FirstActChatDialog extends BaseFragmentDialog {
	private View firstActChatinfoView;
	@ViewInject(R.id.rl_dismiss)
	private RelativeLayout mRLDismiss;
	@ViewInject(R.id.fl_act)
	private FrameLayout mFLACT;
	private boolean isRight;
	public FirstActChatDialog() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		firstActChatinfoView = inflater.inflate(
				R.layout.first_guide_act_chat, null);
		ViewUtils.inject(this, firstActChatinfoView);
		if(isRight){
			setLayoutParamsRight();
		}
		return firstActChatinfoView;
	}
	public void setLayoutParamsCenter(){
		RelativeLayout.LayoutParams par=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		par.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE); 
		mFLACT.setLayoutParams(par);
	
	}
	public void setLayoutParamsRight(){
		RelativeLayout.LayoutParams par=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		par.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE); 
		mFLACT.setLayoutParams(par);
		
	}
	public void setRight(boolean isRight){
		this.isRight=isRight;
	}
	@OnClick(R.id.rl_dismiss)
	public void dismissView(View v) {
		dismiss();
		SharedPreferenceHelper.setNoFirstGuideActChat(getActivity());
		if(onDialogStatus!=null){
			onDialogStatus.onDialogStatusListener(true);
		}
	}
	
	public interface OnActDialogStatus{
		public void onDialogStatusListener(boolean isDismiss);
		
	}
	private OnActDialogStatus onDialogStatus;
	
	public void setOnDialogStatus(OnActDialogStatus onDialogStatus){
		this.onDialogStatus=onDialogStatus;
		
	}
}