package com.ninetowns.tootooplus.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.bean.PriorityCodeBean;
import com.ninetowns.tootooplus.util.ShareUtils;

public class NotUsedPriorityAdapter extends BasePriorityCodeAdapter {

	public NotUsedPriorityAdapter(Activity mContext,
			List<PriorityCodeBean> mPriorityCodeBeans) {
		super(mContext, mPriorityCodeBeans);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 if(null==convertView){
			 convertView=View.inflate(mContext, R.layout.item_notused_prioritycode, null);
		 }
		 TextView tv_priortyCode=ViewHolder.get(convertView, R.id.notused_prioritycode_text);
		 Button btn_present=ViewHolder.get(convertView, R.id.notused_btn_present);
		 
		 final PriorityCodeBean priorityCodeBean=mPriorityCodeBeans.get(position);
		 if(null!=priorityCodeBean){
			 tv_priortyCode.setText(priorityCodeBean.PriorityCodeId);
			 btn_present.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ShareUtils.shareYXM(mContext, "我的优先码"+priorityCodeBean.PriorityCodeId+"快来使用吧");
				}
			});
		 }
		return convertView;
	}

}
