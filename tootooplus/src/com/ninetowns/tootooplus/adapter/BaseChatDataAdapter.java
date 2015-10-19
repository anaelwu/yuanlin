package com.ninetowns.tootooplus.adapter;

import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.bean.PrivateLetterMessageBean;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.util.MSGTypeUtils;
import com.wiriamubin.service.chat.MyChatData;

public abstract class BaseChatDataAdapter extends BaseAdapter {

	private Context mContext;
	/**
	 * @Fields messageHistory : 聊天纪录
	 */
	private List<MyChatData> messageHistory;
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private List<PrivateLetterMessageBean> privateLetterMessageBeans;
	/**
	 * @Fields isChatRoom :是否为聊天室
	 */
	private boolean isChatRoom;

	private static final int MESSAGE_TYPE_RECV_TXT = 0;
	private static final int MESSAGE_TYPE_SEND_TXT = 1;
	private static final int MESSAGE_TYPE_SEND_IMAGE = 2;
	private static final int MESSAGE_TYPE_RECV_IMAGE = 3;
	private static final int MESSAGE_TYPE_SEND_VOICE = 4;
	private static final int MESSAGE_TYPE_RECV_VOICE = 5;
	private static final int MESSAGE_TYPE_SEND_VIDEO = 6;
	private static final int MESSAGE_TYPE_RECV_VIDEO = 7;

	public BaseChatDataAdapter(Context context,
			List<MyChatData> messageHistory,
			List<PrivateLetterMessageBean> privateLetterMessageBeans,
			boolean isChatRoom) {
		this.mContext = context;
		this.isChatRoom = isChatRoom;
		if (isChatRoom) {
			if (null != messageHistory) {
				this.messageHistory = messageHistory;
			}
		} else {
			this.privateLetterMessageBeans = privateLetterMessageBeans;
		}

	}

	@Override
	public int getCount() {
		if (isChatRoom) {
			return messageHistory == null ? 0 : messageHistory.size();
		}
		else {
			return privateLetterMessageBeans == null ? 0
					: privateLetterMessageBeans.size();
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
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 8;
	}

	@Override
	public int getItemViewType(int position) {

		if (isChatRoom) {
			MyChatData myChatData = messageHistory.get(position);
			switch (MSGTypeUtils.convertIntType(myChatData.type)) {
			case 0:
				return myChatData.isRecever ? MESSAGE_TYPE_RECV_TXT
						: MESSAGE_TYPE_SEND_TXT;
			case 1:
				return myChatData.isRecever ? MESSAGE_TYPE_RECV_IMAGE
						: MESSAGE_TYPE_SEND_IMAGE;
			case 2:
				return myChatData.isRecever ? MESSAGE_TYPE_RECV_VOICE
						: MESSAGE_TYPE_SEND_VOICE;
			case 3:
				return myChatData.isRecever ? MESSAGE_TYPE_RECV_VIDEO
						: MESSAGE_TYPE_SEND_VIDEO;

			}
		} else {
			PrivateLetterMessageBean privateLetterMessageBean = privateLetterMessageBeans
					.get(position);
			switch (MSGTypeUtils.convertIntPrivateLetterType(privateLetterMessageBean
					.getType())) {
			case 0:
				return isPrivateReceiver(privateLetterMessageBean) ? MESSAGE_TYPE_RECV_TXT
						: MESSAGE_TYPE_SEND_TXT;
			case 1:
				return isPrivateReceiver(privateLetterMessageBean) ? MESSAGE_TYPE_RECV_IMAGE
						: MESSAGE_TYPE_SEND_IMAGE;
			case 2:
				return isPrivateReceiver(privateLetterMessageBean) ? MESSAGE_TYPE_RECV_VOICE
						: MESSAGE_TYPE_SEND_VOICE;
			case 3:
				return isPrivateReceiver(privateLetterMessageBean) ? MESSAGE_TYPE_RECV_VIDEO
						: MESSAGE_TYPE_SEND_VIDEO;

			}

		}

		return -1;
	}

	protected boolean isPrivateReceiver(
			PrivateLetterMessageBean privateLetterMessageBean) {
		return !privateLetterMessageBean.getUserIdSend().equals(
				SharedPreferenceHelper.getLoginUserId(mContext));
	}
	ViewHolder vhHolder=null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (isChatRoom) {
			MyChatData myChatData = messageHistory.get(position);
			convertView = fillChatRoomData(isChatRoom,position, convertView, myChatData.isRecever, myChatData.type);
			setData(vhHolder, position, myChatData);
		} else {
			PrivateLetterMessageBean privateLetterMessageBean=privateLetterMessageBeans.get(position);
			convertView=fillChatRoomData(isChatRoom,position, convertView, isPrivateReceiver(privateLetterMessageBean), privateLetterMessageBean.getType());
			setData(vhHolder, position, privateLetterMessageBean);
		
		}
		return convertView;
	}

	private View fillChatRoomData(Boolean isChatRoom,int position, View convertView,boolean isRecever,String type) {
		
//		final ViewHolder vhHolder;
			if (null == convertView) {
				int t=isChatRoom?MSGTypeUtils.convertIntType(type):MSGTypeUtils.convertIntPrivateLetterType(type);
				convertView = createViewByMessagType(isRecever,
						t);
				vhHolder = new ViewHolder();
				vhHolder.iv_photoImage = (ImageView) convertView
						.findViewById(R.id.iv_photoimage);
				vhHolder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_name);
				vhHolder.tv_time = (TextView) convertView
						.findViewById(R.id.chatitem_tv_time);

				
				switch (t) {
				case 0:// text
					vhHolder.tv_body = (TextView) convertView
							.findViewById(R.id.tv_body);
					break;
				case 1:// image
					vhHolder.picture_body = (ImageView) convertView
							.findViewById(R.id.picture_body);
					break;
				case 2:// voice
					vhHolder.voice_body = (ImageView) convertView
							.findViewById(R.id.voice_body);

					break;
				case 3:// video
					vhHolder.video_body = (ImageView) convertView
							.findViewById(R.id.video_body);
					break;}
				convertView.setTag(vhHolder);
			} else {
				vhHolder = (ViewHolder) convertView.getTag();
			}
			return convertView;
		}
		
//	}

	/**
	 * @Title: setData
	 * @Description: 在子类中填充数据
	 * @param @param vhHolder
	 * @param @param position
	 * @param @param myChatData 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	protected abstract void setData(ViewHolder vhHolder, int position,
			MyChatData myChatData);
	/**
	 * @Title: setData
	 * @Description: 在子类中填充数据
	 * @param @param vhHolder
	 * @param @param position
	 * @param @param myChatData 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	protected abstract void setData(ViewHolder vhHolder, int position,
			PrivateLetterMessageBean privateLetterMessageBean);

	/**
	 * @param type
	 * @Title: createViewByIsReceiver
	 * @Description: 根据是否为接受者创建不同的layout view
	 * @param @param isRecever
	 * @param @return 设定文件
	 * @return View 返回类型
	 * @throws
	 */
	private View createViewByMessagType(boolean isRecever, int type) {
//		int i_type = MSGTypeUtils.convertIntType(type);
		switch (type) {
		case 0:// 文字
			return isRecever ? View.inflate(mContext,
					R.layout.msgtext_receiver_layout, null) : View.inflate(
					mContext, R.layout.msgtext_sender_layout, null);
		case 1:// 图片
			return isRecever ? View.inflate(mContext,
					R.layout.msgpicture_receiver_layout, null) : View.inflate(
					mContext, R.layout.msgpicture_sender_layout, null);
		case 2:// 语音
			return isRecever ? View.inflate(mContext,
					R.layout.msgvoice_receiver_layout, null) : View.inflate(
					mContext, R.layout.msgvoice_sender_layout, null);
		case 3:// 视频
			return isRecever ? View.inflate(mContext,
					R.layout.msgvideo_receiver_layout, null) : View.inflate(
					mContext, R.layout.msgvideo_sender_layout, null);

		default:
			return isRecever ? View.inflate(mContext,
					R.layout.msgtext_receiver_layout, null) : View.inflate(
					mContext, R.layout.msgtext_sender_layout, null);
		}

	}

	static class ViewHolder {
		TextView tv_body;
		ImageView video_body;
		ImageView voice_body;
		ImageView picture_body;
		TextView tv_name;
		TextView tv_time;
		ImageView iv_photoImage;
		// TextView sender_body;
		// TextView sender_name;
		// ImageView sender_iv_photoImage;
	}

}