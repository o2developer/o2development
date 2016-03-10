/**
 * 
 */
package com.sridama.eztrack.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

/**
 * @author admin
 * 
 */
public class JDBCHelper {

	private static Properties prop = null;

	private static String driver = "";
	private static String url = "";
	private static String user = "";
	private static String password = "";
	public static long SESSION_TIME_OUT = 0;
	
	private static BoneCP connectionPool = null; 

	public static void init() throws IOException, ClassNotFoundException {
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
			System.out.println("Registering mysql driver class...");
			Class.forName(driver);
			
			// Create connection pool and its configuration.
			BoneCPConfig config;
			try {
				
				config = new BoneCPConfig(prop);
				config.setJdbcUrl(url);
				config.setUsername(user);
				config.setPassword(password);
				connectionPool = new BoneCP(config);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private static final ThreadLocal<Connection> con = new ThreadLocal<Connection>() {
		protected Connection initialValue() {

			try {
				return connectionPool.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
	};

	public static Connection getConnection() throws IOException,
			ClassNotFoundException, SQLException {

		return con.get();

	}

	public static void removeConnection() {
		try {
			Connection connection = con.get();
			if (!connection.isClosed()) {
				connection.close();
				System.out.println("Connection removed!");
			} else {
				System.out.println("Connection is in closed state. So not closing... ");
			}
			//con.get().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		con.remove();
	}
}
