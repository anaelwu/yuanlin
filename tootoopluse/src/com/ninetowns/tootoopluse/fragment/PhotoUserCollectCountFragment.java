package com.ninetowns.tootoopluse.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.adapter.GridViewPhotoAdapter;
import com.ninetowns.tootoopluse.application.TootooPlusEApplication;
import com.ninetowns.tootoopluse.bean.GridViewPhotoBean;
import com.ninetowns.tootoopluse.helper.SharedPreferenceHelper;
import com.ninetowns.tootoopluse.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootoopluse.parser.PhotoWishParser;
import com.ninetowns.ui.fragment.PageListFragment;
import com.ninetowns.ui.widget.refreshable.PullToRefreshBase;
import com.ninetowns.ui.widget.refreshable.PullToRefreshBase.Mode;
import com.ninetowns.ui.widget.refreshable.RefreshableGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * 
 * @ClassName: PhotoUserDialogFragment
 * @Description: 白吃用户列表
 * @author wuyulong
 * @date 2015-2-5 下午4:20:56
 * 
 */
public class PhotoUserCollectCountFragment extends
		PageListFragment<GridView, List<GridViewPhotoBean>, PhotoWishParser>
		implements View.OnClickListener {
	private View mWishFragmentView;
	@ViewInject(R.id.gv_photo)
	private RefreshableGridView mWishRefreshGridView;// 刷新的GridView
	private GridView mGridView;
	private int totalPage;// 总页数
	private List<GridViewPhotoBean> wishList = new ArrayList<GridViewPhotoBean>();// 当前数据列表集合
	private View gridViewPhoto;
	private View viewDissMiss;
	@ViewInject(R.id.ll_dissmiss)
	private LinearLayout mDissMiss;
	private String activityId;

	public PhotoUserCollectCountFragment(View viewDissMiss,String storyid) {
		this.viewDissMiss = viewDissMiss;
		this.activityId=storyid;
	
	}

	@Override
	protected View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		gridViewPhoto = inflater.inflate(R.layout.gridview_photo, null);
		ViewUtils.inject(this, gridViewPhoto);
		setViewListener();
		return gridViewPhoto;
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
		mDissMiss.setOnClickListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected PullToRefreshBase<GridView> initRefreshIdView() {
		mGridView = mWishRefreshGridView.getRefreshableView();
		mWishRefreshGridView.setMode(Mode.DISABLED);
		PauseOnScrollListener listener = new PauseOnScrollListener(
				ImageLoader.getInstance(), true, true);
		mWishRefreshGridView.setOnScrollListener(listener);
		return mWishRefreshGridView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
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
		super.onLoadData(true, true, false);
	}

	@Override
	public PhotoWishParser setParser(String str) {
		PhotoWishParser wishParser = new PhotoWishParser(str);
		totalPage = wishParser.getTotalPage();
		return wishParser;

	}

	@Override
	public RequestParamsNet getApiParmars() {
		// CategoryId：分类筛选
		RequestParamsNet requestPar = new RequestParamsNet();
		String userid = SharedPreferenceHelper
				.getLoginUserId(TootooPlusEApplication.getAppContext());
		requestPar.setmStrHttpApi(TootooeNetApiUrlHelper.COLLECT_COUNT);
		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.ACTIVITYID,activityId);
		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.PAGE,
				String.valueOf(currentpage));
		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.PAGE_SIZE,
				String.valueOf(TootooeNetApiUrlHelper.PAGESIZE_DRAFT_PHOTO));// 默认每页6条

		return requestPar;
	}

	@Override
	public int getTotalPage() {
		return totalPage;
	}

	@Override
	public void getPageListParserResult(List<GridViewPhotoBean> parserResult) {
		if (super.currentpage == 1) {
			this.wishList.clear();
		}
		wishList.addAll(parserResult);
		if (wishList.size() > 0) {
			GridViewPhotoAdapter wishAdatper = new GridViewPhotoAdapter(getActivity(),
					wishList);
			mGridView.setAdapter(wishAdatper);
			if (super.currentpage != 1) {
				mGridView.setSelection(this.wishList.size() / 2 + 1);
			}
			wishAdatper.notifyDataSetChanged();
		} else {
			ComponentUtil.showToast(TootooPlusEApplication.getAppContext(),
					"没有数据");
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_dissmiss:
			viewDissMiss.setVisibility(View.GONE);
			break;
		
		}

	}

}