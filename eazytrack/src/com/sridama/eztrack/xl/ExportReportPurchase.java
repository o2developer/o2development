/**
 * 
 */
package com.sridama.eztrack.xl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.json.simple.JSONObject;

import com.sridama.eztrack.bo.SessionManager;
import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 * 
 */
public class ExportReportPurchase {
	
	private final static Logger LOGGER = Logger.getLogger(ExportReportPurchase.class
			.getName());

	private SessionManager session;

	private String query;

	/*
	 * Invoking the session Object to know the current session
	 */
	public ExportReportPurchase(SessionManager session) {
		this.session = session;
	}

	/*
	 * takes the Filter for the XL sheet generation and generates the one
	 */
	public RequestResponse generateXl(RequestResponse req) {

		int rownum = 0;

		String[] reportHeaders = { "Invoice #", "Invoice Date", "Customer",
				"Additional", "Total Dscnt", "VAT_14.5", "VAT_5.5", "Amount" };
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("report");

		Row row1 = sheet.createRow(rownum++);
		for (int l = 0; l < reportHeaders.length; l++) {
			Cell cell = row1.createCell(l);
			cell.setCellValue(reportHeaders[l]);

		}

		getSql(req);
		ResultSet rs = executeQuery();

		try {

			// Additional Total Dscnt VAT_14.5 VAT_5.5 Amount

			float addtnl = 0.0f, tdesc = 0.0f, vat14_5 = 0.0f, vat5_5 = 0.0f, tamount = 0.0f;

			while (rs.next()) {
				Row row = sheet.createRow(rownum++);
				Cell cell = row.createCell(0);
				cell.setCellValue(rs.getString("invoice_no"));
				Cell cell1 = row.createCell(1);
				cell1.setCellValue(rs.getString("txn_date"));

				Cell cell2 = row.createCell(2);
				cell2.setCellValue(rs.getString("name"));

				Cell cell3 = row.createCell(3);
				cell3.setCellValue(rs.getString("transportation"));

				addtnl += rs.getFloat("transportation");

				Cell cell4 = row.createCell(4);
				cell4.setCellValue(rs.getString("discount"));
				tdesc += rs.getFloat("discount");

				Cell cell5 = row.createCell(5);
				cell5.setCellValue(rs.getString("totalvat_fourteen"));

				vat14_5 += rs.getFloat("totalvat_fourteen");

				Cell cell6 = row.createCell(6);
				cell6.setCellValue(rs.getString("totalvat_five"));

				vat5_5 += rs.getFloat("totalvat_five");

				Cell cell7 = row.createCell(7);
				cell7.setCellValue(rs.getString("grand_total"));

				tamount += rs.getFloat("grand_total");
			}

			rownum++;
			Row row = sheet.createRow(rownum++);
			// Adding totals row which will give the summation of all the
			// records

			Cell cell = row.createCell(0);
			cell.setCellValue(" ");

			Cell cell1 = row.createCell(1);
			cell1.setCellValue("  ");

			Cell cell2 = row.createCell(2);
			cell2.setCellValue("Total : ");

			Cell cell3 = row.createCell(3);
			cell3.setCellValue(addtnl);

			Cell cell4 = row.createCell(4);
			cell4.setCellValue(tdesc);

			Cell cell5 = row.createCell(5);
			cell5.setCellValue(vat14_5);

			Cell cell6 = row.createCell(6);
			cell5.setCellValue(vat5_5);

			Cell cell7 = row.createCell(7);
			cell7.setCellValue(tamount);

			String path = "" ,tempPath = "";
			try {
				
				 LOGGER.debug(""+req.getParam("contextPath"));
				 tempPath = "purchases_report_"+session.getBrCode()+"_"+session.getName()+"_"+System.currentTimeMillis()+".xls" ;
				 path =  req.getParam("contextPath")+"/tmp/"+tempPath ;
				 
				 LOGGER.debug("path of the xl"+path);
				FileOutputStream out = new FileOutputStream(new File(
						path));
				workbook.write(out);
				out.close();
				System.out.println("Excel written successfully..");

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			JSONObject obj = new JSONObject();
			obj.put("result", "0");
			obj.put("desc", "xl sheet has been created successfully");
			obj.put("path", tempPath );
			LOGGER.debug("Response JSON IS "+obj.toJSONString()) ;
			return new RequestResponse(obj.toJSONString());

		} catch (Exception e) {
			LOGGER.debug("Exception in purchases Report "+e);
			e.printStackTrace();
		}
		return null;
	}

	private void getSql(RequestResponse req) {

		int branch = session.getBrCode() ; 
		/*
		 *  Gives the report of the selected branch to admin
		 */
		if (req.getParam("branch")!=null && !req.getParam("branch").isEmpty() && !req.getParam("branch").equals("-1"))
    		branch = Integer.parseInt(req.getParam("branch"));
		
		
		StringBuilder sb = new StringBuilder() ;
		sb.append("select txn.txn_id, txn.invoice_no,txn.pur_invoice_no, txn.txn_date,txn.inter_state,txn.cform,txn.payment_type,");
		sb.append("txn.sub_total, txn.discount , txn.transportation ,txn.grand_total ,txn.totalvat_fourteen ,");
		sb.append(" txn.totalvat_five,cust.name,cust.cust_id,cust.phone1  from item_txn txn INNER JOIN client_master cust ");
		sb.append("ON  txn.party_id = cust.cust_id where txn.txn_code='p' AND txn.branch_id="+branch );
		

		// If dates are specified, add them to the where clause filter...
		if (req.getParam("fdate") != null && !req.getParam("tdate").isEmpty()) {
			
			sb.append(" AND DATE(txn_date)>='" + req.getParam("fdate")+"' AND DATE(txn_date)<='" + req.getParam("tdate") + "'");
			
		}
		
		// Sales report based on invoice number range  
		if (req.getParam("inv_from")!=null && !req.getParam("inv_to").isEmpty()) {
			
			sb.append(" AND invoice_no>='" + req.getParam("inv_from") + "' AND invoice_no<='" + req.getParam("inv_to") + "'");
			
		}

		// similarly if customer id is specified, narrow the filter to include
		// this customer data only.
		if (req.getParam("cust_id")!=null && !req.getParam("cust_id").isEmpty() ){
			sb.append(" AND party_id=" + req.getParam("cust_id"));
		}

		System.out.println(sb.toString());
	

		this.query = sb.toString();
	}

	private ResultSet executeQuery() {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = JDBCHelper.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			return rs;

		} catch (ClassNotFoundException | IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			String s = "0";
			if (stmt != null)
				try {
					//stmt.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			//if (conn != null) conn.close();
		}
		return null;
	}
}
