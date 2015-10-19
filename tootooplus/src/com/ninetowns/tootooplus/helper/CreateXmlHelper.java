package com.ninetowns.tootooplus.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.os.Environment;
import android.util.Xml;

/**
 * 
 * @ClassName: CreateXmlHelper
 * @Description:操作xml 创建xml/删除
 * @author wuyulong
 * @date 2015-6-4 下午12:39:30
 * 
 */
public class CreateXmlHelper {
	private static final CreateXmlHelper helper = new CreateXmlHelper();
	private static final String FILENAME = "browse.xml";

	private CreateXmlHelper() {
	}

	public static CreateXmlHelper getInstance() {
		return helper;

	}

	/**
	 * 
	 * @Title: isExistsFileBrose
	 * @Description: 是否存在浏览统计的文件
	 * @param
	 * @return
	 * @throws
	 */
	public static boolean isExistsFileBrose() {
		if (existSDCard()) {
			File sdFile = new File(Environment.getExternalStorageDirectory(),
					FILENAME);
			return sdFile.exists();
		}
		return false;

	}

	/**
	 * 
	 * @Title: createXml
	 * @Description: 创建xml 因为是每一个的转化率所以再每次点击的时候都需要删除一下
	 * @param
	 * @return file
	 * @throws
	 */
	public static File createXml(Context context, List<String> browserList,
			String main, String userId) {
		FileOutputStream fos = null;
		File sdFile =null;
		if (existSDCard()) {
			 sdFile = new File(Environment.getExternalStorageDirectory(),
					FILENAME);
			if (sdFile.exists()) {
				sdFile.delete();// 删除
			}
			try {
				if (sdFile.createNewFile()) {
					fos = new FileOutputStream(sdFile);
					XmlSerializer xmlSerializer = Xml.newSerializer();
					xmlSerializer.setOutput(fos, "UTF-8");
					xmlSerializer.startDocument("UTF-8", null);
					xmlSerializer.startTag(null, "Data");

					xmlSerializer.startTag(null, "UserId");
					xmlSerializer.text(userId);
					xmlSerializer.endTag(null, "UserId");

					xmlSerializer.startTag(null, "main");
					xmlSerializer.text(main);
					xmlSerializer.endTag(null, "main");

					xmlSerializer.startTag(null, "web");
					for (String string : browserList) {
						xmlSerializer.startTag(null, "url");
						xmlSerializer.text(string);
						xmlSerializer.endTag(null, "url");

					}
					xmlSerializer.endTag(null, "web");
					xmlSerializer.endTag(null, "Data");
					xmlSerializer.endDocument();
					xmlSerializer.flush();
					if (fos != null) {
						fos.close();
					}

				}

			} catch (Exception e) {
				e.printStackTrace();

			}

		} else {

		}
		if (sdFile.exists()) {
			return sdFile;
		}else{
			return null;
		}
	}

	private static boolean existSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}

}
