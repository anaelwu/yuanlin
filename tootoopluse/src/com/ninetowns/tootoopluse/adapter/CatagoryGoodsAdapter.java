package com.ninetowns.tootoopluse.adapter;

import java.util.List;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.application.TootooPlusEApplication;
import com.ninetowns.tootoopluse.bean.GoodsScreenMainBean;
import com.ninetowns.tootoopluse.bean.GoodsScreenSubBean;

/**
 * 
 * @ClassName: CatagoryGoosAdapter
 * @Description: 分类
 * @author wuyulong
 * @date 2015-2-2 下午5:41:49
 * 
 */
public class CatagoryGoodsAdapter extends BaseExpandableListAdapter {
	private List<GoodsScreenMainBean> goodsCateGoryList;
	private List<GoodsScreenSubBean> childreaBeanList;
	private LayoutInflater layoutInflater;

	public CatagoryGoodsAdapter(List<GoodsScreenMainBean> goodsCateGoryList,
			List<GoodsScreenSubBean> childreaBeanList) {
		this.goodsCateGoryList = goodsCateGoryList;
		this.childreaBeanList = childreaBeanList;
		layoutInflater = LayoutInflater.from(TootooPlusEApplication
				.getAppContext());
	}

	@Override
	public int getGroupCount() {
		return goodsCateGoryList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childreaBeanList.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return goodsCateGoryList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childreaBeanList.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GoodsScreenMainBean groupCategoryBean = goodsCateGoryList.get(groupPosition);
		GroupViewHolder groupViewHolder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(
					R.layout.item_expandview_group, null);
			groupViewHolder = new GroupViewHolder();
			ViewUtils.inject(groupViewHolder, convertView);
			convertView.setTag(groupViewHolder);
		} else {
			groupViewHolder = (GroupViewHolder) convertView.getTag();
		}
		String categoryName=groupCategoryBean.getCategoryName();
		String imageUrl=groupCategoryBean.getImgUrl();
		if(!TextUtils.isEmpty(categoryName)){
			groupViewHolder.mGroupTitle.setText(categoryName);
		}
//		if(!TextUtils.isEmpty(imageUrl)){
//			ImageLoader.getInstance().displayImage(imageUrl,new ImageViewAware(groupViewHolder.mIVicon),CommonUtil.OPTIONS_ALBUM);
//		}
		return convertView;
	}

	/***** 第一个分类 *****/
	public static class GroupViewHolder {
		@ViewInject(R.id.iv_cate_icon)
		public ImageView mIVicon;
		@ViewInject(R.id.tv_content)
		public TextView mGroupTitle;
		
		
		
	}

	public static class ChildrenHolder {
		@ViewInject(R.id.tv_children_content)
		public TextView mChildrenTitle;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		GoodsScreenSubBean childreaBean = childreaBeanList.get(childPosition);
		ChildrenHolder childrenHolder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(
					R.layout.item_expandview_children, null);
			childrenHolder = new ChildrenHolder();
			ViewUtils.inject(childrenHolder, convertView);
			convertView.setTag(childrenHolder);
		} else {
			childrenHolder = (ChildrenHolder) convertView.getTag();
		}
		String categoryName=childreaBean.getCategoryName();
		if(!TextUtils.isEmpty(categoryName)){
			childrenHolder.mChildrenTitle.setText(categoryName);
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		//要实现children 点击事件必须返回true
		return true;
	}

}