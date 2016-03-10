/**
 * 
 */
package com.sridama.eztrack.bo;

import org.json.simple.JSONObject;

import com.sridama.eztrack.bo.FirstPdf;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 *
 */
public class PDFPrintSaleReport {
	
	SessionManager session = null ;
	
	
	public PDFPrintSaleReport (SessionManager session ) {
		this.session = session ;
	}

	 public RequestResponse  generatePDF(RequestResponse req) {
		
		 FirstPdf pdf = new FirstPdf();
		 pdf.invoivepdf(""+req.getParam("invoice_no") , req.getParam("contextPath") , session.getBrCode() );
		 
		 JSONObject res = new JSONObject();
			res.put("result", 0);
			res.put("desc", "Pdf generation is  Successful !");
			
			return createResponse(res);
		 
	 }
		 
		/**
		 * Internal helper method that wraps a given object within a response
		 * object.
		 */
		private RequestResponse createResponse(JSONObject o) {
			return new RequestResponse(o.toJSONString());
		}
		 

}
