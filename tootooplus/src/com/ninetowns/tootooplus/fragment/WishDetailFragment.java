package com.ninetowns.tootooplus.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.library.util.LogUtil;
import com.ninetowns.library.util.NetworkUtil;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.activity.ChatActivity;
import com.ninetowns.tootooplus.activity.GoBuyBrowserActivity;
import com.ninetowns.tootooplus.activity.HomeActivity;
import com.ninetowns.tootooplus.activity.PersonalHomeActivity;
import com.ninetowns.tootooplus.activity.VideoActivity;
import com.ninetowns.tootooplus.adapter.WishDetailAdapter;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.bean.GroupChatBean;
import com.ninetowns.tootooplus.bean.GroupChatList;
import com.ninetowns.tootooplus.bean.WishDetailBean;
import com.ninetowns.tootooplus.bean.WishDetailPageListBean;
import com.ninetowns.tootooplus.fragment.FirstShareDialog.OnStatusDissMiss;
import com.ninetowns.tootooplus.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootooplus.parser.GetGroupChatParser;
import com.ninetowns.tootooplus.parser.WishDetailParser;
import com.ninetowns.tootooplus.util.CommonUtil;
import com.ninetowns.tootooplus.util.ShareUtils;
import com.ninetowns.ui.cooldraganddrop.CoolDragAndDropGridView;
import com.ninetowns.ui.cooldraganddrop.SimpleScrollingStrategy;
import com.ninetowns.ui.fragment.BaseFragment;
import com.ninetowns.ui.widget.WrapRatingBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * 
 * @ClassName: WishDetailFragment
 * @Description:心愿详情列表
 * @author wuyulong
 * @date 2015-2-5 上午11:03:31
 * 
 */
@SuppressLint("ResourceAsColor")
public class WishDetailFragment extends
		BaseFragment<WishDetailBean, WishDetailParser> implements
		View.OnClickListener, CoolDragAndDropGridView.DragAndDropListener,
		OnItemClickListener,OnStatusDissMiss {
	private View mWishDetailFragment;
	private Bundle bundle;
	private String storyId;
	private String userId;
	@ViewInject(R.id.iv_detail_return)
	private ImageView mIVdetailReturn;
	@ViewInject(R.id.iv_photo)
	private ImageView mIVphoto;
	@ViewInject(R.id.ct_uername)
	private CheckedTextView mCtUserName;
	@ViewInject(R.id.ct_date)
	private CheckedTextView mCtDate;
	@ViewInject(R.id.iv_detail_convert)
	private ImageView mIvConvertImage;
	@ViewInject(R.id.iv_video_icon)
	private ImageView mIVVideoIcon;
	@ViewInject(R.id.tv_storyname)
	private TextView mTvStoryName;
	@ViewInject(R.id.coolDragAndDropGridView)
	private CoolDragAndDropGridView mCoolDragAndrDropView;
	@ViewInject(R.id.ct_want_eate)
	private CheckedTextView mCTWantEate;// 要白吃
	@ViewInject(R.id.ct_want_buy)
	private CheckedTextView mCTWantBuy;
	@ViewInject(R.id.scrollView)
	private ScrollView mScrollView;
	@ViewInject(R.id.photo_user)
	private FrameLayout fl_photouser;
	@ViewInject(R.id.rb_detail_comment)
	private WrapRatingBar mRBDetailComment;// 指数
	@ViewInject(R.id.ll_wishdetail)
	private LinearLayout mLLWishDetail;// 详情
	@ViewInject(R.id.ll_commentdetail)
	private LinearLayout mLLCommentWishDetail;// 点评详情
	@ViewInject(R.id.ct_comment_want_buy)
	private CheckedTextView mCTCommentBuy;
	@ViewInject(R.id.ct_good)
	private CheckedTextView mCTGood;// 点赞
	@ViewInject(R.id.ct_chat_wish)
	private CheckedTextView mCTChat;// 跳转到聊天室
	@ViewInject(R.id.to_buy_num)
	private CheckedTextView mCTBuy;// 购买指数
	@ViewInject(R.id.iv_wish_detail_share)
	private ImageView mIVShare;//分享
	@ViewInject(R.id.ll_buy_baground)
	private LinearLayout mLLGoToBuy;
	@ViewInject(R.id.ll_buy_wish_baground)
	private LinearLayout mLLWishBuy;

	private List<WishDetailPageListBean> pageListData = new ArrayList<WishDetailPageListBean>();
	private WishDetailBean parserResult = new WishDetailBean();
	private PhotoUserAllWishFragment photoUserAllWishFragment;
	private String videoUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getBundleType();
	}
	@OnClick(R.id.iv_wish_detail_share)
	public void setShareWish(View v){//分享
		String type = parserResult.getType();
		if (!TextUtils.isEmpty(type)) {
			if (type.equals("0")) {// 心愿
				ShareUtils.showShareWish(getActivity(), parserResult.getStoryName(), parserResult.getCoverThumb(),storyId);
				
			} else {// 点评
				ShareUtils.showShareWish(getActivity(), parserResult.getActivityName(), parserResult.getCoverThumb(),storyId);
			}

		} 
		
		
		
		
		
	}

	/***
	 * 
	 * @Title: setOnClickToChat
	 * @Description: 点击跳转到聊天室
	 * @param
	 * @return
	 * @throws
	 */
	@OnClick(R.id.ct_chat_wish)
	public void setOnClickToChat(View v) {
		skipToChatGroup();
	}

	@OnClick(R.id.iv_photo)
	public void setOnClickPhoto(View v) {
		String viewUserId = parserResult.getUserId();
		String myUserId = SharedPreferenceHelper
				.getLoginUserId(TootooPlusApplication.getAppContext());
		if (!TextUtils.isEmpty(viewUserId) && !viewUserId.equals(myUserId)) {
			Intent intent = new Intent(getActivity(),
					PersonalHomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("userId", viewUserId);
			startActivity(intent);
		} else {
			Bundle bundle = new Bundle();
			Intent intent = new Intent(TootooPlusApplication.getAppContext(),
					HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			bundle.putInt("tab_index", ConstantsTooTooEHelper.TAB_FIVE);
			intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
			startActivity(intent);

		}

	}

	/**
	 * 
	 * @Title: setCollDragGridViewOperate
	 * @Description: 注册
	 * @param
	 * @return
	 * @throws
	 */
	private void setCollDragGridViewOperate() {
		mCoolDragAndrDropView.setScrollingStrategy(new SimpleScrollingStrategy(
				mScrollView));
		mCoolDragAndrDropView.setDragAndDropListener(this);
		mCoolDragAndrDropView.setOnItemClickListener(this);
	}

	private void setViewListener() {
		mIVdetailReturn.setOnClickListener(this);
		mCTWantBuy.setOnClickListener(this);
		mIvConvertImage.setOnClickListener(this);
		mCTWantEate.setOnClickListener(this);
		mCTGood.setOnClickListener(this);
		mCTCommentBuy.setOnClickListener(this);
	}

	private void getBundleType() {
		if (Intent.ACTION_VIEW.equals(getActivity().getIntent().getAction())) {
			//通过网页打开该页面
            Intent intent = getActivity().getIntent();
            // Manifest中配置的scheme
            String scheme = intent.getScheme();
            Uri uri = intent.getData();
            storyId = uri.getQueryParameter("StoryId");
            userId = "0";
        } else {
			bundle = getActivity().getIntent().getBundleExtra(
					ConstantsTooTooEHelper.BUNDLE);
			storyId = bundle.getString(ConstantsTooTooEHelper.STORYID);
			userId = bundle.getString(ConstantsTooTooEHelper.USERID);
        }
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mWishDetailFragment = inflater.inflate(R.layout.wish_detail_fragment,
				null);
		ViewUtils.inject(this, mWishDetailFragment); // 注入view和事件
		setViewListener();
		setImageConvertParams();
		setCollDragGridViewOperate();
		return mWishDetailFragment;
	}

	/**
	 * 
	 * @Title: setImageConvertParams
	 * @Description: 设置封面图的大小
	 * @param
	 * @return
	 * @throws
	 */
	private void setImageConvertParams() {
		int with = CommonUtil.getWidth(TootooPlusApplication.getAppContext());
		int height = (int) (with * 0.67);
		RelativeLayout.LayoutParams convertParam = new RelativeLayout.LayoutParams(
				with, height);
		mIvConvertImage.setLayoutParams(convertParam);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		onLoadData(true, false, false);
	}

	@Override
	public WishDetailParser setParser(String str) {
		WishDetailParser wishDetailParser = new WishDetailParser(str);
		return wishDetailParser;

	}

	@Override
	public RequestParamsNet getApiParmars() {
		RequestParamsNet requestPar = new RequestParamsNet();
		requestPar.setmStrHttpApi(TootooeNetApiUrlHelper.WISH_DETAIL_API);
		String userid = SharedPreferenceHelper.getLoginUserId(getActivity());
		// requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.USER_ID,
		// userId);
		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.USER_ID,
				userid);

		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.STORYID,
				storyId);
		return requestPar;
	}

	@Override
	public void getParserResult(WishDetailBean parserResult) {
		this.parserResult = parserResult;
		setTitleUserInfo(parserResult);
		setConvertInfo(parserResult);
		justIsVideo(parserResult);
		setFotterChange(parserResult);
		pageListData = parserResult.getWishList();
		if (pageListData != null && pageListData.size() > 0) {
			if(isAdded()){
				if(mLLCommentWishDetail!=null&&mLLCommentWishDetail.getVisibility()==View.VISIBLE){
					boolean showShare = SharedPreferenceHelper.getFirstShowShare(getActivity());
					if(mIVShare.getVisibility()==View.VISIBLE){
						if(showShare){
							FirstShareDialog shared=(FirstShareDialog) CommonUtil.showFirstGuideDialog(getActivity(), ConstantsTooTooEHelper.FIRST_GUIDE_SHARE);
							shared.setOnStatusDissMissListener(this);
						}else{
							if(mLLCommentWishDetail!=null&&mLLCommentWishDetail.getVisibility()==View.VISIBLE){
								if(isAdded()){
									boolean isDianPing = SharedPreferenceHelper.getFirstDianzan(getActivity());
								if(isDianPing){
									CommonUtil.showFirstGuideDialog(getActivity(), ConstantsTooTooEHelper.FIRST_GUIDE_DIANZAN);
								}
								}
								
							}
						}
					}
				}
				
			}
			WishDetailAdapter wishDetailAdapter = new WishDetailAdapter(
					getActivity(), pageListData);
			mCoolDragAndrDropView.setAdapter(wishDetailAdapter);
			wishDetailAdapter.notifyDataSetChanged();
		}
	}
	/**
	 * 
	* @Title: setFotterChange 
	* @Description: 设置角色变化
	* @param  
	* @return   
	* @throws
	 */
	@SuppressWarnings("unused")
	public void setFotterChange(WishDetailBean parserResult){
		String countFree = parserResult
				.getCountFree();

		// 判断是否白吃过 如果没有白吃过 那么就白吃一下
		if (parserResult != null) {
			if (parserResult.getFree().equals("0")) {// 默认值 没有点击过
				
				mCTWantEate.setTextColor(TootooPlusApplication.getAppContext().getResources().getColor(R.color.btn_gray_color));
				Drawable noClick = TootooPlusApplication.getAppContext().getResources().getDrawable(R.drawable.icon_ete_2);
				mCTWantEate.setCompoundDrawablesWithIntrinsicBounds(null, noClick, null, null);
			} else if (parserResult.getFree().equals("1")) {//已点击过白吃
				mCTWantEate.setTextColor(TootooPlusApplication.getAppContext().getResources().getColor(R.color.btn_org_color));
				Drawable noClick = TootooPlusApplication.getAppContext().getResources().getDrawable(R.drawable.icon_eate_already);
				mCTWantEate.setCompoundDrawablesWithIntrinsicBounds(null, noClick, null, null);
			}
			
			String shoppingUrl=parserResult.getShoppingUrl();
			if(!TextUtils.isEmpty(shoppingUrl)){
				mLLWishBuy.setBackgroundColor(getResources().getColor(R.color.btn_org_color));
				mLLGoToBuy.setBackgroundColor(getResources().getColor(R.color.btn_org_color));
			}else{
				mLLWishBuy.setBackgroundColor(getResources().getColor(R.color.gray));
				mLLGoToBuy.setBackgroundColor(getResources().getColor(R.color.gray));
			}
			
			
		}else{
			mLLGoToBuy.setBackgroundColor(getResources().getColor(R.color.gray));
			mCTWantEate.setTextColor(TootooPlusApplication.getAppContext().getResources().getColor(R.color.btn_gray_color));
		}
	
		if(!TextUtils.isEmpty(countFree)){
			mCTWantEate.setText("我要白吃("
					+ parserResult
							.getCountFree()
					+ ")");
		}else{
			mCTWantEate.setText("我要白吃");
		}
	
	
		
		
	}
	

	/**
	 * 
	 * @Title: justIsVideo
	 * @Description: 判断是否是视频
	 * @param
	 * @return
	 * @throws
	 */
	private void justIsVideo(WishDetailBean parserResult) {
		String storyStype = parserResult.getStoryType();
		if (storyStype.equals("3")) {
			mIVVideoIcon.setVisibility(View.VISIBLE);
		} else {
			mIVVideoIcon.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 
	 * @Title: setConvertInfo
	 * @Description: 设置封面图信息
	 * @param
	 * @return
	 * @throws
	 */
	private void setConvertInfo(WishDetailBean parserResult) {
		videoUrl = parserResult.getStoryVideoUrl();
		ImageLoader.getInstance().displayImage(parserResult.getCoverThumb(),
				mIvConvertImage, CommonUtil.OPTIONS_ALBUM);
		mTvStoryName.setText(parserResult.getStoryName());
		CommonUtil.setUserGrade(mCtUserName, parserResult.getUserGrade(),
				"right");
		String type = parserResult.getType();
		if (!TextUtils.isEmpty(type)) {
			if (type.equals("0")) {// 心愿
				mLLWishDetail.setVisibility(View.VISIBLE);
				mLLCommentWishDetail.setVisibility(View.GONE);
			} else {// 点评
				mLLWishDetail.setVisibility(View.GONE);
				mLLCommentWishDetail.setVisibility(View.VISIBLE);
			}

		} else {
			mLLWishDetail.setVisibility(View.VISIBLE);
			mLLCommentWishDetail.setVisibility(View.GONE);
		}
		String commentLike = parserResult.getCommentLike();
		String countLike = parserResult.getCountLike();
		String countRecommend = parserResult.getCountRecommend();
		if (!TextUtils.isEmpty(commentLike)) {
			if (commentLike.equals("1")) {
				mCTGood.setTextColor(TootooPlusApplication.getAppContext().getResources().getColor(R.color.btn_org_color));
				mCTGood.setCompoundDrawablesWithIntrinsicBounds(
						null,
						getActivity().getResources().getDrawable(
								R.drawable.icon_good_clicked), null, null);
			} else {
				mCTGood.setTextColor(TootooPlusApplication.getAppContext().getResources().getColor(R.color.btn_gray_color));
				mCTGood.setCompoundDrawablesWithIntrinsicBounds(
						null,
						getActivity().getResources().getDrawable(
								R.drawable.icon_good_noclick), null, null);
			}
		}
		if (!TextUtils.isEmpty(countLike)) {
			mCTGood.setText(countLike);

		}
		if (!TextUtils.isEmpty(countRecommend)) {
			mRBDetailComment.setRating(Float.valueOf(countRecommend));
		}
		
			

	}

	/**
	 * 
	 * @Title: setTitleUserInfo
	 * @Description: 设置用户信息
	 * @param
	 * @return
	 * @throws
	 */
	private void setTitleUserInfo(WishDetailBean parserResult) {
		String userName = parserResult.getUserName();
		mCtUserName.setText(userName);
		String strDate=parserResult.getCreateDate();
		if(!TextUtils.isEmpty(strDate)){
			String date=CommonUtil.friendly_time(strDate);
			mCtDate.setText(date);
		}
		ImageLoader.getInstance().displayImage(parserResult.getLogoUrl(),
				new ImageViewAware(mIVphoto), CommonUtil.OPTIONS_HEADPHOTO);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_detail_return:// 返回按钮
			getActivity().finish();
			break;
		case R.id.iv_detail_convert:// 点击封面图
			chilckTheConvert();
			break;
		case R.id.ct_want_buy:// 去购买
			wantBuy();
			break;
		case R.id.ct_want_eate:// 点击我要白吃
			justIsEate();
			break;
		case R.id.ct_good:// 点赞的人
			justIsComment();
			break;
		case R.id.ct_comment_want_buy:
			wantBuy();
			break;

		}

	}

	/**
	 * 
	 * @Title: justIsEate
	 * @Description: 判断是否白吃过
	 * @param
	 * @return
	 * @throws
	 */
	private void justIsEate() {
		// 判断是否白吃过 如果没有白吃过 那么就白吃一下
		if (parserResult != null) {
			if (parserResult.getFree().equals("0")) {// 默认值
				wantEate();
			} else if (parserResult.getFree().equals("1")) {
				ComponentUtil.showToast(getActivity(), "已点击过白吃");
				// 如果白吃了就跳转到用户列表
				// showDialogFragmentPhotp();
			}
		}

	}

	private void justIsComment() {
		if (parserResult != null) {
			if (parserResult.getCommentLike().equals("0")) {// 默认值
				wantToComment();
			} else if (parserResult.getCommentLike().equals("1")) {// 点击过
				ComponentUtil.showToast(getActivity(), "已点击过赞");
				// 如果白吃了就跳转到用户列表
				// showDialogFragmentPhotp();
			}
		}

	}

	/**
	 * 
	 * @Title: wantEate
	 * @Description: 我要白吃 请求数据，设置数据
	 * @param
	 * @return
	 * @throws
	 */
	private void wantEate() {
		if (NetworkUtil.isNetworkAvaliable(getActivity())) {
			RequestParamsNet requestParamsNet = new RequestParamsNet();
			String userid = SharedPreferenceHelper
					.getLoginUserId(getActivity());
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.USER_ID, userid);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYID, storyId);
			CommonUtil.xUtilsPostSend(TootooeNetApiUrlHelper.ADD_I_WANT_FREE,
					requestParamsNet, new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String strCallBack = responseInfo.result;
							if (!TextUtils.isEmpty(strCallBack)) {
								try {
									JSONObject object = new JSONObject(
											strCallBack);
									if (object.has("Status")) {
										if (object.getString("Status").equals(
												"1")) {
											parserResult.setFree("1");
											String countFree = parserResult
													.getCountFree();
											if (!TextUtils.isEmpty(countFree)) {
												int iCoutFree = Integer
														.valueOf(countFree) + 1;
												parserResult.setCountFree(String
														.valueOf(iCoutFree));
												Drawable noClick = TootooPlusApplication.getAppContext().getResources().getDrawable(R.drawable.icon_eate_already);
												mCTWantEate.setCompoundDrawablesWithIntrinsicBounds(null, noClick, null, null);
												mCTWantEate.setTextColor(TootooPlusApplication.getAppContext().getResources().getColor(R.color.btn_org_color));
												mCTWantEate.setText("我要白吃("
														+ parserResult
																.getCountFree()
														+ ")");
											}
										} else if (object.getString("Status")
												.equals("4266")) {
											Drawable noClick = TootooPlusApplication.getAppContext().getResources().getDrawable(R.drawable.icon_eate_already);
											mCTWantEate.setCompoundDrawablesWithIntrinsicBounds(null, noClick, null, null);
											mCTWantEate.setTextColor(TootooPlusApplication.getAppContext().getResources().getColor(R.color.btn_org_color));
											mCTWantEate.setText("我要白吃("
													+ parserResult
															.getCountFree()
													+ ")");
											//已点击过
//											showDialogFragmentPhotp();
										} else {
											ComponentUtil.showToast(
													getActivity(), "想吃失败！");
										}
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}

						}

						@Override
						public void onFailure(HttpException error, String msg) {

						}
					});
		} else {
			ComponentUtil.showToast(getActivity(), this.getResources()
					.getString(R.string.errcode_network_response_timeout));

		}

	}

	/**
	 * 
	 * @Title: wantToComment
	 * @Description: 我要点赞
	 * @param
	 * @return
	 * @throws
	 */
	private void wantToComment() {
		if (NetworkUtil.isNetworkAvaliable(getActivity())) {
			RequestParamsNet requestParamsNet = new RequestParamsNet();
			String userid = SharedPreferenceHelper
					.getLoginUserId(getActivity());
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.USER_ID, userid);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYID, storyId);
			CommonUtil.xUtilsPostSend(TootooeNetApiUrlHelper.WANT_TO_COMMENT,
					requestParamsNet, new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String strCallBack = responseInfo.result;
							if (!TextUtils.isEmpty(strCallBack)) {
								try {
									JSONObject object = new JSONObject(
											strCallBack);
									if (object.has("Status")) {
										if (object.getString("Status").equals(
												"1")) {
											parserResult.setCommentLike("1");
											String countFree = parserResult
													.getCountLike();
											if (!TextUtils.isEmpty(countFree)) {
												int iCoutFree = Integer
														.valueOf(countFree) + 1;
												parserResult.setCountLike(String
														.valueOf(iCoutFree));
												mCTGood.setText(String
														.valueOf(iCoutFree));
												mCTGood.setTextColor(TootooPlusApplication.getAppContext().getResources().getColor(R.color.btn_org_color));
											}
											mCTGood.setCompoundDrawablesWithIntrinsicBounds(
													null,
													getActivity()
															.getResources()
															.getDrawable(
																	R.drawable.icon_good_clicked),
													null, null);
										} else if (object.getString("Status")
												.equals("4267")) {
											ComponentUtil.showToast(
													getActivity(), "已点击过");
										} else {
											ComponentUtil.showToast(
													getActivity(), "想吃失败！");
										}
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}

						}

						@Override
						public void onFailure(HttpException error, String msg) {

						}
					});
		} else {
			ComponentUtil.showToast(getActivity(), this.getResources()
					.getString(R.string.errcode_network_response_timeout));

		}

	}

	/**
	 * 
	 * @Title: showDialogFragmentPhotp
	 * @Description: 跳转到用户头像
	 * @param
	 * @return
	 * @throws
	 */
	private void showDialogFragmentPhotp() {
		fl_photouser.setVisibility(View.VISIBLE);
		if (photoUserAllWishFragment == null) {
			photoUserAllWishFragment = new PhotoUserAllWishFragment(
					fl_photouser, storyId);
			FragmentManager manager = getActivity().getSupportFragmentManager();
			FragmentTransaction beginTransatiion = manager.beginTransaction();
			beginTransatiion.add(R.id.photo_user, photoUserAllWishFragment);
			beginTransatiion.commit();
		}

	}

	/**
	 * 
	 * @Title: wantBuy
	 * @Description: 跳转到导购连接地址
	 * @param
	 * @return
	 * @throws
	 */
	private void wantBuy() {
		if (parserResult != null) {
			if (!TextUtils.isEmpty(parserResult.getShoppingUrl())) {// 可以shopp
//				skipToWebView(parserResult.getShoppingUrl());
				requeBrowseCount(parserResult.getShoppingUrl());
			} else {
				ComponentUtil.showToast(getActivity(), "暂无购买地址");
			}
		}

	}
	private void requeBrowseCount(final String shoppingUrl){
		// 创建封面图
		if ((NetworkUtil.isNetworkAvaliable(TootooPlusApplication
				.getAppContext()))) {
			// 显示进度
			RequestParamsNet requestParamsNet = new RequestParamsNet();
			String type = parserResult.getType();
			if(!TextUtils.isEmpty(type)){
				if(type.equals("0")){
					//当是0的时候是心愿，但是出入的值的参数当是1的时候是心愿，所以传1
					requestParamsNet.addQueryStringParameter(
							TootooeNetApiUrlHelper.TYPE, ConstantsTooTooEHelper.BROWSE_TYPE_WISH);
					if (!TextUtils.isEmpty(storyId)) {
						requestParamsNet.addQueryStringParameter(
								ConstantsTooTooEHelper.STORYID, storyId);
					}
				
				}else{
					requestParamsNet.addQueryStringParameter(
							TootooeNetApiUrlHelper.TYPE, ConstantsTooTooEHelper.BROWSE_TYPE_COMMENT);
					if (!TextUtils.isEmpty(storyId)) {
						requestParamsNet.addQueryStringParameter(
								ConstantsTooTooEHelper.STORYID, storyId);
					}
					//当不是0的时候是心愿，但是出入的值的参数当是2的时候是点评，所以传2  没办法后台参数没规范。
				}
				
			}
		
			
			CommonUtil.xUtilsPostSend(TootooeNetApiUrlHelper.GOTOBUY,
					requestParamsNet, new RequestCallBack<String>() {

						private String status;

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
						
							String jsonStr = new String(responseInfo.result);
							skipToWebView(shoppingUrl);
							try {
								JSONObject jsobj = new JSONObject(jsonStr);
								if (jsobj.has("Status")) {
									status = jsobj.getString("Status");
									if (status.equals("1")) {

									} else if (jsonStr.equals("1223")) {
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
						

						@Override
						public void onFailure(HttpException error, String msg) {
							skipToWebView(shoppingUrl);
						}
					});

		} else {
			skipToWebView(shoppingUrl);
		}

	
	}
	/**
	 * 
	 * @Title: skipToWebView
	 * @Description: 跳转到导购连接界面
	 * @param
	 * @return
	 * @throws
	 */
	private void skipToWebView(String shoppingUrl) {
		Intent intent = new Intent(getActivity(), GoBuyBrowserActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle bundle = new Bundle();
		bundle.putString("url", shoppingUrl);
		String type = parserResult.getType();
		if(!TextUtils.isEmpty(type)){
			if(type.equals("0")){
				//当是0的时候是心愿，但是出入的值的参数当是1的时候是心愿，所以传1
				GoBuyBrowserActivity.skipPar("1", storyId);
			}else{
				//当不是0的时候是心愿，但是出入的值的参数当是2的时候是点评，所以传2  没办法后台参数没规范。
				GoBuyBrowserActivity.skipPar("2", storyId);
			}
			
		}
		
		intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
		startActivity(intent);

	}

	/**
	 * 
	 * @Title: chilckTheConvert
	 * @Description: 点击封面图
	 * @param
	 * @return
	 * @throws
	 */
	private void chilckTheConvert() {
		if (parserResult != null) {
			if (!TextUtils.isEmpty(parserResult.getStoryType())
					&& parserResult.getStoryType().equals("3")) {
				skipToVideoPlay();

			}
		}
	}

	/**
	 * 
	 * @Title: skipToVideoPlay
	 * @Description: 播放视频
	 * @param
	 * @return
	 * @throws
	 */
	private void skipToVideoPlay() {
		Intent intent = new Intent(getActivity(), VideoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(ConstantsTooTooEHelper.VIDEO_URL, videoUrl);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
		startActivity(intent);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDragItem(int from) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDraggingItem(int from, int to) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDropItem(int from, int to) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDragAndDropEnabled(int position) {
		return false;
	}

	/**
	 * 
	 * @Title: skipToChatGroup
	 * @Description: TODO
	 * @param
	 * @return
	 * @throws
	 */
	private void skipToChatGroup() {

		if ((NetworkUtil.isNetworkAvaliable(TootooPlusApplication
				.getAppContext()))) {
			RequestParamsNet requestParamsNet = new RequestParamsNet();
			String userid = SharedPreferenceHelper
					.getLoginUserId(TootooPlusApplication.getAppContext());
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.USER_ID, userid);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYID, storyId);
			CommonUtil.xUtilsPostSend(TootooeNetApiUrlHelper.CHAT_GROUP,
					requestParamsNet, new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String jsonStr = new String(responseInfo.result);
							GetGroupChatParser groupChatParser = new GetGroupChatParser(
									jsonStr);
							List<GroupChatBean> groupChatList = groupChatParser
									.getParseResult(jsonStr);
							if (groupChatList != null
									&& groupChatList.size() > 0) {
								Intent intent = new Intent(getActivity(),
										ChatActivity.class);
								Bundle bundle = new Bundle();
								GroupChatList groupChatListBean = new GroupChatList();
								groupChatListBean
										.setGroupChatBeans(groupChatList);
								if (!TextUtils.isEmpty(groupChatParser
										.getName())) {
									groupChatListBean
											.setActivityName(groupChatParser
													.getName());
								}
								// groupChatListBean.setActivityId(resultData.getActivityId());
								intent.putExtra("groupchatlist",
										groupChatListBean);
								startActivity(intent);
							} else {
								int status = groupChatParser.getStatus();
								switch (status) {
								case 1:

									break;
								case 3101:
									LogUtil.error("参数缺失", "");
									break;
								case 3102:
									LogUtil.error("参数错误", "");
									break;
								case 3103:
									ComponentUtil.showToast(getActivity(),
											"会员增加失败");
									break;
								case 3105:
									ComponentUtil.showToast(getActivity(),
											"无法找到该组");
									break;

								default:
									break;
								}

							}

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							ComponentUtil.showToast(
									TootooPlusApplication.getAppContext(),
									getResources()
											.getString(
													R.string.errcode_network_response_timeout));
						}
					});

		} else {
			ComponentUtil.showToast(
					TootooPlusApplication.getAppContext(),
					this.getResources().getString(
							R.string.errcode_network_response_timeout));
		}

	}
	@Override
	public void OnStatusDissMissListener(boolean isDissmiss) {
		if(mLLCommentWishDetail!=null&&mLLCommentWishDetail.getVisibility()==View.VISIBLE){
			if(isAdded()){
				boolean isDianPing = SharedPreferenceHelper.getFirstDianzan(getActivity());
			if(isDianPing){
				CommonUtil.showFirstGuideDialog(getActivity(), ConstantsTooTooEHelper.FIRST_GUIDE_DIANZAN);
			}
			}
			
		}
		
	}

}
