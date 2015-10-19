package com.ninetowns.tootoopluse.activity;


import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.bean.LoginBean;
import com.ninetowns.tootoopluse.helper.SharedPreferenceHelper;
import com.ninetowns.tootoopluse.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootoopluse.parser.LoginParser;
import com.ninetowns.tootoopluse.util.CommonUtil;
import com.ninetowns.ui.Activity.BaseActivity;


public class MobileForgetPwdNextActivity extends BaseActivity {
	private String mobile_num;
	
	private EditText register_next_verification, register_next_pwd, register_next_nick;
	
	private TextView register_next_register_btn, register_next_again_send;
	
	private LoginParser loginParser;
	
	private final long COUNT_ONCLICK_PERIOD = 1000;
	
	private final long COUNT_TOTAL_TIME = 60 * 1000;
	
	private CountDownTimer countDownTimer;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.mobile_register_next);
		
		init();
	}

	private void init(){
		//返回
		LinearLayout two_or_one_btn_head_back = (LinearLayout)findViewById(R.id.two_or_one_btn_head_back);
		two_or_one_btn_head_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		//标题
		TextView two_or_one_btn_head_title = (TextView)findViewById(R.id.two_or_one_btn_head_title);
		two_or_one_btn_head_title.setText(R.string.mobile_login_forget_pwd);
		
		Intent intent = getIntent();
		//手机号码
		mobile_num = intent.getStringExtra("mobile_num");
		
		register_next_verification = (EditText)findViewById(R.id.register_next_verification);
		register_next_pwd = (EditText)findViewById(R.id.register_next_pwd);
		//昵称这个editText，在忘记密码第二步里不需要
		register_next_nick = (EditText)findViewById(R.id.register_next_nick);
		register_next_nick.setVisibility(View.GONE);
		
		
		register_next_register_btn = (TextView)findViewById(R.id.register_next_register_btn);
		register_next_register_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String reg_verification = register_next_verification.getText().toString().trim();
				String reg_pwd = register_next_pwd.getText().toString().trim();
				if(reg_verification == null || "".equals(reg_verification)){
					ComponentUtil.showToast(MobileForgetPwdNextActivity.this, getResources().getString(R.string.mobile_ver_no_empty));
				} else if(reg_pwd == null || "".equals(reg_pwd)){
					ComponentUtil.showToast(MobileForgetPwdNextActivity.this, getResources().getString(R.string.mobile_pwd_no_empty));
				} else if(!CommonUtil.isValidPwd(reg_pwd)){
					ComponentUtil.showToast(MobileForgetPwdNextActivity.this, getResources().getString(R.string.mobile_login_pwd_hint));
				} else {
					//显示进度
					showProgressDialog(MobileForgetPwdNextActivity.this);
					RequestParamsNet requestParamsNet = new RequestParamsNet();
					requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.RESET_PWD_MOBILE_PHONENUMBER, mobile_num);
					requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.RESET_PWD_MOBILE_VERIFICATION, reg_verification);
					requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.RESET_PWD_MOBILE_PASSWORD, CommonUtil.md5(reg_pwd));
					requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.RESET_PWD_MOBILE_BUSINESS_STATUS, "1");
					CommonUtil.xUtilsGetSend(TootooeNetApiUrlHelper.RESET_PWD_MOBILE_URL, requestParamsNet, new RequestCallBack<String>() {
						
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							// TODO Auto-generated method stub
							closeProgressDialog(MobileForgetPwdNextActivity.this);
							String loginStr = new String(responseInfo.result);
							loginParser = new LoginParser();
							try {
								LoginBean loginBean = loginParser.parserLogin(loginStr);
								if(loginBean.getLogin_status().equals("1")){
									SharedPreferenceHelper.saveLoginMsg(MobileForgetPwdNextActivity.this, "phone", "", loginBean.getLogin_Id(), 
											loginBean.getLogin_name(), loginBean.getLogin_logoUrl(), loginBean.getLogin_publishStory(), loginBean.getLogin_businessStatus(), 
											loginBean.getLogin_medal(), loginBean.getLogin_tCurrency(), loginBean.getLogin_isLOHAS(), loginBean.getLogin_userGrade(), loginBean.getLogin_coverImage());
									
									Intent intent = new Intent(MobileForgetPwdNextActivity.this, HomeActivity.class);
									startActivity(intent);
									finish();
									
								} else if(loginBean.getLogin_status().equals("1142")){
									ComponentUtil.showToast(MobileForgetPwdNextActivity.this, getResources().getString(R.string.mobile_ver_error));
								}
								
							} catch (Exception e) {
								e.printStackTrace();
							}
							
						}
						
						@Override
						public void onFailure(HttpException error, String msg) {
							// TODO Auto-generated method stub
							closeProgressDialog(MobileForgetPwdNextActivity.this);
							ComponentUtil.showToast(MobileForgetPwdNextActivity.this, getResources().getString(R.string.errcode_network_response_timeout));
						}
					});
					
				}
			}	
		});
		
		register_next_again_send = (TextView)findViewById(R.id.register_next_again_send);
		
		countDownTimer = new CountDownTimer(COUNT_TOTAL_TIME, COUNT_ONCLICK_PERIOD) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				register_next_again_send.setText(getResources().getString(R.string.mobile_ver_again_send) + "(" + millisUntilFinished / 1000 + ")");
				register_next_again_send.setBackgroundResource(R.drawable.btn_register_send_again_gray);
			}
			
			@Override
			public void onFinish() {
				register_next_again_send.setText(getResources().getString(R.string.mobile_ver_again_send));
				register_next_again_send.setBackgroundResource(R.drawable.btn_register_send_again_red);
			}
		};
		countDownTimer.start();
		
		register_next_again_send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(register_next_again_send.getText().toString().trim().equals(getResources().getString(R.string.mobile_ver_again_send))){
					RequestParamsNet requestParamsNet = new RequestParamsNet();
					requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.SEND_VERIFICATION_PHONENUMBER, mobile_num);
					requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.SEND_VERIFICATION_TYPE, "2");
					//"1"为商家
					requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.SEND_VERIFICATION_BUSINESS_STATUS, "1");
					CommonUtil.xUtilsGetSend(TootooeNetApiUrlHelper.SEND_VERIFICATION_URL, requestParamsNet, new RequestCallBack<String>() {
						
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							// TODO Auto-generated method stub
							String jsonStr = new String(responseInfo.result);
							
							try {
								JSONObject jsonObj = new JSONObject(jsonStr);
								String register_status = "";
								if(jsonObj.has("Status")){
									register_status = jsonObj.getString("Status");
								}
								
								if(register_status.equals("1")){
									/**
									 * 暂时不能发短信，所以要记住这个字段
									 */
									if(jsonObj.has("Data")){
										JSONObject jsonData = jsonObj.getJSONObject("Data");
					CommonUtil.sysApiLog("MobileRegisterNextActivity+++++验证码", jsonData.getString("Verification"));
									}
									//接口调用成功以后再启动倒计时
									countDownTimer.start();
								}
								
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
						@Override
						public void onFailure(HttpException error, String msg) {
							// TODO Auto-generated method stub
							ComponentUtil.showToast(MobileForgetPwdNextActivity.this, getResources().getString(R.string.errcode_network_response_timeout));
						}
					});
					
					
				}
				
			}
		});
		
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		countDownTimer.cancel();
	}
	
	
}
