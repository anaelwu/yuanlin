package com.ninetowns.tootoopluse.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.library.util.NetworkUtil;
import com.ninetowns.library.util.StringUtils;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.bean.AppVersionBean;
import com.ninetowns.tootoopluse.fragment.VersionDownDialogFragment;
import com.ninetowns.tootoopluse.helper.SharedPreferenceHelper;
import com.ninetowns.tootoopluse.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootoopluse.parser.AppVersionParser;
import com.ninetowns.tootoopluse.util.CommonUtil;
import com.ninetowns.tootoopluse.util.INetConstanst;
import com.ninetowns.ui.Activity.BaseActivity;
import com.ninetowns.ui.widget.dialog.TooSureCancelDialog;
import com.ninetowns.ui.widget.dialog.TooSureCancelDialog.OnDismissDialogInterface;
import com.ninetowns.ui.widget.dialog.TooSureCancelDialog.TooDialogCallBack;

public class StartActivity extends BaseActivity implements INetConstanst ,OnDismissDialogInterface{
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

		};
	};
	private final int DELAY_TIME_MILLIS = 1000;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.start);
		getRtmpHttp();

	}

	private void getRtmpHttp() {
		if (NetworkUtil.isNetworkAvaliable(StartActivity.this)) {
			RequestParamsNet params = new RequestParamsNet();
			params.addQueryStringParameter(
					TootooeNetApiUrlHelper.CHAT_RTMP_HTTP_SERVER_TYPE,
					TootooeNetApiUrlHelper.CHAT_RTMP_SERVER_TYPE_CONSTANTS
							+ ","
							+ TootooeNetApiUrlHelper.HTTP_SERVER_TYPE_CONSTANTS);

			CommonUtil.startGetSend(TootooeNetApiUrlHelper.CHAT_RTMP_HTTP_URL,
					params, new RequestCallBack<String>() {

						private String port;
						private String serverType;

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {

							String jsonStr = responseInfo.result;
							try {
								JSONObject jsonObj = new JSONObject(jsonStr);
								String rtmp_http_status = "";
								String req_rtmp_url = "";
								String req_http_url = "";

								if (jsonObj.has("Status")) {
									rtmp_http_status = jsonObj
											.getString("Status");
								}

								if (rtmp_http_status.equals("1")) {
									if (jsonObj.has("Data")) {
										JSONArray jsonData = jsonObj
												.getJSONArray("Data");
										for (int i = 0; i < jsonData.length(); i++) {
											JSONObject jsonItem = jsonData
													.getJSONObject(i);
											if (jsonItem.has("ServerType")) {
												serverType = jsonItem
														.getString("ServerType");
												if (serverType.equals("8")) {
													req_http_url = jsonItem
															.getString("RequestHttpPath");
												} else {
													req_rtmp_url = jsonItem
															.getString("RequestRtmpPath");
												}
											}

											// if (i == 0) {
											// req_rtmp_url = jsonItem
											// .getString("RequestRtmpPath");
											// }
											// if (i == 1) {
											// req_http_url = jsonItem
											// .getString("RequestHttpPath");
											// }
										}

										SharedPreferenceHelper.saveRtmpHttpUrl(
												StartActivity.this,
												req_rtmp_url, req_http_url);


										
										// 检测版本
										checkVersion();
									}
								}

							} catch (Exception e) {
								e.printStackTrace();
							}

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							ComponentUtil
									.showToast(
											StartActivity.this,
											getResources()
													.getString(
															R.string.errcode_network_response_timeout));
						}
					});
		} else {

		}

	}

	/**
	 * @Fields service : 聊天服务
	 */
//	private ChatService service;
	private Runnable runable = new Runnable() {
		@Override
		public void run() {
			Intent intent = null;

			if (!StringUtils.isEmpty(SharedPreferenceHelper.getLoginUserId(StartActivity.this))) {
				enterHomeActivity();
//				bindChatService();
			} else {
				intent = new Intent(StartActivity.this, FirstGuideActivity.class);
				startActivity(intent);
				finish();
			}

			
		}

	};

	/**
	 * 绑定服务
	 */
//	private void bindChatService() {
//		Intent intent = new Intent(this, ChatService.class);
//		startService(intent);
//		bindService(intent, connection, Context.BIND_AUTO_CREATE);
//	}
//
//	private ServiceConnection connection = new ServiceConnection() {
//
//		public void onServiceDisconnected(ComponentName name) {
//			// service.disconnect();
//			service = null;
//		}
//
//		public void onServiceConnected(ComponentName name, IBinder binder) {
//			service = ((ChatService.LocalBinder) binder).getService();
//			boolean isConnect = false;
//			// 在jni中做了判断
//			//rtmp://182.92.104.81:1938/chatroom/   297
//			
////			if(!service.isConnectRtmp()){
//				isConnect = service.connectRtmp(SharedPreferenceHelper.getReqRtmpUrl(StartActivity.this),
//						SharedPreferenceHelper.getLoginUserId(StartActivity.this));
//				LogUtils.i("isConnect===" + isConnect);	
//			
//			if (isConnect) {
//				unBindChatService();
//				enterHomeActivity();
//				
//			} else {
//				UIUtils.showCenterToast(StartActivity.this, "聊天室长连接失败");
//			}
//
//		}
//
//	};
	
	private void enterHomeActivity() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * @Title: unBindChatService
	 * @Description: 解除绑定服务
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
//	private void unBindChatService() {
//		if (null != connection) {
//			unbindService(connection);
//			connection = null;
//		}
//	}

	private boolean isSure;
	
	// 检测版本
	// 1为强制升级,0为不强制升级
	private void checkVersion() {
		RequestParamsNet requestParamsNet = new RequestParamsNet();
    	requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.VERSION_UP_GRADE_TYPE, "androidBusiness");
    	requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.VERSION_UP_GRADE_VERSION, CommonUtil.getVersionName(this));
    	CommonUtil.xUtilsGetSend(TootooeNetApiUrlHelper.VERSION_UP_GRADE_URL,
				requestParamsNet, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						AppVersionParser versionResponse = new AppVersionParser();
						if(TextUtils.isEmpty(responseInfo.result)){
							handler.postDelayed(runable, DELAY_TIME_MILLIS);
							return;
						}
						String resultStr = new String(responseInfo.result);
						try{
							final AppVersionBean versionBean = versionResponse.parserVersion(resultStr);
							
							if(versionBean.getVersion_lineTypes() != null && versionBean.getVersion_lineTypes().size() > 0){
								SharedPreferenceHelper.saveLineType(StartActivity.this, versionBean.getVersion_lineTypes(), versionBean.getVersion_freeCount());
							}
							String upgrate=versionBean.getVersion_upgradeType();
							if(!TextUtils.isEmpty(upgrate)){
								if(upgrate.equals("1")){

									TooSureCancelDialog tooDialog = new TooSureCancelDialog(
											StartActivity.this);
									tooDialog.setOnDismissDialogInterFace(StartActivity.this);
									tooDialog.setDialogTitle(R.string.rainbo_hint);
									tooDialog.setDialogContent(versionBean.getVersion_msg());
									tooDialog.setCancelButtonVisible(View.GONE);
									tooDialog.setTooDialogCallBack(new TooDialogCallBack() {
												


												@Override
												public void onTooDialogSure() {
													// 下载app
													if (!TextUtils.isEmpty(versionBean.getVersion_downloadUrl())) {
														isSure=true;
														downloadApp(versionBean
																.getVersion_downloadUrl());
													}
												}

												@Override
												public void onTooDialogCancel() {

													if (versionBean.getVersion_upgradeType().equals("0")) {
														handler.postDelayed(runable, DELAY_TIME_MILLIS);
													} else {
														finish();
													}
												}
											});

									tooDialog.show();

								
								}else{

									TooSureCancelDialog tooDialog = new TooSureCancelDialog(
											StartActivity.this);
									tooDialog.setDialogTitle(R.string.rainbo_hint);
									tooDialog.setDialogContent(versionBean.getVersion_msg());
									tooDialog.setTooDialogCallBack(new TooDialogCallBack() {
												@Override
												public void onTooDialogSure() {
													// 下载app
													if (!TextUtils.isEmpty(versionBean.getVersion_downloadUrl())) {

														downloadApp(versionBean
																.getVersion_downloadUrl());
													}
												}

												@Override
												public void onTooDialogCancel() {

														handler.postDelayed(runable, DELAY_TIME_MILLIS);
												}
											});

								
									tooDialog.show();
								
								
								
								
								}
								
							}else{
								handler.postDelayed(runable, DELAY_TIME_MILLIS);
							}
							
							
							
						}catch(JSONException e){
							e.printStackTrace();
						}
						
					} 

					@Override
					public void onFailure(HttpException error, String msg) {
						ComponentUtil
						.showToast(
								StartActivity.this,
								getResources()
										.getString(
												R.string.errcode_network_response_timeout));
						
					}
				});

	}
	
	private void downloadApp(String download_url) {
		// 启动DialogFragment
		FragmentManager fragmentManager = getSupportFragmentManager();
		VersionDownDialogFragment versionFragment = new VersionDownDialogFragment(
				download_url, "start");
		if (fragmentManager != null) {
			// 屏幕较小，以全屏形式显示
			FragmentTransaction transaction = fragmentManager
					.beginTransaction();
			// 指定一个过渡动画
			transaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			transaction.addToBackStack(null);
			versionFragment.show(fragmentManager, "dialog");
		}

	}
@Override
protected void onDestroy() {
	super.onDestroy();
	isSure=false;
}
	@Override
	public void OnButtonDismiss() {
		if(isSure){
			
		}else{
			finish();
		}
	
		
		
	}
}
