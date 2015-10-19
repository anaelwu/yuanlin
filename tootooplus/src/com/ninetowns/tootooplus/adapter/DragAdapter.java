package com.ninetowns.tootooplus.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.bean.PhotoSelectOrConvertBean;
import com.ninetowns.tootooplus.bean.StoryDetailListBean;
import com.ninetowns.tootooplus.util.CommonUtil;
import com.ninetowns.tootooplus.util.UIUtils;
import com.ninetowns.ui.cooldraganddrop.SpanVariableGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

public class DragAdapter extends BaseAdapter {
	public List<StoryDetailListBean> mListDragBean;
	private LayoutInflater mInflater;
	public PhotoSelectOrConvertBean mConvertAndListWishBean;

	public DragAdapter(List<StoryDetailListBean> list) {
		this.mListDragBean = list;
		setInflater();
	}

	public void setConvertAndListWishBean(
			PhotoSelectOrConvertBean mConvertAndListWishBean) {
		this.mConvertAndListWishBean = mConvertAndListWishBean;
	}

	/**
	 * 
	 * @Title: setInflate
	 * @Description: 设置inflater加载条目布局
	 * @param
	 * @return
	 * @throws
	 */
	private void setInflater() {
		mInflater = LayoutInflater.from(TootooPlusApplication.getAppContext());

	}

	@Override
	public int getCount() {
		return mListDragBean.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mListDragBean.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		StoryDetailListBean storyDetailBean = mListDragBean.get(position);
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
		setDisplayView(viewHolder, storyDetailBean);
		if (!TextUtils.isEmpty(storyDetailBean.getPageType())) {
			int height = 0;
			int strType = Integer.valueOf(storyDetailBean.getPageType());
			final int with = CommonUtil.getWidth(TootooPlusApplication
					.getAppContext());
			int elementType = storyDetailBean.getElementType();
			if (strType == 1) {// 文字*viewHolder.mItemText.getLineCount()
				String content = storyDetailBean.getPageContent();
				content=CommonUtil.ToDBC(content);
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

			LayoutParams layoutParam = new LayoutParams(with, height);
			convertView.setLayoutParams(layoutParam);
			if (layoutParam != null) {
				SpanVariableGridView.LayoutParams spanVariableGridViewParams = new SpanVariableGridView.LayoutParams(
						convertView.getLayoutParams());
				if (elementType == 1) {
					spanVariableGridViewParams.span = 2;
				} else if (elementType == 2) {
					spanVariableGridViewParams.span = 1;
				} else {
					spanVariableGridViewParams.span = 2;
				}

				convertView.setLayoutParams(spanVariableGridViewParams);
			}
		}

		return convertView;
	}

	/**
	 * 
	 * @Title: setDisplayView
	 * @Description: 设置每个条目数据（文字、图片、视频）
	 * @param
	 * @return
	 * @throws
	 */
	private void setDisplayView(ViewHolder viewholder,
			StoryDetailListBean storyDetailBean) {
		int strType = Integer.valueOf(storyDetailBean.getPageType());
		String pageImage = storyDetailBean.getPageImg();
		
		String dragSquareImg = storyDetailBean.getDragSquareImg();//小正方形
		String dragRectangleImg = storyDetailBean.getDragRectangleImg();//1：2长方形
		
		String dragRectangleBig=storyDetailBean.getPageImgThumbBigRectangle();
		String dragSquareImgBig=storyDetailBean.getPageImgThumbBigSquare();
		
		
		if (TextUtils.isEmpty(dragRectangleImg)) {//如果是null
			dragRectangleImg = storyDetailBean.getPageImgThumbRectangle();

		}
		if (TextUtils.isEmpty(dragSquareImg)) {
			dragSquareImg = storyDetailBean.getPageImgThumbSquare();

		}

	
		int elementType = storyDetailBean.getElementType();
		int textsize = storyDetailBean.getFontSize();
		String pageContent=storyDetailBean.getPageContent();
		if(!TextUtils.isEmpty(pageContent)){
			pageContent=CommonUtil.ToDBC(pageContent);
		}else{
			pageContent="";
		}
		
		switch (strType) {
		case 1:// 文字
			if (textsize == 1) {
				viewholder.mItemText.setTextSize(UIUtils.px2Sp(
						TootooPlusApplication.getAppContext(),
						(int) TootooPlusApplication.getAppContext()
								.getResources().getDimension(R.dimen.h1)));// 标题)
			} else {
				viewholder.mItemText.setTextSize(UIUtils.px2Sp(
						TootooPlusApplication.getAppContext(),
						(int) TootooPlusApplication.getAppContext()
								.getResources().getDimension(R.dimen.h4)));
			}
			viewholder.mIVVideoIcon.setVisibility(View.INVISIBLE);
			// viewholder.mRootView
			// .setBackgroundResource(R.drawable.bg_recomde_tv_border);
			// viewholder.mItemText.setText(""+storyDetailBean.getPageContent());
			viewholder.mItemText.setText(pageContent);
			break;
		case 2:// 图片
			viewholder.mIVVideoIcon.setVisibility(View.INVISIBLE);
			// 图片 长方形或者正方形
			if (elementType == 1) {// 长方形

				if (!TextUtils.isEmpty(dragRectangleImg)) {
					ImageLoader.getInstance().displayImage(dragRectangleImg,
							new ImageViewAware(viewholder.mItemImage),
							CommonUtil.OPTIONS_ALBUM_DETAIL);
				}

			} else if (elementType == 2) {// 正方形
				if (!TextUtils.isEmpty(dragSquareImg)) {
					ImageLoader.getInstance().displayImage(dragSquareImg,
							new ImageViewAware(viewholder.mItemImage),
							CommonUtil.OPTIONS_ALBUM_DETAIL);
				}
			}else if(elementType==3){//4:3
				if (!TextUtils.isEmpty(dragRectangleBig)) {
					ImageLoader.getInstance().displayImage(dragRectangleBig,
							new ImageViewAware(viewholder.mItemImage),
							CommonUtil.OPTIONS_ALBUM_DETAIL);
				}
				
			}else if(elementType==4){
				if (!TextUtils.isEmpty(dragSquareImgBig)) {
					ImageLoader.getInstance().displayImage(dragSquareImgBig,
							new ImageViewAware(viewholder.mItemImage),
							CommonUtil.OPTIONS_ALBUM_DETAIL);
				}
			}

			break;
		case 3:// 视频
			viewholder.mIVVideoIcon.setVisibility(View.VISIBLE);
			if (!TextUtils.isEmpty(dragSquareImg)) {
				ImageLoader.getInstance().displayImage(dragSquareImg,
						new ImageViewAware(viewholder.mItemImage),
						CommonUtil.OPTIONS_ALBUM_DETAIL);
			}
			break;
		}

	}

	class MyOnGlobalLayoutListener implements OnGlobalLayoutListener {
		private ViewHolder viewHolder;
		private View convertView;
		private StoryDetailListBean storyDetailBean;

		public MyOnGlobalLayoutListener(ViewHolder viewHolder,
				View convertView, StoryDetailListBean storyDetailBean) {
			this.viewHolder = viewHolder;
			this.convertView = convertView;
			this.storyDetailBean = storyDetailBean;
		}

		@SuppressLint("NewApi")
		@Override
		public void onGlobalLayout() {
			int height;
			// android:lineSpacingExtra="@dimen/lineSpacingExtraTextDrop"
			// float
			// lineSpace=TootooPlusApplication.getAppContext().getResources().getDimension(R.dimen.lineSpacingExtraTextDrop);
			final int with = CommonUtil.getWidth(TootooPlusApplication
					.getAppContext());
			if (viewHolder.mItemText.getLineCount() > 1) {

				height = (int) (viewHolder.mItemText.getLineHeight()
						* viewHolder.mItemText.getLineCount()
						+ (int) (viewHolder.mItemText.getLineSpacingExtra())
						* (viewHolder.mItemText.getLineCount() - 1) - viewHolder.mItemText
						.getLineSpacingExtra());
				// viewHolder.mItemText.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
			} else {
				height = UIUtils.px2dip(TootooPlusApplication.getAppContext()
						.getResources().getDimension(R.dimen.text_line_heigth));
				// viewHolder.mItemText.setLineSpacing(0,0);
				// viewHolder.mItemText.setGravity(Gravity.BOTTOM|Gravity.LEFT);
			}

			LayoutParams layoutParam = new LayoutParams(with, height);
			convertView.setLayoutParams(layoutParam);
			if (layoutParam != null) {
				SpanVariableGridView.LayoutParams spanVariableGridViewParams = new SpanVariableGridView.LayoutParams(
						convertView.getLayoutParams());
				int elementType = storyDetailBean.getElementType();
				if (elementType == 1) {
					spanVariableGridViewParams.span = 2;
				} else if (elementType == 2) {
					spanVariableGridViewParams.span = 1;
				} else {
					spanVariableGridViewParams.span = 2;
				}

				convertView.setLayoutParams(spanVariableGridViewParams);
			}

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
		public CheckedTextView mItemText;//
		@ViewInject(R.id.iv_video_icon)
		public ImageView mIVVideoIcon;// 视频的icon
		@ViewInject(R.id.item_relate)
		// 跟布局
		public View mRootView;

	}

}
