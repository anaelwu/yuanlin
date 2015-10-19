package com.ninetowns.tootooplus.activity;

import java.util.ArrayList;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.adapter.MyMsgFragmentPagerAdapter;
import com.ninetowns.tootooplus.fragment.HasPresentedPriorityCodeFragment;
import com.ninetowns.tootooplus.fragment.HasUsedPriorityCodeFragment;
import com.ninetowns.tootooplus.fragment.NotUsedPriorityCodeFragment;
import com.ninetowns.tootooplus.util.UIUtils;
import com.ninetowns.ui.Activity.BaseActivity;
import com.ninetowns.ui.widget.ScrollControlViewPager;


// @ContentView(R.layout.mymessage_activity)
/** 
* @ClassName: MyPriorityCodeActivity 
* @Description:我的 排名优先码  
* @author zhou
* @date 2015-4-22 下午3:54:28 
*  
*/
public class MyPriorityCodeActivity extends BaseActivity implements OnClickListener {
	/**
	 * @Fields mCustomViewPager : 可控制滑动的viewpager
	 */
	// @ViewInject(R.id.mymsg_vPager)
	private ScrollControlViewPager mCustomViewPager;
	@ViewInject(R.id.mymessage_clearall_btn)
	private ImageView mClearImageView;
	/**
	 * 标题栏的三个按钮
	 */
	// @ViewInject(R.id.rb_mymsg_privateletter)
	/** 
	* @Fields mNotUsedTextView :使用
	*/ 
	private TextView mNotUsedTextView;
	// @ViewInject(R.id.rb_mymsg_notice)
	/** 
	* @Fields mHasUsedTextView :已使用
	*/ 
	private TextView mHasUsedTextView;
	// @ViewInject(R.id.rb_mymsg_msg)
	private TextView mHasPresentedTextView;

	// @ViewInject(R.id.rg_main_category)
	private LinearLayout mLLTabContainer;

	/**
	 * @Fields mFragList : 用来存放fragment
	 */
	private ArrayList<Fragment> mFragList;

	/**
	 * @Fields tabIsSelectedBg : tab被选中时的颜色
	 */
	private int tabIsSelectedBg, tabTextDefauleColor;

	/**
	 * @Fields tabIsUnSelectedBg : tab未被选中时的颜色
	 */
	private int tabIsUnSelectedBg;

	private int whiteId;

	/**
	 * @Fields mNotUsedPriorityCodeFragment : 未用过的优先码
	 */
	private NotUsedPriorityCodeFragment mNotUsedPriorityCodeFragment;
	private HasUsedPriorityCodeFragment mHasUsedPriorityCodeFragment;// 通知
	private HasPresentedPriorityCodeFragment mHasPresentedPriorityCodeFragment;// 消息


	private PagerAdapter mMyMsgAdatpeter;

	/**
	 * @Title: onBackClick
	 * @Description: 返回
	 * @param @param v 设定文件
	 * @return void 返回类型
	 */
	@OnClick(R.id.mymessage_back_btn)
	public void onBackClick(View v) {
		this.finish();
	}
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.mymessage_activity);
		// 注入view和事件
		ViewUtils.inject(this);

		initViews();
		tabTextDefauleColor = tabIsSelectedBg = getResources().getColor(
				R.color.tab_selected_bg);
		tabIsUnSelectedBg = R.drawable.mymessage_tab_bg;
		whiteId = getResources().getColor(R.color.white);
		initViewPager();
		selectPrivateLetter();
		initLister();
	}

	private void initViews() {
		Resources resources=getResources();
		mNotUsedTextView = (TextView) findViewById(R.id.rb_mymsg_privateletter);
		mNotUsedTextView.setText(resources.getString(R.string.prioritycode_notused));
		mHasUsedTextView = (TextView) findViewById(R.id.rb_mymsg_notice);
		
		mHasUsedTextView.setText(resources.getString(R.string.prioritycode_hasused));
		mHasPresentedTextView = (TextView) findViewById(R.id.rb_mymsg_msg);
		mHasPresentedTextView.setText(resources.getString(R.string.prioritycode_haspresented));
		mLLTabContainer = (LinearLayout) findViewById(R.id.rg_main_category);
		mCustomViewPager = (ScrollControlViewPager) findViewById(R.id.mymsg_vPager);
		UIUtils.setViewGone(mClearImageView);
	}

	private void initViewPager() {
		mFragList = new ArrayList<Fragment>();
		mNotUsedPriorityCodeFragment = new NotUsedPriorityCodeFragment();
		mHasUsedPriorityCodeFragment = new HasUsedPriorityCodeFragment();
		mHasPresentedPriorityCodeFragment = new HasPresentedPriorityCodeFragment();
		mFragList.add(mNotUsedPriorityCodeFragment);
		mFragList.add(mHasUsedPriorityCodeFragment);
		mFragList.add(mHasPresentedPriorityCodeFragment);

		mMyMsgAdatpeter = new MyMsgFragmentPagerAdapter(
				getSupportFragmentManager(), mFragList);
		mCustomViewPager.setAdapter(mMyMsgAdatpeter);

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

	private void initLister() {
		mNotUsedTextView.setOnClickListener(this);
		mHasUsedTextView.setOnClickListener(this);
		mHasPresentedTextView.setOnClickListener(this);
	}

	/**
	 * @Fields mPagerListener : 监听viewpager的变化
	 */
	private ViewPager.SimpleOnPageChangeListener mPagerListener = new ViewPager.SimpleOnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			switch (position) {
			case 0:// 私信
				selectPrivateLetter();
				break;
			case 1:// 通知
				selectNotice();
				break;
			case 2:// 消息
				selectMessage();
				break;
			}
		}

	};

	private void selectNotice() {
		mCustomViewPager.setCurrentItem(1);
		mNotUsedTextView.setTextColor(tabIsSelectedBg);
		mNotUsedTextView.setBackgroundResource(tabIsUnSelectedBg);
		mHasUsedTextView.setTextColor(whiteId);
		mHasUsedTextView.setBackgroundColor(tabIsSelectedBg);
		mHasPresentedTextView.setTextColor(tabIsSelectedBg);
		mHasPresentedTextView.setBackgroundResource(tabIsUnSelectedBg);
	}

	private void selectMessage() {
		mCustomViewPager.setCurrentItem(2);
		mNotUsedTextView.setTextColor(tabTextDefauleColor);
		mNotUsedTextView.setBackgroundResource(tabIsUnSelectedBg);
		mHasUsedTextView.setTextColor(tabTextDefauleColor);
		mHasUsedTextView.setBackgroundResource(tabIsUnSelectedBg);
		mHasPresentedTextView.setTextColor(whiteId);
		mHasPresentedTextView.setBackgroundColor(tabIsSelectedBg);
	}

	private void selectPrivateLetter() {
		mCustomViewPager.setCurrentItem(0);
		mNotUsedTextView.setTextColor(whiteId);
		mNotUsedTextView.setBackgroundColor(tabIsSelectedBg);
		mHasUsedTextView.setTextColor(tabTextDefauleColor);
		mHasUsedTextView.setBackgroundResource(tabIsUnSelectedBg);
		mHasPresentedTextView.setTextColor(tabTextDefauleColor);
		mHasPresentedTextView.setBackgroundResource(tabIsUnSelectedBg);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rb_mymsg_privateletter:
			selectPrivateLetter();
			break;
		case R.id.rb_mymsg_notice:
			selectNotice();
			break;
		case R.id.rb_mymsg_msg:
			selectMessage();
			break;
		}

	}

}
