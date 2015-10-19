package com.ninetowns.tootooplus.fragment;


import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.activity.MyFreeGroupActivity;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.bean.NoticeSuccess;
import com.ninetowns.tootooplus.helper.ConstantsTooTooEHelper;
import com.ninetowns.ui.widget.dialog.BaseFragmentDialog;

public class NoticeSuccessDialog extends BaseFragmentDialog implements
		View.OnClickListener {
	private View noticesuccess;
	@ViewInject(R.id.iv_close)
	private ImageView mIvClose;
	@ViewInject(R.id.lv_notice)
	private ListView mLvNotice;
	@ViewInject(R.id.ll_look)
	private LinearLayout mLLlook;
	private List<NoticeSuccess> listResult;
	public NoticeSuccessDialog(List<NoticeSuccess> listNotice) {
		this.listResult=listNotice;
	}
	public NoticeSuccessDialog() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		noticesuccess = inflater.inflate(R.layout.notice_success, null);
		ViewUtils.inject(this,noticesuccess);
		mIvClose.setOnClickListener(this);
		mLLlook.setOnClickListener(this);
		MyArrayAdapter arrAdapter=new MyArrayAdapter(TootooPlusApplication.getAppContext(), R.layout.item_notice_act, listResult);
		mLvNotice.setAdapter(arrAdapter);	
		arrAdapter.notifyDataSetChanged();
		return noticesuccess;
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_look:
			//我的白吃团
			Intent free_intent = new Intent(getActivity(), MyFreeGroupActivity.class);
			free_intent.putExtra("group_tab", String.valueOf(ConstantsTooTooEHelper.TAB_THREE));
			free_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(free_intent);
			dismiss();
			break;
		case R.id.iv_close:
			dismiss();
			break;

		default:
			break;
		}

	}



	class MyArrayAdapter extends ArrayAdapter<NoticeSuccess> {
		private int resource;
		private LinearLayout newView;

		public MyArrayAdapter(Context context, int resource,
				List<NoticeSuccess> objects) {
			super(context, resource, objects);
			this.resource = resource;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder=null;
			NoticeSuccess item = getItem(position);
			String strContent = item.getContent();
			if (convertView == null) {
				String inflater = Context.LAYOUT_INFLATER_SERVICE;
				LayoutInflater li;
				li = (LayoutInflater) getContext().getSystemService(inflater);
				 holder=new ViewHolder();
				convertView=li.inflate(resource, null);
				 ViewUtils.inject(holder, convertView);
				 convertView.setTag(holder);
			} else {
				holder=(ViewHolder) convertView.getTag();
			}
			String strFH=position+1+".";
			if(!TextUtils.isEmpty(strContent)){
				holder.mCTContent.setText(strFH+strContent);
			}
			
			return convertView;
		}
	}
	private static class ViewHolder{
		@ViewInject(R.id.ct_content)
		public CheckedTextView mCTContent;
		
	}


}