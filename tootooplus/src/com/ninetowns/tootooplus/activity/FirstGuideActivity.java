package com.ninetowns.tootooplus.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.library.util.LogUtil;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.bean.LoginBean;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootooplus.parser.LoginParser;
import com.ninetowns.tootooplus.util.CommonUtil;
import com.ninetowns.ui.Activity.BaseActivity;

public class FirstGuideActivity extends BaseActivity implements OnClickListener, PlatformActionListener{
	
	private List<View> itemViews = null;
	
	private ImageView first_guide_mark_one, first_guide_mark_two, first_guide_mark_three, 
		first_guide_mark_four, first_guide_mark_five;
	
	private ViewPager first_guide_viewPager;
	
	private LoginParser loginParser;
	
	private final int THIRD_LOGIN_CANCEL = 1;
	
	private final int THIRD_LOGIN_ERROR = 2;
	
	private final int THIRD_LOGIN_COMPLETE = 3;
	
	private static final int JPUSH_SET_ALIAS = 1001;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case THIRD_LOGIN_CANCEL:
				ComponentUtil.showToast(FirstGuideActivity.this, getResources().getString(R.string.login_auth_cancel));
				break;
			case THIRD_LOGIN_ERROR:
				ComponentUtil.showToast(FirstGuideActivity.this, getResources().getString(R.string.login_auth_error));
				break;
			case THIRD_LOGIN_COMPLETE:
				final Platform platform = (Platform)msg.obj;
				
				LogUtil.systemlogInfo("Login+++onComplete+++", platform.getDb().getUserId());
				LogUtil.systemlogInfo("Login+++onComplete+++", platform.getDb().getUserIcon());
				LogUtil.systemlogInfo("Login+++onComplete+++", platform.getDb().getPlatformNname());

				//第三方id
				final String loginRelationId = platform.getDb().getUserId();
				
				RequestParamsNet requestParamsNet = new RequestParamsNet();
				
					requestParamsNet.addQueryStringParameter("access_token", platform.getDb().getToken());
					requestParamsNet.addQueryStringParameter("openid", loginRelationId);
					requestParamsNet.addQueryStringParameter("lang", "zh_CN");
					HttpUtils httpUtils = new HttpUtils();
					httpUtils.send(HttpMethod.GET, "https://api.weixin.qq.com/sns/userinfo?", requestParamsNet,
							new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							// TODO Auto-generated method stub
							String wechatStr = new String(responseInfo.result);
							loginParser = new LoginParser();
							try {
								String unionid = loginParser.parserWechat(wechatStr);
								RequestParamsNet requestParamsNet = new RequestParamsNet();
								requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.CHECK_LOGIN_PARAM_SOURCE, "wechat");
								requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.CHECK_LOGIN_PARAM_RELATIONID, unionid);
								requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.CHECK_LOGIN_PARAM_RELATIONNAME, platform.getDb().getUserName());
								requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.CHECK_LOGIN_PARAM_LOGOURL, platform.getDb().getUserIcon());
								requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.CHECK_LOGIN_PARAM_PHONENUMBER, "");
								requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.CHECK_LOGIN_PARAM_PASSWORD, "");
								requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.CHECK_LOGIN_PARAM_PHONE_ONLY_CODE, SharedPreferenceHelper.phoneUniqueId(FirstGuideActivity.this));
								// "0"为客户
								requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.CHECK_LOGIN_PARAM_BUSINESS_STATUS, "0");
								
								CommonUtil.xUtilsGetSend(TootooeNetApiUrlHelper.CHECK_LOGIN_URL,
										requestParamsNet, new RequestCallBack<String>() {

											@Override
											public void onSuccess(ResponseInfo<String> responseInfo) {
												// TODO Auto-generated method stub
												closeProgressDialog(FirstGuideActivity.this);
												String loginStr = new String(responseInfo.result);
												loginParser = new LoginParser();
												try {
													LoginBean loginBean = loginParser.parserLogin(loginStr);
													if (loginBean.getLogin_status().equals("1")) {
														SharedPreferenceHelper.saveLoginMsg(FirstGuideActivity.this, "wechat", loginRelationId, loginBean.getLogin_Id(), 
																loginBean.getLogin_name(), loginBean.getLogin_logoUrl(), loginBean.getLogin_publishStory(), loginBean.getLogin_businessStatus(),
																loginBean.getLogin_medal(), loginBean.getLogin_tCurrency(), loginBean.getLogin_isLOHAS(), loginBean.getLogin_userGrade(), loginBean.getLogin_coverImage());
															
														
														//登录成功以后调用JPush API设置Alias(别名)
														handler.sendMessage(handler.obtainMessage(JPUSH_SET_ALIAS, SharedPreferenceHelper.getLoginUserId(FirstGuideActivity.this)));
														
														Intent intent = new Intent(FirstGuideActivity.this, HomeActivity.class);
														startActivity(intent);
														finish();
														
													} else {
														ComponentUtil.showToast(FirstGuideActivity.this, getResources().getString(R.string.login_fail));
													}

												} catch (Exception e) {
													e.printStackTrace();
												}

											}

											@Override
											public void onFailure(HttpException error,
													String msg) {
												// TODO Auto-generated method stub
												closeProgressDialog(FirstGuideActivity.this);
												ComponentUtil.showToast(FirstGuideActivity.this, getResources().getString(R.string.errcode_network_response_timeout));
											}
										});
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						@Override
						public void onFailure(HttpException error,
								String msg) {
							// TODO Auto-generated method stub
							ComponentUtil.showToast(FirstGuideActivity.this, getResources().getString(R.string.errcode_network_response_timeout));
						}
					});
				break;
			case JPUSH_SET_ALIAS:
            	JPushInterface.setAlias(FirstGuideActivity.this, (String)msg.obj, new TagAliasCallback() {

			        @Override
			        public void gotResult(int code, String alias, Set<String> tags) {
			        	//该变量未用到
			            String logs ;
			            switch (code) {
			            case 0:
			            	//设置别名成功
			                logs = "Set tag and alias success";
			                break;
			                
			            case 6002:
			            	//设置别名失败,60秒以后重新设置
			                logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
			                handler.sendMessageDelayed(handler.obtainMessage(JPUSH_SET_ALIAS, SharedPreferenceHelper.getLoginUserId(FirstGuideActivity.this)), 1000 * 60);
			               
			                break;
			            
			            default:
			            }
			            
			        }
				    
				});
            	break;
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.first_guide);
		ShareSDK.initSDK(this);
		
		itemViews = new ArrayList<View>();
		View item_one_view = LayoutInflater.from(this).inflate(R.layout.first_guide_item_one, null);
		itemViews.add(item_one_view);
		View item_two_view = LayoutInflater.from(this).inflate(R.layout.first_guide_item_two, null);
		itemViews.add(item_two_view);
		View item_three_view = LayoutInflater.from(this).inflate(R.layout.first_guide_item_three, null);
		itemViews.add(item_three_view);
		View item_four_view = LayoutInflater.from(this).inflate(R.layout.first_guide_item_four, null);
		itemViews.add(item_four_view);
		
		View item_five_view = LayoutInflater.from(this).inflate(R.layout.first_guide_item_five, null);
		itemViews.add(item_five_view);
		
		first_guide_mark_one = (ImageView)findViewById(R.id.first_guide_mark_one);
		first_guide_mark_two = (ImageView)findViewById(R.id.first_guide_mark_two);
		first_guide_mark_three = (ImageView)findViewById(R.id.first_guide_mark_three);
		first_guide_mark_four = (ImageView)findViewById(R.id.first_guide_mark_four);
		first_guide_mark_five = (ImageView)findViewById(R.id.first_guide_mark_five);
		
		//默认情况下
		first_guide_mark_one.setImageResource(R.drawable.icon_dot_white);
		first_guide_mark_two.setImageResource(R.drawable.icon_dot_gray);
		first_guide_mark_three.setImageResource(R.drawable.icon_dot_gray);
		first_guide_mark_four.setImageResource(R.drawable.icon_dot_gray);
		first_guide_mark_five.setImageResource(R.drawable.icon_dot_gray);
		
		LinearLayout first_guide_wechat_login_layout = (LinearLayout)findViewById(R.id.first_guide_wechat_login_layout);
		first_guide_wechat_login_layout.setOnClickListener(this);
		
		LinearLayout first_guide_mobile_login_layout = (LinearLayout)findViewById(R.id.first_guide_mobile_login_layout);
		first_guide_mobile_login_layout.setOnClickListener(this);
		
		LinearLayout first_guide_register_layout = (LinearLayout)findViewById(R.id.first_guide_register_layout);
		first_guide_register_layout.setOnClickListener(this);
		
		LinearLayout first_guide_look_layout = (LinearLayout)findViewById(R.id.first_guide_look_layout);
		first_guide_look_layout.setOnClickListener(this);
		
		first_guide_viewPager = (ViewPager)findViewById(R.id.first_guide_viewPager);
		first_guide_viewPager.setAdapter(new FirstGuideItem(itemViews));
		    
		first_guide_viewPager.setOnPageChangeListener(new FirstGuideItemChangeListener());
	}
	

	// 指引页面数据适配器
	class FirstGuideItem extends PagerAdapter {
		
		private List<View> views;
		
		public FirstGuideItem(List<View> views){
			this.views = views;
		}
		
		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			// TODO Auto-generated method stub
			((ViewPager)arg0).removeView(views.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return views.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			// TODO Auto-generated method stub
			((ViewPager)arg0).addView(views.get(arg1));
			return views.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	
	// 指引页面更改事件监听器
    class FirstGuideItemChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int arg0) {
			if(arg0 == 0){
				first_guide_mark_one.setImageResource(R.drawable.icon_dot_white);
				first_guide_mark_two.setImageResource(R.drawable.icon_dot_gray);
				first_guide_mark_three.setImageResource(R.drawable.icon_dot_gray);
				first_guide_mark_four.setImageResource(R.drawable.icon_dot_gray);
				first_guide_mark_five.setImageResource(R.drawable.icon_dot_gray);
			} else if(arg0 == 1){
				first_guide_mark_one.setImageResource(R.drawable.icon_dot_gray);
				first_guide_mark_two.setImageResource(R.drawable.icon_dot_white);
				first_guide_mark_three.setImageResource(R.drawable.icon_dot_gray);
				first_guide_mark_four.setImageResource(R.drawable.icon_dot_gray);
				first_guide_mark_five.setImageResource(R.drawable.icon_dot_gray);
			} else if(arg0 == 2){
				first_guide_mark_one.setImageResource(R.drawable.icon_dot_gray);
				first_guide_mark_two.setImageResource(R.drawable.icon_dot_gray);
				first_guide_mark_three.setImageResource(R.drawable.icon_dot_white);
				first_guide_mark_four.setImageResource(R.drawable.icon_dot_gray);
				first_guide_mark_five.setImageResource(R.drawable.icon_dot_gray);
			} else if(arg0 == 3){
				first_guide_mark_one.setImageResource(R.drawable.icon_dot_gray);
				first_guide_mark_two.setImageResource(R.drawable.icon_dot_gray);
				first_guide_mark_three.setImageResource(R.drawable.icon_dot_gray);
				first_guide_mark_four.setImageResource(R.drawable.icon_dot_white);
				first_guide_mark_five.setImageResource(R.drawable.icon_dot_gray);
			} else if(arg0 == 4){
				first_guide_mark_one.setImageResource(R.drawable.icon_dot_gray);
				first_guide_mark_two.setImageResource(R.drawable.icon_dot_gray);
				first_guide_mark_three.setImageResource(R.drawable.icon_dot_gray);
				first_guide_mark_four.setImageResource(R.drawable.icon_dot_gray);
				first_guide_mark_five.setImageResource(R.drawable.icon_dot_white);
			}
			
		}
    	
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.first_guide_wechat_login_layout:
			//微信登录
			//测试时，需要打包签名；sample测试时，用项目里面的demokey.keystore
			//打包签名apk,然后才能产生微信的登录
			Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
			authorize(wechat);
			
			break;
			
		case R.id.first_guide_mobile_login_layout:
			//手机登录
			Intent login_intent = new Intent(FirstGuideActivity.this, LoginActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("clazzName", "com.ninetowns.tootooplus.activity.HomeActivity");
			login_intent.putExtra("bundle", bundle);
			login_intent.putExtra("come_from", "first_guide");
			startActivity(login_intent);
			finish();
			break;
			
		case R.id.first_guide_register_layout:
			//手机注册
			Intent register_intent = new Intent(FirstGuideActivity.this, MobileRegisterActivity.class);
			Bundle register_bundle = new Bundle();
			register_bundle.putString("clazzName", "com.ninetowns.tootooplus.activity.HomeActivity");
			register_intent.putExtra("bundle", register_bundle);
			register_intent.putExtra("come_from", "first_guide");
			startActivity(register_intent);
			finish();
			break;
		case R.id.first_guide_look_layout:
			//服务条款
			Intent intent = new Intent(FirstGuideActivity.this, ServiceTermActivity.class);
			startActivity(intent);
			
			break;
		}
		
	}
	
	
	/**
	 * 点击新浪微博或者qq登录
	 * @param plat
	 */
	private void authorize(Platform plat) {
		
		showProgressDialog(this);
		
		plat.setPlatformActionListener(this);
		//关闭SSO授权
		plat.SSOSetting(true);
		plat.showUser(null);
	}

	
	@Override
	public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
		// TODO Auto-generated method stub
		closeProgressDialog(this);
		if (arg1 == Platform.ACTION_USER_INFOR) {
			
			handler.sendMessage(handler.obtainMessage(THIRD_LOGIN_COMPLETE, arg0));
		}
		
	}

	
	@Override
	public void onCancel(Platform arg0, int arg1) {
		// TODO Auto-generated method stub
		closeProgressDialog(this);
		if (arg1 == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(THIRD_LOGIN_CANCEL);
		}
		
	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		closeProgressDialog(this);
		if (arg1 == Platform.ACTION_USER_INFOR) {
			handler.sendEmptyMessage(THIRD_LOGIN_ERROR);
		}
		
	}
}
