package com.ninetowns.tootooplus.util;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.BaseCommonUtil;
import com.ninetowns.library.util.NetworkUtil;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.activity.FirstGuideActivity;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.fragment.BaseChatFragment;
import com.ninetowns.tootooplus.fragment.FirstActChatDialog;
import com.ninetowns.tootooplus.fragment.FirstBaichiChatDialog;
import com.ninetowns.tootooplus.fragment.FirstBigEateGoToGroupDialog;
import com.ninetowns.tootooplus.fragment.FirstChatWisDialog;
import com.ninetowns.tootooplus.fragment.FirstComitGroupDialog;
import com.ninetowns.tootooplus.fragment.FirstCommentCountGetBaiChiDialog;
import com.ninetowns.tootooplus.fragment.FirstCreateCommentGuideDialog;
import com.ninetowns.tootooplus.fragment.FirstCreateWishGuideDialog;
import com.ninetowns.tootooplus.fragment.FirstDetailFaceToFaceGuideDialog;
import com.ninetowns.tootooplus.fragment.FirstGetGroupMembersDialog;
import com.ninetowns.tootooplus.fragment.FirstGoToBuyGuideDialog;
import com.ninetowns.tootooplus.fragment.FirstGuideClickGroupCountDialog;
import com.ninetowns.tootooplus.fragment.FirstGuideDropDialog;
import com.ninetowns.tootooplus.fragment.FirstGuideFaceToFace;
import com.ninetowns.tootooplus.fragment.FirstGuideLookGroupCountDialog;
import com.ninetowns.tootooplus.fragment.FirstLookGroupDialog;
import com.ninetowns.tootooplus.fragment.FirstLookinfoDialog;
import com.ninetowns.tootooplus.fragment.FirstLooknumberGroupDialog;
import com.ninetowns.tootooplus.fragment.FirstShareDialog;
import com.ninetowns.tootooplus.fragment.FirstViewPagerGuideDialog;
import com.ninetowns.tootooplus.fragment.FirstWriteCommentGuideDialog;
import com.ninetowns.tootooplus.fragment.FirstsmallEateGoToGroupDialog;
import com.ninetowns.tootooplus.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.ui.Activity.BaseActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * 
 * @ClassName: CommonUtil
 * @Description: 项目公共的工具类
 * @author wuyulong
 * @date 2015-1-22 下午5:01:48
 * 
 */
public class CommonUtil extends BaseCommonUtil {

	public static final String TOO_HOST = Environment
			.getExternalStorageDirectory().getPath() + "/tootoo";
	public static final String PHOTO_HOST = TOO_HOST + File.separator + "photo";


	/**
	 * @author huangchao 拼接app获取数据的url
	 * @param relativeUrl
	 * @return
	 */
	public static String appInterfaceUrl(String relativeUrl) {

		return SharedPreferenceHelper.getReqHttpUrl(TootooPlusApplication
				.getAppContext()) + "/" + relativeUrl;
	}

	/**
	 * @author huangchao 获取网络数据的get方法
	 * @param relativeUrl
	 * @param requestParamsNet
	 * @param requestCallBack
	 */
	public static void xUtilsGetSend(String relativeUrl,
			RequestParamsNet requestParamsNet,
			RequestCallBack<String> requestCallBack) {
		HttpUtils httpUtils = new HttpUtils();
		String url = appInterfaceUrl(relativeUrl);
		sysAipUrl(url, requestParamsNet);
		httpUtils.setDes(TootooPlusApplication.ISDES);
		if (NetworkUtil.isNetworkAvaliable(TootooPlusApplication
				.getAppContext())) {
			httpUtils.send(HttpMethod.GET, url, requestParamsNet,
					requestCallBack);
		} else {
			UIUtils.showCenterToast(TootooPlusApplication.getAppContext(),
					"无网络连接");
		}

	}
	

	

	/**
	 * 
	 * @Title: xUtilsGetSend
	 * @Description: 获取网络数据的post方法
	 * @return void
	 * @throws
	 */
	public static void xUtilsPostSend(String relativeUrl,
			RequestParamsNet requestParamsNet,
			RequestCallBack<String> requestCallBack) {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.setDes(TootooPlusApplication.ISDES);
		String url = appInterfaceUrl(relativeUrl);
		sysAipUrl(url, requestParamsNet);
		httpUtils.send(HttpMethod.POST, url, requestParamsNet, requestCallBack);
	}
	/**
	 * @author huangchao 启动页面获取rtmp和http相关数据的方法(注意：只在启动页面用到)
	 * @param relativeUrl
	 * @param requestParamsNet
	 * @param requestCallBack
	 */
	public static void startGetSend(String relativeUrl,
			RequestParamsNet requestParamsNet,
			RequestCallBack<String> requestCallBack) {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.setDes(false);
		String url = startGetHttpAndRtmp(relativeUrl);
		sysAipUrl(url, requestParamsNet);
		httpUtils.send(HttpMethod.GET, url, requestParamsNet, requestCallBack);
	}
	public static void uploadPostXutil(String relativeUrl,
			RequestParamsNet requestParamsNet,
			RequestCallBack<String> requestCallBack) {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.setDes(false);
		String url = appInterfaceUrl(relativeUrl);
		sysAipUrl(url, requestParamsNet);
		httpUtils.send(HttpMethod.POST, url, requestParamsNet, requestCallBack);
	}
	public static void testxUtilsPostSend(String relativeUrl,
			RequestParamsNet requestParamsNet,
			RequestCallBack<String> requestCallBack) {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.setDes(TootooPlusApplication.ISDES);
//		String url = appInterfaceUrl(relativeUrl);
		sysAipUrl(relativeUrl, requestParamsNet);
		httpUtils.send(HttpMethod.POST, relativeUrl, requestParamsNet, requestCallBack);
	}
	public static void testxUtilsGetSend(String relativeUrl,
			RequestParamsNet requestParamsNet,
			RequestCallBack<String> requestCallBack) {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.setDes(TootooPlusApplication.ISDES);
//		String url = appInterfaceUrl(relativeUrl);
		sysAipUrl(relativeUrl, requestParamsNet);

		if (NetworkUtil.isNetworkAvaliable(TootooPlusApplication
				.getAppContext())) {
			httpUtils.send(HttpMethod.GET, relativeUrl, requestParamsNet,
					requestCallBack);
		} else {
			UIUtils.showCenterToast(TootooPlusApplication.getAppContext(),
					"无网络连接");
		}

	}
	/**
	 * @author huangchao 打印url和参数
	 * @param url
	 * @param par
	 */
	private static void sysAipUrl(String url, RequestParamsNet par) {
		List<NameValuePair> request = par.getQueryStringParams();
		StringBuilder stringBuild = new StringBuilder();
		if (request != null) {
			for (int i = 0; i < request.size(); i++) {
				if (i == request.size() - 1) {
					stringBuild.append(request.get(i).toString());
				} else {
					stringBuild.append(request.get(i).toString()).append("&");
				}

			}
		}
		 if (!TootooeNetApiUrlHelper.PUBLIC_OR_IN_NET) {
		System.out.println("+++>" + url + stringBuild.toString());
		 }
	}

	/**
	 * @author huangchao 公用打印log(外网不打印，内网打印)
	 * @param logTag
	 * @param logContent
	 */
	public static void sysApiLog(String logTag, Object logContent) {
		if (!TootooeNetApiUrlHelper.PUBLIC_OR_IN_NET) {
			System.out.println("+++" + logTag + "+++>" + logContent);
		}
	}

	/**
	 * 头像加载图片设置 进行圆角化
	 */
	public static final DisplayImageOptions OPTIONS_HEADPHOTO = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.icon_default_photo)
			.showImageForEmptyUri(R.drawable.icon_default_photo)
			.showImageOnFail(R.drawable.icon_default_photo)
			.resetViewBeforeLoading(true).delayBeforeLoading(1000)
			.cacheInMemory(true).cacheOnDisc(true)
			.displayer(new RoundedBitmapDisplayer(1000))
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	/**
	 * 大头像加载图片设置 进行圆角化
	 */
	public static final DisplayImageOptions OPTIONS_BIG_HEADPHOTO = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.icon_user_large_logo)
			.showImageForEmptyUri(R.drawable.icon_user_large_logo)
			.showImageOnFail(R.drawable.icon_user_large_logo)
			.resetViewBeforeLoading(true).delayBeforeLoading(1000)
			.cacheInMemory(true).cacheOnDisc(true)
			.displayer(new RoundedBitmapDisplayer(1000))
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	/**
	 * 大头像背景加载图片设置 进行圆角化
	 */
	public static final DisplayImageOptions OPTIONS_BIG_HEADPHOTO_BG = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.icon_user_large_logo_bg)
			.showImageForEmptyUri(R.drawable.icon_user_large_logo_bg)
			.showImageOnFail(R.drawable.icon_user_large_logo_bg)
			.resetViewBeforeLoading(true).delayBeforeLoading(1000)
			.cacheInMemory(true).cacheOnDisc(true)
			.displayer(new RoundedBitmapDisplayer(1000))
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	// 个人封面图加载设置
	public static final DisplayImageOptions MINE_COVER_OPTIONS = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.bg_mine_cover)
			.showImageForEmptyUri(R.drawable.bg_mine_cover)
			.showImageOnFail(R.drawable.bg_mine_cover)
			// .resetViewBeforeLoading(true).delayBeforeLoading(1000)
			.cacheInMemory(true)//
			.cacheOnDisc(true)// 此处用过期的图片
			.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)// 此处防止oom
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

	/**
	 * 头像加载图片设置 进行圆角化
	 */
	public static final DisplayImageOptions OPTIONS_HEADPHOTO_NOROUND = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.icon_default_photo)
			.showImageForEmptyUri(R.drawable.icon_default_photo)
			.showImageOnFail(R.drawable.icon_default_photo)
			.resetViewBeforeLoading(true).delayBeforeLoading(1000)
			.cacheInMemory(true).cacheOnDisc(true).build();

	// 相册缩略图片加载设置
	public static final DisplayImageOptions OPTIONS_ALBUM = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.video_default_img)
			.showImageForEmptyUri(R.drawable.video_default_img)
			.showImageOnFail(R.drawable.video_default_img)
			// .displayer(new FadeInBitmapDisplayer(1000, true, true, true))
			// .resetViewBeforeLoading(true).delayBeforeLoading(1000)
			.cacheInMemory(true).cacheOnDisk(true).cacheOnDisc(true)
			// 此处用过期的图片
			.bitmapConfig(Bitmap.Config.RGB_565)
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
	public static final DisplayImageOptions OPTIONS_ALBUM_DETAIL = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.video_default_img)
			.showImageForEmptyUri(R.drawable.video_default_img)
			.showImageOnFail(R.drawable.video_default_img)
			// .displayer(new SimpleBitmapDisplayer())
			// .displayer(new FadeInBitmapDisplayer(1000, true, true, true))
			// .resetViewBeforeLoading(true).delayBeforeLoading(1000)
			.cacheInMemory(false).cacheOnDisk(true).cacheOnDisc(true)
			// 此处用过期的图片
			.bitmapConfig(Bitmap.Config.RGB_565)
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
	// .bitmapConfig(Bitmap.Config.RGB_565).handler(new Handler()).build();
	public static final DisplayImageOptions OPTIONS_IMAGE = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.icon_rec_create_pic)
			.showImageForEmptyUri(R.drawable.icon_rec_create_pic)
			.showImageOnFail(R.drawable.icon_rec_create_pic)
			.cacheInMemory(true).cacheOnDisk(true).cacheOnDisc(true)
			// 此处用过期的图片
			.bitmapConfig(Bitmap.Config.RGB_565)
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
	public static final DisplayImageOptions OPTIONS_VIDEO = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.icon_rec_create_recorde)
			.showImageForEmptyUri(R.drawable.icon_rec_create_recorde)
			.showImageOnFail(R.drawable.icon_rec_create_recorde)
			.cacheInMemory(true).cacheOnDisk(true).cacheOnDisc(true)
			// 此处用过期的图片
			.bitmapConfig(Bitmap.Config.RGB_565)
			.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

	// 喜欢缩略图片加载设置
	public static final DisplayImageOptions OPTIONS_LIKE_LIST = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.video_default_img)
			.showImageForEmptyUri(R.drawable.video_default_img)
			.showImageOnFail(R.drawable.video_default_img)
			// .resetViewBeforeLoading(true).delayBeforeLoading(1000)
			.cacheInMemory(true)//
			.cacheOnDisc(true)// 此处用过期的图片
			.cacheOnDisk(true).bitmapConfig(Bitmap.Config.RGB_565)// 此处防止oom
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

	// .bitmapConfig(Bitmap.Config.RGB_565).handler(new Handler()).build();

	/**
	 * @author huangchao 拼接启动页面获取rtmp和http的url
	 * @param relativeUrl
	 * @return
	 */
	public static String startGetHttpAndRtmp(String relativeUrl) {
		if (TootooeNetApiUrlHelper.PUBLIC_OR_IN_NET) {
			return TootooeNetApiUrlHelper.BASES_RTMP_PUBLIC_URL + relativeUrl;
		} else {
			return TootooeNetApiUrlHelper.BASES_RTMP_IN_URL + relativeUrl;
		}
	}

	

	/**
	 * 
	 * @Title: getRect
	 * @Description: 获得视图的矩阵
	 * @param 传入当前的view
	 * @return
	 * @throws
	 */
	public static Rect getRect(View view) {
		int[] location = new int[2];

		view.getLocationOnScreen(location);

		// 得到组件到矩阵
		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ view.getWidth(), location[1] + view.getHeight());
		return anchorRect;
	}

	/**
	 * 
	 * @Title: setUserGrade
	 * @Description: 设置等级 在右边
	 * @param oration
	 *            :left,bottom,right,top
	 * @return
	 * @throws
	 */
	public static void setUserGrade(CheckedTextView checkedTextView,
			String userGrade, String oration) {
		if (!TextUtils.isEmpty(userGrade)) {

			if (userGrade.equals("1")) {
				Drawable drawable = TootooPlusApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_1);
				justOrigatiion(checkedTextView, oration, drawable);

			} else if (userGrade.equals("2")) {
				Drawable drawable = TootooPlusApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_2);
				justOrigatiion(checkedTextView, oration, drawable);
			} else if (userGrade.equals("3")) {
				Drawable drawable = TootooPlusApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_3);
				justOrigatiion(checkedTextView, oration, drawable);
			} else if (userGrade.equals("4")) {
				Drawable drawable = TootooPlusApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_4);
				justOrigatiion(checkedTextView, oration, drawable);
			} else if (userGrade.equals("5")) {
				Drawable drawable = TootooPlusApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_5);
				justOrigatiion(checkedTextView, oration, drawable);
			} else if (userGrade.equals("100")) {
				Drawable drawable = TootooPlusApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_store_vip);
				justOrigatiion(checkedTextView, oration, drawable);
			} else {
				// checkedTextView.setVisibility(View.INVISIBLE);
			}

		}
	}

	/**
	 * 
	 * @Title: justOrigatiion
	 * @Description: 判断等级图片在textview的哪边
	 * @param
	 * @return
	 * @throws
	 */
	private static void justOrigatiion(CheckedTextView imageView,
			String oration, Drawable drawable) {
		if (oration.equals("right")) {
			imageView.setCompoundDrawablesWithIntrinsicBounds(null, null,
					drawable, null);
		} else if (oration.equals("bottom")) {
			imageView.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable);
		} else if (oration.equals("top")) {
			imageView.setCompoundDrawablesWithIntrinsicBounds(null, drawable,
					null, null);
		} else if (oration.equals("left")) {
			imageView.setCompoundDrawablesWithIntrinsicBounds(drawable, null,
					null, null);
		}
	}

	/**
	 * 显示用户vip等级
	 * 
	 * @param vipView
	 * @param userGrade
	 */
	public static void showVip(ImageView vipView, String userGrade) {
		if (userGrade.equals("1")) {
			vipView.setImageResource(R.drawable.icon_vip_1);
		} else if (userGrade.equals("2")) {
			vipView.setImageResource(R.drawable.icon_vip_2);
		} else if (userGrade.equals("3")) {
			vipView.setImageResource(R.drawable.icon_vip_3);
		} else if (userGrade.equals("4")) {
			vipView.setImageResource(R.drawable.icon_vip_4);
		} else if (userGrade.equals("5")) {
			vipView.setImageResource(R.drawable.icon_vip_5);
		} else if (userGrade.equals("100")) {
			vipView.setImageResource(R.drawable.icon_store_vip);
		} else {
			vipView.setVisibility(View.GONE);
		}
	}

	/**
	 * 显示个人中心和个人首页用户vip等级(比普通vip等级图片大)
	 * 
	 * @param vipView
	 * @param userGrade
	 */
	public static void showCenterVip(ImageView vipView, String userGrade) {
		if (userGrade.equals("1")) {
			vipView.setImageResource(R.drawable.icon_center_vip_1);
		} else if (userGrade.equals("2")) {
			vipView.setImageResource(R.drawable.icon_center_vip_2);
		} else if (userGrade.equals("3")) {
			vipView.setImageResource(R.drawable.icon_center_vip_3);
		} else if (userGrade.equals("4")) {
			vipView.setImageResource(R.drawable.icon_center_vip_4);
		} else if (userGrade.equals("5")) {
			vipView.setImageResource(R.drawable.icon_center_vip_5);
		} else if (userGrade.equals("100")) {
			vipView.setImageResource(R.drawable.icon_store_vip);
		} else {
			vipView.setVisibility(View.GONE);
		}
	}

	/**
	 * 把时间转换成时间戳
	 * 
	 * @param date_str
	 * @return
	 */
	public static String dateToTimeStamp(String date_str) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		long timeStamp = 0;
		try {
			Date date = dateFormat.parse(date_str);
			timeStamp = date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return String.valueOf(timeStamp).substring(0, 10);
	}

	/**
	 * 拍照照片所存放的路径
	 */
	public static String getPhotoPath() {

		final File path = new File(PHOTO_HOST);
		if (!path.exists()) {
			path.mkdirs();
		}

		return PHOTO_HOST;
	}

	/**
	 * @Title: exitApp
	 * @Description: 退出程序之前需要清除一些数据
	 * @param @param act 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static void exitApp(Context act) {
		SharedPreferenceHelper.clearLoginMsg(act);
		BaseChatFragment.deleteTable();

		ShareSDK.initSDK(act);
		Platform sinaWeibo = ShareSDK.getPlatform(act, SinaWeibo.NAME);
		if (sinaWeibo.isValid()) {
			sinaWeibo.removeAccount(true);
		}
		Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
		if (wechat.isValid()) {
			wechat.removeAccount(true);
		}

		// 发送关闭所有activity广播
		Intent exit_intent = new Intent(BaseActivity.EXIT_ACTION);
		act.sendBroadcast(exit_intent);
		// 然后跳到首页
		Intent intent = new Intent(act, FirstGuideActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		act.startActivity(intent);
	}

	public static String getTempPhotoPath() {

		return getPhotoPath() + File.separator + "temp.jpg";
	}

	/**
	 * 
	 * @Title: showFirstGuideDialog
	 * @Description: 显示引导页的工具管理方法
	 * @param
	 * @return
	 * @throws
	 */
	public static DialogFragment showFirstGuideDialog(FragmentActivity context,
			String type) {
		DialogFragment fragment = null;

		FragmentManager fragmentManager = context.getSupportFragmentManager();

		if (!TextUtils.isEmpty(type)) {
			if (type.equals(ConstantsTooTooEHelper.FIRST_GUIDE_CREATE_WISH)) {
				fragment = new FirstCreateWishGuideDialog();
			} else if (type
					.equals(ConstantsTooTooEHelper.FIRST_GUIDE_CREATE_ACTIVITY)) {
				fragment = new FirstCreateWishGuideDialog();

			} else if (type
					.equals(ConstantsTooTooEHelper.FIRST_GUIDE_CREATE_COMMENT)) {
				fragment = new FirstCreateCommentGuideDialog();

			} else if (type
					.equals(ConstantsTooTooEHelper.FIRST_GUIDE_COMMIT_REGISTER)) {
				fragment = new FirstComitGroupDialog();

			} else if (type.equals(ConstantsTooTooEHelper.FIRST_GUIDE_REGISTER)) {
				fragment = new FirstBigEateGoToGroupDialog();

			} else if (type
					.equals(ConstantsTooTooEHelper.FIRST_GUIDE_WRITE_COMMENT)) {
				fragment = new FirstWriteCommentGuideDialog();

			} else if (type
					.equals(ConstantsTooTooEHelper.FIRST_GUIDE_VIEWPAGER)) {
				fragment = new FirstViewPagerGuideDialog();

			} else if (type
					.equals(ConstantsTooTooEHelper.FIRST_GUIDE_SMALL_REGISTER)) {
				fragment = new FirstsmallEateGoToGroupDialog();
			} else if (type
					.equals(ConstantsTooTooEHelper.FIRST_GUIDE_LOOK_GROUPINFO)) {
				fragment = new FirstLookGroupDialog();
			} else if (type
					.equals(ConstantsTooTooEHelper.FIRST_GUIDE_LOOK_INFO)) {
				fragment = new FirstLookinfoDialog();
			} else if (type
					.equals(ConstantsTooTooEHelper.FIRST_GUIDE_LOOK_ALREADY_GROUP)) {
				fragment = new FirstLooknumberGroupDialog();
			} else if (type.equals(ConstantsTooTooEHelper.FIRST_GUIDE_DROP)) {
				fragment = new FirstGuideDropDialog();
			} else if (type.equals(ConstantsTooTooEHelper.FIRST_GUIDE_ACT_CHAT)) {
				fragment = new FirstActChatDialog();
			} else if (type.equals(ConstantsTooTooEHelper.FIRST_GUIDE_WIS_CHAT)) {
				fragment = new FirstChatWisDialog();
			} else if (type
					.equals(ConstantsTooTooEHelper.FIRST_GUIDE_BAICHI_CHAT)) {
				fragment = new FirstBaichiChatDialog();
			} else if (type
					.equals(ConstantsTooTooEHelper.FIRST_GUIDE_LOOK_GOGROUPCOUNT)) {
				fragment = new FirstGuideLookGroupCountDialog();
			} else if (type
					.equals(ConstantsTooTooEHelper.FIRST_GUIDE_CLICK_GOGROUPCOUNT)) {
				fragment = new FirstGuideClickGroupCountDialog();
			}else if (type
					.equals(ConstantsTooTooEHelper.FIRST_GUIDE_GET_MEMBERS_GROUP)) {
				fragment = new FirstGetGroupMembersDialog();
			}else if(type.equals(ConstantsTooTooEHelper.FIRST_GUIDE_FACE_TO_FACE)){
				//面对面
				fragment=new FirstGuideFaceToFace();
			}else if(type.equals(ConstantsTooTooEHelper.FIRST_GUIDE_FACE_TO_FACE_DETAIL)){
				//活动面对面邀请提示
				fragment=new FirstDetailFaceToFaceGuideDialog();
			}else if(type.equals(ConstantsTooTooEHelper.FIRST_GUIDE_SHARE)){
				fragment=new FirstShareDialog();
			}else if(type.equals(ConstantsTooTooEHelper.FIRST_GUIDE_DIANZAN)){
				fragment=new FirstCommentCountGetBaiChiDialog();
			}else if(type.equals(ConstantsTooTooEHelper.First_GUIDE_GO_BUY_ACT)){
				fragment=new FirstGoToBuyGuideDialog();
			}
		

		}
		if (fragmentManager != null && fragment != null) {
			// 屏幕较小，以全屏形式显示
			FragmentTransaction transaction = fragmentManager
					.beginTransaction();
			// 指定一个过渡动画
			transaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			transaction.addToBackStack(null);
			transaction.attach(fragment);
			fragment.show(fragmentManager, "dialog");
			transaction.commitAllowingStateLoss();
		}
		return fragment;
	}
	/**
	 * 
	* @Title: ToDBC 
	* @Description: 字符全角化
	* @param  
	* @return   
	* @throws
	 */
	public static String ToDBC(String input) {
		   char[] c = input.toCharArray();
		   for (int i = 0; i< c.length; i++) {
		       if (c[i] == 12288) {
		         c[i] = (char) 32;
		         continue;
		       }if (c[i]> 65280&& c[i]< 65375)
		          c[i] = (char) (c[i] - 65248);
		       }
		   return new String(c);
		}

}
