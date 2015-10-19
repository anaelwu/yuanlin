package com.ninetowns.tootoopluse.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ninetowns.library.util.LogUtil;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.activity.VideoActivity;
import com.ninetowns.tootoopluse.adapter.WishDetailAdapter;
import com.ninetowns.tootoopluse.application.TootooPlusEApplication;
import com.ninetowns.tootoopluse.bean.ActivityDetailBean;
import com.ninetowns.tootoopluse.bean.WishDetailBean;
import com.ninetowns.tootoopluse.bean.WishDetailPageListBean;
import com.ninetowns.tootoopluse.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootoopluse.util.CommonUtil;
import com.ninetowns.ui.cooldraganddrop.CoolDragAndDropGridView;
import com.ninetowns.ui.cooldraganddrop.SimpleScrollingStrategy;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * @ClassName: ActivityDetailFragment
 * @Description: 活动详情
 * @author wuyulong
 * @date 2015-2-10 下午1:07:50
 * 
 */
public class ActivityDetailFragment extends Fragment implements
		View.OnClickListener, CoolDragAndDropGridView.DragAndDropListener,
		OnItemClickListener {
	@ViewInject(R.id.coolDragAndDropGridView)
	private CoolDragAndDropGridView mCoolDragAndrDropView;
	private WishDetailBean viewpagerbean;
	@ViewInject(R.id.scrollView)
	private ScrollView mScrollView;
	@ViewInject(R.id.iv_detail_convert)
	private ImageView mIvConvertImage;
	@ViewInject(R.id.iv_video_icon)
	private ImageView mIVVideoIcon;
	@ViewInject(R.id.tv_storyname)
	private TextView mTvActivityName;
	private int currentItem;
	@ViewInject(R.id.tv_what_count)
	public TextView mTvActivityWhatCount;
	@ViewInject(R.id.tv_page_count)
	public TextView mTvActivityPageCount;

	private View mActivityWishDetailFragment;
	private List<WishDetailPageListBean> pageListData;
	private ActivityDetailBean resultData;
	private String shoppingUrl;
	private int currentPosition=-1;
	private boolean isSelected=false;
	public ActivityDetailFragment() {
	}

	public ActivityDetailFragment(WishDetailBean viewpagerbean,
			int currentItem, ActivityDetailBean resultData) {
		this.viewpagerbean = viewpagerbean;
		this.currentItem = currentItem;
		this.resultData = resultData;
		pageListData = viewpagerbean.getWishList();
		shoppingUrl = viewpagerbean.getShoppingUrl();
	}
	@Override
	public void onStart() {
		super.onStart();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivityWishDetailFragment = inflater.inflate(
				R.layout.activity_detail_fragment, null);
		ViewUtils.inject(this, mActivityWishDetailFragment); // 注入view和事件
		
		setViewListener();
		setImageConvertParams();
		setCollDragGridViewOperate();
		setConvertInfo();
		setActivityAdapter();
		return mActivityWishDetailFragment;
	}
	
	


	private void setActivityAdapter() {
		if (pageListData != null && pageListData.size() > 0) {
			WishDetailAdapter wishDetailAdapter = new WishDetailAdapter(
					getActivity(), pageListData);
			mCoolDragAndrDropView.setAdapter(wishDetailAdapter);
			wishDetailAdapter.notifyDataSetChanged();
		}

	}

	private void setViewListener() {
		
		mIvConvertImage.setOnClickListener(this);

	}

	/**
	 * 
	 * @Title: setCollDragGridViewOperate
	 * @Description: 注册
	 * @param
	 * @return
	 * @throws
	 */
	private void setCollDragGridViewOperate() {
		mCoolDragAndrDropView.setScrollingStrategy(new SimpleScrollingStrategy(
				mScrollView));
		mCoolDragAndrDropView.setDragAndDropListener(this);
		mCoolDragAndrDropView.setOnItemClickListener(this);
	}

	/**
	 * 
	 * @Title: setImageConvertParams
	 * @Description: 设置封面图的大小
	 * @param
	 * @return
	 * @throws
	 */
	private void setImageConvertParams() {
		int with = CommonUtil.getWidth(TootooPlusEApplication.getAppContext());
		int height = (int) (with * 0.67);
		RelativeLayout.LayoutParams convertParam = new RelativeLayout.LayoutParams(
				with, height);
		mIvConvertImage.setLayoutParams(convertParam);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDragItem(int from) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDraggingItem(int from, int to) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDropItem(int from, int to) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDragAndDropEnabled(int position) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.iv_detail_convert:
			justIfCanPlay();
			break;
		

		}

	}

	/**
	 * 
	 * @Title: justIfCanPlay
	 * @Description: 判断是否=
	 * @param
	 * @return
	 * @throws
	 */
	private void justIfCanPlay() {
		if (viewpagerbean != null) {
			String strVideoUrl = viewpagerbean.getStoryVideoUrl();
			String strStoryType = viewpagerbean.getStoryType();
			if (!TextUtils.isEmpty(strVideoUrl)
					&& !TextUtils.isEmpty(strStoryType)
					&& strStoryType.equals("3")) {// 视频
				skipPlayVideoView(strVideoUrl);
			}

		}
	}

	/**
	 * 
	 * @Title: skipPlayVideoView
	 * @Description: 跳转到视频
	 * @param
	 * @return
	 * @throws
	 */
	private void skipPlayVideoView(String videoUrl) {

		Intent intent = new Intent(getActivity(), VideoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(ConstantsTooTooEHelper.VIDEO_URL, videoUrl);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
		startActivity(intent);

	}






	/**
	 * 
	 * @Title: setConvertInfo
	 * @Description: 设置封面图信息
	 * @param
	 * @return
	 * @throws
	 */
	private void setConvertInfo() {
		if (viewpagerbean != null) {
			ImageLoader.getInstance().displayImage(
					viewpagerbean.getCoverThumb(), mIvConvertImage,
					CommonUtil.OPTIONS_ALBUM);
			mTvActivityName.setText(resultData.getActivityName());
			String storyType = viewpagerbean.getStoryType();
			if (storyType.equals("2")) {
				mIVVideoIcon.setVisibility(View.INVISIBLE);
			} else if (storyType.equals("3")) {
				mIVVideoIcon.setVisibility(View.VISIBLE);
			} else {
				LogUtil.systemlogError("封面不可以是文字storyType=", storyType);
			}
		}

	}



	
}
