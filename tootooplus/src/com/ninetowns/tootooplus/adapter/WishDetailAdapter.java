package com.ninetowns.tootooplus.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ninetowns.library.util.LogUtil;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.activity.StoryDetailViewPagerActivity;
import com.ninetowns.tootooplus.activity.VideoActivity;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.bean.SkipPhotoViewPagerBean;
import com.ninetowns.tootooplus.bean.WishDetailPageListBean;
import com.ninetowns.tootooplus.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootooplus.util.CommonUtil;
import com.ninetowns.tootooplus.util.UIUtils;
import com.ninetowns.tootooplus.adapter.WishDetailAdapter.MyOnGlobalLayoutListener;
import com.ninetowns.tootooplus.adapter.WishDetailAdapter.ViewHolder;
import com.ninetowns.ui.cooldraganddrop.SpanVariableGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * 
 * @ClassName: WishDetailAdapter
 * @Description: 心愿详情数据适配器
 * @author wuyulong
 * @date 2015-2-27 上午9:37:53
 * 
 */
@SuppressLint("NewApi")
public class WishDetailAdapter extends BaseAdapter {
	public List<WishDetailPageListBean> pageListData;
	private LayoutInflater mInflater;
	public Activity activity;

	public WishDetailAdapter(Activity activity,
			List<WishDetailPageListBean> pageListData) {
		this.pageListData = pageListData;
		this.activity = activity;
		setInflater();
	}

	private void setInflater() {
		mInflater = LayoutInflater.from(TootooPlusApplication.getAppContext());

	}

	@Override
	public int getCount() {
		return pageListData.size();
	}

	@Override
	public Object getItem(int position) {
		return pageListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		WishDetailPageListBean storyDetailBean = pageListData.get(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.griditem_laucher_news,
					null);
			viewHolder = new ViewHolder();
			ViewUtils.inject(viewHolder, convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		setDisplayView(viewHolder, storyDetailBean,convertView);
		OnClickToPage(viewHolder, storyDetailBean);
		if (!TextUtils.isEmpty(storyDetailBean.getPageType())) {
			int height = 0;
			int strType = Integer.valueOf(storyDetailBean.getPageType());
			final int with = CommonUtil.getWidth(TootooPlusApplication
					.getAppContext());
			int elementType = Integer.valueOf(storyDetailBean.getElementType());
			String sizeFount=storyDetailBean.getFontSize();
			if (strType == 1) {// 文字*viewHolder.mItemText.getLineCount()
				String content = storyDetailBean.getPageContent();
			if(!TextUtils.isEmpty(sizeFount)){
				if(sizeFount.equals("1")){
					if (!TextUtils.isEmpty(content)) {
						RelativeLayout.LayoutParams layoutPar = (android.widget.RelativeLayout.LayoutParams) viewHolder.mItemTextOneTitle
								.getLayoutParams();
						if (layoutPar != null) {
							layoutPar.width = CommonUtil
									.getWidth(TootooPlusApplication.getAppContext());
							
							viewHolder.mItemTextOneTitle.setLayoutParams(layoutPar);
						}
					
						height = RelativeLayout.LayoutParams.WRAP_CONTENT;
					} else {
						height = RelativeLayout.LayoutParams.WRAP_CONTENT;
					}
				}else{
					height = noOneTitle(viewHolder, content);
				}
			}else{
				height = noOneTitle(viewHolder, content);
			}
				
				

			} else {// 1、长方形 2:1;
				// 2、小正方形；3、长方形4：3；4、大正方形
				if(elementType==1){//小正方形
					height = with / 2;
				}else if(elementType==2){
					height = with / 2;
				}else if(elementType==3){
					height = with *3/4;
				}else if(elementType==4){
					height = with;
				}
				

			}
		
			LayoutParams layoutParam = new LayoutParams(with,height);
			convertView.setLayoutParams(layoutParam);
			if (layoutParam != null) {
				SpanVariableGridView.LayoutParams spanVariableGridViewParams = new SpanVariableGridView.LayoutParams(
						convertView.getLayoutParams());
				if (elementType == 1) {
					spanVariableGridViewParams.span = 2;
				} else if (elementType == 2) {
					spanVariableGridViewParams.span = 1;
				}else{
					spanVariableGridViewParams.span = 2;
				}
				
				convertView.setLayoutParams(spanVariableGridViewParams);
			}
		}
	


		return convertView;
	}

	/**
	 * 
	 * @Title: OnClickToPage
	 * @Description: 点击page页
	 * @param
	 * @return
	 * @throws
	 */
	private void OnClickToPage(ViewHolder viewHolder,
			WishDetailPageListBean storyDetailBean) {
		viewHolder.mItemImage.setOnClickListener(new MyOnClickListener(
				viewHolder, storyDetailBean));
	}

	/** 
	* @Title: noOneTitle 
	* @Description: TODO
	* @param  
	* @return   
	* @throws 
	*/
	private int noOneTitle(ViewHolder viewHolder, String content) {
		int height;
		if (!TextUtils.isEmpty(content)) {
			RelativeLayout.LayoutParams layoutPar = (android.widget.RelativeLayout.LayoutParams) viewHolder.mItemText
					.getLayoutParams();
			if (layoutPar != null) {
				layoutPar.width = CommonUtil
						.getWidth(TootooPlusApplication.getAppContext());
				
				viewHolder.mItemText.setLayoutParams(layoutPar);
			}
			height = RelativeLayout.LayoutParams.WRAP_CONTENT;
		} else {
			height = RelativeLayout.LayoutParams.WRAP_CONTENT;
		}
		return height;
	}
	private void setDisplayView(ViewHolder viewholder,
			WishDetailPageListBean storyDetailBean,View convertView) {
		int strType = Integer.valueOf(storyDetailBean.getPageType());
		String pageImage = storyDetailBean.getPageImgThumb();
		int textsize = Integer.valueOf(storyDetailBean.getFontSize());
		RelativeLayout.LayoutParams layoutPar=new RelativeLayout.LayoutParams(CommonUtil.getWidth(TootooPlusApplication.getAppContext()),LayoutParams.WRAP_CONTENT);
		String pagecontent=CommonUtil.ToDBC(storyDetailBean.getPageContent());
		if(!TextUtils.isEmpty(pagecontent)){
			
		}else{
			pagecontent=storyDetailBean.getPageContent();
			if(TextUtils.isEmpty(pagecontent)){
				pagecontent="";
			}
		}
		switch (strType) {
		case 1:// 文字
			if (textsize == 1) {//标题
				convertView.setBackgroundDrawable(TootooPlusApplication.getAppContext()
						.getResources().getDrawable(R.drawable.detail_title));
				int textHorizalMargin = UIUtils.px2dip(
						TootooPlusApplication.getAppContext(),
						(int) TootooPlusApplication.getAppContext()
								.getResources().getDimension(R.dimen.text_horizal_margin));
				int vertical_margin=UIUtils.px2dip(
						TootooPlusApplication.getAppContext(),
						(int) TootooPlusApplication.getAppContext()
								.getResources().getDimension(R.dimen.activity_vertical_margin));
				layoutPar.leftMargin=textHorizalMargin;
				layoutPar.rightMargin=textHorizalMargin;
				layoutPar.bottomMargin=vertical_margin;
				layoutPar.topMargin=vertical_margin;
				layoutPar.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
				viewholder.mItemTextOneTitle.setTextSize(UIUtils.px2Sp(
						TootooPlusApplication.getAppContext(),
						(int) TootooPlusApplication.getAppContext()
								.getResources().getDimension(R.dimen.h0)));// 标题)
				viewholder.mItemTextOneTitle.setTextColor(TootooPlusApplication.getAppContext()
						.getResources().getColor(R.color.btn_org_color));
				viewholder.mRootView.setLayoutParams(layoutPar);
				ViewTreeObserver viewTree = viewholder.mItemText.getViewTreeObserver();
				viewTree.addOnGlobalLayoutListener(new MyOnGlobalLayoutListener(viewholder, convertView, storyDetailBean));
				viewholder.mItemTextOneTitle.setText(pagecontent);
				
			} else if(textsize ==2) {//正文
//				viewholder.mItemText.setTextSize(UIUtils.px2Sp(
//						TootooPlusApplication.getAppContext(),
//						(int) TootooPlusApplication.getAppContext()
//								.getResources().getDimension(R.dimen.h4)));
//				viewholder.mItemText.setText(""+storyDetailBean.getPageContent());
				
				
				
				layoutPar.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
				viewholder.mItemText.setTextSize(UIUtils.px2Sp(
						TootooPlusApplication.getAppContext(),
						(int) TootooPlusApplication.getAppContext()
								.getResources().getDimension(R.dimen.h4)));
//				viewholder.mItemText.setTextColor(TootooPlusApplication.getAppContext()
//						.getResources().getColor(R.color.btn_org_color));
				ViewTreeObserver viewTree = viewholder.mItemText.getViewTreeObserver();
				viewTree.addOnGlobalLayoutListener(new MyOnGlobalLayoutListener(viewholder, convertView, storyDetailBean));
				viewholder.mItemText.setText(pagecontent);
			}else if(textsize == 3) {//副标题
				layoutPar.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
				viewholder.mItemText.setGravity(Gravity.CENTER);
				viewholder.mItemText.setTextSize(UIUtils.px2Sp(
						TootooPlusApplication.getAppContext(),
						(int) TootooPlusApplication.getAppContext()
								.getResources().getDimension(R.dimen.h2)));
				 float drawablePadding = UIUtils.px2dip(TootooPlusApplication.getAppContext(),TootooPlusApplication.getAppContext()
				.getResources().getDimension(R.dimen.button_horizontal_margin));
				Drawable secondDrawable = TootooPlusApplication.getAppContext().getResources().getDrawable(R.drawable.detail_second_title);
				viewholder.mItemText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, secondDrawable);
				viewholder.mItemText.setCompoundDrawablePadding((int) drawablePadding);
				viewholder.mItemText.setLayoutParams(layoutPar);
				viewholder.mItemText.setTextColor(TootooPlusApplication.getAppContext()
						.getResources().getColor(R.color.black));
				viewholder.mItemText.setText(pagecontent);
			}else if(textsize == 4) {//高亮
				convertView.setBackgroundDrawable(TootooPlusApplication.getAppContext()
						.getResources().getDrawable(R.drawable.detail_gaoliang_title));
				layoutPar.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
				viewholder.mItemText.setTextSize(UIUtils.px2Sp(
						TootooPlusApplication.getAppContext(),
						(int) TootooPlusApplication.getAppContext()
								.getResources().getDimension(R.dimen.h4)));
				viewholder.mItemText.setTextColor(TootooPlusApplication.getAppContext()
						.getResources().getColor(R.color.btn_org_color));
				ViewTreeObserver viewTree = viewholder.mItemText.getViewTreeObserver();
				viewTree.addOnGlobalLayoutListener(new MyOnGlobalLayoutListener(viewholder, convertView, storyDetailBean));
				viewholder.mItemText.setText(pagecontent);
			}
			viewholder.mIVVideoIcon.setVisibility(View.INVISIBLE);
		
		
			
			
			break;
		case 2:// 图片
			convertView.setBackgroundColor(TootooPlusApplication.getAppContext().getResources().getColor(R.color.transparent));
			viewholder.mIVVideoIcon.setVisibility(View.INVISIBLE);
			// 图片 长方形或者正方形
//			if (elementType == 1) {// 长方形

//				if (!TextUtils.isEmpty(pageImage)) {
//					ImageLoader.getInstance().displayImage(pageImage,
//							new ImageViewAware(viewholder.mItemImage),
//							CommonUtil.OPTIONS_ALBUM_DETAIL);
//				}

//			} else if (elementType == 2) {// 正方形
				if (!TextUtils.isEmpty(pageImage)) {
					ImageLoader.getInstance().displayImage(pageImage,
							new ImageViewAware(viewholder.mItemImage),
							CommonUtil.OPTIONS_ALBUM_DETAIL);
				}
//			}

			break;
		case 3:// 视频
			convertView.setBackgroundColor(TootooPlusApplication.getAppContext().getResources().getColor(R.color.transparent));
			viewholder.mIVVideoIcon.setVisibility(View.VISIBLE);
			if (!TextUtils.isEmpty(pageImage)) {
				ImageLoader.getInstance().displayImage(pageImage,
						new ImageViewAware(viewholder.mItemImage),
						CommonUtil.OPTIONS_ALBUM_DETAIL);
			}
			break;
		}

	}

	/**
	 * 
	 * @ClassName: ViewHolder
	 * @Description: 优化滑动拖动界面
	 * @author wuyulong
	 * @date 2015-1-28 下午1:21:16
	 * 
	 */
	public static class ViewHolder {
		@ViewInject(R.id.item_img)
		public ImageView mItemImage;// 图片
		@ViewInject(R.id.item_text)
		// 文字
		public TextView mItemText;//
		@ViewInject(R.id.iv_video_icon)
		public ImageView mIVVideoIcon;// 视频的icon
		
		@ViewInject(R.id.item_textonetitle)
		// 文字
		public CheckedTextView mItemTextOneTitle;//
		@ViewInject(R.id.item_relate)
		// 跟布局
		public RelativeLayout mRootView;

	}

	/**
	 * 
	 * @ClassName: MyOnClickListener
	 * @Description:
	 * @author wuyulong
	 * @date 2015-2-27 上午9:17:46
	 * 
	 */
	class MyOnClickListener implements View.OnClickListener {
		public List<WishDetailPageListBean> photoList = new ArrayList<WishDetailPageListBean>();
		private ViewHolder viewHolder;
		private WishDetailPageListBean storyDetailBean;
		public int position;

		public MyOnClickListener(ViewHolder viewHolder,
				WishDetailPageListBean storyDetailBean) {
			this.viewHolder = viewHolder;
			this.storyDetailBean = storyDetailBean;
			// 从集合中过滤
			flateData();
		}

		/**
		 * 
		 * @Title: flateData
		 * @Description: 过滤图片数据
		 * @param
		 * @return
		 * @throws
		 */
		private void flateData() {
			for (int i = 0; i < pageListData.size(); i++) {
				WishDetailPageListBean iterable_element=pageListData.get(i);
				if (iterable_element.getPageType().equals("2")) {// 如果是图片
					photoList.add(iterable_element);
				}
			
			}
			
			
		}

		@Override
		public void onClick(View v) {
			if (storyDetailBean.getPageType().equals("2")) {// 如果是图片
				for (int i = 0; i < photoList.size(); i++) {
					WishDetailPageListBean iterable_element=photoList.get(i);
					if (iterable_element.getPageType().equals("2")) {// 如果是图片
						if(storyDetailBean.getPageId().equals(iterable_element.getPageId())){
							this.position=i;
						}
					}
				}
				SkipPhotoViewPagerBean skipPhotoBean=new SkipPhotoViewPagerBean();
				// 找到所点击的是第几个
				Intent intent = new Intent(activity,
						StoryDetailViewPagerActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				skipPhotoBean.setPhotoList(photoList);
				skipPhotoBean.setPosition(position);
				intent.putExtra("photolist", skipPhotoBean);
				activity.startActivity(intent);
			} else if (storyDetailBean.getPageType().equals("3")) {// 如果是视频 那么播放
				String videoUrl=storyDetailBean.getPageVideoUrl();
				if(!TextUtils.isEmpty(videoUrl)){
					Intent intent=new Intent(activity,VideoActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString(ConstantsTooTooEHelper.VIDEO_URL, videoUrl);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
					activity.startActivity(intent);
				}else{
					LogUtil.systemlogError("视频地址错误", videoUrl);
				}
				

			}
		}

	}
	class MyOnGlobalLayoutListener implements OnGlobalLayoutListener{
		private ViewHolder viewHolder;
		private View convertView;
		private WishDetailPageListBean storyDetailBean;
		public MyOnGlobalLayoutListener(ViewHolder viewHolder,View convertView,WishDetailPageListBean storyDetailBean) {
			this.viewHolder=viewHolder;
			this.convertView=convertView;
			this.storyDetailBean=storyDetailBean;
		}

		@SuppressLint("NewApi")
		@Override
		public void onGlobalLayout() {
			final int with = CommonUtil.getWidth(TootooPlusApplication.getAppContext());
			int textsize = Integer.valueOf(storyDetailBean.getFontSize());
			if(textsize==1){
				int height=viewHolder.mItemText.getMeasuredHeight();
				RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(with, height);
				int textHorizalMargin = UIUtils.px2dip(
						TootooPlusApplication.getAppContext(),
						(int) TootooPlusApplication.getAppContext()
								.getResources().getDimension(R.dimen.text_horizal_margin));
				int vertical_margin=UIUtils.px2dip(
						TootooPlusApplication.getAppContext(),
						(int) TootooPlusApplication.getAppContext()
								.getResources().getDimension(R.dimen.lineSpacingExtraTextDrop));
				
				layoutParam.topMargin=vertical_margin;
				layoutParam.leftMargin=textHorizalMargin;
				layoutParam.rightMargin=textHorizalMargin;
				layoutParam.bottomMargin=vertical_margin;
				int lineCount = viewHolder.mItemTextOneTitle.getLineCount();
				if(lineCount>1){
					return;
//					viewHolder.mRootView.setLayoutParams(layoutParam);
				
				}else{
					layoutParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
					viewHolder.mItemTextOneTitle.setGravity(Gravity.CENTER);
					viewHolder.mRootView.setLayoutParams(layoutParam);
				}
			
				
			}else{
				int height=viewHolder.mItemText.getMeasuredHeight();
				RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(with, height);
				int textHorizalMargin = UIUtils.px2dip(
						TootooPlusApplication.getAppContext(),
						(int) TootooPlusApplication.getAppContext()
								.getResources().getDimension(R.dimen.text_horizal_margin));
				int vertical_margin=UIUtils.px2dip(
						TootooPlusApplication.getAppContext(),
						(int) TootooPlusApplication.getAppContext()
								.getResources().getDimension(R.dimen.lineSpacingExtraTextDrop));
				layoutParam.topMargin=vertical_margin;
				layoutParam.leftMargin=textHorizalMargin;
				layoutParam.rightMargin=textHorizalMargin;
//				layoutParam.bottomMargin=vertical_margin;
				viewHolder.mRootView.setLayoutParams(layoutParam);
			}
			
//			int spaceLine = UIUtils.px2dip(
//					TootooPlusEApplication.getAppContext(),
//					(int) TootooPlusEApplication.getAppContext()
//							.getResources().getDimension(R.dimen.lineSpacingExtraTextDrop));
		
		
		}
		
	}
}
