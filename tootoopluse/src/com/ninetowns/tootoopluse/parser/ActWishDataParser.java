package com.ninetowns.tootoopluse.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ninetowns.library.parser.AbsParser;
import com.ninetowns.tootoopluse.bean.ActWishDataBean;
import com.ninetowns.tootoopluse.bean.ActWishDataItem;

public class ActWishDataParser extends AbsParser<ActWishDataBean> {
	
	//总记录数
	private int totalCount;
	//总共页数
	private int totalPage;

    public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public ActWishDataParser(String str) {
		super(str);
	}

	
	@Override
	public ActWishDataBean getParseResult(String netStr) {
		ActWishDataBean actWishData = new ActWishDataBean();
		try{
			JSONObject obj = new JSONObject(netStr);
			if (obj.has("Status") && obj.getString("Status").equals("1")) {
				if (obj.has("Data")) {
					JSONObject jsonData = obj.getJSONObject("Data");
					if(jsonData.has("Conversion")){
						actWishData.setData_Conversion(jsonData.getString("Conversion"));
					}
					
					if(jsonData.has("Pageviews")){
						actWishData.setData_Pageviews(jsonData.getString("Pageviews"));
					}
					
					if(jsonData.has("StartTimestamp")){
						actWishData.setData_StartTimestamp(jsonData.getString("StartTimestamp"));
					}
					
					if(jsonData.has("EndTimestamp")){
						actWishData.setData_EndTimestamp(jsonData.getString("EndTimestamp"));
					}
					if(jsonData.has("UserConversion")){
						actWishData.setUserConversion(jsonData.getString("UserConversion"));
						
					}
					if(jsonData.has("TotalCount")){
						setTotalCount(jsonData.getInt("TotalCount"));
					}
					
					if(jsonData.has("TotalPage")){
						setTotalPage(jsonData.getInt("TotalPage"));
					}
					
					if(jsonData.has("List")){
						JSONArray jsonList = jsonData.getJSONArray("List");
						List<ActWishDataItem> itemList = new ArrayList<ActWishDataItem>();
						
						for(int i = 0; i < jsonList.length(); i++){
							JSONObject jsonItem = jsonList.getJSONObject(i);
							ActWishDataItem item = new ActWishDataItem();
							if(jsonItem.has("Id")){
								item.setData_item_Id(jsonItem.getString("Id"));
							}
							
							if(jsonItem.has("CoverThumb")){
								item.setData_item_CoverThumb(jsonItem.getString("CoverThumb"));
							}
							
							if(jsonItem.has("Name")){
								item.setData_item_Name(jsonItem.getString("Name"));
							}
							
							if(jsonItem.has("Conversion")){
								item.setData_item_Conversion(jsonItem.getString("Conversion"));
							}
							
							if(jsonItem.has("Pageviews")){
								item.setData_item_Pageviews(jsonItem.getString("Pageviews"));
							}
							if(jsonItem.has("UserConversion")){
								item.setUserConversion(jsonItem.getString("UserConversion"));
								
							}
							
							itemList.add(item);
						}
						
						actWishData.setActWishList(itemList);
						
					}
				}
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		
		
		return actWishData;
	}


}
