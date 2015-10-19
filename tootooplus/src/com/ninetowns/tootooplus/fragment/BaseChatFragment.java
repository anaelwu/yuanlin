package com.ninetowns.tootooplus.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.cyberplayer.utils.T;
import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.Des3;
import com.lidroid.xutils.util.LogUtils;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ImageUtil;
import com.ninetowns.library.util.JsonTools;
import com.ninetowns.library.util.NetworkUtil;
import com.ninetowns.library.util.StringUtils;
import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.activity.ChatActivity;
import com.ninetowns.tootooplus.activity.ChatRoomRecordVideoActivity;
import com.ninetowns.tootooplus.adapter.ChatDataAdapter;
import com.ninetowns.tootooplus.application.TootooPlusApplication;
import com.ninetowns.tootooplus.bean.ImageUriInService;
import com.ninetowns.tootooplus.bean.LoginBean;
import com.ninetowns.tootooplus.bean.PrivateLetterMessageBean;
import com.ninetowns.tootooplus.bean.SendMessageBean;
import com.ninetowns.tootooplus.helper.SharedPreferenceHelper;
import com.ninetowns.tootooplus.parser.PrivateLetterMessageParser;
import com.ninetowns.tootooplus.util.CommonUtil;
import com.ninetowns.tootooplus.util.FileUtils;
import com.ninetowns.tootooplus.util.INetConstanst;
import com.ninetowns.tootooplus.util.ParserUitils;
import com.ninetowns.tootooplus.util.ReceiverManager;
import com.ninetowns.tootooplus.util.TimeUtil;
import com.ninetowns.tootooplus.util.UIUtils;
import com.ninetowns.ui.widget.dialog.ProgressiveDialog;
import com.wiriamubin.service.chat.ChatData;
import com.wiriamubin.service.chat.ChatService;
import com.wiriamubin.service.chat.ChatService.OnChatMessageLisenter;
import com.wiriamubin.service.chat.Messages;
import com.wiriamubin.service.chat.MyChatData;
import com.wiriamubin.service.chat.SendMsgData;
import com.wiriamubin.service.chat.SoundMeter;
import com.wiriamubin.service.chat.UserInfo;

/**
 * @ClassName: BaseChatFragment
 * @Description: 聊天基类
 * @author zhou
 * @date 2015-2-5 下午5:22:05
 * 
 */
public class BaseChatFragment extends Fragment implements INetConstanst,
		OnClickListener, OnChatMessageLisenter {

	/**
	 * @Fields micImgResIds : 所有音量的资源id
	 */
	private Integer[] micImgResIds = { R.drawable.record_animation_01,
			R.drawable.record_animation_02, R.drawable.record_animation_03,
			R.drawable.record_animation_04, R.drawable.record_animation_05 };
	/**
	 * @Fields messageHistory : 聊天纪录
	 */
	public List<MyChatData> messageHistory = new ArrayList<MyChatData>();
	private ChatDataAdapter adapter;
	private View mChatView;
	private ChatHandler handler;
	private Uri mImageUri;

	/**
	 * @Fields mDbUtils :数据库帮手
	 */
	private static DbUtils mDbUtils;

	private View recordingContainer;
	private ImageView micImage;
	private TextView recordingHint;

	private static final String activityName = "com.ninetowns.tootoopluse.activity.ChatActivity";

	/**
	 * @Fields selectPicPopupWindow :选择图片的view
	 */
	private PopupWindow morePopupWindow;

	private Button mBTNFromAlbumButton, mBTNFromCamera, mBTNCancel;

	private static final int pageSize = 10;
	// private Button mBTNSelectPicPop, mBTNVideo;

	private ImageView mIVMore;

	List<PrivateLetterMessageBean> privateLetterMessageBeans = new ArrayList<PrivateLetterMessageBean>();
	/**
	 * @Fields offset : 聊天记录查询时的偏移量 用来定位查询位置
	 */
	private int offset = 0;
	/**
	 * @Fields mBTNSend : 发送按钮
	 */
	private Button mBTNSend;
	private ImageView mIVChatModel;
	private ListView listView;

	private View mTVPressToSpeak;
	private View mLLEdit;

	/**
	 * @Fields mImageUriInService :图片上传到服务器后的信息
	 */
	private ImageUriInService mImageUriInService;

	/**
	 * @Fields mSensor : 音频录制
	 */
	private SoundMeter mSensor;

	private static final String ApplicationId = "5";
	public String mCurrentGroupId = "1";
	// private final String nickName = "";
	private static final int POLL_INTERVAL = 300;
	public static final int RECEIVE_MSG = 1120;

	private PowerManager.WakeLock wakeLock;
	private boolean btn_mode_vocie;

	private Activity mContext;
	/**
	 * @Fields service : 聊天服务
	 */
	private ChatService service;
	/**
	 * @Fields message : 文本输入框
	 */
	private EditText message;
	/**
	 * @Fields mVoicCacheFile : 录制语音的缓存文件
	 */
	File mVoicCacheFile;
	/**
	 * @Fields startVoiceT :录制开始时间
	 * @Fields endVoiceT :录制结束时间
	 */
	private long startVoiceT, endVoiceT;
	/**
	 * @Fields voiceName : 音频名称
	 */
	private String voiceName;
	/**
	 * @Fields isPrivateLetter : 是否为私信 true 为真
	 */
	private boolean isPrivateLetter;
	/**
	 * @Fields mLoginBean : 用户信息
	 */
	private LoginBean mLoginBean;

	/**
	 * @Fields mGetChatMsgHistory : 查看聊天记录按钮
	 */
	private View mGetChatMsgHistory;

	private String receiverUserId;

	private View mBottomView;

	private View rootLayout;

	private static BaseChatFragment sInstance;

	private View selectedPictueView;
	private HashMap<Integer, List<MyChatData>> myHashMap;

	/**
	 * @Fields mTempGroupId :接收信息时临时存储
	 */
	private String mTempGroupId;

	private ChatActivity chatAct;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mChatView = inflater.inflate(R.layout.basechat_fragment, null);
		mGetChatMsgHistory = mChatView
				.findViewById(R.id.chatcativity_getmsghistory);
		selectedPictueView = mChatView
				.findViewById(R.id.selectedpicture_linearlayout);
		sInstance = this;
		if (getActivity() != null && getActivity() instanceof ChatActivity) {
			ChatActivity chatAct = (ChatActivity) getActivity();
			myHashMap = chatAct.hashMap;
		}
		Bundle bundle = getArguments();
		if (null != bundle) {
			if (bundle.containsKey("groupid")) {
				mCurrentGroupId = bundle.getString("groupid");
				mChatView.setTag(mCurrentGroupId);
				LogUtils.i("groupId=================" + mCurrentGroupId);
			} else if (bundle.containsKey("isprivateletter")) {
				isPrivateLetter = true;
				receiverUserId = bundle.getString("receiveuserid");
			}
		}
		mBTNFromAlbumButton = (Button) mChatView
				.findViewById(R.id.photopop_selectfrom_album);
		mBTNFromCamera = (Button) mChatView
				.findViewById(R.id.photopop_selectfrom_camera);
		mBTNCancel = (Button) mChatView.findViewById(R.id.photopop_cancel);
		mContext = getActivity();

		mLoginBean = SharedPreferenceHelper.getLoginMsg(mContext);

		mGetChatMsgHistory.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if (offset == 0) {
					// if (null != messageHistory && messageHistory.size() > 0)
					// {
					// messageHistory.clear();
					// }
					if (null != privateLetterMessageBeans
							&& privateLetterMessageBeans.size() > 0) {
						privateLetterMessageBeans.clear();
					}
					offset++;
				}

				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					if (isPrivateLetter == false) {

						try {
							if (getActivity() != null
									&& getActivity() instanceof ChatActivity) {
								chatAct = (ChatActivity) getActivity();
								chatAct.showProgressDialog(chatAct);
							}
							loadChatHistoryFromServer(offset);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						loadDataFromServer(offset);
					}
					offset++;
				}

				return false;
			}

		});

		initBroadcast();
		return mChatView;
	}

	private void initPop() {
		if (null == morePopupWindow) {
			morePopupWindow = new PopupWindow();

			View popupWindow_view = LayoutInflater.from(mContext).inflate(
					R.layout.pop_more, null);
			// View popupWindow_view = View.inflate(context, R.layout.pop_more,
			// null);
			popupWindow_view.findViewById(R.id.popmore_iv_picture)
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							showSelectPicturePop(mContext);
						}
					});
			popupWindow_view.findViewById(R.id.popmore_iv_video)
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							enterRecordVideo();// 进入录制视频
						}
					});
			// pop.setFocusable(true);
			morePopupWindow.setBackgroundDrawable(new BitmapDrawable());
			morePopupWindow.setOutsideTouchable(true);
			morePopupWindow.setAnimationStyle(R.style.win_ani_top_bottom);
			morePopupWindow.setContentView(popupWindow_view);
			morePopupWindow.setWidth(LayoutParams.WRAP_CONTENT);
			morePopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		}
	}

	public static BaseChatFragment getBaseChatFragmentInstance() {
		return sInstance;
	}

	/**
	 * @Title: loadChatHistoryFromServer
	 * @Description: 从服务器获取聊天 记录
	 * @param @param page 第几页
	 * @return void 返回类型
	 */
	private void loadChatHistoryFromServer(int page) throws Exception {

		loadLocalChatHistory(page);
		// RequestParamsNet requestParamsNet = new RequestParamsNet();
		// requestParamsNet.addQueryStringParameter("GroupId", mCurrentGroupId);
		// requestParamsNet.addQueryStringParameter("PageSize", pageSize + "");
		// requestParamsNet.addQueryStringParameter("Page", page + "");
		// CommonUtil.xUtilsGetSend(GET_CHAT_HISTORY, requestParamsNet,
		// new RequestCallBack<String>() {
		//
		// @Override
		// public void onSuccess(ResponseInfo<String> responseInfo) {
		// ChatHistoryParser parser = new ChatHistoryParser();
		// try {
		// List<MyChatData> myChatDatas = parser.parserData(
		// mContext, responseInfo.result);
		// // if(isFirstTouch){
		// // messageHistory.clear();
		// // isFirstTouch=false;
		// // }
		// if (null != myChatDatas) {
		// Collections.reverse(myChatDatas);
		// }
		// messageHistory.addAll(0, myChatDatas);
		// adapter.notifyDataSetChanged();
		//
		// int size = myChatDatas.size() > 0 ? myChatDatas
		// .size() + 1 : 0;
		// listView.setSelection(size);
		//
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		//
		// @Override
		// public void onFailure(HttpException error, String msg) {
		// // TODO Auto-generated method stub
		// }
		// });
	}

	/**
	 * @Title: loadChatHistory
	 * @Description:加载最近十条的聊天纪录
	 * @param 设定文件
	 * @return void 返回类型
	 */
	private void loadChatHistory() {
		try {
			List<MyChatData> lastTenMessages = getTheLastTenMessage();
			lastTenMessages = lastTenMessages == null ? new ArrayList<MyChatData>()
					: lastTenMessages;
			if (lastTenMessages.size() == 0) {
				listView.setStackFromBottom(false);
			} else {
				listView.setStackFromBottom(true);
			}
			Collections.reverse(lastTenMessages);
			messageHistory.addAll(0, lastTenMessages);
			adapter.notifyDataSetChanged();
			int size = lastTenMessages.size() > 0 ? lastTenMessages.size() + 1
					: 0;
			listView.setSelection(size);
		} catch (DbException e) {
			e.printStackTrace();
			LogUtils.e(e.getMessage());
		}
	}

	private int mCurrentLoadMessageLength = 0;

	/**
	 * @Title: loadLocalChatHistory
	 * @Description:加载本地聊天纪录
	 * @param page
	 *            分页加载的页数
	 * @return void 返回类型
	 */
	private void loadLocalChatHistory(int page) {
		try {

			List<MyChatData> lastTenMessages = getLocalHistroyMessage(page);
			lastTenMessages = lastTenMessages == null ? new ArrayList<MyChatData>()
					: lastTenMessages;
			mCurrentLoadMessageLength += lastTenMessages.size();
			if (lastTenMessages.size() == 0) {
				listView.setStackFromBottom(false);
			} else {
				listView.setStackFromBottom(true);
			}
			// Collections.reverse(lastTenMessages);
			messageHistory.addAll(lastTenMessages);
			if (mCurrentLoadMessageLength >= getTotalLocalHostoryMessage()) {
				mGetChatMsgHistory.setVisibility(View.GONE);
				chatAct.closeProgressDialog(chatAct);
				return;
			}
			Collections.sort(messageHistory, new Comparator<MyChatData>() {

				@Override
				public int compare(MyChatData lhs, MyChatData rhs) {
					return lhs.getTime().compareTo(rhs.getTime());
				}
			});

			adapter.notifyDataSetChanged();
			chatAct.closeProgressDialog(chatAct);
			int size = lastTenMessages.size() > 0 ? lastTenMessages.size() + 1
					: 0;
			listView.setSelection(size);

		} catch (DbException e) {
			e.printStackTrace();
			LogUtils.e(e.getMessage());
		}
	}

	private void loadDataFromServer(int page) {
		RequestParamsNet requestPar = new RequestParamsNet();
		requestPar.addQueryStringParameter("UserId", mLoginBean.getLogin_Id());
		requestPar.addQueryStringParameter("ReceiveUserId", receiverUserId);
		requestPar.addQueryStringParameter("PageSize", pageSize + "");
		requestPar.addQueryStringParameter("Page", page + "");

		CommonUtil.xUtilsGetSend(GET_USERPRIVATELETTER_DATA, requestPar,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						PrivateLetterMessageParser parser = new PrivateLetterMessageParser();
						// privateLetterMessageBeans =
						List<PrivateLetterMessageBean> list = parser
								.getParseResult(responseInfo.result);
						privateLetterMessageBeans.addAll(list);
						adapter = new ChatDataAdapter(mContext, null,
								privateLetterMessageBeans, false);
						listView.setAdapter(adapter);

					}

					@Override
					public void onFailure(HttpException error, String msg) {

					}
				});

	}

	private void initView(View mChatView) {
		recordingContainer = mChatView.findViewById(R.id.recording_container);
		micImage = (ImageView) mChatView.findViewById(R.id.mic_image);
		recordingHint = (TextView) mChatView.findViewById(R.id.recording_hint);
		mSensor = new SoundMeter();
		// volume = (ImageView) mChatView.findViewById(R.id.volume);
		mContext.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		wakeLock = ((PowerManager) mContext
				.getSystemService(Context.POWER_SERVICE)).newWakeLock(
				PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
		mIVMore = (ImageView) mChatView.findViewById(R.id.btn_more_pop);
		mBottomView = mChatView.findViewById(R.id.rl_bottom);
		mBTNSend = (Button) mChatView.findViewById(R.id.chatroom_btn_send);
		mIVChatModel = (ImageView) mChatView
				.findViewById(R.id.btn_voicekeyboardmodel_icon);
		mTVPressToSpeak = mChatView.findViewById(R.id.chatroom_tv_presstospeak);
		mLLEdit = mChatView.findViewById(R.id.chatroom_ll_edit);
		// msgTypeContainer = mChatView.findViewById(R.id.ll_msgtype_container);
		handler = new ChatHandler();
		mDbUtils = DbUtils.create(mContext);
		message = (EditText) mChatView.findViewById(R.id.main_edittext_message);
		listView = (ListView) mChatView
				.findViewById(R.id.main_listview_message);

		rootLayout = mChatView.findViewById(R.id.LinearLayout1);
		clearMessageHistory();
		// try {
		if (isPrivateLetter == false) {
			messageHistory = new ArrayList<MyChatData>();
			// messageHistory = getTheLastTenMessage();
			adapter = new ChatDataAdapter(mContext, messageHistory, null, true);
			listView.setAdapter(adapter);
			service = ChatService.getInstance();
			// 调试
			service.setMessageListener(this);
		} else {
			loadDataFromServer(offset);
		}
	}

	/**
	 * @Title: getTheLastTenMessage
	 * @Description: 获取最后十条 消息
	 * @param @throws DbException 设定文件
	 * @return void 返回类型
	 * @throws WhereBuilder.b
	 *             (("userId", "=", mLoginBean.getLogin_Id())).or(columnName,
	 *             op, value))
	 */
	private List<MyChatData> getTheLastTenMessage() throws DbException {
		List<MyChatData> myChatDatas = mDbUtils
				.findAll(Selector
						.from(MyChatData.class)
						.where("groupId", "=", mCurrentGroupId)
						.and(WhereBuilder.b("isChatRoom", "=", true)
						// .and("userId", "=",mLoginBean.getLogin_Id())
						).orderBy("id", true).limit(pageSize)
						.offset(pageSize * offset));
		myChatDatas = myChatDatas == null ? new ArrayList<MyChatData>()
				: myChatDatas;
		mCurrentLoadMessageLength += myChatDatas.size();
		return myChatDatas;
	}

	/**
	 * @Title: getLocalHistroyMessage
	 * @Description: 获取本地聊天 消息(分页查找)
	 * @param page
	 *            需要查询的页数
	 * @param @throws DbException 设定文件
	 * @return void 返回类型
	 * @throws WhereBuilder.b
	 *             (("userId", "=", mLoginBean.getLogin_Id())).or(columnName,
	 *             op, value))
	 */
	private List<MyChatData> getLocalHistroyMessage(int page)
			throws DbException {
		List<MyChatData> myChatDatas = mDbUtils.findAll(Selector
				.from(MyChatData.class).where("groupId", "=", mCurrentGroupId)
				.and(WhereBuilder.b("isChatRoom", "=", true))
				.orderBy("id", true).limit(pageSize).offset(pageSize * page));
		myChatDatas = myChatDatas == null ? new ArrayList<MyChatData>()
				: myChatDatas;
		return myChatDatas;
	}

	/**
	 * @Title getTotalLocalHostoryMessage
	 * @Description 得到本地保存的数据总数
	 * @param void
	 * @return 返回信息总数
	 */
	public int getTotalLocalHostoryMessage() {

		List<MyChatData> myChatDatas = null;
		try {
			myChatDatas = mDbUtils.findAll(Selector.from(MyChatData.class)
					.where("groupId", "=", mCurrentGroupId)
					.and(WhereBuilder.b("isChatRoom", "=", true))
					.orderBy("id", true));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return myChatDatas != null && !myChatDatas.isEmpty() ? myChatDatas
				.size() : 0;
	}

	/**
	 * @Title: clearMessageHistory
	 * @Description: 清空聊天记录
	 * @param 设定文件
	 * @return void 返回类型
	 */
	private void clearMessageHistory() {
		if (null != messageHistory && messageHistory.size() > 0) {
			mCurrentLoadMessageLength = 0;
			messageHistory.clear();
		}
	}

	private class ChatHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {

			if (!isPrivateLetter) {
				if (!TextUtils.isEmpty(mTempGroupId)&&!mTempGroupId.equals(mCurrentGroupId))
					if (RECEIVE_MSG == msg.what) {
						SendMessageBean daterbena = (SendMessageBean) msg.obj;
						// String groupIdView=(String) viewHolder.getTag();
						String dater = daterbena.getMesJson();
						String groupId = daterbena.getGroupId();
						// String currentGroupId =
						// daterbena.getCurrentGroupId();
						if (TextUtils.isEmpty(groupId)) {
							return;
						}
						UserInfo userInfo = null;
						Messages messages = null;
						JSONObject object;
						if (TextUtils.isEmpty(dater)) {
							return;
						}
						try {
							object = new JSONObject(dater);
							if (null != object) {

								if (object.has("NoticeType")
										&& SINGLE_LONGIN_NOTICE.equals(object
												.getString("NoticeType"))) {
									handler.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											UIUtils.showCenterToast(
													TootooPlusApplication
															.getAppContext(),
													"您的账号已在别处登录");
										}
									});
									CommonUtil.exitApp(TootooPlusApplication
											.getAppContext());
									return;
								}
								if (object.has("UserInfo")) {
									userInfo = JsonTools.jsonObj(
											object.getString("UserInfo"),
											UserInfo.class);
								}
								if (object.has("Messages")) {
									messages = JsonTools.jsonObj(
											object.getString("Messages"),
											Messages.class);
								}

								mMyChatData = new MyChatData(userInfo.UserId,
										userInfo.UserName, userInfo.LogoUrl,
										messages.Type, messages.Content,
										groupId, true, true,
										TimeUtil.formatDate(new Date()));
								// if (receiveDataListener != null) {
								// receiveDataListener
								// .OnReceiveDataListener(mMyChatData);
								// }

								try {
									boolean isSuccess = mDbUtils
											.saveBindingId(mMyChatData);
									LogUtils.i("save success============"
											+ isSuccess);
								} catch (DbException e) {
									e.printStackTrace();
									LogUtils.e(e.getMessage());
								}
							}
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						return;
					}
			}
			switch (msg.what) {
			case MAIN_RECEIVED_MESSAGE:
				if (isSame == true) {
					refreshAdapter();
				} else {
					if (UIUtils.isTopNewChatActivity(mContext, activityName))
						((ChatActivity) mContext).refreshFragment();
				}
				break;
			case MAIN_CONNECT_DISCONNECT:
				UIUtils.showCenterToast(mContext, "链接断开");
				break;
			case UPLOAD_IMAGE_SUCCESS:// 图片发送 的 为原图 地址 缩略图地址 自己截取拼接
				if (null != mImageUriInService) {
					if (!TextUtils.isEmpty(mImageUriInService.FileUrl)) {
						if (!isPrivateLetter) {
							sendPictureMessage(mImageUriInService.FileUrl);
						} else {
							sendPrivatePicMessage(mImageUriInService.FileUrl);
						}
					}
				}
				break;
			case UPLOAD_AUDIO_SUCCESS:
				if (null != mImageUriInService) {
					if (!TextUtils.isEmpty(mImageUriInService.FileUrl)) {
						if (!isPrivateLetter) {
							sendVoiceMessage(mImageUriInService.FileUrl);
						} else {
							sendPrivateLetterVoiceMessage(mImageUriInService.FileUrl);
						}
					}
				}
				FileUtils.deleteFile(mVoicCacheFile);
				break;
			case UPLOAD_VIDEO_SUCCESS:
				if (null != mImageUriInService
						&& !StringUtils.isEmpty(mImageUriInService.FileUrl)) {
					LogUtils.i("" + UPLOAD_VIDEO_SUCCESS);
					if (!isPrivateLetter) {
						sendVideoMessage(mImageUriInService.ThumbFileUrl + ";"
								+ mImageUriInService.FileUrl);
					} else {
						sendPrivateLetterVideo(mImageUriInService.ThumbFileUrl
								+ ";" + mImageUriInService.FileUrl);
					}
				}
				break;
			case RECORD_TIMER:
				recordingHint.setText("录制时间还有" + msg.arg1 + "s");
				break;

			case RECEIVE_MSG:
				SendMessageBean daterbena = (SendMessageBean) msg.obj;
				// String groupIdView=(String) viewHolder.getTag();
				String dater = daterbena.getMesJson();
				String groupId = daterbena.getGroupId();
				// String currentGroupId = daterbena.getCurrentGroupId();
				if (TextUtils.isEmpty(groupId)) {
					return;
				}
				UserInfo userInfo = null;
				Messages messages = null;
				JSONObject object;
				if (TextUtils.isEmpty(dater)) {
					return;
				}
				try {
					object = new JSONObject(dater);
					if (null != object) {

						if (object.has("NoticeType")
								&& SINGLE_LONGIN_NOTICE.equals(object
										.getString("NoticeType"))) {
							handler.post(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									UIUtils.showCenterToast(
											TootooPlusApplication
													.getAppContext(),
											"您的账号已在别处登录");
								}
							});
							CommonUtil.exitApp(TootooPlusApplication
									.getAppContext());
							return;
						}
						if (object.has("UserInfo")) {
							userInfo = JsonTools.jsonObj(
									object.getString("UserInfo"),
									UserInfo.class);
						}
						if (object.has("Messages")) {
							messages = JsonTools.jsonObj(
									object.getString("Messages"),
									Messages.class);
						}

						mMyChatData = new MyChatData(userInfo.UserId,
								userInfo.UserName, userInfo.LogoUrl,
								messages.Type, messages.Content, groupId, true,
								true, TimeUtil.formatDate(new Date()));
						if (receiveDataListener != null) {
							receiveDataListener
									.OnReceiveDataListener(mMyChatData);
						}

						// if (groupId.equals(currentGroupId)) {
						// isSame = true;
						// } else {
						// isSame = false;
						// }
						// if(getActivity()!=null&&getActivity() instanceof
						// ChatActivity){
						// ChatActivity chatAct=(ChatActivity) getActivity();
						// //
						// myHashMap.get(chatAct.myCurrentPosition).add(mMyChatData);
						// }

						// messageHistory.add(mMyChatData);
						try {
							boolean isSuccess = mDbUtils
									.saveBindingId(mMyChatData);
							LogUtils.i("save success============" + isSuccess);
						} catch (DbException e) {
							e.printStackTrace();
							LogUtils.e(e.getMessage());
						}
						// Message msg1 = Message.obtain();
						// msg.what = MAIN_RECEIVED_MESSAGE;
						// handler.sendMessage(msg1);
						// if (isSame == true) {
						// refreshAdapter();
						// } else {
						// if (UIUtils.isTopNewChatActivity(mContext,
						// activityName))
						// ((ChatActivity) mContext).refreshFragment();
						// }
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// msg.obj;
				break;
			default:
				break;
			}
		}
	}

	private void sendPrivateLetterVideo(String url) {
		sendPrivateLetterMessage(url, PRIVATELETTER_VIDEO + "");
	}

	/**
	 * @Title: sendPrivatePicMessage
	 * @Description: 发送私信图片 (原图地址)
	 * @param @param imageUrl 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void sendPrivatePicMessage(final String imageUrl) {
		sendPrivateLetterMessage(imageUrl, PRIVATELETTER_IMG + "");
	}

	/**
	 * @Fields mFinishRecordVideoBroadcast : 完成视频录制
	 */
	private FinishRecordVideoBroadcast mFinishRecordVideoBroadcast;
	/**
	 * @Fields mReceiverMessageBroadcast : 接收消息的广播
	 */
	private ReceiverMessageBroadcastReceiver mReceiverMessageBroadcast;

	private void initBroadcast() {
		mFinishRecordVideoBroadcast = new FinishRecordVideoBroadcast();
		ReceiverManager.getIntance().registerReceiver(mContext,
				mFinishRecordVideoBroadcast, FINISH_RECORDVIDEO_ACTION);
		mReceiverMessageBroadcast = new ReceiverMessageBroadcastReceiver();
		ReceiverManager.getIntance().registerReceiver(mContext,
				mReceiverMessageBroadcast, RECEIVE_MESSAGE_ACTION);
	}

	private class FinishRecordVideoBroadcast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (null != intent) {
				Bundle bundle = intent.getExtras();
				String pathString = bundle.getString("videourl");
				if (!StringUtils.isEmpty(pathString)) {
					File imageThumb = ImageUtil.getVideoThumbnailPhoto(
							pathString,
							MediaStore.Images.Thumbnails.FULL_SCREEN_KIND, 480,
							480);
					upload(pathString, "3", imageThumb);
				}
			}
		}
	}

	/**
	 * @Title: sendPrivateLetterVoiceMessage
	 * @Description: 发送语音私信
	 * @param @param fileUrl 设定文件
	 * @return void 返回类型
	 */
	public void sendPrivateLetterVoiceMessage(String fileUrl) {
		sendPrivateLetterMessage(fileUrl, PRIVATELETTER_AUDIO + "");
	}

	private void sendPrivateLetterMessage(final String imageUrl,
			final String type) {

		closeProgressDialogFragment();
		RequestParamsNet requestParamsNet = new RequestParamsNet();
		requestParamsNet.addQueryStringParameter("UserId",
				mLoginBean.getLogin_Id());
		requestParamsNet.addQueryStringParameter("ToUserId", receiverUserId);
		requestParamsNet.addQueryStringParameter("Type", type);
		requestParamsNet.addQueryStringParameter("Content", imageUrl);
		CommonUtil.xUtilsGetSend(SEND_PRIVATELETTER, requestParamsNet,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						boolean isSuccess = ParserUitils
								.isSuccess(responseInfo);
						if (isSuccess) {
							PrivateLetterMessageBean privateLetterMessageBean = new PrivateLetterMessageBean();
							String result = responseInfo.result;
							privateLetterMessageBean.setContent(imageUrl);
							privateLetterMessageBean.setCreateDate(TimeUtil
									.formatDate(new Date()));
							privateLetterMessageBean.setType(type);
							privateLetterMessageBean.setUserIdSend(mLoginBean
									.getLogin_Id());
							privateLetterMessageBean.setUserName(mLoginBean
									.getLogin_name());
							privateLetterMessageBean.setLogoUrl(mLoginBean
									.getLogin_logoUrl());
							privateLetterMessageBeans
									.add(privateLetterMessageBean);
							adapter = new ChatDataAdapter(mContext, null,
									privateLetterMessageBeans, false);
							listView.setAdapter(adapter);
							try {
								mDbUtils.saveBindingId(privateLetterMessageBean);
							} catch (DbException e) {
								e.printStackTrace();
								LogUtils.e(e.getMessage());
							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
					}
				});
	}

	public void refreshAdapter() {
		adapter.notifyDataSetChanged();
		LogUtils.i("groupID=========" + mCurrentGroupId);
		listView.setSelection(listView.getCount() - 1);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView(mChatView);
		setListener();
		if (isPrivateLetter == false) {
			loadChatHistory();// 加载聊天记录
		}
	}

	private void setListener() {
		listView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				UIUtils.hideSoftInput(mContext);
				dismissMorePop();
				dismissSelectedPop();
				return false;
			}
		});
		mBTNSend.setOnClickListener(this);
		mIVChatModel.setOnClickListener(this);
		mIVMore.setOnClickListener(this);
		message.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				updateRightButtonIsSendOrMore(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		mTVPressToSpeak.setOnTouchListener(new PressToSpeakListen());

		mBTNCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismissSelectedPop();
			}
		});
		mBTNFromAlbumButton.setOnClickListener(selectFromAlbumListener);
		mBTNFromCamera.setOnClickListener(selectFromCameraListener);
	}

	private void dismissSelectedPop() {
		UIUtils.dismissViewAddAnmimal(selectedPictueView, mContext);
		setButtonUnclickable();
	}

	/**
	 * @Title: setButtonUnclickable
	 * @Description:使button 不能被点击
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void setButtonUnclickable() {
		mBTNFromAlbumButton.setClickable(false);
		mBTNCancel.setClickable(false);
		mBTNFromCamera.setClickable(false);
		mBTNFromAlbumButton.setFocusable(false);
		mBTNCancel.setFocusable(false);
		mBTNFromCamera.setFocusable(false);
	}

	private Timer timer;

	/**
	 * @Title: updateRightButtonIsSendOrMore
	 * @Description: 更新最右边显示的按钮
	 * @param @param s 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void updateRightButtonIsSendOrMore(CharSequence s) {
		if (!TextUtils.isEmpty(s)) {
			UIUtils.setViewVisible(mBTNSend);
			UIUtils.setViewGone(mIVMore);
		} else {
			UIUtils.setViewVisible(mIVMore);
			UIUtils.setViewGone(mBTNSend);
		}
	}

	/**
	 * @ClassName: PressToSpeakListen
	 * @Description: 按住说话 监听事件
	 * @author zhou
	 * @date 2015-4-3 下午1:20:15
	 * 
	 */
	class PressToSpeakListen implements View.OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!FileUtils.isExitsSdcard()) {
					UIUtils.showCenterToast(mContext, "发送语音需要sdcard支持！");
					return false;
				}
				try {
					v.setPressed(true);
					wakeLock.acquire();
					UIUtils.setViewVisible(recordingContainer);
					recordingContainer.bringToFront();
					// recordingContainer.setVisibility(View.VISIBLE);
					recordingHint
							.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
					startVoiceT = SystemClock.uptimeMillis();
					voiceName = startVoiceT + ".amr";

					mVoicCacheFile = FileUtils.getVoiceCacheFile(voiceName);
					start(mVoicCacheFile.getAbsolutePath());
					startTimer(v, event);
				} catch (Exception e) {

					stopTimer();
					mSensor.resetRecord();
					e.printStackTrace();
					LogUtils.e(e.getMessage());
					UIUtils.setViewGone(recordingContainer);
					v.setPressed(false);
					UIUtils.showCenterToast(mContext, "录音失败，请重试！");
					if (wakeLock.isHeld())
						wakeLock.release();
					return false;
				}
				return true;

			case MotionEvent.ACTION_MOVE: {

				if (event.getY() < 0) {
					recordingHint
							.setText(getString(R.string.release_to_cancel));
					recordingHint
							.setBackgroundResource(R.drawable.recording_text_hint_bg);
				} else {
					recordingHint
							.setText(getString(R.string.move_up_to_cancel));
					recordingHint.setBackgroundColor(Color.TRANSPARENT);
				}
				return true;
			}
			case MotionEvent.ACTION_UP:
				stopRecord(v, event);
				return true;
			default:
				stopTimer();
				UIUtils.setViewGone(recordingContainer);
				FileUtils.deleteFile(mVoicCacheFile);
				return false;
			}

		}

		/**
		 * @Title: startTimer
		 * @Description: 开启定时器 每秒执行一次
		 * @param 设定文件
		 * @return void 返回类型
		 * @throws
		 */

		int i = 60;

		private void startTimer(final View v, final MotionEvent event) {
			timer = new Timer();

			// 定义计划任务，根据参数的不同可以完成以下种类的工作：在固定时间执行某任务，在固定时间开始重复执行某任务，重复时间间隔可控，在延迟多久后执行某任务，在延迟多久后重复执行某任务，重复时间间隔可控
			timer.schedule(new TimerTask() {

				// TimerTask 是个抽象类,实现的是Runable类
				@Override
				public void run() {
					LogUtils.i("timer  run...." + i);
					i--;
					if (i == 0) {
						mContext.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								stopRecord(v, event);
							}
						});

					} else if (5 >= i && i > 0) {
						// 定义一个消息传过去
						Message msg = new Message();
						msg.what = RECORD_TIMER;
						msg.arg1 = i;
						LogUtils.i("i=====" + i);
						handler.sendMessage(msg);
					}
				}

			}, 0, 1000);
		}

	}

	/**
	 * @Title: stopRecord
	 * @Description: 停止 录制
	 * @param @param v
	 * @param @param event 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void stopRecord(View v, MotionEvent event) {

		v.setPressed(false);
		if (wakeLock.isHeld())
			wakeLock.release();
		UIUtils.setViewGone(recordingContainer);
		stop();

		if (event.getY() < 0) {
			FileUtils.deleteFile(mVoicCacheFile);
		} else {
			endVoiceT = SystemClock.uptimeMillis();
			int time = (int) (endVoiceT - startVoiceT);
			if (time < 1000) {
				UIUtils.showShortCenterToast(mContext, R.string.time_tooshort);
			}
			upload(mVoicCacheFile.getAbsolutePath(), "2");
			if (getActivity() != null) {
				showProgressDialog(getActivity());
			}

		}
	}

	private void stop() {
		stopTimer();
		handler.removeCallbacks(mPollTask);
		mSensor.stop();
		micImage.setImageResource(R.drawable.record_animation_01);
	}

	/**
	 * @Title: stopTimer
	 * @Description: 停止计时器
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void stopTimer() {
		if (null != timer) {
			timer.cancel();
			timer = null;
		}
	}

	/**
	 * @param path
	 *            本地图片路径
	 * @Title: upload
	 * @Description: 上传文件
	 * @param 设定文件
	 *            type Type：上传文件类型 (必填 1图片，2语音，3视频)
	 * @return String 返回类型 返回值是缩略图的地址
	 */
	public void upload(String path, final String type, File imageThumbFile) {
		File videoFile = new File(path);
		if (videoFile.exists()) {
			RequestParamsNet params = new RequestParamsNet(); // 默认编码UTF-8
			params.addBodyParameter("UserId", USERID);
			params.addBodyParameter("ApplicationId", ApplicationId);
			params.addBodyParameter("Type", type);

			if ("1".equals(type)) {
				params.addBodyParameter("File1", videoFile, "image/*");
			} else if ("2".equals(type)) {
				params.addBodyParameter("File1", videoFile, "audio/*");
			} else {
				params.addBodyParameter("File1", videoFile, "video/*");
				// 添加文件
				if (null != imageThumbFile && imageThumbFile.exists()) {
					params.addBodyParameter("File2", imageThumbFile, "image/*");
				}
			}
			CommonUtil.uploadPostXutil(UPLOAD_URL, params,
					new RequestCallBack<String>() {

						@Override
						public void onStart() {
							// UIUtils.showCenterToast(mContext, "开始上传");
						}

						@Override
						public void onLoading(long total, long current,
								boolean isUploading) {

						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							JSONObject object;
							try {
								object = new JSONObject(responseInfo.result);
								Gson gson = new Gson();
								mImageUriInService = gson.fromJson(
										object.getString("Data"),
										ImageUriInService.class);

							} catch (JSONException e1) {
								LogUtils.e(e1.getMessage());
							}

							if ("1".equals(type)) {

								handler.sendEmptyMessage(UPLOAD_IMAGE_SUCCESS);

							} else if ("2".equals(type)) {
								handler.sendEmptyMessage(UPLOAD_AUDIO_SUCCESS);

							} else if ("3".equals(type)) {
								handler.sendEmptyMessage(UPLOAD_VIDEO_SUCCESS);
							}

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							UIUtils.showCenterToast(mContext, msg);
							LogUtils.e(msg);
						}
					});

		} else {
		}
	}

	/**
	 * @Title: updateDisplay
	 * @Description: 更新音量大小的显示
	 * @param @param signalEMA 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void updateVolumePictureDisplay(int signalEMA) {

		switch ((int) signalEMA) {
		case 0:
		case 1:
			micImage.setImageResource(micImgResIds[0]);
			break;
		case 3:
		case 2:
			micImage.setImageResource(micImgResIds[1]);
			break;
		case 4:
		case 5:
			micImage.setImageResource(micImgResIds[2]);
			break;
		case 6:
		case 7:
			micImage.setImageResource(micImgResIds[3]);
			break;

		default:
			micImage.setImageResource(micImgResIds[4]);
			break;
		}
	}

	private Runnable mPollTask = new Runnable() {
		public void run() {
			int amp = mSensor.getAmplitude();
			updateVolumePictureDisplay(amp);
			handler.postDelayed(mPollTask, POLL_INTERVAL);
		}
	};

	private void start(String name) throws Exception {
		mSensor.start(name);
		handler.postDelayed(mPollTask, POLL_INTERVAL);
	}

	/**
	 * @Title: sendVideoMessage
	 * @Description: 发送视频消息(自定义规则 fileUrl=缩略图地址+";"+视频地址) 收到信息时 自己截取
	 * @param @param fileUrl 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void sendVideoMessage(String fileUrl) {
		if (null == mLoginBean) {
			return;
		}
		ChatData data = new ChatData(mLoginBean.getLogin_Id(),
				mLoginBean.getLogin_name(), mLoginBean.getLogin_logoUrl(),
				MSGTYPE_VIDEO, fileUrl);
		LogUtils.i("sendVideoMessage");
		LogUtils.i("type" + data.type);
		sendMessageAndUpdataUI(data);
	}

	/**
	 * @Title: sendVoiceMessage
	 * @Description: 发送语音消息
	 * @param @param fileUrl 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void sendVoiceMessage(String fileUrl) {

		if (null == mLoginBean) {
			return;
		}
		ChatData data = new ChatData(mLoginBean.getLogin_Id(),
				mLoginBean.getLogin_name(), mLoginBean.getLogin_logoUrl(),
				MSGTYPE_AUDIO, fileUrl);
		LogUtils.i("type" + data.type);
		sendMessageAndUpdataUI(data);

	}

	/**
	 * @Title: sendVoiceMessage
	 * @Description: 发送图片(发送原图地址)
	 * @param @param fileUrl 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void sendPictureMessage(String fileUrl) {

		if (null == mLoginBean) {
			return;
		}
		ChatData data = new ChatData(mLoginBean.getLogin_Id(),
				mLoginBean.getLogin_name(), mLoginBean.getLogin_logoUrl(),
				MSGTYPE_IMG, fileUrl);

		LogUtils.i("type" + data.type);
		sendMessageAndUpdataUI(data);

	}

	/**
	 * @Title: sendMessage
	 * @Description: TODO 发送信息
	 * @param @param data 设定文件
	 * @return void 返回类型
	 */
	private void sendMessageAndUpdataUI(ChatData data) {
		SendMsgData msgData = new SendMsgData();

		msgData.setMessage(new Messages(data.type, data.body));
		msgData.setUserInfo(new UserInfo(data.userId, data.name,
				data.profileImage));
		String chatData = JsonTools.classToJson(msgData);
		// ComponentUtil.showToast(getActivity(), "发送"+chatData);
		// byte[] encryptData = null;
		// try {
		// encryptData = chatData.getBytes("utf-8");
		// } catch (UnsupportedEncodingException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		String str = null;

		try {
			str = Des3.encode(chatData);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		boolean isSendSuccess = service.send(str, mCurrentGroupId);
		if (isSendSuccess) {
			closeProgressDialogFragment();
			MyChatData myChatData = new MyChatData(data, mCurrentGroupId,
					false, true, TimeUtil.formatDate(new Date()));
			messageHistory.add(myChatData);
			refreshAdapter();
			if (getActivity() != null && getActivity() instanceof ChatActivity) {
				ChatActivity chatAct = (ChatActivity) getActivity();
				myHashMap.put(chatAct.myCurrentPosition, messageHistory);
			}

			try {
				LogUtils.i("save sqlite before");
				mDbUtils.saveBindingId(myChatData);
				LogUtils.i("save sqlite after");
			} catch (DbException e) {
				e.printStackTrace();
				LogUtils.e(e.getMessage());
			}
		} else {
			boolean isConnectRtmp = false;
			while (false == isConnectRtmp) {
				isConnectRtmp = service.connectRtmp(
						SharedPreferenceHelper.getReqRtmpUrl(mContext),
						mLoginBean.getLogin_Id());
			}
			sendMessageAndUpdataUI(data);
		}
	}

	private boolean isSame = true;
	private MyChatData mMyChatData;

	public void onReceivedMessage(final String data, final String groupId) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				try {
					String dater = Des3.decode(data);
					SendMessageBean smb = new SendMessageBean();
					if (getActivity() != null
							&& getActivity() instanceof ChatActivity) {
						ChatActivity chatAct = (ChatActivity) getActivity();
						// smb.setCurrentGroupId(chatAct.myCurrentGroupId);
						mCurrentGroupId = chatAct.myCurrentGroupId;
					}
					if (receiveDataListener != null) {
						receiveDataListener.OnReceiveGroupIdListener(smb);
					}
					LogUtils.d(groupId);
					mTempGroupId = groupId;
					smb.setGroupId(groupId);
					// smb.setViewHolder(mChatView);

					smb.setMesJson(dater);
					Message msg = Message.obtain();
					msg.what = RECEIVE_MSG;
					msg.obj = smb;
					handler.sendMessage(msg);
					// ComponentUtil.showToast(getActivity(), "接收"+dater);
					// dater=new String(decryptData,"utf-8");
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
					// return;
				}
				Looper.loop();

			}
		}).start();

	}

	public void onDisconnect() {
		LogUtils.i("java onDisconnect");
		handler.sendEmptyMessage(MAIN_CONNECT_DISCONNECT);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chatroom_btn_send:
			LogUtils.e("sendbutton onclick");
			if (mLoginBean == null) {
				break;
			}
			if (!NetworkUtil.isNetworkAvaliable(mContext)) {
				UIUtils.showCenterToast(mContext, "无网络连接");
				break;
			}
			if (!"".equals(message.getText()) && null != message.getText()) {
				if (!isPrivateLetter) {
					ChatData data = new ChatData(mLoginBean.getLogin_Id(),
							mLoginBean.getLogin_name(),
							mLoginBean.getLogin_logoUrl(), MSGTYPE_TEXT,
							message.getText().toString());
					LogUtils.i("sendgroupId=====" + mCurrentGroupId);
					sendMessageAndUpdataUI(data);
				} else {
					sendPrivateLetterText(message.getText().toString());
				}
			}
			message.setText("");
			break;
		case R.id.btn_voicekeyboardmodel_icon:
			changeCurrentModel();

			break;
		case R.id.btn_more_pop:
			UIUtils.hideSoftInput(mContext);
			initPop();
			morePopupWindow.showAtLocation(rootLayout, Gravity.RIGHT
					| Gravity.BOTTOM, 0, mBottomView.getHeight());
			break;
		}

	}

	private void sendPrivateLetterText(final String content) {
		sendPrivateLetterMessage(content, PRIVATELETTER_TEXT + "");
	}

	/**
	 * @Title: enterRecordVideo
	 * @Description: 进入录制视频页面
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void enterRecordVideo() {
		dismissMorePop();
		// UIUtils.closeMorePop();
		Intent recordIntent = new Intent(mContext,
				ChatRoomRecordVideoActivity.class);
		mContext.startActivityForResult(recordIntent, REQUESTCODE_RECORD_VIDEO);
	}

	public void showSelectPicturePop(Context context) {
		dismissMorePop();

		setButtonClickable();
		// UIUtils.closeMorePop();
		UIUtils.showViewAddAnmimal(selectedPictueView, mContext);
	}

	/**
	 * @Title: setButtonClickable
	 * @Description: 设置button可以被点击
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void setButtonClickable() {
		mBTNFromAlbumButton.setClickable(true);
		mBTNCancel.setClickable(true);
		mBTNFromCamera.setClickable(true);
		mBTNFromAlbumButton.setFocusable(true);
		mBTNCancel.setFocusable(true);
		mBTNFromCamera.setFocusable(true);
	}

	private void dismissMorePop() {
		if (null != morePopupWindow && morePopupWindow.isShowing()) {
			morePopupWindow.dismiss();
		}

	}

	private View.OnClickListener selectFromAlbumListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			dismissSelectedPop();
			LogUtils.i("selectFromAlbumListener  called.............");
			FileUtils.doChoicePhoto(mContext, CHOICE_PHOTO);
		}
	};
	private View.OnClickListener selectFromCameraListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			dismissSelectedPop();
			mImageUri = FileUtils.generateImageUri();
			LogUtils.i("selectFromCameraListener  called.............");
			FileUtils.doTakePhoto(mContext, mImageUri, TAKE_PHOTO);
		}
	};
	private ProgressiveDialog progressDialog;

	/**
	 * @Title: setModeVoice
	 * @Description:打开发送语音模式
	 * @param @param v 设定文件
	 * @return void 返回类型
	 */
	public void changeCurrentModel() {
		if (btn_mode_vocie) {
			setModeKeyboard();
		} else {
			setModeVoice();
		}
	}

	private void setModeVoice() {
		UIUtils.hideSoftInput(mContext);
		mIVChatModel.setImageResource(R.drawable.icon_rec_create_tv);
		hideKeyboardButtons();
		showVoiceButtons();
		btn_mode_vocie = true;
	}

	/**
	 * 
	 * @Title: ComponentUtil.java
	 * @Description: 显示dialog
	 * @author wuyulong
	 * @date 2014-7-14 下午4:23:26
	 * @param
	 * @return void
	 */
	public void showProgressDialog(Activity activity) {
		if (activity != null) {
			if ((!activity.isFinishing()) && (progressDialog == null)) {
				progressDialog = new ProgressiveDialog(activity);
			}
			if (progressDialog != null) {
				progressDialog.setMessage(R.string.loading);
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.show();

			}
		}

	}

	/**
	 * 
	 * @Title: ComponentUtil.java
	 * @Description: 取消dialog
	 * @author wuyulong
	 * @date 2014-7-14 下午4:23:48
	 * @param
	 * @return void
	 */
	public void closeProgressDialogFragment() {
		if (progressDialog != null) {
			if (progressDialog.isShowing())
				progressDialog.dismiss();
		}
	}

	/**
	 * @Title: setModeKeyboard
	 * @Description: 键盘输入模式
	 * @param @param v 设定文件
	 * @return void 返回类型
	 */
	public void setModeKeyboard() {
		mIVChatModel.setImageResource(R.drawable.chat_iv_voice);
		UIUtils.setViewGone(mTVPressToSpeak);
		showKeyBoardButtons();
		btn_mode_vocie = false;
	}

	private void showKeyBoardButtons() {
		message.requestFocus();
		// UIUtils.setViewVisible(mBTNChatModel);
		UIUtils.setViewVisible(mLLEdit);
		updateRightButtonIsSendOrMore(message.getText());
	}

	private void showVoiceButtons() {
		UIUtils.setViewVisible(mTVPressToSpeak);
		UIUtils.setViewVisible(mIVMore);
	}

	private void hideKeyboardButtons() {
		UIUtils.setViewGone(mLLEdit);
		UIUtils.setViewGone(mBTNSend);
		UIUtils.setViewGone(mLLEdit);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CHOICE_PHOTO:
			if (data != null) {
				// 获取路径
				try {
					Uri originalUri = data.getData();
					String path = FileUtils.getPath(mContext, originalUri);
					Bitmap photoBitmap = FileUtils.getBitmapFromPath(path,
							mContext);
					FileUtils.savePhotoToSDCard(photoBitmap, path);
					upload(path, "1");
					if (getActivity() != null) {
						showProgressDialog(getActivity());
					}

				} catch (Exception e) {
					closeProgressDialogFragment();
				}
			}
			break;
		case TAKE_PHOTO:

			String path = FileUtils.getPath(mContext, mImageUri);
			if (resultCode == Activity.RESULT_OK) {
				try {
					Bitmap photoBitmap = FileUtils.getBitmapFromPath(path,
							mContext);
					FileUtils.savePhotoToSDCard(photoBitmap, path);
					upload(path, "1");
					if (getActivity() != null)
						showProgressDialog(getParentFragment().getActivity());
				} catch (Exception e) {
					closeProgressDialogFragment();
				}
			} else {// 删除可能拍好的照片
				File file = new File(path);
				if (file.exists()) {
					file.delete();
				}
			}
			break;

		case REQUESTCODE_RECORD_VIDEO:
			try {

				String urlString = data.getStringExtra("videourl");
				if (!StringUtils.isEmpty(urlString)) {
					File imageThumb = ImageUtil.getVideoThumbnailPhoto(
							urlString,
							MediaStore.Images.Thumbnails.FULL_SCREEN_KIND, 480,
							480);
					upload(urlString, "3", imageThumb);
					if (getActivity() != null) {
						showProgressDialog(getActivity());
					}

				}
			} catch (Exception e) {
				closeProgressDialogFragment();
			}

			break;
		}
	}

	private void upload(String path, String type) {
		upload(path, type, null);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LogUtils.i("onDestroy");
		ReceiverManager.getIntance().unRegisterReceiver(
				mFinishRecordVideoBroadcast);
		ReceiverManager.getIntance().unRegisterReceiver(
				mReceiverMessageBroadcast);
		stopTimer();
	}

	private class ReceiverMessageBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (null != intent) {
				Bundle bundle = intent.getExtras();
				if (null != bundle)
					onReceivedMessage(bundle.getString("data"),
							bundle.getString("groupid"));
			}

		}
	}

	/**
	 * @Title: deleteTable
	 * @Description: 清除数据库中的 表信息
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static void deleteTable() {
		if (null != mDbUtils) {
			try {
				mDbUtils.dropTable(MyChatData.class);
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @ClassName: ReceiveDataListener
	 * @Description: 监听接收到的数据
	 * @author wuyulong
	 * @date 2015-7-16 上午11:06:59
	 * 
	 */
	public interface ReceiveDataListener {
		public void OnReceiveDataListener(MyChatData myChatData);

		public void OnReceiveGroupIdListener(SendMessageBean smb);
	}

	private ReceiveDataListener receiveDataListener;

	/**
	 * 
	 * @Title: setOnReceiveDataListener
	 * @Description: 设置监听到的数据
	 * @param
	 * @return
	 * @throws
	 */
	public void setOnReceiveDataListener(ReceiveDataListener receiveDataListener) {
		this.receiveDataListener = receiveDataListener;

	}

	@Override
	public void onResume() {
		super.onResume();
		if (getActivity() != null && getActivity() instanceof ChatActivity) {
			ChatActivity chatAct = (ChatActivity) getActivity();
			mCurrentGroupId = chatAct.myCurrentGroupId;
		}
		if (isPrivateLetter == false) {
			clearMessageHistory();
			loadChatHistory();// 加载聊天记录
		}
	}
}
