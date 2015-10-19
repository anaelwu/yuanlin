package com.ninetowns.tootooplus.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.tootooplus.activity.WishDetailActivity;
import com.ninetowns.tootooplus.adapter.MyFreeCommentAdapter;
import com.ninetowns.tootooplus.bean.RemarkStoryBean;
import com.ninetowns.tootooplus.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.ui.widget.refreshable.RefreshableListView;

/**
 * 
 * @ClassName: MyFreeCommentNoPassedFragment
 * @Description: 我的点评未通过
 * @author wuyulong
 * @date 2015-4-20 下午3:59:01
 * 
 */
public class MyFreeCommentNoPassedFragment extends RemarkStoryFragment {

	@Override
	protected void setDiffientPar(RequestParamsNet requestParamsNet) {
		requestParamsNet.addQueryStringParameter(
				TootooeNetApiUrlHelper.REMARK_STORY_USERID,
				SharedPreferenceHelper.getLoginUserId(getActivity()));
		requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.TYPE,
				"2");// 审核未通过
	}

	@Override
	protected void onItemClickToSkip(View view, int position,
			List<RemarkStoryBean> remarkStoryList2) {
		if (position != -1) {
			RemarkStoryBean vTag = remarkStoryList2.get(position - 1);
			Intent intentToDetail = new Intent(getActivity(),
					WishDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(ConstantsTooTooEHelper.USERID,
					vTag.getRemark_userId());
			bundle.putString(ConstantsTooTooEHelper.STORYID,
					vTag.getRemark_storyId());
			intentToDetail.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intentToDetail.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
			startActivity(intentToDetail);
		}
	}

	@Override
	protected void setFreeAdapter(RefreshableListView remark_story_lv,
			List<RemarkStoryBean> parserResult) {
		MyFreeCommentAdapter adapter = new MyFreeCommentAdapter(getActivity(),
				parserResult);
		remark_story_lv.setAdapter(adapter);
	}

}