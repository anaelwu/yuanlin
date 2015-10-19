package com.ninetowns.tootooplus.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.ninetowns.library.helper.AppManager;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.tootooplus.activity.ActivityDetailActivity;
import com.ninetowns.tootooplus.activity.AddressAddActivity;
import com.ninetowns.tootooplus.activity.JoinMemberActivity;
import com.ninetowns.tootooplus.activity.MyFreeGroupActivity;
import com.ninetowns.tootooplus.bean.AddressListBean;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootooplus.parser.AddressListParser;
import com.ninetowns.tootooplus.util.CommonUtil;
import com.ninetowns.tootooplus.R;
import com.ninetowns.ui.fragment.BaseFragment;

public class AddressListFragment extends BaseFragment<List<AddressListBean>, AddressListParser>{
	
	private ListView address_fragment_lv;
	
	private String act_id = "";
	
	private String group_id = "";
	
	private List<AddressListBean> addressList;
	
	private Map<Integer, AddressListBean> selectMap = new HashMap<Integer, AddressListBean>();
	
	
	private AddressLvAdapter addressLvAdapter = null;
	
	private String address_id = "";
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
    	View view = inflater.inflate(R.layout.address_list_fragment, null);
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
		two_or_one_btn_head_title.setText(R.string.address_list_title);
    	
		RelativeLayout two_or_one_btn_head_second_layout = (RelativeLayout)view.findViewById(R.id.two_or_one_btn_head_second_layout);
		two_or_one_btn_head_second_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//确定
				if(addressLvAdapter!=null){
					Map<Integer, AddressListBean> adapterMap = addressLvAdapter.map;
					AddressListBean addressListBean = null;
					for(Integer key : adapterMap.keySet()){
						addressListBean = adapterMap.get(key);
					}
					
					yxmPop(addressListBean.getAdd_addressId());
				}
				}
		});
		
		TextView two_or_one_btn_head_second_tv = (TextView)view.findViewById(R.id.two_or_one_btn_head_second_tv);
		two_or_one_btn_head_second_tv.setVisibility(View.VISIBLE);
		two_or_one_btn_head_second_tv.setText(R.string.rainbo_sure);
		
		
		address_fragment_lv = (ListView)view.findViewById(R.id.address_fragment_lv);
		
		View foot_view = inflater.inflate(R.layout.order_add_adress_layout, null);
		LinearLayout order_add_layout = (LinearLayout)foot_view.findViewById(R.id.order_add_layout);
		order_add_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//添加收货地址
				Intent add_intent = new Intent(getActivity(), AddressAddActivity.class);
				add_intent.putExtra("act_id", act_id);
				add_intent.putExtra("group_id", group_id);
				startActivity(add_intent);
				
			}
		});
		
		address_fragment_lv.addFooterView(foot_view);
    	
    	return view;
	 }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
		
		act_id = getActivity().getIntent().getStringExtra(TootooeNetApiUrlHelper.ACTIVITYID);
		group_id = getActivity().getIntent().getStringExtra("group_id");
		address_id = getActivity().getIntent().getStringExtra("address_id");
        super.onLoadData(true, false, false);
        super.onActivityCreated(savedInstanceState);
    }


	@Override
	public AddressListParser setParser(String str) {
		// TODO Auto-generated method stub
		return new AddressListParser(str);
	}

	@Override
	public void getParserResult(List<AddressListBean> parserResult) {
		// TODO Auto-generated method stub
		if(parserResult != null && parserResult.size() > 0){
			addressList = parserResult;
			if(address_id != null){
				for(int i = 0; i < addressList.size(); i++){
					if(addressList.get(i).getAdd_addressId().equals(address_id)){
						selectMap.put(i, addressList.get(i));
					}
				}
			} else {
				selectMap.put(0, addressList.get(0));
			}
			
			addressLvAdapter = new AddressLvAdapter(getActivity(), addressList, selectMap);
			address_fragment_lv.setAdapter(addressLvAdapter);
			address_fragment_lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					selectMap.clear();
					selectMap.put(position, addressList.get(position));
					addressLvAdapter.notifyDataSetChanged();
				}
			});
			
		} else {
			address_fragment_lv.setAdapter(null);
		}
	}

	@Override
	public RequestParamsNet getApiParmars() {
		// TODO Auto-generated method stub
		RequestParamsNet requestParamsNet = new RequestParamsNet();
		requestParamsNet.setmStrHttpApi(TootooeNetApiUrlHelper.ADDRESS_LIST_URL);
		requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.ADDRESS_LIST_USERID, SharedPreferenceHelper.getLoginUserId(getActivity()));
		requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.ADDRESS_LIST_ACTID, act_id);
		return requestParamsNet;
	}
	
	class AddressLvAdapter extends BaseAdapter{
		
		private Context context;
		
		private List<AddressListBean> list;
		
		public Map<Integer, AddressListBean> map;
		
		public AddressLvAdapter(Context context, List<AddressListBean> list, Map<Integer, AddressListBean> map){
			this.context = context;
			
			this.list = list;
			
			this.map = map;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(list != null && list.size() > 0){
				return list.size();
			} else {
				return 0;
			}
			
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder vh = null;
			if(convertView == null){
				vh = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.address_lv_item, null);
				
				vh.address_lv_item_all_layout = (LinearLayout)convertView.findViewById(R.id.address_lv_item_all_layout);
				
				vh.address_lv_item_name = (TextView)convertView.findViewById(R.id.address_lv_item_name);
				
				vh.address_lv_item_phone = (TextView)convertView.findViewById(R.id.address_lv_item_phone);
				
				vh.address_lv_item_details = (TextView)convertView.findViewById(R.id.address_lv_item_details);
				
				vh.address_lv_item_select = (ImageView)convertView.findViewById(R.id.address_lv_item_select);
				
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder)convertView.getTag();
			}
			
			if(!TextUtils.isEmpty(list.get(position).getAdd_realName())){
				vh.address_lv_item_name.setText(list.get(position).getAdd_realName());
			} else {
				vh.address_lv_item_name.setText("");
			}
			
			
			if(!TextUtils.isEmpty(list.get(position).getAdd_phoneNumber())){
				vh.address_lv_item_phone.setText(list.get(position).getAdd_phoneNumber());
			} else {
				vh.address_lv_item_phone.setText("");
			}
			
			if(list.get(position).getAdd_provinceName().equals(list.get(position).getAdd_cityName())){
				//直辖市的情况
				vh.address_lv_item_details.setText(list.get(position).getAdd_cityName() + getResources().getString(R.string.address_item_city) + 
						list.get(position).getAdd_districtName() + list.get(position).getAdd_detailedAddress() + 
						"    " + list.get(position).getAdd_postcode());
			} else {
				vh.address_lv_item_details.setText(list.get(position).getAdd_provinceName() + getResources().getString(R.string.address_item_province) + 
						list.get(position).getAdd_cityName() + getResources().getString(R.string.address_item_city) + list.get(position).getAdd_districtName() + 
						list.get(position).getAdd_detailedAddress() + "    " + list.get(position).getAdd_postcode());
			}
			
			if(map.get(position) != null){
				vh.address_lv_item_select.setVisibility(View.VISIBLE);
			} else {
				vh.address_lv_item_select.setVisibility(View.GONE);
			}
			
			return convertView;
		}
		
		class ViewHolder{
			LinearLayout address_lv_item_all_layout;
			
			TextView address_lv_item_name;
			
			TextView address_lv_item_phone;
			
			TextView address_lv_item_details;
			
			ImageView address_lv_item_select;
			
		}
	}
	
	
	private void yxmPop(final String yxm_addressId){
		WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		//屏幕宽度
		int screen_width = wm.getDefaultDisplay().getWidth();
		//屏幕高度
		int screen_height = wm.getDefaultDisplay().getHeight();
		final PopupWindow popupWindow = new PopupWindow(getActivity());
		
		if(!popupWindow.isShowing()){
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.yxm_pop_layout, null);
			
			popupWindow.setContentView(view);

			//窗口的宽带和高度根据情况定义
			popupWindow.setWidth(screen_width);
			popupWindow.setHeight(screen_height);
			
			popupWindow.setFocusable(true);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setBackgroundDrawable(new ColorDrawable(0));
			
			popupWindow.showAtLocation(LayoutInflater.from(getActivity()).inflate(R.layout.join_member_fragment, null), 
											//位置可以按要求定义
											Gravity.NO_GRAVITY, 0, 0);
			LinearLayout yxm_pop_all_layout = (LinearLayout)view.findViewById(R.id.yxm_pop_all_layout);
			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)yxm_pop_all_layout.getLayoutParams();
			linearParams.width = screen_width * 3/4;
			linearParams.height = screen_height / 3;
			yxm_pop_all_layout.setLayoutParams(linearParams);
			
			//用优先码
			final CheckBox use_yxm_pop_cb = (CheckBox)view.findViewById(R.id.use_yxm_pop_cb);
			//默认使用优先码
			use_yxm_pop_cb.setChecked(true);
			
			final CheckBox unuse_yxm_pop_cb = (CheckBox)view.findViewById(R.id.unuse_yxm_pop_cb);
			
			LinearLayout use_yxm_pop_layout = (LinearLayout)view.findViewById(R.id.use_yxm_pop_layout);
			use_yxm_pop_layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(!use_yxm_pop_cb.isChecked()){
						use_yxm_pop_cb.setChecked(true);
						unuse_yxm_pop_cb.setChecked(false);
					}
					
				}
			});
			LinearLayout unuse_yxm_pop_layout = (LinearLayout)view.findViewById(R.id.unuse_yxm_pop_layout);
			unuse_yxm_pop_layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(!unuse_yxm_pop_cb.isChecked()){
						unuse_yxm_pop_cb.setChecked(true);
						use_yxm_pop_cb.setChecked(false);
					}
					
				}
			});
			
			final EditText use_yxm_pop_et = (EditText)view.findViewById(R.id.use_yxm_pop_et);
			
			LinearLayout yxm_pop_submit_layout = (LinearLayout)view.findViewById(R.id.yxm_pop_submit_layout);
			yxm_pop_submit_layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//线下
					showProgressDialog();
					//组团报名
					RequestParamsNet requestParamsNet = new RequestParamsNet();
					requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.JOIN_MEM_SUBMIT_GROUP_USERID, SharedPreferenceHelper.getLoginUserId(getActivity()));
					requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.JOIN_MEM_SUBMIT_GROUP_GROUPID, group_id);
					requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.JOIN_MEM_SUBMIT_GROUP_ACTIVITYID, act_id);
					requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.JOIN_MEM_SUBMIT_GROUP_ADDRESS_ID, yxm_addressId);
					if(use_yxm_pop_cb.isChecked()){
						if(use_yxm_pop_et.getText().toString().trim().equals("")){
							ComponentUtil.showToast(getActivity(), getResources().getString(R.string.use_yxm_tv_no_empty));
							closeProgressDialogFragment();
							return;
						} else {
							requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.JOIN_MEM_SUBMIT_GROUP_PRIORITY_CODE, use_yxm_pop_et.getText().toString().trim());
						}
						
					}
					CommonUtil.xUtilsGetSend(TootooeNetApiUrlHelper.JOIN_MEM_SUBMIT_GROUP_URL, requestParamsNet, new RequestCallBack<String>() {
						
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							// TODO Auto-generated method stub
							closeProgressDialogFragment();
							String jsonStr = new String(responseInfo.result);
							
							try {
								JSONObject jsonObj = new JSONObject(jsonStr);
								String register_status = "";
								if(jsonObj.has("Status")){
									register_status = jsonObj.getString("Status");
								}
								
								if(register_status.equals("1")){
									//跳转到活动详情
									Intent act_intent = new Intent(getActivity(), ActivityDetailActivity.class);
									Bundle bundle = new Bundle();
									bundle.putString(TootooeNetApiUrlHelper.ACTIVITYID, act_id);
									bundle.putString("currentPosition", "0");
									act_intent.putExtra(TootooeNetApiUrlHelper.BUNDLE, bundle);
									act_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(act_intent);
									
									AppManager.getAppManager().finishActivity(MyFreeGroupActivity.class);
									AppManager.getAppManager().finishActivity(JoinMemberActivity.class);
									getActivity().finish();
									
								} else if(register_status.equals("3104")){
									ComponentUtil.showToast(getActivity(), getResources().getString(R.string.join_enter_group_yxm_fail));
								} else if(register_status.equals("3105")){
									ComponentUtil.showToast(getActivity(), getResources().getString(R.string.join_enter_group_less_ten));
								} else {
									ComponentUtil.showToast(getActivity(), getResources().getString(R.string.join_enter_group_fail));
								}
								
							} catch (Exception e) {
								e.printStackTrace();
							}
							
						}
						
						@Override
						public void onFailure(HttpException error, String msg) {
							// TODO Auto-generated method stub
							closeProgressDialogFragment();
							ComponentUtil.showToast(getActivity(), getResources().getString(R.string.errcode_network_response_timeout));
						}
					});
					popupWindow.dismiss();	
				}
			});
			
			LinearLayout yxm_pop_cancel_layout = (LinearLayout)view.findViewById(R.id.yxm_pop_cancel_layout);
			yxm_pop_cancel_layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					popupWindow.dismiss();
				}
			});
		}
	}
}
