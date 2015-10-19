package com.ninetowns.tootooplus.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.parser.AbsParser;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.adapter.PrivateLetterAdapter;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.bean.PrivateLetterBean;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootooplus.parser.PrivateLetterParser;
import com.ninetowns.tootooplus.util.CommonUtil;
import com.ninetowns.tootooplus.util.INetConstanst;
import com.ninetowns.tootooplus.util.ParserUitils;
import com.ninetowns.tootooplus.util.UIUtils;
import com.ninetowns.ui.fragment.PageListFragment;
import com.ninetowns.ui.widget.refreshable.PullToRefreshBase;
import com.ninetowns.ui.widget.refreshable.RefreshableGridView;


/** 
* @ClassName: PrivateLetterFragment 
* @Description: 私信
* @author zhou
* @date 2015-1-26 上午11:31:13 
*  
*/
public class PrivateLetterFragment extends PageListFragment<GridView, List<PrivateLetterBean>, PrivateLetterParser> implements INetConstanst{
	
	View mPrivateLetterView;
	private Activity mContext;
	private GridView mPrivateLetterRefreshGridView;

	private PrivateLetterAdapter mPrivateLetterAdapter;
	private int totalPage;
	private List<PrivateLetterBean> mPrivateLetterBeans;

	@Override
	public void getPageListParserResult(List<PrivateLetterBean> parserResult) {
		if (super.currentpage == 1) {
			mPrivateLetterBeans=parserResult==null?new ArrayList<PrivateLetterBean>():parserResult;
		}else{
			mPrivateLetterBeans.addAll(parserResult);
		}
		mPrivateLetterAdapter = new PrivateLetterAdapter(mContext, mPrivateLetterBeans);
		mPrivateLetterRefreshGridView.setAdapter(mPrivateLetterAdapter);
		if (super.currentpage != 1) {
			mPrivateLetterRefreshGridView.setSelection(this.mPrivateLetterBeans.size() / 2 + 1);
		}
		mPrivateLetterAdapter.notifyDataSetChanged();
	}

	@Override
	protected PullToRefreshBase<GridView> initRefreshIdView() {
		RefreshableGridView refresh = (RefreshableGridView) mPrivateLetterView
				.findViewById(R.id.refresh_gridview);
		mPrivateLetterRefreshGridView =(GridView) refresh.getRefreshableView();
		// PauseOnScrollListener listener = new
		// PauseOnScrollListener(ImageLoader.getInstance(), true, true);
		// mRefresh.setOnScrollListener(listener);
		
//		mPrivateLetterRefreshGridView.setOnItemClickListener(new  OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				if(null!=mPrivateLetterAdapter){
//					mPrivateLetterAdapter.hideBadeView();
//				}
//				Intent intent=new Intent(mContext,PrivateLetterChat.class);
//				LogUtils.i("position onclick================"+position);
//				intent.putExtra("receiveuserid", mPrivateLetterBeans.get(position).UserId);
//				mContext.startActivity(intent);
//				
//			}
//		});
		
		
//		mPrivateLetterRefreshGridView.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> parent, View view,
//					 final int position, long id) {
//				
//				final String delUserId=mPrivateLetterBeans.get(position-1).getUserId();
//				UIUtils.showConfirmDialog(mContext,  "删除消息", "是否删除该条消息", new OnClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						RequestParamsNet requestPar = new RequestParamsNet();
//						requestPar.addQueryStringParameter("UserId", userid);
////						requestPar.addQueryStringParameter("UserId", testUserId);
//						requestPar.addQueryStringParameter("DelUserId",delUserId );
//						
//						CommonUtil.xUtilsGetSend(DELETE_SINGLE_PRIVATE, requestPar, new RequestCallBack<String>() {
//							
//							@Override
//							public void onSuccess(ResponseInfo<String> responseInfo) {
//								if(ParserUitils.isSuccess(responseInfo)){
//									UIUtils.showCenterToast(mContext, "删除成功");	
//									mPrivateLetterBeans.remove(position-1);
//									mPrivateLetterAdapter.notifyDataSetChanged();
//								}
//								
//							}
//							
//							@Override
//							public void onFailure(HttpException error, String msg) {
//								UIUtils.showCenterToast(mContext, "删除失败"+msg);
//								
//							}
//						});
//						
//					}
//				} );
//				return false;
//			}
//		});
		return refresh;
	}

	@Override
	protected View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mPrivateLetterView=inflater.inflate(R.layout.privateletter_fragment, null);
//		ViewUtils.inject(this, mPrivateLetterView);
		mContext=getActivity();
		return mPrivateLetterView;
	}

	@Override
	public int getTotalPage() {
		// TODO Auto-generated method stub
		return totalPage;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onLoadData(true, true, true);
		super.onActivityCreated(savedInstanceState);
	}
	@Override
	public AbsParser setParser(String str) {
		PrivateLetterParser privateParser = new PrivateLetterParser(str);
		totalPage=privateParser.getTotalPage();
		return privateParser;
	}

	private String userid;
	@Override
	public RequestParamsNet getApiParmars() {
		 userid = SharedPreferenceHelper
				.getLoginUserId(TootooPlusApplication.getAppContext());
		RequestParamsNet requestPar = new RequestParamsNet();
		requestPar.setmStrHttpApi(PRIVATELETTER_LIST_URL);
		requestPar.addQueryStringParameter("UserId", userid);
//		requestPar.addQueryStringParameter("UserId", testUserId);
		requestPar.addQueryStringParameter("PageSize", TootooeNetApiUrlHelper.PRIVATE_LETTER_PAGESIZE+ "");
		requestPar.addQueryStringParameter("Page", currentpage + "");
		return requestPar;
	}
	
	
//	private String testUserId="2";
	public void clearAll(){
		RequestParamsNet requestPar = new RequestParamsNet();
		requestPar.addQueryStringParameter("UserId",userid);
//		requestPar.addQueryStringParameter("UserId",testUserId);
		CommonUtil.xUtilsGetSend(CLEAR_ALL_PRIVATELETTER, requestPar, new RequestCallBack<String>() {
			
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {

				if(ParserUitils.isSuccess(responseInfo)){
					UIUtils.showCenterToast(mContext, "成功清空");
					if(null!=mPrivateLetterBeans){
						mPrivateLetterBeans.clear();
						mPrivateLetterAdapter.notifyDataSetChanged();
					}
				}

			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
