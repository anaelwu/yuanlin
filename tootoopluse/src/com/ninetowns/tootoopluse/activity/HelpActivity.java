package com.ninetowns.tootoopluse.activity;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.adapter.HelpLvAdapter;
import com.ninetowns.tootoopluse.bean.HelpBean;
import com.ninetowns.tootoopluse.parser.HelpParser;
import com.ninetowns.tootoopluse.util.CommonUtil;
import com.ninetowns.tootoopluse.util.INetConstanst;
import com.ninetowns.ui.Activity.BaseActivity;

public class HelpActivity extends BaseActivity implements INetConstanst {
	private ListView help_lv;

	HelpLvAdapter helpLvAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

		// 返回按钮
		LinearLayout two_or_one_btn_head_back = (LinearLayout) findViewById(R.id.two_or_one_btn_head_back);
		two_or_one_btn_head_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HelpActivity.this.finish();
			}
		});
		// 标题
		TextView two_or_one_btn_head_title = (TextView) findViewById(R.id.two_or_one_btn_head_title);
		two_or_one_btn_head_title.setText("帮助");

		help_lv = (ListView) findViewById(R.id.help_lv);

		loadDataFromServer();
	}

	private void loadDataFromServer() {

		RequestParamsNet paramsNet = new RequestParamsNet();
		// 1为用户版 0为商户版
		paramsNet.addQueryStringParameter("Type", 0 + "");
		CommonUtil.xUtilsGetSend(URL_HELP, paramsNet,new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				HelpParser parser = new HelpParser();
				if(responseInfo.result==null){
					return;
				}
				List<HelpBean> helpBeans = parser
						.getParseResult(responseInfo.result);
				if(helpBeans!=null){
					HelpLvAdapter helpLvAdapter = new HelpLvAdapter(
							HelpActivity.this, helpBeans);
					help_lv.setAdapter(helpLvAdapter);
				}

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub

			}
		});

	}

}
