package com.ninetowns.tootooplus.bean;

import java.io.Serializable;

public class UpLoadFileBean implements Serializable{
	private String fileUrl;//原始文件名
	private String thumbFileUrl;//商品故事和推荐故事缩略图
	private int count;
	

	private String ListCoverImg;//：列表页封面图
	private String DragRectangleImg;//：拖动长方形元素
	private String DragSquareImg;//：拖动正方形元素
	private String TailorSquareImg;//：用户裁剪后生成的元素
	private String imageFileUrl;
	private String DragSquareBigImg;//1:1大正方形	
	private String DragRectangleBigImg;//4:3
	private String DefaultType;//：DefaultType字段 //默认显示图片  1、长方形 2:1; 2、小正方形；3、长方形4：3；4、大正方形


	public String getDefaultType() {
		return DefaultType;
	}
	public void setDefaultType(String defaultType) {
		DefaultType = defaultType;
	}
	public String getDragSquareBigImg() {
		return DragSquareBigImg;
	}
	public void setDragSquareBigImg(String dragSquareBigImg) {
		DragSquareBigImg = dragSquareBigImg;
	}
	public String getDragRectangleBigImg() {
		return DragRectangleBigImg;
	}
	public void setDragRectangleBigImg(String dragRectangleBigImg) {
		DragRectangleBigImg = dragRectangleBigImg;
	}
	public String getImageFileUrl() {
        return imageFileUrl;
    }
    public void setImageFileUrl(String imageFileUrl) {
        this.imageFileUrl = imageFileUrl;
    }
    public String getListCoverImg() {
        return ListCoverImg;
    }
    public void setListCoverImg(String listCoverImg) {
        ListCoverImg = listCoverImg;
    }
    public String getDragRectangleImg() {
        return DragRectangleImg;
    }
    public void setDragRectangleImg(String dragRectangleImg) {
        DragRectangleImg = dragRectangleImg;
    }
    public String getDragSquareImg() {
        return DragSquareImg;
    }
    public void setDragSquareImg(String dragSquareImg) {
        DragSquareImg = dragSquareImg;
    }
    public String getTailorSquareImg() {
        return TailorSquareImg;
    }
    public void setTailorSquareImg(String tailorSquareImg) {
        TailorSquareImg = tailorSquareImg;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public String getThumbFileUrl() {
		return thumbFileUrl;
	}
	public void setThumbFileUrl(String thumbFileUrl) {
		this.thumbFileUrl = thumbFileUrl;
	}
	@Override
	public String toString() {
		return "UpLoadFileBean [fileUrl=" + fileUrl + ", thumbFileUrl="
				+ thumbFileUrl + ", count=" + count + ", ListCoverImg="
				+ ListCoverImg + ", DragRectangleImg=" + DragRectangleImg
				+ ", DragSquareImg=" + DragSquareImg + ", TailorSquareImg="
				+ TailorSquareImg + "]";
	}

}
