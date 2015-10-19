package com.ninetowns.tootoopluse.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import com.custom.vg.list.CustomListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.adapter.CitysLayoutAdapter;
import com.ninetowns.tootoopluse.bean.ActivityDetailBean;
import com.ninetowns.tootoopluse.bean.ActivityInfoBean;
import com.ninetowns.tootoopluse.bean.PostAresBean;
import com.ninetowns.ui.widget.text.MarqueeTextView;

/**
 * 
 * @ClassName: OnLineActivityFragment
 * @Description: 线上活动界面
 * @author wuyulong
 * @date 2015-3-13 上午11:19:23
 * 
 */
@SuppressLint("ValidFragment")
public class OnLineActivityFragment extends Fragment implements com.custom.vg.list.OnItemClickListener{

	// R.id.ct_categray_name
	// R.id.ct_time_name
	// R.id.custom_listView
	// R.id.ct_man_name
	// R.id.ct_postage_name
	@ViewInject(R.id.ct_type_name)
	private CheckedTextView mCTType;// 类型
	@ViewInject(R.id.ct_categray_name)
	private CheckedTextView mCTCateGory;// 分类
	@ViewInject(R.id.ct_sing_time_over)
	private MarqueeTextView mCTTime;// 报名结束时间
	@ViewInject(R.id.ct_sign_up_time)
	private MarqueeTextView mCTStarTime;// 报名开始时间
	@ViewInject(R.id.custom_listView)
	private CustomListView mCustListView;// 城市列表
	@ViewInject(R.id.ct_man_name)
	private CheckedTextView mCTCount;// 人数
	@ViewInject(R.id.ct_postage_name)
	private CheckedTextView mCTPostage;// 邮费
	@ViewInject(R.id.ll_bottom)
	private LinearLayout mBottom;
	private ActivityDetailBean resultData;// 返回的数据
	private View myOnLineView;
	private List<PostAresBean> cityList;
	private View currentView;

	@SuppressLint("ValidFragment")
	public OnLineActivityFragment(ActivityDetailBean resultData,View currentView) {
		this.resultData = resultData;
		this.currentView=currentView;
	}
/**
 * 
* @Title: dissMissView 
* @Description: 隐藏
* @param  
* @return   
* @throws
 */
	@OnClick(R.id.ll_bottom)
	public void dissMissView(View v) {
		currentView.setVisibility(View.GONE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		myOnLineView = inflater
				.inflate(R.layout.online_activity_fragment, null);
		ViewUtils.inject(this, myOnLineView);
		setActivityInfo();
		return myOnLineView;
	}

	/**
	 * 
	 * @Title: setActivityInfo
	 * @Description: 设置活动信息
	 * @param
	 * @return
	 * @throws
	 */
	private void setActivityInfo() {
		if (resultData != null) {
			ActivityInfoBean activityInfoBean = resultData
					.getActivityInfoBean();
			String gcateGory = activityInfoBean.getCategory();
			String type = activityInfoBean.getType();
			String countPartcipant = activityInfoBean.getCountParticipant();
			String startTime = activityInfoBean.getDateRegisterStart();//报名时间
			String endTime = activityInfoBean.getDateRegisterEnd();//截至时间
			String postage = activityInfoBean.getPostage();
			cityList = activityInfoBean.getPostAresList();
			mCTType.setText(type);
			mCTCateGory.setText(gcateGory);
			mCTStarTime.setText("报名开始:"+startTime);
			mCTTime.setText("报名结束:"+endTime);
			mCTCount.setText(countPartcipant);
			mCTPostage.setText(postage);
			if(cityList!=null){
				CitysLayoutAdapter  adapter=new CitysLayoutAdapter(getActivity(), cityList);
				mCustListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
			mCustListView.setDividerHeight(10);
			mCustListView.setDividerWidth(10);
			mCustListView.setOnItemClickListener((com.custom.vg.list.OnItemClickListener) this);

		}

	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		
		
	}

}
