package com.ninetowns.tootooplus.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lidroid.xutils.ViewUtils;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.fragment.EditextWishFragment;
import com.ninetowns.tootooplus.fragment.RecommendWishFragment;
import com.ninetowns.tootooplus.helper.ConstantsTooTooEHelper;
import com.ninetowns.ui.Activity.FragmentGroupActivity;

/**
 * 
 * @ClassName: CreateWishActivity
 * @Description: 创建心愿创建故事
 * @author wuyulong
 * @date 2015-1-23 下午4:14:03
 * 
 */
public class CreateWishActivity extends FragmentGroupActivity {
	private Bundle bundle;
	private boolean isEditextView;
	private boolean isCreateView;
	private boolean isDraftView;
	private boolean isConvertView;
	private boolean isRecommendView;
	private boolean isConvertRecommendView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_wish_activity);
		getType();
		ViewUtils.inject(this);

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
		isEditextView = bundle.getBoolean(ConstantsTooTooEHelper.isEditextView);
		isCreateView = bundle.getBoolean(ConstantsTooTooEHelper.isCreateView);
		isDraftView = bundle.getBoolean(ConstantsTooTooEHelper.isDraftView);
		isConvertView = bundle.getBoolean(ConstantsTooTooEHelper.isConvertView);
		isRecommendView = bundle
				.getBoolean(ConstantsTooTooEHelper.isRecommendView);
		isConvertRecommendView = bundle
				.getBoolean(ConstantsTooTooEHelper.isConvertRecommendView);

	}

	@Override
	protected void initPrimaryFragment() {
		switchPrimaryFragment(R.id.fragment_stub);

	}

	@Override
	protected Class<? extends Fragment> getPrimaryFragmentClass(int fragmentId) {
		Class<? extends Fragment> clazz = null;
		// 根据不同的type显示是创建界面 还是 推荐创建界面 还是编辑界面
		if (isCreateView) {
		} else if (isEditextView) {
			clazz = EditextWishFragment.class;
		} else if (isConvertView) {
		} else if (isDraftView) {
		} else if (isRecommendView) {

		} else if (isConvertRecommendView) {
			clazz = RecommendWishFragment.class;
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

}