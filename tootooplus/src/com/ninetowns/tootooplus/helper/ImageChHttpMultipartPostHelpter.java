package com.ninetowns.tootooplus.helper;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.mime.content.StringBody;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.ninetowns.library.util.LogUtil;
import com.ninetowns.tootooplus.fragment.UpLoadViewDialogFragment;

public class ImageChHttpMultipartPostHelpter extends ImageHttpMultipartPostHelper{
    private Bundle bundle;
    private HashMap<String, String> map;
    private boolean isEditextView;
    private boolean isCreateView;
    private boolean isDraftView;
    private boolean isConvertView;
    private boolean isRecommendView;
    private boolean isRecommendConvertView;
    private List<File> listFile;


    public ImageChHttpMultipartPostHelpter(Context context, String url,
            File imageFile, File file, HashMap<String, String> map,
            Handler handler) {
        super(context, url, imageFile, file, map, handler);
        this.map=map;
     
    }
    public ImageChHttpMultipartPostHelpter(Context context, String url,
            List<File> listFile, HashMap<String, String> map, Handler handler,UpLoadViewDialogFragment  upDialog,Bundle bundle){
        super(context, url, listFile, map, handler, upDialog);
        this.bundle=bundle;
        this.map=map;
        this.listFile=listFile;
        isEditextView = bundle.getBoolean(ConstantsTooTooEHelper.isEditextView);
        isCreateView = bundle.getBoolean(ConstantsTooTooEHelper.isCreateView);
        isDraftView = bundle.getBoolean(ConstantsTooTooEHelper.isDraftView);
        isConvertView = bundle.getBoolean(ConstantsTooTooEHelper.isConvertView);
        isRecommendView = bundle.getBoolean(ConstantsTooTooEHelper.isRecommendView);
        isRecommendConvertView = bundle
                .getBoolean(ConstantsTooTooEHelper.isConvertRecommendView);
    }
  @Override
    public void setParamPart(CustomMultiPartEntityHelper multipartContent,int count) {//偷粮换柱  count  上传第几个
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                try {
                    if(entry.getKey().equals(ConstantsTooTooEHelper.ScenarioType)){
                        if(isRecommendConvertView){
                            if(count==1){//如果是第一个 那么是封面图
                                multipartContent.addPart(entry.getKey(),
                                        new StringBody("2"));
                                LogUtil.systemlogInfo("isRecommendView封面图"+ConstantsTooTooEHelper.ScenarioType, "2");
                            }else{//3是拖拽页
                                multipartContent.addPart(entry.getKey(),
                                        new StringBody("3"));  
                                LogUtil.systemlogInfo("isRecommendView拖拽图"+ConstantsTooTooEHelper.ScenarioType, "3");
                            }
                         
                        }else if(isCreateView){
                            multipartContent.addPart(entry.getKey(),
                                    new StringBody("3"));  
                            LogUtil.systemlogInfo("isCreateView拖拽图"+ConstantsTooTooEHelper.ScenarioType, "3");
                        }else if(isConvertView){
                            if(count==1){//如果是第一个 那么是封面图
                                multipartContent.addPart(entry.getKey(),
                                        new StringBody("2"));
                                LogUtil.systemlogInfo("isRecommendView封面图"+ConstantsTooTooEHelper.ScenarioType, "2");
                            }else{//3是拖拽页
                                multipartContent.addPart(entry.getKey(),
                                        new StringBody("3"));  
                                LogUtil.systemlogInfo("isRecommendView拖拽图"+ConstantsTooTooEHelper.ScenarioType, "3");
                            }
                            
                        }else if(isEditextView){

                            multipartContent.addPart(entry.getKey(),
                                    new StringBody("3"));  
                            LogUtil.systemlogInfo("isCreateView拖拽图"+ConstantsTooTooEHelper.ScenarioType, "3");
                        
                        }
                    }else{
                        multipartContent.addPart(entry.getKey(),
                                new StringBody(entry.getValue()));
                    }
                
                 
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
   
    }
    

}
