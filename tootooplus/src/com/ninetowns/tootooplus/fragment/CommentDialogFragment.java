package com.ninetowns.tootooplus.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.tootooplus.R;
import com.ninetowns.ui.widget.dialog.BaseFragmentDialog;

/**
 * 
 * @ClassName: CommentDialogFragment
 * @Description: 评论发布并且点评
 * @author wuyulong
 * @date 2015-1-27 下午4:30:26
 * 
 */
public class CommentDialogFragment extends BaseFragmentDialog  {

	@ViewInject(R.id.ll_background)
	private LinearLayout mLLBackGround;
	@ViewInject(R.id.ct_comment_push)
	private CheckedTextView mCTCommentPush;
	@ViewInject(R.id.rat_bar_comment)
	private RatingBar mRatingComment;
	private View pushComment;
	private String ratingCount;
	public CommentDialogFragment() {
	}
	public CommentDialogFragment(String ratingCount) {
		this.ratingCount=ratingCount;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		pushComment=inflater.inflate(R.layout.dialog_push_comment, null);
		ViewUtils.inject(this, pushComment);
		if(!TextUtils.isEmpty(ratingCount)){
		float ratingFlCount = Float.valueOf(ratingCount);
			mRatingComment.setRating(ratingFlCount);
		}
		return pushComment;
	}

	@OnClick(R.id.ct_comment_push)
	public void onClickCommentPush(View v) {
		float ratingCount = mRatingComment.getRating();
		String strRatCount=String.valueOf(ratingCount);
		if(pushListener!=null){
			if(!TextUtils.isEmpty(strRatCount)){
				pushListener.setOnPushPar(strRatCount);
			}else{
				ComponentUtil.showToast(getActivity(), "您还没有星评！");
			}
			
		}
		

	}
	@OnClick(R.id.ll_background)
	protected void setDissmiss(View v){
		dismiss();
		
	}
	private PushListener pushListener;
	/**
	 * 
	* @ClassName: PushListener 
	* @Description: 监听评论星星
	* @author wuyulong
	* @date 2015-4-24 上午11:09:04 
	*
	 */
	public interface PushListener{
		public void setOnPushPar(String ratingCount);
		
	}
	public void setPushListener(PushListener pushListener){
		this.pushListener=pushListener;
		
	}

}