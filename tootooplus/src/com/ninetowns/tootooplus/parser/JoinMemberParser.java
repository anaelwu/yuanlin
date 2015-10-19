package com.ninetowns.tootooplus.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ninetowns.library.parser.AbsParser;
import com.ninetowns.tootooplus.bean.GroupDetailBean;
import com.ninetowns.tootooplus.bean.JoinMemberBean;
import com.ninetowns.tootooplus.bean.WishDetailPageListBean;

public class JoinMemberParser extends AbsParser<List<JoinMemberBean>> {

	private String Authority;
	private String ActivityAddress;
	private String ActivityTime;
	private String ActivityCode;
	private String Source;
	private String status;
	public String getMinPeopleNumber() {
		return MinPeopleNumber;
	}

	public void setMinPeopleNumber(String minPeopleNumber) {
		MinPeopleNumber = minPeopleNumber;
	}

	public String getMinGroupNumber() {
		return MinGroupNumber;
	}

	public void setMinGroupNumber(String minGroupNumber) {
		MinGroupNumber = minGroupNumber;
	}

	private String MinPeopleNumber;
	private String MinGroupNumber;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getActivityAddress() {
		return ActivityAddress;
	}

	public String getActivityCode() {
		return ActivityCode;
	}

	public void setActivityCode(String activityCode) {
		ActivityCode = activityCode;
	}

	public String getSource() {
		return Source;
	}

	public void setSource(String source) {
		Source = source;
	}

	public void setActivityAddress(String activityAddress) {
		ActivityAddress = activityAddress;
	}

	public String getActivityTime() {
		return ActivityTime;
	}

	public void setActivityTime(String activityTime) {
		ActivityTime = activityTime;
	}

	public void setTotalPage(int page)
	{
		this.totalPage = page;
	}
	
	
	private int totalPage;
	
	public int getTotalPage() {
		return totalPage;
	}
	
	//
	// "Authority": "1",
	// "EatCode": ""
	private String EatCode;
	private GroupDetailBean groupDetailBean;

	public GroupDetailBean getGroupDetailBean() {
		return groupDetailBean;
	}

	public void setGroupDetailBean(GroupDetailBean groupDetailBean) {
		this.groupDetailBean = groupDetailBean;
	}

	public String getEatCode() {
		return EatCode;
	}

	public void setEatCode(String eatCode) {
		EatCode = eatCode;
	}

	public JoinMemberParser(String str) {
		super(str);
		// TODO Auto-generated constructor stub
	}

	public void setAuthority(String Authority) {
		this.Authority = Authority;
	}

	public String getAuthority() {
		return Authority;
	}

	@Override
	public List<JoinMemberBean> getParseResult(String netStr) {
		// TODO Auto-generated method stub
		List<JoinMemberBean> members = new ArrayList<JoinMemberBean>();
		try {
			JSONObject jsonObj = new JSONObject(netStr);
			if (jsonObj.has("Status")
					&& jsonObj.getString("Status").equals("1")) {
				if (jsonObj.has("Data")) {
					JSONObject jsonData = jsonObj.getJSONObject("Data");
					if (jsonData.has("List")) {
						JSONArray jsonList = jsonData.getJSONArray("List");
						for (int i = 0; i < jsonList.length(); i++) {
							JoinMemberBean joinMemberBean = new JoinMemberBean();
							JSONObject jsonItem = jsonList.getJSONObject(i);
							if (jsonItem.has("GroupMemberId")) {
								joinMemberBean.setGroupMemberId(jsonItem
										.getString("GroupMemberId"));
							}

							if (jsonItem.has("GroupId")) {
								joinMemberBean.setGroupId(jsonItem
										.getString("GroupId"));
							}

							if (jsonItem.has("IsAdmin")) {
								joinMemberBean.setIsAdmin(jsonItem
										.getString("IsAdmin"));
							}

							if (jsonItem.has("UserId")) {
								joinMemberBean.setUserId(jsonItem
										.getString("UserId"));
							}

							if (jsonItem.has("UserName")) {
								joinMemberBean.setUserName(jsonItem
										.getString("UserName"));
							}

							if (jsonItem.has("LogoUrl")) {
								joinMemberBean.setLogoUrl(jsonItem
										.getString("LogoUrl"));
							}

							if (jsonItem.has("UserGrade")) {
								joinMemberBean.setUserGrade(jsonItem
										.getString("UserGrade"));
							}

							if (jsonItem.has("BusinessStatus")) {
								joinMemberBean.setBusinessStatus(jsonItem
										.getString("BusinessStatus"));
							}
							members.add(joinMemberBean);
						}
					}

					if(jsonData.has("GroupDetail")){
						String strList = jsonData.getString("GroupDetail");
					
						Gson gson = new Gson();
						GroupDetailBean groupDetaidBean = gson.fromJson(strList, GroupDetailBean.class);
						setGroupDetailBean(groupDetaidBean);
//						List<GroupDetailBean> localgroupList = gson.fromJson(strList,
//								new TypeToken<List<GroupDetailBean>>() {
//								}.getType());
//						setGroupList(localgroupList);
					}

					if (jsonData.has("ActivityCode")) {
						setActivityCode(jsonData.getString("ActivityCode"));
					}
					if (jsonData.has("TotalPage")) {
						setTotalPage(jsonData.getInt("TotalPage"));
					}
					if (jsonData.has("Source")) {
						setSource(jsonData.getString("Source"));
					}
					if (jsonData.has("Authority")) {
						setAuthority(jsonData.getString("Authority"));
					}
					if (jsonData.has("MinPeopleNumber")) {
						setMinPeopleNumber(jsonData.getString("MinPeopleNumber"));
					}
					if (jsonData.has("MinGroupNumber")) {
						setMinGroupNumber(jsonData.getString("MinGroupNumber"));
					}
					if (jsonData.has("EatCode")) {
						setEatCode(jsonData.getString("EatCode"));
					}
					if (jsonData.has("ActivityTime")) {
						setActivityTime(jsonData.getString("ActivityTime"));
					}
					if (jsonData.has("ActivityAddress")) {
						setActivityAddress(jsonData.getString("ActivityAddress"));
					}
				}
			}else if(jsonObj.getString("Status").equals("3402")){
				setStatus(jsonObj.getString("Status"));
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return members;
	}
}
