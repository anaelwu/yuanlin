package com.ninetowns.tootooplus.helper;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.mime.content.StringBody;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.ninetowns.library.util.LogUtil;
import com.ninetowns.tootooplus.fragment.UpLoadViewDialogFragment;


public class HttpChMultipartPostHelper extends HttpMultipartPostHelper{
private HashMap<String, String> map;
private Bundle bundle;
private boolean isEditextView;
private boolean isCreateView;
private boolean isDraftView;
private boolean isConvertView;
private boolean isRecommendView;
private boolean isRecommendConvertView;
    public HttpChMultipartPostHelper(Context context, String url, File imageFile,
            File file, HashMap<String, String> map, Handler handler,
            UpLoadViewDialogFragment uploadDialog,Bundle bundle) {
        super(context, url, imageFile, file, map, handler, uploadDialog);
        this.map=map;
        this.bundle=bundle;
        isEditextView = bundle.getBoolean(ConstantsTooTooEHelper.isEditextView);
        isCreateView = bundle.getBoolean(ConstantsTooTooEHelper.isCreateView);
        isDraftView = bundle.getBoolean(ConstantsTooTooEHelper.isDraftView);
        isConvertView = bundle.getBoolean(ConstantsTooTooEHelper.isConvertView);
        isRecommendView = bundle.getBoolean(ConstantsTooTooEHelper.isRecommendView);
        isRecommendConvertView = bundle
                .getBoolean(ConstantsTooTooEHelper.isConvertRecommendView);
    }

@Override
    public void setMapParams(CustomMultiPartEntityHelper multipartContent, int count) {
    
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
                     
                    }else if(isCreateView||isRecommendView||isEditextView){
                        multipartContent.addPart(entry.getKey(),
                                new StringBody("3"));  
                        LogUtil.systemlogInfo("isCreateView拖拽图"+ConstantsTooTooEHelper.ScenarioType, "3");
                    }else if(isConvertView){
                        multipartContent.addPart(entry.getKey(),
                                new StringBody("2"));  
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