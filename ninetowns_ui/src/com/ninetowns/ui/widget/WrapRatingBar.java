package com.ninetowns.ui.widget;

import com.ninetowns.ui.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;

/**
 * 评分星星
 * 防止星星底部出现拉长似的阴影
 * 
 * 
 */
public class WrapRatingBar extends LinearLayout {

	private RatingBar ratingBar;

	// // java代码创建视图的时候被调用
	// public WrapRatingBar(Context context) {
	// super(context);
	// initView(context);
	// }

	// 这个是在xml创建但是没有指定style的时候被调用
	public WrapRatingBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	// // 这个是在xml创建,指定style的时候被调用
	// public WrapRatingBar(Context context, AttributeSet attrs, int defStyle) {
	// super(context, attrs);
	// initView(context);
	// }

	private void initView(Context context) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.view_myratingbar, null);

		ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

		// 取控件当前的布局参数
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) ratingBar
				.getLayoutParams();
		// 获取屏幕密度
		float scale = context.getResources().getDisplayMetrics().density;
		// 计算高度, 23为该图片(星星)的宽和高
		linearParams.height = (int) (23 * scale + 0.5f);
		
		// 设置高度
		ratingBar.setLayoutParams(linearParams);

		// 设置默认不可以触摸选择
		ratingBar.setIsIndicator(true);
		
		addView(view);
	}

	/**
	 * 设置星星改变事件
	 * 
	 * @param listener
	 */
	public void setOnRatingBarChangeListener(OnRatingBarChangeListener listener) {
		ratingBar.setOnRatingBarChangeListener(listener);
	}

	/**
	 * 设置星星显示数量
	 * 
	 * @param num
	 */
	public void setRating(float num) {
		ratingBar.setRating(num);
	}

	/**
	 * 设置星星显示数量
	 * 
	 * @param num
	 */
	public void setRating(int num) {
		ratingBar.setRating(num);
	}

	/**
	 * 获得星星显示数量
	 * 
	 * @return
	 */
	public float getRating() {
		return ratingBar.getRating();
	}

	/**
	 * 设置是否能触摸改变
	 * 
	 * @param isIndicator
	 */
	public void setIsIndicator(boolean isIndicator) {
		ratingBar.setIsIndicator(isIndicator);
	}

}
