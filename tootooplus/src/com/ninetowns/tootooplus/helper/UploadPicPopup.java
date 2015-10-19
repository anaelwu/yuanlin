package com.ninetowns.tootooplus.helper;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.ninetowns.tootooplus.R;

public class UploadPicPopup extends PopupWindow {
	
	private LayoutInflater layoutInflater = null;
	
	private int screen_width;
	
	private int screen_height;
	
	private LinearLayout all_trans_layout;
	
	public interface CameraAndLocalListener{
		
		public void onCamera();
		
		public void onLocal();
	}
	
	
	private CameraAndLocalListener cameraAndLocalListener;
	
	public UploadPicPopup(Context context, int screen_width, int screen_height, LinearLayout all_trans_layout){
		super(context);
		
		layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.screen_width = screen_width;
		this.screen_height = screen_height;
		this.all_trans_layout = all_trans_layout;

		setFocusable(true);
		setOutsideTouchable(true);
		setBackgroundDrawable(new ColorDrawable(0));
		// 窗口进入和退出的效果
		setAnimationStyle(R.style.win_ani_top_bottom);
		
		addView(R.layout.upload_pic_popup_layout);
	}
	
	
	public void addView(int root_id){
		
		View rootView = layoutInflater.inflate(root_id, null);
		setContentView(rootView);
		
		RelativeLayout.LayoutParams transLayoutParams = (RelativeLayout.LayoutParams)all_trans_layout.getLayoutParams();
		transLayoutParams.width = screen_width;
		transLayoutParams.height = screen_height;
		all_trans_layout.setLayoutParams(transLayoutParams);
		all_trans_layout.setBackgroundResource(R.color.join_black_trans);
		all_trans_layout.setVisibility(View.VISIBLE);
		
		// 窗口的宽带和高度根据情况定义
		setWidth(screen_width * 9 / 10);
		
		setHeight(screen_height / 4);
		// 位置可以按要求定义
		showAtLocation(rootView, Gravity.NO_GRAVITY, screen_width / 20, screen_height * 3 / 4 - 15);

		//把整个布局的高度设置的跟popupWindow的高度一样
		LinearLayout upload_pic_layout = (LinearLayout)rootView.findViewById(R.id.upload_pic_layout);
		LinearLayout.LayoutParams lLayoutParams = (LinearLayout.LayoutParams)upload_pic_layout.getLayoutParams();
		lLayoutParams.height = screen_height / 4;
		upload_pic_layout.setLayoutParams(lLayoutParams);
		
		LinearLayout upload_pic_camera_layout = (LinearLayout)rootView.findViewById(R.id.upload_pic_camera_layout);
		upload_pic_camera_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//拍照				
				if(cameraAndLocalListener != null){
					cameraAndLocalListener.onCamera();
				}
				dismiss();
			}
		});
		
		
		LinearLayout upload_pic_local_layout = (LinearLayout)rootView.findViewById(R.id.upload_pic_local_layout);
		upload_pic_local_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//图库选择
				if(cameraAndLocalListener != null){
					cameraAndLocalListener.onLocal();
				}
				
				dismiss();
			}
		});
		
		//取消
		LinearLayout upload_pic_cancel_layout = (LinearLayout)rootView.findViewById(R.id.upload_pic_cancel_layout);
		upload_pic_cancel_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
	}
	
	public void setCameraAndLocalListener(CameraAndLocalListener cameraAndLocalListener){
		this.cameraAndLocalListener = cameraAndLocalListener;
	}


	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
		all_trans_layout.setVisibility(View.GONE);
	}

}
