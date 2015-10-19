package com.ninetowns.tootoopluse.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.bean.GridViewGroupBean;
import com.ninetowns.tootoopluse.util.CommonUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

/**
 * 
* @ClassName: MyGroupSuccessAdapter 
* @Description: 成功组团白吃码
* @author wuyulong
* @date 2015-6-17 下午1:15:40 
*
 */
public class MyGroupSuccessAdapter extends GridViewGroupPhotoAdapter{

	public MyGroupSuccessAdapter(Activity activity,
			List<GridViewGroupBean> parserResult) {
		super(activity, parserResult);
	}
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public void setUserInfo(GridViewGroupBean photoBean,
			PhotoViewHolder photoViewHolder) {

		String logurl = photoBean.getLogoUrl();
		String userGrade = photoBean.getUserGrade();
		String userName = photoBean.getUserName();
		String isUsed=photoBean.getIsUsed();
		String EatCode=photoBean.getEatCode();
		if(!TextUtils.isEmpty(isUsed)){
			if(isUsed.equals("1")){
				//成功
				photoViewHolder.mIVYestGroup.setImageResource(R.drawable.icon_yes_group);
				photoViewHolder.mIVYestGroup.setVisibility(View.VISIBLE);
			}else if(isUsed.equals("0")){//0未成功
				photoViewHolder.mIVYestGroup.setVisibility(View.GONE);
			}else if(isUsed.equals("2")){
				//签到
				photoViewHolder.mIVYestGroup.setVisibility(View.VISIBLE);
				photoViewHolder.mIVYestGroup.setImageResource(R.drawable.icon_sign_group);
			}
		}
		if (!TextUtils.isEmpty(logurl)) {
			ImageLoader.getInstance().displayImage(logurl,
					new ImageViewAware(photoViewHolder.ivPhoto),
					CommonUtil.OPTIONS_HEADPHOTO);
		}
//		if (!TextUtils.isEmpty(userGrade)) {
//			CommonUtil.setUserGrade(photoViewHolder.mCtUserInfo, userGrade,"bottom");
//		}
		if (!TextUtils.isEmpty(userName)) {
			photoViewHolder.mCtUserInfo.setText(userName);
		}
		photoViewHolder.mCTGroupCount.setBackgroundDrawable(null);
		if(!TextUtils.isEmpty(EatCode)){
			photoViewHolder.mCTGroupCount.setText(EatCode);
		}else{
			photoViewHolder.mCTGroupCount.setText("没有白吃码");
		}
	}
	@Override
	public boolean isPhotoClickable() {
		return false;
	}
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}
