package com.sridama.eztrack.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class EzTrackErrorCodes {
	
	private static Properties prop = null;
	private static HashMap<Integer, String> ecodeMap = new HashMap<Integer, String> ();
	
	public static synchronized void loadMap() throws IOException  {
		if ( prop == null ) {
			prop = new Properties();
			InputStream inputStream = EzTrackErrorCodes.class.getClassLoader()
					.getResourceAsStream("errorcode.properties");
			prop.load(inputStream);
			
			Set set = prop.keySet();
			
			Iterator iterate = set.iterator(); 
			
			while (iterate.hasNext()) {
				String key = iterate.next().toString() ;
				//System.out.println( key +":"+prop.getProperty(key));
				ecodeMap.put(Integer.parseInt(key), prop.getProperty(key) );
			}
			
		}
		
	}
	
	/*
	 * returns the value of the error code 
	 */
	public static String getErrorCode(int key) {
		return ecodeMap.get( key );
	}

}
