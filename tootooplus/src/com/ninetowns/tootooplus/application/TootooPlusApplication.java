package com.ninetowns.tootooplus.application;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.baidu.mapapi.SDKInitializer;
import com.lidroid.xutils.util.LogUtils;
import com.mechat.mechatlibrary.MCClient;
import com.mechat.mechatlibrary.callback.OnInitCallback;
import com.ninetowns.tootooplus.helper.ErrorHandlerHelper;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class TootooPlusApplication extends Application implements
		ErrorHandlerHelper.OnListenerError {
	private static TootooPlusApplication instance;
	private ErrorHandlerHelper errorHandlerHelper;
	public static String type = "";
	public static boolean ISDES=true;
	public static String shareStoryId = "";
    public static String shareSource = "";

	/*
	 * static { Thread.setDefaultUncaughtExceptionHandler(new
	 * Thread.UncaughtExceptionHandler() {
	 * 
	 * @Override public void uncaughtException(Thread thread, Throwable ex) {
	 * ex.printStackTrace(); } }); }
	 */
	@Override
	public void onCreate() {
		// 设置开启日志,发布时请关闭日志
		JPushInterface.setDebugMode(false);
		// 初始化 JPush
        JPushInterface.init(this);
        logManager(false);
//		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
//		errorHandlerHelper = ErrorHandlerHelper.getInstance();
//		errorHandlerHelper.init(this);
//		errorHandlerHelper.setOnListenerError(this);
		SDKInitializer.initialize(this);
		initImageLoader(this);
		instance = this;
		initMeiQiaSdk();
	}

	/** 
	* @Title: logManager 
	* @Description: TODO
	* @param  
	* @return   
	* @throws 
	*/
	private void logManager(boolean allow) {
		LogUtils.allowD=allow;
        LogUtils.allowI=allow;
        LogUtils.allowV=allow;
        LogUtils.allowE=allow;
	}

	/**
	 * @Title: initMeiQiaSdk
	 * @Description: 初始化美恰sdk
	 * @param
	 * @return
	 * @throws
	 */
	private void initMeiQiaSdk() {
		MCClient.init(this, "55346acb4eae35570400000e", new OnInitCallback() {

			@Override
			public void onSuccess(String response) {
				// Toast.makeText(getApplicationContext(), "init MCSDK success",
				// Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailed(String response) {
				Toast.makeText(getApplicationContext(),
						"init MCSDK failed " + response, Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	public static TootooPlusApplication getAppContext() {
		return instance;
	}

	public static File cacheDir;

	public static void initImageLoader(Context context) {
		// cacheDir = ImageUtil.getImageLoaderCacheFile();
		cacheDir = StorageUtils.getOwnCacheDirectory(context,
				"TooToo/eee/cache");// 创建文件目录
		int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);// 允许内存的八分之一

		MemoryCache memoryCache = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {// 在9之前
			memoryCache = new LruMemoryCache(memoryCacheSize);
		} else {
			memoryCache = new LRULimitedMemoryCache(memoryCacheSize);
		}

		// ImageLoaderConfiguration config = config(context);
		ImageLoaderConfiguration config = null;
		try {
			config = new ImageLoaderConfiguration.Builder(context)
					.threadPriority(Thread.NORM_PRIORITY - 2)
					.memoryCache(memoryCache)
					.denyCacheImageMultipleSizesInMemory()
					.diskCache(
							new LruDiscCache(cacheDir,
									new Md5FileNameGenerator(),
									10 * 1024 * 1024))
					// 文件用md5加密
					.imageDownloader(new BaseImageDownloader(context))
					.tasksProcessingOrder(QueueProcessingType.LIFO)
					.writeDebugLogs()
					// .enableLogging() // Not necessary in common
					.build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ImageLoader.getInstance().init(config);
	}

	/**
	 * 
	 * @Title: TootooPlusApplication.java
	 * @Description: 暂时不用
	 * @author wuyulong
	 * @date 2014-11-26 下午4:54:23
	 * @param
	 * @return ImageLoaderConfiguration
	 */
	private static ImageLoaderConfiguration config(Context context) {
		LimitedAgeDiscCache discCache = new LimitedAgeDiscCache(cacheDir,
				15 * 60);
		DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc().build();
		// 一些相关的设置
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY - 1)
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new LruMemoryCache(3 * 1024 * 1024))
				.memoryCacheSize(3 * 1024 * 1024)
				.diskCache(discCache)
				// .dissCache( new FIFOLimitedMemoryCache(20 * 1024 * 1024))
				// default
				.discCacheSize(100 * 1024 * 1024).discCacheFileCount(1000)
				.discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
				.imageDownloader(new BaseImageDownloader(context)) // default
				.imageDecoder(new BaseImageDecoder(true)) // default
				.writeDebugLogs().build();
		return config;
	}

	@Override
	public void onLowMemory() {
		System.gc();
		super.onLowMemory();

	}

	@SuppressLint("NewApi")
	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		System.gc();
	}

	// public static boolean isClearCache;

	@Override
	public void onListenerError(Throwable ex) {/*
												 * View layout =
												 * LayoutInflater.from
												 * (this).inflate
												 * (R.layout.error_layout,null);
												 * TextView textViewError=
												 * (TextView)
												 * layout.findViewById
												 * (R.id.tv_error);
												 * textViewError
												 * .setText(ex.getMessage());
												 * new
												 * AlertDialog.Builder(this).
												 * setTitle
												 * ("错误信息").setCancelable(false)
												 * .setPositiveButton("确定", new
												 * DialogInterface
												 * .OnClickListener() {
												 * 
												 * public void
												 * onClick(DialogInterface
												 * dialog, int which) {
												 * 
												 * } })
												 * .setNegativeButton("取消",null
												 * ).show();
												 */
	}

}
