package com.ninetowns.tootoopluse.util;


import java.security.MessageDigest;
import java.util.Iterator;
import java.util.TreeMap;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.application.TootooPlusEApplication;
import com.ninetowns.ui.widget.dialog.ProgressiveDialog;

public class UIUtils {
	private static final float scale = TootooPlusEApplication.getAppContext().getResources().getDisplayMetrics().density;  
	  
	private static final float scaledDensity = TootooPlusEApplication.getAppContext().getResources().getDisplayMetrics().scaledDensity;  
	
	// 通过xml获取view
		public static LayoutInflater getLayoutInflater(Context context) {

			return (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public static View inflate(Context context, int resource, ViewGroup root) {
			return LayoutInflater.from(context).inflate(resource, root);
		}

		/**
		 * 绘制圆角图片
		 * 
		 * @param bitmap
		 * @return
		 */
		public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),

			bitmap.getHeight(), Config.ARGB_8888);

			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;

			final Paint paint = new Paint();

			final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

			final RectF rectF = new RectF(rect);

			final float roundPx = 12;

			paint.setAntiAlias(true);

			canvas.drawARGB(0, 0, 0, 0);

			paint.setColor(color);

			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

			canvas.drawBitmap(bitmap, rect, rect, paint);
			return output;

		}

		/**
		 * 绘制圆形图片
		 * 
		 * @param bitmap
		 * @return
		 */
		public Bitmap toRoundBitmap(Bitmap bitmap) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int ovalLen = Math.min(width, height);
			Rect src = new Rect((width - ovalLen) / 2, (height - ovalLen) / 2,
					(width - ovalLen) / 2 + ovalLen, (height - ovalLen) / 2
							+ ovalLen);
			Rect dst = new Rect(0, 0, ovalLen, ovalLen);
			Bitmap output = Bitmap.createBitmap(ovalLen, ovalLen, Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			canvas.drawOval(new RectF(0, 0, ovalLen, ovalLen), paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, src, dst, paint);
			return output;
		}

		public static void startActivity(Context context,Bundle bundle,Class<?>clazz){
			Intent intent=new Intent(context,clazz);
			if(null!=bundle){
				intent.putExtras(bundle);
			}
			context.startActivity(intent);
		}
		public static void startActivity(Context context,String userId,Class<?>clazz){
			Intent intent=new Intent(context,clazz);
			if(!TextUtils.isEmpty(userId)){
				intent.putExtra("userId", userId);
			}
			context.startActivity(intent);
		}
		
		/**
		 * 检查SDCARD的状态
		 * 
		 * @return true表示SDCARD已准备好
		 */
		public static boolean checkSdCard() {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				return true;
			} else {
				return false;
			}
		}

		/**
		 * MD5加密前准备
		 * 
		 * @param loginId
		 * @param password
		 * @param appKey
		 * @param appSecret
		 * @param deviceCode
		 * @return
		 */
		public static String openIdEncode(String loginId, String password,
				String appKey, String appSecret, String deviceCode) {
			try {

				// MD5加密前先把密码MD5加密然后整体加密
				password = MD5(password);
				TreeMap<String, Object> map = new TreeMap<String, Object>();

				map.put("login_id", loginId);
				map.put("password", password);

				map.put("app_key", appKey);
				map.put("app_secret", appSecret);
				map.put("device_code", deviceCode);

				String s = "";
				for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
					String key = it.next();
					Object value = map.get(key);
					if (value != null) {
						String p = String
								.format("%1$s=%2$s", key, value.toString());
						s += ((s.length() > 0 ? "&" : "") + p);
					}
				}

				return MD5(s);
			} catch (Exception e) {
			}

			return null;
		}

		/**
		 * MD5加密
		 * 
		 * @param s
		 * @return
		 */
		public static String MD5(String s) {

			char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
					'a', 'b', 'c', 'd', 'e', 'f' };
			try {
				byte[] strTemp = s.getBytes();
				MessageDigest mdTemp = MessageDigest.getInstance("MD5");
				mdTemp.update(strTemp);
				byte[] md = mdTemp.digest();
				int j = md.length;
				char str[] = new char[j * 2];
				int k = 0;
				for (int i = 0; i < j; i++) {
					byte byte0 = md[i];
					str[k++] = hexDigits[byte0 >>> 4 & 0xf];
					str[k++] = hexDigits[byte0 & 0xf];
				}

				return new String(str);
			} catch (Exception e) {
				return null;
			}
		}

		/**
		 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
		 */
		public static int dip2px(Context context, float dpValue) {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (dpValue * scale + 0.5f);
		}

		/**
		 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
		 */
		public static int px2dip(Context context, float pxValue) {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (pxValue / scale + 0.5f);
		}

		/**
		 * px转sp
		 * 
		 * @param context
		 * @param px
		 * @return sp
		 */
		public static float px2Sp(Context context, float px) {
			float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
			return px / scaledDensity;
		}

		/**
		 * 
		 * @param context
		 * @param sp
		 *            需要转换的sp
		 * @return 转换后的px
		 */
		public static float sp2Pix(Context context, float sp) {

			float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
			return sp * scaledDensity;

		}

		/**
		 * 判断网络情况
		 * 
		 * @param mContext
		 * @return
		 */
		public static boolean isConnect(Context mContext) {
			boolean isConnected = false;
			ConnectivityManager cm = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo ni = cm.getActiveNetworkInfo();
			isConnected = (ni != null && ni.isConnected());

			return isConnected;
		}

		/**
		 * 获取屏幕高度
		 * 
		 * @param context
		 * @return 屏幕的高度
		 */
		@SuppressWarnings("deprecation")
		public static int getScreenHeight(Activity context) {
			return context.getWindowManager().getDefaultDisplay().getHeight();

		}

		/**
		 * 获取屏幕的宽度
		 * 
		 * @param context
		 * @return 屏幕的宽度
		 */
		@SuppressWarnings("deprecation")
		public static int getScreenWidth(Context context) {
			return ((Activity) context).getWindowManager().getDefaultDisplay()
					.getWidth();
		}

		/**
		 * 获取屏幕宽度
		 * 
		 * @param context
		 * @return
		 */
		@SuppressWarnings("deprecation")
		public static int getScreenWidth(Activity context) {
			return context.getWindowManager().getDefaultDisplay().getWidth();
		}

		public static int getScreenPx(Context context) {
			// TODO Auto-generated method stub
			Display display = ((Activity) context).getWindowManager()
					.getDefaultDisplay();
			DisplayMetrics displayMetrics = new DisplayMetrics();
			display.getMetrics(displayMetrics);
			// float density = displayMetrics.density; //得到密度
			float width = displayMetrics.widthPixels;// 得到宽度
			// float height = displayMetrics.heightPixels;//得到高度
			return (int) width;
		}

		/**
		 * 隐藏软键盘
		 * 
		 * @param context
		 */
		public static void hideSoftInput(Context context) {
			View focusView = ((Activity) context).getCurrentFocus();
			if (focusView == null)
				return;
			((InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
		}
		
		
		
		private static PowerManager.WakeLock getPowerWakeLock(Context context){
			return ((PowerManager)context .getSystemService(Context.POWER_SERVICE))
					.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
		}
		public static void wakeLockAcquire(Context context){
			getPowerWakeLock(context).acquire();
		}
		public static void wakeLockRelease(Context context){
			if(getPowerWakeLock(context).isHeld()){
				getPowerWakeLock(context).release();
			}
		}

		/**
		 * 显示软键盘
		 * 
		 * @param context
		 */
		public static void showSoftInput(Context context) {
			((InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.toggleSoftInput(InputMethodManager.SHOW_FORCED,
							InputMethodManager.HIDE_IMPLICIT_ONLY);

		}

		private static Toast mToast;

		public static void showCenterToast(Context mContext, String text) {
			// if (mToast == null) {
			mToast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
			// }
			mToast.setGravity(Gravity.CENTER, 0, 0);
			mToast.show();
		}

		public static void showShortCenterToast(Context mContext, String text) {
			// if (mToast == null) {
			mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
			// }
			mToast.setGravity(Gravity.CENTER, 0, 0);
			mToast.show();
		}

		public static void showShortCenterToast(Context mContext, int text) {
			// if (mToast == null) {
			mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
			// }
			mToast.setGravity(Gravity.CENTER, 0, 0);
			mToast.show();
		}

		public static void showLeftTopToast(Context context, String text) {
			// if (mToast == null) {
			mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
			// }
			mToast.setGravity(Gravity.LEFT | Gravity.TOP, 0, 100);
			mToast.show();
		}

		/**
		 * 显示一个dialog(确认，取消)
		 * 
		 * @param context
		 * @return
		 */
		public static Dialog showConfirmDialog(Context context, String titleText,
				String text, DialogInterface.OnClickListener positiveListener) {
			Builder builder = new AlertDialog.Builder(context).setMessage(text)
					.setPositiveButton("确定", positiveListener)
					.setNegativeButton("取消", null).setTitle(titleText)
					.setMessage(text).setCancelable(false);
			return builder.show();
		}

		/**
		 * 显示一个dialog(确认，取消)
		 * 
		 * @param context
		 * @return
		 */
		public static Dialog showConfirmDialog(Context context, String titleText,
				String text, String cancle, String comfire,
				DialogInterface.OnClickListener comfirmListener) {
			Builder builder = new AlertDialog.Builder(context).setMessage(text)
					.setPositiveButton(comfire, comfirmListener)
					.setNegativeButton(cancle, null).setTitle(titleText)
					.setMessage(text).setCancelable(false);
			return builder.show();
		}

		/**
		 * 消息提示框
		 * 
		 * @param context
		 * @param title
		 * @param message
		 */
		public static void showDialog(Context context, String title, String message) {
			AlertDialog mDialog = new AlertDialog.Builder(context).setTitle(title)
					.setMessage(message).setNegativeButton("确定", null).create();
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.show();
		}

		public static void showDialog(Context context, String title,
				String message, DialogInterface.OnClickListener comfirmListener) {
			AlertDialog mDialog = new AlertDialog.Builder(context).setTitle(title)
					.setMessage(message).setNegativeButton("确定", comfirmListener)
					.create();
			mDialog.setCanceledOnTouchOutside(false);
			mDialog.show();
		}





		/**
		 * 检测Activity是否在当前Task的栈顶
		 */
		public static boolean isTopNewChatActivity(Context context,
				String activityName) {
			ActivityManager manager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			ComponentName cn = manager.getRunningTasks(1).get(0).topActivity;
			if (activityName.equals(cn.getClassName())) {
				return true;
			}
			return false;
		}

	private static ProgressDialog mProgressDialog;


	private static PopupWindow mSelectPicturePopWindow;

	public static void showSelectPicturePop(Activity context,
			View upLocationView, View.OnClickListener selectFromAlumListener,
			View.OnClickListener selectFromCameraListener) {
		if (null == mSelectPicturePopWindow) {
			mSelectPicturePopWindow = creatPopWindow(context,
					selectFromAlumListener, selectFromCameraListener);
		}
		if(!context.isFinishing()){
			mSelectPicturePopWindow.showAtLocation(upLocationView, Gravity.BOTTOM,
					0, 0);
		}
		
	}
	private static PopupWindow mMorePopWindow;
	
	public static void showMorePop(Activity context,
			View upLocationView, View.OnClickListener onPictureClickListener,
			View.OnClickListener onVideoClickListener,int offsetHeight) {
		if (null == mMorePopWindow) {
			mMorePopWindow = createMorePopView(context,onPictureClickListener,onVideoClickListener);
		}
		
//		int[] location = new int[2];

//		upLocationView.getLocationOnScreen(location);
//		if(!context.isFinishing())
		mMorePopWindow.showAtLocation(upLocationView, Gravity.RIGHT|Gravity.BOTTOM, 0, offsetHeight );
//		mMorePopWindow.showAtLocation(upLocationView, Gravity.TOP,0
//				, 0);
	}
	
	public static void closeMorePop(){
		if(mMorePopWindow!=null&&mMorePopWindow.isShowing()){
			mMorePopWindow.dismiss();
		}
	}

	private static PopupWindow createMorePopView(Context context, OnClickListener onPictureClickListener, OnClickListener onVideoClickListener) {
		PopupWindow pop = new PopupWindow();
		// 获取自定义布局文件activity_popupwindow_left.xml的视图
		
		View popupWindow_view= LayoutInflater.from(context).inflate(
				 R.layout.pop_more, null);
//		View popupWindow_view = View.inflate(context, R.layout.pop_more, null);
		popupWindow_view.findViewById(R.id.popmore_iv_picture)
				.setOnClickListener(onPictureClickListener);
		popupWindow_view.findViewById(R.id.popmore_iv_video)
				.setOnClickListener(onVideoClickListener);
//		pop.setFocusable(true);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setOutsideTouchable(true);
		pop.setAnimationStyle(R.style.win_ani_top_bottom);
		pop.setContentView(popupWindow_view);
		pop.setWidth( LayoutParams.WRAP_CONTENT);
		pop.setHeight( LayoutParams.WRAP_CONTENT);
		// 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
//				pop = new PopupWindow(popupWindow_view, LayoutParams.WRAP_CONTENT,
//						LayoutParams.WRAP_CONTENT);
		return pop;
	}

	public static void dismissSelectPicPop() {
		if (null != mSelectPicturePopWindow
				&& mSelectPicturePopWindow.isShowing()) {
			mSelectPicturePopWindow.dismiss();
		}
	}

	private static PopupWindow creatPopWindow(Context context,
			View.OnClickListener selectFromAlumListener,
			View.OnClickListener selectFromCameraListener) {
		PopupWindow pop = null;
		// 获取自定义布局文件activity_popupwindow_left.xml的视图
		View popupWindow_view = View.inflate(context, R.layout.pop_photo, null);
		popupWindow_view.findViewById(R.id.photopop_selectfrom_album)
				.setOnClickListener(selectFromAlumListener);
		popupWindow_view.findViewById(R.id.photopop_selectfrom_camera)
				.setOnClickListener(selectFromCameraListener);
		popupWindow_view.findViewById(R.id.photopop_cancel).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mSelectPicturePopWindow.dismiss();
					}
				});
		// 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
		pop = new PopupWindow(popupWindow_view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setOutsideTouchable(true);
		pop.setAnimationStyle(R.style.win_ani_top_bottom);
		pop.setContentView(popupWindow_view);

		return pop;
	}

	private static boolean isShow;

	public static void showViewAddAnmimal(View msgTypeContainer, Context context) {
		if (null != msgTypeContainer && !isShow) {
			Animation animation = AnimationUtils.loadAnimation(context,
					R.anim.push_bottom_in);// 使用rotate.xml生成动画效果对象
			animation.setFillAfter(true);//
			animation.setDuration(500);
			msgTypeContainer.startAnimation(animation);
			isShow = true;
			setViewVisible(msgTypeContainer);
			
		}

	}

	

    /**
     * 
     * @Title: ComponentUtil.java
     * @Description: 显示dialog
     * @author wuyulong
     * @date 2014-7-14 下午4:23:26
     * @param
     * @return void
     */
    public static void showProgressDialog(Activity activity) {
        if ((!activity.isFinishing()) && (progressDialog == null)) {
            progressDialog = new ProgressiveDialog(activity);
        }
        if (progressDialog != null) {
            progressDialog.setMessage(R.string.loading);
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
    public static void closeProgressDialogFragment() {
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }
	public static void dismissViewAddAnmimal(View msgTypeContainer,
			Context context) {
		if (null != msgTypeContainer && isShow) {
			Animation animation = AnimationUtils.loadAnimation(context,
					R.anim.push_bottom_out);// 使用rotate.xml生成动画效果对象
			animation.setFillAfter(true);//
			animation.setDuration(500);
			msgTypeContainer.startAnimation(animation);
			isShow = false;
			setViewGone(msgTypeContainer);
		}

	}

	
	private static ProgressiveDialog progressDialog;
	  /** 
	* @Title: showProgressDialog 
	* @Description: 显示 进度对话 框
	* @param @param activity    设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	public static void showProgressDialog(Context activity) {
	        if (progressDialog == null) {
	            progressDialog = new ProgressiveDialog(activity);
	        }
	        if (progressDialog != null) {
	            progressDialog.setMessage(R.string.loading);
	            progressDialog.setCanceledOnTouchOutside(false);
	            progressDialog.show();

	        }

	    }
	  
	  /** 
	* @Title: closeProgressDialogFragment 
	* @Description: 关闭 进度对话框 
	* @param     设定文件 
	* @return void    返回类型 
	* @throws 
	*/
//	public static void closeProgressDialog() {
//	        if (progressDialog != null) {
//	            if (progressDialog.isShowing())
//	                progressDialog.dismiss();
//	        }
//	    }
	public static void setViewVisible(View v) {
		if (null != v && View.VISIBLE != v.getVisibility()) {
			v.setVisibility(View.VISIBLE);
		}
	}


	public static void setViewGone(View v) {
		if (null != v && View.GONE != v.getVisibility()) {
			v.setVisibility(View.GONE);
		}
	}
	/**
	 * 
	* @Title: subStringToColor 
	* @Description: 根据某个特定的符号对文字进行变颜色
	* @param  strindexOf（以特定字符获取位置）      ctView（文字控件 ） content（内容）
	* @return   
	* @throws
	 */
	public static void subStringToColor(TextView ctView,String strindexOf,String content){
		int strPostion = content.indexOf(strindexOf);
		SpannableStringBuilder style = new SpannableStringBuilder(
				content);
		ForegroundColorSpan whatObj = new ForegroundColorSpan(
				TootooPlusEApplication.getAppContext().getResources()
						.getColor(R.color.btn_org_color)); //设置指定位置文字的颜色
		style.setSpan(whatObj, 0, strPostion,
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		ctView.setText(style);
	}
	/** 
	 * dp转成px 
	 * @param dipValue 
	 * @return 
	 */  
	public static int dip2px(float dipValue) {  
	    return (int) (dipValue * scale + 0.5f);  
	}  
	  
	/** 
	 * px转成dp 
	 * @param pxValue 
	 * @return 
	 */  
	public static int px2dip(float pxValue) {  
	    return (int) (pxValue / scale + 0.5f);  
	}  
	  
	/** 
	 * sp转成px 
	 * @param spValue 
	 * @param type 
	 * @return 
	 */  
	public static float sp2px(Activity activity,float spValue) {  
		 return spValue * scaledDensity;   
	}  
}
