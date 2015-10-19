package com.ninetowns.tootooplus.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.library.util.MyTextwatcherUtil;
import com.ninetowns.library.util.NetworkUtil;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.adapter.DragAdapter;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.bean.StoryDetailListBean;
import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootooplus.util.CommonUtil;
import com.ninetowns.ui.widget.dialog.BaseFragmentDialog;

public class TextEditextDialogFragment extends BaseFragmentDialog implements
		View.OnClickListener {

	private View view;
	private ImageButton back;
	private EditText mEditext;
	private TextView mTextCount;
	private TextView mTextTitle;
	private Fragment operateFragment;
	private DragAdapter adatper;
	private StoryDetailListBean sdlbean;
	private String storyId;
	private String pageid;
	private ImageButton mIMBtnRight;

	public TextEditextDialogFragment(Fragment operateFragment,
			DragAdapter adatper, StoryDetailListBean sdlbean, String storyId) {
		this.operateFragment = operateFragment;
		this.adatper = adatper;
		this.sdlbean = sdlbean;
		this.storyId = storyId;
		pageid = sdlbean.getPageId();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.editext_text_dialog_fragment, null);
		mEditext = (EditText) view.findViewById(R.id.et_text_des);
		
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) mEditext.getLayoutParams();
		int height = CommonUtil.getHeight(TootooPlusApplication.getAppContext());
		params.height=height;
		mTextCount = (TextView) view.findViewById(R.id.count);
		back = (ImageButton) view.findViewById(R.id.title_bar_left_btn);
		mTextTitle = (TextView) view.findViewById(R.id.title_bar_title);
		mTextTitle.setText(getResources().getString(R.string.editor_text));
		mIMBtnRight = (ImageButton) view
				.findViewById(R.id.title_bar_title_right);
		view.findViewById(R.id.title_bar_right_btn_first).setVisibility(
				View.GONE);
		back.setImageResource(R.drawable.btn_return_gray);
		mEditext.setText(sdlbean.getPageContent());
		mEditext.setSelection(mEditext.length());
		mTextCount = (TextView) view.findViewById(R.id.count);
		int fontSize = sdlbean.getFontSize();
		if (fontSize == 1) {
			mEditext.setTextSize(CommonUtil.convertPxToDp(
					TootooPlusApplication.getAppContext(),
					(int) TootooPlusApplication.getAppContext().getResources()
							.getDimension(R.dimen.h1)));
			mEditext.addTextChangedListener(new MyTextwatcherUtil(this
					.getActivity(), mEditext, mTextCount, 20));// 标题
		} else {
			mEditext.setTextSize(CommonUtil.convertPxToDp(
					TootooPlusApplication.getAppContext(),
					(int) TootooPlusApplication.getAppContext().getResources()
							.getDimension(R.dimen.h4)));
			mEditext.addTextChangedListener(new MyTextwatcherUtil(this
					.getActivity(), mEditext, mTextCount, 500));// 最多输入1000个字符
		}

		mEditext.setHint(TootooPlusApplication.getAppContext().getResources()
				.getString(R.string.editor_text_num_fivehundred));
		back.setOnClickListener(this);
		mIMBtnRight.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_bar_left_btn:
			dismiss();
			break;
		case R.id.title_bar_title_right:
			String textcontent = mEditext.getText().toString();
			if (!TextUtils.isEmpty(textcontent)) {
				mIMBtnRight.setFocusableInTouchMode(false);
				if(!textcontent.equals(sdlbean.getPageContent())){
					postDataEdit(storyId, pageid, textcontent);
				}else{
					mIMBtnRight.setFocusableInTouchMode(true);
					ComponentUtil.showToast(TootooPlusApplication.getAppContext(), "您还没有修改任何东西");
				}
				
			} else {
				mIMBtnRight.setFocusableInTouchMode(true);
			}

			break;

		default:
			break;
		}
	}

	private void postDataEdit(String storyid, String pageid,
			final String content) {
		if ((NetworkUtil.isNetworkAvaliable(this.getActivity()))) {// 有网络

			// StoryId：故事Id (必填)
			// PageId：故事页Id (必填)
			// PageContent：故事页文字
			// PageImg：故事页图片或直播录播封面图地址
			// PageVideoUrl：故事页录播视频地址
			// RecordId：录制Id
			// PageDesc：故事页描述

			RequestParamsNet params = new RequestParamsNet();
			params.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYCREATE_STORYID, storyid);
			params.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYCREATE_PAGEID, pageid);
			params.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYCREATE_PAGE_CONTENT, content);
			params.addQueryStringParameter(
					TootooeNetApiUrlHelper.STORYCREATE_PAGE_TYPE, "1");

			CommonUtil.xUtilsPostSend(
					TootooeNetApiUrlHelper.STORYCREATE_PAGE_UPDATE, params,
					new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String strObj = responseInfo.result;
							mIMBtnRight.setFocusableInTouchMode(true);
							try {
								JSONObject jsobj = new JSONObject(strObj);
								if (jsobj.has("Status")) {
									String status = jsobj.getString("Status");
									if (status.equals("1")) {
										hideWindwon();

									}
									sdlbean.setPageContent(content);
									adatper.notifyDataSetChanged();

								}

							} catch (JSONException e) {
								e.printStackTrace();
								mIMBtnRight.setFocusableInTouchMode(true);
							}

						}


						@Override
						public void onFailure(HttpException error, String msg) {
							mIMBtnRight.setFocusableInTouchMode(true);

						}

					});

		} else {
			mIMBtnRight.setFocusableInTouchMode(true);
			ComponentUtil.showToast(
					getActivity(),
					this.getActivity()
							.getResources()
							.getString(
									R.string.errcode_network_response_timeout));
		}
	}

	/** 
	* @Title: hideWindwon 
	* @Description: TODO
	* @param  
	* @return   
	* @throws 
	*/
	private void hideWindwon() {
		InputMethodManager imm = (InputMethodManager) TootooPlusApplication
				.getAppContext()
				.getSystemService(
						Context.INPUT_METHOD_SERVICE);
		if (null != mEditext)
			imm.hideSoftInputFromWindow(
					mEditext.getWindowToken(),
					0);
		dismiss();
	}

}
