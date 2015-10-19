package com.ninetowns.tootooplus.bean;

import java.io.Serializable;
/**
 * 
* @ClassName: GridViewGroupBean 
* @Description: 白吃活动与组团白吃活动列表
* @author wuyulong
* @date 2015-3-25 下午3:33:39 
*
 */
public class GridViewGroupBean implements Serializable{
//	　　UserId：团长用户Id
//	　　UserName：用户名称
//	　　LogoUrl：用户头像
//	　　UserGrade：用户等级
//	　　Status：0组团报名（或未成功），1组团成功
//	　　Members：团成员数
	private String GroupId;
	private String Status;
	private String Members;
	private String UserId;
	private String UserName;
	private String LogoUrl;
	private String UserGrade;
	public String getGroupId() {
		return GroupId;
	}
	public void setGroupId(String groupId) {
		GroupId = groupId;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public String getMembers() {
		return Members;
	}
	public void setMembers(String members) {
		Members = members;
	}
	public String getUserId() {
		return UserId;
	}
	public void setUserId(String userId) {
		UserId = userId;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getLogoUrl() {
		return LogoUrl;
	}
	public void setLogoUrl(String logoUrl) {
		LogoUrl = logoUrl;
	}
	public String getUserGrade() {
		return UserGrade;
	}
	public void setUserGrade(String userGrade) {
		UserGrade = userGrade;
	}

}
