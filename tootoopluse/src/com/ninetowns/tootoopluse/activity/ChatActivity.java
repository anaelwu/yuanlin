package com.ninetowns.tootoopluse.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ninetowns.library.helper.AppManager;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.adapter.ChatPagerAdapter;
import com.ninetowns.tootoopluse.bean.GroupChatBean;
import com.ninetowns.tootoopluse.bean.GroupChatList;
import com.ninetowns.tootoopluse.fragment.BaseChatFragment;
import com.ninetowns.tootoopluse.util.INetConstanst;
import com.ninetowns.tootoopluse.util.UIUtils;
import com.ninetowns.ui.Activity.BaseActivity;
import com.ninetowns.ui.widget.ScrollControlViewPager;

@ContentView(R.layout.chat_activity)
public class ChatActivity extends BaseActivity implements OnClickListener,
		INetConstanst {

	/**
	 * @Fields currIndex : 当前fragment的position
	 */
	private int mCurrIndex;
	/**
	 * @Fields mBackButton :返回按钮
	 */
	@ViewInject(R.id.setting_iv_back)
	private ImageView mBackButton;


	@OnClick(R.id.setting_iv_back)
	public void onBackButtonClick(View v) {
		AppManager.getAppManager().finishActivity(this);
	}

	/**
	 * @Fields mTitleText : 标题文字
	 */
	@ViewInject(R.id.commontitlebar_tv_title)
	private TextView mTitleText;
	/**
	 * @Fields mTabGroupOne : 群组1
	 */
	@ViewInject(R.id.chat_acitivity_tv_groupone)
	private TextView mTabGroupOne;
	/**
	 * @Fields mTabGroupTwo :群组2
	 */
	@ViewInject(R.id.chat_acitivity_tv_grouptwo)
	private TextView mTabGroupTwo;
	/**
	 * @Fields mTabGroupThree :群组3
	 */
	@ViewInject(R.id.chat_acitivity_tv_groupthree)
	private TextView mTabGroupThree;
	/**
	 * @Fields mViewPager : 自定义viewpager
	 */
	@ViewInject(R.id.chat_vPager)
	private ScrollControlViewPager mCustomViewPager;

	/**
	 * @Fields mIVGetAllMember :获取 群组成员 按钮
	 */
	@ViewInject(R.id.commontitlebar_iv_groupicon)
	private ImageView mIVGetAllMember;
	private ArrayList<Fragment> mFragmentsList;

	private ChatPagerAdapter mChatPagerAdapter;
	/**
	 * @Fields mBottomLine : 标题tab底部的线条
	 */
	@ViewInject(R.id.iv_bottom_line)
	private ImageView mBottomLine;
	private int mOffset;
	private int mPositionOne;
	private int mPositionTwo;

	private GroupChatList groupChatList;

	private List<GroupChatBean> groupChatBeans;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		ViewUtils.inject(this);
		try {
			groupChatList = (GroupChatList) getIntent().getSerializableExtra(
					"groupchatlist");
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.e(e.getMessage());
		}
		if (null == groupChatList) {
			LogUtils.e("groupChatList==null  不能为null");
			return;
		}
		mTitleText.setText(groupChatList.getActivityName() );
		UIUtils.setViewVisible(mIVGetAllMember);
		initTabWidth();
		initViewPager();
		setListener();
	}
	public void refreshFragment() {
		 mChatPagerAdapter.notifyDataSetChanged();
	}
	private void initTabWidth() {
		int screenW = UIUtils.getScreenWidth(this);
		groupChatBeans = groupChatList.getGroupChatBeans();
		mFragmentsList = new ArrayList<Fragment>();
		// 3个tab
		if (null != groupChatBeans && groupChatBeans.size() > 0) {
			mOffset = (int) (screenW / groupChatBeans.size());
			int height = UIUtils.dip2px(this, 3);
			mBottomLine.setLayoutParams(new LinearLayout.LayoutParams(mOffset,
					height));
			mPositionOne = (int) (screenW / groupChatBeans.size());
			mTabGroupOne.setText(groupChatBeans.get(0).getGroupName()+"("+groupChatBeans.get(0).getMemberCount()+"人)");
			switch (groupChatBeans.size()) {
			case 1:
				UIUtils.setViewGone(mTabGroupThree);
				UIUtils.setViewGone(mTabGroupTwo);
				break;
			case 2:
				UIUtils.setViewGone(mTabGroupThree);
				mTabGroupTwo.setText(groupChatBeans.get(1).getGroupName()+"("+groupChatBeans.get(1).getMemberCount()+"人)");
				break;
			case 3:
				mPositionTwo = mPositionOne * 2;
				mTabGroupTwo.setText(groupChatBeans.get(1).getGroupName()+"("+groupChatBeans.get(1).getMemberCount()+"人)");
				mTabGroupThree.setText(groupChatBeans.get(2).getGroupName()+"("+groupChatBeans.get(2).getMemberCount()+"人)");
				break;
			}
			for (GroupChatBean groupChatBean : groupChatBeans) {
				BaseChatFragment fragment = new BaseChatFragment();
				Bundle bundle = new Bundle();
				bundle.putString("groupid", groupChatBean.getGroupId());
				fragment.setArguments(bundle);
				mFragmentsList.add(fragment);
			}
		}
	}
	private void setListener() {
		mTabGroupOne.setOnClickListener(this);
		mTabGroupTwo.setOnClickListener(this);
		mTabGroupThree.setOnClickListener(this);
	}
	/**
	 * @Title: getAllMembersOfGroup
	 * @Description: 进入群组成员列表页面
	 * @param @param v 设定文件
	 * @return void 返回类型
	 */
	@OnClick(R.id.commontitlebar_iv_groupicon)
	public void getAllMembersOfGroup(View v) {
		Intent intent = new Intent(this, AllMembersListActivity.class);
		intent.putExtra("groupid", groupChatBeans.get(mCurrIndex).getGroupId());
		intent.putExtra("group_type", groupChatBeans.get(mCurrIndex).getType());
		startActivity(intent);
	}

	private void initViewPager() {
		mChatPagerAdapter = new ChatPagerAdapter(getSupportFragmentManager(),
				mFragmentsList);
		
		// mCustomViewPager.setDispatch(true);
		mCustomViewPager.setAdapter(mChatPagerAdapter);
		// 设置预加载的数量 可以缓存
		// mCustomViewPager.setOffscreenPageLimit(mFragmentsList.size());
		// 设置预加载的数量 可以缓存
		mCustomViewPager.setOffscreenPageLimit(3);
		mCustomViewPager.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
		mCustomViewPager.setOnPageChangeListener(mPagerListener);
	}
	/**
	 * @Fields mPagerListener : 监听viewpager的变化
	 */
	private ViewPager.SimpleOnPageChangeListener mPagerListener = new ViewPager.SimpleOnPageChangeListener() {
		Animation animation = null;

		@Override
		public void onPageSelected(int position) {
			int tabSelectedTextColor=getResources().getColor(R.color.tab_selected_textcolor);
			int tabUnSelectedTextColor=getResources().getColor(R.color.tab_unSelected_textcolor);
			switch (position) {
			case 0://
			changeTabOneTextColor(tabSelectedTextColor,tabUnSelectedTextColor);
				if (mCurrIndex == 1) {
					animation = new TranslateAnimation(mPositionOne, 0, 0, 0);
				} else if (mCurrIndex == 2) {
					animation = new TranslateAnimation(mPositionTwo, 0, 0, 0);
				}
				break;
			case 1://
				changeTabTwoTextColor(tabSelectedTextColor,tabUnSelectedTextColor);
				if (mCurrIndex == 0) {
					animation = new TranslateAnimation(0, mPositionOne, 0, 0);
				} else if (mCurrIndex == 2) {
					animation = new TranslateAnimation(mPositionTwo,
							mPositionOne, 0, 0);
				}
				break;
			case 2://
				changeTabThreeTextColor(tabSelectedTextColor,tabUnSelectedTextColor);
				if (mCurrIndex == 0) {
					animation = new TranslateAnimation(0, mPositionTwo, 0, 0);
				} else if (mCurrIndex == 1) {
					animation = new TranslateAnimation(mPositionOne,
							mPositionTwo, 0, 0);
				}
				break;
			}
			mCurrIndex = position;
			animation.setFillAfter(true);
			animation.setDuration(300);
			mBottomLine.startAnimation(animation);
		}
	};
	/**
	 * @param tabUnSelectedTextColor 
	 * @param tabSelectedTextColor  
	* @Title: changeTabOneTextColor 
	* @Description: 设置第一个 tab被选中 
	* @param     设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	private void changeTabOneTextColor(int tabSelectedTextColor, int tabUnSelectedTextColor) {
		mTabGroupOne.setTextColor(tabSelectedTextColor);
		mTabGroupTwo.setTextColor(tabUnSelectedTextColor);
		mTabGroupThree.setTextColor(tabUnSelectedTextColor);
	}
	/** 
	 * @Title: changeTabTwoTextColor 
	 * @Description: 设置第二 个 tab被选中 
	 * @param     设定文件 
	 * @return void    返回类型 
	 * @throws 
	 */
	private void changeTabTwoTextColor(int tabSelectedTextColor, int tabUnSelectedTextColor) {
		mTabGroupOne.setTextColor(tabUnSelectedTextColor);
		mTabGroupTwo.setTextColor(tabSelectedTextColor);
		mTabGroupThree.setTextColor(tabUnSelectedTextColor);
	}
	/** 
	 * @Title: changeTabThreeTextColor 
	 * @Description: 设置第一个 tab被选中 
	 * @param     设定文件 
	 * @return void    返回类型 
	 * @throws 
	 */
	private void changeTabThreeTextColor(int tabSelectedTextColor, int tabUnSelectedTextColor) {
		mTabGroupOne.setTextColor(tabUnSelectedTextColor);
		mTabGroupTwo.setTextColor(tabUnSelectedTextColor);
		mTabGroupThree.setTextColor(tabSelectedTextColor);
	}
	@Override
	public void onClick(View v) {

		int index = 0;
		switch (v.getId()) {
		case R.id.chat_acitivity_tv_groupone:
			index = 0;

			break;
		case R.id.chat_acitivity_tv_grouptwo:
			index = 1;
			break;
		case R.id.chat_acitivity_tv_groupthree:
			index = 2;
			break;
		}
		mCustomViewPager.setCurrentItem(index);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(Activity.RESULT_OK==resultCode&&null!=mChatPagerAdapter){
			LogUtils.i("resultCode"+resultCode);
			mChatPagerAdapter.getItem(mCurrIndex).onActivityResult(requestCode,
					resultCode, data);
		}
	}
}
