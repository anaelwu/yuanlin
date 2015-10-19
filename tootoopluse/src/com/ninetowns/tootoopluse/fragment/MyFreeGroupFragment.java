package com.ninetowns.tootoopluse.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.activity.MyJoinMemberActivity;
import com.ninetowns.tootoopluse.bean.FreeGroupBean;
import com.ninetowns.tootoopluse.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootoopluse.helper.SharedPreferenceHelper;
import com.ninetowns.tootoopluse.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootoopluse.parser.MyFreeGroupParser;
import com.ninetowns.tootoopluse.util.CommonUtil;
import com.ninetowns.ui.fragment.PageListFragment;
import com.ninetowns.ui.widget.refreshable.PullToRefreshBase;
import com.ninetowns.ui.widget.refreshable.RefreshableListView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyFreeGroupFragment extends
		PageListFragment<ListView, List<FreeGroupBean>, MyFreeGroupParser> {

	// 总页数
	private int totalPage;

	private RefreshableListView free_group_lv;

	private List<FreeGroupBean> freeGroupList = new ArrayList<FreeGroupBean>();

	private FreeGroupLvAdapter freeGroupLvAdapter = null;

	private String group_type = "";

	private TextView mTv;

	public MyFreeGroupFragment() {
		// TODO Auto-generated constructor stub
	}

	public MyFreeGroupFragment(String group_type) {
		this.group_type = group_type;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onLoadData(true, true, false);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	protected View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.my_free_group_fragment, null);
		ViewUtils.inject(this, view);
		free_group_lv = (RefreshableListView) view
				.findViewById(R.id.free_group_lv);
		// view.findViewById(R.id.ll_left);
		view.findViewById(R.id.ibtn_right).setVisibility(View.INVISIBLE);
		if (isAdded()) {
			String strmyFree = getString(R.string.my_free_group);
			mTv = (TextView) view.findViewById(R.id.tv_title);
			mTv.setText(strmyFree);
		}

		return view;
	}

	@OnClick(R.id.ibtn_left)
	public void finishAct(View v) {
		if (getActivity() != null)
			getActivity().finish();
	}

	@Override
	public void getPageListParserResult(List<FreeGroupBean> parserResult) {
		if (super.currentpage == 1) {
			freeGroupList.clear();
		}
		if(parserResult != null&&parserResult.size()>0){
			int moreSize = parserResult.size();
			freeGroupList.addAll(parserResult);

			freeGroupLvAdapter = new FreeGroupLvAdapter(getActivity(),
					freeGroupList);

			free_group_lv.setAdapter(freeGroupLvAdapter);

			if (super.currentpage != 1) {
				free_group_lv.getRefreshableView().setSelection(
						freeGroupList.size()-moreSize+1);
			}
		}
	

	}

	@Override
	protected PullToRefreshBase<ListView> initRefreshIdView() {
		return free_group_lv;
	}

	@Override
	public int getTotalPage() {
		return totalPage;
	}

	@Override
	public MyFreeGroupParser setParser(String str) {
		MyFreeGroupParser myFreeGroupParser = new MyFreeGroupParser(str);

		totalPage = myFreeGroupParser.getTotalPage();

		return myFreeGroupParser;
	}

	@Override
	public RequestParamsNet getApiParmars() {
		RequestParamsNet requestParamsNet = new RequestParamsNet();
		requestParamsNet
				.setmStrHttpApi(TootooeNetApiUrlHelper.MY_FREE_GROUP_URL);
		requestParamsNet.addQueryStringParameter(
				TootooeNetApiUrlHelper.USER_ID,
				SharedPreferenceHelper.getLoginUserId(getActivity()));
		requestParamsNet.addQueryStringParameter(
				TootooeNetApiUrlHelper.PAGE_SIZE,
				String.valueOf(TootooeNetApiUrlHelper.PAGESIZE_DRAFT));
		requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.PAGE,
				String.valueOf(currentpage));
		return requestParamsNet;
	}

	class FreeGroupLvAdapter extends BaseAdapter {

		private Context context;

		private List<FreeGroupBean> list;

		public FreeGroupLvAdapter(Context context, List<FreeGroupBean> list) {

			this.context = context;

			this.list = list;
		}

		@Override
		public int getCount() {
			if (list != null && list.size() > 0) {
				return list.size();
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder vh = null;
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.free_group_lv_item, null);
				vh.free_group_item_layout = (LinearLayout) convertView
						.findViewById(R.id.free_group_item_layout);
				vh.free_group_item_iv = (ImageView) convertView
						.findViewById(R.id.free_group_item_iv);
				vh.free_group_item_tv = (TextView) convertView
						.findViewById(R.id.free_group_item_tv);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}

			ImageLoader.getInstance().displayImage(
					list.get(position).getCoverThumb(), vh.free_group_item_iv,
					CommonUtil.OPTIONS_ALBUM);
			if (list.get(position) != null) {
//				vh.free_group_item_tv.setText(list.get(position)
//						.getActivityName()
//						+ "("
//						+ list.get(position).getMemberCount() + ")");
				vh.free_group_item_tv.setText(list.get(position)
						.getActivityName());
			} else {
				vh.free_group_item_tv.setText("");
			}

			vh.free_group_item_layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent intent = new Intent(context,
							MyJoinMemberActivity.class);
					intent.putExtra(ConstantsTooTooEHelper.GROUP_ID,
							list.get(position).getGroupId());
					intent.putExtra(ConstantsTooTooEHelper.ACTIVITYID,
							list.get(position).getActivityId());
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			});

			vh.free_group_item_iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

				}
			});

			return convertView;
		}

		class ViewHolder {
			ImageView free_group_item_iv;

			TextView free_group_item_tv;

			LinearLayout free_group_item_layout;
		}
	}
}
