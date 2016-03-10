/**
 * 
 */
package com.sridama.eztrack.junit;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import com.sridama.eztrack.dao.CategoriesGW;

/**
 * @author Rizwan
 *
 */
public class CategoryGWTest {

	/**
	 * Test method for {@link com.sridama.eztrack.dao.CategoriesGW#getCategories()}.
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void testGetCategories() throws ClassNotFoundException, SQLException, IOException {
		System.out.println(CategoriesGW.getCategories());
		assertTrue("category list cannot be null ",CategoriesGW.getCategories()!=null);
	}

}
