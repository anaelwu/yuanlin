package com.ninetowns.tootooplus.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.util.CommonUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 初始化心愿布局
 * @author huangchao
 *
 */
public class ChangeRowsView extends LinearLayout {

	private LayoutInflater changeInflater;
	
	private LinearLayout change_rows_title_layout, change_rows_cont_layout;
	
	public interface ChangeRowsDownAndDltListener {
		public void downOnClick(View view, View clickView);
		public void deleteOnClick(View view);
	}
	
	private ChangeRowsDownAndDltListener changeRowsDownAndDltListener;
	
	public ChangeRowsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		changeInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View change_rows_view = changeInflater.inflate(R.layout.change_rows_view, null);
		//继承LinearLayout后实现系统的方法
		addView(change_rows_view);
		
		change_rows_title_layout = (LinearLayout)change_rows_view.findViewById(R.id.change_rows_title_layout);

		change_rows_cont_layout = (LinearLayout)change_rows_view.findViewById(R.id.change_rows_cont_layout);
		
	}
	
	
	public void initRowsView(String store_story_name, String store_story_id, String story_story_cover){

		View change_rows_title_item = changeInflater.inflate(R.layout.change_rows_title_view, null);
		TextView change_rows_title_tv = (TextView)change_rows_title_item.findViewById(R.id.change_rows_title_tv);

		if(change_rows_cont_layout.getChildCount() == 0){
			change_rows_title_tv.setText(R.string.create_act_store_wish);
		}
		
		change_rows_title_layout.addView(change_rows_title_item);
		
		final View change_rows_cont_item = changeInflater.inflate(R.layout.change_rows_cont_view, null);
		//故事名称
		TextView change_rows_cont_tv = (TextView)change_rows_cont_item.findViewById(R.id.change_rows_cont_tv);
		change_rows_cont_tv.setText(store_story_name);
		
		//故事id
		TextView change_rows_cont_story_id = (TextView)change_rows_cont_item.findViewById(R.id.change_rows_cont_story_id);
		change_rows_cont_story_id.setText(store_story_id);
		
		ImageView change_rows_count_story_cover = (ImageView)change_rows_cont_item.findViewById(R.id.change_rows_count_story_cover);
		ImageLoader.getInstance().displayImage(story_story_cover, change_rows_count_story_cover, CommonUtil.OPTIONS_LIKE_LIST);
		
		final ImageView change_rows_cont_view_arrow = (ImageView)change_rows_cont_item.findViewById(R.id.change_rows_cont_view_arrow);
		change_rows_cont_view_arrow.setImageResource(R.drawable.icon_arrow_down);
		
		LinearLayout change_rows_cont_down_layout = (LinearLayout)change_rows_cont_item.findViewById(R.id.change_rows_cont_down_layout);
		change_rows_cont_down_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//弹出下拉框
				if(changeRowsDownAndDltListener != null){
					changeRowsDownAndDltListener.downOnClick(change_rows_cont_item, v);
				}
			}
		});
		
		LinearLayout change_rows_cont_delete_layout = (LinearLayout)change_rows_cont_item.findViewById(R.id.change_rows_cont_delete_layout);
		change_rows_cont_delete_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//删除
				change_rows_cont_layout.removeView(change_rows_cont_item);
				change_rows_title_layout.removeViewAt(change_rows_title_layout.getChildCount() - 1);
				if(change_rows_cont_layout.getChildCount() == 1){
					change_rows_cont_layout.getChildAt(0).findViewById(R.id.change_rows_cont_delete_layout).setVisibility(View.INVISIBLE);
				}
				
				if(changeRowsDownAndDltListener != null){
					changeRowsDownAndDltListener.deleteOnClick(change_rows_cont_item);
				}
			}
		});
		
		if(change_rows_cont_layout.getChildCount() > 0){
			change_rows_cont_layout.getChildAt(0).findViewById(R.id.change_rows_cont_delete_layout).setVisibility(View.VISIBLE);
			change_rows_cont_delete_layout.setVisibility(View.VISIBLE);
		} else {
			change_rows_cont_delete_layout.setVisibility(View.INVISIBLE);
		}
		
		change_rows_cont_layout.addView(change_rows_cont_item);
		
	}
	
	
	public void setChangeRowsDownAndDltListener(ChangeRowsDownAndDltListener changeRowsDownAndDltListener){
		this.changeRowsDownAndDltListener = changeRowsDownAndDltListener;
	}
	
	/**
	 * @author huangchao
	 * 获取商家心愿中添加故事个数
	 * @return
	 */
	public int getChangeRowsContChildCount(){
		
		return change_rows_cont_layout.getChildCount();
	}

}
