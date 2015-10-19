package com.ninetowns.tootooplus.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.cookie.Cookie;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.bean.AlbumPhotoBean;
import com.ninetowns.tootooplus.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootooplus.util.AuthenticationUtil;
import com.ninetowns.tootooplus.util.AuthenticationUtil.HttpAuthenticationHandler;
import com.ninetowns.tootooplus.util.BrowserTitleUpdateUtil;
import com.ninetowns.ui.widget.dialog.ProgressiveDialog;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GoBuyBrowserFragment extends Fragment implements
		View.OnClickListener {

	public static final String EXTRA_BROWSER_ADDRESS = "browser_url";
	public static final String EXTRA_OVERRIDE_SCHEME_PREFIX = "override_scheme_prefix";
	public static final String EXTRA_USER_AGENT = "user_agent";

	private List<AlbumPhotoBean> listLocal = new ArrayList<AlbumPhotoBean>();
	private ImageButton mBackwardBtn, mForwardBtn;
	private WebView mWebView;
	private ProgressBar mProgressBar;
	private FrameLayout mWebviewContainer;

	private int mBackwardNormalRes = R.drawable.browser_backward_normal;
	private int mBackwardSelectedRes = R.drawable.browser_backward_pressed;
	private int mForwardNormalRes = R.drawable.browser_forward_normal;
	private int mForwardSelectedRes = R.drawable.browser_forward_pressed;

	private BrowserTitleUpdateUtil mBrowserTitleUpdate;
	private String mOverrideSchemePrefix;
	private String mExtraUserAgent;
	private String mDefaultUserAgent;
	private Bundle intentbundle;

	private void loadPage(Bundle bundle) {
		if (bundle == null) {
			return;
		}
		String data = bundle.getString(EXTRA_BROWSER_ADDRESS);
		if (data == null) {
			return;
		}

		if (bundle != null && bundle.size() > 0) {
			CookieSyncManager.createInstance(getActivity()
					.getApplicationContext());
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.setAcceptCookie(true);
			CookieSyncManager.getInstance().sync();
		}
		loadUrl(data);
	}

	public void setBrowserTitleUpdate(BrowserTitleUpdateUtil browserTitleUpdate) {
		this.mBrowserTitleUpdate = browserTitleUpdate;
	}

	private void loadUrl(String url) {
		mWebView.loadUrl(url);
	}

	public boolean onBackPressed() {
		if (mWebView.canGoBack()) {
			backward();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获得bundle
		getBundleType();
	}

	/***
	 * 
	 * @Title: getBundleType
	 * @Description: 获得bundle的值
	 * @param
	 * @return
	 * @throws
	 */
	private void getBundleType() {
		intentbundle = getActivity().getIntent().getBundleExtra(
				ConstantsTooTooEHelper.BUNDLE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.go_buty_browser_main, container,
				false);
		mBackwardBtn = (ImageButton) view.findViewById(R.id.browser_backward);
		mForwardBtn = (ImageButton) view.findViewById(R.id.browser_forward);
		mProgressBar = (ProgressBar) view.findViewById(R.id.browser_progress);
		mWebView = (WebView) view.findViewById(R.id.browser_web);
		mWebviewContainer = (FrameLayout) view
				.findViewById(R.id.webview_container);

		mBackwardBtn.setImageResource(mBackwardNormalRes);
		mForwardBtn.setImageResource(mForwardNormalRes);

		mBackwardBtn.setClickable(true);
		mForwardBtn.setClickable(true);
		mBackwardBtn.setOnClickListener(this);
		mForwardBtn.setOnClickListener(this);

		Bundle bundle = getArguments();
		mOverrideSchemePrefix = bundle.getString(EXTRA_OVERRIDE_SCHEME_PREFIX);
		mExtraUserAgent = bundle.getString(EXTRA_USER_AGENT);
		createWeb();

		loadPage(bundle);
		return view;
	}

	private WebViewClient webViewClient = new WebViewClient() {
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// mRefreshBtn.setImageResource(R.drawable.browser_refresh_normal);
			// mRefreshBtn.setEnabled(false);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (!TextUtils.isEmpty(mOverrideSchemePrefix)
					&& getActivity() != null) {
				Uri uri = Uri.parse(url);
				String scheme = uri.getScheme();
				if (scheme.startsWith(mOverrideSchemePrefix)) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(uri);
					intent.addCategory(Intent.CATEGORY_BROWSABLE);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					intent.putExtra(ShareCompat.EXTRA_CALLING_PACKAGE,
							getActivity().getPackageName());
					try {
						startActivity(intent);
					} catch (ActivityNotFoundException e) {
					} catch (SecurityException e) {
					}
					return true;
				}
			}
			if (url.startsWith("tel:")) {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
				} catch (SecurityException e) {
				}
				return true;
			}
			if (MailTo.isMailTo(url)) {
				Intent intent = new Intent(Intent.ACTION_SENDTO);
				intent.setData(Uri.parse(url));
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
				} catch (SecurityException e) {
				}
				return true;
			}
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onReceivedHttpAuthRequest(WebView view,
				HttpAuthHandler handler, String host, String realm) {
			String username = null;
			String password = null;
			boolean reuseHttpAuthUsernamePassword = handler
					.useHttpAuthUsernamePassword();
			if (reuseHttpAuthUsernamePassword && view != null) {
				String[] credentials = view.getHttpAuthUsernamePassword(host,
						realm);
				if (credentials != null && credentials.length == 2) {
					username = credentials[0];
					password = credentials[1];
				}
			}
			if (username != null && password != null) {
				handler.proceed(username, password);
			} else {
				showHttpAuthentication(handler, host, realm, null, username,
						password, 0);
			}
		}

		public void onPageFinished(WebView view, String url) {
			// mRefreshBtn.setImageResource(R.drawable.browser_refresh_pressed);
			// mRefreshBtn.setEnabled(true);

			System.out.println("++++++++++hc+++dsads+++++++++>" + url);
		}

	};
	private String netName;
	private String urlContent;
	private WebChromeClient webChromeClient = new WebChromeClient() {

		@Override
		public void onReceivedTitle(WebView view, String title) {
			netName = title;
			urlContent = view.getUrl();
			if (null != mBrowserTitleUpdate) {
				mBrowserTitleUpdate.updateTitle(title, urlContent);
			}
		}

		public void onProgressChanged(WebView view, int newProgress) {
			int tempProgress = newProgress > 50 ? 100 : newProgress;
			if (mProgressBar.getVisibility() == View.GONE) {
				showProgressView();
			}
			mProgressBar.setProgress(tempProgress);
			if (newProgress == 100) {
				dismissProgressView();
				changeStatus();
			}
		}
	};
	private ProgressiveDialog progressDialog;

	private void showProgressView() {
		AlphaAnimation anim = new AlphaAnimation(0, 1.0f);
		anim.setDuration(200);
		mProgressBar.startAnimation(anim);
		mProgressBar.setVisibility(View.VISIBLE);

	}

	private void dismissProgressView() {
		AlphaAnimation anim = new AlphaAnimation(1.0f, 0);
		anim.setDuration(200);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mProgressBar.setVisibility(View.GONE);
			}
		});
		mProgressBar.startAnimation(anim);
		mProgressBar.setVisibility(View.VISIBLE);

	}

	/**
	 * 
	 * @Title: showHttpAuthentication
	 * @Description: 展示dialog
	 * @param
	 * @return
	 * @throws
	 */
	void showHttpAuthentication(final HttpAuthHandler handler,
			final String host, final String realm, final String title,
			final String name, final String password, int focusId) {
		AuthenticationUtil.showHttpAuthentication(getActivity(),
				new HttpAuthenticationHandler() {

					@Override
					public void process(String name, String password) {
						mWebView.setHttpAuthUsernamePassword(host, realm, name,
								password);
						handler.proceed(name, password);
					}

					@Override
					public void cancel() {
						handler.cancel();
					}
				}, host, realm, title, name, password, focusId);
	}

	/**
	 * 
	 * @Title: createWeb
	 * @Description: 创建浏览器
	 * @param
	 * @return
	 * @throws
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	private void createWeb() {
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setPluginState(PluginState.ON);
		settings.setBuiltInZoomControls(true);
		settings.setLoadWithOverviewMode(true);
		if (TextUtils.isEmpty(mDefaultUserAgent)) {
			mDefaultUserAgent = settings.getUserAgentString();
		}
		if (!TextUtils.isEmpty(mExtraUserAgent)) {
			String userAgent = mDefaultUserAgent;
			StringBuffer buffer = new StringBuffer();
			buffer.append(userAgent);
			buffer.append(" ");
			buffer.append(mExtraUserAgent);
			settings.setUserAgentString(buffer.toString());
		}

		mWebView.requestFocus();
		mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.setMapTrackballToArrowKeys(false);
		mWebView.setWebChromeClient(webChromeClient);
		mWebView.setWebViewClient(webViewClient);
		mWebView.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View paramView, int paramInt,
					KeyEvent paramKeyEvent) {
				return false;
			}
		});
		mWebView.setDownloadListener(new DownloadListener() {
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				return;
			}
		});
		mWebView.setVerticalScrollbarOverlay(true);
	}

	@Override
	public void onClick(View v) {
		if (v == mBackwardBtn) {
			backward();
			return;
		} else if (v == mForwardBtn) {
			forward();
			return;
		}
	}

	private void backward() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
		}
	}

	private void forward() {
		if (mWebView.canGoForward()) {
			mWebView.goForward();
		}
	}

	private void changeStatus() {
		if (mWebView.canGoBack()) {
			mBackwardBtn.setImageResource(mBackwardSelectedRes);
		} else {
			mBackwardBtn.setImageResource(mBackwardNormalRes);
		}
		if (mWebView.canGoForward()) {
			mForwardBtn.setImageResource(mForwardSelectedRes);
		} else {
			mForwardBtn.setImageResource(mForwardNormalRes);
		}
	}

	public void onResume() {
		super.onResume();
		mWebView.onResume();
		WebSettings settings = mWebView.getSettings();
		settings.setBuiltInZoomControls(true);
		mWebView.resumeTimers();
	}

	public void onPause() {
		super.onPause();
		mWebView.pauseTimers();
		mWebView.onPause();
	}

	public void onStop() {
		super.onStop();
	}

	public void onDestroy() {
		mWebView.setVisibility(View.GONE);
		destroyWeb();
		super.onDestroy();
	}

	private void destroyWeb() {
		mWebviewContainer.removeView(mWebView);
		mWebView.stopLoading();
		mWebView.loadData("<a></a>", "text/html", "utf-8");
		mWebView.clearCache(false);
		mWebView.clearHistory();
		mWebView.destroyDrawingCache();
		mWebView.removeAllViews();
		mWebView.clearView();
		mWebView.clearDisappearingChildren();
		mWebView.freeMemory();
		mWebView.clearFocus();
		mWebView.clearMatches();
		mWebView.clearSslPreferences();
		mWebView.destroy();
	}

	public static Bundle getCookiesBundle(List<Cookie> cookies) {
		Bundle bundle = new Bundle();
		if (cookies == null) {
			return bundle;
		}
		for (Cookie c : cookies) {
			bundle.putString(c.getName(), c.getValue());
		}
		return bundle;
	}

	public void showProgressDialog(Context context) {
		if ((!((Activity) context).isFinishing()) && (progressDialog == null)) {
			progressDialog = new ProgressiveDialog(context);
		}
		if (progressDialog != null) {
			// progressDialog.setMessage(R.string.loading_gather);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
		}

	}

	/**
	 * 
	 * @Title: ComponentUtil.java
	 * @Description: 取消dialog
	 * @author wuyulong
	 * @date 2014-7-14 下午4:23:48
	 * @param
	 * @return void
	 */
	public void closeProgressDialog(Context context) {
		if (progressDialog != null) {
			progressDialog.isShowing();
			progressDialog.dismiss();
		}
	}

	

	
}