package com.ninetowns.tootoopluse.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.library.util.ImageUtil;
import com.ninetowns.library.util.LogUtil;
import com.ninetowns.library.util.NetworkUtil;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.activity.CreateActSecondStepActivity;
import com.ninetowns.tootoopluse.activity.CreateWishActivity;
import com.ninetowns.tootoopluse.application.TootooPlusEApplication;
import com.ninetowns.tootoopluse.bean.ConVertBean;
import com.ninetowns.tootoopluse.bean.CreateActivitySecondBean;
import com.ninetowns.tootoopluse.bean.PhotoSelectOrConvertBean;
import com.ninetowns.tootoopluse.bean.SecondStepStoryBean;
import com.ninetowns.tootoopluse.bean.StoryDetailListBean;
import com.ninetowns.tootoopluse.bean.UpLoadFileBean;
import com.ninetowns.tootoopluse.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootoopluse.helper.SharedPreferenceHelper;
import com.ninetowns.tootoopluse.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootoopluse.util.CommonUtil;

/**
 * 
 * @Title: VideoRecommendStoryCreateFragment.java
 * @Description: 创建推荐故事视频类型的界面
 * @author wuyulong
 * @date 2015-1-8 上午11:22:34
 * @version V1.0
 */
public class VideoRecommendStoryCreateFragment extends VideoStoryCreateFragment
		implements View.OnClickListener {
	private boolean isEditextView;
	private boolean isCreateView;
	private boolean isDraftView;
	private boolean isConvertView;
	private boolean isRecommendView;
	private boolean isRecommendConvertView;
	private UpLoadFileBean bean;
	private PhotoSelectOrConvertBean isCreateViewPhotoConvertBean;
	private List<StoryDetailListBean> isCreateListBean;
	private String fileFurl;
	private Bundle bundle;
	private String storyid;
	private int indey;
	private PhotoSelectOrConvertBean photoSelectOrConvert;
	private List<StoryDetailListBean> detailList;
	private Lock lock = new ReentrantLock();
	private Handler handler = new Handler() {
		private UpLoadFileBean bean;

		@Override
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case TootooeNetApiUrlHelper.UPLOAD_VIDEO:
				if (fragment != null) {
					fragment.dismiss();
				}

				bean = (UpLoadFileBean) msg.obj;
				setIsRecommendViewData(bean);
				break;
			}

		}

		private void setIsRecommendViewData(UpLoadFileBean bean) {

			String thumb = bean.getThumbFileUrl();
			String fileurl = bean.getFileUrl();
			String rect = bean.getDragRectangleImg();
			String square = bean.getDragSquareImg();
			String convert = bean.getListCoverImg();
			String videoImageYuantu = bean.getImageFileUrl();
			if (isRecommendConvertView) {
				createConvert(fileurl, convert, thumb);
			} else if (isCreateView || isRecommendView || isEditextView) {
				postData(fileurl, videoImageYuantu, thumb, rect, square);
			} else if (isConvertView) {
				createConvert(fileurl, convert, thumb);
			}

		}

	};
	private String UpdateStoryId;
	private boolean isActivitySecondView;
	private int currentPosition;
	private SecondStepStoryBean secondStepStoryBean;
	private CreateActivitySecondBean storyBean;
	private ConVertBean mConvertBean;

	@Override
	public void getType(Bundle bundle) {
		this.bundle = bundle;
		photoSelectOrConvert = new PhotoSelectOrConvertBean();
		detailList = new LinkedList<StoryDetailListBean>();
		this.bundle = bundle;
		isEditextView = bundle.getBoolean(ConstantsTooTooEHelper.isEditextView);
		isCreateView = bundle.getBoolean(ConstantsTooTooEHelper.isCreateView);
		isDraftView = bundle.getBoolean(ConstantsTooTooEHelper.isDraftView);
		isConvertView = bundle.getBoolean(ConstantsTooTooEHelper.isConvertView);
		isRecommendView = bundle
				.getBoolean(ConstantsTooTooEHelper.isRecommendView);
		isRecommendConvertView = bundle
				.getBoolean(ConstantsTooTooEHelper.isConvertRecommendView);
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
				storyid = bundle.getString("storyid");
				isCreateViewPhotoConvertBean = (PhotoSelectOrConvertBean) bundle
						.getSerializable(ConstantsTooTooEHelper.BundleResopnse);
				isCreateListBean = isCreateViewPhotoConvertBean.getListBean();
			}

		} else if (isRecommendConvertView) {

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
				storyid = bundle.getString("storyid");
				isCreateViewPhotoConvertBean = (PhotoSelectOrConvertBean) bundle
						.getSerializable(ConstantsTooTooEHelper.BundleResopnse);
				isCreateListBean = isCreateViewPhotoConvertBean.getListBean();
			}

		} else if (isRecommendView) {
			UpdateStoryId = bundle.getString("UpdateStoryId");
			isCreateViewPhotoConvertBean = (PhotoSelectOrConvertBean) bundle
					.getSerializable(ConstantsTooTooEHelper.BundleResopnse);
			storyid = bundle.getString("storyid");
			isCreateListBean = isCreateViewPhotoConvertBean.getListBean();

		} else if (isEditextView) {
			// upd
			UpdateStoryId = bundle.getString("UpdateStoryId");
			isCreateViewPhotoConvertBean = (PhotoSelectOrConvertBean) bundle
					.getSerializable(ConstantsTooTooEHelper.BundleResopnse);
			storyid = bundle.getString("storyid");
			isCreateListBean = isCreateViewPhotoConvertBean.getListBean();

		}

	}

	private String mp4Url;
	private UpLoadViewDialogFragment fragment;

	@Override
	public void skipToUpload(String mp4url) {
		this.mp4Url = mp4url;

		if (isCreateView || isEditextView || isRecommendView) {
			uploadVideo(mp4url);// 上传视频
		} else if (isRecommendConvertView) {
			createStoryId();

		} else if (isConvertView) {
			uploadVideo(mp4url);// 上传视频
		}

	}

	private String status;

	public void createConvert(final String fileurl, final String convert,
			final String thumb) {

		// StoryId：故事Id (必填)
		// PageType：故事页类型：1,文字，2图片，3视频 (必填)
		// PageContent：故事页文字
		// PageImgThumb：故事页缩略图
		// PageImg：故事页图片或直播录播封面图地址
		// PageVideoUrl：故事页录播视频地址
		// RecordId：录制Id
		// PageDesc：故事页描述
		// 创建封面图
		if ((NetworkUtil.isNetworkAvaliable(TootooPlusEApplication
				.getAppContext()))) {
			// 显示进度
			showProgressDialog(getActivity());
			RequestParamsNet requestParamsNet = new RequestParamsNet();
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYID, storyid);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.COVER_THUMB, thumb);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.COVER_IMG, convert);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORY_VIDEO_URL, fileurl);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORY_TYPE, "3");// 拍摄视频
			CommonUtil.xUtilsPostSend(TootooeNetApiUrlHelper.STORY_NAME_UPDATE,
					requestParamsNet, new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							closeProgressDialog(getActivity());
							String jsonStr = new String(responseInfo.result);
							try {
								JSONObject jsobj = new JSONObject(jsonStr);
								if (jsobj.has("Status")) {
									status = jsobj.getString("Status");
									if (status.equals("1")) {
										if (isConvertView) {
											ConVertBean convertBean = new ConVertBean();
											convertBean.setCoverThumb(thumb);
											convertBean.setCoverImg(convert);
											convertBean.setStoryType("3");
											convertBean
													.setStoryVideoUrl(fileurl);
											if (isActivitySecondView) {
												ConVertBean convertPostBean = storyBean
														.getConvertBean();

												if (convertPostBean != null) {
													convertBean
															.setCategoryId(convertPostBean
																	.getCategoryId());
													convertBean
															.setCategoryParentId(convertPostBean
																	.getCategoryParentId());
													convertBean
															.setStoryName(convertPostBean
																	.getStoryName());
													convertBean
															.setShoppingUrl(convertPostBean
																	.getShoppingUrl());
													convertBean
															.setStoryId(convertPostBean
																	.getStoryId());
												}

												storyBean
														.setConvertBean(convertBean);
											} else {
												ConVertBean convertPostBean = isCreateViewPhotoConvertBean
														.getConvertBean();
												if (convertPostBean != null) {
													convertBean
															.setCategoryId(convertPostBean
																	.getCategoryId());
													convertBean
															.setCategoryParentId(convertPostBean
																	.getCategoryParentId());
													convertBean
															.setStoryName(convertPostBean
																	.getStoryName());
													convertBean
															.setShoppingUrl(convertPostBean
																	.getShoppingUrl());
													convertBean
															.setStoryId(convertPostBean
																	.getStoryId());
												}

												isCreateViewPhotoConvertBean
														.setConvertBean(convertBean);
											}

										} else {
											ConVertBean convertBean = new ConVertBean();
											convertBean.setCoverThumb(thumb);
											convertBean.setCoverImg(convert);
											convertBean.setStoryType("3");
											convertBean
													.setStoryVideoUrl(fileurl);
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

										skipToOperaActivity();

									}

								}

							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							// TODO Auto-generated method stub
							closeProgressDialog(getActivity());
							ComponentUtil.showToast(
									TootooPlusEApplication.getAppContext(),
									getResources()
											.getString(
													R.string.errcode_network_response_timeout));
						}
					});

		} else {
			ComponentUtil.showToast(
					TootooPlusEApplication.getAppContext(),
					this.getResources().getString(
							R.string.errcode_network_response_timeout));
		}

	}

	public void createStoryId() {// 创建封面图

		// 创建封面图
		if ((NetworkUtil.isNetworkAvaliable(TootooPlusEApplication
				.getAppContext()))) {
			// 显示进度
			showProgressDialog(getActivity());
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
							closeProgressDialog(getActivity());
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
										uploadVideo(mp4Url);

									}
								}

								if (register_status.equals("1")) {

								} else if (register_status.equals("1123")) {
									ComponentUtil.showToast(
											TootooPlusEApplication
													.getAppContext(),
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
							closeProgressDialog(getActivity());
							ComponentUtil.showToast(
									TootooPlusEApplication.getAppContext(),
									getResources()
											.getString(
													R.string.errcode_network_response_timeout));
						}
					});

		} else {
			ComponentUtil.showToast(
					TootooPlusEApplication.getAppContext(),
					this.getResources().getString(
							R.string.errcode_network_response_timeout));
		}

	}

	/**
	 * 
	 * @Title: VideoStoryCreateFragment.java
	 * @Description: 创建故事id
	 * @author wuyulong
	 * @date 2014-8-19 下午4:32:01
	 * @param
	 * @return void
	 */

	public void uploadVideo(String mp4path) {
		if (NetworkUtil.isNetworkAvaliable(this.getActivity())) {
			// 如果有网络
			List<File> listFile = new ArrayList<File>();
			File upFile = new File(mp4path);
			listFile.add(upFile);
			File imageThumb = ImageUtil.getVideoThumbnailPhoto(mp4path,
					MediaStore.Images.Thumbnails.FULL_SCREEN_KIND, 480, 480);
			// LogUtil.systemlogInfo(tag, msg)
			if (upFile.exists() && imageThumb.exists()) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put(TootooeNetApiUrlHelper.UPLOAD_FIRLE_TYPE,
						TootooeNetApiUrlHelper.UPLOAD_FIRLE_TYPE_VIDEO);//
				map.put(TootooeNetApiUrlHelper.UPLOAD_FIRLE_FLAG,
						TootooeNetApiUrlHelper.UPLOAD_FIRLE_FLAG_DEBUG);
				map.put(TootooeNetApiUrlHelper.APPLICATIONID,
						(TootooeNetApiUrlHelper.APPLICATIONID_PARAM));
				String userId = SharedPreferenceHelper
						.getLoginUserId(TootooPlusEApplication.getAppContext());
				map.put(TootooeNetApiUrlHelper.USERID, userId);
				map.put(TootooeNetApiUrlHelper.ScenarioType, "");// 2是推荐故事
				map.put(TootooeNetApiUrlHelper.ElementType, "");// 传“”
				// HttpMultipartPost uploadVieo = new
				// HttpMultipartPost(getActivity(),
				// NetUtil.getAbsoluteUrl(TootooeNetApiUrlHelper.UPLOAD_FIRLE),
				// imageThumb, upFile, map, handler);
				// uploadVieo.execute();
				FragmentManager fragmentManager = getActivity()
						.getSupportFragmentManager();
				fragment = new UpLoadViewDialogFragment(imageThumb, upFile,
						map, handler, bundle);
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

			} else {
				ComponentUtil.showToast(getActivity(), this.getResources()
						.getString(R.string.already_no_video));
			}
		} else {
			ComponentUtil.showToast(getActivity(), this.getResources()
					.getString(R.string.errcode_network_response_timeout));
		}

	}

	private int index;

	private void skipToOperaActivity() {

		if (isRecommendConvertView) {
			Intent intent = new Intent(getActivity(), CreateWishActivity.class);
			bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
					photoSelectOrConvert);
			bundle.putString("storyid", storyid);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
			startActivity(intent);
		} else if (isCreateView) {

			if (isActivitySecondView) {
				Intent intent = new Intent(getActivity(),
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
				Intent intent = new Intent(getActivity(),
						CreateWishActivity.class);
				ConstantsTooTooEHelper.putView(
						ConstantsTooTooEHelper.isConvertRecommendView, bundle);
				bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
						isCreateViewPhotoConvertBean);
				bundle.putString("storyid", storyid);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
				startActivity(intent);
			}

		} else if (isConvertView) {

			if (isActivitySecondView) {
				Intent intent = new Intent(getActivity(),
						CreateActSecondStepActivity.class);
				bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
						secondStepStoryBean);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				bundle.putString("UpdateStoryId", UpdateStoryId);
				bundle.putString("storyid", storyid);
				bundle.putInt("currentPosition", currentPosition);
				intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			} else {
				Intent intent = new Intent(getActivity(),
						CreateWishActivity.class);

				ConstantsTooTooEHelper.putView(
						ConstantsTooTooEHelper.isConvertRecommendView, bundle);
				bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
						isCreateViewPhotoConvertBean);
				bundle.putString("storyid", storyid);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
				startActivity(intent);
			}

		} else if (isEditextView) {
			Intent intent = new Intent(getActivity(), CreateWishActivity.class);
			bundle.putString("UpdateStoryId", UpdateStoryId);
			bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
					isCreateViewPhotoConvertBean);
			ConstantsTooTooEHelper.putView(
					ConstantsTooTooEHelper.isRecommendView, bundle);
			bundle.putString("storyid", storyid);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
			startActivity(intent);
		} else if (isRecommendView) {
			Intent intent = new Intent(getActivity(), CreateWishActivity.class);
			bundle.putString("UpdateStoryId", UpdateStoryId);
			bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
					isCreateViewPhotoConvertBean);
			ConstantsTooTooEHelper.putView(
					ConstantsTooTooEHelper.isRecommendView, bundle);
			bundle.putString("storyid", storyid);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
			startActivity(intent);
		}

		getActivity().finish();
	}

	private int itemIndewx;
	private String PageId;

	public void postData(final String fileurl, final String yuantu,
			final String thumb, final String rect, final String square) {

		if ((NetworkUtil.isNetworkAvaliable(TootooPlusEApplication
				.getAppContext()))) {
			// 显示进度
			showProgressDialog(getActivity());
			RequestParamsNet requestParamsNet = new RequestParamsNet();
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYCREATE_STORYID, storyid);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYCREATE_PAGE_TYPE, "3");// 2是图片3是视频
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYCREATE_PAGE_VIDEO_URL, fileurl);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYCREATE_THUMBIMAGE, square);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYCREATE_PAGE_IMAGE, yuantu);
			CommonUtil.xUtilsPostSend(
					TootooeNetApiUrlHelper.STORYCREATE_PAGE_API,
					requestParamsNet, new RequestCallBack<String>() {

						private StoryDetailListBean heightItem;

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String obj = responseInfo.result;
							try {
								JSONObject jsobj = new JSONObject(obj);
								if (jsobj.has("Status")) {
									jsobj.getString("Status");

								}
								if (jsobj.has("Data")) {
									String st = jsobj.getString("Data");
									JSONObject storyjsonobj = new JSONObject(st);
									if (storyjsonobj.has("PageId")) {
										PageId = storyjsonobj
												.getString("PageId");
										if (!isRecommendConvertView) {

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
//												heightItem = isCreateListBean
//														.get(itemIndewx);
											}

											StoryDetailListBean storyDetailListBean = new StoryDetailListBean();
											storyDetailListBean
													.setElementType(2);
											storyDetailListBean
													.setPageType("3");
											storyDetailListBean
													.setPageId(PageId);
											storyDetailListBean
													.setDragSquareImg(square);
											storyDetailListBean
													.setDragRectangleImg(rect);
											int itemIndexO = itemIndewx;
											if (listSize == 0) {
												storyDetailListBean
														.setLocation(1);
												storyDetailListBean
														.setItemIndex(0);
												storyDetailListBean
														.setElementType(2);
											} else {
												if (heightItem != null) {
													int location = heightItem
															.getLocation();
													int elementType = heightItem
															.getElementType();
													if (location == 1
															&& elementType == 2) {//
														storyDetailListBean
																.setElementType(2);
														storyDetailListBean
																.setLocation(2);
														storyDetailListBean
																.setItemIndex(itemIndexO);
													} else {
														storyDetailListBean
																.setElementType(2);
														storyDetailListBean
																.setLocation(1);
														storyDetailListBean
																.setItemIndex(itemIndexO + 1);
													}
												}

											}

											isCreateListBean
													.add(storyDetailListBean);
											if (isActivitySecondView) {
												storyBean
														.setWishDetailBean(isCreateListBean);
											} else {
												isCreateViewPhotoConvertBean
														.setListBean(isCreateListBean);
											}
											skipToOperaActivity();
										}
									}
								}

							} catch (JSONException e) {
								LogUtil.error("CreateStoryFragment",
										e.toString());
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							closeProgressDialog(getActivity());
							ComponentUtil.showToast(
									TootooPlusEApplication.getAppContext(),
									getResources()
											.getString(
													R.string.errcode_network_response_timeout));
						}
					});

		} else {
			ComponentUtil.showToast(
					TootooPlusEApplication.getAppContext(),
					this.getResources().getString(
							R.string.errcode_network_response_timeout));
		}

	}
}
