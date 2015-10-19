package com.ninetowns.tootooplus.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.bean.GoodsScreenMainBean;

/**
 * 
 * @ClassName: CategoryGoodsMainAdapter
 * @Description: 主分类的adapter
 * @author wuyulong
 * @date 2015-2-3 上午9:54:18
 * 
 */
public class CategoryGoodsMainAdapter extends BaseAdapter {
	private List<GoodsScreenMainBean> goodsCateGoryList;

	public CategoryGoodsMainAdapter(List<GoodsScreenMainBean> goodsCateGoryList) {
		this.goodsCateGoryList = goodsCateGoryList;
	}

	@Override
	public int getCount() {
		return goodsCateGoryList.size();
	}

	@Override
	public Object getItem(int position) {
		return goodsCateGoryList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GoodsScreenMainBean goodsScreenMainBean=goodsCateGoryList.get(position);
		CateGoryGoodsMainHolder viewHolder=null;
		if (convertView == null) {
			convertView = LayoutInflater.from(
					TootooPlusApplication.getAppContext()).inflate(
					R.layout.category_goods_main_item, null);
			viewHolder=new CateGoryGoodsMainHolder();
			ViewUtils.inject(viewHolder, convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder=(CateGoryGoodsMainHolder) convertView.getTag();
		}viewHolder.textContent.setText(goodsScreenMainBean.getCategoryName());
		return convertView;
	}

	public static class CateGoryGoodsMainHolder {
		@ViewInject(R.id.tv_gategory_text)
		public TextView textContent;

	}

}
