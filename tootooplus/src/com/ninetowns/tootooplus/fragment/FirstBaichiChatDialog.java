package com.ninetowns.tootooplus.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.baidu.cyberplayer.utils.L;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.fragment.FirstViewPagerGuideDialog.OnViewPagerDialogStatus;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.ui.widget.dialog.BaseFragmentDialog;

/**
 * 
 * @ClassName: FirstBaichiChatDialog
 * @Description: 查看白吃团群聊
 * @author wuyulong
 * @date 2015-4-29 上午9:39:10
 * 
 */
public class FirstBaichiChatDialog extends BaseFragmentDialog {
	private View firstGuideBaiChiChatView;
	@ViewInject(R.id.rl_dismiss)
	private RelativeLayout mRLDismiss;
	@ViewInject(R.id.fl_baichi)
	public FrameLayout mFrameBaichi;
	private boolean isLeft;
	public FirstBaichiChatDialog() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		firstGuideBaiChiChatView = inflater.inflate(
				R.layout.first_guide_baichi_chat, null);
		ViewUtils.inject(this, firstGuideBaiChiChatView);
		if(isLeft){
			setLayoutParamsLeft();
		}
		return firstGuideBaiChiChatView;
	}
	public void setLayoutParamsCenter(){
		RelativeLayout.LayoutParams par=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		par.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE); 
		mFrameBaichi.setLayoutParams(par);
		mFrameBaichi.setBackgroundResource(R.drawable.icon_baichituan);
	}
	public void setLayoutParamsLeft(){
		RelativeLayout.LayoutParams par=new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		par.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE); 
		mFrameBaichi.setLayoutParams(par);
		mFrameBaichi.setBackgroundResource(R.drawable.icon_baichi_chat);
	}
	public void setLeft(boolean isLeft){
		isLeft=this.isLeft;
	}

	@OnClick(R.id.rl_dismiss)
	public void dismissView(View v) {
		dismiss();
		SharedPreferenceHelper.setNoFirstGuideBaiChiChat(getActivity());
		if(onDialogStatus!=null){
			onDialogStatus.onDialogBichiStatusListener(true);
		}
	}
	
	public interface OnDialogBaichiStatus{
		public void onDialogBichiStatusListener(boolean isDismiss);
		
	}
	private OnDialogBaichiStatus onDialogStatus;
	
	public void setOnDialogStatus(OnDialogBaichiStatus onDialogStatus){
		this.onDialogStatus=onDialogStatus;
		
	}
}