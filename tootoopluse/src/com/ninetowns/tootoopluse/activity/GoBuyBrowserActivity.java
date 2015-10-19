package com.ninetowns.tootoopluse.activity;

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
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.fragment.GoBuyBrowserFragment;
import com.ninetowns.tootoopluse.fragment.NinetownsBrowserFragment;
import com.ninetowns.tootoopluse.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootoopluse.util.BrowserTitleUpdateUtil;

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
	@ViewInject(R.id.two_or_one_iv_back)
	private ImageView mImageViewBack;

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
		bundle.putString(NinetownsBrowserFragment.EXTRA_BROWSER_ADDRESS,
				urlcontent);
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
		mTextTitle.setText(title);
	}

}