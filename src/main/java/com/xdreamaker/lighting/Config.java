package com.xdreamaker.lighting;

import java.io.File;

import com.abigdreamer.ark.commons.util.PropertiesUtil;
import com.abigdreamer.ark.framework.collection.Mapx;

/**
 * @author Darkness
 * @date 2017年4月20日 上午10:37:45
 * @version 1.0
 * @since 1.0 
 */
public class Config {
	private static Mapx<String, String> properties;
	
	private static void init() {
		if(properties == null) {
			String configPath = System.getProperty("user.dir") + File.separator + "config.properties";
			properties = PropertiesUtil.read(new File(configPath));
		}
		
	}
	
	public static int getInt(String key) {
		init();
		return properties.getInt(key);
	}
	
	public static String get(String key) {
		init();
		return properties.get(key);
	}

}
