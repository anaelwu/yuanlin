package com.ninetowns.tootoopluse.bean;

import com.lidroid.xutils.db.annotation.Id;
/**
 * 
* @ClassName: HistoryBean 
* @Description: 历史记录实体类
* @author wuyulong
* @date 2015-4-10 下午3:36:23 
*
 */
public class ActivityHistoryBean extends HistoryBean{
	@Id
	private int historyId;//历史记录的id
	private String historyName;
	public int getHistoryId() {
		return historyId;
	}
	public void setHistoryId(int historyId) {
		this.historyId = historyId;
	}
	public String getHistoryName() {
		return historyName;
	}
	public void setHistoryName(String historyName) {
		this.historyName = historyName;
	}

}