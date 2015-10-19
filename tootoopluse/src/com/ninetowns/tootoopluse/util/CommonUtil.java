package com.ninetowns.tootoopluse.util;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;

import android.content.ClipData;
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

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.BaseCommonUtil;
import com.ninetowns.library.util.NetworkUtil;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.activity.FirstGuideActivity;
import com.ninetowns.tootoopluse.application.TootooPlusEApplication;
import com.ninetowns.tootoopluse.fragment.BaseChatFragment;
import com.ninetowns.tootoopluse.fragment.FirstCreateWishGuideDialog;
import com.ninetowns.tootoopluse.fragment.FirstGuideActivityDialog;
import com.ninetowns.tootoopluse.fragment.FirstGuideDropDialog;
import com.ninetowns.tootoopluse.fragment.FirstGuideLookInternetDialog;
import com.ninetowns.tootoopluse.fragment.FirstQianDaoGuideDialog;
import com.ninetowns.tootoopluse.fragment.FirstViewPagerGuideDialog;
import com.ninetowns.tootoopluse.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootoopluse.helper.SharedPreferenceHelper;
import com.ninetowns.tootoopluse.helper.TootooeNetApiUrlHelper;
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

		return SharedPreferenceHelper.getReqHttpUrl(TootooPlusEApplication
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
		httpUtils.setDes(TootooPlusEApplication.ISDES);//设置des3加密
		sysAipUrl(url, requestParamsNet);

		if (NetworkUtil.isNetworkAvaliable(TootooPlusEApplication
				.getAppContext())) {
			httpUtils.send(HttpMethod.GET, url, requestParamsNet,
					requestCallBack);
		} else {
			UIUtils.showCenterToast(TootooPlusEApplication.getAppContext(),
					"无网络连接");
		}

	}
	/**
	 * 
	* @Title: uploadPostXutil 
	* @Description: 上传图片不需要加密
	* @param  
	* @return   
	* @throws
	 */
	public static void uploadPostXutil(String relativeUrl,
			RequestParamsNet requestParamsNet,
			RequestCallBack<String> requestCallBack) {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.setDes(false);//设置des3加密
		String url = appInterfaceUrl(relativeUrl);
		sysAipUrl(url, requestParamsNet);
		httpUtils.send(HttpMethod.POST, url, requestParamsNet, requestCallBack);
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
		httpUtils.setDes(TootooPlusEApplication.ISDES);//设置des3加密
		String url = appInterfaceUrl(relativeUrl);
		sysAipUrl(url, requestParamsNet);
		httpUtils.send(HttpMethod.POST, url, requestParamsNet, requestCallBack);
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
	 * @author huangchao 启动页面获取rtmp和http相关数据的方法(注意：只在启动页面用到)
	 * @param relativeUrl
	 * @param requestParamsNet
	 * @param requestCallBack
	 */
	public static void startGetSend(String relativeUrl,
			RequestParamsNet requestParamsNet,
			RequestCallBack<String> requestCallBack) {
		HttpUtils httpUtils = new HttpUtils();
		
		httpUtils.setDes(false);//不设置des3加密
		String url = startGetHttpAndRtmp(relativeUrl);
		sysAipUrl(url, requestParamsNet);
		httpUtils.send(HttpMethod.GET, url, requestParamsNet, requestCallBack);
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
				Drawable drawable = TootooPlusEApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_1);
				justOrigatiion(checkedTextView, oration, drawable);

			} else if (userGrade.equals("2")) {
				Drawable drawable = TootooPlusEApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_2);
				justOrigatiion(checkedTextView, oration, drawable);
			} else if (userGrade.equals("3")) {
				Drawable drawable = TootooPlusEApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_3);
				justOrigatiion(checkedTextView, oration, drawable);
			} else if (userGrade.equals("4")) {
				Drawable drawable = TootooPlusEApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_4);
				justOrigatiion(checkedTextView, oration, drawable);
			} else if (userGrade.equals("5")) {
				Drawable drawable = TootooPlusEApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_5);
				justOrigatiion(checkedTextView, oration, drawable);
			} else if (userGrade.equals("6")) {
				Drawable drawable = TootooPlusEApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_6);
				justOrigatiion(checkedTextView, oration, drawable);
			} else if (userGrade.equals("7")) {
				Drawable drawable = TootooPlusEApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_7);
				justOrigatiion(checkedTextView, oration, drawable);
			} else if (userGrade.equals("8")) {
				Drawable drawable = TootooPlusEApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_8);
				justOrigatiion(checkedTextView, oration, drawable);
			} else if (userGrade.equals("9")) {
				Drawable drawable = TootooPlusEApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_8);
				justOrigatiion(checkedTextView, oration, drawable);
			} else if (userGrade.equals("10")) {
				Drawable drawable = TootooPlusEApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_10);
				justOrigatiion(checkedTextView, oration, drawable);
			} else if (userGrade.equals(ConstantsTooTooEHelper.SHANGJIA)) {
				Drawable drawable = TootooPlusEApplication.getAppContext()
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
		if(!TextUtils.isEmpty(userGrade)){
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
			} else if (userGrade.equals("6")) {
				vipView.setImageResource(R.drawable.icon_vip_6);
			} else if (userGrade.equals("7")) {
				vipView.setImageResource(R.drawable.icon_vip_7);
			} else if (userGrade.equals("8")) {
				vipView.setImageResource(R.drawable.icon_vip_8);
			} else if (userGrade.equals("9")) {
				vipView.setImageResource(R.drawable.icon_vip_9);
			} else if (userGrade.equals("10")) {
				vipView.setImageResource(R.drawable.icon_vip_10);
			} else if (userGrade.equals("100")) {
				vipView.setImageResource(R.drawable.icon_store_vip);
			} else {
				vipView.setVisibility(View.GONE);
			}
		}else{
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
	* @param @param act    设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	public static void exitApp(Context  act) {
		SharedPreferenceHelper.clearLoginMsg(act);
		BaseChatFragment.deleteTable();
		// 发送关闭所有activity广播
		Intent exit_intent = new Intent(BaseActivity.EXIT_ACTION);
		act.sendBroadcast(exit_intent);
		// 然后跳到首页
		Intent intent = new Intent(act,
				FirstGuideActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		FLAG_ACTIVITY_NEW_TASK
		act.startActivity(intent);
	}
	public static String getTempPhotoPath() {

		return getPhotoPath() + File.separator + "temp.jpg";
	}
	/**
	 * 
	* @Title: removeDuplicateWithOrder 
	* @Description: 去掉重复值
	* @param  
	* @return   
	* @throws
	 */
	  public static List removeDuplicateWithOrder(List list) {

	        Set set = new HashSet();

	        List newList = new ArrayList();

	        for (Iterator iter = list.iterator(); iter.hasNext();) {

	            Object element = iter.next();

	            if (set.add(element)){

	                newList.add(element);

	            }

	        }
	        return newList;

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
					fragment = new FirstGuideActivityDialog();

				} else if (type
						.equals(ConstantsTooTooEHelper.FIRST_GUIDE_DROP)) {
					fragment = new FirstGuideDropDialog();

				} else if (type
						.equals(ConstantsTooTooEHelper.FIRST_GUIDE_LOOK_LINK)) {
					fragment = new FirstGuideLookInternetDialog();

				} else if (type.equals(ConstantsTooTooEHelper.FIRST_GUIDE_VIEWPAGER)) {
					fragment = new FirstViewPagerGuideDialog();

				}else if (type.equals(ConstantsTooTooEHelper.FIRST_GUIDE_QD)) {
					fragment = new FirstQianDaoGuideDialog();

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
		* @Title: copyContent 
		* @Description: 复制文本
		* @param  
		* @return   
		* @throws 
		*/
		@SuppressWarnings({ "deprecation", "unused" })
		public static final void copyContent(final String content,Context context) {
			if (android.os.Build.VERSION.SDK_INT > 11) {
				android.content.ClipboardManager c = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
				  c.setPrimaryClip(ClipData.newPlainText("text", content));

				} else {
				  android.text.ClipboardManager c = (android.text.ClipboardManager)  context.getSystemService(Context.CLIPBOARD_SERVICE);
				c.setText(content);
}
		}
}
