package com.ninetowns.tootooplus.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ninetowns.library.util.ImageUtil;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.bean.AlbumPhotoBean;
import com.ninetowns.tootooplus.util.CommonUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class AlbumPhotoGvAdapter extends BaseAdapter {
	
	private Context context;
	
	private List<AlbumPhotoBean> albumPhotoBeans;
	
	public AlbumPhotoGvAdapter(Context context, List<AlbumPhotoBean> albumPhotoBeans){
		this.context = context;
		
		this.albumPhotoBeans = albumPhotoBeans;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(albumPhotoBeans != null && albumPhotoBeans.size() > 0){
			return albumPhotoBeans.size();
		} else {
			return 0;
		}
		
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder vh = null;
		if(convertView == null){
			vh = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.album_photo_gv_item, null);
			vh.album_photo_item_photo = (ImageView)convertView.findViewById(R.id.album_photo_item_photo);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder)convertView.getTag();
		}
		
		if(!TextUtils.isEmpty(albumPhotoBeans.get(position).getAlbum_photo_path())){
			ImageLoader.getInstance().displayImage("file://" + albumPhotoBeans.get(position).getAlbum_photo_path(), new ImageViewAware(vh.album_photo_item_photo), CommonUtil.OPTIONS_ALBUM, new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLoadingFailed(String imageUri, View view,
						FailReason failReason) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					int angle =ImageUtil. readPictureDegree(albumPhotoBeans.get(position).getAlbum_photo_path());
	                if(angle != 0){
	                    // 下面的方法主要作用是把图片转一个角度，也可以放大缩小等
	                    Matrix m = new Matrix();
	                    int width = loadedImage.getWidth();
	                    int height = loadedImage.getHeight();
	                    m.setRotate(angle); // 旋转angle度
	                    loadedImage = Bitmap.createBitmap(loadedImage, 0, 0, width, height, m, true);// 从新生成图片
	                }
					ImageView imageView = (ImageView)view;
					imageView.setImageBitmap(ImageUtil.cutSquareBmp(loadedImage));
				}
				
				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					// TODO Auto-generated method stub
					
				}
			});
			//自己定义ImageLoader加载本地图片速度比较快
//			ImageLoader.getInstance(3,Type.LIFO).loadImage(albumPhotoBeans.get(position).getAlbum_photo_path(), vh.album_photo_item_photo);
		}
		return convertView;
	}

	
	static class ViewHolder{
		ImageView album_photo_item_photo;
	}
}
