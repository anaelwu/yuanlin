package com.ninetowns.tootoopluse.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.library.util.LogUtil;
import com.ninetowns.library.util.NetworkUtil;
import com.ninetowns.library.util.StringUtils;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.adapter.GridViewApdapter;
import com.ninetowns.tootoopluse.application.TootooPlusEApplication;
import com.ninetowns.tootoopluse.bean.AlbumPhotoBean;
import com.ninetowns.tootoopluse.bean.ConVertBean;
import com.ninetowns.tootoopluse.bean.CreateActivitySecondBean;
import com.ninetowns.tootoopluse.bean.PhotoSelectOrConvertBean;
import com.ninetowns.tootoopluse.bean.SecondStepStoryBean;
import com.ninetowns.tootoopluse.bean.StoryDetailListBean;
import com.ninetowns.tootoopluse.bean.UpLoadFileBean;
import com.ninetowns.tootoopluse.fragment.UpLoadViewDialogFragment;
import com.ninetowns.tootoopluse.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootoopluse.helper.SharedPreferenceHelper;
import com.ninetowns.tootoopluse.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootoopluse.util.AlbumUtil;
import com.ninetowns.tootoopluse.util.CommonUtil;
import com.ninetowns.ui.Activity.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

/****** 选择照片的基类 ******/
public abstract class BaseCreateStoryImageActivity extends BaseActivity
		implements View.OnClickListener {

	private String UpdateStoryId = "0";
	private boolean isEditextView;
	private boolean isCreateView;
	private boolean isDraftView;
	private boolean isConvertView;
	private boolean isRecommendView;
	private TextView comit;
	int itemIndexQ = 0;
	int itemIndexO = 0;
	private View comitLayout;

	// 多选存放图片的map
	private LinkedHashMap<Integer, AlbumPhotoBean> selectMap = new LinkedHashMap<Integer, AlbumPhotoBean>();

	private GridViewApdapter moreGridViewAdapter;
	List<UpLoadFileBean> list;
	public List<AlbumPhotoBean> albumPhotoBeans;

	/********* 此处的handler获得上传图片之后返回的数据 **********/
	private Handler handler = new Handler() {
		private UpLoadFileBean bean;

		@Override
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (fragment != null) {
					fragment.dismiss();
				}
				list = (List<UpLoadFileBean>) msg.obj;
				if (isRecommendConvertView) {// 创建封面图的时候
					setIsRecommendViewData();
				} else if (isCreateView) {// 追加创建的时候
					setIsCreateViewData();
				} else if (isConvertView) {// 创建封面图的时候
					setIsRecommendViewData();
				} else if (isEditextView) {// 编辑的时候
					setIsCreateViewData();
				}
				break;
			}

		}

		/**
		 * 
		 * @Title: BaseCreateStoryImageActivity.java
		 * @Description: 追加创建调用创建故事页接口 ，或者编辑追加调用创建故事页接口
		 * @author wuyulong
		 * @date 2015-1-5 上午9:51:59
		 * @param
		 * @return void
		 */
		private void setIsCreateViewData() {
			StringBuilder yuantu = new StringBuilder();
			StringBuilder rect = new StringBuilder();
			StringBuilder square = new StringBuilder();
			StringBuilder itemType = new StringBuilder();

			StringBuilder rectfourthree = new StringBuilder();

			StringBuilder squareOneToOne = new StringBuilder();
			for (int i = 0; i < list.size(); i++) {
				bean = list.get(i);
				String imageurl = bean.getFileUrl();
				String dragRectangleImg = bean.getDragRectangleImg();
				String drageSquareImage = bean.getDragSquareImg();
				String dragRectangleFourThreeImg = bean
						.getDragRectangleBigImg();
				String squreOneToOneImage = bean.getDragSquareBigImg();

				String defaultType = bean.getDefaultType();
				if (!TextUtils.isEmpty(defaultType)) {// 1、长方形 2:1;
					// 2、小正方形；3、长方形4：3；4、大正方形

					if (defaultType.equals("1")) {
						drageSquareImage = bean.getDragRectangleImg();
					} else if (defaultType.equals("2")) {
						drageSquareImage = bean.getDragSquareImg();

					} else if (defaultType.equals("3")) {
						drageSquareImage = bean.getDragRectangleBigImg();

					} else if (defaultType.equals("4")) {
						drageSquareImage = bean.getDragSquareBigImg();

					}

				} else {
					drageSquareImage = bean.getDragSquareImg();
				}
				if (i != list.size() - 1) {// 不等于最后一个和第一个
					yuantu.append(imageurl).append("@@");
					rect.append(dragRectangleImg).append("@@");
					square.append(drageSquareImage).append("@@");
					itemType.append(defaultType).append("@@");
					rectfourthree.append(dragRectangleFourThreeImg)
							.append("@@");
					squareOneToOne.append(squreOneToOneImage).append("@@");
				} else {
					yuantu.append(imageurl);
					rect.append(dragRectangleImg);
					square.append(drageSquareImage);
					rectfourthree.append(dragRectangleFourThreeImg);
					squareOneToOne.append(squreOneToOneImage);
					itemType.append(defaultType);
				}

			}
			if ((list.size() > 0)) {
				postData(yuantu.toString(), rect.toString(), square.toString(),
						itemType.toString(), rectfourthree.toString(),
						squareOneToOne.toString());
			}

		}

		/**
		 * 
		 * @Title: BaseCreateStoryImageActivity.java
		 * @Description: 创建封面图上传图片，可上传1-5张
		 * @author wuyulong
		 * @date 2015-1-5 上午9:49:02
		 * @param
		 * @return void
		 */
		private void setIsRecommendViewData() {
			int sizelist = list.size();
			if (sizelist > 0) {
				UpLoadFileBean beanfirst = list.get(0);
				if (beanfirst == null) {
					return;
				}
				String imageurlConvert = beanfirst.getFileUrl();
				String convertImage = beanfirst.getListCoverImg();
				String thumb = beanfirst.getThumbFileUrl();
				if (!TextUtils.isEmpty(thumb)) {// 封面图不为null
					createConvert(thumb, imageurlConvert, true); // 当为true的时候就跳转
				} else {
					setDefaultConvert(imageurlConvert, convertImage);
					setPagePost();
				}
			}
		}

		/**
		 * 
		 * @Title: BaseCreateStoryImageActivity.java
		 * @Description: 如果封面图上传失败，默认初始化一条数据
		 * @author wuyulong
		 * @date 2015-1-5 上午9:50:14
		 * @param
		 * @return void
		 */
		private void setDefaultConvert(String imageurl, String convertImage) {
			ConVertBean convertBean = new ConVertBean();
			convertBean.setCoverThumb(convertImage);
			convertBean.setCoverImg(imageurl);
			convertBean.setStoryType("2");
			photoSelectOrConvert.setConvertBean(convertBean);
		}

	};

	public abstract String getCreateConvertType();

	/**
	 * 
	 * @Title: BaseCreateStoryImageActivity.java
	 * @Description: 处理page页的数据并且调用page页的接口
	 * @author wuyulong
	 * @date 2015-1-5 下午1:19:26
	 * @param
	 * @return void
	 */
	private void setPagePost() {
		int size = list.size();
		StringBuilder yuantu = new StringBuilder();
		StringBuilder rect = new StringBuilder();
		StringBuilder square = new StringBuilder();

		StringBuilder defaulttypeStr = new StringBuilder();
		StringBuilder rectfourthree = new StringBuilder();
		StringBuilder squareOneToOne = new StringBuilder();
		for (int i = 0; i < size; i++) {
			UpLoadFileBean localbean = list.get(i);
			String imageurl = localbean.getFileUrl();
			String dragRectangleImg = localbean.getDragRectangleImg();
			String drageSquareImage = localbean.getDragSquareImg();
			String defaultType = localbean.getDefaultType();
			String dragRectangleFourThreeImg = localbean
					.getDragRectangleBigImg();
			String squreOneToOneImage = localbean.getDragSquareBigImg();
			if (i != 0) {// 如果不是第一个数据
				yuantu.append(imageurl).append("@@");
				rect.append(dragRectangleImg).append("@@");
				square.append(drageSquareImage).append("@@");
				defaulttypeStr.append(defaultType).append("@@");
				rectfourthree.append(dragRectangleFourThreeImg).append("@@");
				squareOneToOne.append(squreOneToOneImage).append("@@");
			}
		}
		if (yuantu.length() > 0 && rect.length() > 0 && square.length() > 0) {
			yuantu.delete(yuantu.length() - 2, yuantu.length());
			rect.delete(rect.length() - 2, rect.length());
			square.delete(square.length() - 2, square.length());
			if (defaulttypeStr.length() > 0) {
				defaulttypeStr.delete(defaulttypeStr.length() - 2,
						defaulttypeStr.length());
			}
			if (rectfourthree.length() > 0) {
				rectfourthree.delete(rectfourthree.length() - 2,
						rectfourthree.length());
			}
			if (squareOneToOne.length() > 0) {
				squareOneToOne.delete(squareOneToOne.length() - 2,
						squareOneToOne.length());
			}
			if ((size > 1)) {// 如果大于1才走上传page页接口
				postData(yuantu.toString(), rect.toString(), square.toString(),
						defaulttypeStr.toString(), rectfourthree.toString(),
						squareOneToOne.toString());

			}
		} else {
			skipActivity();
		}
	}

	/**
	 * 
	 * @Title: BaseCreateStoryImageActivity.java
	 * @Description: TODO
	 * @author wuyulong
	 * @date 2014-12-22 下午7:12:58
	 * @param 正方形
	 *            ，原图，是否一个，isOnePage
	 * @return void
	 */
	public void createConvert(final String convert, final String imageUrl,
			final boolean isOnePage) {
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
			showProgressDialog(BaseCreateStoryImageActivity.this);
			RequestParamsNet requestParamsNet = new RequestParamsNet();
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYID, storyid);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.COVER_THUMB, convert);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.COVER_IMG, imageUrl);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORY_TYPE, getCreateConvertType());// 拍摄相片
			CommonUtil.xUtilsPostSend(TootooeNetApiUrlHelper.STORY_NAME_UPDATE,
					requestParamsNet, new RequestCallBack<String>() {

						private String status;

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							closeProgressDialog(BaseCreateStoryImageActivity.this);
							String jsonStr = new String(responseInfo.result);
							try {
								JSONObject jsobj = new JSONObject(jsonStr);
								if (jsobj.has("Status")) {
									status = jsobj.getString("Status");
									if (status.equals("1")) {
										if (isConvertView) {
											if (mConvertBean != null) {
												mConvertBean
														.setCoverThumb(convert);
												mConvertBean
														.setCoverImg(imageUrl);
												mConvertBean.setStoryType("2");
												if (isActivitySecondView) {
													mConvertBean
															.setStoryId(storyBean
																	.getConvertBean()
																	.getStoryId());
													storyBean
															.setConvertBean(mConvertBean);
												} else {
													isCreateViewPhotoConvertBean
															.setConvertBean(mConvertBean);
												}

											}

										} else {
											ConVertBean convertBean = new ConVertBean();
											convertBean.setCoverThumb(convert);
											convertBean.setCoverImg(imageUrl);
											convertBean.setStoryType("2");
											if (!TextUtils.isEmpty(storyid))
												convertBean.setStoryId(storyid);
											photoSelectOrConvert
													.setConvertBean(convertBean);
											photoSelectOrConvert
													.setListBean(detailList);

										}
										setPagePost();
									}

								}

							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							closeProgressDialog(BaseCreateStoryImageActivity.this);
							ComponentUtil
									.showToast(
											BaseCreateStoryImageActivity.this,
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
	 * @Title: BaseCreateStoryImageActivity.java
	 * @Description: 创建故事页的类型 1.文字 2.图片 3.视频
	 * @author wuyulong
	 * @date 2014-12-17 上午9:40:43
	 * @param
	 * @return String
	 */
	public abstract String getPageType();

	/**
	 * 
	 * @Title: AlbumPhotoActivity.java
	 * @Description: 子类实现
	 * @author wuyulong
	 * @date 2014-12-15 下午2:32:44
	 * @param 原图
	 *            ，长方形 正方形
	 * @return void
	 */
	private int indey;

	/**
	 * 
	 * @Title: BaseCreateStoryImageActivity.java
	 * @Description: 调用上传故事页的接口
	 * @author wuyulong
	 * @date 2015-1-5 上午9:45:25
	 * @param yuantu
	 *            原图，rectimage长方形图，squareimage正方形图
	 * @return void
	 */
	public void postData(final String yuantu, final String rectimage,
			final String squareimage, final String defaultType,
			final String fourthree, final String onetoone) {

		if ((NetworkUtil.isNetworkAvaliable(this))) {
			// 显示进度
			showProgressDialog(BaseCreateStoryImageActivity.this);
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

						private String PageId;
						private int itemIndewx;
						private StoryDetailListBean heightItem;

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							closeProgressDialog(BaseCreateStoryImageActivity.this);
							String strResult = responseInfo.result;
							if (!TextUtils.isEmpty(strResult)) {
								try {
									JSONObject jsobj = new JSONObject(strResult);
									if (jsobj.has("Status")) {
										jsobj.getString("Status");

									}
									if (jsobj.has("Data")) {
										String st = jsobj.getString("Data");
										JSONObject storyjsonobj = new JSONObject(
												st);
										if (storyjsonobj.has("PageId")) {
											PageId = storyjsonobj
													.getString("PageId");
											if (!StringUtils.isEmpty(PageId)) {
												if (isRecommendConvertView) {
													isRecommendViewInitAdapterData(
															yuantu, rectimage,
															squareimage,
															PageId,
															defaultType,
															fourthree, onetoone);
												} else {
													setCreateListBean(yuantu,
															rectimage,
															squareimage,
															defaultType,
															fourthree, onetoone);

												}
											} else {
												LogUtil.error(
														"TextContentStoryCreateFragment",
														"pageid是null");
											}

										}
									}

								} catch (JSONException e) {
									LogUtil.error("CreateStoryFragment",
											e.toString());
									e.printStackTrace();
								}
							}

						}

						private void setCreateListBean(final String yuantu,
								final String rectimage,
								final String squareimage,
								final String defaultType,
								final String fourthree, final String onetoone) {
							String arr[] = yuantu.split("@@");
							String rect[] = rectimage.split("@@");
							String square[] = squareimage.split("@@");
							String pageId[] = PageId.split(",");
							String fourtheearr[] = fourthree.split("@@");
							String onetonoearr[] = onetoone.split("@@");
							String arrDefaultType[] = defaultType.split("@@");

							if (mHashMap != null) {
								int insertPosition = mHashMap
										.get(ConstantsTooTooEHelper.POSITION_KEY);
								if (insertPosition == ConstantsTooTooEHelper.NO_ITEM_CLICK_INSERT_CODE) {
									insertBottom(arr, rect, square,
											fourtheearr, onetonoearr,
											arrDefaultType, pageId);
								} else {
									insertPosition(arr, rect, square,
											fourtheearr, onetonoearr,
											arrDefaultType, pageId,
											insertPosition);
								}

							} else {
								insertBottom(arr, rect, square, fourtheearr,
										onetonoearr, arrDefaultType, pageId);
							}

							LogUtil.systemlogInfo("PageId", PageId);
							if (isActivitySecondView) {
								storyBean.setWishDetailBean(isCreateListBean);
							} else {
								isCreateViewPhotoConvertBean
										.setListBean(isCreateListBean);
							}

							skipActivity();
						}

						/**
						 * @Title: insertBottom
						 * @Description: TODO
						 * @param
						 * @return
						 * @throws
						 */
						private void insertBottom(String[] arr, String[] rect,
								String[] square, String[] pageId,
								String[] fourtheearr, String[] onetonoearr,
								String[] arrDefaultType) {
							int listSize = isCreateListBean.size();
							for (int i = 0; i < listSize; i++) {
								if (isCreateListBean.get(i).getItemIndex() >= isCreateListBean
										.get(indey).getItemIndex()) {
									indey = i;
								}
							}
							if (listSize > 0) {
								heightItem = isCreateListBean.get(indey);
								itemIndewx = heightItem.getItemIndex();
								// heightItem =
								// isCreateListBean.get(itemIndewx);
							}
							for (int i = 0; i < pageId.length; i++) {
								String rectst = "";
								String squarest = "";
								String defaultTypeitem = "";
								@SuppressWarnings("unused")
								String fourToThree = "";
								String oneToOne = "";
								if (rect != null) {
									rectst = rect[i];
									LogUtil.systemlogInfo("rectstiamge", rectst);
								}
								if (square != null) {
									squarest = square[i];
									LogUtil.systemlogInfo("squarestiamge",
											squarest);
								}
								if (arrDefaultType != null) {
									defaultTypeitem = arrDefaultType[i];
									LogUtil.systemlogInfo("squarestiamge",
											defaultTypeitem);
								}
								if (fourtheearr != null) {
									fourToThree = fourtheearr[i];
								}
								if (onetonoearr != null) {
									oneToOne = onetonoearr[i];

								}
								StoryDetailListBean storyDetailListBean = new StoryDetailListBean();
								storyDetailListBean.setPageType("2");
								storyDetailListBean.setPageId(pageId[i]);
								storyDetailListBean.setPageImg(arr[i]);
								storyDetailListBean
										.setDefaultType(defaultTypeitem);
								storyDetailListBean
										.setPageImgThumbBigRectangle(fourToThree);
								storyDetailListBean
										.setPageImgThumbBigSquare(oneToOne);
								storyDetailListBean.setDragRectangleImg(rectst);
								storyDetailListBean.setDragSquareImg(squarest);
								if (!TextUtils.isEmpty(defaultTypeitem)) {
									int elementType = Integer
											.valueOf(defaultTypeitem);
									storyDetailListBean
											.setElementType(elementType);

								} else {
									storyDetailListBean.setElementType(2);
								}
								// 找到index

								int itemIndexQ = itemIndewx;
								int itemIndexO = itemIndewx;
								if (listSize == 0) {
									storyDetailListBean.setLocation(1);
									storyDetailListBean.setItemIndex(0);
									if (!TextUtils.isEmpty(defaultTypeitem)) {
										int elementType = Integer
												.valueOf(defaultTypeitem);
										storyDetailListBean
												.setElementType(elementType);

									} else {
										storyDetailListBean.setElementType(2);
									}
								} else {

									if (heightItem != null) {
										int location = heightItem.getLocation();
										int elementType = heightItem
												.getElementType();
										int element = Integer
												.valueOf(defaultTypeitem);
										if (location == 1) {//
											if (elementType == 2) {
												storyDetailListBean
														.setLocation(2);
											} else {
												storyDetailListBean
														.setLocation(1);
											}
											if (!TextUtils
													.isEmpty(defaultTypeitem)) {

												storyDetailListBean
														.setElementType(element);

											} else {
												storyDetailListBean
														.setElementType(2);
											}

											storyDetailListBean
													.setItemIndex(itemIndexO);
										} else {

											if (!TextUtils
													.isEmpty(defaultTypeitem)) {

												storyDetailListBean
														.setElementType(element);

											} else {
												storyDetailListBean
														.setElementType(2);
											}
											storyDetailListBean.setLocation(1);
											storyDetailListBean
													.setItemIndex(itemIndexO + 1);
										}
									}

								}

								isCreateListBean.add(storyDetailListBean);
							}
						}

						private void insertPosition(String[] arr,
								String[] rect, String[] square,
								String[] fourtheearr, String[] onetonoearr,
								String[] arrDefaultType, String[] pageId,
								int insertPosition) {
							int listSize = isCreateListBean.size();

							if (listSize > 0) {
								heightItem = isCreateListBean
										.get(insertPosition);
								itemIndewx = heightItem.getItemIndex();
								// heightItem =
								// isCreateListBean.get(itemIndewx);
							}
							for (int i = 0; i < pageId.length; i++) {
								String rectst = "";
								String squarest = "";
								String defaultTypeitem = "";
								@SuppressWarnings("unused")
								String fourToThree = "";
								String oneToOne = "";

								if (rect != null) {
									rectst = rect[i];
									LogUtil.systemlogInfo("rectstiamge", rectst);
								}
								if (square != null) {
									squarest = square[i];
									LogUtil.systemlogInfo("squarestiamge",
											squarest);
								}
								if (arrDefaultType != null) {
									defaultTypeitem = arrDefaultType[i];
									LogUtil.systemlogInfo("squarestiamge",
											defaultTypeitem);
								}
								if (fourtheearr != null) {
									fourToThree = fourtheearr[i];
								}
								if (onetonoearr != null) {
									oneToOne = onetonoearr[i];

								}
								StoryDetailListBean storyDetailListBean = new StoryDetailListBean();
								storyDetailListBean.setPageType("2");
								storyDetailListBean.setPageId(pageId[i]);
								storyDetailListBean.setPageImg(arr[i]);
								storyDetailListBean
										.setPageImgThumbBigRectangle(fourToThree);
								storyDetailListBean
										.setPageImgThumbBigSquare(oneToOne);
								storyDetailListBean.setDragRectangleImg(rectst);
								storyDetailListBean.setDragSquareImg(squarest);
								if (!TextUtils.isEmpty(defaultTypeitem)
										&& !defaultTypeitem.equals("null")) {
									int elementType = Integer
											.valueOf(defaultTypeitem);
									storyDetailListBean
											.setElementType(elementType);

								} else {
									storyDetailListBean.setElementType(2);
								}

								// 找到index

								int itemIndexO = itemIndewx;
								if (listSize == 0) {
									storyDetailListBean.setLocation(1);
									storyDetailListBean.setItemIndex(0);
									if (!TextUtils.isEmpty(defaultTypeitem)
											&& !defaultTypeitem.equals("null")) {// 没办法后台竟然有时候会返回null的字符串
										int elementType = Integer
												.valueOf(defaultTypeitem);
										storyDetailListBean
												.setElementType(elementType);

									} else {
										storyDetailListBean.setElementType(2);
									}
								} else {

									if (heightItem != null) {
										int location = heightItem.getLocation();
										int elementType = heightItem
												.getElementType();

										if (location == 1) {//
											if (elementType == 2) {
												storyDetailListBean
														.setLocation(2);
											} else {
												storyDetailListBean
														.setLocation(1);
											}
											if (!TextUtils
													.isEmpty(defaultTypeitem)
													&& !defaultTypeitem
															.equals("null")) {
												int element = Integer
														.valueOf(defaultTypeitem);
												storyDetailListBean
														.setElementType(element);

											} else {
												storyDetailListBean
														.setElementType(2);
											}

											storyDetailListBean
													.setItemIndex(itemIndexO);
										} else {

											if (!TextUtils
													.isEmpty(defaultTypeitem)) {
												int element = Integer
														.valueOf(defaultTypeitem);
												storyDetailListBean
														.setElementType(element);

											} else {
												storyDetailListBean
														.setElementType(2);
											}
											storyDetailListBean.setLocation(1);
											storyDetailListBean
													.setItemIndex(itemIndexO + 1);
										}
									}

								}
								LogUtil.error("图片地址" + insertPosition + 1 + i,
										storyDetailListBean.getPageImg());
								isCreateListBean.add(insertPosition + 1 + i,
										storyDetailListBean);
							}
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							closeProgressDialog(BaseCreateStoryImageActivity.this);
							ComponentUtil
									.showToast(
											BaseCreateStoryImageActivity.this,
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
	 * @Title: BaseCreateStoryImageActivity.java
	 * @Description: 初始化拖拽适配器adapter的数据源
	 * @author wuyulong
	 * @date 2015-1-5 上午9:42:47
	 * @param
	 * @return void
	 */
	private void isRecommendViewInitAdapterData(final String yuantu,
			final String rectimage, final String squareImage, String strPageId,
			String defaultType, String fourthree, String oneToone) {
		String arr[] = yuantu.split("@@");
		String rect[] = rectimage.split("@@");
		String square[] = squareImage.split("@@");
		String pageId[] = strPageId.split(",");
		String typearr[] = defaultType.split("@@");
		String fourtheearr[] = fourthree.split("@@");
		String onetonoearr[] = oneToone.split("@@");
		for (int i = 0; i < pageId.length; i++) {
			StoryDetailListBean storyDetailListBean = new StoryDetailListBean();
			String convert = "";
			String rectst = "";
			String squarest = "";
			String fourToThree = "";
			String oneToOne = "";
			String defaultTypeitem = "";
			if (rect != null) {
				rectst = rect[i];
				LogUtil.systemlogInfo("rectstiamge", rectst);
			}
			if (square != null) {
				squarest = square[i];
				LogUtil.systemlogInfo("squarestiamge", squarest);
			}
			if (fourtheearr != null) {
				fourToThree = fourtheearr[i];
			}
			if (onetonoearr != null) {
				oneToOne = onetonoearr[i];

			}
			if (typearr != null) {
				defaultTypeitem = typearr[i];

			}
			storyDetailListBean.setPageType("2");
			storyDetailListBean.setPageId(pageId[i]);
			storyDetailListBean.setPageImg(arr[i]);
			storyDetailListBean.setDragRectangleImg(rectst);
			storyDetailListBean.setDragSquareImg(squarest);
			storyDetailListBean.setPageImgThumbBigRectangle(fourToThree);
			storyDetailListBean.setPageImgThumbBigSquare(oneToOne);
			storyDetailListBean.setDefaultType(defaultTypeitem);
			if (!TextUtils.isEmpty(defaultTypeitem)) {
				int elementType = Integer.valueOf(defaultTypeitem);
				storyDetailListBean.setElementType(elementType);// 正方形小正方形

			} else {
				storyDetailListBean.setElementType(2);// 正方形小正方形
			}
			if (defaultTypeitem.equals("2")) {
				if ((i & 1) != 0) {// 是奇数
					storyDetailListBean.setItemIndex(itemIndexQ);
					itemIndexQ++;
					storyDetailListBean.setLocation(2);
				} else {// 偶数
					storyDetailListBean.setItemIndex(itemIndexO);
					itemIndexO++;
					storyDetailListBean.setLocation(1);
				}
				detailList.add(storyDetailListBean);
			} else {
				storyDetailListBean.setItemIndex(itemIndexQ);
				itemIndexQ++;
				storyDetailListBean.setLocation(1);// 长方形也是1
				detailList.add(storyDetailListBean);
			}

		}

		photoSelectOrConvert.setListBean(detailList);
		// dd
		skipActivity();
	}

	/**
	 * 
	 * @Title: AlbumPhotoActivity.java
	 * @Description: 子类实现初始化数据适配器
	 * @author wuyulong
	 * @date 2014-12-15 下午12:37:43
	 * @param
	 * @return GridViewApdapter
	 */
	public abstract GridViewApdapter initAdapter(Context context,
			List<AlbumPhotoBean> list,
			LinkedHashMap<Integer, AlbumPhotoBean> selectMap);

	/**
	 * 
	 * @Title: AlbumPhotoActivity.java
	 * @Description: 子类中实现
	 * @author wuyulong
	 * @date 2014-12-15 下午12:48:22
	 * @param
	 * @return void
	 */
	public void skipActivity() {

		if (isEditextView) {
			Intent intent = new Intent(this, CreateWishActivity.class);
			bundle.putString("UpdateStoryId", UpdateStoryId);
			bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
					isCreateViewPhotoConvertBean);
			ConstantsTooTooEHelper.putView(
					ConstantsTooTooEHelper.isRecommendView, bundle);
			bundle.putString("storyid", storyid);
			intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

		} else if (isConvertView) {

			if (isActivitySecondView) {
				Intent intent = new Intent(this,
						CreateActSecondStepActivity.class);
				bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
						secondStepStoryBean);
				bundle.putString("UpdateStoryId", UpdateStoryId);
				bundle.putString("storyid", storyid);
				bundle.putInt("currentPosition", currentPosition);
				intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

			} else {
				Intent intent = new Intent(this, CreateWishActivity.class);
				bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
						isCreateViewPhotoConvertBean);
				ConstantsTooTooEHelper.putView(
						ConstantsTooTooEHelper.isConvertRecommendView, bundle);
				bundle.putString("storyid", storyid);
				intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}

		} else if (isDraftView) {
			Intent intent = new Intent(this, CreateWishActivity.class);
			bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
					isCreateViewPhotoConvertBean);
			ConstantsTooTooEHelper.putView(
					ConstantsTooTooEHelper.isConvertRecommendView, bundle);
			bundle.putString("storyid", storyid);
			intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else if (isCreateView) {

			if (isActivitySecondView) {
				Intent intent = new Intent(this,
						CreateActSecondStepActivity.class);
				bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
						secondStepStoryBean);
				bundle.putString("UpdateStoryId", UpdateStoryId);
				bundle.putString("storyid", storyid);
				bundle.putInt("currentPosition", currentPosition);
				intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

			} else {
				Intent intent = new Intent(this, CreateWishActivity.class);
				bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
						isCreateViewPhotoConvertBean);
				ConstantsTooTooEHelper.putView(
						ConstantsTooTooEHelper.isConvertRecommendView, bundle);
				bundle.putString("UpdateStoryId", UpdateStoryId);
				bundle.putString("storyid", storyid);
				intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}

		} else if (isRecommendView) {
			Intent intent = new Intent(this, CreateWishActivity.class);
			bundle.putString("UpdateStoryId", UpdateStoryId);
			bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
					isCreateViewPhotoConvertBean);
			ConstantsTooTooEHelper.putView(
					ConstantsTooTooEHelper.isRecommendView, bundle);
			bundle.putString("storyid", storyid);
			intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		} else if (isRecommendConvertView) {
			Intent intent = new Intent(this, CreateWishActivity.class);
			bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
					photoSelectOrConvert);
			bundle.putString("storyid", storyid);
			intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}

		finish();

	}

	private Bundle bundle;

	private boolean isRecommendConvertView;

	private PhotoSelectOrConvertBean photoSelectOrConvert;

	private List<StoryDetailListBean> detailList;

	private PhotoSelectOrConvertBean isCreateViewPhotoConvertBean;

	private List<StoryDetailListBean> isCreateListBean;

	private UpLoadViewDialogFragment fragment;
	/****** public 的方式方便显示网络采集图册 ******/
	public TextView two_or_one_btn_head_title;
	private ConVertBean mConvertBean;
	private int currentPosition;
	private SecondStepStoryBean secondStepStoryBean;
	private boolean isActivitySecondView;
	private CreateActivitySecondBean storyBean;
	private HashMap<String, Integer> mHashMap;

	@SuppressWarnings("rawtypes")
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.album_photo);
		photoSelectOrConvert = new PhotoSelectOrConvertBean();
		detailList = new LinkedList<StoryDetailListBean>();
		getType();
		bundle = this.getIntent().getBundleExtra(ConstantsTooTooEHelper.BUNDLE);
		isEditextView = bundle.getBoolean(ConstantsTooTooEHelper.isEditextView);
		isCreateView = bundle.getBoolean(ConstantsTooTooEHelper.isCreateView);
		isDraftView = bundle.getBoolean(ConstantsTooTooEHelper.isDraftView);
		isConvertView = bundle.getBoolean(ConstantsTooTooEHelper.isConvertView);
		isRecommendView = bundle
				.getBoolean(ConstantsTooTooEHelper.isRecommendView);
		isRecommendConvertView = bundle
				.getBoolean(ConstantsTooTooEHelper.isConvertRecommendView);
		if (isEditextView) {// 编辑显示
			// upd
			UpdateStoryId = bundle.getString("UpdateStoryId");
			isCreateViewPhotoConvertBean = (PhotoSelectOrConvertBean) bundle
					.getSerializable(ConstantsTooTooEHelper.BundleResopnse);
			mHashMap = isCreateViewPhotoConvertBean.getmHashMap();
			storyid = bundle.getString("storyid");
			isCreateListBean = isCreateViewPhotoConvertBean.getListBean();
		} else if (isCreateView) {

			storyid = bundle.getString("storyid");
			UpdateStoryId = bundle.getString("UpdateStoryId");
			if (!TextUtils.isEmpty(UpdateStoryId) && UpdateStoryId.equals("-1")) {// 故事第二步
				isActivitySecondView = true;
				currentPosition = bundle.getInt("currentPosition");
				secondStepStoryBean = (SecondStepStoryBean) bundle
						.getSerializable(ConstantsTooTooEHelper.BundleResopnse);
				mHashMap=secondStepStoryBean.getmHashMap();
				storyBean = secondStepStoryBean.getStoryList().get(
						currentPosition);
				isCreateListBean = storyBean.getWishDetailBean();
			} else {
				isCreateViewPhotoConvertBean = (PhotoSelectOrConvertBean) bundle
						.getSerializable(ConstantsTooTooEHelper.BundleResopnse);
				isCreateListBean = isCreateViewPhotoConvertBean.getListBean();
				mHashMap = isCreateViewPhotoConvertBean.getmHashMap();
			}

		} else if (isDraftView) {

		} else if (isConvertView) {
			storyid = bundle.getString("storyid");
			UpdateStoryId = bundle.getString("UpdateStoryId");
			if (!TextUtils.isEmpty(UpdateStoryId) && UpdateStoryId.equals("-1")) {// 故事第二步
				isActivitySecondView = true;
				currentPosition = bundle.getInt("currentPosition");
				secondStepStoryBean = (SecondStepStoryBean) bundle
						.getSerializable(ConstantsTooTooEHelper.BundleResopnse);
				mHashMap=secondStepStoryBean.getmHashMap();
				storyBean = secondStepStoryBean.getStoryList().get(
						currentPosition);
				isCreateListBean = storyBean.getWishDetailBean();
				mConvertBean = storyBean.getConvertBean();
			} else {
				isCreateViewPhotoConvertBean = (PhotoSelectOrConvertBean) bundle
						.getSerializable(ConstantsTooTooEHelper.BundleResopnse);
				mHashMap = isCreateViewPhotoConvertBean.getmHashMap();
				storyid = bundle.getString("storyid");
				isCreateListBean = isCreateViewPhotoConvertBean.getListBean();
				mConvertBean = isCreateViewPhotoConvertBean.getConvertBean();

			}

		} else if (isRecommendView) {// 编辑追加

			UpdateStoryId = bundle.getString("UpdateStoryId");
			isCreateViewPhotoConvertBean = (PhotoSelectOrConvertBean) bundle
					.getSerializable(ConstantsTooTooEHelper.BundleResopnse);
			mHashMap = isCreateViewPhotoConvertBean.getmHashMap();
			storyid = bundle.getString("storyid");
			isCreateListBean = isCreateViewPhotoConvertBean.getListBean();

		} else if (isRecommendConvertView) {
			createStoryId();
		}

		setVisibleCareraOrSele();
		// 返回按钮
		LinearLayout two_or_one_btn_head_back = (LinearLayout) findViewById(R.id.two_or_one_btn_head_back);
		two_or_one_btn_head_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 标题
		two_or_one_btn_head_title = (TextView) findViewById(R.id.two_or_one_btn_head_title);
		two_or_one_btn_head_title.setText(R.string.album_photo_title);
		comit = (TextView) findViewById(R.id.two_or_one_btn_head_second_tv);
		comit.setVisibility(View.VISIBLE);
		comitLayout = findViewById(R.id.two_or_one_btn_head_second_layout);
		comitLayout.setOnClickListener(this);
		// 默认情况下显示
		comit.setText(getResources().getString(
				R.string.mobile_forget_pwd_success_btn)
				+ "(0/" + ConstantsTooTooEHelper.MAX_UPLOAD_PHOTO + ")");

		GridView album_photo_gv = (GridView) findViewById(R.id.album_photo_gv);
		String folder_name = getIntent().getStringExtra("folder_name");// 这是先获取的文件夹
		if (!StringUtils.isEmpty(folder_name)) {
			albumPhotoBeans = AlbumUtil.albumPhotoInfo(this, folder_name);
			LogUtil.systemlogInfo("AlbumPhotoActivity+++>", albumPhotoBeans);
		}
		moreGridViewAdapter = initAdapter(this, albumPhotoBeans, selectMap);// 子类必须实现
		if (moreGridViewAdapter != null) {
			album_photo_gv.setAdapter(moreGridViewAdapter);
		} // 暂时先酱紫

		album_photo_gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onItemSelectPhoto(position, comit, view);

			}

		});

	}

	/**
	 * 
	 * @Title: AlbumPhotoActivity.java
	 * @Description: 子类去实现
	 * @author wuyulong
	 * @date 2014-12-16 下午3:08:57
	 * @param
	 * @return void
	 */
	public void setVisibleCareraOrSele() {

	}

	/**
	 * 
	 * @Title: BaseCreateStoryImageActivity.java
	 * @Description: 图片选择
	 * @author wuyulong
	 * @date 2015-1-4 下午3:37:20
	 * @param position
	 *            位置 comit 显示选择几个图片的textview
	 * @return void
	 */
	public abstract void onItemSelectPhoto(int position, TextView comit,
			View view);

	/**
	 * 
	 * @Title: AlbumPhotoActivity.java
	 * @Description: 子类可复写
	 * @author wuyulong
	 * @date 2014-12-15 上午11:29:58
	 * @param
	 * @return void
	 */
	public abstract void getType();

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.two_or_one_btn_head_second_layout:
			if (detailList != null && detailList.size() > 0) {
				detailList.clear();
			}
			List<String> temp_imgs = new ArrayList<String>();
			Iterator<Entry<Integer, AlbumPhotoBean>> iter = moreGridViewAdapter.selectMap
					.entrySet().iterator();
			while (iter.hasNext()) {// linkHashMap是有序的
				@SuppressWarnings("rawtypes")
				LinkedHashMap.Entry entry = (LinkedHashMap.Entry) iter.next(); // linkedhashmap是有序的，防止选择图片的时候第一个不是封面图
				LogUtil.systemlogInfo("position", entry.getKey().toString());
				String photoPath = ((AlbumPhotoBean) entry.getValue())
						.getAlbum_photo_path();
				temp_imgs.add(photoPath);

			}
			LogUtil.systemlogInfo("++++hc+++selectPhoto++++>", temp_imgs);
			if (temp_imgs.size() > 0) {
				uploadImage(temp_imgs);
			}

			Toast.makeText(getApplication(), "选择了" + temp_imgs.size() + "张图片",
					Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @Title: BaseCreateStoryImageActivity.java
	 * @Description: 上传图片 ，由UpLoadViewDialogFragment 管理上传业务
	 * @author wuyulong
	 * @date 2015-1-4 下午3:26:39
	 * @param temp_imgs
	 *            文件图片的地址集合
	 * @return void
	 */
	private void uploadImage(List<String> temp_imgs) {
		List<File> listFile = new ArrayList<File>();
		for (int i = 0; i < temp_imgs.size(); i++) {
			String photoPath = temp_imgs.get(i);
			if (photoPath != null && photoPath.length() > 4) {
				String subStr = photoPath.substring(0, 4);//
				if (subStr.contains("http")) {// 前四个是http
					@SuppressWarnings("deprecation")
					String pathCath = ImageLoader.getInstance().getDiscCache()
							.get(photoPath).getPath();
					if (!TextUtils.isEmpty(pathCath)) {
						File upFile = new File(pathCath);
						listFile.add(upFile);
					}

				} else {
					File upFile = new File(photoPath);
					listFile.add(upFile);
				}

			}

		}
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(TootooeNetApiUrlHelper.UPLOAD_FIRLE_TYPE,
				TootooeNetApiUrlHelper.UPLOAD_FIRLE_TYPE_PHOTO);//
		map.put(TootooeNetApiUrlHelper.UPLOAD_FIRLE_FLAG,
				TootooeNetApiUrlHelper.UPLOAD_FIRLE_FLAG_DEBUG);
		map.put(TootooeNetApiUrlHelper.APPLICATIONID,
				(TootooeNetApiUrlHelper.APPLICATIONID_PARAM));
		map.put(TootooeNetApiUrlHelper.ScenarioType, "");// 这个地方先传一个key 在上传的时候赋值
		map.put(TootooeNetApiUrlHelper.ElementType, "");// 传“”
		String useridlocal = SharedPreferenceHelper
				.getLoginUserId(TootooPlusEApplication.getAppContext());
		if (!StringUtils.isEmpty(useridlocal)) {
			map.put(TootooeNetApiUrlHelper.USERID, useridlocal);
		} else {
			ComponentUtil.showToast(getApplicationContext(), "未登陆");
		}
		FragmentManager fragmentManager = getSupportFragmentManager();
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

	private String storyid;

	/**
	 * 
	 * @Title: BaseCreateStoryImageActivity.java
	 * @Description: 创建故事的id,如果没有故事的时候
	 * @author wuyulong
	 * @date 2015-1-4 下午3:28:13
	 * @param
	 * @return void
	 */
	public void createStoryId() {
		// 创建封面图
		if ((NetworkUtil.isNetworkAvaliable(this))) {
			// 显示进度
			showProgressDialog(BaseCreateStoryImageActivity.this);
			RequestParamsNet requestParamsNet = new RequestParamsNet();
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.TYPE, "0");
			String userId = SharedPreferenceHelper
					.getLoginUserId(TootooPlusEApplication.getAppContext());
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.USERID, userId);
			CommonUtil.xUtilsPostSend(TootooeNetApiUrlHelper.STORYCREATEAPI,
					requestParamsNet, new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							closeProgressDialog(BaseCreateStoryImageActivity.this);
							String jsonStr = new String(responseInfo.result);

							try {
								JSONObject jsobj = new JSONObject(jsonStr);
								if (jsobj.has("Status")) {
									String status = jsobj.getString("Status");
									if (status.equals("1250")) {//
										ComponentUtil
												.showToast(
														BaseCreateStoryImageActivity.this,
														getResources()
																.getString(
																		R.string.no_permissions));
									} else if (status.equals("1221")) {
										ComponentUtil
												.showToast(
														BaseCreateStoryImageActivity.this,
														getResources()
																.getString(
																		R.string.return_data_error));
									} else {
										LogUtil.systemlogError("创建故事失败status=",
												status);
									}

								}
								if (jsobj.has("Data")) {
									String st = jsobj.getString("Data");
									JSONObject storyjsonobj = new JSONObject(st);
									if (storyjsonobj.has("StoryId")) {
										storyid = storyjsonobj
												.getString("StoryId");
									}
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							closeProgressDialog(BaseCreateStoryImageActivity.this);
							ComponentUtil
									.showToast(
											BaseCreateStoryImageActivity.this,
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

}