package com.ninetowns.tootoopluse.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.activity.CreateActSecondStepActivity;
import com.ninetowns.tootoopluse.bean.UpdateActFirstStepBean;
import com.ninetowns.tootoopluse.helper.ChangeItemView;
import com.ninetowns.tootoopluse.helper.ChangeItemView.ChangeItemDltAndAddListener;
import com.ninetowns.tootoopluse.helper.ChangeRowsView;
import com.ninetowns.tootoopluse.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootoopluse.helper.SharedPreferenceHelper;
import com.ninetowns.tootoopluse.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootoopluse.parser.UpdateActFirstParser;
import com.ninetowns.ui.fragment.BaseFragment;

public class UpdateActFirstStepFragment extends BaseFragment<UpdateActFirstStepBean, UpdateActFirstParser> {
	
	private String act_id = "";
	
	//区分是再次发布还是修改
	private boolean isAgainPush = false;
	
	private ChangeItemView create_act_first_step_change_item_view;
	
	private LinearLayout create_act_first_step_add_store_layout;
	
	private TextView create_act_first_step_add_store;
	
	private UpdateActFirstStepBean updateActBean;
	
	private boolean isManyTimes = false;
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		
		Intent intent = getActivity().getIntent();
		Bundle bundle = intent.getExtras().getBundle(ConstantsTooTooEHelper.BUNDLE);
		act_id = bundle.getString(ConstantsTooTooEHelper.ACTIVITYID);
		isAgainPush = bundle.getBoolean("isAgainPush");
        super.onLoadData(true, false, false);
        super.onActivityCreated(savedInstanceState);
    }
	
	 @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		 
		View view = inflater.inflate(R.layout.create_active_first_step_fragment, null);
		
		//返回
		LinearLayout two_or_one_btn_head_back = (LinearLayout)view.findViewById(R.id.two_or_one_btn_head_back);
		two_or_one_btn_head_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().finish();
			}
		});
		
		//标题
		TextView two_or_one_btn_head_title = (TextView)view.findViewById(R.id.two_or_one_btn_head_title);
		two_or_one_btn_head_title.setText(R.string.update_act_title);
		
		RelativeLayout create_act_first_step_submit_layout = (RelativeLayout)view.findViewById(R.id.create_act_first_step_submit_layout);
		create_act_first_step_submit_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//下一步
				
				List<String> selectStoryList = new ArrayList<String>();
				List<String> selectUser = new ArrayList<String>();
				
				LinearLayout change_item_layout = (LinearLayout)create_act_first_step_change_item_view.getChildAt(0).findViewById(R.id.change_item_layout);
				
				if(create_act_first_step_change_item_view.getChangeItemChildCount() > 1){
					for(int i = 0; i < create_act_first_step_change_item_view.getChangeItemChildCount(); i++){
						ChangeRowsView changeRowsView = (ChangeRowsView)change_item_layout.getChildAt(i).findViewById(R.id.act_first_step_item_change_rows);
						
						TextView act_first_step_item_tv = (TextView)change_item_layout.getChildAt(i).findViewById(R.id.act_first_step_item_tv);
						
						AutoCompleteTextView act_first_step_item_auto_tv = (AutoCompleteTextView)change_item_layout.getChildAt(i).findViewById(R.id.act_first_step_item_auto_tv);
						
						
						LinearLayout change_rows_cont_layout = (LinearLayout)changeRowsView.getChildAt(0).findViewById(R.id.change_rows_cont_layout);
						TextView tv = (TextView)change_rows_cont_layout.getChildAt(0).findViewById(R.id.change_rows_cont_story_id);
						if(!tv.getText().toString().equals("")){
							selectStoryList.add(tv.getText().toString());
							if(i == 0){
								selectUser.add(act_first_step_item_tv.getText().toString().trim());
							} else {
								if(selectUser.contains(act_first_step_item_auto_tv.getText().toString().trim())){
									isManyTimes = true;
								} else {
									selectUser.add(act_first_step_item_auto_tv.getText().toString().trim());
								}
							}
						}
					}
					
					
					if(isManyTimes){
						isManyTimes = !isManyTimes;
						ComponentUtil.showToast(getActivity(), getResources().getString(R.string.create_act_many_stores));
					} else {
						StringBuffer sb = new StringBuffer();
						for(int i = 0; i < selectStoryList.size(); i++){
							if(i == selectStoryList.size() - 1){
								sb.append(selectStoryList.get(i));
							} else {
								sb.append(selectStoryList.get(i)).append(",");
							}
						}
						Intent second_intent = new Intent(getActivity(), CreateActSecondStepActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString(ConstantsTooTooEHelper.STORYID, sb.toString());
						bundle.putString(ConstantsTooTooEHelper.ACTIVITYID, act_id);
						bundle.putBoolean("isEditextAct", true);
						
						second_intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
						startActivity(second_intent);
					}
					
				} else {
					ChangeRowsView changeRowsView = (ChangeRowsView)change_item_layout.getChildAt(0).findViewById(R.id.act_first_step_item_change_rows);
					LinearLayout change_rows_cont_layout = (LinearLayout)changeRowsView.getChildAt(0).findViewById(R.id.change_rows_cont_layout);
					
					for(int i = 0; i < changeRowsView.getChangeRowsContChildCount(); i++){
						
						TextView tv = (TextView)change_rows_cont_layout.getChildAt(i).findViewById(R.id.change_rows_cont_story_id);
						if(!tv.getText().toString().equals("")){
							if(selectStoryList.contains(tv.getText().toString())){
								isManyTimes = true;
							} else {
								selectStoryList.add(tv.getText().toString());
							}
						}
					}
					
					
					if(isManyTimes){
						isManyTimes = !isManyTimes;
						ComponentUtil.showToast(getActivity(), getResources().getString(R.string.create_act_many_storys));
					} else {
						StringBuffer sb = new StringBuffer();
						for(int i = 0; i < selectStoryList.size(); i++){
							if(i == selectStoryList.size() - 1){
								sb.append(selectStoryList.get(i));
							} else {
								sb.append(selectStoryList.get(i)).append(",");
							}
						}
						Intent second_intent = new Intent(getActivity(), CreateActSecondStepActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString(ConstantsTooTooEHelper.STORYID, sb.toString());
						bundle.putString(ConstantsTooTooEHelper.ACTIVITYID, act_id);
						bundle.putBoolean("isEditextAct", true);
						
						second_intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
						startActivity(second_intent);
					}
				}
				
				
			}
		});
		
		TextView two_or_one_btn_head_second_tv = (TextView)view.findViewById(R.id.two_or_one_btn_head_second_tv);
		two_or_one_btn_head_second_tv.setVisibility(View.VISIBLE);
		two_or_one_btn_head_second_tv.setText(R.string.create_act_next_hint);
		
		
		create_act_first_step_change_item_view = (ChangeItemView)view.findViewById(R.id.create_act_first_step_change_item_view);
		
		create_act_first_step_add_store_layout = (LinearLayout)view.findViewById(R.id.create_act_first_step_add_store_layout);
		create_act_first_step_add_store = (TextView)view.findViewById(R.id.create_act_first_step_add_store);
		create_act_first_step_add_store.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//添加商家
				create_act_first_step_change_item_view.initItemView("", updateActBean.getUpdate_user_list(), null, null, null);
				if(create_act_first_step_change_item_view.getChangeItemChildCount() == 5){
					create_act_first_step_add_store_layout.setVisibility(View.GONE);
				}
				
				
			}
		});
		
        return view;
     }


	@Override
	public UpdateActFirstParser setParser(String str) {
		// TODO Auto-generated method stub
		return new UpdateActFirstParser(str);
	}

	@Override
	public void getParserResult(UpdateActFirstStepBean parserResult) {
		if(parserResult != null){
			updateActBean = parserResult;
			
			if(!TextUtils.isEmpty(updateActBean.getUpdate_act_id())){
				act_id = updateActBean.getUpdate_act_id();
			}
			
			if(updateActBean.getUpdate_isManyE().equals("0")){
				//单商家
				create_act_first_step_change_item_view.initItemView(updateActBean.getUpdate_user_name(), 
						null, updateActBean.getUpdate_story_list(), null, updateActBean.getUpdate_old_storys());
				
			} else {
				//多商家
				for(int i = 0; i < updateActBean.getUpdate_old_storys().size(); i++){
					if(i ==0){
						create_act_first_step_change_item_view.initItemView(updateActBean.getUpdate_old_storys().get(i).getCreate_act_userName(), 
								null, updateActBean.getUpdate_story_list(), updateActBean.getUpdate_old_storys().get(i), null);
					} else {
						create_act_first_step_change_item_view.initItemView(updateActBean.getUpdate_old_storys().get(i).getCreate_act_userName(), 
								updateActBean.getUpdate_user_list(), null, updateActBean.getUpdate_old_storys().get(i), null);
					}
					
				}
				
			}
			
			create_act_first_step_change_item_view.setChangeItemDltAndAddListener(new ChangeItemDltAndAddListener() {
				
				@Override
				public void changeItemDelete() {
					if(create_act_first_step_change_item_view.getChangeItemChildCount() < 5 && create_act_first_step_add_store_layout.getVisibility() == View.GONE){
						create_act_first_step_add_store_layout.setVisibility(View.VISIBLE);
					}
				}
				
				@Override
				public void changeItemAdd() {
					if(create_act_first_step_add_store_layout.getVisibility() == View.VISIBLE){
						create_act_first_step_add_store_layout.setVisibility(View.GONE);
					}
					
				}
			});
			
			if(updateActBean.getUpdate_old_storys().size() == 5){
				create_act_first_step_add_store_layout.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public RequestParamsNet getApiParmars() {
		// TODO Auto-generated method stub
		RequestParamsNet requestParamsNet = new RequestParamsNet();
		requestParamsNet.setmStrHttpApi(TootooeNetApiUrlHelper.UPDATE_ACT_FIRST_STEP_URL);
		requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.UPDATE_ACT_FIRST_STEP_USERID, SharedPreferenceHelper.getLoginUserId(getActivity()));
		requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.UPDATE_ACT_FIRST_STEP_ACTID, act_id);
		if(isAgainPush){
			requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.UPDATE_ACT_FIRST_STEP_AGAIN_RELEASED, "1");
		}
		
		return requestParamsNet;
	}
	
	

}
