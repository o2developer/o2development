/**
 * 
 */
package com.sridama.eztrack.posprint;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sridama.eztrack.utils.JDBCHelper;

/**
 * @author Rizwan
 */
public class SalePosPrint {

	private int txnId ;
	private int branchId ;
	
	public SalePosPrint(int txnId,int branchId) {
		this.txnId = txnId ;
		this.branchId = branchId ;
	}
	
	public boolean print() throws SQLException, ClassNotFoundException,
			IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("\t Sports Line \n    Queens Road, Bangalore \n");
		sb.append("\t Sale Receipt \n");

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
		Date date = new Date();
		String Date = dateFormat.format(date);
		String Time = timeFormat.format(date);
		sb.append(" Date : " + Date + "\n");
		sb.append(" Time : " + Time + "\n");
		sb.append(String.format("%1s%20s%3s%6s", "#  ", "itemname  ", "qty  ",
				"amount  "));
		sb.append("\n");
		ResultSet rs = getDataSql(0);

		int i = 1;
		float totalAmount = 0.0f;
		while (rs.next()) {
			totalAmount += rs.getFloat("amount");
			sb.append(String.format("%2s%20s%3s%6s", i + "  ",
					rs.getString("name") + i + "  ", rs.getString("quantity")
							+ "  ", rs.getFloat("amount") + "  ")
					+ "\n");
			i++;
		}

		ResultSet rs1 = getDataSql(1);
		while (rs1.next())
			sb.append("\n  Total Vat@"
					+ (String.format("%.02f", rs1.getFloat(1)) + "%  :" + String
							.format("%.02f", rs1.getFloat(2))));

		sb.append("\n  Total Amount    : " + totalAmount + "\n");
		sb.append("\n  Authorized Signature : ________________  \n");
		sb.append("\n  ******* Thank You Visit Again *********");

		//System.out.println(sb.toString());
		return false;
	}
	
	
	/*
	 * Gives the sql for getting item details of the particular transaction
	 */
	private ResultSet getDataSql(int choice) throws ClassNotFoundException,
			IOException {

		ResultSet rs = null;
		try {
			Connection con = JDBCHelper.getConnection();
			Statement stmt = con.createStatement();

			StringBuilder sb = new StringBuilder();

			if (choice == 0) {
				sb.append("select   txn.item,txn.quantity,txn.discount,txn.amount,txn.rate," +
						"txn.dscnt_percent,txn.vat_percent,txn.catagory ");
				sb.append(", item.name from txn_details txn INNER JOIN item_master item ON txn.item=item.id  where txn.txnid="
						+ txnId);
			}
			if (choice == 1) {
				sb.append("select vat_percent , sum(rate*vat_percent/100) from txn_details where txnid="
						+ txnId + " group by vat_percent");
			}

			// System.out.println(sb.toString());
			rs = stmt.executeQuery(sb.toString());

			return rs;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * @param 
	 * Main method for testing pos print format
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		// TODO Auto-generated method stub
		SalePosPrint pos = new SalePosPrint(16,1);
		pos.print();
	}

}
