package com.ninetowns.tootoopluse.activity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.adapter.GridViewApdapter;
import com.ninetowns.tootoopluse.adapter.GridViewApdapter.GridViewHolder;
import com.ninetowns.tootoopluse.bean.AlbumPhotoBean;
import com.ninetowns.tootoopluse.helper.ConstantsTooTooEHelper;

public class AlbumPhotoCHRecommendConvertActivity extends BaseCreateStoryImageActivity {
    private Bundle bundle;
    private List<AlbumPhotoBean> list;
    private GridViewApdapter moreGridViewAdapter;
    private Context context;
    private Map<Integer, AlbumPhotoBean> selectMap;
    private String storyid;
    private boolean isConvertView;
    @Override
    public void getType() {
        bundle = getIntent().getBundleExtra(ConstantsTooTooEHelper.BUNDLE);
        isConvertView = bundle.getBoolean(ConstantsTooTooEHelper.isConvertView);
    }

    @Override
    public GridViewApdapter initAdapter(Context context,
            List<AlbumPhotoBean> list, LinkedHashMap<Integer, AlbumPhotoBean> selectMap) {
        this.list = list;
        this.context = context;
        this.selectMap = selectMap;
        moreGridViewAdapter = new GridViewApdapter(context, list, selectMap);
        return moreGridViewAdapter;
    }
    @Override
    public void onItemSelectPhoto(int position, TextView comit,View view) {
        if(isConvertView){
            onePhoto(position, comit,view);
        }else{
            fivephoto(position, comit,view);
        }
       
    }

    private void fivephoto(int position, TextView comit,View view) {
    	GridViewHolder viewHolder = (GridViewHolder)view.getTag();
    	if (moreGridViewAdapter.selectMap.get(position) != null) {
    		viewHolder.checkBox.setChecked(false);
    		moreGridViewAdapter.selectMap.remove(position);
        } else {
         
            // 最多选5张
            if (moreGridViewAdapter.selectMap.size() < Integer
                    .parseInt(ConstantsTooTooEHelper.MAX_UPLOAD_PHOTO)) {
            	viewHolder.checkBox.setChecked(true);
            	moreGridViewAdapter.selectMap.put(position,
                        albumPhotoBeans.get(position));
            } else {
                ComponentUtil.showToast(
                        AlbumPhotoCHRecommendConvertActivity.this,
                        getResources().getString(
                                R.string.pass_max_more_update_photo));
            }
        }
        comit.setText(getResources().getString(
                R.string.mobile_forget_pwd_success_btn)
                + "("
                + moreGridViewAdapter.selectMap.size()
                + "/"
                + ConstantsTooTooEHelper.MAX_UPLOAD_PHOTO + ")");
    }
    
    private void onePhoto(int position, TextView comit,View view) {
    	GridViewHolder viewHolder = (GridViewHolder)view.getTag();
    	if (moreGridViewAdapter.selectMap.get(position) != null) {
    		viewHolder.checkBox.setChecked(false);
    		moreGridViewAdapter.selectMap.remove(position);
        } else {
         
            // 最多选5张
            if (moreGridViewAdapter.selectMap.size() < Integer
                    .parseInt(ConstantsTooTooEHelper.MAX_UPLOAD_PHOTO_ONE)) {
            	viewHolder.checkBox.setChecked(true);
            	moreGridViewAdapter.selectMap.put(position,
                        albumPhotoBeans.get(position));
            } else {
                ComponentUtil.showToast(
                        AlbumPhotoCHRecommendConvertActivity.this,
                        getResources().getString(
                                R.string.pass_max_more_update_photo));
            }
        }
        comit.setText(getResources().getString(
                R.string.mobile_forget_pwd_success_btn)
                + "("
                + moreGridViewAdapter.selectMap.size()
                + "/"
                + ConstantsTooTooEHelper.MAX_UPLOAD_PHOTO_ONE + ")");
    }

    @Override
    public String getCreateConvertType() {
        return "2";
    }

    @Override
    public String getPageType() {
        return "2";
    }
}
