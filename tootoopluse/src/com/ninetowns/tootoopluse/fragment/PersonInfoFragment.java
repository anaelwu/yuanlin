package com.ninetowns.tootoopluse.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.library.util.ImageUtil;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.activity.ChangeNickActivity;
import com.ninetowns.tootoopluse.activity.FollowFansActivity;
import com.ninetowns.tootoopluse.bean.PersonInfoBean;
import com.ninetowns.tootoopluse.bean.UpLoadFileBean;
import com.ninetowns.tootoopluse.helper.ImageHttpMultipartPostHelper;
import com.ninetowns.tootoopluse.helper.SharedPreferenceHelper;
import com.ninetowns.tootoopluse.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootoopluse.helper.UploadPicPopup;
import com.ninetowns.tootoopluse.helper.UploadPicPopup.CameraAndLocalListener;
import com.ninetowns.tootoopluse.parser.PersonInfoParser;
import com.ninetowns.tootoopluse.util.AlbumUtil;
import com.ninetowns.tootoopluse.util.CommonUtil;
import com.ninetowns.ui.fragment.BaseFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class PersonInfoFragment extends BaseFragment<PersonInfoBean, PersonInfoParser> {
	
	private PersonInfoBean perInfoBean;
	
	private ImageView per_info_head_cover_iv;
	
	/**头像**/
	private ImageView per_home_head_photo;
	/**vip**/
	private ImageView per_home_head_vip;
	/**用户名**/
	private TextView per_home_head_name;
	/**关注数**/
	private TextView per_home_head_follow_count;
	/**粉丝数**/
	private TextView per_home_head_fans_count;
	
	/**标志是企业认证**/
	private LinearLayout per_info_shop_layout;
	
	/**营业执照**/
	private ImageView person_info_business_iv;
	/**食物流通许可证**/
	private ImageView per_info_food_flow_iv;
	/**食品生产许可证**/
	private ImageView per_info_food_product_iv;
	/**食品卫生许可证**/
	private ImageView per_info_food_hygiene_iv;
	
	private ImageView per_IDCard_front_iv;
	
	private ImageView per_IDCard_reverse_iv;
	
	private ImageView finalImageView;
	
	private String finalPicPath = "";
	
	private DisplayImageOptions finalDisplayImageOptions = null;
	
	//判断是本地图片还是相机
	private String uploadType = "";
	
	private Map<Integer, UpLoadFileBean> per_upMap = new HashMap<Integer, UpLoadFileBean>();
	
	private Map<Integer, UpLoadFileBean> shop_upMap = new HashMap<Integer, UpLoadFileBean>();
	/**修改昵称**/
	private ImageView per_info_change_nick;
	
	/**证件审核状态**/
	private ImageView person_info_business_audit_status, per_info_food_flow_audit_status, per_info_food_product_audit_status,
	per_info_food_hygiene_audit_status, per_info_IDCard_front_audit_status, per_IDCard_reverse_audit_status;
	
	//手机屏幕宽度
	private int screen_width = 0;
	
	public Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				List<UpLoadFileBean> list = (List<UpLoadFileBean>) msg.obj;
				if(msg.arg1 == 6){
					//修改头像处理
					changeUserLogo(list.get(0).getDragSquareImg());
					
				} else if(msg.arg1 == 7){
					//修改封面图处理
					changeCoverImage(list.get(0).getListCoverImg());
				} else {
					if(per_info_shop_layout.getVisibility() == View.GONE){
						per_upMap.put(msg.arg1, list.get(0));
					} else {
						shop_upMap.put(msg.arg1, list.get(0));
					}
				}
				
				
				break;
			}
		};
		
	};
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onLoadData(true, false, false);
        super.onActivityCreated(savedInstanceState);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        
    	View view = inflater.inflate(R.layout.person_info_fragment, null);
    	WindowManager wm = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
		//屏幕宽度
		screen_width = wm.getDefaultDisplay().getWidth();
    	
    	//返回
		LinearLayout two_or_one_btn_head_back = (LinearLayout)view.findViewById(R.id.two_or_one_btn_head_back);
		two_or_one_btn_head_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().finish();
			}
		});
		
		//标题
		TextView two_or_one_btn_head_title = (TextView)view.findViewById(R.id.two_or_one_btn_head_title);
		two_or_one_btn_head_title.setText(R.string.person_info_title);
		
		RelativeLayout per_info_head_cover_layout = (RelativeLayout)view.findViewById(R.id.per_info_head_cover_layout);
		LinearLayout.LayoutParams lLayoutParams = (LinearLayout.LayoutParams)per_info_head_cover_layout.getLayoutParams();
		lLayoutParams.width = screen_width;
		lLayoutParams.height = screen_width * 2 / 3;
		per_info_head_cover_layout.setLayoutParams(lLayoutParams);
		
		per_info_head_cover_iv = (ImageView)view.findViewById(R.id.per_info_head_cover_iv);
		
		per_info_head_cover_iv.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				//修改封面图
				showUploadView(7);
				
				return false;
			}
		});
		
		
    	per_home_head_photo = (ImageView)view.findViewById(R.id.per_home_head_photo);
    	per_home_head_photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//修改头像
				showUploadView(6);
				
			}
		});
    	
    	per_home_head_vip = (ImageView)view.findViewById(R.id.per_home_head_vip);
    	
    	per_home_head_name = (TextView)view.findViewById(R.id.per_home_head_name);
    	
    	per_home_head_follow_count = (TextView)view.findViewById(R.id.per_home_head_follow_count);
    	
    	per_home_head_fans_count = (TextView)view.findViewById(R.id.per_home_head_fans_count);
    	
    	per_info_shop_layout = (LinearLayout)view.findViewById(R.id.per_info_shop_layout);
    	
    	person_info_business_iv = (ImageView)view.findViewById(R.id.person_info_business_iv);
    	TextView per_info_business_again = (TextView)view.findViewById(R.id.per_info_business_again);
    	per_info_business_again.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//重新上传营业执照
				showUploadView(0);
			}
		});
    	
    	per_info_food_flow_iv = (ImageView)view.findViewById(R.id.per_info_food_flow_iv);
    	TextView per_info_food_flow_again = (TextView)view.findViewById(R.id.per_info_food_flow_again);
    	per_info_food_flow_again.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//重新上传食品流通许可证
				showUploadView(1);
			}
		});
    	
    	
    	per_info_food_product_iv = (ImageView)view.findViewById(R.id.per_info_food_product_iv);
    	TextView per_info_product_again = (TextView)view.findViewById(R.id.per_info_product_again);
    	per_info_product_again.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//重新上传食品生产许可证
				showUploadView(2);
			}
		});
    	
    	per_info_food_hygiene_iv = (ImageView)view.findViewById(R.id.per_info_food_hygiene_iv);
    	TextView per_info_food_hygiene_again = (TextView)view.findViewById(R.id.per_info_food_hygiene_again);
    	per_info_food_hygiene_again.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//重新上传食品卫生许可证
				showUploadView(3);
			}
		});
    	
    	
    	per_IDCard_front_iv = (ImageView)view.findViewById(R.id.per_IDCard_front_iv);
    	TextView per_info_ID_front_again = (TextView)view.findViewById(R.id.per_info_ID_front_again);
    	per_info_ID_front_again.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//重新上传身份证正面
				showUploadView(4);
			}
		});
    	
    	per_IDCard_reverse_iv = (ImageView)view.findViewById(R.id.per_IDCard_reverse_iv);
    	TextView per_info_ID_reverse_again = (TextView)view.findViewById(R.id.per_info_ID_reverse_again);
    	per_info_ID_reverse_again.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//重新上传身份证反面
				showUploadView(5);
				
			}
		});
    	
    	TextView per_info_all_submit_ver = (TextView)view.findViewById(R.id.per_info_all_submit_ver);
    	per_info_all_submit_ver.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//提交审核
				if(per_info_shop_layout.getVisibility() == View.GONE){

					RequestParamsNet requestParamsNet = new RequestParamsNet();
					requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.SHOP_CERT_USERID, SharedPreferenceHelper.getLoginUserId(getActivity()));
					//0表示个人，1表示企业
					requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.SHOP_CERT_VERIFY_TYPE, "0");
					
					for(int key: per_upMap.keySet()){
						if(key == 4){
							requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.SHOP_CERT_IDCARD_FRONT, per_upMap.get(key).getThumbFileUrl());
						}else if(key == 5){
							requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.SHOP_CERT_IDCARD_REVERSE, per_upMap.get(key).getThumbFileUrl());
						}
						
					}
					
					CommonUtil.xUtilsGetSend(TootooeNetApiUrlHelper.SHOP_CERT_URL, requestParamsNet, new RequestCallBack<String>() {
								@Override
								public void onSuccess(ResponseInfo<String> responseInfo) {
									// TODO Auto-generated method stub
									String verifyStr = new String(responseInfo.result);
									try {
										JSONObject verifyJson = new JSONObject(verifyStr);
										if(verifyJson.has("Status")){
											String status = verifyJson.getString("Status");
											if(status.equals("1")){
												//认证成功
												
											} else {
												ComponentUtil.showToast(getActivity(), getResources().getString(R.string.cert_fail));
											}
											
										}
										
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									
								}

								@Override
								public void onFailure(HttpException error, String msg) {
									// TODO Auto-generated method stub
								}
							});
					
					
				
				} else {

					RequestParamsNet requestParamsNet = new RequestParamsNet();
					requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.SHOP_CERT_USERID, SharedPreferenceHelper.getLoginUserId(getActivity()));
					//0表示个人，1表示企业
					requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.SHOP_CERT_VERIFY_TYPE, "1");
					
					for(int key: shop_upMap.keySet()){
						if(key == 0){
							requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.SHOP_CERT_BUSINESS_LICENCE, shop_upMap.get(key).getThumbFileUrl());
						} else if(key == 1){
							requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.SHOP_CERT_FOOD_CIRCULATION_PERMITS, shop_upMap.get(key).getThumbFileUrl());
						} else if(key == 2){
							requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.SHOP_CERT_FOOD_PRODUCTION_LICENCE, shop_upMap.get(key).getThumbFileUrl());
						} else if(key == 3){
							requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.SHOP_CERT_FOOD_HYGIENE_LICENCE, shop_upMap.get(key).getThumbFileUrl());
						} else if(key == 4){
							requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.SHOP_CERT_IDCARD_FRONT, shop_upMap.get(key).getThumbFileUrl());
						} else if(key == 5){
							requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.SHOP_CERT_IDCARD_REVERSE, shop_upMap.get(key).getThumbFileUrl());
						}
						
					}
					
					CommonUtil.xUtilsGetSend(TootooeNetApiUrlHelper.SHOP_CERT_URL, requestParamsNet, new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							// TODO Auto-generated method stub
							String verifyStr = new String(responseInfo.result);

							try {
								JSONObject verifyJson = new JSONObject(verifyStr);
								if(verifyJson.has("Status")){
									String status = verifyJson.getString("Status");
									if(status.equals("1")){
										//认证成功
										ComponentUtil.showToast(getActivity(), getResources().getString(R.string.shop_cert_success));
										for(Integer key: shop_upMap.keySet()){
											if(key == 0){
												person_info_business_audit_status.setImageResource(R.drawable.icon_pending_audit);
											} else if(key == 1){
												per_info_food_flow_audit_status.setImageResource(R.drawable.icon_pending_audit);
											} else if(key == 2){
												per_info_food_product_audit_status.setImageResource(R.drawable.icon_pending_audit);
											} else if(key == 3){
												per_info_food_hygiene_audit_status.setImageResource(R.drawable.icon_pending_audit);
											} else if(key == 4){
												per_info_IDCard_front_audit_status.setImageResource(R.drawable.icon_pending_audit);
											} else if(key == 5){
												per_IDCard_reverse_audit_status.setImageResource(R.drawable.icon_pending_audit);
											}
										}
										
										
									} else {
										ComponentUtil.showToast(getActivity(), getResources().getString(R.string.cert_fail));
									}
									
								}
								
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							// TODO Auto-generated method stub
						}
					});
					
				
				}
				
				
			}
		});
    	
    	
    	person_info_business_audit_status = (ImageView)view.findViewById(R.id.person_info_business_audit_status);
    	
    	per_info_food_flow_audit_status = (ImageView)view.findViewById(R.id.per_info_food_flow_audit_status);
    	
    	per_info_food_product_audit_status = (ImageView)view.findViewById(R.id.per_info_food_product_audit_status);
    	
    	per_info_food_hygiene_audit_status = (ImageView)view.findViewById(R.id.per_info_food_hygiene_audit_status);
    	
    	per_info_IDCard_front_audit_status = (ImageView)view.findViewById(R.id.per_info_IDCard_front_audit_status);
    	
    	per_IDCard_reverse_audit_status = (ImageView)view.findViewById(R.id.per_IDCard_reverse_audit_status);
    	
    	
    	per_info_change_nick = (ImageView)view.findViewById(R.id.per_info_change_nick);
    	per_info_change_nick.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//修改昵称
				Intent nick_intent = new Intent(getActivity(), ChangeNickActivity.class);
				nick_intent.putExtra("nick", per_home_head_name.getText().toString());
				startActivity(nick_intent);
				
			}
		});
    	
    	return view;
	}

	@Override
	public PersonInfoParser setParser(String str) {
		// TODO Auto-generated method stub
		return new PersonInfoParser(str);
	}

	@Override
	public void getParserResult(PersonInfoBean parserResult) {
		// TODO Auto-generated method stub
		if(parserResult != null){
			perInfoBean = parserResult;
			ImageLoader.getInstance().displayImage(perInfoBean.getInfo_coverImage(), per_info_head_cover_iv, CommonUtil.MINE_COVER_OPTIONS);
			
			ImageLoader.getInstance().displayImage(perInfoBean.getInfo_logoUrl(), per_home_head_photo, CommonUtil.OPTIONS_BIG_HEADPHOTO);
			
			
			if(!TextUtils.isEmpty(perInfoBean.getInfo_userGrade())){
				CommonUtil.showVip(per_home_head_vip, perInfoBean.getInfo_userGrade());
			} else {
				per_home_head_vip.setVisibility(View.GONE);
			}
			
			if(!TextUtils.isEmpty(perInfoBean.getInfo_userName())){
				per_home_head_name.setText(perInfoBean.getInfo_userName());
    		} else {
    			per_home_head_name.setText("");
    		}
			
			if(!TextUtils.isEmpty(perInfoBean.getInfo_attend())){
				per_home_head_follow_count.setText(perInfoBean.getInfo_attend());
			} else {
				per_home_head_follow_count.setText("");
			}
			per_home_head_follow_count.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//跳到关注列表
					Intent follow_intent = new Intent(getActivity(), FollowFansActivity.class);
					follow_intent.putExtra("follow_or_fans", "follow");
					follow_intent.putExtra("other_userId", SharedPreferenceHelper.getLoginUserId(getActivity()));
					startActivity(follow_intent);
				}
			});
			
			
			if(!TextUtils.isEmpty(perInfoBean.getInfo_fans())){
				per_home_head_fans_count.setText(perInfoBean.getInfo_fans());
			} else {
				per_home_head_fans_count.setText("");
			}
			per_home_head_fans_count.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//跳到粉丝列表
					Intent follow_intent = new Intent(getActivity(), FollowFansActivity.class);
					follow_intent.putExtra("follow_or_fans", "fans");
					follow_intent.putExtra("other_userId", SharedPreferenceHelper.getLoginUserId(getActivity()));
					startActivity(follow_intent);
					
				}
			});
			
			
			if(!TextUtils.isEmpty(perInfoBean.getInfo_verifyType())){
				if(perInfoBean.getInfo_verifyType().equals("0")){
					per_info_shop_layout.setVisibility(View.GONE);
				} else if(perInfoBean.getInfo_verifyType().equals("1")){
					per_info_shop_layout.setVisibility(View.VISIBLE);
				}
			}
			ImageLoader.getInstance().displayImage(perInfoBean.getInfo_businessLicence(), person_info_business_iv, CommonUtil.OPTIONS_LIKE_LIST);
			
			ImageLoader.getInstance().displayImage(perInfoBean.getInfo_foodCirculationPermits(), per_info_food_flow_iv, CommonUtil.OPTIONS_LIKE_LIST);
			
			ImageLoader.getInstance().displayImage(perInfoBean.getInfo_foodProductionLicence(), per_info_food_product_iv, CommonUtil.OPTIONS_LIKE_LIST);
			
			ImageLoader.getInstance().displayImage(perInfoBean.getInfo_foodHygieneLicence(), per_info_food_hygiene_iv, CommonUtil.OPTIONS_LIKE_LIST);
			
			ImageLoader.getInstance().displayImage(perInfoBean.getInfo_IDCardFront(), per_IDCard_front_iv, CommonUtil.OPTIONS_LIKE_LIST);
			
			ImageLoader.getInstance().displayImage(perInfoBean.getInfo_IDCardReverse(), per_IDCard_reverse_iv, CommonUtil.OPTIONS_LIKE_LIST);
			
	    	if(!TextUtils.isEmpty(perInfoBean.getInfo_businessLicenceOk())){
	    		if("0".equals(perInfoBean.getInfo_businessLicenceOk())){
					person_info_business_audit_status.setImageResource(R.drawable.icon_pending_audit);
				} else if("-1".equals(perInfoBean.getInfo_businessLicenceOk())){
					person_info_business_audit_status.setImageResource(R.drawable.icon_no_through_audit);
				} else {
					person_info_business_audit_status.setImageResource(R.drawable.icon_audit);
				}
				
	    	} else {
	    		//如果为空的时候，就默认为待审核
	    		person_info_business_audit_status.setImageResource(R.drawable.icon_pending_audit);
	    	}
			
	    	
	    	if(!TextUtils.isEmpty(perInfoBean.getInfo_foodCirculationPermitsOk())){
	    		if("0".equals(perInfoBean.getInfo_foodCirculationPermitsOk())){
	    			per_info_food_flow_audit_status.setImageResource(R.drawable.icon_pending_audit);
	    		} else if("-1".equals(perInfoBean.getInfo_foodCirculationPermitsOk())){
	    			per_info_food_flow_audit_status.setImageResource(R.drawable.icon_no_through_audit);
	    		} else {
	    			per_info_food_flow_audit_status.setImageResource(R.drawable.icon_audit);
	    		}
	    	} else {
	    		per_info_food_flow_audit_status.setImageResource(R.drawable.icon_pending_audit);
	    	}
	    	
	    	
	    	if(!TextUtils.isEmpty(perInfoBean.getInfo_foodProductionLicenceOk())){
	    		if("0".equals(perInfoBean.getInfo_foodProductionLicenceOk())){
	    			per_info_food_product_audit_status.setImageResource(R.drawable.icon_pending_audit);
	    		} else if("-1".equals(perInfoBean.getInfo_foodProductionLicenceOk())){
	    			per_info_food_product_audit_status.setImageResource(R.drawable.icon_no_through_audit);
	    		} else {
	    			per_info_food_product_audit_status.setImageResource(R.drawable.icon_audit);
	    		}
	    	} else {
	    		per_info_food_product_audit_status.setImageResource(R.drawable.icon_pending_audit);
	    	}
	    	
	    	if(!TextUtils.isEmpty(perInfoBean.getInfo_foodHygieneLicenceOk())){
	    		if("0".equals(perInfoBean.getInfo_foodHygieneLicenceOk())){
	    			per_info_food_hygiene_audit_status.setImageResource(R.drawable.icon_pending_audit);
	    		} else if("-1".equals(perInfoBean.getInfo_foodHygieneLicenceOk())){
	    			per_info_food_hygiene_audit_status.setImageResource(R.drawable.icon_no_through_audit);
	    		} else {
	    			per_info_food_hygiene_audit_status.setImageResource(R.drawable.icon_audit);
	    		}
	    	} else {
	    		per_info_food_hygiene_audit_status.setImageResource(R.drawable.icon_pending_audit);
	    	}
	    	
	    	
	    	if(!TextUtils.isEmpty(perInfoBean.getInfo_IDCardFrontOk())){
	    		if("0".equals(perInfoBean.getInfo_IDCardFrontOk())){
	    			per_info_IDCard_front_audit_status.setImageResource(R.drawable.icon_pending_audit);
	    		} else if("-1".equals(perInfoBean.getInfo_IDCardFrontOk())){
	    			per_info_IDCard_front_audit_status.setImageResource(R.drawable.icon_no_through_audit);
	    		} else {
	    			per_info_IDCard_front_audit_status.setImageResource(R.drawable.icon_audit);
	    		}
	    	} else {
	    		per_info_IDCard_front_audit_status.setImageResource(R.drawable.icon_pending_audit);
	    	}
	    	
	    	
	    	if(!TextUtils.isEmpty(perInfoBean.getInfo_IDCardReverseOk())){
	    		if("0".equals(perInfoBean.getInfo_IDCardReverseOk())){
	    			per_IDCard_reverse_audit_status.setImageResource(R.drawable.icon_pending_audit);
	    		} else if("-1".equals(perInfoBean.getInfo_IDCardReverseOk())){
	    			per_IDCard_reverse_audit_status.setImageResource(R.drawable.icon_no_through_audit);
	    		} else {
	    			per_IDCard_reverse_audit_status.setImageResource(R.drawable.icon_audit);
	    		}
	    	} else {
	    		per_IDCard_reverse_audit_status.setImageResource(R.drawable.icon_pending_audit);
	    	}
			
		}
	}

	@Override
	public RequestParamsNet getApiParmars() {
		// TODO Auto-generated method stub
		
		RequestParamsNet requestParamsNet = new RequestParamsNet();
    	requestParamsNet.setmStrHttpApi(TootooeNetApiUrlHelper.PERSON_INFO_URL);
    	requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.PERSON_INFO_USERID, SharedPreferenceHelper.getLoginUserId(getActivity()));
		
		return requestParamsNet;
	}
	
	
	
	

	/**
	 * 显示上传底部样式
	 */
	public void showUploadView(final int requestCode){
		WindowManager wm = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
		//屏幕宽度
		int screen_width_show = wm.getDefaultDisplay().getWidth();
		//屏幕高度
		int screen_height = wm.getDefaultDisplay().getHeight();
		UploadPicPopup uploadPicPopup = new UploadPicPopup(getActivity(), screen_width_show, screen_height);
		uploadPicPopup.setCameraAndLocalListener(new CameraAndLocalListener() {
			
			@Override
			public void onLocal() {
				//本地图片
				uploadType = "local";
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
		        intent.setType("image/*");  
		        intent.putExtra("return-data", true);  
		        startActivityForResult(intent, requestCode);  
				
			}
			
			@Override
			public void onCamera() {
				//拍照
				uploadType = "camera";
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				Uri imageUri = Uri.fromFile(new File(ImageUtil.getTempPhotoPath()));
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				intent.putExtra(ImageColumns.ORIENTATION, 0);
				startActivityForResult(intent, requestCode);
				
			}
		});
		
		
	}
	
	
	@Override
	public void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		
        if(arg1 == Activity.RESULT_OK){
        	
        	if(arg2 != null && uploadType.equals("local")){
        		String picPath = AlbumUtil.getPicPath(getActivity(), arg2);
                cutImage(arg0, picPath);
        	} else {
        		cutImage(arg0, "");
        	}
        	
        	
		}
	}
	
	/**
	 * 
	 * @Title: PhotoCameraActivity.java
	 * @Description: 防止某些手机拍照倒立
	 * @author wuyulong
	 * @date 2014-12-19 下午12:40:37
	 * @param
	 * @return void
	 */
	private void cutImage(final int iv_mark, String picPath) {
		ImageLoader.getInstance().clearMemoryCache();
		ImageLoader.getInstance().clearDiskCache();
		if(iv_mark == 0){
			finalImageView = person_info_business_iv;
			finalDisplayImageOptions = CommonUtil.OPTIONS_LIKE_LIST;
		} else if(iv_mark == 1){
			finalImageView = per_info_food_flow_iv;
			finalDisplayImageOptions = CommonUtil.OPTIONS_LIKE_LIST;
		} else if(iv_mark == 2){
			finalImageView = per_info_food_product_iv;
			finalDisplayImageOptions = CommonUtil.OPTIONS_LIKE_LIST;
		} else if(iv_mark == 3){
			finalImageView = per_info_food_hygiene_iv;
			finalDisplayImageOptions = CommonUtil.OPTIONS_LIKE_LIST;
		} else if(iv_mark == 4){
			finalImageView = per_IDCard_front_iv;
			finalDisplayImageOptions = CommonUtil.OPTIONS_LIKE_LIST;
		} else if(iv_mark == 5){
			finalImageView = per_IDCard_reverse_iv;
			finalDisplayImageOptions = CommonUtil.OPTIONS_LIKE_LIST;
		} else if(iv_mark == 6){
			finalImageView = per_home_head_photo;
			finalDisplayImageOptions = CommonUtil.OPTIONS_BIG_HEADPHOTO;
		} else if(iv_mark == 7){
			finalImageView = per_info_head_cover_iv;
			finalDisplayImageOptions = CommonUtil.MINE_COVER_OPTIONS;
		}
		if(picPath.equals("")){
			finalPicPath = ImageUtil.getTempPhotoPath();
		} else {
			finalPicPath = picPath;
		}
		ImageLoader.getInstance().displayImage("file://" + finalPicPath, finalImageView, finalDisplayImageOptions,
				new SimpleImageLoadingListener() {

					private Bitmap bmp;

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap arg2) {
						int angle = ImageUtil.readPictureDegree(finalPicPath);
						if (angle == 0) {
							bmp = arg2;
						} else {
							// 下面的方法主要作用是把图片转一个角度，也可以放大缩小等
							Matrix m = new Matrix();
							int width = arg2.getWidth();
							int height = arg2.getHeight();
							m.setRotate(angle); // 旋转angle度
							bmp = Bitmap.createBitmap(arg2, 0, 0, width,
									height, m, true);// 从新生成图片
						}
						if (bmp != null) {
							upCameraPic(bmp, iv_mark);
							
						}

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
					}

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
					}
				});
	}
	
	
	/**
	 * 上传照相机拍到的照片
	 * @param iv_mark
	 */
	private void upCameraPic(Bitmap bmp, int iv_mark){
		
		final String upFileName = ImageUtil.makePhotoName(new Date());
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(upFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
		List<File> listFile = new ArrayList<File>();
		File upFile1 = new File(upFileName);
		listFile.add(upFile1);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TootooeNetApiUrlHelper.UPLOAD_FILE_TYPE, TootooeNetApiUrlHelper.UPLOAD_FIRLE_TYPE_PHOTO);
        map.put(TootooeNetApiUrlHelper.UPLOAD_FILE_APPLICATIONID, TootooeNetApiUrlHelper.APPLICATIONID_PARAM);
        String useridlocal = SharedPreferenceHelper.getLoginUserId(getActivity());
        map.put(TootooeNetApiUrlHelper.UPLOAD_FILE_USERID, useridlocal);
        
        if(iv_mark == 6){
        	map.put(TootooeNetApiUrlHelper.UPLOAD_RECDOMMMEND_FILE_SCENARIOTYPE, "3");
        } else if(iv_mark == 7){
        	map.put(TootooeNetApiUrlHelper.UPLOAD_RECDOMMMEND_FILE_SCENARIOTYPE, "1");
        }
        ImageHttpMultipartPostHelper uploadImages = null;
        if(iv_mark == 6 || iv_mark == 7){
        	uploadImages = new ImageHttpMultipartPostHelper(getActivity(),
                    CommonUtil.appInterfaceUrl(TootooeNetApiUrlHelper.UPLOAD_RECDOMMMEND_FIRLE), listFile,
                    map, handler, iv_mark);
            
        } else {
        	uploadImages = new ImageHttpMultipartPostHelper(getActivity(),
                    CommonUtil.appInterfaceUrl(TootooeNetApiUrlHelper.UPLOAD_FIRLE), listFile,
                    map, handler, iv_mark);
            
        }
        uploadImages.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

	}

	/**
	 * 修改头像
	 */
	private void changeUserLogo(final String logoUrl){
		RequestParamsNet requestParamsNet = new RequestParamsNet();
		requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.CHANGE_LOGO_USERID, SharedPreferenceHelper.getLoginUserId(getActivity()));
		requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.CHANGE_LOGO_LOGOURL, logoUrl);
		CommonUtil.xUtilsGetSend(TootooeNetApiUrlHelper.CHANGE_LOGO_URL, requestParamsNet, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				String verifyStr = new String(responseInfo.result);
				try {
					JSONObject verifyJson = new JSONObject(verifyStr);
					if(verifyJson.has("Status")){
						String status = verifyJson.getString("Status");
						if(status.equals("1")){
							
							ImageLoader.getInstance().displayImage(logoUrl, per_home_head_photo, CommonUtil.OPTIONS_HEADPHOTO);
							//修改存到本地的数据
							SharedPreferenceHelper.changeLoginLogoUrl(logoUrl, getActivity());
							
							ComponentUtil.showToast(getActivity(), getResources().getString(R.string.change_logo_success));
						} else {
							ComponentUtil.showToast(getActivity(), getResources().getString(R.string.change_logo_fail));
						}
						
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	
	
	/**
	 * 修改封面图
	 */
	private void changeCoverImage(final String coverUrl){
		RequestParamsNet requestParamsNet = new RequestParamsNet();
		requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.CHANGE_COVER_IMAGE_USERID, SharedPreferenceHelper.getLoginUserId(getActivity()));
		requestParamsNet.addQueryStringParameter(TootooeNetApiUrlHelper.CHANGE_COVER_IMAGE_COVER, coverUrl);
		CommonUtil.xUtilsGetSend(TootooeNetApiUrlHelper.CHANGE_COVER_IMAGE_URL, requestParamsNet, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				String verifyStr = new String(responseInfo.result);
				try {
					JSONObject verifyJson = new JSONObject(verifyStr);
					if(verifyJson.has("Status")){
						String status = verifyJson.getString("Status");
						if(status.equals("1")){
							ImageLoader.getInstance().displayImage(coverUrl, per_info_head_cover_iv, CommonUtil.OPTIONS_LIKE_LIST);
							SharedPreferenceHelper.changeLoginCoverUrl(coverUrl, getActivity());
							ComponentUtil.showToast(getActivity(), getResources().getString(R.string.change_cover_success));
						} else {
							ComponentUtil.showToast(getActivity(), getResources().getString(R.string.change_cover_fail));
						}
						
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(!"".equals(SharedPreferenceHelper.getLoginUserName(getActivity()))){
			per_home_head_name.setText(SharedPreferenceHelper.getLoginUserName(getActivity()));
		}
		
	}
}
