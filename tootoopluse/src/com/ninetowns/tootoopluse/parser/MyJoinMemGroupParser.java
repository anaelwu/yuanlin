package com.ninetowns.tootoopluse.parser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ninetowns.library.parser.AbsParser;
import com.ninetowns.tootoopluse.bean.GridViewGroupBean;

public class MyJoinMemGroupParser extends AbsParser<List<List<GridViewGroupBean>>>{
	private String Info;

	public String getInfo() {
		return Info;
	}

	public void setInfo(String info) {
		Info = info;
	}

	public MyJoinMemGroupParser(String str) {
		super(str);
	}

//	@Override
//	public List<GridViewGroupBean> getParseResult(String netStr) {
//		List<GridViewGroupBean> listFreeGroupBean=new ArrayList<GridViewGroupBean>();
//		 try {
//	            JSONObject obj=new JSONObject(netStr);
//	            if(obj.has("Status")&&obj.getString("Status").equals("1")){
//	                if(obj.has("Data")){
//	                    String strData=obj.getString("Data");
//	                    JSONObject objList=new JSONObject(strData);
//	                    if(objList.has("Info")){
//	                    	setInfo(objList.getString("Info"));
//	                    }
//	                 
//	                    String strList=objList.getString("List");
//	                    
//	                    Gson gson=new Gson();
//	                    listFreeGroupBean=gson.fromJson(strList, new TypeToken<List<GridViewGroupBean>>() {
//	                    }.getType());
//	                }
//	               
//	                
//	            }
//	        } catch (JSONException e) {
//	            e.printStackTrace();
//	        }
//		return listFreeGroupBean;
//	}
	
	@Override
	public List<List<GridViewGroupBean>> getParseResult(String netStr) {
		List<List<GridViewGroupBean>> listFreeGroupBean=new ArrayList<List<GridViewGroupBean>>();
		 try {
	            JSONObject obj=new JSONObject(netStr);
	            if(obj.has("Status")&&obj.getInt("Status") == 1){
	                if(obj.has("Data")){
	                    String strData=obj.getString("Data");
	                    JSONObject objList=new JSONObject(strData);
	                    if(objList.has("Info")){
	                    	setInfo(objList.getString("Info"));
	                    }
	                 
	                    String strList=objList.getString("List");
	                    
	                    Gson gson=new Gson();
//	                    listFreeGroupBean=gson.fromJson(strList, new TypeToken<List<GridViewGroupBean>>() {}.getType());
	                    listFreeGroupBean = gson.fromJson(strList, new TypeToken<List<List<GridViewGroupBean>>>(){}.getType());
	                }
	               
	                
	            }
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }
		return listFreeGroupBean;
	}

}