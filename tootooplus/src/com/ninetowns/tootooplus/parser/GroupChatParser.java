package com.ninetowns.tootooplus.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.util.LogUtils;
import com.ninetowns.library.parser.AbsParser;
import com.ninetowns.library.util.JsonTools;
import com.ninetowns.tootooplus.bean.GroupChatBean;
import com.ninetowns.tootooplus.bean.GroupChatList;

public class GroupChatParser  extends AbsParser<GroupChatList>{

	public GroupChatParser(String str) {
		super(str);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<GroupChatList> getParseResult(String netStr) {
		
		List<GroupChatList> groupChatLists=new ArrayList<GroupChatList>();
		try {
			JSONObject object = new JSONObject(netStr);
			
			if (object != null) {
//				if(object.has("TotalPage")){
//					totalPage=object.getInt("TotalPage");
//				}
				if (object.has("Data")) {
					JSONObject data = object.getJSONObject("Data");
					if (null != data && data.has("ActivityList")) {
						
						JSONArray array=data.getJSONArray("ActivityList");
						if(null!=array){
							for (int i = 0; i < array.length(); i++) {
								GroupChatList groupChatList= new GroupChatList();
								JSONObject obj=(JSONObject) array.get(i);
								if(null!=obj){
									groupChatList.setActivityId(obj.getString("ActivityId"));
									groupChatList.setActivityName(obj.getString("ActivityName"));
									groupChatList.setCoverThumb(obj.getString("CoverThumb"));
									groupChatList.setCounts(obj.getString("Counts"));
									groupChatList.setGroupChatBeans(JsonTools.jsonObjArray(obj.getString("List"), GroupChatBean.class));
									groupChatLists.add(groupChatList);
								}
							}
						}
//						String list = data.getString("ActivityList");
//						List<GroupChatList> groupChatBeans = JsonTools
//								.jsonObjArray(list, GroupChatList.class);
						return groupChatLists;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			LogUtils.e(e.getMessage());
			return null;
		}
		return null;
	}

}