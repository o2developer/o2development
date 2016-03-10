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
import com.sridama.eztrack.utils.Utils;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 * 
 */
public class ExportReportStockTransfer {
	
	private final static Logger LOGGER = Logger.getLogger(ExportReportStockTransfer.class
			.getName());

	private SessionManager session;

	private String query;

	/*
	 * Invoking the session Object to know the current session
	 */
	public ExportReportStockTransfer(SessionManager session) {
		this.session = session;
	}

	/*
	 * takes the Filter for the XL sheet generation and generates the one
	 */
	public RequestResponse generateXl(RequestResponse req) {

		int rownum = 0; 
//		 # 	Date 	DC Number 	Description 	Amount 	Status
		String[] reportHeaders = { "DC #", " Date " , "Description", "Amount" } ;
		
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

			float  tamount = 0.0f;

			while ( rs.next() ) {
				Row row  =  sheet.createRow ( rownum++ );
				
				Cell cell = row.createCell(0);
				cell.setCellValue(rs.getString("dc_no"));
				
				Cell cell1 = row.createCell(1);
				cell1.setCellValue(rs.getString("txn_date"));

				
				Cell cell2 = row.createCell(2);
				if ( rs.getInt("from_branch") == session.getBrCode() ) {
					//obj.put("action", "inword");
					cell2.setCellValue(" To branch "+Utils.getBranchName(rs.getInt("to_branch")));
				} else if (rs.getInt("to_branch") == session.getBrCode()) {
					//obj.put("action", "inword");
					cell2.setCellValue(" From branch "+Utils.getBranchName(rs.getInt("from_branch")));
				}
				
				
				

				Cell cell3 = row.createCell(3);
				cell3.setCellValue(rs.getString("amount"));

				tamount += rs.getFloat("amount");

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
			cell3.setCellValue(tamount);

			String tempPath = "" , path = "" ;

			try {
				 LOGGER.debug(""+req.getParam("contextPath"));
				 tempPath = "stock_xfr_report_"+session.getBrCode()+"_"+session.getName()+"_"+System.currentTimeMillis()+".xls" ;
				 path =  req.getParam("contextPath")+"/tmp/"+tempPath ;
				 
				 LOGGER.debug("path of the xl"+path);
				 
				    FileOutputStream out =
				            new FileOutputStream(new File(path ));
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
			 obj.put("path", tempPath ) ;
			return new RequestResponse(obj.toJSONString());

		} catch (Exception e) {
			LOGGER.debug("Stock transfer report Export Xl Exception \n"+e);
			e.printStackTrace();
		}
		return null;
	}

	private void getSql(RequestResponse req) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("select txn_id , dc_no , txn_date, from_branch , to_branch , status , amount from stock_txn where ");
		
		
		/*
		 *  filtering the search when to_branch is mentioned
		 */
		if ( req.getParam("to_branch")!=null && !req.getParam("to_branch").isEmpty() )
			sb.append("   to_branch="+req.getParam("to_branch")+" AND from_branch = "+session.getBrCode() );
		
		else if ( req.getParam("from_branch")!=null && !req.getParam("from_branch").isEmpty() )
			sb.append("   to_branch="+session.getBrCode()+" AND from_branch="+req.getParam("from_branch") );
		
		else 
			sb.append("   to_branch="+session.getBrCode()+" OR from_branch = "+session.getBrCode()  );
		
		if ( req.getParam("fdate")!=null   && req.getParam("tdate")!=null )
			sb.append(" AND  txn_date >="+req.getParam("fdate")+"  AND txn_date <="+req.getParam("tdate") );
		
		sb.append(" ORDER BY txn_date ");
		
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
				//	stmt.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			// if (conn != null) conn.close();
		}
		return null;
	}
}
