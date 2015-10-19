package com.ninetowns.tootooplus.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.NetworkUtil;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.fragment.GoBuyBrowserFragment;
import com.ninetowns.tootooplus.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootooplus.helper.CreateXmlHelper;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootooplus.util.BrowserTitleUpdateUtil;
import com.ninetowns.tootooplus.util.CommonUtil;

/**
 * 
 * @ClassName: GoBuyBrowserActivity
 * @Description: 去购买的网站
 * @author wuyulong
 * @date 2015-2-6 上午10:02:13
 * 
 */
public class GoBuyBrowserActivity extends FragmentActivity implements
		BrowserTitleUpdateUtil {
	public static final String BROWSER_TITLE_TEXT = "Browser_Title_Text";
	private GoBuyBrowserFragment fragment;
	@ViewInject(R.id.ibtn_left)
	private ImageButton ibtnBack;
	@ViewInject(R.id.tv_go_buy_title)
	private TextView mTextTitle;
	private String mEditextContent;
	@ViewInject(R.id.tv_go)
	private TextView mTvGo;
	private String urlcontent;
	private Bundle bundle;
	private static String activityId;
	private static String storyId;
	private static String wishId;
	private static String typep;
	private List<String> mBrowseList=new ArrayList<String>();
    @ViewInject(R.id.two_or_one_iv_back)
	private ImageView mImageViewBack;
	public static void skipPar(String type, String typekey) {
		typep = type;
		if (type.equals("0")) {// 活动
			activityId = typekey;

		} else if (type.equals("1")) {// 心愿
			storyId = typekey;

		} else if (type.equals("2")) {// 点评
			wishId = typekey;
		}

	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getTypeBundle();
		setContentView(R.layout.go_buy_activity_browser);
		
		mImageViewBack = (ImageView) findViewById(R.id.two_or_one_iv_back);
		mImageViewBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		ViewUtils.inject(this);
		if (!TextUtils.isEmpty(urlcontent)) {
			setDisplayFragment();
		}

	}

	private void getTypeBundle() {
		bundle = getIntent().getBundleExtra(ConstantsTooTooEHelper.BUNDLE);
		urlcontent = bundle.getString("url");
		if (!TextUtils.isEmpty(urlcontent)) {
			if (urlcontent.length() > 5) {
				String spit = urlcontent.substring(0, 5);
				if (spit.contains("http")) {
				} else {
					urlcontent = "http://" + urlcontent;
				}
			}
		}

	}

	/**
	 * 
	 * @Title: setDisplayFragment
	 * @Description: 展示网络内容
	 * @param
	 * @return
	 * @throws
	 */
	private void setDisplayFragment() {

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction ft = fragmentManager.beginTransaction();
		fragment = new GoBuyBrowserFragment();
		fragment.setBrowserTitleUpdate(this);
		Bundle bundle = new Bundle();
		bundle.putString(GoBuyBrowserFragment.EXTRA_BROWSER_ADDRESS, urlcontent);
		fragment.setArguments(bundle);
		ft.add(R.id.content, fragment);
		ft.commit();
		getSupportFragmentManager().executePendingTransactions();
	}

	@OnClick(R.id.ibtn_left)
	public void backView(View view) {
		finish();

	}

	@Override
	public void updateTitle(String title, String url) {
		mBrowseList.add(url);
		mTextTitle.setText(title);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		createBrowseXml();
	}

	/**
	 * 
	 * @Title: createBrowseXml
	 * @Description: 创建浏览
	 * @param
	 * @return
	 * @throws
	 */
	public void createBrowseXml() {

		if ((NetworkUtil.isNetworkAvaliable(TootooPlusApplication
				.getAppContext()))) {
			String loginUserId= SharedPreferenceHelper
				.getLoginUserId(TootooPlusApplication
						.getAppContext());
			// 显示进度
			RequestParamsNet requestParamsNet = new RequestParamsNet();
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.USER_ID,loginUserId);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.APPLICATIONID,
					TootooeNetApiUrlHelper.APPLICATIONID_PARAM);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.TYPE, typep);
			if (!TextUtils.isEmpty(activityId)) {
				requestParamsNet.addQueryStringParameter(
						ConstantsTooTooEHelper.ACTIVITYID, activityId);
			}
			if (!TextUtils.isEmpty(storyId)) {
				requestParamsNet.addQueryStringParameter(
						ConstantsTooTooEHelper.STORYID, storyId);
			}
			if (!TextUtils.isEmpty(wishId)) {
				requestParamsNet.addQueryStringParameter(
						ConstantsTooTooEHelper.STORYID, wishId);

			}
			@SuppressWarnings("static-access")
			File file = CreateXmlHelper.getInstance().createXml(TootooPlusApplication.getAppContext(), mBrowseList, urlcontent,loginUserId);
			if(file!=null){
				requestParamsNet.addBodyParameter("xml", file);
			}else{
				cleanId();
				return;
			}
			
			CommonUtil.xUtilsPostSend(TootooeNetApiUrlHelper.BROWSER,
					requestParamsNet, new RequestCallBack<String>() {

						private String status;

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String jsonStr = new String(responseInfo.result);
							try {
								JSONObject jsobj = new JSONObject(jsonStr);
								if (jsobj.has("Status")) {
									status = jsobj.getString("Status");
									if (status.equals("1")) {

									} else if (jsonStr.equals("1223")) {
									}
								}
								cleanId();
							} catch (JSONException e) {
								e.printStackTrace();
								cleanId();
							}

						}

						

						@Override
						public void onFailure(HttpException error, String msg) {
							cleanId();
							/*ComponentUtil.showToast(
									GoBuyBrowserActivity.this,
									GoBuyBrowserActivity.this.getResources().getString(
											R.string.errcode_network_response_timeout));*/
						}
					});

		} else {
			cleanId();
			/*ComponentUtil.showToast(
					this,
					this.getResources().getString(
							R.string.errcode_network_response_timeout));*/
		}

	}
	/** 
	* @Title: cleanId 
	* @Description: TODO
	* @param  
	* @return   
	* @throws 
	*/
	private void cleanId() {
		if (!TextUtils.isEmpty(activityId)) {
			activityId=null;
		}
		if (!TextUtils.isEmpty(storyId)) {
			storyId=null;
		}
		if (!TextUtils.isEmpty(wishId)) {
			wishId=null;
		}
		mBrowseList.clear();
	}

}