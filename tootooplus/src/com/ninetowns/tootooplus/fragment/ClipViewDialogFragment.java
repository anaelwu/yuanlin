package com.ninetowns.tootooplus.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.library.util.ImageUtil;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.adapter.DragAdapter;
import com.ninetowns.tootooplus.adapter.DragAdapter.ViewHolder;
import com.ninetowns.tootooplus.bean.ConVertBean;
import com.ninetowns.tootooplus.bean.StoryDetailListBean;
import com.ninetowns.tootooplus.bean.UpLoadFileBean;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootooplus.util.CommonUtil;
import com.ninetowns.ui.widget.clipimageview.ClipImageView;
import com.ninetowns.ui.widget.clipimageview.ClipView;
import com.ninetowns.ui.widget.dialog.BaseFragmentDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 
 * @Title: ClipViewDialogFragment.java
 * @Description: 裁剪图片
 * @author huangchao @ editor wuyulong
 * @date 2015-1-8 上午11:09:29
 * @version V1.0
 */
public class ClipViewDialogFragment extends BaseFragmentDialog {

	private ClipImageView clip_view_df_src_pic;

	private int cellWidth = 0;

	private int cellHeight = 0;

	private DragAdapter dragAdatper = null;

	private StoryDetailListBean sdlbean = null;

	private Fragment operateFragment = null;

	private List<UpLoadFileBean> list;

	private String storyId = "";

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if (fragment != null) {
					fragment.dismiss();
				}
				list = (List<UpLoadFileBean>) msg.obj;
				if (list != null && list.size() > 0) {
					final UpLoadFileBean upLoadFileBean = list.get(0);

					if (vonvertBean != null) {
						showProgressDialog(getActivity());
						RequestParamsNet params = new RequestParamsNet();
						params.addQueryStringParameter(
								TootooeNetApiUrlHelper.STORYCREATE_STORYID,
								storyId);
						params.addQueryStringParameter(
								TootooeNetApiUrlHelper.STORYCREATE_STORY_NAME,
								vonvertBean.getStoryName());
						params.addQueryStringParameter(
								TootooeNetApiUrlHelper.STORYTYPE, "2");// 裁剪只能是裁剪图片
						params.addQueryStringParameter(
								TootooeNetApiUrlHelper.COVER_THUMB,
								upLoadFileBean.getThumbFileUrl());
						params.addQueryStringParameter(
								TootooeNetApiUrlHelper.COVER_IMG,
								vonvertBean.getCoverImg());
						CommonUtil.xUtilsPostSend(
								TootooeNetApiUrlHelper.STORYCREATE_NAME_UPDATE,
								params, new RequestCallBack<String>() {

									@Override
									public void onSuccess(
											ResponseInfo<String> responseInfo) {
										closeProgressDialog(getActivity());
										String strRespone = responseInfo.result;
										if (!TextUtils.isEmpty(strRespone)) {
											try {
												JSONObject jsobj = new JSONObject(
														strRespone);
												if (jsobj.has("Status")) {
													String status = jsobj
															.getString("Status");

													if (status.equals("1")) {

														ImageLoader
																.getInstance()
																.displayImage(
																		upLoadFileBean
																				.getListCoverImg(),
																		new ImageViewAware(
																				imageConvert),
																		CommonUtil.OPTIONS_ALBUM_DETAIL,
																		new ImageLoadingListener() {

																			@Override
																			public void onLoadingStarted(
																					String arg0,
																					View arg1) {
																			}

																			@Override
																			public void onLoadingFailed(
																					String arg0,
																					View arg1,
																					FailReason arg2) {
																			}

																			@Override
																			public void onLoadingComplete(
																					String arg0,
																					View arg1,
																					Bitmap arg2) {
																				if (arg2 != null) {
																					// vonvertBean.setCoverImg(upLoadFileBean.getListCoverImg());
																					vonvertBean
																							.setCoverThumb(upLoadFileBean
																									.getThumbFileUrl());
																					imageConvert
																							.setImageBitmap(arg2);
																					// dragAdatper.notifyDataSetChanged();
																					dismiss();
																				}
																			}

																			@Override
																			public void onLoadingCancelled(
																					String arg0,
																					View arg1) {
																			}
																		});

													}

												}

											} catch (JSONException e) {
												e.printStackTrace();
											}
										}

									}

									@Override
									public void onFailure(HttpException error,
											String msg) {
										closeProgressDialog(getActivity());
										ComponentUtil
												.showToast(
														getActivity(),
														getResources()
																.getString(
																		R.string.errcode_network_response_timeout));
									}
								});

					}
					if (sdlbean != null) {

						RequestParamsNet params = new RequestParamsNet();
						params.addQueryStringParameter(
								TootooeNetApiUrlHelper.STORYCREATE_PAGEID,
								sdlbean.getPageId());
						params.addQueryStringParameter(
								TootooeNetApiUrlHelper.STORYCREATE_STORYID,
								storyId);
						String pageType = sdlbean.getPageType();
						params.addQueryStringParameter(
								TootooeNetApiUrlHelper.STORYCREATE_PAGE_TYPE,
								pageType);// 裁剪只能是图片

						if (cellWidth == cellHeight) {
							params.addQueryStringParameter(
									TootooeNetApiUrlHelper.STORYCREATE_THUMBIMAGE,
									upLoadFileBean.getDragSquareImg());
						} else {
							params.addQueryStringParameter(
									TootooeNetApiUrlHelper.STORYCREATE_THUMBIMAGE,
									upLoadFileBean.getDragRectangleImg());
						}
						params.addQueryStringParameter(
								TootooeNetApiUrlHelper.STORYCREATE_PAGE_IMAGE,
								sdlbean.getPageImg());

						CommonUtil.xUtilsPostSend(
								TootooeNetApiUrlHelper.STORYCREATE_PAGE_UPDATE,
								params, new RequestCallBack<String>() {

									@Override
									public void onSuccess(
											ResponseInfo<String> responseInfo) {
										String strRespone = responseInfo.result;
										if (!TextUtils.isEmpty(strRespone)) {
											try {
												JSONObject jsobj = new JSONObject(
														strRespone);
												if (jsobj.has("Status")) {
													String status = jsobj
															.getString("Status");

													if (status.equals("1")) {

														ImageLoader
																.getInstance()
																.displayImage(
																		upLoadFileBean
																				.getDragSquareImg(),
																		new ImageViewAware(
																				mItemImage),
																		CommonUtil.OPTIONS_ALBUM_DETAIL,
																		new ImageLoadingListener() {

																			@Override
																			public void onLoadingStarted(
																					String arg0,
																					View arg1) {
																			}

																			@Override
																			public void onLoadingFailed(
																					String arg0,
																					View arg1,
																					FailReason arg2) {
																			}

																			@Override
																			public void onLoadingComplete(
																					String arg0,
																					View arg1,
																					Bitmap arg2) {
																				if (arg2 != null) {
																					sdlbean.setDragRectangleImg(upLoadFileBean
																							.getDragRectangleImg());
																					sdlbean.setDragSquareImg(upLoadFileBean
																							.getDragSquareImg());
																					sdlbean.setPageImgThumbBigRectangle(upLoadFileBean.getDragRectangleBigImg());
																					sdlbean.setPageImgThumbBigSquare(upLoadFileBean.getDragSquareBigImg());
																					mItemImage
																							.setImageBitmap(arg2);
																					dragAdatper
																							.notifyDataSetChanged();
																					dismiss();
																				}
																			}

																			@Override
																			public void onLoadingCancelled(
																					String arg0,
																					View arg1) {
																			}
																		});

													}

												}

											} catch (JSONException e) {
												e.printStackTrace();
											}
										}

									}

									@Override
									public void onFailure(HttpException error,
											String msg) {
										closeProgressDialog(getActivity());
										ComponentUtil
												.showToast(
														getActivity(),
														getResources()
																.getString(
																		R.string.errcode_network_response_timeout));
									}
								});

					}

				}

				break;
			}

		}

	};

	private UpLoadViewDialogFragment fragment;

	private ConVertBean vonvertBean;

	private ImageView mItemImage;
	private ImageView imageConvert;

	public ClipViewDialogFragment(Fragment operateFragment,
			DragAdapter adatper, ConVertBean vonvertBean, String storyId,
			ImageView imageConvert) {
		this.imageConvert = imageConvert;
		this.operateFragment = operateFragment;
		this.cellWidth = imageConvert.getWidth();
		this.cellHeight = imageConvert.getHeight();
		this.dragAdatper = adatper;
		this.vonvertBean = vonvertBean;
		this.storyId = storyId;
	}

	public ClipViewDialogFragment(Fragment operateFragment,
			DragAdapter adatper, StoryDetailListBean sdlbean, String storyId,
			View view) {
		this.operateFragment = operateFragment;
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		mItemImage = (ImageView) viewHolder.mItemImage;
		this.cellWidth = view.getWidth();
		this.cellHeight = view.getHeight();
		this.dragAdatper = adatper;
		this.sdlbean = sdlbean;
		this.storyId = storyId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.clip_view_dialog_fragment, null);

		LinearLayout two_or_one_btn_head_back = (LinearLayout) view
				.findViewById(R.id.two_or_one_btn_head_back);
		two_or_one_btn_head_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		TextView two_or_one_btn_head_title = (TextView) view
				.findViewById(R.id.two_or_one_btn_head_title);
		two_or_one_btn_head_title
				.setText(R.string.clip_view_diloag_fragment_title);
		two_or_one_btn_head_title.setTextColor(getResources().getColor(
				R.color.black));

		TextView two_or_one_btn_head_second_tv = (TextView) view
				.findViewById(R.id.two_or_one_btn_head_second_tv);
		two_or_one_btn_head_second_tv.setVisibility(View.VISIBLE);
		two_or_one_btn_head_second_tv.setText(R.string.complet);
		two_or_one_btn_head_second_tv.setTextColor(getResources().getColor(
				R.color.black));

		RelativeLayout two_or_one_btn_head_second_layout = (RelativeLayout) view
				.findViewById(R.id.two_or_one_btn_head_second_layout);
		two_or_one_btn_head_second_layout
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 此处获取剪裁后的bitmap
						Bitmap bitmap = clip_view_df_src_pic.clip();
						if (sdlbean != null) {
							// 可拖拉的
							uploadImage(bitmap, "2");
						}
						if (vonvertBean != null) {
							// 封面
							uploadImage(bitmap, "1");
						}

					}
				});

		// 需要裁剪的原图
		clip_view_df_src_pic = (ClipImageView) view
				.findViewById(R.id.clip_view_df_src_pic);
		clip_view_df_src_pic.borderAttrs(cellWidth, cellHeight);
		if (sdlbean != null)
			ImageLoader.getInstance().displayImage(sdlbean.getPageImg(),
					clip_view_df_src_pic);
		if (vonvertBean != null)
			ImageLoader.getInstance().displayImage(vonvertBean.getCoverImg(),
					clip_view_df_src_pic);
		// 裁剪框
		ClipView cilp_view_df_clip = (ClipView) view
				.findViewById(R.id.cilp_view_df_clip);
		cilp_view_df_clip.setClipViewAttrs(cellWidth, cellHeight);

		return view;

	}

	/**
	 * 上传剪切图
	 */
	private void uploadImage(Bitmap bmp, String elementType) {
		String upFileName = ImageUtil.makePhotoName(new Date());
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(upFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
		List<File> listFile = new ArrayList<File>();
		File upFile = new File(upFileName);
		listFile.add(upFile);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(TootooeNetApiUrlHelper.USERID,
				SharedPreferenceHelper.getLoginUserId(getActivity()));
		map.put(TootooeNetApiUrlHelper.APPLICATIONID,
				(TootooeNetApiUrlHelper.APPLICATIONID_PARAM));
		map.put(TootooeNetApiUrlHelper.UPLOAD_FIRLE_TYPE,
				TootooeNetApiUrlHelper.UPLOAD_FIRLE_TYPE_PHOTO);
		map.put(TootooeNetApiUrlHelper.UPLOAD_FIRLE_FLAG,
				TootooeNetApiUrlHelper.UPLOAD_FIRLE_FLAG_DEBUG);
		// 裁剪图片传"4"
		map.put(TootooeNetApiUrlHelper.ScenarioType, "4");
		map.put(TootooeNetApiUrlHelper.ElementType, elementType);

		FragmentManager fragmentManager = operateFragment.getActivity()
				.getSupportFragmentManager();
		fragment = new UpLoadViewDialogFragment(listFile, handler, map);
		if (fragmentManager != null) {
			// 屏幕较小，以全屏形式显示
			FragmentTransaction transaction = fragmentManager
					.beginTransaction();
			// 指定一个过渡动画
			transaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			transaction.addToBackStack(null);
			fragment.show(fragmentManager, "dialog");
		}
	}
}
