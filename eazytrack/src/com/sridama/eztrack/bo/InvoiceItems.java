package com.sridama.eztrack.bo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sridama.eztrack.utils.JDBCHelper;

public class InvoiceItems {
	
	 private int  txnId ;
	 private int  itemId ;
	 private int  qty ;
	 private double discount ;
	 private double amount ;
	 private double rate ;
	 private double disPercent ;
	 private double vatPercent ;
	 private int category ;
	 
	 private int brCode =  0 ;
	 
	 private String txnType ;
	 
	 private int toBranch ;
	 
	 // to update the stock for to branch
	 public void setToBranch(int toBranch) {
		this.toBranch = toBranch;
	}


	// to update the stock appropriately
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}


	public void setBrCode(int brCode) {
			this.brCode = brCode;
	}


	/*
 * Initializing all class variables 	 
 */
	public InvoiceItems(String json) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(json);
		this.itemId = Integer.parseInt(obj.get("item_id").toString());
		this.qty = Integer.parseInt(obj.get("qty").toString());
		this.discount = Double.parseDouble(obj.get("discount").toString());
		this.rate = Double.parseDouble(obj.get("rate").toString());
		// this.amount = Double.parseDouble(obj.get("amount").toString());

		this.disPercent = Double.parseDouble(obj.get("dsc_p").toString());
		this.vatPercent = Double.parseDouble(obj.get("vat_p").toString());
		this.category = Integer.parseInt(obj.get("category").toString());

		// Calculating the amount with out taxes
		double discount1 = (rate * qty) * (disPercent / 100);
		double tamountWithOutTax = ((rate * qty) - discount1)
				/ (1 + (vatPercent / 100));
		this.amount = tamountWithOutTax;
		this.discount = discount1;

	}
	
	
	public void setTxnId(int txnId) {
		this.txnId = txnId;
	}


	/*
	 * saves particular item to txn_details table
	 */
	public void save (Connection con) throws SQLException {
		
		Statement stmt = null ;
		StringBuilder sb = new StringBuilder() ;
		sb.append("insert into txn_details values (");
		sb.append(txnId+",");
		sb.append(itemId+",");
		sb.append(qty+",");
		sb.append(discount+",");
		sb.append(amount+",");    //amount = qty*rate
		sb.append(rate+",");
		sb.append(disPercent+",");
		sb.append(vatPercent+",");
		sb.append(category+")") ;
		try {
			stmt = con.createStatement() ;
			stmt.execute(sb.toString()) ;
		}finally {
			if (stmt!=null) stmt.close();
		}
			
	}
	
/*
 *  updates the stock of an item after the sale
 */
	public void updateStock(Connection con) throws SQLException {
		Statement stmt = null;
		StringBuilder sb = new StringBuilder();

		if (txnType.equalsIgnoreCase("s") ) {
			sb.append("update item_stock set stock=(stock-" + qty + ")");
		} else if (txnType.equalsIgnoreCase("p")) {
			sb.append("update item_stock set stock=(stock+" + qty + ")");
		}
		sb.append("where itemid=" + itemId);
		sb.append(" AND branchcode=" + brCode);
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sb.toString());
			
		} finally {
			if (stmt != null)stmt.close();
		}
	}
}
