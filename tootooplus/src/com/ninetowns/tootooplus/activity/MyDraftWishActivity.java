package com.ninetowns.tootooplus.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lidroid.xutils.ViewUtils;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.fragment.MyDraftFragment;
import com.ninetowns.tootooplus.helper.ConstantsTooTooEHelper;
import com.ninetowns.ui.Activity.FragmentGroupActivity;
/**
 * 
* @ClassName: MyWishActivity 
* @Description: 我的心愿草稿
* @author wuyulong
* @date 2015-2-4 下午5:46:11 
*
 */
public class MyDraftWishActivity extends FragmentGroupActivity {
	private Bundle bundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_draft_activity);
		getType();
		ViewUtils.inject(this);
		
	}
	/**
	 * 
	* @Title: getType 
	* @Description: bundle 参数
	* @param    
	* @return void    返回类型 
	* @throws
	 */
private void getType(){
	  bundle = getIntent().getBundleExtra(ConstantsTooTooEHelper.BUNDLE);
	
}
	@Override
	protected void initPrimaryFragment() {
		switchPrimaryFragment(R.id.fragment_stub);

	}

	@Override
	protected Class<? extends Fragment> getPrimaryFragmentClass(int fragmentId) {
        Class<? extends Fragment> clazz = null;
        //根据不同的type显示是创建界面 还是 推荐创建界面 还是编辑界面
        	clazz=MyDraftFragment.class;

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