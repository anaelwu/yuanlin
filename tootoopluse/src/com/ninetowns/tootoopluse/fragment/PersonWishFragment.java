package com.ninetowns.tootoopluse.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.activity.WishDetailActivity;
import com.ninetowns.tootoopluse.adapter.WishHomeAdapter;
import com.ninetowns.tootoopluse.application.TootooPlusEApplication;
import com.ninetowns.tootoopluse.bean.WishBean;
import com.ninetowns.tootoopluse.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootoopluse.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootoopluse.parser.MyWishParser;
import com.ninetowns.ui.fragment.PageListFragment;
import com.ninetowns.ui.widget.refreshable.PullToRefreshBase;
import com.ninetowns.ui.widget.refreshable.RefreshableListView;
/**
 * 个人首页中的白吃心愿或者商家心愿
 * @author huangchao
 *
 */
public class PersonWishFragment extends PageListFragment<ListView, List<WishBean>, MyWishParser> implements OnItemClickListener {
	
	private RefreshableListView remark_story_lv;
	// 总页数
	private int totalPage;
	
	private String userId = "";
	
	private List<WishBean> wishList = new ArrayList<WishBean>();
	
	
	private View per_home_head_top;
	
	private View per_home_head_next;
	
	private LinearLayout per_home_change_layout;
	
	public PersonWishFragment(String userId, View per_home_head_top, View per_home_head_next, LinearLayout per_home_change_layout){
		this.userId = userId;
		
		this.per_home_head_top = per_home_head_top;
		
		this.per_home_head_next = per_home_head_next;
		
		this.per_home_change_layout = per_home_change_layout;
	}
	
	@Override
	protected View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.remark_story_fragment, null);
		
		remark_story_lv = (RefreshableListView)view.findViewById(R.id.remark_story_lv);
		
		remark_story_lv.getRefreshableView().addHeaderView(per_home_head_top);
		remark_story_lv.getRefreshableView().addHeaderView(per_home_head_next);
		
		remark_story_lv.setOnItemClickListener(this);
		remark_story_lv.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem > 1){
					per_home_change_layout.setVisibility(View.VISIBLE);
				} else if(firstVisibleItem < 4){
					per_home_change_layout.setVisibility(View.GONE);
				}
				
			}
		});
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onLoadData(true, true, false);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void getPageListParserResult(List<WishBean> parserResult) {
		if (super.currentpage == 1) {
			this.wishList.clear();
			if(parserResult == null || parserResult.size() == 0){
				remark_story_lv.setAdapter(null);
				return;
			}
		}
		wishList.addAll(parserResult);
		if (wishList.size() > 0) {
			WishHomeAdapter wishAdatper = new WishHomeAdapter(getActivity(),wishList);
			remark_story_lv.setAdapter(wishAdatper);
			if (super.currentpage != 1) {
				remark_story_lv.getRefreshableView().setSelection(this.wishList.size());
			}
			wishAdatper.notifyDataSetChanged();
		} else {
			ComponentUtil.showToast(TootooPlusEApplication.getAppContext(), "没有数据");
		}
		
	}

	@Override
	protected PullToRefreshBase<ListView> initRefreshIdView() {
		// TODO Auto-generated method stub
		
		return remark_story_lv;
	}

	@Override
	public int getTotalPage() {
		// TODO Auto-generated method stub
		return totalPage;
	}

	@Override
	public MyWishParser setParser(String str) {
		
		MyWishParser wishParser = new MyWishParser(str);
		totalPage = wishParser.getTotalPage();
		return wishParser;
	}

	@Override
	public RequestParamsNet getApiParmars() {
		
		RequestParamsNet requestPar = new RequestParamsNet();
		requestPar.setmStrHttpApi(TootooeNetApiUrlHelper.MY_WISH_LIST);
		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.USER_ID, userId);
		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.PAGE, String.valueOf(currentpage));
		requestPar.addQueryStringParameter(TootooeNetApiUrlHelper.PAGE_SIZE, String.valueOf(TootooeNetApiUrlHelper.PAGESIZE_DRAFT));// 默认每页6条

		return requestPar;
	}
	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// position=1
		if (position != -1) {
			WishBean selectionItem = wishList.get(position - 3);
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
	
}
