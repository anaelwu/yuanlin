package com.ninetowns.tootooplus.bean;

import java.io.Serializable;

/**
 * 
 * @ClassName: WishBean
 * @Description: 白吃心愿实体类
 * @author wuyulong
 * @date 2015-1-22 下午4:09:35
 * 
 */
@SuppressWarnings("serial")
public class WishBean implements Serializable {

	private String StoryId;// ：故事Id
	private String UserId;// ：故事创建者用户Id
	private String UserName;// ：故事创建者用户名称
	private String LogoUrl;// ：故事创建者用户头像
	private String UserGrade;// ：故事创建者用户等级
	private String StoryName;// ：故事名称
	private String StoryType;// ：类型：1,文字，2图片，3视频
	private String CoverThumb;// ：故事封面图，类型为图片或视频的改字段有值
	private String StoryVideoUrl;// ：故事封面录播视频地址
	private String CountFree;// ：我要白吃的用户数量
	private String Free;// ：我要白吃 0默认值1点击过
	private String IsRecommend;// ：推荐是否通过0，未通过，1通过
	public String getStoryType() {
		return StoryType;
	}
	public void setStoryType(String storyType) {
		StoryType = storyType;
	}
	public String getCoverThumb() {
		return CoverThumb;
	}
	public void setCoverThumb(String coverThumb) {
		CoverThumb = coverThumb;
	}
	public String getStoryId() {
		return StoryId;
	}
	public void setStoryId(String storyId) {
		StoryId = storyId;
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
	public String getStoryName() {
		return StoryName;
	}
	public void setStoryName(String storyName) {
		StoryName = storyName;
	}
	public String getStoryVideoUrl() {
		return StoryVideoUrl;
	}
	public void setStoryVideoUrl(String storyVideoUrl) {
		StoryVideoUrl = storyVideoUrl;
	}
	public String getCountFree() {
		return CountFree;
	}
	public void setCountFree(String countFree) {
		CountFree = countFree;
	}
	public String getFree() {
		return Free;
	}
	public void setFree(String free) {
		Free = free;
	}
	public String getIsRecommend() {
		return IsRecommend;
	}
	public void setIsRecommend(String isRecommend) {
		IsRecommend = isRecommend;
	}
	@Override
	public String toString() {
		return "WishBean [StoryId=" + StoryId + ", UserId=" + UserId
				+ ", UserName=" + UserName + ", LogoUrl=" + LogoUrl
				+ ", UserGrade=" + UserGrade + ", StoryName=" + StoryName
				+ ", StoryType=" + StoryType + ", CoverThumb=" + CoverThumb
				+ ", StoryVideoUrl=" + StoryVideoUrl + ", CountFree="
				+ CountFree + ", Free=" + Free + ", IsRecommend=" + IsRecommend
				+ "]";
	}

	
}
