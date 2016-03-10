/**
 * 
 */
package com.sridama.eztrack.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.sridama.eztrack.utils.JDBCHelper;

/**
 * @author admin
 *
 */
public class CategoriesGW {

	/**
	 * Creates a tree of categories and returns its JSON representation
	 * @return
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static String getCategories() throws SQLException, ClassNotFoundException, IOException {
		Connection connection = JDBCHelper.getConnection();
		// Make your sql statement here...
		
		CategoryNode rootNode = new CategoryNode(0,"root", "", -1 );
		
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("select * from category_master order by category_id "); // where
		
		while (rs.next()) {
			// for each record...
			// create a CategoryNode object.
			CategoryNode node = new CategoryNode(rs.getInt("category_id"),
								rs.getString("cat_name"),
								rs.getString("description"),
								rs.getInt("parent_id"));
			
			if (node.isRoot()) {
				rootNode.addChild(node);
			}
			else {
				rootNode.find(node.getParentId()).addChild(node);
			}
			
		}
		//TODO: prepare a JSON array by iterating the category rootnode...
		String temp=rootNode.toJSON();
     StringBuilder sb = new StringBuilder();
		
	/*	sb.append("{");
		sb.append("\"more\" : \"false\" ,");
		sb.append("\"results\" : [ ");
		sb.append(temp);
	sb.append("]");
		sb.append("}");*/
		
		sb.append("[ ");
		sb.append(temp);
		sb.append("]");
		
		return sb.toString();
	}
}
