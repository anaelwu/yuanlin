package com.ninetowns.tootooplus.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.parser.AbsParser;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.activity.MyActivityCommentActivity;
import com.ninetowns.tootooplus.activity.SearchActivity;
import com.ninetowns.tootooplus.fragment.BaseShaiXuanDialog.OnSelectedListener;
import com.ninetowns.tootooplus.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.util.CommonUtil;
import com.ninetowns.ui.fragment.GroupFragment;

/**
 * 
 * @ClassName: BaseCommentFreeFragment
 * @Description: 最新、最热、推荐 附着界面
 * @author wuyulong
 * @date 2015-4-16 下午2:16:52
 * 
 */
public class HomeCommentFragment extends GroupFragment implements
		OnClickListener,OnSelectedListener {
	private View homeCommentFreeFragment;

	@ViewInject(R.id.rl_free_comment)
	private FrameLayout mFreeComment;

	@ViewInject(R.id.ibtn_left)
	private ImageButton mIBTN_Return;//返回
	@ViewInject(R.id.ll_middle)
	private LinearLayout mLLSearch;//搜索
	@ViewInject(R.id.ll_right)
	private RelativeLayout mLLCommentActFree;//跳转到我的白吃活动个
	private static final int ALL=0;
	private static final int NEW=1;
	private static final int HOT=2;
	private static final int RECOMMEND=3;
	

	private int viewType=0;

	private CommentShaiXuanDialog fragment;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		homeCommentFreeFragment = inflater.inflate(
				R.layout.home_comment_free_fragment, null);
		ViewUtils.inject(this, homeCommentFreeFragment);
		showCreateCommentDialog();
		setClickListener();
		return homeCommentFreeFragment;
	}
	/**
	 * 
	* @Title: showCreateCommentDialog 
	* @Description: 显示评论
	* @param  
	* @return   
	* @throws
	 */
	private void showCreateCommentDialog() {
		boolean isFirst = SharedPreferenceHelper.getFirstGuideCreateComment(getActivity());
		if(isFirst){//如果第一次
			CommonUtil.showFirstGuideDialog(getActivity(), ConstantsTooTooEHelper.FIRST_GUIDE_CREATE_COMMENT);
		}

	}
	/** 
	* @Title: setClickListener 
	* @Description: 设置点击事件
	* @param  
	* @return   
	* @throws 
	*/
	private void setClickListener() {
		mIBTN_Return.setOnClickListener(this);
		mLLSearch.setOnClickListener(this);
		mLLCommentActFree.setOnClickListener(this);
	}

	@Override
	protected void initPrimaryFragment() {
		switchPrimaryFragment(ALL);
	}

	@Override
	protected Class<? extends Fragment> getPrimaryFragmentClass(int fragmentId) {
		Class<? extends Fragment> calzz = null;
		switch (viewType) {
		case ALL:
			calzz = AllFreeFragment.class;
			break;
		case NEW:
			calzz = NewFreeFragment.class;
			
			break;
		case HOT:
			calzz = HotFreeFragment.class;

			break;
		case RECOMMEND:
			calzz = RecommendFreeFragment.class;

			break;
		default:
			throw new IllegalArgumentException();
		}
		return calzz;
	}

	@Override
	protected Bundle getPrimaryFragmentArguments(int fragmentId) {
		return null;
	}

	@Override
	protected int getPrimaryFragmentStubId() {
		return R.id.rl_free_comment;
	}





	@Override
	public AbsParser setParser(String str) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getParserResult(Object parserResult) {
		// TODO Auto-generated method stub

	}

	@Override
	public RequestParamsNet getApiParmars() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left:

			showSelectedDiallog();
		
			break;
		case R.id.ll_middle:
			skipToSearchWish();

			break;
		case R.id.ll_right:
		Intent intent=new Intent(getActivity(),MyActivityCommentActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);

			break;

		default:
			break;
		}
	}

	/** 
	* @Title: showSelectedDiallog 
	* @Description: TODO
	* @param  
	* @return   
	* @throws 
	*/
	private void showSelectedDiallog() {
		FragmentManager fragmentManager = getActivity()
				.getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager
				.beginTransaction();
		if(fragment!=null){
			if(fragment.isVisible()){
				fragment.dismiss();
			}else{
				fragment.show(fragmentManager, "dialog");
			}
			
		}else{
			fragment = new CommentShaiXuanDialog();
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
	 * @Title: showHisToryDialog
	 * @Description: 展示dialog
	 * @param
	 * @return
	 * @throws
	 */
	private void skipToSearchWish() {
		Intent intent = new Intent(getActivity(), SearchActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("searchType", "isComment");
		startActivity(intent);

	}

	@Override
	public void OnSelectedListenerPar(String type) {
		if(!TextUtils.isEmpty(type)){
			viewType=Integer.valueOf(type);
		}
		switchPrimaryFragment(viewType);
		
	}
}
