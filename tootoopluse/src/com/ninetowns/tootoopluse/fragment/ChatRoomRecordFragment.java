package com.ninetowns.tootoopluse.fragment;

import android.app.Activity;
import android.content.Intent;

import com.lidroid.xutils.util.LogUtils;
import com.ninetowns.tootoopluse.util.INetConstanst;

/** 
* @ClassName: ChatRoomRecordFragment 
* @Description: 录制视频页面
* @author zhou
* @date 2015-2-7 下午5:09:33 
*  
*/
public class ChatRoomRecordFragment  extends VideoStoryCreateFragment implements INetConstanst{

	
	@Override
	public void skipToUpload(String mp4url) {
//		Bundle bundle=new Bundle();
//		bundle.putInt("requestCode", REQUESTCODE_RECORD_VIDEO);
//		bundle.putString("videourl",mp4url );
//		ReceiverManager.getIntance().sendBroadCastReceiver(getActivity(), bundle, FINISH_RECORDVIDEO_ACTION);
//		intgetActivity().getIntent().putExtra("videourl", mp4url);
	
		Intent intent=new Intent();
		intent.putExtra("videourl", mp4url);
		getActivity().setResult(Activity.RESULT_OK, intent);
		LogUtils.i("ChatRoomRecordFragment");
		getActivity().finish();
	}
	
	
	
}
