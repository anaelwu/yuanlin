package com.ninetowns.tootooplus.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.adapter.GroupFreeCommentAdapter;
import com.ninetowns.tootooplus.bean.ActivityHistoryBean;
import com.ninetowns.tootooplus.bean.CommentHistoryBean;
import com.ninetowns.tootooplus.bean.FreeCommentBean;
import com.ninetowns.tootooplus.fragment.HistoryFragmentDialog.OnSearchListener;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.ui.widget.refreshable.PullToRefreshBase;
import com.ninetowns.ui.widget.refreshable.PullToRefreshBase.Mode;
import com.ninetowns.ui.widget.refreshable.RefreshableListView;

/**
 * 
 * @ClassName: CommentFreePageFragment
 * @Description: 搜索点评
 * @author wuyulong
 * @date 2015-3-13 上午10:18:16
 * 
 */
public class CommentFreePageFragment extends GroupFreeCommentFragment implements OnSearchListener,OnClickListener{
	private HistoryFragmentDialog fragment;
	private String searchActName;
	private View mHomePageFragmentView;
	@ViewInject(R.id.ll_se_right)
	private LinearLayout mLLSearch;// 搜索
	@ViewInject(R.id.et_search)
	private EditText mEditext;// 搜索输入框
	@ViewInject(R.id.ibtn_se_left)
	private ImageButton mLLBack;// 返回
	@ViewInject(R.id.fl_history)
	private FrameLayout mFLHistory;
	private RefreshableListView refresh;
	private ListView mHomePageRefreshListView;
	@Override
	protected View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mHomePageFragmentView = inflater.inflate(
				R.layout.search_home_page_fragment, null);
		ViewUtils.inject(this, mHomePageFragmentView); // 注入view和事件
		setViewListener();
		return mHomePageFragmentView;
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
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		showHisToryDialog();
		super.onActivityCreated(savedInstanceState);
		
	}
	@Override
	protected boolean isGetData() {
		return false;
	}
	@Override
	public void setDifferentParam(RequestParamsNet requestParamNet) {
		if (!TextUtils.isEmpty(searchActName)) {
			requestParamNet.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYNAME, searchActName);// 分类筛选id
		}
		
	}

	@Override
	public void onItemClickToSkip(View view, int position,
			List<FreeCommentBean> parserData) {
		
	}
	@Override
	protected PullToRefreshBase<ListView> initRefreshIdView() {
		refresh = (RefreshableListView) mHomePageFragmentView
				.findViewById(R.id.refresh_home_page_list);
		refresh.setRefreshing(false);

		mHomePageRefreshListView = refresh.getRefreshableView();
		refresh.setMode(Mode.BOTH);
		mHomePageRefreshListView.setFastScrollEnabled(false);
		return refresh;
	}
	private void showHisToryDialog() {

		FragmentManager fragmentManager = getActivity()
				.getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		fragment = new HistoryFragmentDialog("isComment", mFLHistory, refresh);
		fragment.setOnSearchListener(this);
		if (fragmentManager != null) {
			// 屏幕较小，以全屏形式显示
			// 指定一个过渡动画
			transaction.add(R.id.fl_history, fragment);
			transaction.commitAllowingStateLoss();
		}

	}

	@Override
	public void OnSearchListener(String name) {
		searchActName = name;
		if(null!=fragment){
			mFLHistory.setVisibility(View.GONE);
			refresh.setVisibility(View.VISIBLE);
		}else{
			mFLHistory.setVisibility(View.VISIBLE);
			refresh.setVisibility(View.GONE);
		}
		super.onLoadData(true, true, true);

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

			CommentHistoryBean history = new CommentHistoryBean();
			history.setHistoryName(strName);
			try {
				CommentHistoryBean searHistoryBean = dbUtils.findFirst(Selector.from(
						ActivityHistoryBean.class).where("historyName", "=", strName));
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
	protected void initAdapter(List<FreeCommentBean> parserResult,List<FreeCommentBean> moreResult) {
		int moreSize=moreResult.size();
		GroupFreeCommentAdapter wishAdatper = new GroupFreeCommentAdapter(getActivity(),
				parserResult);
		mHomePageRefreshListView.setAdapter(wishAdatper);
		if (super.currentpage != 1) {
			mHomePageRefreshListView.setSelection(parserResult.size() -moreSize + 1);
		}
		wishAdatper.notifyDataSetChanged();
	}
}