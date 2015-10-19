package com.ninetowns.tootooplus.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.activity.WishDetailActivity;
import com.ninetowns.tootooplus.adapter.CommentListAdapter;
import com.ninetowns.tootooplus.bean.CommentListBean;
import com.ninetowns.tootooplus.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootooplus.parser.CommentListParser;
import com.ninetowns.ui.fragment.PageListFragment;
import com.ninetowns.ui.widget.WrapRatingBar;
import com.ninetowns.ui.widget.refreshable.PullToRefreshBase;
import com.ninetowns.ui.widget.refreshable.PullToRefreshBase.Mode;
import com.ninetowns.ui.widget.refreshable.RefreshableListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
/**
 * 
* @ClassName: CommentListFragment 
* @Description: 评论列表界面  
* @author wuyulong
* @date 2015-3-24 下午1:09:07 
*
 */
public class CommentListFragment extends PageListFragment<ListView, List<CommentListBean>, CommentListParser> implements OnItemClickListener{
	@ViewInject(R.id.remark_story_lv)
	private RefreshableListView remark_story_lv;
	@ViewInject(R.id.rat_bar)
	private WrapRatingBar ratingBar;//评论指数
	// 总页数
	private int totalPage;
	@ViewInject(R.id.ll_dismiss)
	private LinearLayout mIVDismiss;//销毁布局
	private String activityId = "";
	private FragmentTransaction framgentTransaction;//事物
	private FrameLayout mFLCommentListView;//当前界面
	private String countRecommend;//综合评论指数
	private String storyId;
	
	private List<CommentListBean> remarkStoryList = new ArrayList<CommentListBean>();
	private CommentListAdapter commentListAdapter;
	private View view;
	
	public CommentListFragment(String activityId,FragmentTransaction framgentTransaction,FrameLayout mFLCommentListView,String countRecommend,String storyId){
		this.activityId = activityId;
		this.mFLCommentListView=mFLCommentListView;
		this.framgentTransaction=framgentTransaction;
		this.countRecommend=countRecommend;
		this.storyId=storyId;
	}
	public CommentListFragment() {
	}
	@Override
	protected View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		 view = inflater.inflate(R.layout.comment_list_fragment, null);
		ViewUtils.inject(this,view);
		setComposite();
		return view;
	}
	/** 
	* @Title: setComposite 
	* @Description: 设置综合指数
	* @param  
	* @return   
	* @throws 
	*/
	private void setComposite() {
		if(!TextUtils.isEmpty(countRecommend)){
			ratingBar.setRating(Float.valueOf(countRecommend));
		}
	}
	/**
	 * 
	* @Title: dismissView 
	* @Description: 销毁当前界面
	* @param  
	* @return   
	* @throws
	 */
	@OnClick(R.id.ll_dismiss)
	public void dismissView(View v){
		if(mFLCommentListView!=null&&framgentTransaction!=null){
			mFLCommentListView.setVisibility(View.GONE);
//			framgentTransaction.remove(this);
		}
		
		
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		super.onLoadData(true, true, true);
		
	}

	@Override
	public void getPageListParserResult(List<CommentListBean> parserResult) {
		remark_story_lv.setOnItemClickListener(this);
		if(parserResult != null){
			int moreSize=parserResult.size();
			if(super.currentpage == 1){
				remarkStoryList.clear();
			}
			remarkStoryList.addAll(parserResult);
			//初始化adapter
			commentListAdapter=new CommentListAdapter(getActivity(), remarkStoryList);
			remark_story_lv.setAdapter(commentListAdapter);
			commentListAdapter.notifyDataSetChanged();
			if(super.currentpage != 1){
				remark_story_lv.getRefreshableView().setSelection(remarkStoryList.size()-moreSize+1);
			}
			
		}
		
		
	}

	@Override
	protected PullToRefreshBase<ListView> initRefreshIdView() {
		remark_story_lv=(RefreshableListView) view.findViewById(R.id.remark_story_lv);
		remark_story_lv.setRefreshing(true);
		remark_story_lv.setMode(Mode.BOTH);
		PauseOnScrollListener listener = new PauseOnScrollListener(
				ImageLoader.getInstance(), true, true);
		remark_story_lv.setOnScrollListener(listener);
		
		return remark_story_lv;
	}

	@Override
	public int getTotalPage() {
		return totalPage;
	}

	@Override
	public CommentListParser setParser(String str) {
		CommentListParser remarkStoryParser = new CommentListParser(str);
		totalPage = remarkStoryParser.getTotalPage();
		return remarkStoryParser;
	}

	@Override
	public RequestParamsNet getApiParmars() {
		RequestParamsNet requestParamsNet = new RequestParamsNet();
		requestParamsNet.setmStrHttpApi(TootooeNetApiUrlHelper.COMMENT_LIST);
		requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.ACTIVITYID, activityId);
		requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.STORYID, storyId);
		System.out.println("每次刷新调用的sotoryid"+storyId);
		requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.REMARK_STORY_PAGESIZE, String.valueOf(TootooeNetApiUrlHelper.PAGESIZE_DRAFT));
		requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.REMARK_STORY_PAGE, String.valueOf(currentpage));
		return requestParamsNet;
	}
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		System.out.println("每次刷新调用的sotoryid"+storyId);
	}
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		System.out.println("每次刷新调用的sotoryid"+storyId);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(position!=-1){
			 CommentListBean selectionItem = remarkStoryList.get(position-1);
			Intent intentToDetail=new Intent(getActivity(),WishDetailActivity.class);
			Bundle bundle=new Bundle();
			bundle.putString(ConstantsTooTooEHelper.USERID, selectionItem.getUserId());
			bundle.putString(ConstantsTooTooEHelper.STORYID, selectionItem.getStoryId());
			intentToDetail.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentToDetail.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
			startActivity(intentToDetail);
		}
		
	}
	
	

}
