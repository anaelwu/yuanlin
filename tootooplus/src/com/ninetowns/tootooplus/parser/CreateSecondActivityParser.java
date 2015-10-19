package com.ninetowns.tootooplus.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ninetowns.library.parser.AbsParser;
import com.ninetowns.tootooplus.bean.ConVertBean;
import com.ninetowns.tootooplus.bean.CreateActivitySecondBean;
import com.ninetowns.tootooplus.bean.SecondStepStoryBean;
import com.ninetowns.tootooplus.bean.StoryDetailListBean;

/**
 * 
 * @ClassName: WishDetailParser
 * @Description: 创建活动第二步
 * @author wuyulong
 * @date 2015-2-5 上午10:51:03
 * 
 */

public class CreateSecondActivityParser extends AbsParser<SecondStepStoryBean> {

	public CreateSecondActivityParser(String str) {
		super(str);
	}

	@Override
	public SecondStepStoryBean getParseResult(String netStr) {
		SecondStepStoryBean secondStepStoryBean = new SecondStepStoryBean();
		List<StoryDetailListBean> wishList = new ArrayList<StoryDetailListBean>();
		List<CreateActivitySecondBean> storySecondList = new ArrayList<CreateActivitySecondBean>();
		try {
			JSONObject obj = new JSONObject(netStr);
			if (obj.has("Status") && obj.getString("Status").equals("1")) {
				if (obj.has("Data")) {
					String strData = obj.getString("Data");
					JSONObject jsobj = new JSONObject(strData);
					if (jsobj.has("StoryList")) {
						String storyList = jsobj.getString("StoryList");
						JSONArray jsonArray = new JSONArray(storyList);
						for (int i = 0; i < jsonArray.length(); i++) {
							CreateActivitySecondBean createActiSecBean = new CreateActivitySecondBean();
							JSONObject objList = (JSONObject) jsonArray.get(i);
							ConVertBean vonvertBean = new ConVertBean();
							String strList = objList.getString("PageList");
							Gson gson = new Gson();
							wishList = gson.fromJson(strList,
									new TypeToken<List<StoryDetailListBean>>() {
									}.getType());
							createActiSecBean.setWishDetailBean(wishList);
							if (objList.has("StoryCover")) {
								String storyCovet = objList
										.getString("StoryCover");
								JSONObject jsonobject = new JSONObject(
										storyCovet);
								if (jsonobject.has("StoryType")) {
									vonvertBean.setStoryType(jsonobject
											.getString("StoryType"));
								}
								if (jsonobject.has("CoverThumb")) {
									vonvertBean.setCoverThumb(jsonobject
											.getString("CoverThumb"));
								}
								if (jsonobject.has("CoverImg")) {
									vonvertBean.setCoverImg(jsonobject
											.getString("CoverImg"));
								}
								if (jsonobject.has("StoryVideoUrl")) {
									vonvertBean.setStoryVideoUrl(jsonobject
											.getString("StoryVideoUrl"));
								}

							}
							if (objList.has("StoryId")) {
								vonvertBean.setStoryId(objList
										.getString("StoryId"));

							}
							if (objList.has("StoryName")) {
								vonvertBean.setStoryName(objList
										.getString("StoryName"));
							}
							createActiSecBean.setConvertBean(vonvertBean);
							storySecondList.add(createActiSecBean);
						}

					}
					if (jsobj.has("ActivityId")) {
						String activityId = jsobj.getString("ActivityId");
						secondStepStoryBean.setActivityId(activityId);
					}
					if(jsobj.has("StoryId")){
						secondStepStoryBean.setStoryIdArray(jsobj.getString("StoryId"));
						
					}if(jsobj.has("ActivityName")){
						secondStepStoryBean.setActivityName(jsobj.getString("ActivityName"));
						
					}

				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		secondStepStoryBean.setStoryList(storySecondList);
		return secondStepStoryBean;
	}
}
