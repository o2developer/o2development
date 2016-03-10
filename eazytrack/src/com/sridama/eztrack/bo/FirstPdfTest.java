/**
 * 
 */
package com.sridama.eztrack.bo;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Rizwan
 *
 */
public class FirstPdfTest {

	/**
	 * Test method for {@link com.sridama.eztrack.bo.FirstPdf#invoivepdf(java.lang.String, java.lang.String, int)}.
	 */
	@Test
	public void testInvoivepdf() {
		FirstPdf pdf = new FirstPdf();
    	pdf.invoivepdf(""+9 , "E:/",2);   //E:/SPORTSLINE_RV_SIR/eazytrack
	}

}
