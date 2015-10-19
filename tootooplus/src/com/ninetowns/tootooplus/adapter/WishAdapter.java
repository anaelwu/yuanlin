package com.ninetowns.tootooplus.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.library.util.NetworkUtil;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.activity.CreateWishActivity;
import com.ninetowns.tootooplus.activity.HomeActivity;
import com.ninetowns.tootooplus.activity.MyWishActivity;
import com.ninetowns.tootooplus.activity.PersonalHomeActivity;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.bean.WishBean;
import com.ninetowns.tootooplus.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootooplus.util.CommonUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * 
 * @ClassName: WishAdapter
 * @Description: 心愿列表的数据适配器
 * @author wuyulong
 * @date 2015-1-22 下午4:31:16
 * 
 */
public class WishAdapter extends BaseAdapter {
	private List<WishBean> wishListBean;
	private Activity activity;

	public WishAdapter(Activity activity, List<WishBean> wishListBean) {
		this.wishListBean = wishListBean;
		this.activity = activity;
	}

	public void updateNotify(List<WishBean> wishListBean) {
		if (this.wishListBean != null && !this.wishListBean.isEmpty()) {
			this.wishListBean.addAll(wishListBean);
		} else {
			this.wishListBean = wishListBean;
		}
		notifyDataSetChanged();

	}

	public List<WishBean> getWishList(){
		return wishListBean;
	}
	public void clear() {
		if (wishListBean != null && !wishListBean.isEmpty()) {
			this.wishListBean.clear();
		}
	}

	@Override
	public int getCount() {
		return wishListBean.size();
	}

	@Override
	public Object getItem(int position) {
		return wishListBean.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		WishBean selectedWishBean = wishListBean.get(position);
		WishHolder wishHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(
					TootooPlusApplication.getAppContext()).inflate(
					R.layout.item_wish_adapter, null);
			wishHolder = new WishHolder();
			ViewUtils.inject(wishHolder, convertView);
			convertView.setTag(wishHolder);
		} else {
			wishHolder = (WishHolder) convertView.getTag();
		}
		setUserInfoData(wishHolder, selectedWishBean);
		setWishInfoType(wishHolder, selectedWishBean);
		setWishInfoData(wishHolder, selectedWishBean);
		setIsEditext(wishHolder, selectedWishBean);
		String userid = selectedWishBean.getUserId();
		int convertWidth = CommonUtil.getWidth(TootooPlusApplication
				.getAppContext());
		int height = convertWidth * 2 / 3;
		LayoutParams convertPa = wishHolder.mIVWishConvert.getLayoutParams();
		convertPa.height = height;
		convertPa.width = convertWidth;
		// RelativeLayout.LayoutParams rlParams=new
		// RelativeLayout.LayoutParams(convertWidth,height);
		wishHolder.mIVWishConvert.setLayoutParams(convertPa);
		if (!TextUtils.isEmpty(userid)) {
			wishHolder.mIVWishPhoto.setTag(userid);

		}
		setOnDelEdiOne(wishHolder, selectedWishBean);
		setOnDelEdiTwo(wishHolder, selectedWishBean);
		justIsDraftOrMyWishView(wishHolder, selectedWishBean);
		wishHolder.mIVWishPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!(activity instanceof PersonalHomeActivity)) {

					String userid = (String) v.getTag();
					String myUserId = SharedPreferenceHelper
							.getLoginUserId(TootooPlusApplication
									.getAppContext());
					if (!TextUtils.isEmpty(userid) && !userid.equals(myUserId)) {
						Intent intent = new Intent(activity,
								PersonalHomeActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("userId", userid);
						activity.startActivity(intent);
					} else {
						Bundle bundle = new Bundle();
						Intent intent = new Intent(TootooPlusApplication
								.getAppContext(), HomeActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						bundle.putInt("tab_index",
								ConstantsTooTooEHelper.TAB_FIVE);
						intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
						activity.startActivity(intent);

					}
				}

			}
		});
		return convertView;
	}

	/**
	 * 
	 * @Title: justIsDraftOrMyWishView
	 * @Description: 判断是否显示草稿
	 * @param
	 * @return
	 * @throws
	 */
	public void justIsDraftOrMyWishView(WishHolder wishHolder,
			WishBean selectedWishBean) {
		// LayoutParams params =
		// wishHolder.mRLWishDraftBottom.getLayoutParams();
		// params.width = CommonUtil.getWidth(activity);
		// params.height = activity.getResources().getDimensionPixelSize(
		// R.dimen.wish_bottom_height);
		// //
		// params.height=activity.getResources().getDimensionPixelSize(R.dimen.draft_bottom_height);
		// wishHolder.mRLWishDraftBottom.setLayoutParams(params);
		wishHolder.mRlStoryId.setVisibility(View.VISIBLE);
		if (!TextUtils.isEmpty(selectedWishBean.getStoryId())) {
			wishHolder.mCTStoryId
					.setText("ID:" + selectedWishBean.getStoryId());
		}

	}

	/**
	 * 
	 * @Title: setIsEditext
	 * @Description: 设置是否是我的心愿
	 * @param
	 * @return
	 * @throws
	 */
	public void setIsEditext(WishHolder wishHolder, WishBean selectedWishBean) {
		String userid = selectedWishBean.getUserId();
		String myUserid = SharedPreferenceHelper.getLoginUserId(activity);
		if (userid.equals(myUserid)) {
			// 显示编辑按钮
			wishHolder.mIvDelOrEdiOne.setVisibility(View.INVISIBLE);
			wishHolder.mIvDelOrEdiTwo.setImageResource(R.drawable.icon_edit);
			wishHolder.mIvDelOrEdiTwo.setVisibility(View.VISIBLE);
		} else {
			wishHolder.mIvDelOrEdiOne.setVisibility(View.INVISIBLE);
			wishHolder.mIvDelOrEdiTwo.setVisibility(View.INVISIBLE);
		}

	}

	/**
	 * 
	 * @Title: setOnDelEdiOne
	 * @Description: 编辑按钮
	 * @param
	 * @return
	 * @throws
	 */
	public void setOnDelEdiOne(WishHolder wishHolder, WishBean selectedWishBean) {
		wishHolder.mIvDelOrEdiOne.setOnClickListener(new MyEditOnClickListener(
				wishHolder, selectedWishBean));

	}

	/**
	 * 
	 * @Title: setOnDelEdiTwo
	 * @Description: 默认是我的心愿显示编辑按钮
	 * @param
	 * @return
	 * @throws
	 */
	public void setOnDelEdiTwo(WishHolder wishHolder, WishBean selectedWishBean) {
		if (activity instanceof MyWishActivity
				|| activity instanceof HomeActivity) {
			wishHolder.mIvDelOrEdiTwo
					.setOnClickListener(new MyEditOnClickListener(wishHolder,
							selectedWishBean));

		} else {
			wishHolder.mIvDelOrEdiTwo
					.setOnClickListener(new MyDelOnClickListener(wishHolder,
							selectedWishBean));
		}

	}

	/**
	 * 
	 * @Title: setWishInfoData
	 * @Description: 设置心愿信息数据
	 * @param
	 * @return
	 * @throws
	 */
	public void setWishInfoData(WishHolder wishHolder, WishBean selectedWishBean) {
		String countFree = selectedWishBean.getCountFree();
		String free = selectedWishBean.getFree();// 我要白吃 0默认值1点击过
		String convertThumb = selectedWishBean.getCoverThumb();
		if (!TextUtils.isEmpty(countFree)) {
			// 我要白吃的用户数量
			wishHolder.mCTHasEate.setText(countFree);
		}
		if (!TextUtils.isEmpty(free)) {
			if (free.equals("0")) {// 未点击过
				Drawable noClick = TootooPlusApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_want_eat);
				wishHolder.mCTHasEate.setCompoundDrawablesWithIntrinsicBounds(
						noClick, null, null, null);
			} else {
				Drawable clicked = TootooPlusApplication.getAppContext()
						.getResources()
						.getDrawable(R.drawable.icon_eate_clicked);
				wishHolder.mCTHasEate.setCompoundDrawablesWithIntrinsicBounds(
						clicked, null, null, null);
			}
		}

		if (!TextUtils.isEmpty(convertThumb)) {
			// 封面图
			ImageLoader.getInstance().displayImage(convertThumb,
					new ImageViewAware(wishHolder.mIVWishConvert),
					CommonUtil.OPTIONS_ALBUM);
		}

	}

	/**
	 * 
	 * @Title: setWishInfoType
	 * @Description: 显示是图片还是视频
	 * @param
	 * @return
	 * @throws
	 */
	public void setWishInfoType(WishHolder wishHolder, WishBean selectedWishBean) {
		String recommend = selectedWishBean.getIsRecommend();
		String storyType = selectedWishBean.getStoryType();
		String storyName = selectedWishBean.getStoryName();
		if (!TextUtils.isEmpty(recommend)) {
			// 是否推荐
			if (recommend.equals("1")) {// 通过 显示
				wishHolder.mIvIconRecommend.setVisibility(View.VISIBLE);
			} else {// 不显示推荐图标
				wishHolder.mIvIconRecommend.setVisibility(View.GONE);
			}
		} else {
			wishHolder.mIvIconRecommend.setVisibility(View.GONE);
		}

		if (!TextUtils.isEmpty(storyType)) {
			if (storyType.equals("3")) {// 视频 显示视频图标
				wishHolder.mIVIcon.setVisibility(View.VISIBLE);
			} else {// 图片
				wishHolder.mIVIcon.setVisibility(View.GONE);
			}
		}
		if (!TextUtils.isEmpty(storyName)) {
			wishHolder.mCTStoryName.setText(storyName);
		}

	}

	/**
	 * 
	 * @Title: setUerInfoData
	 * @Description: 设置用户信息
	 * @param
	 * @return
	 * @throws
	 */
	public void setUserInfoData(WishHolder wishHolder, WishBean selectedWishBean) {
		String userName = selectedWishBean.getUserName();
		String userLogoUrl = selectedWishBean.getLogoUrl();
		String userGrade = selectedWishBean.getUserGrade();

		if (!TextUtils.isEmpty(userLogoUrl)) {
			ImageLoader.getInstance().displayImage(userLogoUrl,
					wishHolder.mIVWishPhoto, CommonUtil.OPTIONS_HEADPHOTO);
		} else {
			wishHolder.mIVWishPhoto
					.setImageResource(R.drawable.icon_default_photo);
		}
		if (!TextUtils.isEmpty(userName)) {
			wishHolder.mCTUserName.setText(userName);
		}
		CommonUtil.setUserGrade(wishHolder.mCTUserName, userGrade, "right");
	}

	public static class WishHolder {
		@ViewInject(R.id.ct_username)
		public CheckedTextView mCTUserName;// 姓名
		@ViewInject(R.id.ct_has_eate)
		public CheckedTextView mCTHasEate;// 是否吃过
		@ViewInject(R.id.iv_wish_convert)
		public ImageView mIVWishConvert;// 心愿封面图
		@ViewInject(R.id.iv_wish_photo)
		public ImageView mIVWishPhoto;// 心愿头像
		@ViewInject(R.id.iv_wish_recommend_icon)
		public ImageView mIvIconRecommend;// 推荐的图标
		@ViewInject(R.id.iv_video_icon)
		public ImageView mIVIcon;// 视频标记图标
		@ViewInject(R.id.ct_story_name)
		public CheckedTextView mCTStoryName;// 心愿的名字
		@ViewInject(R.id.iv_del_or_edi_two)
		public ImageView mIvDelOrEdiTwo;
		@ViewInject(R.id.iv_del_or_edi_one)
		public ImageView mIvDelOrEdiOne;
		// @ViewInject(R.id.rl_bottom)
		// public RelativeLayout mRLWishDraftBottom;//
		// 显示故事名称和阴影或者显示storyid的父类组件，草稿没有storyid
		// 的显示，而心愿中显示
		@ViewInject(R.id.rl_storyid)
		public RelativeLayout mRlStoryId;
		@ViewInject(R.id.ct_storyid)
		public CheckedTextView mCTStoryId;
	}

	/**
	 * 
	 * @ClassName: MyEditOnClickListener
	 * @Description: 编辑
	 * @author wuyulong
	 * @date 2015-2-27 下午2:29:33
	 * 
	 */
	class MyEditOnClickListener implements OnClickListener {
		private WishHolder wishHolder;
		private WishBean selectedWishBean;

		public MyEditOnClickListener(WishHolder wishHolder,
				WishBean selectedWishBean) {
			this.wishHolder = wishHolder;
			this.selectedWishBean = selectedWishBean;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(activity, CreateWishActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("storyid", selectedWishBean.getStoryId());
			bundle.putString(TootooeNetApiUrlHelper.TYPE, "0");
			ConstantsTooTooEHelper.putView(
					ConstantsTooTooEHelper.isEditextView, bundle);
			intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
			activity.startActivity(intent);

		}

	}

	/**
	 * 
	 * @ClassName: MyDelOnClickListener
	 * @Description: 删除 只针对草稿
	 * @author wuyulong
	 * @date 2015-2-27 下午2:31:55
	 * 
	 */
	class MyDelOnClickListener implements OnClickListener {
		private WishHolder wishHolder;
		private WishBean selectedWishBean;

		public MyDelOnClickListener(WishHolder wishHolder,
				WishBean selectedWishBean) {
			this.wishHolder = wishHolder;
			this.selectedWishBean = selectedWishBean;
		}

		@Override
		public void onClick(View v) {
			if ((NetworkUtil.isNetworkAvaliable(activity))) {
				// 显示进度
				RequestParamsNet requestParamsNet = new RequestParamsNet();
				requestParamsNet.addQueryStringParameter(
						TootooeNetApiUrlHelper.STORYID,
						selectedWishBean.getStoryId());
				requestParamsNet.addQueryStringParameter(
						TootooeNetApiUrlHelper.USER_ID,
						selectedWishBean.getUserId());
				CommonUtil.xUtilsPostSend(TootooeNetApiUrlHelper.DEL_STORY,
						requestParamsNet, new RequestCallBack<String>() {

							private String status;

							@Override
							public void onSuccess(
									ResponseInfo<String> responseInfo) {
								String jsonStr = new String(responseInfo.result);
								try {
									JSONObject jsobj = new JSONObject(jsonStr);
									if (jsobj.has("Status")) {
										status = jsobj.getString("Status");
										if (status.equals("1")) {
											wishListBean
													.remove(selectedWishBean);
											notifyDataSetChanged();

										}

									}

								} catch (JSONException e) {
									e.printStackTrace();
								}

							}

							@Override
							public void onFailure(HttpException error,
									String msg) {
								ComponentUtil.showToast(
										activity,
										activity.getString(R.string.errcode_network_response_timeout));
							}
						});

			} else {
				ComponentUtil.showToast(activity, activity
						.getString(R.string.errcode_network_response_timeout));
			}

		}

	}

}
