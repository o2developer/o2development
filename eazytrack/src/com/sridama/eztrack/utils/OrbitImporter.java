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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * @author rv
 * 
 */
public class OrbitImporter {

	private static Connection conn = null;
	private static PreparedStatement stmtItemMaster = null;
	private static boolean itemStmtPrepared = false;
	private static PreparedStatement stmtCategoryMaster = null;
	private static boolean categoryStmtPrepared = false;
	private static HashMap<String, Integer> categoryMap = new HashMap<String, Integer>();

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		importData(args[0]);
	}

	public static void importData(String file) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		// Connection conn = null;
		PreparedStatement stmt = null;
		int lineNo = 0;
		try {
			conn = JDBCHelper.getConnection();
			while ((line = br.readLine()) != null) {

				/** ignore comment and empty lines **/
				if (line.startsWith("#") || line.trim().length() == 0)
					continue;

				String[] fields = line.split("\t");
				if (fields.length <= 15) {
					System.out.println("Line format error." + line);
					continue;
				}

				// if field[0] is not a number type, ignore this line too.
				if (!isNumeric(fields[0]))
						continue;
				
				stmt = getSQL(fields);
				
				System.out.println("Processing record: [" + ++lineNo + "]");
				stmt.executeUpdate();

			}

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static PreparedStatement getSQL(String[] fields) throws SQLException {
		//String[] fields = line.split("\t");
		//System.out.println("No. of fields in the row: " + fields.length);

		int categoryId = add2Categories(fields);

		String sql = "INSERT INTO item_master values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		stmtItemMaster = conn.prepareStatement(sql);

		// Set the values to prepared statement
		stmtItemMaster.setInt(1, Integer.parseInt(fields[0])); // item id
		stmtItemMaster.setString(2, fields[2]);
		
		/** if item code is not integer format, let us add this to remarks column 
		 * Should be reconciled later with customer. 
		 */
		if (isNumeric(fields[1])) {
			stmtItemMaster.setInt(3, Integer.parseInt(fields[1]));
			stmtItemMaster.setString(13, "");
		}
		else {
			stmtItemMaster.setInt(3, 0);
			stmtItemMaster.setString(13, fields[1]);
		}
		
		/**
		 * category addition is a bit tricky affair. Look for brand field if not
		 * empty, add it as category. if empty look for itemsubgroupid field if
		 * this is too empty, look for itemgroupid and add it. if this is also
		 * empty, just ignore it :)
		 * 
		 **/
		String category = null;
		if (fields.length == 35)
			category = fields[34].trim();
		else
			category = fields[15].trim();

		if (category.length() == 0) {
			category = fields[15].trim();
			if (category.length() == 0) {
				category = fields[14].trim();
			}
		}
		stmtItemMaster.setInt(4, categoryId);

		stmtItemMaster.setString(5, fields[4]); // Units of measure
		stmtItemMaster.setFloat(6, Float.parseFloat(fields[12])); // Selling
																	// rate
		stmtItemMaster.setInt(7, -1); // There is no opening balance in the
										// import data.
		stmtItemMaster.setFloat(8, Float.parseFloat(fields[13])); // buying rate
		stmtItemMaster.setFloat(9, Float.parseFloat(fields[22])); // Tax rate
																	// for this
																	// item
		stmtItemMaster.setFloat(10, Float.parseFloat(fields[26])); // discount %
		stmtItemMaster.setString(11, ""); // model?
		stmtItemMaster.setFloat(12, Float.parseFloat(fields[24]));
		return stmtItemMaster;
	}

	private static int add2Categories(String[] fields) throws SQLException {

		String sql = "INSERT INTO  category_master VALUES (default,?,?,?)";
		if (!categoryStmtPrepared) {
			stmtCategoryMaster = conn.prepareStatement(sql);
			categoryStmtPrepared = true;
		}

		int categoryId = addCategory(fields[14], 0);
		int subCategory = addCategory(fields[15], categoryId);
		int brandId = addCategory(fields[15], subCategory);
		
		return brandId;
	}

	/**
	 * Adds a new entry to category master table, if not already added.
	 * 
	 * @param category
	 * @param level
	 * @return id of the newly added category
	 * @throws SQLException
	 */
	private static int addCategory(String category, int level)
			throws SQLException {

		int categoryId = -1;
		/**
		 * Add to category master table, if not already added.
		 */
		if (!categoryMap.containsKey(category)) {

			stmtCategoryMaster.setString(1, category);
			stmtCategoryMaster.setString(2, category);
			stmtCategoryMaster.setInt(3, level);
			stmtCategoryMaster.executeUpdate();

			/** Find the id of the newly added category **/
			String sql = "select category_id from category_master where cat_name='"
					+ category + "'";

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			categoryId = rs.getInt("category_id");

			stmt.close();
			rs.close();
			categoryMap.put(category, categoryId);

		}
		else {
			categoryId = categoryMap.get(category);
		}
		return categoryId;

	}

	private static boolean isNumeric(String s) {
		try {
		Integer.parseInt(s);
		return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
}
