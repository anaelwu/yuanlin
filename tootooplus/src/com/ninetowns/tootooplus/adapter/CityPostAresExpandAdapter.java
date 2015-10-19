package com.ninetowns.tootooplus.adapter;

import java.util.List;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;

import com.custom.vg.list.CustomAdapter;
import com.custom.vg.list.OnItemClickListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.bean.AreSubBean;
import com.ninetowns.tootooplus.bean.PostAresBean;
import com.ninetowns.tootooplus.helper.HomePagerCityHelper.OnClickListenerItem;

/**
 * 
 * @ClassName: CityPostAresExpandAdapter
 * @Description: 配送范围
 * @author wuyulong
 * @date 2015-2-7 下午4:25:35
 * 
 */
public class CityPostAresExpandAdapter extends BaseExpandableListAdapter implements OnClickListenerItem {
	private List<PostAresBean> postSresList;
	private LayoutInflater mInflater;
	private int position;
	private int currentPosition;

	public CityPostAresExpandAdapter(List<PostAresBean> postSresList,
			int position) {
		this.postSresList = postSresList;
		this.position = position;
		setInflater();
	}

	private void setInflater() {
		mInflater = LayoutInflater.from(TootooPlusApplication.getAppContext());

	}

	@Override
	public int getGroupCount() {
		return postSresList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (postSresList.get(groupPosition).getSupcitypageList()!=null&&postSresList.get(groupPosition).getSupcitypageList().size() > 0) {
			return 1;
		}
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return postSresList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
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
		PostAresBean groupBean = postSresList.get(groupPosition);
		CityGroupViewHolder cityGroupViewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_group_city_post_ares,
					null);
			cityGroupViewHolder = new CityGroupViewHolder();
			ViewUtils.inject(cityGroupViewHolder, convertView);
			convertView.setTag(cityGroupViewHolder);
		} else {
			cityGroupViewHolder = (CityGroupViewHolder) convertView.getTag();
		}
		String strCity = groupBean.getAreaName();
		if (!TextUtils.isEmpty(strCity)) {
			cityGroupViewHolder.ct_city_pro.setText(strCity);
		}
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		List<AreSubBean> childBeanList = postSresList.get(currentPosition)
				.getSupcitypageList();
		CityChildViewHolder cityChildViewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_child_city_post_ares,
					null);
			cityChildViewHolder = new CityChildViewHolder();
			ViewUtils.inject(cityChildViewHolder, convertView);
			convertView.setTag(cityChildViewHolder);
		} else {
			cityChildViewHolder = (CityChildViewHolder) convertView.getTag();
		}
		setGridViewChildAdapter(cityChildViewHolder, childBeanList);
		return convertView;
	}

	/**
	 * 
	 * @Title: setGridViewChildAdapter
	 * @Description: 设置数据
	 * @param
	 * @return
	 * @throws
	 */
	private void setGridViewChildAdapter(
			CityChildViewHolder cityChildViewHolder,
			List<AreSubBean> childBeanList) {
		if (childBeanList != null) {
				MyChildCityAdapter myChildCityAdapter = new MyChildCityAdapter(childBeanList);
				cityChildViewHolder.mGvChild.setAdapter(myChildCityAdapter);
				cityChildViewHolder.mGvChild.setDividerHeight(10);
				cityChildViewHolder.mGvChild.setDividerWidth(10);
				cityChildViewHolder.mGvChild.setOnItemClickListener(new OnItemClickListener() {
					
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						
					}
				});
			myChildCityAdapter.notifyDataSetChanged();
			}

	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {

		return true;
	}

	public static class CityGroupViewHolder {
		@ViewInject(R.id.ct_city_pro)
		public CheckedTextView ct_city_pro;

	}

	public static class CityChildViewHolder {
		@ViewInject(R.id.custom_listView)
		public com.custom.vg.list.CustomListView mGvChild;

	}

	class MyChildCityAdapter extends CustomAdapter {
		private List<AreSubBean> childBeanList;

		public MyChildCityAdapter(List<AreSubBean> childBeanList) {
			this.childBeanList = childBeanList;
		}

		public void setList(List<AreSubBean> childBeanList) {
			this.childBeanList = childBeanList;

		}

		@Override
		public int getCount() {
			return childBeanList.size();
		}

		@Override
		public Object getItem(int position) {
			return childBeanList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AreSubBean childBean = childBeanList.get(position);
			ViewChildCityHolder viewChildCityHolder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.item_children_group_city_post_ares, null);
				viewChildCityHolder = new ViewChildCityHolder();
				ViewUtils.inject(viewChildCityHolder, convertView);
				convertView.setTag(viewChildCityHolder);
			} else {
				viewChildCityHolder = (ViewChildCityHolder) convertView
						.getTag();
			}
			String strChildCity = childBean.getAreaName();
			if (!TextUtils.isEmpty(strChildCity)) {
				viewChildCityHolder.ct_city_pro.setText(strChildCity);
			}
			return convertView;
		}

	}

	public static class ViewChildCityHolder {
		@ViewInject(R.id.ct_city_pro)
		public CheckedTextView ct_city_pro;
	}

	@Override
	public void onListenerItemPosition(int position) {
		currentPosition=position;
		
	}
}