package com.ninetowns.tootooplus.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ninetowns.library.helper.AppManager;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.adapter.ChatDataAdapter;
import com.ninetowns.tootooplus.adapter.ChatPagerAdapter;
import com.ninetowns.tootooplus.bean.GroupChatBean;
import com.ninetowns.tootooplus.bean.GroupChatList;
import com.ninetowns.tootooplus.bean.SendMessageBean;
import com.ninetowns.tootooplus.fragment.BaseChatFragment;
import com.ninetowns.tootooplus.fragment.BaseChatFragment.ReceiveDataListener;
import com.ninetowns.tootooplus.fragment.FirstActChatDialog;
import com.ninetowns.tootooplus.fragment.FirstActChatDialog.OnActDialogStatus;
import com.ninetowns.tootooplus.fragment.FirstBaichiChatDialog;
import com.ninetowns.tootooplus.fragment.FirstBaichiChatDialog.OnDialogBaichiStatus;
import com.ninetowns.tootooplus.fragment.FirstChatWisDialog;
import com.ninetowns.tootooplus.fragment.FirstChatWisDialog.OnWisDialogStatus;
import com.ninetowns.tootooplus.helper.ConstantsTooTooEHelper;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.util.CommonUtil;
import com.ninetowns.tootooplus.util.INetConstanst;
import com.ninetowns.tootooplus.util.UIUtils;
import com.ninetowns.ui.Activity.BaseActivity;
import com.ninetowns.ui.widget.ScrollControlViewPager;
import com.ninetowns.ui.widget.dialog.BaseFragmentDialog;
import com.wiriamubin.service.chat.MyChatData;

@SuppressLint("UseSparseArrays")
@ContentView(R.layout.chat_activity)
public class ChatActivity extends BaseActivity implements OnClickListener,
		INetConstanst, OnWisDialogStatus, OnActDialogStatus,
		OnDialogBaichiStatus, ReceiveDataListener {

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
	public int myCurrentPosition = 0;
	public HashMap<Integer, List<MyChatData>> hashMap = new HashMap<Integer, List<MyChatData>>();
	public HashMap<Integer, ChatDataAdapter> hashMapAdapter = new HashMap<Integer, ChatDataAdapter>();

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
		mTitleText.setText(groupChatList.getActivityName());
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
			// String type = groupChatBeans.get(0).getType();
			// if (groupChatBeans.size() > 1) {// 换位置
			// String positionType = groupChatBeans.get(0).getType();
			// if (!TextUtils.isEmpty(positionType)
			// && positionType.equals("1")) {// 群聊
			// groupChatBeans.set(0, groupChatBeans.get(1));
			// groupChatBeans.set(1, groupChatBeans.get(0));
			//
			// }
			// }
			// mTabGroupOne.setText(groupChatBeans.get(0).getGroupName() + "("
			// + groupChatBeans.get(0).getMemberCount() + "人)");
			// justActWisBaiChi();
			// justActWisBai(groupChatBeans);
			justActWisBaiChi(groupChatBeans);

		}
	}

	private void justActWisBaiChi(List<GroupChatBean> chats) {
		@SuppressWarnings("unused")
		int localIndex = 0;
		if (chats != null && chats.size() > 0) {
			for (int i = 0; i < chats.size(); i++) {
				GroupChatBean item = chats.get(i);
				String itemteype = item.getType();
				if (!TextUtils.isEmpty(itemteype)) {
					if (itemteype.equals("3") && i == 0) {// 白吃团

					} else {
						if (itemteype.equals("3")) {
							localIndex = i;
							break;
						}
					}
				}
			}
		}
		if (chats.size() > 1 && localIndex != 0) {// 换位置
			GroupChatBean firstPosition = chats.get(0);
			chats.set(0, chats.get(localIndex));
			chats.set(localIndex, firstPosition);
		}

		mTabGroupOne.setText(chats.get(0).getGroupName() + "("
				+ chats.get(0).getMemberCount() + "人)");
		if (chats != null && chats.size() > 0) {
			switch (chats.size()) {
			case 1:
				justShowGuide(chats.get(0).getType());
				UIUtils.setViewGone(mTabGroupThree);
				UIUtils.setViewGone(mTabGroupTwo);
				break;
			case 2:
				justShowGuide(chats.get(0).getType());// 第一个必须是白吃团
				UIUtils.setViewGone(mTabGroupThree);
				mTabGroupTwo.setText(chats.get(1).getGroupName() + "("
						+ chats.get(1).getMemberCount() + "人)");
				break;

			}

		}
		for (int i = 0; i < chats.size(); i++) {
			BaseChatFragment fragment = new BaseChatFragment();
			if (chats != null && chats.size() > 0) {
				if (i == 0) {
					myCurrentGroupId = chats.get(i).getGroupId();

				}

			}
			fragment.setOnReceiveDataListener(this);
			Bundle bundle = new Bundle();
			bundle.putString("groupid", chats.get(i).getGroupId());
			fragment.setArguments(bundle);
			mFragmentsList.add(fragment);
		}

	}

	/**
	 * @Title: justActWisBai
	 * @Description: TODO
	 * @param
	 * @return
	 * @throws
	 */
	private void justActWisBai(String type) {
		switch (groupChatBeans.size()) {
		case 1:// 活动群聊或者心愿群聊
			LogUtils.e("群聊1：" + groupChatBeans.get(0).getGroupName());
			justShowGuide(type);
			UIUtils.setViewGone(mTabGroupThree);
			UIUtils.setViewGone(mTabGroupTwo);
			break;
		case 2:// 白吃团 或者活动群聊
			LogUtils.e("群聊2：" + groupChatBeans.get(1).getGroupName());
			justShowGuide(type);
			UIUtils.setViewGone(mTabGroupThree);
			mTabGroupTwo.setText(groupChatBeans.get(1).getGroupName() + "("
					+ groupChatBeans.get(1).getMemberCount() + "人)");
			break;
		case 3:
			LogUtils.e("群聊3：" + groupChatBeans.get(2).getGroupName());
			mPositionTwo = mPositionOne * 2;
			mTabGroupTwo.setText(groupChatBeans.get(1).getGroupName() + "("
					+ groupChatBeans.get(1).getMemberCount() + "人)");
			mTabGroupThree.setText(groupChatBeans.get(2).getGroupName() + "("
					+ groupChatBeans.get(2).getMemberCount() + "人)");
			break;
		}
	}

	/**
	 * @Title: justShowGuide
	 * @Description: TODO
	 * @param
	 * @return
	 * @throws
	 */
	private void justShowGuide(String type) {
		if (!TextUtils.isEmpty(type)) {
			if (type.equals("-1")) {// 心愿
				boolean isFirstGuideWisChat = SharedPreferenceHelper
						.getFirstGuideWisChat(this);
				if (isFirstGuideWisChat) {
					FirstChatWisDialog wishchat = (FirstChatWisDialog) CommonUtil
							.showFirstGuideDialog(this,
									ConstantsTooTooEHelper.FIRST_GUIDE_WIS_CHAT);
					wishchat.setOnDialogStatus(this);
				}

			} else if (type.equals("1")) {// 活动
				boolean isFirstGuideActChat = SharedPreferenceHelper
						.getFirstGuideActChat(this);
				if (isFirstGuideActChat) {
					FirstActChatDialog actChatDialog = (FirstActChatDialog) CommonUtil
							.showFirstGuideDialog(this,
									ConstantsTooTooEHelper.FIRST_GUIDE_ACT_CHAT);
					if (groupChatBeans != null && groupChatBeans.size() > 1) {
						actChatDialog.setRight(true);
					}

					actChatDialog.setOnDialogStatus(this);
				}

			} else if (type.equals("3")) {
				// 白吃团
				// 活动
				boolean isFirstGuideActChat = SharedPreferenceHelper
						.getFirstGuideBaiChiChat(this);
				if (isFirstGuideActChat) {
					FirstBaichiChatDialog baichiChatDialog = (FirstBaichiChatDialog) CommonUtil
							.showFirstGuideDialog(
									this,
									ConstantsTooTooEHelper.FIRST_GUIDE_BAICHI_CHAT);
					if (groupChatBeans != null && groupChatBeans.size() > 1) {
						baichiChatDialog.setLeft(true);
					}
					baichiChatDialog.setOnDialogStatus(this);
				}

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
		// mCustomViewPager.setOffscreenPageLimit(0);
		mCustomViewPager.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
		mCustomViewPager.setOnPageChangeListener(mPagerListener);
	}

	public String myCurrentGroupId = "";
	/**
	 * @Fields mPagerListener : 监听viewpager的变化
	 */
	private ViewPager.SimpleOnPageChangeListener mPagerListener = new ViewPager.SimpleOnPageChangeListener() {
		Animation animation = null;

		@Override
		public void onPageSelected(int position) {
			int tabSelectedTextColor = getResources().getColor(
					R.color.tab_selected_textcolor);
			int tabUnSelectedTextColor = getResources().getColor(
					R.color.tab_unSelected_textcolor);
			if (groupChatBeans != null && groupChatBeans.size() > 0) {
				myCurrentGroupId = groupChatBeans.get(position).getGroupId();
			}
			myCurrentPosition = position;
			mFragmentsList.get(myCurrentPosition).onPause(); // 调用切换前Fargment的onPause()
			// fragments.get(currentPageIndex).onStop(); //
			// 调用切换前Fargment的onStop()
			if (mFragmentsList.get(myCurrentPosition).isAdded()) {
				// fragments.get(i).onStart(); // 调用切换后Fargment的onStart()
				mFragmentsList.get(myCurrentPosition).onResume(); // 调用切换后Fargment的onResume()
			}

			switch (position) {
			case 0://

				changeTabOneTextColor(tabSelectedTextColor,
						tabUnSelectedTextColor);
				if (mCurrIndex == 1) {
					animation = new TranslateAnimation(mPositionOne, 0, 0, 0);
				} else if (mCurrIndex == 2) {
					animation = new TranslateAnimation(mPositionTwo, 0, 0, 0);
				}
				break;
			case 1://
				changeTabTwoTextColor(tabSelectedTextColor,
						tabUnSelectedTextColor);
				if (mCurrIndex == 0) {
					animation = new TranslateAnimation(0, mPositionOne, 0, 0);
				} else if (mCurrIndex == 2) {
					animation = new TranslateAnimation(mPositionTwo,
							mPositionOne, 0, 0);
				}
				break;
			case 2://
				changeTabThreeTextColor(tabSelectedTextColor,
						tabUnSelectedTextColor);
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
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void changeTabOneTextColor(int tabSelectedTextColor,
			int tabUnSelectedTextColor) {
		mTabGroupOne.setTextColor(tabSelectedTextColor);
		mTabGroupTwo.setTextColor(tabUnSelectedTextColor);
		mTabGroupThree.setTextColor(tabUnSelectedTextColor);
	}

	/**
	 * @Title: changeTabTwoTextColor
	 * @Description: 设置第二 个 tab被选中
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void changeTabTwoTextColor(int tabSelectedTextColor,
			int tabUnSelectedTextColor) {
		mTabGroupOne.setTextColor(tabUnSelectedTextColor);
		mTabGroupTwo.setTextColor(tabSelectedTextColor);
		mTabGroupThree.setTextColor(tabUnSelectedTextColor);
	}

	/**
	 * @Title: changeTabThreeTextColor
	 * @Description: 设置第一个 tab被选中
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void changeTabThreeTextColor(int tabSelectedTextColor,
			int tabUnSelectedTextColor) {
		mTabGroupOne.setTextColor(tabUnSelectedTextColor);
		mTabGroupTwo.setTextColor(tabUnSelectedTextColor);
		mTabGroupThree.setTextColor(tabSelectedTextColor);
	}

	@Override
	public void onClick(View v) {
		if (groupChatBeans != null && groupChatBeans.size() > 0) {
			if (groupChatBeans.size() > 1) {
				String itemType = groupChatBeans.get(1).getType();
				justShowGuide(itemType);
			} else {
				String itemType = groupChatBeans.get(0).getType();
				justShowGuide(itemType);
			}

		}

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
		mChatPagerAdapter.getItem(mCurrIndex).onActivityResult(requestCode,
				resultCode, data);
	}

	@Override
	public void onDialogStatusListener(boolean isDismiss) {
		if (isDismiss) {

			if (groupChatBeans.size() > 1) {
				String itemType = groupChatBeans.get(1).getType();
				justShowGuide(itemType);
			}

		}

	}

	@Override
	public void onDialogWisStatusListener(boolean isDismiss) {
		if (isDismiss) {

			if (groupChatBeans.size() > 1) {
				String itemType = groupChatBeans.get(1).getType();
				justShowGuide(itemType);
			}

		}

	}

	@Override
	public void onDialogBichiStatusListener(boolean isDismiss) {
		if (isDismiss) {
			if (groupChatBeans.size() > 1) {
				String itemType = groupChatBeans.get(1).getType();
				justShowGuide(itemType);
			}

		}
	}

	@Override
	public void OnReceiveDataListener(MyChatData myChatData) {
		if (mFragmentsList != null && mFragmentsList.size() > 0) {
			// 当前的位置
			BaseChatFragment basefragment = (BaseChatFragment) mFragmentsList
					.get(myCurrentPosition);
			basefragment.messageHistory.add(myChatData);
			basefragment.refreshAdapter();
		}
		/*
		 * if(groupChatBeans!=null&&groupChatBeans.size()>0){ for (int i = 0; i
		 * < groupChatBeans.size(); i++) { String groupID =
		 * myChatData.getGroupId();
		 * if(myCurrentGroupId.equals(groupID)){//如果是当前的 BaseChatFragment
		 * basefragment = (BaseChatFragment)
		 * mFragmentsList.get(myCurrentPosition);
		 * basefragment.messageHistory.add(myChatData);
		 * basefragment.refreshAdapter(); break; }else{ BaseChatFragment
		 * basefragment = (BaseChatFragment) mFragmentsList.get(i);
		 * basefragment.messageHistory.add(myChatData);
		 * basefragment.refreshAdapter(); break; }
		 * 
		 * 
		 * }
		 * 
		 * 
		 * }
		 */

	}

	@Override
	public void OnReceiveGroupIdListener(SendMessageBean smb) {
		/*
		 * if(groupChatBeans!=null&&groupChatBeans.size()>0){ for (int i = 0; i
		 * < groupChatBeans.size(); i++) {
		 * if(i!=myCurrentPosition){//不等于当前的groupid String
		 * groupId=groupChatBeans.get(i).getGroupId(); smb.setGroupId(groupId);
		 * }else{//等于 String
		 * groupId=groupChatBeans.get(myCurrentPosition).getGroupId();
		 * smb.setGroupId(groupId); }
		 * 
		 * } }
		 */

	}
}