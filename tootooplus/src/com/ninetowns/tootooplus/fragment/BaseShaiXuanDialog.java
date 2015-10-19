package com.ninetowns.tootooplus.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ninetowns.tootooplus.R;
import com.ninetowns.ui.widget.dialog.BaseFragmentDialog;

/**
 * 
 * @ClassName: BaseShaiXuanDialog
 * @Description: 筛选dialog的基类
 * @author wuyulong
 * @date 2015-4-28 上午10:18:32
 * 
 */
public abstract class BaseShaiXuanDialog extends BaseFragmentDialog implements
		OnClickListener {
	private View shaixuan;
	private OnSelectedListener onSelectedListener;
	private RelativeLayout mRLAll;
	private RelativeLayout mRLNew;
	private RelativeLayout mRLHot;
	private RelativeLayout mRLRecommend;
	private String viewType;
	private View mHotLine;
	private View llDismiss;
	protected static final int ALL=1;
	protected static final int NEW=2;
	protected static final int HOT=3;
	protected static final int RECOMMEND=4;
	
	protected static final int ACTIVITY=11;
	protected static final int WISH=12;
	protected static final int COMMENT=13;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		shaixuan = inflater.inflate(R.layout.base_shai_xuan_dialog, null);
		initId();
		justTypeView();
		setItemClick();
		return shaixuan;
	}
	/**判断是哪一个界面**/
	private void justTypeView() {
		int  viewType=getViewType();
		switch (viewType) {
		case ACTIVITY:
			mHotLine.setVisibility(View.GONE);
			mRLRecommend.setVisibility(View.GONE);
			break;
		case WISH:
			break;
		case COMMENT:
			break;
		
		}
		
		
		
	}
	/**
	 * 
	* @Title: getViewType 
	* @Description: 获得显示界面的类型
	* @param  
	* @return   
	* @throws
	 */
public abstract int getViewType();
	/**
	 * @Title: initId
	 * @Description: 初始化id
	 */
	private void initId() {
		
		llDismiss= shaixuan.findViewById(R.id.ll_dismiss);
		
		mRLAll = (RelativeLayout) shaixuan.findViewById(R.id.rl_all);
		mRLNew = (RelativeLayout) shaixuan.findViewById(R.id.rl_new);
		mRLHot = (RelativeLayout) shaixuan.findViewById(R.id.rl_hot);
		mRLRecommend = (RelativeLayout) shaixuan
				.findViewById(R.id.rl_recommend);
		mHotLine=shaixuan.findViewById(R.id.hot_line);
	}

	private void setItemClick() {
		mRLAll.setOnClickListener(this);
		mRLNew.setOnClickListener(this);
		mRLHot.setOnClickListener(this);
		mRLRecommend.setOnClickListener(this);
		llDismiss.setOnClickListener(this);
	}

	/**
	 * 
	 * @ClassName: OnSelectedListener
	 * @Description: 此接口是回调所选择的参数
	 * @author wuyulong
	 * @date 2015-4-28 上午10:22:37
	 * 
	 */
	public interface OnSelectedListener {
		/**
		 * 
		 * @Title: setOnSelectedListener
		 * @Description: 监听索选择的参数
		 * @param
		 * @return
		 * @throws
		 */
		public void OnSelectedListenerPar(String type);
	}

	public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
		this.onSelectedListener = onSelectedListener;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_all:
			if(onSelectedListener!=null){
				onSelectedListener.OnSelectedListenerPar(getListenerPar(ALL));
				dismiss();
			}

			break;
		case R.id.rl_new:
			if(onSelectedListener!=null){
				onSelectedListener.OnSelectedListenerPar(getListenerPar(NEW));
				dismiss();
			}

			break;
		case R.id.rl_hot:
			if(onSelectedListener!=null){
				onSelectedListener.OnSelectedListenerPar(getListenerPar(HOT));
				dismiss();
			}

			break;
		case R.id.rl_recommend:
			if(onSelectedListener!=null){
				onSelectedListener.OnSelectedListenerPar(getListenerPar(RECOMMEND));
				dismiss();
			}
			break;
		case R.id.ll_dismiss:
			dismiss();
			break;
		}

	}
	/**
	 * 
	* @Title: setListenerPar 
	* @Description: 获得
	* @param  
	* @return   
	* @throws
	 */
	public abstract String getListenerPar(int type);
}