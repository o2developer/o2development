/**
 * 
 */
package com.sridama.eztrack.controller;

/**
 * @author Rizwan
 *
 */
import java.net.InetAddress;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import com.sridama.eztrack.utils.JDBCHelper;

public class License {
	 String  macAddress;

	public boolean isDateValid() throws ParseException, IOException {
		
		FileReader fr = new FileReader("resources/license");
		BufferedReader br = new BufferedReader(fr);
		StringBuilder sb4 = new StringBuilder() ;
		String line = "" ;
		while ((line = br.readLine())!=null) 
			sb4.append(line.trim());
		
		
		System.out.println("Length of license String "+sb4.toString().length());
		
		String s1 = sb4.toString().substring(15, 23);
		String s2 = sb4.toString().substring(45, 53);
	
		 
		char keyArr[] = s1.toCharArray() ;
		char keyEnd[] = s2.toCharArray() ;
		char keyArrayStart[] = {'k','l','p','b','m','a','d','i','r','s'}; 
		char keyArrayEnd[]   = new StringBuilder(String.valueOf(keyArrayStart)).reverse().toString().toCharArray() ;
		
		HashMap< Character , Integer>  startMap = new HashMap<Character , Integer>() ;
		HashMap<Character , Integer>  endMap = new HashMap<Character , Integer>() ;
		
		for (int loop = 0 ;  loop < keyArrayStart.length  ; loop++) {
		   startMap.put(keyArrayStart[loop],loop);
		}
		
		
		for (int loop = 0 ;  loop < keyArrayEnd.length  ; loop++) {
			endMap.put(  keyArrayEnd[loop],loop);
			}
	
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		
		
		
		for (int loop1 = 0 ;  loop1 < keyArr.length  ; loop1++) {
			sb.append(startMap.get(keyArr[loop1]));
		}
		
		
		
		for (int loop1 = 0 ;  loop1 < keyEnd.length  ; loop1++) {
			sb1.append(endMap.get(keyEnd[loop1]));
		}

	
		SimpleDateFormat sdf = new SimpleDateFormat("MMyyyydd");
		Date date = sdf.parse(sdf.format(new Date()));
		Date bd = sdf.parse(sb.toString());
		Date ed = sdf.parse(sb1.toString());

		if (date.compareTo(bd) >= 0  && date.compareTo(ed) <= 0 ) {
			System.out.println("***License valid***");
			return true ;
		} else {
			System.out.println("Your License Expired...........!");
			return false ;
		}
	}

	

		
	
	/*
	 * returns true if license is valid
	 */
	public boolean licenseValid() {
		

		if (System.getProperty("os.name").contains("Windows"))
			macAddress = getMacAddressWindows();
		else	
			macAddress = getMacAddressLinux();
	

		try {

			InetAddress h = InetAddress.getLocalHost();
			String n = h.getHostName();
			Connection con = JDBCHelper.getConnection() ;

			if (!checkIfMacExits()) {
			String sql = "INSERT INTO mac(macaddress)" + "values(?)";
			PreparedStatement stm = con.prepareStatement(sql);
			stm.setString(1, macAddress);
			stm.executeUpdate();
			}
			
			
			Statement stmtd = con.createStatement();
			ResultSet res = stmtd
					.executeQuery("select macaddress from mac where id=1");
		
			if (res.isBeforeFirst() && isDateValid() ) {
				return true ;
			}

		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			System.out.println("Expired...........!");
		}
	
		return false ;
	}
	
	
	/*
	 * Returns the mac address of the current running windows  machine
	 */
	private String getMacAddressWindows()  {
		String command = "ipconfig /all";
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(command);
			BufferedReader inn = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			String line = ""; 
			while ((line = inn.readLine()) != null) {
				if (line.trim().startsWith("Physical Address"))
					break;
			}

			if (line != null) {
				String a[] = line.split(":");
				macAddress = a[1];
			} 
			
			return macAddress;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	/*
	 * Returns the mac address of the current running Linux machine
	 */
	private String getMacAddressLinux()  {
		String command = "ifconfig";
		Process p = null;
		try {
		 p = Runtime.getRuntime().exec(command);
			FileReader fr = new FileReader("resources/linuxmac");
			/*BufferedReader inn = new BufferedReader(new InputStreamReader(
					p.getInputStream()));*/
			BufferedReader inn = new BufferedReader(fr);
			String line = ""; 
			while ((line = inn.readLine()) != null) {
				//System.out.println(line.trim());
				if (line.trim().contains("HWaddr"))
					break;
			}
			
			return line==null?null:line.substring(line.indexOf("HWaddr")+"HWaddr".length() , line.length()).trim();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null ;
	}
	
	/*
	 * returns true if current mac address already exists in the database
	 */
	private boolean checkIfMacExits()  {
		Connection con = null ;
		Statement stmt = null ;
		ResultSet rs   = null ;
		try {
			con  = JDBCHelper.getConnection();
		    stmt = con.createStatement();
			rs = stmt
					.executeQuery("select * from mac where macaddress='"+macAddress+"'");
			if (rs.isBeforeFirst()) 
			return true ;
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
			if (con!=null) con.close();
			if (stmt!=null) stmt.close();
			if (rs!=null ) rs.close () ;
			} catch(SQLException e ) {
				e.printStackTrace();
			}
		}
		return false ;
	}
	
	
	/*
	 * Main method only for testing
	 */
	public static void main(String args[]) throws Exception {
		License d = new License();
		System.out.println(d.licenseValid());
	}
	
}
