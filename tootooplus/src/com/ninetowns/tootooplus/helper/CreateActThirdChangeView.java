package com.ninetowns.tootooplus.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.ninetowns.tootooplus.R;

public class CreateActThirdChangeView extends LinearLayout {
	
	private LayoutInflater changeLayoutInflater = null;

	public CreateActThirdChangeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		changeLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		initView("");
	}
	
	
	public void initView(String act_type){
		
		removeAllViews();
		View view = null;
		if(act_type.equals("") || act_type.equals("0")){
			view = changeLayoutInflater.inflate(R.layout.create_act_online_change_view, null);
		} else {
			view = changeLayoutInflater.inflate(R.layout.create_act_outline_change_view, null);
		}
		addView(view);
	}

	
}
