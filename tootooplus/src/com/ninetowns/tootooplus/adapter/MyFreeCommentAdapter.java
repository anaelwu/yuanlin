package com.ninetowns.tootooplus.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.library.util.NetworkUtil;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.activity.ActivityDetailActivity;
import com.ninetowns.tootooplus.activity.CreateWishActivity;
import com.ninetowns.tootooplus.activity.HomeActivity;
import com.ninetowns.tootooplus.activity.PersonalHomeActivity;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.bean.RemarkStoryBean;
import com.ninetowns.tootooplus.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootooplus.util.CommonUtil;
import com.ninetowns.ui.widget.WrapRatingBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
/**
 * 
* @ClassName: MyFreeCommentAdapter 
* @Description: 通过未通过
* @author wuyulong
* @date 2015-4-16 下午5:00:28 
*
 */
public class MyFreeCommentAdapter extends BaseAdapter{
	private Context context;
	private List<RemarkStoryBean> parserResult;
	public MyFreeCommentAdapter(Context context,List<RemarkStoryBean>  parserResult) {
		this.context=context;
		this.parserResult=parserResult;
	}

	@Override
	public int getCount() {
		return parserResult.size();
	}

	@Override
	public Object getItem(int position) {
		return parserResult.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RemarkStoryBean commentListBean = parserResult.get(position);
		CommentHolder commentHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context)
					.inflate(R.layout.my_free_comment_list_lv_item, null);
			commentHolder = new CommentHolder();
			ViewUtils.inject(commentHolder, convertView);
			convertView.setTag(commentHolder);
		} else {
			commentHolder = (CommentHolder) convertView.getTag();
		}
		String userid=commentListBean.getRemark_userId();
		if (!TextUtils.isEmpty(userid)) {
			commentHolder.mIVPhoto.setTag(userid);

		}
		commentHolder.mIVPhoto.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String userid = (String) v.getTag();
				String myUserId = SharedPreferenceHelper
						.getLoginUserId(TootooPlusApplication.getAppContext());
				if (!TextUtils.isEmpty(userid) && !userid.equals(myUserId)) {
					Intent intent = new Intent(context,
							PersonalHomeActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("userId", userid);
					context.startActivity(intent);
				} else {
					Bundle bundle=new Bundle();
					Intent intent = new Intent(TootooPlusApplication.getAppContext(),
							HomeActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					bundle.putInt("tab_index", ConstantsTooTooEHelper.TAB_FIVE);
					intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
					context.startActivity(intent);
					
				}
				
			}
		});
		commentHolder.mLLActName.setTag(commentListBean);
		commentHolder.mLLActName.setOnClickListener(new MyOnClickActivityComment(commentListBean));
		setItemData(commentHolder, commentListBean);
		setVisibleDel(commentHolder);
		commentHolder.mIVDelOrone.setOnClickListener(new MyDelOnClickListener(commentHolder, commentListBean) );
		commentHolder.mIVDelOrEdi.setOnClickListener(new MyEditOnClickListener(commentHolder, commentListBean));
		return convertView;
	}
	class MyOnClickActivityComment implements View.OnClickListener {
		private RemarkStoryBean  commentListBean;

		public MyOnClickActivityComment(RemarkStoryBean commentListBean) {
			this.commentListBean = commentListBean;
		}

		@Override
		public void onClick(View v) {

			// 跳转到列表
			//

			String activityId = commentListBean.getRemark_actId();
			String userId = commentListBean.getRemark_userId();
			Bundle bundle = new Bundle();
			Intent intent = new Intent(context, ActivityDetailActivity.class);
			bundle.putString("activity", ConstantsTooTooEHelper.OPEN_COMMENT_LIST);
			bundle.putString(TootooeNetApiUrlHelper.STORYID,
					commentListBean.getWishStoryId());
			bundle.putString(ConstantsTooTooEHelper.USERID, userId);
			bundle.putString(ConstantsTooTooEHelper.ACTIVITYID, activityId);
			bundle.putInt("currentPosition", 0);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
			context.startActivity(intent);

		}

	}
	/**
	 * 
	* @Title: setVisibleDel 
	* @Description: 默认不显示
	* @param  
	* @return   
	* @throws
	 */
	public void setVisibleDel(CommentHolder commentHolder){
		commentHolder.mIVDelOrone.setVisibility(View.GONE);
	}

	/**
	 * 
	 * @Title: setItemData
	 * @Description: 设置条目的数据
	 * @param
	 * @return
	 * @throws
	 */
	public void setItemData(CommentHolder commentHolder,
			RemarkStoryBean commentListBean) {
		setConvertParam(commentHolder);
		String strCountLike = commentListBean.getRemark_countLike();
		String strCountRecommend = commentListBean.getRemark_countRecommend();
		String strCommentConvertThumb = commentListBean.getRemark_coverThumb();
		String strCommentLogoUrl = commentListBean.getRemark_logoUrl();
		String strCommentStoryId = commentListBean.getRemark_storyId();
		String strStoryName = commentListBean.getRemark_storyName();
		String strStoryType = commentListBean.getRemark_storyType();
		String strUserGrade = commentListBean.getRemark_userGrade();
		String strStoryVideoUrl = commentListBean.getRemark_storyVideoUrl();
		String strUserId = commentListBean.getRemark_userId();
		String strUserName = commentListBean.getRemark_userName();
		String strIsRecommend=commentListBean.getIsRecommend();
		String strActName=commentListBean.getRemark_actName();
		if(!TextUtils.isEmpty(strActName)){
			commentHolder.mCtActName.setText("# "+strActName+" #");
		}
		if (!TextUtils.isEmpty(strCountLike)) {
			commentHolder.mCTLike.setText(strCountLike);//设置点赞
		}
		if (!TextUtils.isEmpty(strCountRecommend)) {
			commentHolder.mRatBar.setRating(Float.valueOf(strCountRecommend));//设置评论指数

		}
		if (!TextUtils.isEmpty(strCommentConvertThumb)) {//判断并设置封面图
			ImageLoader.getInstance().displayImage(strCommentConvertThumb,
					new ImageViewAware(commentHolder.mIVConvert),
					CommonUtil.OPTIONS_ALBUM);

		}
		if (!TextUtils.isEmpty(strCommentLogoUrl)) {//设置头像
			ImageLoader.getInstance().displayImage(strCommentLogoUrl,
					new ImageViewAware(commentHolder.mIVPhoto),
					CommonUtil.OPTIONS_HEADPHOTO);
		}else{
			commentHolder.mIVPhoto.setImageResource(R.drawable.icon_default_photo);
		}
		if (!TextUtils.isEmpty(strStoryName)) {
			commentHolder.mTVStoryName.setText(strStoryName);

		}
		if (!TextUtils.isEmpty(strStoryType)&&strStoryType.equals("3")) {//如果是视频
			commentHolder.mIVVideoIcon.setVisibility(View.VISIBLE);
		}else{
			commentHolder.mIVVideoIcon.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(strUserGrade)) {//设置用户等级
			setUserGrade(commentHolder, strUserGrade);

		}
		if (!TextUtils.isEmpty(strUserName)) {
			commentHolder.mCTUserName.setText(strUserName);

		}
		if (!TextUtils.isEmpty(strIsRecommend)&&strIsRecommend.equals("1")) {//是否是推荐  1是推荐
			commentHolder.mIVRecommendIcon.setVisibility(View.VISIBLE);
		}else{
			commentHolder.mIVRecommendIcon.setVisibility(View.GONE);
		}

	}
	/**
	 * 
	* @Title: setUserGrade 
	* @Description: 设置用户等级
	* @param  
	* @return   
	* @throws
	 */
	private void setUserGrade(CommentHolder wishHolder, String userGrade) {
		if (!TextUtils.isEmpty(userGrade)) {

			if (userGrade.equals("1")) {
				Drawable drawable = TootooPlusApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_1);
				wishHolder.mCTUserName.setCompoundDrawablesWithIntrinsicBounds(
						null, null, drawable, null);
			} else if (userGrade.equals("2")) {
				Drawable drawable = TootooPlusApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_2);
				wishHolder.mCTUserName.setCompoundDrawablesWithIntrinsicBounds(
						null, null, drawable, null);
			} else if (userGrade.equals("3")) {
				Drawable drawable = TootooPlusApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_3);
				wishHolder.mCTUserName.setCompoundDrawablesWithIntrinsicBounds(
						null, null, drawable, null);
			} else if (userGrade.equals("4")) {
				Drawable drawable = TootooPlusApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_4);
				wishHolder.mCTUserName.setCompoundDrawablesWithIntrinsicBounds(
						null, null, drawable, null);
			} else if (userGrade.equals("5")) {
				Drawable drawable = TootooPlusApplication.getAppContext()
						.getResources().getDrawable(R.drawable.icon_vip_5);
				wishHolder.mCTUserName.setCompoundDrawablesWithIntrinsicBounds(
						null, null, drawable, null);
			} else {
				wishHolder.mCTUserName.setVisibility(View.INVISIBLE);
			}

		}
	}
	
/**
 * 
* @Title: setConvertParam 
* @Description: 设置封面图的宽高比是3：2
* @param  CommentHolder
* @return   
* @throws
 */
	private void setConvertParam(CommentHolder commentHolder) {
		int convertWidth = CommonUtil.getWidth(TootooPlusApplication
				.getAppContext());
		int height = convertWidth * 2 / 3;
		LayoutParams convertPa = commentHolder.mIVConvert.getLayoutParams();
		convertPa.height = height;
		convertPa.width = convertWidth;
		commentHolder.mIVConvert.setLayoutParams(convertPa);
	}

	/**
	 * 
	 * @ClassName: CommentHolder
	 * @Description: 体验点评列表viewholder
	 * @author wuyulong
	 * @date 2015-3-24 下午1:47:52
	 * 
	 */
	public static class CommentHolder {
		@ViewInject(R.id.ct_user_name)
		public CheckedTextView mCTUserName;// 用户 等级
		@ViewInject(R.id.ct_like)
		public CheckedTextView mCTLike;// 点赞
		@ViewInject(R.id.iv_comment_convert)
		public ImageView mIVConvert;// 封面图
		@ViewInject(R.id.iv_comment_photo)
		public ImageView mIVPhoto;// 头像
		@ViewInject(R.id.tv_story_name)
		public TextView mTVStoryName;// 心愿名字
		@ViewInject(R.id.rb_comment)
		public WrapRatingBar mRatBar;// 好评指数
		@ViewInject(R.id.iv_video_icon)
		public ImageView mIVVideoIcon;// 视频小图标
		@ViewInject(R.id.iv_wish_recommend_icon)
		public ImageView mIVRecommendIcon;// 活动状态图标
		
		@ViewInject(R.id.ct_activityname)
		public CheckedTextView mCtActName;//活动名称
		
		@ViewInject(R.id.ll_act_name)
		public LinearLayout mLLActName;//点击活动标题
		
		@ViewInject(R.id.iv_del_or_edi_two)
		public ImageView mIVDelOrEdi;
		
		
		@ViewInject(R.id.iv_del_or_edi_one)
		public ImageView mIVDelOrone;
		
		
		

	}
	class MyEditOnClickListener implements OnClickListener {
		private CommentHolder wishHolder;
		private RemarkStoryBean selectedWishBean;

		public MyEditOnClickListener(CommentHolder wishHolder,
				RemarkStoryBean selectedWishBean) {
			this.wishHolder = wishHolder;
			this.selectedWishBean = selectedWishBean;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(TootooPlusApplication.getAppContext(), CreateWishActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("storyid", selectedWishBean.getRemark_storyId());
			bundle.putString(TootooeNetApiUrlHelper.TYPE, "1");
			ConstantsTooTooEHelper.putView(
					ConstantsTooTooEHelper.isEditextView, bundle);
			intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
			context.startActivity(intent);

		}

	}	/**
	 * 
	 * @ClassName: MyDelOnClickListener
	 * @Description: 删除 只针对草稿
	 * @author wuyulong
	 * @date 2015-2-27 下午2:31:55
	 * 
	 */
	class MyDelOnClickListener implements OnClickListener {
		private CommentHolder wishHolder;
		private RemarkStoryBean selectedWishBean;

		public MyDelOnClickListener(CommentHolder wishHolder,
				RemarkStoryBean selectedWishBean) {
			this.wishHolder = wishHolder;
			this.selectedWishBean = selectedWishBean;
		}

		@Override
		public void onClick(View v) {
			if ((NetworkUtil.isNetworkAvaliable(context))) {
				// 显示进度
				RequestParamsNet requestParamsNet = new RequestParamsNet();
				requestParamsNet.addQueryStringParameter(
						TootooeNetApiUrlHelper.STORYID,
						selectedWishBean.getRemark_storyId());
				requestParamsNet.addQueryStringParameter(
						TootooeNetApiUrlHelper.USER_ID,
						selectedWishBean.getRemark_userId());
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
											parserResult.remove(selectedWishBean);
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
										context,
										context.getString(R.string.errcode_network_response_timeout));
							}
						});

			} else {
				ComponentUtil.showToast(context, context
						.getString(R.string.errcode_network_response_timeout));
			}

		}

	}
}