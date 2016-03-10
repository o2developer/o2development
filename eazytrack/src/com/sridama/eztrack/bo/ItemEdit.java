package com.sridama.eztrack.bo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.eztrack.utils.Utils;
import com.sridama.txngw.core.RequestResponse;

public class ItemEdit extends Report {

	
	private int id;
	private int item_id;
	private String name;
	private int code;
	private int category;
	private int units;
	private float rate;
	private int opBalance;    // same value will be updated to stock of item_stock
	private String cat_name;
	private JSONArray opBalances ;
	private float cost;
//	private float taxes;
	private int disPercentage ;
	private int taxSlab ;
	private SessionManager ses ;

	public ItemEdit(SessionManager ses) {
		super(ses);
		// TODO Auto-generated constructor stub
	}
	public void  loadValues(RequestResponse req ) {
		
		if (req.getParam("id")!=null && !(req.getParam("id").isEmpty()))
				this.id = Integer.parseInt(req.getParam("id"));
		if (req.getParam("name")!=null)
			this.name =  req.getParam("name");
		if (req.getParam("code")!=null && !(req.getParam("code").isEmpty()))
			this.code = Integer.parseInt(req.getParam("code"));
		if (req.getParam("category")!=null && !(req.getParam("category").isEmpty()))
			this.category = Integer.parseInt(req.getParam("category"));
		if (req.getParam("units")!=null && !req.getParam("units").isEmpty() && !req.getParam("units").trim().equals("-1")) {
			this.units =  Integer.parseInt(req.getParam("units"));
		  } else if (req.getParam("units").equals("-1")) {
			  Units unit = new Units(req.getParam("units_val"));
			  unit.save();
			  this.units = unit.getId();
		  }
		if (req.getParam("rate")!=null && !(req.getParam("rate").isEmpty()))
			this.rate = Float.parseFloat(req.getParam("rate"));
		if (req.getParam("op_bal")!=null && !(req.getParam("op_bal").isEmpty())) {
		    JSONParser parser = new JSONParser();
		    try {
				this.opBalances = (JSONArray) parser.parse(req.getParam("op_bal"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (req.getParam("cost")!=null && !(req.getParam("cost").isEmpty()))
			this.cost = Float.parseFloat(req.getParam("cost"));
	
		if (req.getParam("discnt_percent")!=null && !req.getParam("discnt_percent").isEmpty() && !(req.getParam("discnt_percent").trim().equals("-1"))){
			this.disPercentage = Integer.parseInt(req.getParam("discnt_percent"));
		}else if (req.getParam("discnt_percent").equals("-1")) {
			DiscountSlab disc = new DiscountSlab(Float.parseFloat(req.getParam("discnt_percent_val")));
			disc.save();
			this.disPercentage = disc.getId();
		}
		System.out.println(!req.getParam("tax_slab").trim().equals("-1"));
		if (req.getParam("tax_slab")!=null && !req.getParam("tax_slab").isEmpty() && !req.getParam("tax_slab").equals("-1")){
			this.taxSlab = Integer.parseInt(req.getParam("tax_slab"));
		}else if (req.getParam("tax_slab").equals("-1")){
		TaxSlab tax = new TaxSlab(Float.parseFloat(req.getParam("tax_slab_val")));
		tax.save();
		this.taxSlab = tax.getId() ;
	  }
			
	}

	@Override
	protected JSONArray toJSON(ResultSet rs) throws SQLException {

		final JSONArray resJsonSend = new JSONArray();
		while (rs.next())
		{
			final JSONObject resJson = new JSONObject();
			resJson.put("code", rs.getInt("code"));
			resJson.put("name", rs.getString("name"));
			resJson.put("category", rs.getInt("category"));
			resJson.put("units", rs.getString("units"));
			resJson.put("rate", rs.getFloat("rate"));
		//	resJson.put("op_bal", rs.getInt("op_bal"));
			resJson.put("cost", rs.getFloat("cost"));
			resJson.put("discnt_percent", rs.getFloat("discnt_percent"));
			resJson.put("tax_slab", rs.getFloat("tax_slab"));
		//	resJson.put("taxes", rs.getInt("taxes"));
		//	resJson.put("remarks", rs.getString("remarks"));

			try {
				resJson.put("op_bal", getOpBalances(rs.getInt("id")));
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			resJsonSend.add(resJson);
		}
		System.out.println("Result Json"+resJsonSend.toString());
		
		return resJsonSend;

	}
	
	
	/*
	 * Gives the json of opbalances
	 */
	private JSONArray getOpBalances(int itemid) throws ClassNotFoundException, IOException {
		JSONArray res = new JSONArray();
		ResultSet rs = null;
		Connection con = null;
		Statement stmt = null;
		try {
			con = JDBCHelper.getConnection();
			rs = null;
			stmt = con.createStatement();
			StringBuilder sb = new StringBuilder();
			sb.append(" select  branchcode, stock from item_stock where itemid="+itemid);
			rs = stmt.executeQuery(sb.toString());

		  while (rs.next()) {
				JSONObject obj = new JSONObject();
				obj.put( "branch", rs.getInt("branchcode"));
				obj.put("stock", rs.getInt("stock"));
				res.add(obj);
			} // End of while
			return res ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return null ;
	}
	@Override
	protected String getSQL(RequestResponse req) {
		
		 StringBuilder sb = new StringBuilder();
	
		sb.append ("select item.name,item.id, item.code , item.category , ");
		sb.append ("item.units,item.rate,item.cost ,item.discnt_percent,item.tax_slab ,item.last_modified_by,item.last_modified_date ") ;
		sb.append(" from item_master item where  item.id="+req.getParam("item_id") );
		
		
		System.out.println("Qzdasdasd "+sb.toString());

		return sb.toString();
		}
	@Override
	protected String getReportSortField(RequestResponse req) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	protected String getReportFilter(RequestResponse req) {
		// TODO Auto-generated method stub
		return "";
	}
	@Override
	protected String getOrderbyString(RequestResponse req) {
		// TODO Auto-generated method stub
		return "";
	}

}
