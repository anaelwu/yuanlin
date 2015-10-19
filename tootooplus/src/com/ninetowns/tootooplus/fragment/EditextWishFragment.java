package com.ninetowns.tootooplus.fragment;

import android.os.Bundle;
import android.text.TextUtils;

import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.tootooplus.bean.ConVertBean;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;

/**
 * 
 * @ClassName: EditextWishFragment
 * @Description: 编辑故事
 * @author wuyulong
 * @date 2015-2-28 上午9:36:20
 * 
 */
public class EditextWishFragment extends RecommendWishFragment implements
		CreateWishBaseFragment.OnApiParamsInterface {

	private String stringCommentCount;
	@Override
	public void getBundleType(Bundle bundle) {
		super.bundle=bundle;
		setOnApkParamsListener(this);
	}

	@Override
	public boolean isGetNetData() {
		return true;
	}

	@Override
	public RequestParamsNet getRequestParamsNet(Bundle bundle) {
		RequestParamsNet requestParamsNet = new RequestParamsNet();
		requestParamsNet.setmStrHttpApi(TootooeNetApiUrlHelper.EDITEXT_WISH);
		String userId = SharedPreferenceHelper.getLoginUserId(getActivity());
		requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.USERID,
				userId);
		requestParamsNet.addQueryStringParameter(
				TootooeNetApiUrlHelper.STORYID, storyid);
		return requestParamsNet;
	}
	@Override
	protected void getConvertComment(ConVertBean mConvertBean) {
		if(mConvertBean!=null){
			stringCommentCount=mConvertBean.getCountRecommend();
		}
	}
	@Override
	protected CommentDialogFragment createPushCommentDialog() {//编辑的时候需要传递多少颗星星
		CommentDialogFragment countDialog=null;
		if(!TextUtils.isEmpty(stringCommentCount)){
			countDialog=new CommentDialogFragment(stringCommentCount);
		}else{
			countDialog=new CommentDialogFragment();
			
		}
		return countDialog;
	}

}
