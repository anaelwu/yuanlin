package com.example.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ninetowns.tootooplus.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootooplus.util.CommonUtil;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback,
		Camera.PreviewCallback {
	// public String mp4Path=CommonUtil.getVideoPath()+ File.separator +
	// CommonUtil.getCurrentDate()+".mp4";
	public String mp4Path = CommonUtil.getDCIMurl() + File.separator
			+ CommonUtil.getCurrentDateVideo() + ".mp4";
	// public String mp4Path="/storage/sdcard0/DCIM/Camera/sfsf.mp4";
	private static final String TAG = "CameraView";
	public Camera mCamera = null;
	public boolean isFailed;

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	private boolean bIfPreview = false;
	private Activity activity;
	public String publishUrl;
	boolean streamStarted = false;
	Camera.Size realsize;
	int realfmt;
	int realfps;
	int cameraCount = 0;
	int cameraPosition = 1;// 0代表前置摄像头，1代表后置摄像头
	public PcmRecorder pcmSource = null;
	private  Context context;
	 MyBroadCastReceiver myBroadCast;

	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initSurface();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("changecamera");
		myBroadCast = new MyBroadCastReceiver();
		context.registerReceiver(myBroadCast, intentFilter);
	}

	public  void connectionLostNotify() {
		Log.e("ABCDEFG", "catch connection lost notify");
		Intent intent = new Intent();
		intent.setAction(TootooeNetApiUrlHelper.RECTIMEOUT);
		context.sendBroadcast(intent);
	}

	public  void unRegisterReceiver(Context context) {
		if(null!=myBroadCast){
			context.unregisterReceiver(myBroadCast);
			myBroadCast=null;
		}
		
	}

	public class MyBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			String action = intent.getAction();
			if ("changecamera".equals(action)) {
				cameraPosition = intent.getIntExtra("cameraPosition", 1);
				cameraCount = Camera.getNumberOfCameras();
				if (cameraCount > 1) {
					CameraInfo cameraInfo = new CameraInfo();
					for (int i = 0; i < cameraCount; i++) {

						Camera.getCameraInfo(i, cameraInfo);// 得到每一个摄像头的信息

						if (cameraPosition == 1) {

							// 现在是后置，变更为前置
							if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {// 代表摄像头的方位，CAMERA_FACING_FRONT前置
								if (pcmSource != null && pcmSource.isRecording) {
									openVideoEncoder(realsize.width,
											realsize.height, true, 90, realfps,
											1, 100, 17);// 后置
								}

								changeCamera(i);

								cameraPosition = 0;
								break;
							}
						} else {

							// 现在是前置， 变更为后置
							if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {// 代表摄像头的方位，CAMERA_FACING_FRONT前置
								if (pcmSource != null && pcmSource.isRecording) {
									openVideoEncoder(realsize.width,
											realsize.height, true, 270,
											realfps, 1, 100, 17);//
								}
								changeCamera(i);
								cameraPosition = 1;
								break;
							}
						}

					}
				}
			}
		}
	}

	private void changeCamera(int i) {
		if(mCamera!=null){
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();// 停掉原来摄像头的预览
			mCamera.release();// 释放资源
			mCamera = null;// 取消原来摄像头
		}
		

		try {
			mCamera = Camera.open(i);// 打开当前选中的摄像头
			deal();
			mCamera.setPreviewDisplay(holder);// 通过surfaceview显示取景画面
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
		}
		if (mCamera != null)
			mCamera.startPreview();// 开始预览
	}

	SurfaceHolder holder;

	@SuppressWarnings("deprecation")
	private void initSurface() {
		holder = getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (null != mCamera) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		try {
			if (cameraPosition == 1) {
				mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
			}

			else {
				mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);// 打开摄像头

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (mCamera != null) {
				mCamera.setPreviewDisplay(holder);
			}

		} catch (Exception ex) {
			if (null != mCamera) {
				mCamera.release();
				mCamera = null;
			}
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// setCameraDisplayOrientation(getActivity(), 0, mCamera);

		initCamera(w, h);
		if (null != mCamera)
			setCameraDisplayOrientation(getActivity(), 0, mCamera);
		// setCameraDisplayOrientation(getActivity(), 0, mCamera);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		deinitCamera();
	}

	public boolean startPublish() throws Exception {
		streamStarted = init();
		if (streamStarted == false) {
			return false;
		}
		if (!openPublisher(publishUrl)) {
			// Cannot start publish
			// return false;
		}

		openMp4Writer(mp4Path);
		if (cameraPosition == 1) {
			openVideoEncoder(realsize.width, realsize.height, true, 270,
					realfps, 1, 100, 17);// 后置
		} else {
			openVideoEncoder(realsize.width, realsize.height, true, 90,
					realfps, 1, 100, 17);// 前置
		}

		openAudioEncoder(2, 2, 44100, 64);

		pcmSource = new PcmRecorder();
		Thread audioProvider = new Thread(pcmSource);
		audioProvider.start();

		return true;
	}

	public boolean stopPublish() {
		if (streamStarted = true) {
			streamStarted = false;
			if (pcmSource != null && pcmSource.isRecording) {
				pcmSource.stopRecording();
				closePublisher();
			}

			return true;

		}
		return false;
	}

	FileOutputStream fout;
	int mPreviewWidth;
	int mPreviewHeight;

	private void initCamera(int width, int height) {
		if (bIfPreview) {
			if (mCamera != null)
				mCamera.stopPreview();
		}
		if (null != mCamera) {
			deal();
			// mCamera.autoFocus(null);
			mCamera.startPreview();
			bIfPreview = true;
			// verify if successfully set
			realsize = mCamera.getParameters().getPreviewSize();
			mPreviewHeight = realsize.height; //
			mPreviewWidth = realsize.width;
			realfmt = mCamera.getParameters().getPreviewFormat();
			realfps = mCamera.getParameters().getPreviewFrameRate();
			List<Size> supportedPreviewSizes = mCamera.getParameters()
					.getSupportedPreviewSizes();
			for (Size s : supportedPreviewSizes)
				Log.e("ABCDEFG", "{" + s.width + "," + s.height + "}");
		}

	}

	private Camera.Parameters deal() {
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setPreviewFormat(PixelFormat.YCbCr_420_SP);// default
		parameters.setPreviewFrameRate(15);

		// =================根据手机分辨率设置4:3的预览========================
		// 优先选择640x480 的分辨率 如果不支持640x480 则从播放列表中获取一个4:3的预览尺寸
		List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
		int PreviewWidth = 0;
		int PreviewHeight = 0;
		Boolean findSize = false;
		if (sizeList.size() > 1) {
			Iterator<Camera.Size> itor = sizeList.iterator();
			while (itor.hasNext()) {
				Camera.Size cur = itor.next();
				findSize = true;
				PreviewWidth = cur.width;
				PreviewHeight = cur.height;
				if (PreviewHeight == 480
						&& PreviewHeight * 4 / 3 == PreviewWidth) {
					break;
				}
			}
			if (PreviewWidth != 640) {
				itor = sizeList.iterator();
				while (itor.hasNext()) {
					Camera.Size cur = itor.next();
					findSize = true;
					PreviewWidth = cur.width;
					PreviewHeight = cur.height;
					if (PreviewHeight * 4 / 3 == PreviewWidth) {
						break;
					}
				}
			}
		}
		if (!findSize) {
			PreviewWidth = sizeList.get(0).width;
			PreviewHeight = sizeList.get(0).height;
		}
		parameters.setPreviewSize(PreviewWidth, PreviewHeight);
		// =================根据手机分辨率设置4:3的预览========================

		mCamera.setDisplayOrientation(90);
		mCamera.setPreviewCallback(this);
		mCamera.setParameters(parameters);

		return parameters;
	}

	private void deinitCamera() {
		if (null != mCamera) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			bIfPreview = false;
			mCamera.release();
			mCamera = null;
		}

	}

	boolean first_frame = true;
	long s_ts;
	long cnt = 0;
	long avg = 0;

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		try {
			// 获取原生的YUV420SP数据
			// String msg = "<" + data.length + ">";
			// Log.e("TEA", msg);
			if (streamStarted) {

				long ts = System.currentTimeMillis();
				long delta = ts - s_ts;
				s_ts = ts;

				if (first_frame) {
					first_frame = false;
				} else {
					avg += delta;
					cnt += 1;
				}

				x264Input(data, data.length, ts);
			}
		} catch (Exception e) {
		}
	}

	// private int cdata;
	//
	// public native boolean openPublisher(String uri);
	//
	// public native boolean openVideoEncoder(int width, int height, boolean
	// square, int rotate, int fpsnum, int fpsden, int bitrate, int csp);
	//
	// public native void x264Input(byte[] in, int in_size, long ts);
	//
	// public native boolean openAudioEncoder(int channels, int samplebytes, int
	// samplerate, int bitrate);
	//
	// public native void faacInput(short[] in, int in_size, long ts);
	//
	// public native void closePublisher();

	static {
		// The runtime will add "lib" on the front and ".so" on the end of
		// the name supplied to loadLibrary.
		System.loadLibrary("encoder");
	}

	long ts_last;

	public class PcmRecorder implements Runnable {
		public volatile boolean isRecording;

		public PcmRecorder() {
			super();
			isRecording = false;
		}

		public void stopRecording() {
			isRecording = false;
		}

		@Override
		public void run() {
			long ts = 0;

			android.os.Process
					.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

			int bufferRead = 0;
			int bufferSize = AudioRecord.getMinBufferSize(44100,
					AudioFormat.CHANNEL_IN_STEREO,
					AudioFormat.ENCODING_PCM_16BIT);
			Log.d("cameraview", "bufferSize" + bufferSize);
			// bufferSize is in bytes, it's 6144 i've tested.
			bufferSize = bufferSize > 2048 ? bufferSize : 2048;
			bufferSize *= 2;// daniel make twice bufferSize

			short[] tempBuffer = new short[bufferSize];
			AudioRecord recordInstance = new AudioRecord(
					MediaRecorder.AudioSource.MIC, 44100,
					AudioFormat.CHANNEL_IN_STEREO,
					AudioFormat.ENCODING_PCM_16BIT, bufferSize);

			recordInstance.startRecording();
			isRecording = true;
			while (this.isRecording) {
				bufferRead = recordInstance.read(tempBuffer, 0, 2048);
				if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
					throw new IllegalStateException(
							"read() returned AudioRecord.ERROR_INVALID_OPERATION");
				} else if (bufferRead == AudioRecord.ERROR_BAD_VALUE) {
					throw new IllegalStateException(
							"read() returned AudioRecord.ERROR_BAD_VALUE");
				} else if (bufferRead == AudioRecord.ERROR_INVALID_OPERATION) {
					throw new IllegalStateException(
							"read() returned AudioRecord.ERROR_INVALID_OPERATION");
				}

				// ts += 1000*1024/44100;
				ts = System.currentTimeMillis();
				faacInput(tempBuffer, bufferRead, ts);

				/*
				 * //as per zhangjianhong need different ts in audio try {
				 * Thread.sleep(1); } catch (InterruptedException e) { // TODO
				 * Auto-generated catch block e.printStackTrace(); }
				 */
			}
			recordInstance.stop();
			Log.d("cameraview", "audio thread exits");
		}
	}

	/**
	 * 
	 * @Title: CameraView.java
	 * @Description: 解决自拍倒立方案
	 * @author wuyulong
	 * @date 2014-9-10 下午2:06:50
	 * @param
	 * @return void
	 */
	public static void setCameraDisplayOrientation(Activity activity,
			int cameraId, android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		if (null != camera)
			camera.setDisplayOrientation(result);
	}

	private int cdata;

	public native boolean init();

	public native boolean openPublisher(String uri);

	public native boolean openVideoEncoder(int width, int height,
			boolean square, int rotate, int fpsnum, int fpsden, int bitrate,
			int csp);// 8

	public native void x264Input(byte[] in, int in_size, long ts);

	public native boolean openAudioEncoder(int channels, int samplebytes,
			int samplerate, int bitrate);

	public native void faacInput(short[] in, int in_size, long ts);

	public native boolean openMp4Writer(String path);

	public native void closePublisher();
}
