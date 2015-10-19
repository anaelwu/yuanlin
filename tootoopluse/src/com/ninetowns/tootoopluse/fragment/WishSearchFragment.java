package com.ninetowns.tootoopluse.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.activity.WishDetailActivity;
import com.ninetowns.tootoopluse.adapter.WishHomeAdapter;
import com.ninetowns.tootoopluse.application.TootooPlusEApplication;
import com.ninetowns.tootoopluse.bean.HistoryBean;
import com.ninetowns.tootoopluse.bean.WishBean;
import com.ninetowns.tootoopluse.fragment.HistoryFragmentDialog.OnSearchListener;
import com.ninetowns.tootoopluse.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootoopluse.helper.SharedPreferenceHelper;
import com.ninetowns.tootoopluse.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootoopluse.parser.WishParser;
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
public class WishSearchFragment extends
		PageListFragment<ListView, List<WishBean>, WishParser> implements
		View.OnClickListener, OnItemClickListener, OnSearchListener {
	private View mWishFragmentView;
	private ListView mWishRefreshListView;// 刷新的listView
	private int totalPage;// 总页数
	private List<WishBean> wishList = new ArrayList<WishBean>();// 当前数据列表集合
	private String wishName;
	@ViewInject(R.id.ll_se_right)
	private LinearLayout mLLSearch;// 搜索
	@ViewInject(R.id.et_search)
	private EditText mEditext;// 搜索输入框
	@ViewInject(R.id.ibtn_se_left)
	private ImageButton mLLBack;// 返回
	@ViewInject(R.id.fl_history)
	private FrameLayout mFLHistory;
	private HistoryFragmentDialog fragment;
	private RefreshableListView refresh;

	@SuppressLint("InflateParams")
	@Override
	protected View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mWishFragmentView = inflater.inflate(R.layout.wish_search_fragment,
				null);
		ViewUtils.inject(this, mWishFragmentView); // 注入view和事件
		mEditext.requestFocus();
		setViewListener();
		return mWishFragmentView;
	}

	/**
	 * 
	 * @Title: showHisToryDialog
	 * @Description: 展示dialog
	 * @param
	 * @return
	 * @throws
	 */
	private void showHisToryDialog() {

		FragmentManager fragmentManager = getActivity()
				.getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		fragment = new HistoryFragmentDialog("isWish", mFLHistory, refresh);
		fragment.setOnSearchListener(this);
		if (fragmentManager != null) {
			// 屏幕较小，以全屏形式显示

			// 指定一个过渡动画
			transaction.add(R.id.fl_history, fragment);
			transaction.commitAllowingStateLoss();
		}

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
		mLLSearch.setOnClickListener(this);
		mLLBack.setOnClickListener(this);
	}

	/**
	 * @Title: createSearch
	 * @Description: 搜索 、存储、
	 * @param
	 * @return
	 * @throws
	 */
	private void createSearch() {
		String strName = mEditext.getText().toString();
		searchHistory(strName);

	}

	private void searchHistory(String strName) {
		if (!TextUtils.isEmpty(strName)) {
			DbUtils dbUtils = DbUtils.create(getActivity());

			HistoryBean history = new HistoryBean();
			history.setHistoryName(strName);
			try {
				HistoryBean searHistoryBean = dbUtils.findFirst(Selector.from(
						HistoryBean.class).where("historyName", "=", strName));
				if (searHistoryBean != null) {
					dbUtils.delete(searHistoryBean);
					dbUtils.save(history);
				} else {
					dbUtils.save(history);
				}

			} catch (DbException e) {
				e.printStackTrace();
			}
			OnSearchListener(strName);

		} else {
			ComponentUtil.showToast(getActivity(), "请输入搜索内容");
		}
	}

	@Override
	protected PullToRefreshBase<ListView> initRefreshIdView() {
		refresh = (RefreshableListView) mWishFragmentView
				.findViewById(R.id.refresh_home_page_list);
		refresh.setRefreshing(false);
		mWishRefreshListView = refresh.getRefreshableView();
		refresh.setMode(Mode.DISABLED);
		PauseOnScrollListener listener = new PauseOnScrollListener(
				ImageLoader.getInstance(), true, true);
		refresh.setOnScrollListener(listener);
		mWishRefreshListView.setFastScrollEnabled(false);
		mWishRefreshListView.setOnItemClickListener(this);
		return refresh;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// loadData();
		showHisToryDialog();
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
				.getLoginUserId(TootooPlusEApplication.getAppContext());
		requestPar.setmStrHttpApi(TootooeNetApiUrlHelper.WISH_LIST_API);
		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.USER_ID,
				userid);
		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.PAGE,
				String.valueOf(currentpage));
		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.PAGE_SIZE,
				String.valueOf(TootooeNetApiUrlHelper.PAGESIZE_DRAFT));// 默认每页6条
		if (!TextUtils.isEmpty(wishName)) {
			requestPar.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORY_NAME, wishName);// 分类筛选id
		}

		// requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.CATEGORY_PARENTID,
		// cateGoryParentId);// 分类筛选id

		return requestPar;
	}

	@Override
	public int getTotalPage() {
		return totalPage;
	}

	@Override
	public void getPageListParserResult(List<WishBean> parserResult) {
		if (super.currentpage == 1) {
			this.wishList.clear();
		}
		wishList.addAll(parserResult);
		if (wishList.size() > 0) {
			WishHomeAdapter wishAdatper = new WishHomeAdapter(getActivity(),
					wishList);
			mWishRefreshListView.setAdapter(wishAdatper);
			if (super.currentpage != 1) {
				mWishRefreshListView.setSelection(this.wishList.size() / 2 + 1);
			}
			wishAdatper.notifyDataSetChanged();
		} else {
			ComponentUtil.showToast(TootooPlusEApplication.getAppContext(),
					"没有要筛选的数据");
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_se_right:
			// 创建一个数据库
			createSearch();
			break;
		case R.id.ibtn_se_left:
			getActivity().finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// position=1
		if (position != -1) {
			WishBean selectionItem = wishList.get(position - 1);
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

	@Override
	public void OnSearchListener(String name) {
		wishName = name;
		if (null != fragment) {
			mFLHistory.setVisibility(View.GONE);
			refresh.setVisibility(View.VISIBLE);
		} else {
			mFLHistory.setVisibility(View.VISIBLE);
			refresh.setVisibility(View.GONE);
		}
		if(!TextUtils.isEmpty(name)){
			refresh.setRefreshing(true);
			refresh.setMode(Mode.BOTH);
			super.onLoadData(true, true, true);
		}
		

	}
}
