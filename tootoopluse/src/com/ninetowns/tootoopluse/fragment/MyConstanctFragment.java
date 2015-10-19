package com.ninetowns.tootoopluse.fragment;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.adapter.MyConstanctAdapter;
import com.ninetowns.tootoopluse.application.TootooPlusEApplication;
import com.ninetowns.tootoopluse.bean.AddressListBean;
import com.ninetowns.tootoopluse.bean.MyConstanctBean;
import com.ninetowns.tootoopluse.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootoopluse.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootoopluse.parser.MyConstanctMemGroupParser;
import com.ninetowns.tootoopluse.util.CommonUtil;
import com.ninetowns.tootoopluse.util.UIUtils;
import com.ninetowns.ui.fragment.BaseFragment;

public class MyConstanctFragment extends
		BaseFragment<List<MyConstanctBean>, MyConstanctMemGroupParser> {
	private String act_id = "";

	@ViewInject(R.id.two_or_one_btn_head_title)
	private TextView mTvHeader;
	@ViewInject(R.id.two_or_one_btn_head_back)
	private LinearLayout mLLBack;
	@ViewInject(R.id.address_fragment_lv)
	private ListView mListView;

	private View mCopy;
	private List<MyConstanctBean> localData;

	@OnClick(R.id.two_or_one_btn_head_back)
	public void destoryAct(View v) {
		if (isAdded()) {
			getActivity().finish();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.address_list_fragment, null);
		ViewUtils.inject(this, view);
		TextView mTvCopy = (TextView) view
				.findViewById(R.id.two_or_one_btn_head_second_tv);
		mTvCopy.setVisibility(View.VISIBLE);
		mTvCopy.setText(R.string.copy_all);
		mTvCopy.setTextSize(UIUtils.px2Sp(
				TootooPlusEApplication.getAppContext(),
				(int) TootooPlusEApplication.getAppContext().getResources()
						.getDimension(R.dimen.h3)));
		mCopy = view.findViewById(R.id.two_or_one_btn_head_second_layout);
		mTvHeader.setText("联系方式");
		return view;
	}

	@OnClick(R.id.two_or_one_btn_head_second_layout)
	public void setOnClickCopy(View v) {
		if (localData != null && localData.size() > 0) {

			if (localData != null && localData.size() > 0) {
				StringBuilder strbuild = new StringBuilder();
				for (MyConstanctBean iterable_element : localData) {
					String realName = iterable_element.getName();
					String count = iterable_element.getMemberCount();
					String phoneNumber = iterable_element.getPhone();
					strbuild.append(realName).append("  (" + count + "人)")
							.append("　　").append(phoneNumber);
				}
				if (getActivity() != null) {
					CommonUtil.copyContent(strbuild.toString(), getActivity());
					ComponentUtil.showToast(getActivity(), "复制成功");
				}

			}

		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		super.onLoadData(true, true, true);
	}

	@Override
	public MyConstanctMemGroupParser setParser(String str) {
		return new MyConstanctMemGroupParser(str);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		act_id = getActivity().getIntent().getStringExtra(
				TootooeNetApiUrlHelper.ACTIVITYID);
	}

	@Override
	public RequestParamsNet getApiParmars() {
		RequestParamsNet par = new RequestParamsNet();
		par.setmStrHttpApi(TootooeNetApiUrlHelper.GET_BIG_EAT_CONSTANCT);
		par.addQueryStringParameter(ConstantsTooTooEHelper.ACTIVITYID, act_id);
		return par;
	}

	@Override
	public void getParserResult(List<MyConstanctBean> parserResult) {
		if (localData != null && localData.size() > 0) {
			localData.clear();

		}
		if (parserResult != null && parserResult.size() > 0) {
			localData = parserResult;
			MyConstanctAdapter adapter = new MyConstanctAdapter(parserResult,
					getActivity());
			mListView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}

	}

}