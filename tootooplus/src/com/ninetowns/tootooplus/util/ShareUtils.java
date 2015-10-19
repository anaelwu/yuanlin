package com.ninetowns.tootooplus.util;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootooplus.onekeyshare.OnekeyShare;

public class ShareUtils {

	/**
	 * 显示分享布局，进行微信好友，微信朋友圈分享
	 * 
	 */
	public static void showShare(Context context, String act_id,
			String group_id, String act_name) {

		ShareSDK.initSDK(context);
		OnekeyShare oks_tv = new OnekeyShare();
		// oks_tv.setNotification(R.drawable.logo,context.getString(R.string.app_name));
		// 设置imageUrl图片的url路径
		oks_tv.setImageUrl(ConstantsTooTooEHelper.WECHAT_SHARE_BEFORE_URL
				+ ConstantsTooTooEHelper.WECHAT_SHARE_AFTER_URL + act_id + "_"
				+ group_id);

		oks_tv.setUrl(ConstantsTooTooEHelper.WECHAT_SHARE_AFTER_URL + act_id
				+ "_" + group_id);

		// text是分享文本，所有平台都需要这个字段
		oks_tv.setText(context.getResources().getString(
						R.string.share_app_before_content)
				+ act_name
				+ context.getResources().getString(
						R.string.share_app_after_content));

		// 微信朋友圈和微信好友显示这个字段
		oks_tv.setTitle(context.getResources().getString(
				R.string.story_share_weixin_title));
		oks_tv.setSite(context.getResources().getString(R.string.app_name));
		oks_tv.setTitleUrl(ConstantsTooTooEHelper.WECHAT_SHARE_AFTER_URL
				+ act_id + "_" + group_id);
		oks_tv.show(context);
	}
	/**
	 * 显示分享布局，进行微信好友，微信朋友圈分享,面对面
	 * 
	 */
	public static void showShareActivityDetail(Context context, String act_id,
			String group_id, String act_name,OnClickListener listener,PlatformActionListener platFormActionListener) {

		ShareSDK.initSDK(context);
		OnekeyShare oks_tv = new OnekeyShare();
		oks_tv.setCallback(platFormActionListener);
		Bitmap logo  = BitmapFactory. decodeResource (context.getResources(), R. drawable.icon_share_facetoface ) ; 
		// 定义图标的标签 
		String label  = context.getResources().getString (R.string.str_face_to_face_detail ) ; 
		// 图标点击后会通过Toast提示消息 
	
		oks_tv.setCustomerLogo(logo,null,label,listener) ; 
		// oks_tv.setNotification(R.drawable.logo,context.getString(R.string.app_name));
		// 设置imageUrl图片的url路径
		oks_tv.setImageUrl(ConstantsTooTooEHelper.WECHAT_SHARE_BEFORE_URL
				+ ConstantsTooTooEHelper.WECHAT_SHARE_AFTER_URL + act_id + "_"
				+ group_id);

		oks_tv.setUrl(ConstantsTooTooEHelper.WECHAT_SHARE_AFTER_URL + act_id
				+ "_" + group_id);

		// text是分享文本，所有平台都需要这个字段
		oks_tv.setText(context.getResources().getString(
						R.string.share_app_before_content)
				+ act_name
				+ context.getResources().getString(
						R.string.share_app_after_content));

		// 微信朋友圈和微信好友显示这个字段
		oks_tv.setTitle(context.getResources().getString(
				R.string.story_share_weixin_title));
		oks_tv.setSite(context.getResources().getString(R.string.app_name));
		oks_tv.setTitleUrl(ConstantsTooTooEHelper.WECHAT_SHARE_AFTER_URL
				+ act_id + "_" + group_id);
		oks_tv.show(context);
	}

	public static void showShareText(Context context, String content) {

		ShareSDK.initSDK(context);
		OnekeyShare oks_tv = new OnekeyShare();
		oks_tv.setImageUrl(ConstantsTooTooEHelper.WECHAT_INVITE_SHARE_URL + SharedPreferenceHelper.getReqHttpUrl(context) + "/");
		// text是分享文本，所有平台都需要这个字段
		oks_tv.setText(context.getResources().getString(R.string.invite_code_share_content)
				+ content + "\n" + SharedPreferenceHelper.getReqHttpUrl(context) + "/d");
		
		oks_tv.setUrl(SharedPreferenceHelper.getReqHttpUrl(context) + "/d");

		// 微信朋友圈和微信好友显示这个字段
		oks_tv.setTitle(context.getResources().getString(R.string.invite_code_share_title));
//		oks_tv.setSite(context.getResources().getString(R.string.app_name));

		oks_tv.show(context);

	}
	
	/**
	 * 分享优先码
	 * @param context
	 * @param content
	 */
	public static void shareYXM(Context context, String content) {

		ShareSDK.initSDK(context);
		OnekeyShare oks_tv = new OnekeyShare();
		oks_tv.setImageUrl(ConstantsTooTooEHelper.WECHAT_INVITE_SHARE_URL + SharedPreferenceHelper.getReqHttpUrl(context) + "/");
		// text是分享文本，所有平台都需要这个字段
		oks_tv.setText(context.getResources().getString(R.string.yxm_code_share_content)
				+ content + "\n" + SharedPreferenceHelper.getReqHttpUrl(context) + "/d");
		
		oks_tv.setUrl(SharedPreferenceHelper.getReqHttpUrl(context) + "/d");

		// 微信朋友圈和微信好友显示这个字段
		oks_tv.setTitle(context.getResources().getString(R.string.yxm_code_share_title));
//		oks_tv.setSite(context.getResources().getString(R.string.app_name));

		oks_tv.show(context);

	}

	/**
	 * 
	 * @Title: showShareText
	 * @Description: 分享活动
	 * @param
	 * @return
	 * @throws
	 */
	public static void showShareActivity(Context context, String content,
			String activityId, String imageUrl) {
		String url = CommonUtil
				.appInterfaceUrl(TootooeNetApiUrlHelper.ACTIVITY_SHARE);
		ShareSDK.initSDK(context);
		OnekeyShare oks_tv = new OnekeyShare();

		oks_tv.setImageUrl(imageUrl);
		oks_tv.setUrl(url + activityId);

		// text是分享文本，所有平台都需要这个字段
		oks_tv.setText(content
				+ context.getResources().getString(
						R.string.share_app_before_content_act_good));
		// 微信朋友圈和微信好友显示这个字段
		oks_tv.setTitle(context.getResources().getString(
				R.string.story_share_weixin_wish_title)
				+ content);
		oks_tv.setSite(context.getResources().getString(R.string.app_name));
		oks_tv.setTitleUrl(url + activityId);
		oks_tv.show(context);
		
		TootooPlusApplication.shareStoryId = activityId;
		TootooPlusApplication.shareSource = "3";
	}

	/**
	 * 
	 * @Title: showShareActivity
	 * @Description: 分享故事
	 * @param
	 * @return
	 * @throws
	 */
	public static void showShareWish(Context context, String content,
			String imageUrl, String storyId) {
		String url = CommonUtil
				.appInterfaceUrl(TootooeNetApiUrlHelper.WISH_COMMENT_SHARE);
		ShareSDK.initSDK(context);
		OnekeyShare oks_tv = new OnekeyShare();
		// text是分享文本，所有平台都需要这个字段
		oks_tv.setText(content
				+ context.getResources().getString(
						R.string.share_app_before_content_act_good));

		oks_tv.setUrl(url + storyId);
		oks_tv.setImageUrl(imageUrl);
		// 微信朋友圈和微信好友显示这个字段
		oks_tv.setTitle(context.getResources().getString(
				R.string.story_share_weixin_wish_title)
				+ content);
		oks_tv.setSite(context.getResources().getString(R.string.app_name));
		oks_tv.setTitleUrl(url + storyId);
		oks_tv.show(context);
		
		TootooPlusApplication.shareStoryId = storyId;
		TootooPlusApplication.shareSource = "1";

	}

}