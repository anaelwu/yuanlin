package com.ninetowns.tootoopluse.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ninetowns.library.parser.AbsParser;
import com.ninetowns.tootoopluse.bean.WishBean;
public class WishParser extends AbsParser<List<WishBean>>{
	private int totalCount;//总记录数
	private int totalPage;//总共页数

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

	public WishParser(String str) {
        super(str);
    }

    @Override
    public List<WishBean> getParseResult(String netStr) {
        List<WishBean> wishList=new ArrayList<WishBean>();
        try {
            JSONObject obj=new JSONObject(netStr);
            if(obj.has("Status")&&obj.getString("Status").equals("1")){
                if(obj.has("Data")){
                    String strData=obj.getString("Data");
                    JSONObject objList=new JSONObject(strData);
                    if(objList.has("TotalCount")){
                    	setTotalCount(objList.getInt("TotalCount"));
                    }
                    if(objList.has("TotalPage")){
                    	setTotalPage(objList.getInt("TotalPage"));
                    }
                    String strList=objList.getString("List");
                    Gson gson=new Gson();
                    wishList=gson.fromJson(strList, new TypeToken<List<WishBean>>() {
                    }.getType());
                }
               
                
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return wishList;
    }
}
