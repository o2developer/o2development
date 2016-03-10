/**
 * 
 */
package com.sridama.eztrack.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author rv
 *
 */
public class OrbitCustomerImporter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			importData(args[0]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private static void importData(String file) throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String line = null;
		Connection conn = null;
		
		try {
			conn = JDBCHelper.getConnection();
			String sql = "INSERT INTO client_master VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			int lineNo = 0;
			
			while ( (line=br.readLine()) != null) {
				
				if (line.startsWith("#") || line.trim().isEmpty())
					continue;
				
				String[] fields = line.split("\t");
				stmt.setInt(1, Integer.parseInt(fields[0]));		// id
				stmt.setString(2, fields[1]);								// name
				stmt.setString(3, fields[7] + fields[8] + fields[9]); //address
				
				// if data for phone1 field > 32, truncate it.
				if (fields[10].length() > 32)
					fields[10] = fields[10].substring(0, 31);
				stmt.setString(4, fields[10]); //phone
				
				stmt.setString(5, "");
				stmt.setString(6, "");
				stmt.setString(7, "");
				stmt.setString(8, "");
				stmt.setString(9, "");
				stmt.setString(10, "");
				
				//System.out.println("Processing line#" + ++lineNo);
				stmt.executeUpdate();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println("Line: "+ line);
		}
		

		
	}
	
	

}
