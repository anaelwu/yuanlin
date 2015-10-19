package com.ninetowns.tootooplus.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.fragment.ChatMessageHistoryFragment;
import com.ninetowns.ui.Activity.FragmentGroupActivity;

/** 
* @ClassName: ChatMessageHistoryActivity 
* @Description: 聊天记录界面
* @author zhou
* @date 2015-2-12 上午11:01:19 
*  
*/
public class ChatMessageHistoryActivity extends FragmentGroupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_wish_activity);
	}


	@Override
	protected void initPrimaryFragment() {
		switchPrimaryFragment(R.id.fragment_stub);

	}

	@Override
	protected Class<? extends Fragment> getPrimaryFragmentClass(int fragmentId) {
		Class<? extends Fragment> clazz = null;

		clazz = ChatMessageHistoryFragment.class;// 录制视频

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