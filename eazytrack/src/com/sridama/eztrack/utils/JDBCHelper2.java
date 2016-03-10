/**
 * 
 */
package com.sridama.eztrack.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * @author admin
 * 
 */
public class JDBCHelper2 {

	private static Properties prop = null;
	
	private static String driver =""; 
	private static String url ="";
	private static String user="";
	private static String password="";
	public static long SESSION_TIME_OUT = 0 ;

	public static Connection getConnection() throws IOException,
			ClassNotFoundException, SQLException {
		
		
		if (prop == null) {
			prop = new Properties();
			InputStream inputStream = JDBCHelper.class.getClassLoader()
					.getResourceAsStream("eztrack.properties");
			prop.load(inputStream);
			
			 driver = prop.getProperty("driver");
			 url = prop.getProperty("url");
			 user = prop.getProperty("user");
			 password = prop.getProperty("password");
			 SESSION_TIME_OUT = Long.parseLong(prop.getProperty("session_time_out"));
			Class.forName(driver);
		}
			Connection connection = DriverManager.getConnection(url, user,
					password);
			
			return connection ;
		
	}
}
