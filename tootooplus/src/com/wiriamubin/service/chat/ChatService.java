package com.wiriamubin.service.chat;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.activity.ChatActivity;

/**
 * 聊天服务
 * @author zhou
 * @focus 使用注意事项：此类不可以被修改（包括包名） 用于和一般的activity 绑定 注意setListener设置回调 否则消息不予以处理
 *        所有本地方法和本地回调方法签名不能被修改 目前本地聊天消息接收不在主线程，需要注意
 */
public final class ChatService extends IntentService {
	public ChatService() {
		super(TAG);
		// TODO Auto-generated constructor stub
	}
	//	private static final String LIB_NAME = "ChatService";
	private static final String TAG = "ChatService";

	
	static {
		System.loadLibrary(TAG);
	}
	  private static ChatService sInstance;
	  public  final IBinder mBinder=new LocalBinder();
	       
	     public class LocalBinder extends Binder{
	           // 在Binder中定义一个自定义的接口用于数据交互
	          // 这里直接把当前的服务传回给宿主
	           public ChatService getService(){
	               return ChatService.this;
	          }                
	       }
	       
	private static OnChatMessageLisenter messageLisenter;
//	private static  OnChatConnectLisenter connectLisenter;

	
	 public static ChatService getInstance()  {
	        synchronized (ChatService.class) {
	            if (sInstance == null) {
	                /* First call */
	                sInstance = new ChatService();
	            }
	        }

	        return sInstance;
	    }

	public void setMessageListener(OnChatMessageLisenter listener) {
		ChatService.messageLisenter = listener;
	}
	//链接rtmp
	public native boolean connectRtmp(String host,String userId);
	
	//长连接是否存在
	public native boolean isConnectRtmp();
	
	/** 
	* @Title: shutDown 
	* @Description: 断开长连接
	* @param     设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	public native void shutDown();

	//发送信息
	/** 
	* @Title: send 
	* @Description: TODO(描述这个方法的作用) 
	* @param @param data  json字符串
	* @param @param groupId
	* @param @return    设定文件 
	* @return boolean    返回类型 
	*/
	public native boolean send(String data,String groupId);
	
	//发送信息
//	public native boolean send(ChatData data,String groupId);
	

	// -------------------- JNI 回调 begin --------------------//
	/**
	 * 当有消息进入的时候被调用
	 * @param message ChatMessage 实体
	 */
	public void receivedMessage(String data,String groupId) {
		
		if (messageLisenter == null) {
			Log.w(TAG, "接受到消息，没有设置回调 不予处理");
			return;
		}
		messageLisenter.onReceivedMessage(data,groupId);
	}

	/**
	 * 当有新的用户进入的时候被调用
	 * 
	 * @param users
	 *            新进入的用户数组
	 */


	/**
	 * 当刚进入聊天室的时候 ，被调用
	 * 
	 * @param users
	 *            所有当前在线用户列表
	 */
//	public void getUserList(ChatUserInfo[] users) {
////		LogUtil.info(TAG, "2=================getUserList");
//		if (ChatService.messageLisenter == null) {
//			Log.w(TAG, "获取用户列表，没有设置回调 不予处理");
//			return;
//		}
//		messageLisenter.onGetUserList(users);
//	}
	

	/**
	 * 获取聊天室的信息
	 * @param roomInfo	聊天室信息
	 */
//	public void getRoomInfo(ChatRoomInfo roomInfo)
//	{
//		if (connectLisenter == null) {
//			Log.w(TAG, "链接断开，却没有设置回调");
//			return;
//		}
//		messageLisenter.onGetRoomInfo(roomInfo);
//	}
	
	/**
	 * 当链接断开是，被调用
	 */
	public void onDisconnect() {
		if (messageLisenter == null) {
			Log.w(TAG, "链接断开，却没有设置回调");
			return;
		}
		messageLisenter.onDisconnect();
	} 

	// -------------------- JNI 回调 end --------------------//

	public static interface OnChatMessageLisenter {
		void onReceivedMessage(String data, String groupId);
		void onDisconnect();
	}

	
	@SuppressWarnings("deprecation")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		Notification notification = new Notification(R.drawable.logo, getText(R.string.app_name),
		        System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, ChatActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, "tuotuo",
		       "聊天服务开启", pendingIntent);
		startForeground(0, notification);//0为不显示 1为显示
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public IBinder onBind(Intent intent) {
		
		return mBinder;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
	}
}
