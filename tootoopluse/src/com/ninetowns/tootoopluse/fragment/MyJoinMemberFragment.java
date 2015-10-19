package com.ninetowns.tootoopluse.fragment;

import java.util.List;

import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.library.util.NetworkUtil;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.activity.SettingActivity;
import com.ninetowns.tootoopluse.adapter.MyGroupSuccessAdapter;
import com.ninetowns.tootoopluse.application.TootooPlusEApplication;
import com.ninetowns.tootoopluse.bean.GridViewGroupBean;
import com.ninetowns.tootoopluse.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootoopluse.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootoopluse.parser.MyJoinMemGroupParser;
import com.ninetowns.tootoopluse.util.CommonUtil;
import com.ninetowns.ui.fragment.BaseFragment;
import com.ninetowns.ui.widget.dialog.TooSureCancelDialog;
import com.ninetowns.ui.widget.dialog.TooSureCancelDialog.TooDialogCallBack;

public class MyJoinMemberFragment extends
		BaseFragment<List<GridViewGroupBean>, MyJoinMemGroupParser> implements
		View.OnClickListener, OnItemClickListener {

	@ViewInject(R.id.tv_rem_info)
	private TextView mTvInfo;
	@ViewInject(R.id.tv_comit)
	private TextView mTVComit;
	@ViewInject(R.id.et_left)
	private EditText mETLeft;
	@ViewInject(R.id.gl_rember)
	private GridView mGV;
	@ViewInject(R.id.ibtn_left)
	private ImageButton ibtnLeft;
	@ViewInject(R.id.tv_title)
	private TextView mTVTitle;
	@ViewInject(R.id.ibtn_right)
	private ImageButton ibtnRight;
	private View remberCount;
	private String groupid;
	private String activityId;
	private List<GridViewGroupBean> localResult;
	private String itemEateCode;
	private String username;
	private boolean isUsered;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		groupid = getActivity().getIntent().getStringExtra(
				ConstantsTooTooEHelper.GROUP_ID);
		activityId = getActivity().getIntent().getStringExtra(
				ConstantsTooTooEHelper.ACTIVITYID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		remberCount = inflater.inflate(R.layout.myjonimemberact, null);
		ViewUtils.inject(this, remberCount);
		mTVComit.setOnClickListener(this);
		ibtnLeft.setOnClickListener(this);
		ibtnRight.setVisibility(View.INVISIBLE);
		mTVTitle.setText(R.string.already_title_group);
		mGV.setOnItemClickListener(this);
		return remberCount;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadData();
	}

	private void loadData() {
		super.onLoadData(true, true, true);
	}

	@Override
	public MyJoinMemGroupParser setParser(String str) {
		MyJoinMemGroupParser parser = new MyJoinMemGroupParser(str);
		String info = parser.getInfo();
		if (!TextUtils.isEmpty(info)) {
			mTvInfo.setText(info);
		}
		return parser;
	}

	@Override
	public void getParserResult(List<GridViewGroupBean> parserResult) {
		localResult = parserResult;
		if (parserResult != null && parserResult.size() > 0) {
			MyGroupSuccessAdapter adapter = new MyGroupSuccessAdapter(
					getActivity(), parserResult);
			mGV.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}

	}

	@Override
	public RequestParamsNet getApiParmars() {
		RequestParamsNet par = new RequestParamsNet();
		par.setmStrHttpApi(TootooeNetApiUrlHelper.MY_FREE_GROUP_URL_MEMBER);
		if (!TextUtils.isEmpty(groupid)) {
			par.addQueryStringParameter(
					TootooeNetApiUrlHelper.ALL_MEMBER_EXIT_GROUP_GROUPID,
					groupid);
		}
		return par;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_comit:
			if(isUsered){
				ComponentUtil.showToast(TootooPlusEApplication.getAppContext(), "该用户已签到");
			}else{

				final String myEditText=mETLeft.getText().toString();
				if(!TextUtils.isEmpty(myEditText)){
					

					TooSureCancelDialog tooDialog = new TooSureCancelDialog(
							getActivity());
					tooDialog.setDialogTitle(R.string.rainbo_hint);
					StringBuilder strbu=new StringBuilder();
					strbu.append("确定是用户").append(username).append("的白吃码").append(myEditText).append("吗？");
							
					tooDialog
							.setDialogContent(strbu.toString());
					tooDialog.setTooDialogCallBack(new TooDialogCallBack() {

						@Override
						public void onTooDialogSure() {
							shiyongNetData(myEditText);
						}

						@Override
						public void onTooDialogCancel() {

						}
					});
					tooDialog.show();
				
				
				}else{
					ComponentUtil.showToast(TootooPlusEApplication.getAppContext(), "请选择白吃码");
				}
			
			}
			
		
			break;
		case R.id.ibtn_left:
			if (isAdded()) {
				getActivity().finish();
			}

			break;

		default:
			break;
		}

	}

	/**
	 * 
	 * @Title: shiyongNetData
	 * @Description: 使用优先码
	 * @param
	 * @return
	 * @throws
	 */
	private void shiyongNetData(String baichima) {

		if ((NetworkUtil.isNetworkAvaliable(TootooPlusEApplication
				.getAppContext()))) {
			// 显示进度
			RequestParamsNet requestParamsNet = new RequestParamsNet();
			requestParamsNet.addBodyParameter(
					TootooeNetApiUrlHelper.ALL_MEMBER_EXIT_GROUP_GROUPID,
					groupid);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.ACTIVITYID, activityId);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.EatCode, baichima);// userid
			CommonUtil.xUtilsPostSend(TootooeNetApiUrlHelper.MY_USERDEAT,
					requestParamsNet, new RequestCallBack<String>() {

						private String status;

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String strreault = responseInfo.result;
							if (!TextUtils.isEmpty(strreault)) {
								try {
									JSONObject obj = new JSONObject(strreault);
									if (obj.has("Status")) {
										String status = obj.getString("Status");
										if (status.equals("1")) {
											mETLeft.setText("");
											loadData();
											// <string
											// name="qiandaosuccess">签到成功！</string>
											// <string
											// name="request_error">请求失败!</string>
											// <string
											// name="no_user">无此用户!</string>
											// <string
											// name="the_user_isget">该用户已签到!</string>
											if (isAdded()) {
												ComponentUtil.showToast(
														TootooPlusEApplication
																.getAppContext(),
														getResources()
																.getString(
																		R.string.qiandaosuccess));
											}

										} else if (status.equals("3103")) {
											if (isAdded()) {
												ComponentUtil.showToast(
														TootooPlusEApplication
																.getAppContext(),
														getResources()
																.getString(
																		R.string.qiandaosuccess));
											}

										} else if (status.equals("3101")) {
											if (isAdded()) {
												ComponentUtil.showToast(
														TootooPlusEApplication
																.getAppContext(),
														getResources()
																.getString(
																		R.string.no_user));
											}
										} else if (status.equals("3102")) {
											if (isAdded()) {
												ComponentUtil.showToast(
														TootooPlusEApplication
																.getAppContext(),
														getResources()
																.getString(
																		R.string.the_user_isget));
											}
										} else if (status.equals("3100")) {
											if (isAdded()) {
												ComponentUtil.showToast(
														TootooPlusEApplication
																.getAppContext(),
														getResources()
																.getString(
																		R.string.error_endter_num));
											}
										}

									}
								} catch (Exception e) {
									e.printStackTrace();
								}

							}

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							ComponentUtil.showToast(
									TootooPlusEApplication.getAppContext(),
									getResources()
											.getString(
													R.string.errcode_network_response_timeout));
						}
					});

		} else {
			ComponentUtil.showToast(
					TootooPlusEApplication.getAppContext(),
					this.getResources().getString(
							R.string.errcode_network_response_timeout));
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	
		itemEateCode="";
		username="";
		if (localResult != null && position != -1 && localResult.size() > 0) {
			GridViewGroupBean item = localResult.get(position);
			itemEateCode = item.getEatCode();
			String status=item.getIsUsed();
			if(!TextUtils.isEmpty(status)){
				if(status.equals("1")){
					isUsered=true;
					//成功
				}else{//0未成功
					isUsered=false;
				}
			}else{
				isUsered=false;
			}
			username = item.getUserName();
			mETLeft.setText(itemEateCode);
		}

	}
}
