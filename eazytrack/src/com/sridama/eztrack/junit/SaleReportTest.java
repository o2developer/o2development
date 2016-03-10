/**
 * 
 */
package com.sridama.eztrack.junit;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Test;

import com.sridama.eztrack.bo.InvoiceNoListSale;
import com.sridama.eztrack.bo.Report;
import com.sridama.eztrack.bo.SaleReport;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class SaleReportTest {

	/**
	 * Test method for {@link com.sridama.eztrack.bo.Report#generateReport(com.sridama.txngw.core.RequestResponse)}.
	 * @throws IOException 
	 */
	@Test
	public void testGenerateReport() throws IOException {
		
		FileReader fr = new FileReader("resources/sale_report") ;   //  sale_invoice_reject.json
		BufferedReader br = new BufferedReader(fr);

		String line = "" ;
		StringBuilder sb = new StringBuilder();
		while ( (line = br.readLine())!=null) {
			sb.append( line );
		}
		RequestResponse req = new RequestResponse("{\"branch\":\"-1\"}");
		req.setParam("opcode", "4") ;
		req.setParam("queryString", "opcode=1&data={\"branch\":\"-1\"}&sEcho=1&iColumns=9&sColumns=&iDisplayStart=0&iDisplayLength=10&mDataProp_0=0&mDataProp_1=1&mDataProp_2=2&mDataProp_3=3&mDataProp_4=4&mDataProp_5=5&mDataProp_6=6&mDataProp_7=7&mDataProp_8=function&sSearch=2013-12-31&bRegex=false&sSearch_0=&bRegex_0=false&bSearchable_0=false&sSearch_1=&bRegex_1=false&bSearchable_1=true&sSearch_2=&bRegex_2=false&bSearchable_2=true&sSearch_3=&bRegex_3=false&bSearchable_3=true&sSearch_4=&bRegex_4=false&bSearchable_4=false&sSearch_5=&bRegex_5=false&bSearchable_5=false&sSearch_6=&bRegex_6=false&bSearchable_6=false&sSearch_7=&bRegex_7=false&bSearchable_7=false&sSearch_8=&bRegex_8=false&bSearchable_8=false&iSortCol_0=1&sSortDir_0=asc&iSortingCols=1&bSortable_0=true&bSortable_1=true&bSortable_2=true&bSortable_3=true&bSortable_4=true&bSortable_5=true&bSortable_6=true&bSortable_7=true&bSortable_8=true&undefined=" ) ;
		Report rpt = new SaleReport(new SessionManager("session_id","admin", 2)) ;
		RequestResponse res = rpt.generateReport(req) ;
		String json = res.getParam("request") ;
		System.out.println(json);
		assertTrue("response cannot be null",res.getParam("request")!=null);
	}
}
