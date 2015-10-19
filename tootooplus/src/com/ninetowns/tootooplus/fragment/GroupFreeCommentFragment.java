package com.ninetowns.tootooplus.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.library.util.LogUtil;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.activity.HomeActivity;
import com.ninetowns.tootooplus.adapter.GroupFreeCommentAdapter;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.bean.FreeCommentBean;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootooplus.parser.FreeCommentParser;
import com.ninetowns.ui.fragment.PageListFragment;
import com.ninetowns.ui.widget.refreshable.PullToRefreshBase;
import com.ninetowns.ui.widget.refreshable.PullToRefreshBase.Mode;
import com.ninetowns.ui.widget.refreshable.RefreshableListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * 
 * @ClassName: GroupFreeCommentFragment
 * @Description: 最新、最热、推荐、搜索、筛选
 * @author wuyulong
 * @date 2015-4-16 下午5:08:43
 * 
 */
public abstract class GroupFreeCommentFragment extends
		PageListFragment<ListView, List<FreeCommentBean>, FreeCommentParser>
		implements OnItemClickListener {

	private int totalPage;
	private ListView mWishRefreshListView;
	private View mFreeFragmentView;
	private List<FreeCommentBean> parserResult = new ArrayList<FreeCommentBean>();

	@Override
	protected PullToRefreshBase<ListView> initRefreshIdView() {
		RefreshableListView refresh = (RefreshableListView) mFreeFragmentView
				.findViewById(R.id.refresh_group_comment);
		refresh.setRefreshing(true);
		mWishRefreshListView = refresh.getRefreshableView();
		refresh.setMode(Mode.BOTH);
		PauseOnScrollListener listener = new PauseOnScrollListener(
				ImageLoader.getInstance(), true, true);
		refresh.setOnScrollListener(listener);
		mWishRefreshListView.setFastScrollEnabled(false);
		mWishRefreshListView.setOnItemClickListener(this);
		return refresh;
	}

	@Override
	protected View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mFreeFragmentView = inflater.inflate(
				R.layout.group_free_comment_fragment, null);
		return mFreeFragmentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (isGetData()) {
			onLoadData(true, true, true);
		}

	}

	protected boolean isGetData() {

		return true;
	}

	@Override
	public int getTotalPage() {
		return totalPage;
	}

	@Override
	public FreeCommentParser setParser(String str) {
		FreeCommentParser freeCommentParser = new FreeCommentParser(str);
		totalPage = freeCommentParser.getTotalPage();
		String numberCount=freeCommentParser.getNoCommentCount();
		if(getActivity() instanceof HomeActivity){
			HomeActivity homeact=(HomeActivity) getActivity();
			String message=homeact.message;
			if(!TextUtils.isEmpty(message)){
				setNoCommentCount(message);
			}else{
				if(!TextUtils.isEmpty(numberCount)){
				setNoCommentCount(numberCount);
			}
			}
			
			
		}
//		if(!TextUtils.isEmpty(numberCount)){
//			setNoCommentCount(numberCount);
//		}
		
		return freeCommentParser;
	}

	protected void setNoCommentCount(String noCommentCount) {
		
		if( getActivity()!=null){
			HomeActivity act = (HomeActivity)getActivity();
			TextView tvCount = (TextView) getActivity().findViewById(
					R.id.tv_no_write_count);
		     if(act.mTVCount!=null){
           	  if(!TextUtils.isEmpty(noCommentCount)&&!noCommentCount.equals("0")){
           		act.mTVCount.setVisibility(View.VISIBLE);
           		act.mTVCount.setText(noCommentCount);
           	  }else{
           		act.mTVCount.setVisibility(View.GONE);
           	  }
           	 
             }
			if (!TextUtils.isEmpty(noCommentCount)&&!noCommentCount.equals("0")) {
				if(tvCount!=null){
					tvCount.setVisibility(View.VISIBLE);
					tvCount.setText(noCommentCount);
				}
			
			} else {
				if(tvCount!=null){
					tvCount.setVisibility(View.GONE);
				}
				
			}
		}

	}

	@Override
	public RequestParamsNet getApiParmars() {
		RequestParamsNet requestParamNet = new RequestParamsNet();
		String userid = SharedPreferenceHelper
				.getLoginUserId(TootooPlusApplication.getAppContext());
		requestParamNet.setmStrHttpApi(TootooeNetApiUrlHelper.FREE_COMMENT);
		requestParamNet.addQueryStringParameter(TootooeNetApiUrlHelper.USER_ID,
				userid);
		requestParamNet.addQueryStringParameter(TootooeNetApiUrlHelper.PAGE,
				String.valueOf(currentpage));
		requestParamNet.addQueryStringParameter(
				TootooeNetApiUrlHelper.PAGE_SIZE,
				String.valueOf(TootooeNetApiUrlHelper.PAGESIZE_DRAFT));//
		setDifferentParam(requestParamNet);
		return requestParamNet;
	}

	/**
	 * 
	 * @Title: setDiffientParam
	 * @Description: 设置不同的参数，请求不同的数据
	 * @param
	 * @return
	 * @throws
	 */
	public abstract void setDifferentParam(RequestParamsNet requestParamNet);

	@Override
	public void getPageListParserResult(List<FreeCommentBean> parserData) {

		if (super.currentpage == 1) {
			this.parserResult.clear();
		}
		if (parserData != null) {
			parserResult.addAll(parserData);
			if (parserResult.size() > 0) {
				initAdapter(parserResult, parserData);
			} else {
				ComponentUtil.showToast(TootooPlusApplication.getAppContext(),
						"没有要筛选的数据");
			}
		}

	}

	/**
	 * @Title: initAdapter
	 * @Description: TODO
	 * @param
	 * @return
	 * @throws
	 */
	protected void initAdapter(List<FreeCommentBean> parserResult,
			List<FreeCommentBean> moreResult) {
		int moreSize = moreResult.size();
		GroupFreeCommentAdapter wishAdatper = new GroupFreeCommentAdapter(
				getActivity(), parserResult);
		mWishRefreshListView.setAdapter(wishAdatper);
		if (super.currentpage != 1) {
			mWishRefreshListView.setSelection(this.parserResult.size()
					- moreSize + 1);
		}
		wishAdatper.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parserResult != null && parserResult.size() > 0) {
			onItemClickToSkip(view, position, parserResult);
		} else {
			LogUtil.error("数据异常", "parserResult=null");
		}

	}

	/**
	 * 
	 * @Title: onItemClickToSkip
	 * @Description: 点击条目跳转数据
	 * @param
	 * @return
	 * @throws
	 */
	public abstract void onItemClickToSkip(View view, int position,
			List<FreeCommentBean> parserData);
}
