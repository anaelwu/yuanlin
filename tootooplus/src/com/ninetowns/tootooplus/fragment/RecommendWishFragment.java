package com.ninetowns.tootooplus.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.library.util.ImageUtil;
import com.ninetowns.library.util.LogUtil;
import com.ninetowns.library.util.NetworkUtil;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.activity.CoverRecommendSelectActivity;
import com.ninetowns.tootooplus.activity.HomeActivity;
import com.ninetowns.tootooplus.activity.InternetBrowserActivity;
import com.ninetowns.tootooplus.activity.MyWishActivity;
import com.ninetowns.tootooplus.activity.PhotoCameraActivity;
import com.ninetowns.tootooplus.activity.RecordVideoActivity;
import com.ninetowns.tootooplus.activity.TextStoryActivity;
import com.ninetowns.tootooplus.adapter.DragAdapter;
import com.ninetowns.tootooplus.adapter.DragAdapter.ViewHolder;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.bean.ConVertBean;
import com.ninetowns.tootooplus.bean.PageBean;
import com.ninetowns.tootooplus.bean.PhotoSelectOrConvertBean;
import com.ninetowns.tootooplus.bean.StoryDetailListBean;
import com.ninetowns.tootooplus.fragment.GoodsTypeDialogFragment.OnScreenGoods;
import com.ninetowns.tootooplus.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootooplus.util.CommonUtil;
import com.ninetowns.ui.cooldraganddrop.SpanVariableGridView.LayoutParams;
import com.ninetowns.ui.widget.popwindow.StoryItemPopupWindow;
import com.ninetowns.ui.widget.popwindow.StoryItemPopupWindow.PopWindowItemClickListener;

/**
 * 
 * @ClassName: RecommendWishFragment
 * @Description: 商家推荐界面
 * @author wuyulong
 * @date 2015-1-28 上午11:33:52
 * 
 */
public class RecommendWishFragment extends CreateWishBaseFragment implements
		OnScreenGoods, CreateWishBaseFragment.OnUpdateStoryIdListener {
	public Bundle bundle;
	private DragAdapter mItemAdapter;
	public String storyid;
	private String updateStoryId = "0";
	private boolean isConvertRecommendView;
	private List<StoryDetailListBean> locallist;
	private StoryItemPopupWindow spw;
	private boolean isClickPush;
	private boolean isClickDialog;
	private List<StoryDetailListBean> listAdapter = new ArrayList<StoryDetailListBean>();
	private StoryDetailListBean oldBean;
	private String activity_create_Id;
	private String type;
	private String storyCreateId;
	private String ratingCount;// 商品推荐指数
	//记录点击哪一条目的hashMap
	private HashMap<String,Integer>mHashMap=new HashMap<String,Integer>();

	@Override
	public void getBundle(Bundle bundle) {
		mHashMap.put(ConstantsTooTooEHelper.POSITION_KEY, ConstantsTooTooEHelper.NO_ITEM_CLICK_INSERT_CODE);
		setOnUpdateStoryIdListener(this);
		this.bundle = bundle;
		isConvertRecommendView = bundle
				.getBoolean(ConstantsTooTooEHelper.isConvertRecommendView);
		storyid = bundle.getString("storyid");
		updateStoryId = bundle.getString("UpdateStoryId");

		type = bundle.getString(TootooeNetApiUrlHelper.TYPE);
		activity_create_Id = bundle
				.getString(TootooeNetApiUrlHelper.ACTIVITY_CREATE_ID);
		storyCreateId = bundle
				.getString(TootooeNetApiUrlHelper.STORY_CREATE_ID);
		sortStoryId();
		getBundleType(bundle);
	}

	/**
	 * 
	 * @Title: sortStoryId
	 * @Description: 获得第一次编辑的storyid和updateStroryId
	 * @param
	 * @return
	 * @throws
	 */
	public void sortStoryId() {
		if (updateStoryId != null) {
			sortId();
		} else {
		}
	}

	/**
	 * 
	 * @Title: sortId
	 * @Description: 找到最新的storyid和旧的storyid
	 * @param
	 * @return
	 * @throws
	 */
	private void sortId() {
		if (updateStoryId != null && storyid != null) {
			// 编辑
			int storyid0 = 0;
			storyid0 = Integer.valueOf(updateStoryId);
			int storyid1 = Integer.valueOf(storyid);
			if (storyid0 > storyid1) {
				this.storyid = String.valueOf(storyid0);
				this.updateStoryId = String.valueOf(storyid1);
			} else {
				this.storyid = String.valueOf(storyid1);
				this.updateStoryId = String.valueOf(storyid0);
			}
		}

	}

	public void getBundleType(Bundle bundle2) {

	}

	private String categoryidMain;
	private String categoryidSub;
	private String shoppingUrl;
	private int oldRow;
	private int itemIndex = 0;

	@Override
	public void onPushGoodsListener(View push, String categoryidMain,
			String categoryidSub, String shoppingUrl) {
		this.categoryidMain = categoryidMain;
		this.categoryidSub = categoryidSub;
		this.shoppingUrl = shoppingUrl;
		pushParData();

	}

	/**
	 * @Title: pushParData
	 * @Description: 发布故事
	 * @param
	 * @return
	 * @throws
	 */
	private void pushParData() {
		String content = mEtName.getText().toString();
		isClickPush = true;
		if (!TextUtils.isEmpty(content)) {
			LogUtil.systemlogInfo("编辑时的文字", content);
			pushEditeName(storyid, content);
		} else {
			ComponentUtil.showToast(getActivity(),
					TootooPlusApplication.getAppContext().getResources()
							.getString(R.string.plase_input_title));
		}
	}

	@Override
	public void onDragItem(int from) {

	}

	@Override
	public void onDraggingItem(int from, int to) {

	}

	@Override
	public void onDropItem(int from, int to) {
		if (from != to) {
			// if(currentList!=null){
			// StoryDetailListBean fromBean = currentList.get(from);
			// StoryDetailListBean toBean = currentList.get(to);
			//
			// }
			locallist.add(to, locallist.remove(from));
			mItemAdapter.notifyDataSetChanged();
		}

	}

	@Override
	public boolean isDragAndDropEnabled(int position) {
		return true;
	}

	@Override
	protected void justCanPushData() {

		if (justIsPushComment()) {
			skipToCommentType();
		} else {
			String strCrenotice = TootooPlusApplication.getAppContext()
					.getResources().getString(R.string.cre_comment_notice);
			ComponentUtil.showToast(TootooPlusApplication.getAppContext(),
					strCrenotice);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, final View view,
			final int position, long arg3) {
//		mHashMap.put(ConstantsTooTooEHelper.POSITION_KEY, position);
		final StoryDetailListBean storyDetailListBean = (StoryDetailListBean) mItemAdapter
				.getItem(position);
		final String pageType = storyDetailListBean.getPageType();

		if (!TextUtils.isEmpty(pageType)) {
			spw = new StoryItemPopupWindow(
					TootooPlusApplication.getAppContext(), pageType);
			spw.setOutSideTouch(true);
			spw.show(view);
			spw.setPopWindowItemClickListener(new PopWindowItemClickListener() {

				@Override
				public void popWinOneClick() {
					if (pageType.equals("1")) {

						String storyid = getStoryId();

						skipToEditextFragmentDialog(storyDetailListBean,
								storyid);
						// setEditFause(storyDetailListBean, storyid, view);
						//

					} else if (pageType.equals("2")) {

						changeStoryItem(storyDetailListBean, position, view);

					} else {// 视频的时候删除
							// 删除
						deleStory(storyDetailListBean, position);
					}

				}

				@Override
				public void popWinTwoClick() {
					if (pageType.equals("1")) {
						// 删除
						// 删除
						deleStory(storyDetailListBean, position);

					} else if (pageType.equals("2")) {

						String storyid = getStoryId();
						skiptoClipFragment(storyDetailListBean, storyid, view);

					} else {

					}

				}

				@Override
				public void popWinThreeClick() {
					if (pageType.equals("1")) {

					} else if (pageType.equals("2")) {
						// 删除
						deleStory(storyDetailListBean, position);
					} else {
						// 删除
						deleStory(storyDetailListBean, position);
					}

				}

			});

		}

	}

	private String getStoryId() {
		String storyids = "";
		int storyid0 = 0;
		int storyid1 = 0;
		if (updateStoryId != null) {
			storyid0 = Integer.valueOf(updateStoryId);
		} else {
			storyid0 = 0;
		}
		if (!TextUtils.isEmpty(storyid)) {
			storyid1 = Integer.valueOf(storyid);
		}
		if (storyid0 > storyid1) {
			storyids = String.valueOf(storyid0);
		} else {
			storyids = String.valueOf(storyid1);
		}
		return storyids;
	}

	/**
	 * 
	 * @Title: changeStoryItem
	 * @Description: 变换长方形正方形
	 * @param
	 * @return
	 * @throws
	 */
	protected void changeStoryItem(StoryDetailListBean storyDetailListBean,
			int position, View view) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		int mSpans = storyDetailListBean.getElementType();

		if (mSpans == 1) {// 长方形变正方形-1
			setRefreshAdapter(mSpans, position, storyDetailListBean);
		} else if (mSpans == 2) {// 正方形变大
			setRefreshAdapter(mSpans, position, storyDetailListBean);
		}else if(mSpans == 3){
			setRefreshAdapter(mSpans, position, storyDetailListBean);
		}else if(mSpans == 4){//大正方形变小正方形
			setRefreshAdapter(mSpans, position, storyDetailListBean);
		}
		mItemAdapter.notifyDataSetChanged();
	}

	/**
	 * 
	 * @Title: setRefreshAdapter
	 * @Description: TODO
	 * @param IsRect
	 *            ==1是长方形 2正方形
	 * @return
	 * @throws
	 */
	public void setRefreshAdapter(int IsRect, int position,
			StoryDetailListBean storyDetailListBean) {
		List<StoryDetailListBean> mList = mItemAdapter.mListDragBean;
		if (IsRect == 1||IsRect==3||IsRect==4) {// 长变正
			int prePostion = position - 1;
			int nextPosition = position + 1;
			storyDetailListBean.setElementType(2);
			if (prePostion < 0) {// 说明是第一个
				rectChangeToSquare(mList, nextPosition);
			} else if (prePostion >= 0) {
				int elementType = mList.get(prePostion).getElementType();
				int localtion = mList.get(prePostion).getLocation();
				if (elementType == 2 && localtion == 2) {// 如果前面的是正方形 并且位于右边
					rectChangeToSquare(mList, nextPosition);
				} else if (elementType == 2 && localtion == 1) {// 如果前面的是正方形
																// 并且位于左边
					for (int i = 0; i < mList.size(); i++) {
						StoryDetailListBean item = mList.get(i);
						if (i >= position) {// 大于等于当前的都要-1
							int nextPositionItemIndex = item.getItemIndex();
							item.setItemIndex(nextPositionItemIndex - 1);
						}

					}
				} else if (elementType == 1||elementType ==3||elementType ==4) {// 前面的是长方形
					rectChangeToSquare(mList, nextPosition);
				}

			}

		} else if (IsRect == 2) {//
			int currentLocation = storyDetailListBean.getLocation();
			String bigrect=storyDetailListBean.getPageImgThumbBigRectangle();
			String bigSquare=storyDetailListBean.getPageImgThumbBigSquare();
			String rectthumb=storyDetailListBean.getPageImgThumbRectangle();
			String squareThumb=storyDetailListBean.getPageImgThumbSquare();
			String rectimg=storyDetailListBean.getDragRectangleImg();
			String squareimg=storyDetailListBean.getDragSquareImg();
			String defaultType=storyDetailListBean.getDefaultType();//小正方形要转化成的
			if(!TextUtils.isEmpty(bigrect)&&!bigrect.equals("null")){//判断是否是老数据
				if(!TextUtils.isEmpty(defaultType)){
					storyDetailListBean.setElementType(Integer.valueOf(defaultType));
				}else{
					storyDetailListBean.setElementType(4);
				}
				
			}else{
				storyDetailListBean.setElementType(1);
			}
			if (currentLocation == 2) {// 如果等于2
				for (int i = 0; i < mList.size(); i++) {
					StoryDetailListBean item = mList.get(i);
					if (i >= position) {
						int nextPositionItemIndex = item.getItemIndex();
						item.setItemIndex(nextPositionItemIndex + 1);
					}

				}
			} else if (currentLocation == 1) {
				for (int i = 0; i < mList.size(); i++) {
					StoryDetailListBean item = mList.get(i);
					if (i > position) {
						int nextPositionItemIndex = item.getItemIndex();
						item.setItemIndex(nextPositionItemIndex + 1);
					}

				}

			} else {
				LogUtil.systemlogError("location=", currentLocation + "错误");
			}

		}
		mItemAdapter.notifyDataSetChanged();

	}

	private void rectChangeToSquare(List<StoryDetailListBean> mList,
			int nextPosition) {
		for (int i = 0; i < mList.size(); i++) {
			StoryDetailListBean item = mList.get(i);
			if (i >= nextPosition) {
				int nextPositionItemIndex = item.getItemIndex();
				item.setItemIndex(nextPositionItemIndex - 1);
			}

		}
	}

	@Override
	public void setTitleName() {
		if (!TextUtils.isEmpty(type)) {
			if (type.equals("0")) {
				mCTMiddle.setText(getActivity().getResources().getString(
						R.string.trader_wish));
			} else if (type.equals("1")) {
				mCTMiddle.setText(getActivity().getResources().getString(
						R.string.comment_wish));
			}
		}

	}

	/**
	 * 
	 * @Title: deleStory
	 * @Description: TODO
	 * @param
	 * @return
	 * @throws
	 */
	protected void deleStory(StoryDetailListBean storyDetailListBean,
			final int position) {

		if ((NetworkUtil.isNetworkAvaliable(getActivity()))) {
			// 显示进度
			RequestParamsNet requestParamsNet = new RequestParamsNet();

			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYID, storyid);
			requestParamsNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.PAGE_ID,
					storyDetailListBean.getPageId());
			CommonUtil.xUtilsPostSend(TootooeNetApiUrlHelper.DEL_PAGE_STORY,
					requestParamsNet, new RequestCallBack<String>() {

						private String status;

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String jsonStr = new String(responseInfo.result);
							try {
								JSONObject jsobj = new JSONObject(jsonStr);
								if (jsobj.has("Status")) {
									status = jsobj.getString("Status");
									if (status.equals("1")) {
										locallist.remove(position);
										mItemAdapter.notifyDataSetChanged();

									}

								}

							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							if (isAdded()) {
								ComponentUtil
										.showToast(
												getActivity(),
												getActivity()
														.getString(
																R.string.errcode_network_response_timeout));
							}
						}
					});

		} else {
			ComponentUtil.showToast(
					getActivity(),
					getActivity().getString(
							R.string.errcode_network_response_timeout));
		}

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub

		mCoolDragAndDropGridView.startDragAndDrop();
		return false;
	}

	@Override
	public void onCalculatePosition(View view, int position, int row, int column) {
		if (mItemAdapter != null) {
			List<StoryDetailListBean> localList = mItemAdapter.mListDragBean;
			StoryDetailListBean itemPosition = localList.get(position);
			// if(!itemPosition.getPageType().equals("1")){
			itemPosition.setItemIndex(row);
			// }

			LogUtil.systemlogError("上传参数值",
					"itemindex=" + itemPosition.getItemIndex() + "location="
							+ itemPosition.getLocation() + "elementType="
							+ itemPosition.getElementType() + "imageurl"
							+ itemPosition.getPageImg() + "我的row" + row);

		}

	}

	@Override
	public boolean isGetNetData() {
		return false;
	}

	@Override
	public DragAdapter initAdapter(List<StoryDetailListBean> list) {
		locallist = list;
		mItemAdapter = new DragAdapter(locallist);
		return mItemAdapter;
	}

	@Override
	public void onDialogSure() {
		isClickDialog = true;
		saveDraft();
	}

	@Override
	public void skipVideoView(boolean isConvert) {
		Intent intent = new Intent(this.getActivity(),
				RecordVideoActivity.class);
		skipToActivity(isConvert, intent);

	}

	@Override
	public void skipTextEditView(boolean isTitle, boolean isConvert) {
		Intent intent = new Intent(this.getActivity(), TextStoryActivity.class);
		bundle.putBoolean("isTitle", isTitle);
		skipToActivity(isConvert, intent);
	}

	@Override
	public void skipLocalPhotoView(boolean isConvert) {
		Intent intent = new Intent(this.getActivity(),
				CoverRecommendSelectActivity.class);
		skipToActivity(isConvert, intent);
	}

	@Override
	public void skipInternetCollect(boolean isConvert) {

		Intent intent = new Intent(this.getActivity(),
				InternetBrowserActivity.class);
		skipToActivity(isConvert, intent);

	}

	@Override
	public void onCameraDataCallback(int arg0, int arg1, Intent arg2,
			boolean isConvert) {

		if (arg0 == 1 && arg1 == Activity.RESULT_OK) {

			Intent intent = new Intent(this.getActivity(),
					PhotoCameraActivity.class);
			skipToActivity(isConvert, intent);
		}

	}



	private void skipToActivity(boolean isConvert, Intent intent) {

		PhotoSelectOrConvertBean photoSelectOrConvert = new PhotoSelectOrConvertBean();
		photoSelectOrConvert.setmHashMap(mHashMap);
		ConVertBean convert = mConvertAndListWishBean.getConvertBean();
		if (convert != null) {
			LogUtil.systemlogError("当不是null的时候", "");
			String content = mEtName.getText().toString();
			convert.setStoryName(content);
			photoSelectOrConvert.setConvertBean(convert);
		} else {
			ConVertBean convert1 = new ConVertBean();
			String content = mEtName.getText().toString();
			convert1.setStoryName(content);
			photoSelectOrConvert.setConvertBean(convert1);
			LogUtil.systemlogError("当是null的时候", "");
		}

		if (mItemAdapter != null) {
			List<StoryDetailListBean> list = mItemAdapter.mListDragBean;
			photoSelectOrConvert.setListBean(list);
		} else {
			photoSelectOrConvert
					.setListBean(new ArrayList<StoryDetailListBean>());
		}
		bundle.putString("storyid", storyid);
		bundle.putString("UpdateStoryId", updateStoryId);
		justIsCommentOrWish(bundle);
		bundle.putSerializable(ConstantsTooTooEHelper.BundleResopnse,
				photoSelectOrConvert);
		if (isConvert) {
			ConstantsTooTooEHelper.putView(
					ConstantsTooTooEHelper.isConvertView, bundle);
		} else {
			putParamType(bundle);
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		bundle.putString("picPath", ImageUtil.getTempPhotoPath());// 拍照图片的路径
		intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
		startActivity(intent);
	}

	/**
	 * 
	 * @Title: putParamType
	 * @Description: 跳转的不同类型
	 * @param
	 * @return
	 * @throws
	 */
	public void putParamType(Bundle bundle) {
		ConstantsTooTooEHelper.putView(ConstantsTooTooEHelper.isCreateView,
				bundle);
	}

	

	

	/**
	 * 
	 * @Title: skipToEditextFragmentDialog
	 * @Description: 编辑文字
	 * @param
	 * @return
	 * @throws
	 */
	public void skipToEditextFragmentDialog(StoryDetailListBean sdlbean,
			String storyId) {
		FragmentManager fragmentManager = getActivity()
				.getSupportFragmentManager();
		TextEditextDialogFragment fragment = new TextEditextDialogFragment(
				this, mItemAdapter, sdlbean, storyId);
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
		}
	}

	/**
	 * 
	 * @Title: skiptoClipFragment
	 * @Description: 跳转到裁剪
	 * @param
	 * @return
	 * @throws
	 */
	public void skiptoClipFragment(StoryDetailListBean sdlbean, String storyId,
			View view) {
		FragmentManager fragmentManager = getActivity()
				.getSupportFragmentManager();
		ClipViewDialogFragment fragment = new ClipViewDialogFragment(this,
				mItemAdapter, sdlbean, storyId, view);
		instanstFragment(fragmentManager, fragment);
	}

	private void instanstFragment(FragmentManager fragmentManager,
			ClipViewDialogFragment fragment) {
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
		}
	}

	@Override
	public void skipToClipConvert(ImageView convertImage,
			ConVertBean convertBean) {
		FragmentManager fragmentManager = getActivity()
				.getSupportFragmentManager();
		ClipViewDialogFragment fragment = new ClipViewDialogFragment(this,
				mItemAdapter, convertBean, getStoryId(), convertImage);
		instanstFragment(fragmentManager, fragment);
	}

	@Override
	public void saveDraft() {
		String content = mEtName.getText().toString();

		if (!TextUtils.isEmpty(content)) {
			LogUtil.systemlogInfo("编辑时的文字", content);
			pushEditeName(storyid, content);
		} else {
			ComponentUtil.showToast(getActivity(),
					TootooPlusApplication.getAppContext().getResources()
							.getString(R.string.plase_input_title));
		}

	}

	private void pushEditeName(final String storyid, String content) {

		RequestParamsNet params = new RequestParamsNet();
		params.addQueryStringParameter(
				TootooeNetApiUrlHelper.STORYCREATE_STORYID, storyid);
		params.addQueryStringParameter(
				TootooeNetApiUrlHelper.STORYCREATE_STORY_NAME, content);
		CommonUtil.xUtilsPostSend(
				TootooeNetApiUrlHelper.STORYCREATE_NAME_UPDATE, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						closeProgressDialogFragment();
						String strRespone = responseInfo.result;
						if (!TextUtils.isEmpty(strRespone)) {
							try {
								JSONObject jsobj = new JSONObject(strRespone);
								if (jsobj.has("Status")) {
									String status = jsobj.getString("Status");
									if (mItemAdapter != null) {
										if (mItemAdapter.mListDragBean != null) {
											listAdapter = mItemAdapter.mListDragBean;
										}

									}
									int size = listAdapter.size();
									if (status.equals("1")) {
										if (isClickPush) {

											if (size > 1) {
												saveDraftToJson();
											} else {
												// ComponentUtil.showToast(
												// TootooPlusEApplication
												// .getAppContext(),
												// "发布成功！");
												// 封面图
												pushChData();

											}

										} else {
											// 编辑成功之后调用保存
											if (size > 1) {
												saveDraftToJson();
											} else {// 封面图
												saveStoryToDraft(storyid,
														updateStoryId);
												// ComponentUtil.showToast(
												// TootooPlusEApplication
												// .getAppContext(),
												// "发布成功！");
											}

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

					@Override
					public void onFailure(HttpException error, String msg) {
						closeProgressDialogFragment();
						ComponentUtil
								.showToast(
										getActivity(),
										getResources()
												.getString(
														R.string.errcode_network_response_timeout));
					}
				});

	}

	@Override
	public void savestatus(String status) {
		if (status.equals("1")) {
			savetoDraftOrPush();
		} else {
			LogUtil.systemlogInfo("savestatus", "结构保存失败");
		}

	}

	/**
	 * @Title: savetoDraftOrPush
	 * @Description: TODO
	 * @param
	 * @return
	 * @throws
	 */
	private void savetoDraftOrPush() {
		if (isClickPush) {
			pushChData();
		} else {
			saveStoryToDraft(storyid, updateStoryId);
		}
	}

	@Override
	public void saveDraftStatus(String status) {
		if (status.equals("1")) {
			if (isClickDialog) {
				getActivity().finish();
			}
			ComponentUtil.showToast(getActivity(), "保存成功");
		} else {
			ComponentUtil.showToast(getActivity(), "保存失败");
		}

	}

	/**
	 * 
	 * @Title: pushChData
	 * @Description: 发送心愿故事
	 * @param
	 * @return
	 * @throws
	 */
	private void pushChData() {

		RequestParamsNet params = new RequestParamsNet();
		params.addQueryStringParameter(TootooeNetApiUrlHelper.USER_ID,
				SharedPreferenceHelper.getLoginUserId(TootooPlusApplication
						.getAppContext()));
		if (updateStoryId != null) {
			params.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYCREATE_UPDATE_STORYID,
					updateStoryId);
		}
		params.addQueryStringParameter(TootooeNetApiUrlHelper.TYPE, type);
		params.addQueryStringParameter(
				TootooeNetApiUrlHelper.STORYCREATE_STORYID, storyid);

		if (!TextUtils.isEmpty(categoryidMain)) {
			params.addQueryStringParameter(
					TootooeNetApiUrlHelper.CATEGORY_PARENT_ID, categoryidMain);// //商品父分类
		}
		//
		if (!TextUtils.isEmpty(ratingCount)) {
			params.addQueryStringParameter(
					TootooeNetApiUrlHelper.COUNTRECOMMEND, ratingCount);// 商品推荐指数
		}
		if (!TextUtils.isEmpty(categoryidSub)) {
			params.addQueryStringParameter(TootooeNetApiUrlHelper.CATE_GORYID,
					categoryidSub);// 商品子分类 (必填)
		}
		if (!TextUtils.isEmpty(shoppingUrl)) {
			params.addQueryStringParameter(TootooeNetApiUrlHelper.SHOPPING_URL,
					shoppingUrl);// 导购链接
		}

		CommonUtil.xUtilsPostSend(TootooeNetApiUrlHelper.STORYCREATE_PAGE_PUSH,
				params, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						closeProgressDialogFragment();
						String strRespone = responseInfo.result;
						if (!TextUtils.isEmpty(strRespone)) {
							try {
								JSONObject jsobj = new JSONObject(strRespone);
								if (jsobj.has("Status")) {
									String status = jsobj.getString("Status");
									if (status.equals("1")) {
										if (!TextUtils.isEmpty(type)) {
											if (type.equals("0")) {
												Intent intent = new Intent(
														getActivity(),
														MyWishActivity.class);
												intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												startActivity(intent);
												getActivity().finish();
											} else if (type.equals("1")) {//sssssssssssssss
//												Intent intent = new Intent(
//														getActivity(),
//														MyFreeCommentActivity.class);
//												intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//												startActivity(intent);
//												getActivity().finish();
												Bundle bundle = new Bundle();
												Intent intent = new Intent(
														TootooPlusApplication.getAppContext(),
														HomeActivity.class);
												intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												bundle.putInt("tab_index", ConstantsTooTooEHelper.TAB_FOUR);
												intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
												startActivity(intent);
												getActivity().finish();
											}
										}

									} else {

										ComponentUtil.showToast(
												TootooPlusApplication
														.getAppContext(),
												getActivity()
														.getResources()
														.getString(
																R.string.push_error));

									}
								}

							} catch (JSONException e) {
								LogUtil.error("CreateStoryFragment",
										e.toString());
								e.printStackTrace();
							}
						}

					}

					@Override
					public void onFailure(HttpException error, String msg) {
						closeProgressDialogFragment();
						if (isAdded()) {
							ComponentUtil
									.showToast(
											getActivity(),
											getResources()
													.getString(
															R.string.errcode_network_response_timeout));
						}

					}
				});

	}

	/**
	 * 
	 * @Title: saveDraftToJson
	 * @Description: 将数据转化为json 串
	 * @param
	 * @return
	 * @throws
	 */
	private void saveDraftToJson() {
		int centerPalyX = CommonUtil.getWidth(TootooPlusApplication
				.getAppContext()) / 2;
		Gson gson = new Gson();
		// 存储本地数据
		// 存储与服务器交互的数据
		List<StoryDetailListBean> listRespn = mItemAdapter.mListDragBean;
		if (listRespn != null) {
			List<PageBean> pageList = new ArrayList<PageBean>();
			int datasize = listRespn.size();
			if (datasize > 0) {//
				// 集合转化json 保存
				int datatsize = listRespn.size();
				for (int i = 0; i < datatsize; i++) {
					StoryDetailListBean trbean = listRespn.get(i);
					PageBean pageBean = new PageBean();
					View positionView = mCoolDragAndDropGridView.getChildAt(i);
					final LayoutParams lp = (LayoutParams) positionView
							.getLayoutParams();
					int location = trbean.getLocation();
					int elementType = trbean.getElementType();
					if (oldBean != null) {// 如果不是第一条那么第一条如果是长方形
						int oldElement = oldBean.getElementType();
						int oldLocation = oldBean.getLocation();
						if (oldElement == 1 ||oldElement == 3||oldElement == 4|| oldElement == 0) {// 长方形
							itemIndex = itemIndex + 1;
							pageBean.setItemIndex(itemIndex);// 追加到后边
							trbean.setItemIndex(itemIndex);
						} else {// 上一条数据是正方形
							if (oldLocation == 1) {
								if (elementType == 2) {// 当前的是否是2？ 如果是
									pageBean.setItemIndex(itemIndex);// 追加到后边
									trbean.setItemIndex(itemIndex);
								} else if (elementType == 0) {// 当前的是长方形
									itemIndex = itemIndex + 1;
									pageBean.setItemIndex(itemIndex);// 追加到后边
									trbean.setItemIndex(itemIndex);
									trbean.setElementType(elementType);
								} else {
									itemIndex = itemIndex + 1;
									pageBean.setItemIndex(itemIndex);// 追加到后边
									trbean.setItemIndex(itemIndex);
								}
							} else {// 第二个位置
								itemIndex = itemIndex + 1;
								pageBean.setItemIndex(itemIndex);// 追加到后边
								trbean.setItemIndex(itemIndex);
							}

						}

					} else {// 是第一条数据
						if (elementType == 1||elementType == 3||elementType == 4) {// 长方形
							pageBean.setItemIndex(itemIndex);
							trbean.setItemIndex(itemIndex);
						} else {
							pageBean.setItemIndex(itemIndex);
							trbean.setItemIndex(itemIndex);
						}

					}

					oldBean = trbean;// 把当前的复制给老的

					LogUtil.systemlogError("我发布时候的row1", trbean.getItemIndex()
							+ "");

					if (positionView != null) {
						Rect rect = CommonUtil.getRect(positionView);
						int centerViewX = rect.centerX();
						if (centerViewX < centerPalyX) {
							trbean.setLocation(1);

							pageBean.setLocation(1);

						} else if (centerViewX == centerPalyX) {
							trbean.setLocation(1);
							pageBean.setLocation(1);
						} else if (centerViewX > centerPalyX) {
							trbean.setLocation(2);
							pageBean.setLocation(2);
						} else {
							LogUtil.systemlogError("拖动的时候centerViewX=",
									centerViewX + "");
						}
						LogUtil.systemlogError(
								"上传参数值",
								"itemindex=" + trbean.getItemIndex()
										+ "location=" + trbean.getLocation()
										+ "elementType="
										+ trbean.getElementType() + "imageurl"
										+ trbean.getPageImg());
					}

					LogUtil.systemlogInfo("transbean spanx", trbean.spanX);
					String pageid = trbean.getPageId();
					pageBean.setPageId(pageid);
					int xsan = trbean.spanX;
					LogUtil.systemlogInfo("spanx", xsan);
					pageBean.setElementType(trbean.getElementType());
					pageBean.setLocation(trbean.getLocation());
					pageBean.setTemplate("1");
					trbean.setTemplate("1");
					pageBean.setItemIndex(trbean.getItemIndex());
					pageList.add(pageBean);
				}
			} else {
				// json 是“”

			}

			LogUtil.systemlogInfo("responsedata  size", listRespn.size());

			String json = gson.toJson(pageList);
			LogUtil.systemlogInfo("json 是==", json);
			savestoryPageSory(storyid, json);// 保存结构
		} else {
			//
			savetoDraftOrPush();

		}
	}

	@Override
	public void onUpdateStoryIdListener(String storyId, String updateStoryId) {
		this.updateStoryId = updateStoryId;
		this.storyid = storyId;
		sortStoryId();
	}

	private void justIsCommentOrWish(Bundle bundle) {
		bundle.putString(TootooeNetApiUrlHelper.TYPE, type);

		if (!TextUtils.isEmpty(activity_create_Id)) {
			bundle.putString(TootooeNetApiUrlHelper.ACTIVITY_CREATE_ID,
					activity_create_Id);
		} else {
			bundle.putString(TootooeNetApiUrlHelper.STORY_CREATE_ID, "");
		}

		if (!TextUtils.isEmpty(storyCreateId)) {
			bundle.putString(TootooeNetApiUrlHelper.STORY_CREATE_ID,
					storyCreateId);

		} else {
			bundle.putString(TootooeNetApiUrlHelper.STORY_CREATE_ID, "");
		}
	}

	@Override
	public void setOnPushPar(String ratingCount) {
		this.ratingCount = ratingCount;
		// s
		pushParData();

	}

	/**
	 * 
	 * @Title: justIsPushComment
	 * @Description: 判断是否可以发布点评
	 * @param
	 * @return
	 * @throws
	 */
	protected boolean justIsPushComment() {
		boolean isCanPush = false;
		if (mItemAdapter != null) {
			List<String> listString = new ArrayList<String>();
			List<StoryDetailListBean> dropAdapter = mItemAdapter.mListDragBean;
//			PhotoSelectOrConvertBean convert = mItemAdapter.mConvertAndListWishBean;
//			if (convert != null) {
//				ConVertBean convertBean = convert.getConvertBean();
//				String videoUrl = convertBean.getStoryVideoUrl();
//				if (TextUtils.isEmpty(videoUrl)) {
//					listString.add("2");// 如果不是videourl那么就添加2元素
//
//				}
//
//			}
			// && dropAdapter.size() >=
			// ConstantsTooTooEHelper.CREATE_COMMENT_COUNT
			//
			if (dropAdapter != null) {
				for (StoryDetailListBean storyDetailListBean : dropAdapter) {
					String pageType = storyDetailListBean.getPageType();
					if (!TextUtils.isEmpty(pageType) && pageType.equals("2")) {// 图片
						listString.add("2");
					}

				}
				if (listString.size() >= ConstantsTooTooEHelper.CREATE_COMMENT_COUNT) {
					isCanPush = true;
				}

			} else {
				isCanPush = false;
			}
		}
		return isCanPush;
	}

	@Override
	public void onScroolState(ScrollView mScrollView) {
		HashMap<String, Integer> hashMap = mConvertAndListWishBean.getmHashMap();
		if(hashMap!=null){
			int insertPosition = hashMap.get(ConstantsTooTooEHelper.POSITION_KEY);
			if(insertPosition==ConstantsTooTooEHelper.NO_ITEM_CLICK_INSERT_CODE){
				mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}else{
					if (mItemAdapter != null) {
						View viewPosition = mCoolDragAndDropGridView.getChildAt(insertPosition+1);
						float y = viewPosition.getY();
//						mScrollView.scrollTo(0, (int) y);//初始化的时候无效
						mScrollView.smoothScrollTo(0, (int) y);
				}

			}
		}else{
			mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
		}
	
		
	}
}
