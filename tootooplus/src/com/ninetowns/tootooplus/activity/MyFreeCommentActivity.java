package com.ninetowns.tootooplus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.fragment.MyFreeCommentNoPassedFragment;
import com.ninetowns.tootooplus.fragment.MyFreeCommentPassedFragment;
import com.ninetowns.tootooplus.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.ui.Activity.FragmentGroupActivity;

/**
 * 
 * @ClassName: MyFreeCommentActivity
 * @Description: 我的点评  已通过未通过
 * @author wuyulong
 * @date 2015-2-4 下午5:46:11
 * 
 */
public class MyFreeCommentActivity extends FragmentGroupActivity implements
		OnCheckedChangeListener {
	private Bundle bundle;

	@ViewInject(R.id.iv_left)
	private ImageButton mIVBack;// 返回上一级
	@ViewInject(R.id.rg_my_wis)
	private RadioGroup mRGMyWis;
	@ViewInject(R.id.rbtn_my_wish_passed)
	private RadioButton mRbtnPassed;// 已通过
	@ViewInject(R.id.rbtn_my_free_no_passed)
	private RadioButton mRbtnMyFreeNoPassend;// 未通过
	@ViewInject(R.id.fragment_stub)
	private FrameLayout mFlContainer;
	@ViewInject(R.id.iv_right)
	private ImageButton mIVRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_free_comment_activity);
		getType();
		ViewUtils.inject(this);
		mRGMyWis.setOnCheckedChangeListener(this);

	}

	@OnClick(R.id.iv_left)
	public void backReturn(View view) {
		finish();

	}

	/**
	 * 
	 * @Title: getType
	 * @Description: bundle 参数
	 * @param
	 * @return void 返回类型
	 * @throws
	 */
	private void getType() {
		bundle = getIntent().getBundleExtra(ConstantsTooTooEHelper.BUNDLE);

	}

	@Override
	protected void initPrimaryFragment() {
		switchTab(R.id.rbtn_my_wish_passed);

	}

	@Override
	protected Class<? extends Fragment> getPrimaryFragmentClass(int fragmentId) {
		// 根据不同TAB

		Class<? extends Fragment> clazz = null;
		switch (fragmentId) {
		case R.id.rbtn_my_wish_passed:// 已通过
			clazz = MyFreeCommentPassedFragment.class;
			break;
		case R.id.rbtn_my_free_no_passed:// 未通过
			clazz = MyFreeCommentNoPassedFragment.class;
			break;

		default:
			throw new IllegalArgumentException();
		}

		return clazz;
	}

	@Override
	protected Bundle getPrimaryFragmentArguments(int fragmentId) {
		return null;
	}

	@Override
	protected int getPrimaryFragmentStubId() {
		return R.id.fragment_stub;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switchTab(checkedId);

	}

	/**
	 * 
	 * @Title: skipToDraftAct
	 * @Description: 跳转到草稿界面
	 * @param
	 * @return
	 * @throws
	 */
	@SuppressWarnings("unused")
	@OnClick(R.id.iv_right)
	private void skipToDraftAct(View v) {
		String userid = SharedPreferenceHelper
				.getLoginUserId(TootooPlusApplication.getAppContext());
		if (!TextUtils.isEmpty(userid)) {
			Intent intent = new Intent(this, MyDraftFreeCommentActivity.class);
			// Bundle bundle=new Bundle();
			// ConstantsTooTooEHelper.putView(
			// ConstantsTooTooEHelper.isDraftView, bundle);
			// intent.putExtra(ConstantsTooTooEHelper.BUNDLE, bundle);
			startActivity(intent);

		} else {
			ComponentUtil.showToast(TootooPlusApplication.getAppContext(),
					"没有登陆");
		}
	}

	/**
	 * 
	 * @Title: switchTab
	 * @Description: 切换tab页
	 * @param
	 * @return
	 * @throws
	 */
	private void switchTab(int checkedId) {
		switch (checkedId) {
		case R.id.rbtn_my_wish_passed:
			mRGMyWis.setBackgroundResource(R.drawable.applay_yes);

			break;
		case R.id.rbtn_my_free_no_passed:
			mRGMyWis.setBackgroundResource(R.drawable.applay_no);
			break;

		}
		switchPrimaryFragment(checkedId);
	}

}