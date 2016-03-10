/**
 * 
 */
package com.sridama.eztrack.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.sridama.eztrack.bo.PDFStockTransfer;

/**
 * @author Rizwan
 *
 */
public class PDFStockTransferTest {

	@Test
	public void test4PDFGenerationOfTransfer() {
		PDFStockTransfer pdf = new PDFStockTransfer();
		pdf.invoivepdf(""+12, "E:", 1,12) ;
	}

}
