package com.ninetowns.library.helper;

import android.os.Bundle;

public class ConstantsHelper {

	// 分页大小
	public static final int PAGESIZE = 20;
	public static final int PAGESIZE_DRAFT = 6;
	public static final int PAGESIZE_DRAFT_PHOTO = 8;
	public static int CURRENTPAGE = 1;
	public static final int UPLOAD_VIDEO = 1;
	public static final String BUNDLE = "bundle";
	public static final String RTMP_HTTP_SHARE_PREFS = "too_rtmp_http";
	public static final String isEditextView = "isEditextView";
	public static final String isCreateView = "isCreateView";
	public static final String isDraftView = "StringisDraftView";
	public static final String isRecommendView = "isRecommendView";
	public static final String isConvertRecommendView = "isConvertRecommendView";
	public static final String isConvertView = "isConvertView";
	public static final String BundleResopnse = "bundleResponse";
	public static final String TYPE = "Type";
	public static final String USERID = "UserId";
	public static final String STORYID = "StoryId";
	public static final String STORYNAME = "StoryName";
	public static final String ACTIVITYID = "ActivityId";
	public static final String ACTIVITY_NAME = "ActivityName";
	/************ 录制时间action ******************/
	public static final String RECTIMEOUT = "rectimeoutbroadcast";
	public static final String MAX_UPLOAD_PHOTO = "5";
	public static final String MAX_UPLOAD_PHOTO_ONE = "1";
	public static final String CREATE_STORY_TYPE_ACTIVITY_ACTION = "create_type_action";
	/******** 采集图片返回数据的实体类的key值 ************/
	public static final String GATHER_IMAGE_BEAN_KEY = "gather_image";
	  /**
     * 您的ak 1GRG3IEKGOuSbNaYALC6NS6d
     */
    public static final String AK = "1GRG3IEKGOuSbNaYALC6NS6d";
    /**
     * 您的sk的前16个字节或者全部 2dDfd4IPxl1K8ZvbF4Geq90p1lRy0uFb
     */
    public static final String SK = "2dDfd4IPxl1K8ZvbF4Geq90p1lRy0uFb";
    public static final String VIDEO_URL="videourl";
//	"ContactId": "7",
//	"Name": "??",
//	"Phone": "13681085987",
//	"UserId": "1",
//	"CreateDate": "2015-06-11 15:01:00"
    public static final String CONTACTID="ContactId";
    public static final String RealName="RealName";
    public static final String PhoneNumber="PhoneNumber";
    public static final String POSITION_KEY="position_key";
    public static final int NO_ITEM_CLICK_INSERT_CODE=-1;
    
	/**
	 * 
	 * @Title: ConstantsCell.java
	 * @Description: 设置跳转到哪个界面
	 * @author wuyulong
	 * @date 2014-12-15 下午1:11:08
	 * @param
	 * @return void
	 */
	public static final void putView(String stview, Bundle bundle) {
		if (stview.equals(isCreateView)) {
			bundle.putBoolean(isCreateView, true);
			bundle.putBoolean(isEditextView, false);
			bundle.putBoolean(isDraftView, false);
			bundle.putBoolean(isRecommendView, false);
			bundle.putBoolean(isConvertView, false);
			bundle.putBoolean(isConvertRecommendView, false);
		} else if (stview.equals(isEditextView)) {
			bundle.putBoolean(isCreateView, false);
			bundle.putBoolean(isEditextView, true);
			bundle.putBoolean(isDraftView, false);
			bundle.putBoolean(isRecommendView, false);
			bundle.putBoolean(isConvertView, false);
			bundle.putBoolean(isConvertRecommendView, false);

		} else if (stview.equals(isDraftView)) {
			bundle.putBoolean(isCreateView, false);
			bundle.putBoolean(isEditextView, false);
			bundle.putBoolean(isDraftView, true);
			bundle.putBoolean(isRecommendView, false);
			bundle.putBoolean(isConvertView, false);
			bundle.putBoolean(isConvertRecommendView, false);

		} else if (stview.equals(isRecommendView)) {
			bundle.putBoolean(isCreateView, false);
			bundle.putBoolean(isEditextView, false);
			bundle.putBoolean(isDraftView, false);
			bundle.putBoolean(isRecommendView, true);
			bundle.putBoolean(isConvertView, false);
			bundle.putBoolean(isConvertRecommendView, false);

		} else if (stview.equals(isConvertView)) {
			bundle.putBoolean(isCreateView, false);
			bundle.putBoolean(isEditextView, false);
			bundle.putBoolean(isDraftView, false);
			bundle.putBoolean(isRecommendView, false);
			bundle.putBoolean(isConvertView, true);
			bundle.putBoolean(isConvertRecommendView, false);

		} else if (stview.equals(isConvertRecommendView)) {
			bundle.putBoolean(isCreateView, false);
			bundle.putBoolean(isEditextView, false);
			bundle.putBoolean(isDraftView, false);
			bundle.putBoolean(isRecommendView, false);
			bundle.putBoolean(isConvertView, false);
			bundle.putBoolean(isConvertRecommendView, true);

		}

	}

	/***** 文件上传 *******/
	public static String UPLOAD_RECDOMMMEND_FIRLE = "FileUpload/FileUploadScenario.htm?";

	public static final String UPLOAD_FIRLE_TYPE = "Type";
	public static final String UPLOAD_FIRLE_TYPE_PHOTO = "1";
	public static final String UPLOAD_FIRLE_TYPE_AIDEO = "2";
	public static final String UPLOAD_FIRLE_TYPE_VIDEO = "3";
	public static final String UPLOAD_FIRLE_TYPE_PHOTOlIST = "4";
	public static final String UPLOAD_FIRLE_WIDTH = "Width";
	public static final String UPLOAD_FIRLE_HEIGHT = "Height";
	public static final String UPLOAD_FIRLE_FLAG = "Flag";
	public static final String UPLOAD_FIRLE_FILE1 = "File1";
	public static final String UPLOAD_FIRLE_FILE2 = "File2";
	public static final String UPLOAD_FIRLE_FLAG_DEBUG = "1";// 是调试
	public static final String UPLOAD_FIRLE_FLAG_NO_DEBUG = "0";// 不调试
	public static final String ScenarioType = "ScenarioType";
	public static final String APPLICATIONID = "ApplicationId";
	public static final String ElementType = "ElementType";
	public static final String APPLICATION_KEY_PARAMER = "6920961";
	public static final String APPLICATION_KEY = "ApplicationKey";
	public static final String APPLICATIONID_PARAM = "5";
	public static final int TAB_ONE=0;
	public static final int TAB_TWO=1;
	public static final int TAB_THREE=2;
	public static final int TAB_FOUR=3;
	public static final int TAB_FIVE=4;
	public static final String CATEGROY_TYPE="1";//分类发布的时候    携带的参数
	public static final String SHANGJIA="100";
	/**
	 * 置顶系数，从0（普通）开始,大于0为置顶new
	 */
	
	public static final int TopCoefficient=0;//
	
	public static final String OPEN_COMMENT_LIST="open_comment_list";
	public static final int FIVE_HANDRED=500;
	public static final int TWO_HANDRED=200;
	
	
	    
}
