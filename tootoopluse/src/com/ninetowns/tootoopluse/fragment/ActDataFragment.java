package com.ninetowns.tootoopluse.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.adapter.ActWishLvAdapter;
import com.ninetowns.tootoopluse.application.TootooPlusEApplication;
import com.ninetowns.tootoopluse.bean.ActWishDataBean;
import com.ninetowns.tootoopluse.bean.ActWishDataItem;
import com.ninetowns.tootoopluse.helper.SharedPreferenceHelper;
import com.ninetowns.tootoopluse.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootoopluse.parser.ActWishDataParser;
import com.ninetowns.ui.fragment.PageListFragment;
import com.ninetowns.ui.widget.refreshable.PullToRefreshBase;
import com.ninetowns.ui.widget.refreshable.RefreshableListView;

public class ActDataFragment extends PageListFragment<ListView, ActWishDataBean, ActWishDataParser> implements OnClickListener{
	private boolean isSetData=false;
	// 总页数
	private int totalPage;
	
	private RefreshableListView act_wish_data_lv;
	
	private TextView data_start_date;
	
	private TextView data_end_date;
	
	private TextView look_data_num_tv;
	
	private TextView look_data_change_tv;
	
	private ActWishDataBean actWishDataBean = null;
	
	private List<ActWishDataItem> actWishList = new ArrayList<ActWishDataItem>();
	
	private LinearLayout data_start_date_layout;
	
	private LinearLayout data_end_date_layout;
	
	@Override
	public void getPageListParserResult(ActWishDataBean parserResult) {
		// TODO Auto-generated method stub
		if(parserResult != null){
			actWishDataBean = parserResult;
			if (super.currentpage == 1) {
				if(!TextUtils.isEmpty(actWishDataBean.getData_StartTimestamp())){
					data_start_date.setText(actWishDataBean.getData_StartTimestamp());
				} else {
					data_start_date.setText("");
				}
				
				if(!TextUtils.isEmpty(actWishDataBean.getData_EndTimestamp())){
					data_end_date.setText(actWishDataBean.getData_EndTimestamp());
				} else {
					data_end_date.setText("");
				}
				
				
				if(!TextUtils.isEmpty(actWishDataBean.getData_Pageviews())){
					look_data_num_tv.setText(actWishDataBean.getData_Pageviews());
				} else {
					look_data_num_tv.setText("");
				}
				
				if(!TextUtils.isEmpty(actWishDataBean.getUserConversion())){
					look_data_change_tv.setText(actWishDataBean.getUserConversion());
				} else {
					look_data_change_tv.setText("");
				}
				
				actWishList.clear();
			}
			if(actWishDataBean.getActWishList() != null && actWishDataBean.getActWishList().size() > 0){
				actWishList.addAll(actWishDataBean.getActWishList());
			}
			
			if(actWishList.size() > 0){
				ActWishLvAdapter actWishLvAdapter = new ActWishLvAdapter(getActivity(), actWishList);
				act_wish_data_lv.setAdapter(actWishLvAdapter);
				if (super.currentpage != 1) {
					act_wish_data_lv.getRefreshableView().setSelection(this.actWishList.size());
				}
				actWishLvAdapter.notifyDataSetChanged();
				
			} else {
				ComponentUtil.showToast(TootooPlusEApplication.getAppContext(), getResources().getString(R.string.http_no_data));
			}
		}
		
	}

	@Override
	protected PullToRefreshBase<ListView> initRefreshIdView() {
		// TODO Auto-generated method stub
		return act_wish_data_lv;
	}

	@Override
	protected View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		isSetData=false;
		View view = inflater.inflate(R.layout.act_wish_data_fragment, null);
		
		act_wish_data_lv = (RefreshableListView)view.findViewById(R.id.act_wish_data_lv);
		
		data_start_date = (TextView)view.findViewById(R.id.data_start_date);
		
		data_end_date = (TextView)view.findViewById(R.id.data_end_date);
		
		look_data_num_tv = (TextView)view.findViewById(R.id.look_data_num_tv);
		
		look_data_change_tv = (TextView)view.findViewById(R.id.look_data_change_tv);
		
		data_start_date_layout = (LinearLayout)view.findViewById(R.id.data_start_date_layout);
		data_start_date_layout.setOnClickListener(this);
		data_end_date_layout = (LinearLayout)view.findViewById(R.id.data_end_date_layout);
		data_end_date_layout.setOnClickListener(this);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		loadData();
		super.onActivityCreated(savedInstanceState);
	}
public void loadData(){
	super.onLoadData(true, true, true);
}
	@Override
	public int getTotalPage() {
		// TODO Auto-generated method stub
		return totalPage;
	}

	@Override
	public ActWishDataParser setParser(String str) {
		// TODO Auto-generated method stub
		ActWishDataParser actWishParser = new ActWishDataParser(str);
		totalPage = actWishParser.getTotalPage();
		return actWishParser;
	}

	public String isActOrWish(){
		return "0";
	}
	@Override
	public RequestParamsNet getApiParmars() {
		
		RequestParamsNet requestPar = new RequestParamsNet();
		requestPar.setmStrHttpApi(TootooeNetApiUrlHelper.ACT_WISH_GET_DATA_URL);
		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.ACT_WISH_GET_DATA_USERID, SharedPreferenceHelper.getLoginUserId(getActivity()));
		//"0"表示是活动的数据, "1"表示是心愿的数据
		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.ACT_WISH_GET_DATA_TYPE, isActOrWish());
		if(isSetData){
			requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.ACT_WISH_GET_DATA_START_TIME_STAMP, data_start_date.getText().toString());
			requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.ACT_WISH_GET_DATA_END_TIME_STAMP, data_end_date.getText().toString());
		}
		
		// 默认每页6条
		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.ACT_WISH_GET_DATA_PAGE_SIZE, String.valueOf(TootooeNetApiUrlHelper.PAGESIZE_DRAFT));
		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.ACT_WISH_GET_DATA_PAGE, String.valueOf(currentpage));
		
		return requestPar;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.data_start_date_layout:
			//选择开始时间
			selectDate(data_start_date);
			
			break;
		case R.id.data_end_date_layout:
			//选择结束时间
			selectDate(data_end_date);
			break;
		}
	}
	
	/**
	 * 选择时间
	 * @param date_tv
	 */
	private void selectDate(final TextView date_tv){
		if(!date_tv.getText().toString().equals("")){
			
			String[] date_strs = date_tv.getText().toString().split("-");
			
			new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
				
			

				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
					
					date_tv.setText(new StringBuilder().append(year).append("-")
		                    .append(formateTime(monthOfYear+1)).append("-")
		                    .append(formateTime(dayOfMonth)));
					isSetData=true;
					
//					onPullDownToRefresh(act_wish_data_lv);	
					loadData();
					
				}
			}, Integer.parseInt(date_strs[0]), Integer.parseInt(date_strs[1]) - 1,Integer.parseInt(date_strs[2])).show();
		}
		
	}

    /* 时间格式 */
    private String formateTime(int time) {
       String timeStr = "";
       if (time < 10){
           timeStr = "0" + String.valueOf(time);
       }else{
           timeStr = String.valueOf(time);
       }
       return timeStr;
    }
}
