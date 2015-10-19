package com.ninetowns.tootooplus.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.library.util.ImageUtil;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.activity.CoverRecommendSelectActivity;
import com.ninetowns.tootooplus.activity.InternetBrowserActivity;
import com.ninetowns.tootooplus.activity.PhotoCameraActivity;
import com.ninetowns.tootooplus.activity.RecordVideoActivity;
import com.ninetowns.tootooplus.activity.SearchActivity;
import com.ninetowns.tootooplus.activity.WishDetailActivity;
import com.ninetowns.tootooplus.adapter.WishHomeAdapter;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.bean.WishBean;
import com.ninetowns.tootooplus.fragment.BaseShaiXuanDialog.OnSelectedListener;
import com.ninetowns.tootooplus.fragment.GoodsCateGoryDialogFragment.OnCategoryGoods;
import com.ninetowns.tootooplus.helper.ArcMenu;
import com.ninetowns.tootooplus.helper.ArcMenu.OnMenuItemClickListener;
import com.ninetowns.tootooplus.helper.ArcMenu.Status;
import com.ninetowns.tootooplus.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootooplus.parser.WishParser;
import com.ninetowns.tootooplus.util.CommonUtil;
import com.ninetowns.ui.fragment.PageListFragment;
import com.ninetowns.ui.widget.refreshable.PullToRefreshBase;
import com.ninetowns.ui.widget.refreshable.PullToRefreshBase.Mode;
import com.ninetowns.ui.widget.refreshable.RefreshableListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * 
 * @ClassName: WishFragment
 * @Description: 心愿列表
 * @author wuyulong
 * @date 2015-1-23 下午3:44:09
 * 
 */
public class WishFragment extends
		PageListFragment<ListView, List<WishBean>, WishParser> implements
		View.OnClickListener, OnCategoryGoods, OnItemClickListener,
		OnSelectedListener {
	private View mWishFragmentView;
	private ListView mWishRefreshListView;// 刷新的listView
	private int totalPage;// 总页数
	private List<WishBean> wishList = new ArrayList<WishBean>();// 当前数据列表集合
	@ViewInject(R.id.ibtn_left)
	private ImageButton mIbtnSelect;// 筛选
	@ViewInject(R.id.arc_menu)
	private ArcMenu mArcmenuStory;// 创建故事心愿
	@ViewInject(R.id.tv_title)
	private CheckedTextView mTvTitle;
	@ViewInject(R.id.id_button)
	private ImageView mIVAdd;
	@ViewInject(R.id.rl_arcmenu)
	private RelativeLayout mRelativeMenu;
	@ViewInject(R.id.ll_middle)
	private LinearLayout mLLSearch;
	private PopupWindow popupWindow;
	private int screen_width;
	private int screen_height;
	private String sortParams = "";// 排序方式（1最新，2最热，3推荐）
	private String cateGoryId = "";// 子分类筛选
	private String publisher = "";// 发布者
	private String cateGoryParentId = "";// 付分类
	private String oldSortParams = "";
	private String wishName;
	private String activity_create_Id;
	private String storyCreateId;
	private String type;
	private WishShaiXuanDialog fragment;

	/** */

	private WishHomeAdapter wishAdatper;

	@SuppressLint("InflateParams")
	@Override
	protected View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mWishFragmentView = inflater.inflate(R.layout.wish_fragment, null);
		ViewUtils.inject(this, mWishFragmentView); // 注入view和事件
		mIbtnSelect.setImageResource(R.drawable.goodsgreed);
		setViewListener();
		showCreateWishDialog();
		popupWindow = new PopupWindow();
		// 屏幕宽度
		screen_width = CommonUtil.getWidth(getActivity());
		// 屏幕高度
		screen_height = CommonUtil.getHeight(getActivity());

		mArcmenuStory.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public void onClick(View view, int pos) {
				// ComponentUtil.showToast(getActivity(), "点击"+pos);
				switch (pos) {
				case 0:
					intetnetWish();
					break;
				case 1:// 图潘
					cameraWish();
					break;
				case 2:// 图片

					photoWish();
					break;
				case 3:// 视频
					createVideo();
					break;

				default:
					break;
				}

			}

			@Override
			public void getStatus(Status status) {
				if (status == Status.CLOSE) {
					mIVAdd.setImageResource(R.drawable.btn_add_gray);
					// mArcmenuStory.setVisibility(View.GONE);
					// mArcmenuStory.setBackgroundColor(getActivity()
					// .getResources().getColor(R.color.transparent));
					mRelativeMenu.setBackgroundResource(R.color.transparent);
				} else {
					// mArcmenuStory.setVisibility(View.VISIBLE);
					mIVAdd.setImageResource(R.drawable.btn_create_act);
					mRelativeMenu
							.setBackgroundResource(R.color.halftransparent);
					// mArcmenuStory.setBackgroundColor(getActivity()
					// .getResources().getColor(R.color.halftransparent));
				}

			}
		});
		return mWishFragmentView;
	}

	/**
	 * 
	 * @Title: showCreateWishDialog
	 * @Description: 显示心愿dialog
	 * @param
	 * @return
	 * @throws
	 */
	private void showCreateWishDialog() {
		boolean isFirst = SharedPreferenceHelper
				.getFirstGuideCreateWish(getActivity());
		if (isFirst) {// 如果第一次
			CommonUtil.showFirstGuideDialog(getActivity(),
					ConstantsTooTooEHelper.FIRST_GUIDE_CREATE_WISH);
		}

	}

	/**
	 * 
	 * @Title: setOnClickSearch
	 * @Description: 搜索
	 * @param
	 * @return
	 * @throws
	 */
	@OnClick(R.id.ll_middle)
	public void setOnClickSearch(View v) {

		skipToSearchWish();

	}

	/**
	 * 
	 * @Title: showHisToryDialog
	 * @Description: 展示dialog
	 * @param
	 * @return
	 * @throws
	 */
	private void skipToSearchWish() {
		Intent intent = new Intent(getActivity(), SearchActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("searchType", "isWish");
		startActivity(intent);

	}

	/**
	 * 
	 * @Title: setViewListener
	 * @Description: 注册点击事件
	 * @param
	 * @return void
	 * @throws
	 */
	private void setViewListener() {
		mIbtnSelect.setOnClickListener(this);

	}

	@Override
	protected PullToRefreshBase<ListView> initRefreshIdView() {
		RefreshableListView refresh = (RefreshableListView) mWishFragmentView
				.findViewById(R.id.refresh_home_page_list);
		refresh.setRefreshing(true);
		mWishRefreshListView = refresh.getRefreshableView();
		refresh.setMode(Mode.BOTH);
		PauseOnScrollListener listener = new PauseOnScrollListener(
				ImageLoader.getInstance(), true, true);
		refresh.setOnScrollListener(listener);
		wishList = new ArrayList<WishBean>();
		wishAdatper = new WishHomeAdapter(getActivity(), wishList);
		mWishRefreshListView.setAdapter(wishAdatper);
		mWishRefreshListView.setFastScrollEnabled(false);
		mWishRefreshListView.setOnItemClickListener(this);
		return refresh;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		this.sortParams = "0";
		loadData();
		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * 
	 * @Title: loadData
	 * @Description: 加载数据
	 * @param
	 * @return
	 * @throws
	 */
	public void loadData() {
		super.onLoadData(true, true, true);
	}

	@Override
	public WishParser setParser(String str) {
		WishParser wishParser = new WishParser(str);
		totalPage = wishParser.getTotalPage();
		return wishParser;

	}

	@Override
	public RequestParamsNet getApiParmars() {
		// CategoryId：分类筛选
		RequestParamsNet requestPar = new RequestParamsNet();
		String userid = SharedPreferenceHelper
				.getLoginUserId(TootooPlusApplication.getAppContext());
		requestPar.setmStrHttpApi(TootooeNetApiUrlHelper.WISH_LIST_API);
		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.USER_ID,
				userid);
		// if(!TextUtils.isEmpty(oldSortParams)){
		// if (oldSortParams.equals(sortParams)) {
		// requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.PAGE,
		// String.valueOf(currentpage));
		// } else {
		// currentpage = 1;// 如果和old不一样那么就重置
		// requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.PAGE,
		// String.valueOf(currentpage));
		// }
		// }else{
		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.PAGE,
				String.valueOf(currentpage));
		// }

		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.PAGE_SIZE,
				String.valueOf(TootooeNetApiUrlHelper.PAGESIZE_DRAFT));// 默认每页6条
		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.PUBLISHER,
				publisher);// 1商家 和个体户
		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.SORT,
				sortParams);// Sort：排序方式（1最新，2最热，3推荐）
		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.CATEGORYID,
				cateGoryId);// 分类筛选id
		if (!TextUtils.isEmpty(wishName)) {
			requestPar.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORY_NAME, wishName);// 分类筛选id
			wishName = null;// 重置null
		}

		requestPar.addQueryStringParameter(
				TootooeNetApiUrlHelper.CATEGORY_PARENTID, cateGoryParentId);// 分类筛选id

		return requestPar;
	}

	@Override
	public int getTotalPage() {
		return totalPage;
	}

	@Override
	public void getPageListParserResult(List<WishBean> parserResult) {
		if (super.currentpage == 1) {
			wishAdatper.clear();
			this.wishList.clear();
		}
		if (parserResult != null) {
			// int moreSize = parserResult.size();
			// wishList.addAll(parserResult);
			this.wishList = parserResult;
			if (wishList.size() > 0) {
				wishAdatper.updateNotify(wishList);
				if (super.currentpage == totalPage)
					return;
				if (super.currentpage != 1) {
					mWishRefreshListView.setSelection((super.currentpage - 1)
							* TootooeNetApiUrlHelper.PAGESIZE_DRAFT +1);
				}else {
					mWishRefreshListView.smoothScrollToPosition(0);
				}
				// wishAdatper.notifyDataSetChanged();
			} else {
				ComponentUtil.showToast(TootooPlusApplication.getAppContext(),
						"没有要筛选的数据");
				// wishAdatper.notifyDataSetChanged();
				wishAdatper.updateNotify(wishList);
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:// 筛选
			showCateGoryDialog();

			break;
		case R.id.arc_menu:// 创建
			String userid = SharedPreferenceHelper
					.getLoginUserId(TootooPlusApplication.getAppContext());
			if (!TextUtils.isEmpty(userid)) {
				setAuthentication();
			} else {
				ComponentUtil.showToast(TootooPlusApplication.getAppContext(),
						"没有登陆");
			}

			break;

		}

	}

	/************ 显示分类页 *****************/
	private void showCateGoryDialog() {

		FragmentManager fragmentManager = getActivity()
				.getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		if (fragment != null) {
			if (fragment.isVisible()) {
				fragment.dismiss();
			} else {
				fragment.show(fragmentManager, "dialog");
			}

		} else {
			fragment = new WishShaiXuanDialog();
			fragment.setOnSelectedListener(this);
			if (fragmentManager != null) {
				// 屏幕较小，以全屏形式显示

				// 指定一个过渡动画
				transaction
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				transaction.addToBackStack(null);
				transaction.attach(fragment);
				fragment.show(fragmentManager, "dialog");
				transaction.commitAllowingStateLoss();
			}

		}

	}

	/**
	 * 
	 * @Title: setAuthentication
	 * @Description: 需要商家通过店铺认证才能创建
	 * @param
	 * @return void
	 * @throws
	 */
	private void setAuthentication() {
		setSelectCreateType();
	}

	/**
	 * 
	 * @Title: setSelectCreateType
	 * @Description: 选择创建故事的类型
	 * @param
	 * @return
	 * @throws
	 */
	private void setSelectCreateType() {
		showSelectPopup();
	}

	/**
	 * 
	 * @Title: showSelectPopup
	 * @Description:选择创建故事方式弹出面板
	 * @param
	 * @return void
	 * @throws
	 */
	public void showSelectPopup() {

		/*
		 * <com.capricorn.ArcMenu android:id="@+id/arc_menu_2"
		 * android:layout_width="wrap_content"
		 * android:layout_height="wrap_content"
		 * arc:fromDegrees="@dimen/menuFromDegrees"
		 * arc:toDegrees="@dimen/menuToDegrees"
		 * arc:childSize="@dimen/menuChildSize"/>
		 */

		if (!popupWindow.isShowing()) {
			View view = LayoutInflater.from(getActivity()).inflate(
					R.layout.create_wish_bottom_popup_layout, null);
			popupWindow.setContentView(view);
			// 窗口的宽带和高度根据情况定义
			popupWindow.setWidth(screen_width * 9 / 10);
			popupWindow.setHeight(screen_height / 3);

			popupWindow.setFocusable(true);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setBackgroundDrawable(new ColorDrawable(0));

			// 窗口进入和退出的效果
			popupWindow.setAnimationStyle(R.style.win_ani_top_bottom);

			popupWindow.showAtLocation(LayoutInflater.from(getActivity())
					.inflate(R.layout.home_activity, null),
					// 位置可以按要求定义
					Gravity.NO_GRAVITY, screen_width / 20,
					screen_height * 4 / 5 - 15);

			LinearLayout rec_bot_popup_total_layout = (LinearLayout) view
					.findViewById(R.id.rec_bot_popup_total_layout);
			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) rec_bot_popup_total_layout
					.getLayoutParams();
			linearParams.height = screen_height / 3;
			rec_bot_popup_total_layout.setLayoutParams(linearParams);

			LinearLayout llCreateWishPhoto = (LinearLayout) view
					.findViewById(R.id.ll_create_wish_photo);
			llCreateWishPhoto.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 拍照
					cameraWish();
				}

			});

			LinearLayout llCreateWishVideo = (LinearLayout) view
					.findViewById(R.id.ll_create_wish_video);
			llCreateWishVideo.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// 拍视频
					createVideo();
					popupWindow.dismiss();

				}

			});

			LinearLayout llCreateWishAlbum = (LinearLayout) view
					.findViewById(R.id.ll_create_wish_album);

			llCreateWishAlbum.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// 相册
					photoWish();

				}

			});

			LinearLayout llCreateWishInternet = (LinearLayout) view
					.findViewById(R.id.ll_create_wish_internet);
			llCreateWishInternet.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					intetnetWish();
				}

			});
			LinearLayout llCreateWishCancel = (LinearLayout) view
					.findViewById(R.id.ll_create_wish_cancel);
			llCreateWishCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// 取消
					popupWindow.dismiss();
				}
			});
		}
	}

	/**
	 * @Title: cameraWish
	 * @Description: TODO
	 * @param
	 * @return
	 * @throws
	 */
	private void cameraWish() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri imageUri = Uri.fromFile(new File(ImageUtil.getTempPhotoPath()));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra(ImageColumns.ORIENTATION, 0);
		intent.putExtra("storyType", "2");// 封面图
		startActivityForResult(intent, 1);
		popupWindow.dismiss();
	}

	/**
	 * @Title: photoWish
	 * @Description: TODO
	 * @param
	 * @return
	 * @throws
	 */
	private void photoWish() {
		Intent intent3 = new Intent(getActivity(),
				CoverRecommendSelectActivity.class);// 封面图的activity
		Bundle bundle = new Bundle();
		justIsCommentOrWishBundle(bundle);

		ConstantsTooTooEHelper.putView(
				ConstantsTooTooEHelper.isConvertRecommendView, bundle);
		intent3.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
		intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent3);
		popupWindow.dismiss();
	}

	/**
	 * @Title: createVideo
	 * @Description: TODO
	 * @param
	 * @return
	 * @throws
	 */
	private void createVideo() {
		Intent intent1 = new Intent(getActivity(), RecordVideoActivity.class);
		Bundle bundle = new Bundle();
		justIsCommentOrWishBundle(bundle);
		ConstantsTooTooEHelper.putView(
				ConstantsTooTooEHelper.isConvertRecommendView, bundle);
		intent1.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
		intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent1);
	}

	/**
	 * @Title: intetnetWish
	 * @Description: TODO
	 * @param
	 * @return
	 * @throws
	 */
	private void intetnetWish() {
		// 网址
		Intent intent4 = new Intent(getActivity(),
				InternetBrowserActivity.class);// 封面图的activity
		Bundle bundle = new Bundle();
		justIsCommentOrWishBundle(bundle);
		ConstantsTooTooEHelper.putView(
				ConstantsTooTooEHelper.isConvertRecommendView, bundle);
		intent4.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
		intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent4);
		popupWindow.dismiss();
	}

	/**
	 * 
	 * @Title: skipToCreateWishActivity
	 * @Description: 跳转到创建心愿故事页
	 * @param
	 * @return void
	 * @throws
	 */
	private void skipToCreateWishActivity() {
		setSelectCreateType();
		// Intent intent = new Intent(this.getActivity(),
		// CreateWishActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(intent);
	}

	@Override
	public void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg0 == 1 && arg1 == Activity.RESULT_OK) {
			Bundle bundle = new Bundle();
			Intent intent = new Intent(this.getActivity(),
					PhotoCameraActivity.class);
			justIsCommentOrWishBundle(bundle);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			bundle.putString("picPath", ImageUtil.getTempPhotoPath());// 拍照图片的路径
			ConstantsTooTooEHelper.putView(
					ConstantsTooTooEHelper.isConvertRecommendView, bundle);
			intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
			startActivity(intent);
		}
	}

	@Override
	public void onPushGoodsListener(View push, String publisher, String sort,
			String cateGoryIdMain, String cateGoryIdSub) {
		this.publisher = publisher;
		oldSortParams = this.sortParams;// 把当前的记录起来
		this.sortParams = sort;// 把最新的赋值
		this.cateGoryParentId = cateGoryIdMain;
		this.cateGoryId = cateGoryIdSub;
		loadData();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// position=1
		if (position != -1) {
			WishBean selectionItem = wishAdatper.getWishList().get(position - 1);
			Intent intentToDetail = new Intent(getActivity(),
					WishDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(ConstantsTooTooEHelper.USERID,
					selectionItem.getUserId());
			bundle.putString(ConstantsTooTooEHelper.STORYID,
					selectionItem.getStoryId());
			intentToDetail.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentToDetail.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
			startActivity(intentToDetail);
		}

	}

	private void justIsCommentOrWishBundle(Bundle bundle) {
		if (!TextUtils.isEmpty(activity_create_Id)) {
			bundle.putString(TootooeNetApiUrlHelper.ACTIVITY_CREATE_ID,
					activity_create_Id);

		} else {
			bundle.putString(TootooeNetApiUrlHelper.ACTIVITY_CREATE_ID, "");
		}
		if (!TextUtils.isEmpty(storyCreateId)) {
			bundle.putString(TootooeNetApiUrlHelper.STORY_CREATE_ID,
					storyCreateId);
		} else {
			bundle.putString(TootooeNetApiUrlHelper.STORY_CREATE_ID, "");
		}
		bundle.putString(TootooeNetApiUrlHelper.TYPE, "0");
	}

	@Override
	public void OnSelectedListenerPar(String type) {
		// oldSortParams = this.sortParams;// 把当前的记录起来
		this.sortParams = type;// 把最新的赋值
		currentpage = 1;// 如果切换了那么每一次都初始化为1
		loadData();
	}

	// @Override
	// public void OnSearchListener(String name) {
	// wishName=name;
	// super.onLoadData(true, true, true);
	//
	// }

}