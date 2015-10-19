package com.ninetowns.ui.widget.popwindow;


import com.ninetowns.ui.R;

import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class StoryItemPopupWindow extends PopupWindows {
	/**"1"为文字, "2"为图片, "3"为视频**/
	private String storyItemType= "";
	private View mRootView;
	
	private LayoutInflater mInflater;
	
	private int rootWidth = 0;
	
	private ImageView story_item_up_arrow;
	
	private ImageView story_item_down_arrow;
	
	public PopWindowItemClickListener popWindowItemClickListener;
	
	public interface PopWindowItemClickListener {
		public void popWinOneClick();
		
		public void popWinTwoClick();
		
		public void popWinThreeClick();
	}
	@Override
	public void setTouchInterListener() {/*
	    
        mWindow.setTouchInterceptor(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    mWindow.dismiss();
                    LogUtil.systemlogInfo("我的pop销毁了", "触发");

                    return true;
                }
//                mWindow.dismiss();
                LogUtil.systemlogInfo("我的pop没有销毁了", "触发");
                return false;
            }
        });
    
	*/}
	public StoryItemPopupWindow(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public StoryItemPopupWindow(Context context, String storyItemType){
		super(context);
		this.storyItemType = storyItemType;
		
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(storyItemType.equals("1")){
			setRootViewId(R.layout.story_item_two_icon_pwindow);
		} else if(storyItemType.equals("2")){
			setRootViewId(R.layout.story_item_three_icon_pwindow);
		} else if(storyItemType.equals("3")){
			setRootViewId(R.layout.story_item_one_icon_pwindow);
		}
		
	}

	
	 public void setRootViewId(int id) {
		 mRootView = mInflater.inflate(id, null);
		 //文字可以编辑和删除
		 if(storyItemType.equals("1")){
			 LinearLayout story_item_two_icon_one_layout = (LinearLayout)mRootView.findViewById(R.id.story_item_two_icon_one_layout);
			 story_item_two_icon_one_layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(popWindowItemClickListener != null){
						dismiss();
						popWindowItemClickListener.popWinOneClick();
					}
				}
			});
			 
			 LinearLayout story_item_two_icon_two_layout = (LinearLayout)mRootView.findViewById(R.id.story_item_two_icon_two_layout);
			 story_item_two_icon_two_layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(popWindowItemClickListener != null){
						dismiss();
						popWindowItemClickListener.popWinTwoClick();
					}
				}
			});
		 } else if(storyItemType.equals("2")){
			 //图片可以缩放，剪切和删除
			 LinearLayout story_item_three_icon_one_layout = (LinearLayout)mRootView.findViewById(R.id.story_item_three_icon_one_layout);
			 story_item_three_icon_one_layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(popWindowItemClickListener != null){
						dismiss();
						popWindowItemClickListener.popWinOneClick();
					}
				}
			});
		    LinearLayout story_item_three_icon_two_layout = (LinearLayout)mRootView.findViewById(R.id.story_item_three_icon_two_layout);
		    story_item_three_icon_two_layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(popWindowItemClickListener != null){
						dismiss();
						popWindowItemClickListener.popWinTwoClick();
					}
				}
			});
		    LinearLayout story_item_three_icon_three_layout = (LinearLayout)mRootView.findViewById(R.id.story_item_three_icon_three_layout);
		    story_item_three_icon_three_layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(popWindowItemClickListener != null){
						dismiss();
						popWindowItemClickListener.popWinThreeClick();
					}
				}
			});
			 
		 } else if(storyItemType.equals("3")){
			 //视频只能删除
			 LinearLayout story_item_one_icon_one_layout = (LinearLayout)mRootView.findViewById(R.id.story_item_one_icon_one_layout);
			 story_item_one_icon_one_layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(popWindowItemClickListener != null){
						dismiss();
						popWindowItemClickListener.popWinOneClick();
					}
				}
			});
		 }
		 
		 
		 story_item_up_arrow = (ImageView)mRootView.findViewById(R.id.story_item_up_arrow);
		 
		 story_item_down_arrow = (ImageView)mRootView.findViewById(R.id.story_item_down_arrow);
		 
		 setContentView(mRootView);
	 }
	 
	 public void show(View anchor){
		 preShow();

        int xPos, yPos;

        int[] location = new int[2];

        anchor.getLocationOnScreen(location);
        
        //得到组件到矩阵
        Rect anchorRect = new Rect(location[0], location[1], location[0]
                + anchor.getWidth(), location[1] + anchor.getHeight());

        mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        int rootHeight = mRootView.getMeasuredHeight();

        if (rootWidth == 0) {
            rootWidth = mRootView.getMeasuredWidth();
        }

        int screenWidth = mWindowManager.getDefaultDisplay().getWidth();

        // automatically get X coord of popup (top left)
        if ((anchorRect.centerX() + rootWidth / 2) > screenWidth) {
        	//这种算法是popupWindow最右边跟控件的最右边对齐
            xPos = anchorRect.right - rootWidth;
        } else {
        	xPos = anchorRect.centerX() - (rootWidth / 2);
	    }
        xPos = (xPos < 0) ? 0 : xPos;

        if(anchorRect.top > rootHeight){
        	 yPos = anchorRect.top - rootHeight;
        	 story_item_up_arrow.setVisibility(View.GONE);
        	 story_item_down_arrow.setVisibility(View.VISIBLE);
        } else {
        	 yPos = anchorRect.bottom;
        	 story_item_up_arrow.setVisibility(View.VISIBLE);
        	 story_item_down_arrow.setVisibility(View.GONE);
        }
        
        boolean onTop = (anchorRect.top > rootHeight) ? true : false;

        setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);

        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	 }
	 
	 
	 private void setAnimationStyle(int screenWidth, int requestedX,
	            boolean onTop) {
		 mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center
                 : R.style.Animations_PopDownMenu_Center);

	    }
	 
	 
	 public void setPopWindowItemClickListener(PopWindowItemClickListener popWindowItemClickListener){
		 this.popWindowItemClickListener = popWindowItemClickListener;
	 }


//    private void setTouchInterListener() {
//	        mWindow.setTouchInterceptor(new OnTouchListener() {
//	            @Override
//	            public boolean onTouch(View v, MotionEvent event) {
//	                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//	                    mWindow.dismiss();
//
//	                    return true;
//	                }
//
//	                return false;
//	            }
//	        });
//	    }
}
