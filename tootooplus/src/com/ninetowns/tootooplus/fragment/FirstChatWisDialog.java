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
import com.ninetowns.tootooplus.fragment.FirstViewPagerGuideDialog.OnViewPagerDialogStatus;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.ui.widget.dialog.BaseFragmentDialog;

/**
 * 
 * @ClassName: FirstChatWisDialog
 * @Description: 查看心愿群聊引导页
 * @author wuyulong
 * @date 2015-4-29 上午9:39:10
 * 
 */
public class FirstChatWisDialog extends BaseFragmentDialog {
	private View firstGuideWisView;
	@ViewInject(R.id.rl_dismiss)
	private RelativeLayout mRLDismiss;
	public FirstChatWisDialog() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		firstGuideWisView = inflater.inflate(
				R.layout.first_guide_wis_chat, null);
		ViewUtils.inject(this, firstGuideWisView);
		return firstGuideWisView;
	}

	@OnClick(R.id.rl_dismiss)
	public void dismissView(View v) {
		dismiss();
		SharedPreferenceHelper.setNoFirstGuideWisChat(getActivity());
		if(onDialogStatus!=null){
			onDialogStatus.onDialogWisStatusListener(true);
		}
	}
	
	public interface OnWisDialogStatus{
		public void onDialogWisStatusListener(boolean isDismiss);
		
	}
	private OnWisDialogStatus onDialogStatus;
	
	public void setOnDialogStatus(OnWisDialogStatus onDialogStatus){
		this.onDialogStatus=onDialogStatus;
		
	}
}
