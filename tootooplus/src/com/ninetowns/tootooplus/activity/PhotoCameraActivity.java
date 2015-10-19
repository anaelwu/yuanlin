package com.ninetowns.tootooplus.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.library.util.ImageUtil;
import com.ninetowns.library.util.LogUtil;
import com.ninetowns.library.util.NetworkUtil;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.bean.ConVertBean;
import com.ninetowns.tootooplus.bean.CreateActivitySecondBean;
import com.ninetowns.tootooplus.bean.PhotoSelectOrConvertBean;
import com.ninetowns.tootooplus.bean.SecondStepStoryBean;
import com.ninetowns.tootooplus.bean.StoryDetailListBean;
import com.ninetowns.tootooplus.bean.UpLoadFileBean;
import com.ninetowns.tootooplus.fragment.UpLoadViewDialogFragment;
import com.ninetowns.tootooplus.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootooplus.util.CommonUtil;
import com.ninetowns.ui.Activity.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 
 * @ClassName: PhotoCameraActivity
 * @Description: 拍照创建心愿
 * @author wuyulong
 * @date 2015-1-27 上午10:47:20
 * 
 */
public class PhotoCameraActivity extends BaseActivity {
	private Bitmap finalUpBmp;
	private ImageView mPhoto;
	private String storyid;
	private int itemIndewx;
	private Lock lock = new ReentrantLock();
	private Handler handler = new Handler() {
		private UpLoadFileBean bean;
		private List<UpLoadFileBean> list;

		@Override
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (fragment != null) {
					fragment.dismiss();
				}
				list = (List<UpLoadFileBean>) msg.obj;
				setIsRecommendViewData();
				break;
			}

		}

		private void setIsRecommendViewData() {

			for (int i = 0; i < list.size(); i++) {
				bean = list.get(i);
				String imagefial = bean.getThumbFileUrl();
				String yuantu = bean.getFileUrl();
				String rectimage = bean.getDragRectangleImg();
				String squareimage = bean.getDragSquareImg();
				String bigfourtothree = bean.getDragRectangleBigImg();
				String bigDragSquareImg = bean.getDragSquareBigImg();
				String defaultType = bean.getDefaultType();
				if (!TextUtils.isEmpty(defaultType)) {// 1、长方形 2:1;
														// 2、小正方形；3、长方形4：3；4、大正方形

					if (defaultType.equals("1")) {
						squareimage = bean.getDragRectangleImg();
					} else if (defaultType.equals("2")) {
						squareimage = bean.getDragSquareImg();

					} else if (defaultType.equals("3")) {
						squareimage = bean.getDragRectangleBigImg();

					} else if (defaultType.equals("4")) {
						squareimage = bean.getDragSquareBigImg();

					}

				}
				if (list.size() == 1) {
					if (i == 0) {// 如果只有一张
						if (isRecommendConvertView) {
							createConvert(yuantu, imagefial);
						} else if (isCreateView || isEditextView
								|| isRecommendView) {
							postData(yuantu, rectimage, squareimage,
									defaultType, bigfourtothree,
									bigDragSquareImg);
						} else if (isConvertView) {
							createConvert(yuantu, imagefial);
						}

					}
				}

			}

		}

	};
	private PhotoSelectOrConvertBean photoSelectOrConvert;
	private LinkedList<StoryDetailListBean> detailList;
	private Bundle bundle;
	private boolean isEditextView;
	private boolean isCreateView;
	private boolean isDraftView;
	private boolean isConvertView;
	private boolean isRecommendView;
	private boolean isRecommendConvertView;
	private String imageUrl;
	private PhotoSelectOrConvertBean isCreateViewPhotoConvertBean;
	private List<StoryDetailListBean> isCreateListBean = new ArrayList<StoryDetailListBean>();
	private UpLoadViewDialogFragment fragment;
	private String UpdateStoryId = "0";
	private boolean isActivitySecondView;
	private int currentPosition;
	private SecondStepStoryBean secondStepStoryBean;
	private CreateActivitySecondBean storyBean;
	private ConVertBean mConvertBean;
	private String tu;
	private String type;// 传递的参数
	private String activity_create_Id;
	private String storyCreateId;
	private HashMap<String, Integer> mHashMap;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.photocamera);
		photoSelectOrConvert = new PhotoSelectOrConvertBean();
		detailList = new LinkedList<StoryDetailListBean>();
		bundle = this.getIntent().getBundleExtra(ConstantsTooTooEHelper.BUNDLE);
		getType();
		if (isCreateView) {
			storyid = bundle.getString("storyid");
			UpdateStoryId = bundle.getString("UpdateStoryId");
			if (!TextUtils.isEmpty(UpdateStoryId) && UpdateStoryId.equals("-1")) {// 故事第二步
				isActivitySecondView = true;
				currentPosition = bundle.getInt("currentPosition");
				secondStepStoryBean = (SecondStepStoryBean) bundle
						.getSerializable(ConstantsTooTooEHelper.BundleResopnse);
				storyBean = secondStepStoryBean.getStoryList().get(
						currentPosition);
				isCreateListBean = storyBean.getWishDetailBean();
			} else {
				isCreateViewPhotoConvertBean = (PhotoSelectOrConvertBean) bundle
						.getSerializable(ConstantsTooTooEHelper.BundleResopnse);
				mHashMap=isCreateViewPhotoConvertBean.getmHashMap();
				storyid = bundle.getString("storyid");
				UpdateStoryId = bundle.getString("UpdateStoryId");
				isCreateListBean = isCreateViewPhotoConvertBean.getListBean();
			}

		} else if (isConvertView) {

			storyid = bundle.getString("storyid");
			UpdateStoryId = bundle.getString("UpdateStoryId");
			if (!TextUtils.isEmpty(UpdateStoryId) && UpdateStoryId.equals("-1")) {// 故事第二步
				isActivitySecondView = true;
				currentPosition = bundle.getInt("currentPosition");
				secondStepStoryBean = (SecondStepStoryBean) bundle
						.getSerializable(ConstantsTooTooEHelper.BundleResopnse);
				storyBean = secondStepStoryBean.getStoryList().get(
						currentPosition);
				isCreateListBean = storyBean.getWishDetailBean();
				mConvertBean = storyBean.getConvertBean();
			} else {
				isCreateViewPhotoConvertBean = (PhotoSelectOrConvertBean) bundle
						.getSerializable(ConstantsTooTooEHelper.BundleResopnse);
				mHashMap=isCreateViewPhotoConvertBean.getmHashMap();
				storyid = bundle.getString("storyid");
				isCreateListBean = isCreateViewPhotoConvertBean.getListBean();
			}

		} else if (isEditextView) {
			UpdateStoryId = bundle.getString("UpdateStoryId");
			isCreateViewPhotoConvertBean = (PhotoSelectOrConvertBean) bundle
					.getSerializable(ConstantsTooTooEHelper.BundleResopnse);
			mHashMap=isCreateViewPhotoConvertBean.getmHashMap();
			storyid = bundle.getString("storyid");
			isCreateListBean = isCreateViewPhotoConvertBean.getListBean();
		} else if (isRecommendView) {// 编辑追加

			UpdateStoryId = bundle.getString("UpdateStoryId");
			isCreateViewPhotoConvertBean = (PhotoSelectOrConvertBean) bundle
					.getSerializable(ConstantsTooTooEHelper.BundleResopnse);
			mHashMap=isCreateViewPhotoConvertBean.getmHashMap();
			storyid = bundle.getString("storyid");
			isCreateListBean = isCreateViewPhotoConvertBean.getListBean();

		} else {
			isCreateViewPhotoConvertBean = (PhotoSelectOrConvertBean) bundle
					.getSerializable(ConstantsTooTooEHelper.BundleResopnse);
			storyid = bundle.getString("storyid");
			UpdateStoryId = bundle.getString("UpdateStoryId");
			if (isCreateViewPhotoConvertBean != null) {
				isCreateListBean = isCreateViewPhotoConvertBean.getListBean();
				mHashMap=isCreateViewPhotoConvertBean.getmHashMap();
			}

		}

		mPhoto = (ImageView) findViewById(R.id.iv_photo);
		if (!TextUtils.isEmpty(imageUrl)) {
			cutImage(imageUrl);
		}
	}

	private void getType() {
		imageUrl = bundle.getString("picPath");
		type = bundle.getString(TootooeNetApiUrlHelper.TYPE);
		activity_create_Id = bundle
				.getString(TootooeNetApiUrlHelper.ACTIVITY_CREATE_ID);
		storyCreateId = bundle
				.getString(TootooeNetApiUrlHelper.STORY_CREATE_ID);
		isEditextView = bundle.getBoolean(ConstantsTooTooEHelper.isEditextView);
		isCreateView = bundle.getBoolean(ConstantsTooTooEHelper.isCreateView);
		isDraftView = bundle.getBoolean(ConstantsTooTooEHelper.isDraftView);
		isConvertView = bundle.getBoolean(ConstantsTooTooEHelper.isConvertView);
		isRecommendView = bundle
				.getBoolean(ConstantsTooTooEHelper.isRecommendView);
		isRecommendConvertView = bundle
				.getBoolean(ConstantsTooTooEHelper.isConvertRecommendView);
	}

	private int indey;
	private String PageId;

	public void postData(final String yuantu, final String rectimage,
			final String squareimage, final String defaultType,
			final String fourthree, final String onetoone) {
		// StoryId：故事Id (必填)
		// PageType：故事页类型：1,文字，2图片，3视频 (必填)
		// PageContent：故事页文字
		// PageImgThumb：故事页缩略图
		// PageImg：故事页图片或直播录播封面图地址
		// PageVideoUrl：故事页录播视频地址
		// RecordId：录制Id
		// PageDesc：故事页描述

		// 创建封面图

		if ((NetworkUtil.isNetworkAvaliable(this))) {
			// 显示进度
			showProgressDialog(PhotoCameraActivity.this);
			RequestParamsNet requestParamsNet = new RequestParamsNet();
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYCREATE_STORYID, storyid);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYCREATE_PAGE_TYPE, "2");// 2是图片3是视频
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYCREATE_THUMBIMAGE, squareimage);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYCREATE_PAGE_IMAGE, yuantu);
			CommonUtil.xUtilsPostSend(
					TootooeNetApiUrlHelper.STORYCREATE_PAGE_API,
					requestParamsNet, new RequestCallBack<String>() {

						private StoryDetailListBean heightItem;

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String strRespone = responseInfo.result;
							if (!TextUtils.isEmpty(strRespone)) {
								try {
									JSONObject jsonObject = new JSONObject(
											strRespone);
									if (jsonObject.has("Status")) {
										String status = jsonObject
												.getString("Status");
										if (!status.equals("1")) {
											LogUtil.systemlogError("创建故事页拍照失败",
													"");
										}
									}
									if (jsonObject.has("Data")) {
										String st = jsonObject
												.getString("Data");
										JSONObject storyjsonobj = new JSONObject(
												st);
										if (storyjsonobj.has("PageId")) {
											PageId = storyjsonobj
													.getString("PageId");
											if (!isRecommendConvertView) {
												if(mHashMap!=null){
													int insertPosition=mHashMap.get(ConstantsTooTooEHelper.POSITION_KEY);
													if(insertPosition!=ConstantsTooTooEHelper.NO_ITEM_CLICK_INSERT_CODE){
														insertPosition(yuantu, rectimage, squareimage, defaultType, fourthree, onetoone, insertPosition);
													}else{
														insertBottom(yuantu, rectimage,
																squareimage,
																defaultType, fourthree,
																onetoone);
													}
												}else{
													insertBottom(yuantu, rectimage,
															squareimage,
															defaultType, fourthree,
															onetoone);
												}
												
												if (isActivitySecondView) {
													storyBean
															.setWishDetailBean(isCreateListBean);
												} else {
													isCreateViewPhotoConvertBean
															.setListBean(isCreateListBean);
												}

												skipToActivity();
											}
										}
									}

								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						}

						/** 
						* @Title: insertBottom 
						* @Description: 插入到底部
						* @param  
						* @return   
						* @throws 
						*/
						private void insertBottom(final String yuantu,
								final String rectimage,
								final String squareimage,
								final String defaultType,
								final String fourthree, final String onetoone) {
							int listSize = isCreateListBean
									.size();
							for (int i = 0; i < listSize; i++) {
								if (isCreateListBean.get(i)
										.getItemIndex() >= isCreateListBean
										.get(indey)
										.getItemIndex()) {
									indey = i;
								}
							}
							if (listSize > 0) {
								heightItem = isCreateListBean
										.get(indey);
								itemIndewx = heightItem
										.getItemIndex();
							}

							StoryDetailListBean storyDetailListBean = new StoryDetailListBean();
							if(!TextUtils.isEmpty(defaultType)&&!defaultType.equals("null")){
								storyDetailListBean.setElementType(Integer
										.valueOf(defaultType));
							}else{
								storyDetailListBean.setElementType(2);
							}
							
							storyDetailListBean
									.setPageType("2");
							storyDetailListBean
									.setPageId(PageId);
							storyDetailListBean
									.setDragSquareImg(squareimage);
							storyDetailListBean
									.setPageImgThumb(squareimage);
							storyDetailListBean
									.setDragRectangleImg(rectimage);
							storyDetailListBean
									.setPageImgThumbBigRectangle(fourthree);
							storyDetailListBean
									.setPageImgThumbBigSquare(onetoone);
							storyDetailListBean.setDefaultType(defaultType);
							storyDetailListBean
									.setPageImg(yuantu);
							int itemIndexO = itemIndewx;
							if (listSize == 0) {
								storyDetailListBean
										.setLocation(1);
								storyDetailListBean
										.setItemIndex(0);
								if (!TextUtils.isEmpty(defaultType)&&!defaultType.equals("null")) {
									int elementType = Integer
											.valueOf(defaultType);
									storyDetailListBean
											.setElementType(elementType);

								} else {
									storyDetailListBean.setElementType(2);
								}
							} else {
								if (heightItem != null) {
									int location = heightItem
											.getLocation();
									int elementType = heightItem
											.getElementType();
									if (location == 1) {//小
										
										//
										if(elementType==2){
											storyDetailListBean.setLocation(2);
										}else{
											storyDetailListBean.setLocation(1);
										}
									
										
										if (!TextUtils.isEmpty(defaultType)&&!defaultType.equals("null")) {

											storyDetailListBean
													.setElementType(Integer.valueOf(defaultType));

										} else {
											storyDetailListBean
													.setElementType(2);
										}
										
										
										storyDetailListBean
												.setItemIndex(itemIndexO);
//														
									} else {
										if(!TextUtils.isEmpty(defaultType)&&!defaultType.equals("null")){
											storyDetailListBean
											.setElementType(Integer.valueOf(defaultType));
										}else{
											storyDetailListBean
											.setElementType(2);
										}
										storyDetailListBean
												.setLocation(1);
										storyDetailListBean
												.setItemIndex(itemIndexO + 1);
									}
								}

							}

							isCreateListBean
									.add(storyDetailListBean);
						}
						private void insertPosition(final String yuantu,
								final String rectimage,
								final String squareimage,
								final String defaultType,
								final String fourthree, final String onetoone,int inPosition) {
							int listSize = isCreateListBean
									.size();
						
							if (listSize > 0) {
								heightItem = isCreateListBean
										.get(inPosition);
								itemIndewx = heightItem
										.getItemIndex();
							}

							StoryDetailListBean storyDetailListBean = new StoryDetailListBean();
							if(!TextUtils.isEmpty(defaultType)&&!defaultType.equals("null")){
								storyDetailListBean.setElementType(Integer
										.valueOf(defaultType));
							}else{
								storyDetailListBean.setElementType(2);
							}
							
							storyDetailListBean
									.setPageType("2");
							storyDetailListBean
									.setPageId(PageId);
							storyDetailListBean
									.setDragSquareImg(squareimage);
							storyDetailListBean
									.setPageImgThumb(squareimage);
							storyDetailListBean
									.setDragRectangleImg(rectimage);
							storyDetailListBean
									.setPageImgThumbBigRectangle(fourthree);
							storyDetailListBean
									.setPageImgThumbBigSquare(onetoone);
							storyDetailListBean.setDefaultType(defaultType);
							storyDetailListBean
									.setPageImg(yuantu);
							int itemIndexO = itemIndewx;
							if (listSize == 0) {
								storyDetailListBean
										.setLocation(1);
								storyDetailListBean
										.setItemIndex(0);
								if (!TextUtils.isEmpty(defaultType)&&!defaultType.equals("null")) {
									int elementType = Integer
											.valueOf(defaultType);
									storyDetailListBean
											.setElementType(elementType);

								} else {
									storyDetailListBean.setElementType(2);
								}
							} else {
								if (heightItem != null) {
									int location = heightItem
											.getLocation();
									int elementType = heightItem
											.getElementType();
									if (location == 1) {//小
										
										//
										if(elementType==2){
											storyDetailListBean.setLocation(2);
										}else{
											storyDetailListBean.setLocation(1);
										}
									
										
										if (!TextUtils.isEmpty(defaultType)&&!defaultType.equals("null")) {

											storyDetailListBean
													.setElementType(Integer.valueOf(defaultType));

										} else {
											storyDetailListBean
													.setElementType(2);
										}
										
										
										storyDetailListBean
												.setItemIndex(itemIndexO);
//														
									} else {
										if(!TextUtils.isEmpty(defaultType)&&!defaultType.equals("null")){
											storyDetailListBean
											.setElementType(Integer.valueOf(defaultType));
										}else{
											storyDetailListBean
											.setElementType(2);
										}
										storyDetailListBean
												.setLocation(1);
										storyDetailListBean
												.setItemIndex(itemIndexO + 1);
									}
								}

							}

							isCreateListBean
									.add(inPosition+1,storyDetailListBean);
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							closeProgressDialog(PhotoCameraActivity.this);
							ComponentUtil
									.showToast(
											PhotoCameraActivity.this,
											getResources()
													.getString(
															R.string.errcode_network_response_timeout));
						}
					});

		} else {
			ComponentUtil.showToast(
					this,
					this.getResources().getString(
							R.string.errcode_network_response_timeout));
		}

	}

	private String status;

	/**
	 * 
	 * @Title: createConvert
	 * @Description: 创建封面图
	 * @param yuantu
	 * @param convert
	 * @return void 返回类型
	 * @throws
	 */
	public void createConvert(final String yuantu, final String convert) {
		// 创建封面图
		if ((NetworkUtil.isNetworkAvaliable(this))) {
			// 显示进度
			showProgressDialog(PhotoCameraActivity.this);
			RequestParamsNet requestParamsNet = new RequestParamsNet();
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYID, storyid);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.COVER_THUMB, convert);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.COVER_IMG, yuantu);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORY_TYPE, "2");// 拍摄相片
			CommonUtil.xUtilsPostSend(TootooeNetApiUrlHelper.STORY_NAME_UPDATE,
					requestParamsNet, new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							closeProgressDialog(PhotoCameraActivity.this);
							String jsonStr = new String(responseInfo.result);
							try {
								JSONObject jsobj = new JSONObject(jsonStr);
								if (jsobj.has("Status")) {
									status = jsobj.getString("Status");
									if (status.equals("1")) {
										if (isConvertView) {

											ConVertBean convertBean = new ConVertBean();
											convertBean.setCoverThumb(convert);
											convertBean.setCoverImg(yuantu);
											convertBean.setStoryType("2");
											if (isActivitySecondView) {
												storyBean
														.setConvertBean(convertBean);
											} else {
												if (isCreateViewPhotoConvertBean != null) {
													String storyName = isCreateViewPhotoConvertBean
															.getConvertBean()
															.getStoryName();
													convertBean
															.setStoryName(storyName);
													isCreateViewPhotoConvertBean
															.setConvertBean(convertBean);
												} else {
													photoSelectOrConvert
															.setConvertBean(convertBean);
												}

											}
										} else {
											ConVertBean convertBean = new ConVertBean();
											convertBean.setCoverThumb(convert);
											convertBean.setCoverImg(yuantu);
											convertBean.setStoryType("2");
											photoSelectOrConvert
													.setConvertBean(convertBean);
											photoSelectOrConvert
													.setConvertBean(convertBean);
											// StoryDetailListBean bean=new
											// StoryDetailListBean();
											// bean.setPageImg(convert);
											// bean.setListCoverImg(convert);
											// bean.cellX = 0;
											// bean.cellY = 0;
											// bean.spanX = 4;//正方形
											// bean.spanY =4;
											// bean.setPageType("3");
											// bean.setFileUrl(fileurl);
											// detailList.add(bean);
											photoSelectOrConvert
													.setListBean(detailList);

										}

										skipToActivity();
									} else if (jsonStr.equals("1223")) {
										LogUtil.systemlogError("返回失败",
												"封面图返回失败");

									}

								}

							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							// TODO Auto-generated method stub
							closeProgressDialog(PhotoCameraActivity.this);
							ComponentUtil
									.showToast(
											PhotoCameraActivity.this,
											getResources()
													.getString(
															R.string.errcode_network_response_timeout));
						}
					});

		} else {
			ComponentUtil.showToast(
					this,
					this.getResources().getString(
							R.string.errcode_network_response_timeout));
		}
	}

	/**
	 * 
	 * @Title: skipToActivity
	 * @Description: 跳转到创建心愿的界面
	 * @param
	 * @return void
	 * @throws
	 */
	private void skipToActivity() {

		if (isRecommendConvertView) {
			Intent intent = new Intent(this, CreateWishActivity.class);
			bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
					photoSelectOrConvert);
			bundle.putString("storyid", storyid);
			justIsCommentOrWishBundle(bundle);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
			startActivity(intent);
		} else if (isCreateView) {
			if (isActivitySecondView) {
				Intent intent = new Intent(this,
						CreateActSecondStepActivity.class);
				bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
						secondStepStoryBean);
				justIsCommentOrWishBundle(bundle);
				bundle.putString("UpdateStoryId", UpdateStoryId);
				bundle.putString("storyid", storyid);
				bundle.putInt("currentPosition", currentPosition);
				intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

			} else {
				Intent intent = new Intent(this, CreateWishActivity.class);
				ConstantsTooTooEHelper.putView(
						ConstantsTooTooEHelper.isConvertRecommendView, bundle);
				bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
						isCreateViewPhotoConvertBean);
				bundle.putString("storyid", storyid);
				justIsCommentOrWishBundle(bundle);
				intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}

		} else if (isConvertView) {
			if (isActivitySecondView) {
				Intent intent = new Intent(this,
						CreateActSecondStepActivity.class);
				bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
						secondStepStoryBean);
				justIsCommentOrWishBundle(bundle);
				bundle.putString("UpdateStoryId", UpdateStoryId);
				bundle.putString("storyid", storyid);
				bundle.putInt("currentPosition", currentPosition);
				intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			} else {
				Intent intent = new Intent(this, CreateWishActivity.class);

				ConstantsTooTooEHelper.putView(
						ConstantsTooTooEHelper.isConvertRecommendView, bundle);
				justIsCommentOrWishBundle(bundle);
				bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
						isCreateViewPhotoConvertBean);
				ConstantsTooTooEHelper.putView(
						ConstantsTooTooEHelper.isConvertRecommendView, bundle);
				bundle.putString("storyid", storyid);
				intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}

		} else if (isEditextView) {
			Intent intent = new Intent(this, CreateWishActivity.class);
			bundle.putString("UpdateStoryId", UpdateStoryId);
			justIsCommentOrWishBundle(bundle);
			bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
					isCreateViewPhotoConvertBean);
			ConstantsTooTooEHelper.putView(
					ConstantsTooTooEHelper.isRecommendView, bundle);
			bundle.putString("storyid", storyid);
			intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else if (isRecommendView) {
			Intent intent = new Intent(this, CreateWishActivity.class);
			bundle.putString("UpdateStoryId", UpdateStoryId);
			justIsCommentOrWishBundle(bundle);
			bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
					isCreateViewPhotoConvertBean);
			ConstantsTooTooEHelper.putView(
					ConstantsTooTooEHelper.isRecommendView, bundle);
			bundle.putString("storyid", storyid);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
			startActivity(intent);
		}

		finish();
	}

	/**
	 * @Title: justIsCommentOrWishBundle
	 * @Description: TODO
	 * @param
	 * @return
	 * @throws
	 */
	private void justIsCommentOrWishBundle(Bundle bundle) {
		if (!TextUtils.isEmpty(activity_create_Id)) {
			bundle.putString(TootooeNetApiUrlHelper.ACTIVITY_CREATE_ID,
					activity_create_Id);

		}
		if (!TextUtils.isEmpty(storyCreateId)) {
			bundle.putString(TootooeNetApiUrlHelper.STORY_CREATE_ID,
					storyCreateId);
		}
		if (!TextUtils.isEmpty(type)) {
			bundle.putString(TootooeNetApiUrlHelper.TYPE, type);
		}
	}

	/**
	 * 
	 * @Title: createStoryId
	 * @Description: 创建故事id
	 * @param
	 * @return void 返回类型
	 * @throws
	 */
	public void createStoryId() {// 创建封面图
		if ((NetworkUtil.isNetworkAvaliable(this))) {
			// 显示进度
			showProgressDialog(PhotoCameraActivity.this);
			RequestParamsNet requestParamsNet = new RequestParamsNet();
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.TYPE, type);
			if (!TextUtils.isEmpty(storyCreateId)) {
				requestParamsNet.addQueryStringParameter(
						TootooeNetApiUrlHelper.STORYID, storyCreateId);
			}
			if (!TextUtils.isEmpty(activity_create_Id)) {
				requestParamsNet.addQueryStringParameter(
						TootooeNetApiUrlHelper.ACTIVITYID, activity_create_Id);
			}
			String userId = SharedPreferenceHelper
					.getLoginUserId(TootooPlusApplication.getAppContext());
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.USERID, userId);
			CommonUtil.xUtilsPostSend(TootooeNetApiUrlHelper.STORYCREATEAPI,
					requestParamsNet, new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							closeProgressDialog(PhotoCameraActivity.this);
							String jsonStr = new String(responseInfo.result);

							try {
								JSONObject jsonObj = new JSONObject(jsonStr);
								String register_status = "";
								if (jsonObj.has("Status")) {
									register_status = jsonObj
											.getString("Status");
								}

								/**
								 * 暂时不能发短信，所以要记住这个字段，留着下一页输入
								 */
								if (jsonObj.has("Data")) {
									JSONObject jsonData = jsonObj
											.getJSONObject("Data");
									if (jsonData.has("StoryId")) {
										storyid = jsonData.getString("StoryId");
										uploadImage();

									}
								}

								if (register_status.equals("1")) {

								} else if (register_status.equals("1123")) {
									ComponentUtil
											.showToast(
													PhotoCameraActivity.this,
													getResources()
															.getString(
																	R.string.mobile_num_no_exist));
								}

							} catch (Exception e) {
								e.printStackTrace();
							}

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							closeProgressDialog(PhotoCameraActivity.this);
							ComponentUtil
									.showToast(
											PhotoCameraActivity.this,
											getResources()
													.getString(
															R.string.errcode_network_response_timeout));
						}
					});

		} else {
			ComponentUtil.showToast(
					this,
					this.getResources().getString(
							R.string.errcode_network_response_timeout));
		}

	}

	/**
	 * 
	 * @Title: uploadImage
	 * @Description: 上传图片
	 * @param
	 * @return void
	 * @throws
	 */
	private void uploadImage() {
		String upFileName = ImageUtil.makePhotoName(new Date());
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(upFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finalUpBmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
		List<File> listFile = new ArrayList<File>();
		File upFile = new File(upFileName);
		listFile.add(upFile);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(TootooeNetApiUrlHelper.UPLOAD_FIRLE_TYPE,
				TootooeNetApiUrlHelper.UPLOAD_FIRLE_TYPE_PHOTO);//
		map.put(TootooeNetApiUrlHelper.UPLOAD_FIRLE_FLAG,
				TootooeNetApiUrlHelper.UPLOAD_FIRLE_FLAG_DEBUG);
		map.put(TootooeNetApiUrlHelper.APPLICATIONID,
				(TootooeNetApiUrlHelper.APPLICATIONID_PARAM));
		map.put(TootooeNetApiUrlHelper.ScenarioType, "");// 2是推荐故事
		map.put(TootooeNetApiUrlHelper.ElementType, "");// 传“”
		String userId = SharedPreferenceHelper
				.getLoginUserId(TootooPlusApplication.getAppContext());
		map.put(TootooeNetApiUrlHelper.USERID, userId);

		FragmentManager fragmentManager = this.getSupportFragmentManager();
		fragment = new UpLoadViewDialogFragment(listFile, handler, map, bundle);
		if (fragmentManager != null) {
			// 屏幕较小，以全屏形式显示
			FragmentTransaction transaction = fragmentManager
					.beginTransaction();
			// 指定一个过渡动画
			transaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			transaction.addToBackStack(null);
			// transaction.attach(newFragment);
			fragment.show(fragmentManager, "dialog");
			// 作为全屏显示,使用“content”作为fragment容器的基本视图,这始终是Activity的基本视图
			// transaction.add(android.R.id.content,
			// newFragment).addToBackStack(null).commit();
		}
	}

	/**
	 * 
	 * @Title: PhotoCameraActivity.java
	 * @Description: 防止某些手机拍照倒立
	 * @author wuyulong
	 * @date 2014-12-19 下午12:40:37
	 * @param
	 * @return void
	 */
	private void cutImage(final String picPath) {
		ImageLoader.getInstance().clearMemoryCache();
		ImageLoader.getInstance().clearDiskCache();
		ImageLoader.getInstance().displayImage("file://" + picPath, mPhoto,
				new SimpleImageLoadingListener() {

					private Bitmap bmp;

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap arg2) {
						int angle = ImageUtil.readPictureDegree(picPath);
						if (angle == 0) {
							bmp = arg2;
						} else {
							// 下面的方法主要作用是把图片转一个角度，也可以放大缩小等
							Matrix m = new Matrix();
							int width = arg2.getWidth();
							int height = arg2.getHeight();
							m.setRotate(angle); // 旋转angle度
							bmp = Bitmap.createBitmap(arg2, 0, 0, width,
									height, m, true);// 从新生成图片
						}
						if (bmp != null) {
							mPhoto.setImageBitmap(bmp);
							finalUpBmp = bmp;
							if (isRecommendConvertView) {
								createStoryId();
							} else if (isCreateView || isEditextView
									|| isRecommendView || isConvertView) {
								uploadImage();

							}

						}

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
					}

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
					}
				});
	}
}