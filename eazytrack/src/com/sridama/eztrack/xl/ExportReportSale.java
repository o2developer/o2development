/**
 *    To Export the Report filter to XL sheet , this will receive the filter content from the 
 *    front End the Generates the XL sheet Based on the user Filter .
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
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sridama.eztrack.bo.SessionManager;
import com.sridama.eztrack.controller.EzTrackServlet;
import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.eztrack.utils.Utils;
import com.sridama.txngw.core.RequestResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;

/**
 * @author Rizwan
 */
public class ExportReportSale {
	
	private final static Logger LOGGER = Logger.getLogger(ExportReportSale.class
			.getName());
                 
	private SessionManager session ;
	
	private String query ;
	
	private String filterStirng ;
	
	
	/*
	 *  Invoking the session Object to know the current session
	 */
	 public ExportReportSale (SessionManager session)  {
		  this.session = session ;
	 }

	 /*
	  * takes the Filter for the XL sheet generation and generates the one
	  */
	 public RequestResponse  generateXl ( RequestResponse req ) {
		
		 
		
		 getSql(req);   // to generate the query for filteirng if specified 
		 
		 
		 
		 LOGGER.debug(" Inside the Generate XL method "+req.getParam("contextPath"));
		 
		 
			final short align = 9;

			LOGGER.debug(" Inside the Generate XL method " + req.getParam("contextPath"));

			int rownum = 4;

		
			final String[] reportHeaders = { "Invoice #", "Invoice Date", "Customer", "Additional", "Total Dscnt", "VAT_14.5", "VAT_5.5", "Amount" };
			final HSSFWorkbook workbook = new HSSFWorkbook();
			final HSSFSheet sheet = workbook.createSheet("report");
			sheet.addMergedRegion(new CellRangeAddress(0, //first row (0-based)
					2, //last row  (0-based)
					0, //first column (0-based)
					7 //last column  (0-based)
			));
			final Row rowbor = sheet.createRow(3);
			final Cell cellbor = rowbor.createCell(0);
			cellbor.setCellValue("________________________________________________________________________");

			final Row rowj = sheet.createRow(4);
			final Cell cellj = rowj.createCell(0);
			final HSSFFont hSSFFont = workbook.createFont();
		
			cellj.setCellValue( filterStirng );    // Dispaying the Filter criteria
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle = workbook.createCellStyle();
			cellStyle.setAlignment(align);
			sheet.addMergedRegion(new CellRangeAddress(4, //first row (0-based)
					6, //last row  (0-based)
					0, //first column (0-based)
					7//last column  (0-based)
			));

		
			final Row rowi = sheet.createRow(0);
			final Cell celli = rowi.createCell(0);
			celli.setCellStyle(cellStyle);
			cellj.setCellStyle(cellStyle);
			final SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");//dd/MM/yyyy
			final Date now = new Date();
			final String strDate = sdfDate.format(now);
			celli.setCellValue("Date : "+ strDate);
			final Row rowm = sheet.createRow(7);
			final Cell cellm = rowm.createCell(0);
			cellm.setCellValue("________________________________________________________________________");
		

			rownum = 8;
			final Row row1 = sheet.createRow(rownum++);
			for (int l = 0; l < reportHeaders.length; l++)
			{
				final Cell cell1 = row1.createCell(l);
				cell1.setCellValue(reportHeaders[l]);

			}
			rownum = 10;
			final Row row10 = sheet.createRow(9);
			final Cell cell10 = row10.createCell(0);
			cell10.setCellValue("________________________________________________________________________");
			
			ResultSet rs = executeQuery();
		 
		 try {
			 
//			 Additional	Total Dscnt	VAT_14.5	VAT_5.5	Amount

	float 		 addtnl=0.0f , tdesc=0.0f, vat14_5=0.0f , vat5_5=0.0f , tamount=0.0f ;
			 
		 while ( rs.next() ) {
			 Row row = sheet.createRow(rownum++);
			 Cell cell = row.createCell(0);
	            cell.setCellValue( rs.getString("invoice_no") );
	            Cell cell1 = row.createCell(1);
	            cell1.setCellValue( rs.getString("txn_date") );
	            
	            Cell cell2 = row.createCell(2);
	            cell2.setCellValue( rs.getString("name") );

	            Cell cell3 = row.createCell(3);
	            cell3.setCellValue( rs.getString("transportation") );
	            
	            addtnl+= rs.getFloat("transportation");
	            

	            Cell cell4 = row.createCell(4);
	            cell4.setCellValue( rs.getString("discount") );
	            tdesc += rs.getFloat("discount") ;

	            Cell cell5 = row.createCell(5);
	            cell5.setCellValue( rs.getString("totalvat_fourteen") );
	            
	            vat14_5 += rs.getFloat("totalvat_fourteen");
	            
	            Cell cell6 = row.createCell(6);
	            cell6.setCellValue( rs.getString("totalvat_five") );
	            
	            vat5_5 += rs.getFloat("totalvat_five");

	            Cell cell7 = row.createCell(7);
	            cell7.setCellValue( rs.getString("grand_total") );
			 
	            tamount += rs.getFloat("grand_total");
		 }
		 
		 rownum++;
		 Row row = sheet.createRow(rownum++);
		 // Adding totals row which will give the summation of all  the records
		 
		 Cell cell = row.createCell(0);
         cell.setCellValue( " " );
      
         Cell cell1 = row.createCell(1);
         cell1.setCellValue( "  " );
         
         Cell cell2 = row.createCell(2);
         cell2.setCellValue( "Total : " );
         
         Cell cell3 = row.createCell(3);
         cell3.setCellValue( addtnl );
         

         Cell cell4 = row.createCell(4);
         cell4.setCellValue( tdesc );
         

         Cell cell5 = row.createCell(5);
         cell5.setCellValue( vat14_5 );
         

         Cell cell6 = row.createCell(6);
         cell5.setCellValue( vat5_5 );
         

         Cell cell7 = row.createCell(7);
         cell7.setCellValue( tamount );
		 
         
         String path = "" ;
         String tempPath = "" ;
		 
		 try {
		/*	 
			 new FileOutputStream(path + "/tmp/"
						+"inv_"+ invoiceno + "_" + brCode + ".pdf")
			 */
			 
			 LOGGER.debug(""+req.getParam("contextPath"));
			 tempPath = "sales_report_"+session.getBrCode()+"_"+session.getName()+"_"+System.currentTimeMillis()+".xls" ;
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
		 
		 JSONObject  obj =  new JSONObject();
		 obj.put("result", "0");
		 obj.put("desc" , "xl sheet has been created successfully");
		 obj.put("path", tempPath ) ;
		 return new RequestResponse(obj.toJSONString()) ;
		 
		 }catch (Exception e) {
			 
			 LOGGER.debug("Exception Inside the export result"+e);
			 e.printStackTrace() ;
		 }
		return null ;
	 }
	 
	 
	 private void getSql(RequestResponse req) {
		 
		 // to build the filter string to display on top of XL
		 StringBuilder filter = new StringBuilder();
		 filter.append("Filtered the Sales Report of Branch "+Utils.getBranchName(session.getBrCode())+"\n");
			
		 int branch = session.getBrCode() ; 
			/*
			 *  Gives the report of the selected branch to admin
			 */
			if (req.getParam("branch")!=null && !req.getParam("branch").isEmpty() && !req.getParam("branch").equals("-1"))
	    		branch = Integer.parseInt(req.getParam("branch"));
			
			
			StringBuilder sb = new StringBuilder() ;
			sb.append("select txn.txn_id, txn.invoice_no, txn.txn_date,txn.inter_state,txn.cform,txn.payment_type,");
			sb.append("txn.sub_total, txn.discount , txn.transportation ,txn.grand_total ,txn.totalvat_fourteen ,");
			sb.append(" txn.totalvat_five,cust.name,cust.cust_id,cust.phone1  from item_txn txn INNER JOIN client_master cust ");
			sb.append("ON  txn.party_id = cust.cust_id where txn.txn_code='s' AND txn.branch_id="+branch );
			
			
			// Sales report based on invoice number range  
			if (req.getParam("inv_from")!=null && !req.getParam("inv_to").isEmpty()) {
				
				sb.append(" AND invoice_no>='" + req.getParam("inv_from") + "' AND invoice_no<='" + req.getParam("inv_to") + "'");
				filter.append("Invoice Number from "+req.getParam("inv_from")+"  to "+req.getParam("inv_to")+"\n");
				
			}

			// similarly if customer id is specified, narrow the filter to include
			// this customer data only.
			if (req.getParam("cust_id")!=null && !req.getParam("cust_id").isEmpty() ){
				sb.append(" AND party_id=" + req.getParam("cust_id"));
				filter.append(" Customer ID "+req.getParam("cust_id")+"\n");
			}
			

			// If dates are specified, add them to the where clause filter...
			if (req.getParam("fdate") != null && !req.getParam("tdate").isEmpty()) {
				
				sb.append(" AND DATE(txn_date)>='" + req.getParam("fdate")+"' AND DATE(txn_date)<='" + req.getParam("tdate") + "'");
				filter.append("Between Dates "+req.getParam("fdate") +"  To  "+req.getParam("tdate")+"\n");
				
			} else {
				// If by default the current months transactions will be exported 
				sb.append (" AND YEAR(txn_date) = YEAR(CURDATE()) AND MONTH(txn_date)=MONTH(CURDATE()) ORDER BY txn_date DESC ");	
				filter.append("Sales Report for the month of  March");
			}
			
			this.filterStirng = filter.toString() ;

			System.out.println(sb.toString());
			
			
			this.query =  sb.toString() ;
		}
	 
	 
	 private ResultSet  executeQuery() {
			
			Connection conn = null;
			Statement  stmt = null;
			ResultSet  rs   = null;
			

				try {
					conn = JDBCHelper.getConnection();
					stmt = conn.createStatement();
					rs = stmt.executeQuery(query);
					
					return rs ;
			
				} catch (ClassNotFoundException | IOException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally {
					//	if (stmt != null) stmt.close();
	if (conn != null)
		try {
			//conn.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				}
			return null;
		}
}
