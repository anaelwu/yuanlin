package com.ninetowns.tootoopluse.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.lidroid.xutils.util.LogUtils;
import com.ninetowns.library.util.StringUtils;
import com.ninetowns.tootoopluse.activity.SrcPicActivity;
import com.ninetowns.tootoopluse.activity.VideoPlayActivity;
import com.ninetowns.tootoopluse.bean.PrivateLetterMessageBean;
import com.ninetowns.tootoopluse.util.CommonUtil;
import com.ninetowns.tootoopluse.util.INetConstanst;
import com.ninetowns.tootoopluse.util.MSGTypeUtils;
import com.ninetowns.tootoopluse.util.TimeUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.wiriamubin.service.chat.MyChatData;
import com.wiriamubin.service.chat.VoicePlayer;
public class ChatDataAdapter extends BaseChatDataAdapter implements
		INetConstanst {

	private DisplayImageOptions options;
	private Context mContext;

	public ChatDataAdapter(Context context, List<MyChatData> messageHistory,List<PrivateLetterMessageBean> privateLetterMessageBeans,
			boolean isChatRoom) {

		super(context, messageHistory, privateLetterMessageBeans, isChatRoom);
		this.mContext = context;
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).build();
	}
	@Override
	protected void setData(final ViewHolder vhHolder, int position,
			final PrivateLetterMessageBean privateLetterMessageBean) {
		ImageLoader.getInstance().displayImage(privateLetterMessageBean.getLogoUrl(),
				new ImageViewAware(vhHolder.iv_photoImage),
				CommonUtil.OPTIONS_HEADPHOTO);
		vhHolder.tv_name.setText(privateLetterMessageBean.getUserName());
		vhHolder.tv_time.setText(privateLetterMessageBean.getCreateDate());
		switch (MSGTypeUtils.convertIntPrivateLetterType(privateLetterMessageBean.getType())) {
		case 0:// text
			vhHolder.tv_body.setText(privateLetterMessageBean.getContent());
			break;
		case 1:// picture
			if (!StringUtils.isEmpty(privateLetterMessageBean.getContent())
					&& privateLetterMessageBean.getContent().length() > 4) {
				String thumbNailUri = getThumbNailUri(privateLetterMessageBean.getContent());
				ImageLoader.getInstance().displayImage(thumbNailUri,
						new ImageViewAware(vhHolder.picture_body), CommonUtil.OPTIONS_IMAGE);
				// myChatData.body.
				vhHolder.picture_body.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						enterSrcPicActivity(privateLetterMessageBean.getContent());
					}
				});
			} else {
				ImageLoader.getInstance().displayImage(privateLetterMessageBean.getContent(),
						new ImageViewAware(vhHolder.picture_body),  CommonUtil.OPTIONS_IMAGE);
			}
			break;
		case 2:// voice
			vhHolder.voice_body.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						VoicePlayer.getInstance().playVoice(privateLetterMessageBean.getContent(),
						isPrivateReceiver(privateLetterMessageBean), vhHolder.voice_body);
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(mContext, "语音播放失败，请重试！", 1).show();
						LogUtils.e(e.getMessage());
					}
				}
			});
			break;
		case 3:// video
			if (!StringUtils.isEmpty(privateLetterMessageBean.getContent())) {
				if (privateLetterMessageBean.getContent().contains(";")) {
					String[] imageAndVideoUrls = privateLetterMessageBean.getContent().split(";");//
					// 前边是缩略图的地址后面是视频地址
					if (null != imageAndVideoUrls) {
						ImageLoader.getInstance().displayImage(
								imageAndVideoUrls[0],
								new ImageViewAware(vhHolder.video_body),
								options);
					}
					onVideoItemClick(vhHolder.video_body, imageAndVideoUrls[1]);
				}
			}
			break;
		}
	}
	@Override
	protected void setData(final ViewHolder vhHolder, int position,
			final MyChatData myChatData) {
		ImageLoader.getInstance().displayImage(myChatData.profileImage,
				new ImageViewAware(vhHolder.iv_photoImage),
				CommonUtil.OPTIONS_HEADPHOTO);
		vhHolder.tv_name.setText(myChatData.name);
		if(vhHolder.tv_time!=null){
			String format_time=TimeUtil.formatChatRoomTime(myChatData.time);
//			if("".equalsIgnoreCase(myChatData.time)){
				vhHolder.tv_time.setText(format_time);
//			}
//			vhHolder.tv_time.setText(myChatData.time);
		}
		switch (MSGTypeUtils.convertIntType(myChatData.type)) {
		case 0:// text
			vhHolder.tv_body.setText(myChatData.body);
			break;
		case 1:// picture
			if (!StringUtils.isEmpty(myChatData.body)
					&& myChatData.body.length() > 4) {
				String thumbNailUri = getThumbNailUri(myChatData.body);
				ImageLoader.getInstance().displayImage(thumbNailUri,
						new ImageViewAware(vhHolder.picture_body),  CommonUtil.OPTIONS_IMAGE);
				vhHolder.picture_body.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						enterSrcPicActivity(myChatData.body);
					}
				});
			} else {
				ImageLoader.getInstance().displayImage(myChatData.body,
						new ImageViewAware(vhHolder.picture_body),  CommonUtil.OPTIONS_IMAGE);
			}
			break;
		case 2:// voice
			if (!StringUtils.isEmpty(myChatData.body)){
				vhHolder.voice_body.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							LogUtils.i("voice_body  onclick............");
							VoicePlayer.getInstance().playVoice(myChatData.body,
									myChatData.isRecever, vhHolder.voice_body);
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(mContext, "语音播放失败，请重试！", 1).show();
							LogUtils.e(e.getMessage());
						}
					}
				});
			}
			break;
		case 3:// video
			if (!StringUtils.isEmpty(myChatData.body)) {
				if (myChatData.body.contains(";")) {
					String[] imageAndVideoUrls = myChatData.body.split(";");//
					// 前边是缩略图的地址后面是视频地址
					if (null != imageAndVideoUrls) {
						ImageLoader.getInstance().displayImage(
								imageAndVideoUrls[0],
								new ImageViewAware(vhHolder.video_body),
								 CommonUtil.OPTIONS_VIDEO);
					}
					onVideoItemClick(vhHolder.video_body, imageAndVideoUrls[1]);
				}
			}
			break;
		}
	}
	private void onVideoItemClick(ImageView video_body, final String videoUri) {
		video_body.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				enterVideoPlayActivity(videoUri);
			}
		});
	}
	private void enterVideoPlayActivity(String videoUri) {
		Intent intent = new Intent(mContext, VideoPlayActivity.class);
		intent.putExtra("videoUri", videoUri);
		mContext.startActivity(intent);
	}

	/**
	 * @Title: getThumbNailUri
	 * @Description: 通过原图地址 拼接缩略图地址
	 * @param @param myChatData 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private String getThumbNailUri(String srcPicUrl) {
		String preSrcUri =srcPicUrl.substring(0,
				srcPicUrl.length() - 4);
		String aftSrcUri = srcPicUrl.substring(
				srcPicUrl.length() - 4,srcPicUrl.length());
		return preSrcUri.concat("_thumb").concat(aftSrcUri);
	}
	/**
	 * @Title: enterSrcPicActivity
	 * @Description:进入 原图 界面
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void enterSrcPicActivity(String srcPicUri) {
		Intent intent = new Intent(mContext, SrcPicActivity.class);
		intent.putExtra("srcpicuri", srcPicUri);
		mContext.startActivity(intent);
	}
}
