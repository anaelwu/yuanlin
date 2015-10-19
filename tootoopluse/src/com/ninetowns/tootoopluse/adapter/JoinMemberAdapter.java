package com.ninetowns.tootoopluse.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.interpolator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.GridView;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.application.TootooPlusEApplication;
import com.ninetowns.tootoopluse.bean.GridViewGroupBean;
import com.ninetowns.tootoopluse.util.CommonUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

@SuppressLint("NewApi")
public class JoinMemberAdapter extends BaseAdapter {

	private List<HashMap<String, List<GridViewGroupBean>>> mList;

	private LayoutInflater mInflater;

	private Activity mContext;

	// private List<GridViewGroupBean>
	private Map<Integer, Map<Integer, Boolean>> cmp;

	private Map<Integer, Map<Integer, Boolean>> mMutiMap;

	private List<GridViewGroupBean> mSignList;

	public JoinMemberAdapter(Activity activity,
			List<HashMap<String, List<GridViewGroupBean>>> parserResult) {
		mContext = activity;
		this.mList = parserResult;
		setInflater();
		mSignList = new ArrayList<GridViewGroupBean>();

		cmp = new HashMap<Integer, Map<Integer, Boolean>>();
		mMutiMap = new HashMap<Integer, Map<Integer, Boolean>>();
		if (parserResult != null || !parserResult.isEmpty()) {
			for (int i = 0; i < parserResult.size(); i++) {
				Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
				Map<Integer, Boolean> maps = new HashMap<Integer, Boolean>();
				for (int j = 0; j < parserResult.get(i).get("IsAdmin").size(); j++) {
					GridViewGroupBean bean = parserResult.get(i).get("IsAdmin")
							.get(j);
					if (bean.getIsUsed().equals("1")) {
						map.put(j, true);
						maps.put(j, true);
					} else {
						map.put(j, false);
						maps.put(j, false);
					}
				}
				cmp.put(i, map);
				mMutiMap.put(i, maps);
			}
		}

	}

	private void setInflater() {
		mInflater = LayoutInflater.from(TootooPlusEApplication.getAppContext());

	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		if (position % 2 == 0) {
			return 0;
		} else {
			return 1;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		PhotoViewHolder photoViewHolder = null;
		GridViewHolder mGridViewHolder = null;
		int type = getItemViewType(position);
		if (convertView == null) {
			switch (type) {
			case 0:
				convertView = mInflater.inflate(
						R.layout.item_group_gridview_item_boss, null);
				photoViewHolder = new PhotoViewHolder();
				ViewUtils.inject(photoViewHolder, convertView);
				convertView.setTag(photoViewHolder);
				break;
			case 1:

				convertView = mInflater.inflate(R.layout.mytempjoinmemberchild,
						null);
				mGridViewHolder = new GridViewHolder();
				ViewUtils.inject(mGridViewHolder, convertView);
				convertView.setTag(mGridViewHolder);
				break;
			}
		} else {
			switch (type) {
			case 0:
				photoViewHolder = (PhotoViewHolder) convertView.getTag();
				break;
			case 1:
				mGridViewHolder = (GridViewHolder) convertView.getTag();
				break;
			}
		}
		// if (isPhotoClickable()) {
		// photoViewHolder.ivPhoto.setOnClickListener(new MyOnClickListener(
		// photoGroup));
		// }

		switch (type) {
		case 0:
			setUserInfo(mList.get(position).get("IsAdmin").get(0),
					photoViewHolder);
			break;
		case 1:
			setGridViewAdapter(position, mList.get(position).get("IsAdmin"),
					mGridViewHolder.mGridViewmember);
			break;
		}
		return convertView;

	}

	public void setUserInfo(GridViewGroupBean photoBean,
			PhotoViewHolder photoViewHolder) {
		String logurl = photoBean.getLogoUrl();
		// String userGrade = photoBean.getUserGrade();
		String userName = photoBean.getUserName();
		String isUsed = photoBean.getIsUsed();
		String EatCode = photoBean.getEatCode();
		if (!TextUtils.isEmpty(isUsed)) {
			if(isUsed.equals("1")){
				//成功
				photoViewHolder.mIVYestGroup.setImageResource(R.drawable.icon_yes_group);
				photoViewHolder.mIVYestGroup.setVisibility(View.VISIBLE);
			}else if(isUsed.equals("0")){//0未成功
				photoViewHolder.mIVYestGroup.setVisibility(View.GONE);
			}else if(isUsed.equals("2")){
				//签到
				photoViewHolder.mIVYestGroup.setVisibility(View.VISIBLE);
				photoViewHolder.mIVYestGroup.setImageResource(R.drawable.icon_sign_group);
			}
		}
		if (!TextUtils.isEmpty(logurl)) {
			ImageLoader.getInstance().displayImage(logurl,
					new ImageViewAware(photoViewHolder.ivPhoto),
					CommonUtil.OPTIONS_HEADPHOTO);
		}
		// if (!TextUtils.isEmpty(userGrade)) {
		// // CommonUtil.setUserGrade(photoViewHolder.mCtUserInfo,
		// userGrade,"bottom");
		// }
		if (!TextUtils.isEmpty(userName)) {
			photoViewHolder.mCtUserInfo.setText(userName);
		}
		setViewBackGroud(photoViewHolder.mCTGroupCount, null);
//		photoViewHolder.mCTGroupCount.setBackground(null);

		if (!TextUtils.isEmpty(EatCode)) {
			photoViewHolder.mCTGroupCount.setText(EatCode);
		} else {
			photoViewHolder.mCTGroupCount.setText("没有白吃码");
		}
	}
	private void setViewBackGroud(View v,Drawable drawable){
		if (Build.VERSION.SDK_INT >= 16) {

		    v.setBackground(drawable);

		} else {

		    v.setBackgroundDrawable(drawable);
		}
		
		
	}
	public void setGridViewAdapter(final int position,
			final List<GridViewGroupBean> list, GridView view) {

		final JoinMultiMemberAdapter adapter = new JoinMultiMemberAdapter(
				position, mContext, list);

		view.setAdapter(adapter);

		setOnItemClick(position, list, view, adapter);
	}

	public void setOnItemClick(final int position,
			final List<GridViewGroupBean> list, GridView view,
			final JoinMultiMemberAdapter adapter) {
		view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int p,
					long id) {
				if (!JoinMemberAdapter.this.cmp.get(position).get(p)) {
					GridViewGroupBean bean = adapter.updateNotifyAdapter(
							position, p);
					if (bean.getIsUsed().equals("2")) {
						mNotifyChangeData.notifyChange(0, bean);
						// mSignList.add(bean);
					} else {
						mNotifyChangeData.notifyChange(1, bean);
						// mSignList.remove(bean);
					}
					adapter.notifyDataSetChanged();
				} else
					ComponentUtil.showToast(
							TootooPlusEApplication.getAppContext(), "该用户已签到");

			}
		});
	}

	public static class PhotoViewHolder {
		@ViewInject(R.id.iv_photo_list)
		public ImageView ivPhoto;
		@ViewInject(R.id.ct_user_info)
		public CheckedTextView mCtUserInfo;
		@ViewInject(R.id.iv_yes_group)
		public ImageView mIVYestGroup;
		@ViewInject(R.id.ct_group_count)
		public CheckedTextView mCTGroupCount;

	}

	public static class GridViewHolder {
		@ViewInject(R.id.gl_member)
		public GridView mGridViewmember;
	}

	public List<GridViewGroupBean> getmSignList() {
		return mSignList;
	}

	public void setmSignList(List<GridViewGroupBean> mSignList) {
		this.mSignList = mSignList;
	}

	public Map<Integer, Map<Integer, Boolean>> getCmp() {
		return cmp;
	}

	public List<HashMap<String, List<GridViewGroupBean>>> getmList() {
		return mList;
	}

	public Map<Integer, Map<Integer, Boolean>> getMultiGroupListMap() {
		return mMutiMap;
	}

	/**
	 * @Field 进行数据回调，显示选中的白吃码接口
	 * @author wujin
	 * @since 2015/8/6 15:49
	 * 
	 */
	public interface OnNotifyChangeData {

		/**
		 * @Field 回调方法，实现用于返回的状态码与白吃
		 * @param code
		 *            返回状态码 0 代表添加 1 表示取消
		 * @param bean
		 *            选中或取消的签到人数
		 */
		public void notifyChange(int code, GridViewGroupBean bean);
	}

	private OnNotifyChangeData mNotifyChangeData;

	public void setOnNotifyChangeData(OnNotifyChangeData o) {
		mNotifyChangeData = o;
	}
}
