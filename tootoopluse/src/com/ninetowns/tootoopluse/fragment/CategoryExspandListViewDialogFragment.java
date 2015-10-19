package com.ninetowns.tootoopluse.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ninetowns.library.net.RequestParamsNet;
import com.ninetowns.library.util.ComponentUtil;
import com.ninetowns.library.util.NetworkUtil;
import com.ninetowns.tootoopluse.R;
import com.ninetowns.tootoopluse.adapter.CatagoryGoodsAdapter;
import com.ninetowns.tootoopluse.application.TootooPlusEApplication;
import com.ninetowns.tootoopluse.bean.GoodsScreenMainBean;
import com.ninetowns.tootoopluse.bean.GoodsScreenSubBean;
import com.ninetowns.tootoopluse.helper.TootooeNetApiUrlHelper;
import com.ninetowns.tootoopluse.util.CommonUtil;
import com.ninetowns.ui.widget.dialog.BaseFragmentDialog;
import com.ninetowns.ui.widget.expandableview.EdgeEffectExpandableListView;
/**
 * 
 * @ClassName: CategoryExspandListViewDialogFragment
 * @Description: 可扩展的分类id
 * @author wuyulong
 * @date 2015-2-4 上午11:00:22
 * 
 */
public class CategoryExspandListViewDialogFragment extends BaseFragmentDialog implements View.OnClickListener,OnChildClickListener {
	@ViewInject(R.id.expandablelistview)
	private EdgeEffectExpandableListView mExpandableListView;
	private View mCategoryExpandView;
	@ViewInject(R.id.iv_back)
	private ImageView mIVback;//返回
	private List<GoodsScreenMainBean> listGoodsScreenMain = new ArrayList<GoodsScreenMainBean>();// 存储主分类列表
	private List<GoodsScreenSubBean> listGoodsScreenSub = new ArrayList<GoodsScreenSubBean>();
	private List<List<GoodsScreenSubBean>> childArrayList=new ArrayList<List<GoodsScreenSubBean>>();
	private CatagoryGoodsAdapter categoryGoodsAdapter;
	private List<GoodsScreenSubBean> childerData;
	private GoodsScreenSubBean childrenBean;
	private GoodsScreenMainBean groupBean;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mCategoryExpandView=inflater.inflate(R.layout.category_exspand_view, null);
		ViewUtils.inject(this,mCategoryExpandView);
		mIVback.setOnClickListener(this);
		mExpandableListView.setOnChildClickListener(this);
		return mCategoryExpandView;
	}
	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		setCateData();
	}
	@Override
	public void onClick(View v) {
		dismiss();
	}
	/**
	 * 
	 * @Title: getChildrenData
	 * @Description: 获取子分类的数据
	 * @param
	 * @return
	 * @throws
	 */
	private List<GoodsScreenSubBean> getChildrenData() {
		if (listGoodsScreenMain != null)
			for (GoodsScreenMainBean iterable_element : listGoodsScreenMain) {
				getChildrenCateGoryData(iterable_element);

			}
		return listGoodsScreenSub;
	}

	private void getChildrenCateGoryData(GoodsScreenMainBean iterable_element) {
		String strJson = iterable_element.getCategorySub();

		JSONArray jsonArr;
		try {
			jsonArr = new JSONArray(strJson);
			for (int i = 0; i < jsonArr.length(); i++) {
				GoodsScreenSubBean subBean = new GoodsScreenSubBean();
				JSONObject obj = (JSONObject) jsonArr.get(i);
				if (obj.has("CategoryId")) {
					String subCatagoryId = obj.getString("CategoryId");
					subBean.setCategoryId(subCatagoryId);
				}
				if (obj.has("CategoryName")) {
					String subCatagoryName = obj.getString("CategoryName");
					subBean.setCategoryName(subCatagoryName);
				}
				listGoodsScreenSub.add(subBean);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	/*********分类数据**********/
	public void setCateData(){
		if ((NetworkUtil.isNetworkAvaliable(TootooPlusEApplication
				.getAppContext()))) {
			// 显示进度
			showProgressDialog(getActivity());
			RequestParamsNet requestParamsNet = new RequestParamsNet();
			CommonUtil.xUtilsGetSend(TootooeNetApiUrlHelper.GOODS_CATEGORY,
					requestParamsNet, new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							closeProgressDialog(getActivity());
							String strObj = responseInfo.result;
							try {
								JSONObject jsonObject = new JSONObject(strObj);
								if (jsonObject.has("Status")) {
									String strStatus = jsonObject
											.getString("Status");
									if (strStatus.equals("1")) {
									} else {
										return;
									}

								}
								if (jsonObject.has("Data")) {
									String strArr = jsonObject
											.getString("Data");
									JSONObject jsonObjectMain = new JSONObject(
											strArr);
									if (jsonObjectMain.has("CategoryMain")) {
										String strJsonObjectMain = jsonObjectMain
												.getString("CategoryMain");
										JSONArray jsonArray = new JSONArray(
												strJsonObjectMain);
										for (int i = 0; i < jsonArray.length(); i++) {
											JSONObject jsonObjectCategoryMain = (JSONObject) jsonArray
													.get(i);
											String cateGoryId = jsonObjectCategoryMain
													.getString("CategoryId");
											String categoryName = jsonObjectCategoryMain
													.getString("CategoryName");
											String categorySub = jsonObjectCategoryMain
													.getString("CategorySub");
											String categoryImageUrl=jsonObjectCategoryMain.getString("ImgUrl");
											GoodsScreenMainBean goodsScreenMainBean = new GoodsScreenMainBean();
											goodsScreenMainBean
													.setCategoryId(cateGoryId);
											goodsScreenMainBean
													.setCategoryName(categoryName);
											goodsScreenMainBean
													.setCategorySub(categorySub);
											goodsScreenMainBean.setImgUrl(categoryImageUrl);
											listGoodsScreenMain
													.add(goodsScreenMainBean);

										}
										initExpandAdapter();
									}

								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							closeProgressDialog(getActivity());
							ComponentUtil.showToast(
									TootooPlusEApplication.getAppContext(),
									getResources()
											.getString(
													R.string.errcode_network_response_timeout));
						}
					});

		} else {
			ComponentUtil.showToast(
					TootooPlusEApplication.getAppContext(),
					this.getResources().getString(
							R.string.errcode_network_response_timeout));
		}

	}
	/**
	 * 
	* @Title: initExpandAdapter 
	* @Description: 初始化可扩展的adapter
	* @param  
	* @return   
	* @throws
	 */
	protected void initExpandAdapter() {
	 childerData = getChildrenData();
		
		if (childerData != null) {
			  categoryGoodsAdapter=new CatagoryGoodsAdapter(listGoodsScreenMain, childerData);
		}
		if(categoryGoodsAdapter!=null){
			mExpandableListView.setAdapter(categoryGoodsAdapter);
			categoryGoodsAdapter.notifyDataSetChanged();
		}
	}
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		if(childerData!=null){
			childrenBean=childerData.get(childPosition);
		}
		if(listGoodsScreenMain!=null){
			groupBean=listGoodsScreenMain.get(groupPosition);	
		}
		if(groupListener!=null){
			groupListener.onGroupListener(childrenBean, groupBean);
		}
		dismiss();
		return true;
	}
	/**
	 * 
	* @ClassName: OnGroupDataListener 
	* @Description: 监听选择哪个数据
	* @author wuyulong
	* @date 2015-2-4 下午3:37:07 
	*
	 */
	public interface OnGroupDataListener{
		public void onGroupListener(GoodsScreenSubBean goodsScreenSub,GoodsScreenMainBean goosMainBean);
		
	}
	/********注册监听器********/
	public void setOnGroupDataListener(OnGroupDataListener groupListener){
		this.groupListener=groupListener;
		
	}
	private OnGroupDataListener groupListener;
	}

