/**
 * 
 */
package com.sridama.eztrack.bo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sridama.txngw.core.RequestResponse;

/**
 * @author admin
 *
 */
public class StockReport extends Report {
	private final static Logger LOGGER = Logger.getLogger(StockReport.class
			.getName());
	
	 
	
	//private HashMap<String,String> qmap = new HashMap<String,String>() ;
	
	private String[] colsArray = {
			"category_master.cat_name",
			"item_master.code",
			"item_master.name",
			"",
			"",
			"",
			""
			};

	public StockReport(SessionManager s) {
		super(s);
	}
	
	
	
	
/*
 * (non-Javadoc)
 * this methos will fetch the stock details from database and returns of array of items
 */
	@Override
	protected JSONArray toJSON(ResultSet rs) throws SQLException {
		JSONArray resJsonSend = new JSONArray();
		JSONObject objtot = new JSONObject();
		
		while (rs.next()) {
			JSONObject obj = new JSONObject() ;
			obj.put("id", ""+rs.getInt("id"));
			obj.put("name",rs.getString("name"));
			obj.put("category",rs.getString("cat_name"));
			obj.put("modal",rs.getString("modal"));
			obj.put("units",rs.getString("units"));
			obj.put("rate",""+rs.getFloat("rate"));
			
			totalAmount += totalAmount + (rs.getFloat("rate") * rs.getInt("stock") );
			
			obj.put("cost",rs.getFloat("cost"));
			obj.put("qty",""+rs.getInt("stock"));
			obj.put("discnt_percent", ""+rs.getDouble("disc"));   // discout from discount master
			
			obj.put("catid", ""+rs.getInt("category")) ;
			obj.put("vat", ""+rs.getFloat("rate1")) ;  // tax rate from tax_master
			obj.put("code", ""+rs.getInt("code")) ;
			obj.put("taxes", ""+rs.getFloat("rate1")) ; // tax rate from tax_master 
			
		   obj.put( "value" , rs.getString("name") );
			JSONArray tokenArray =  new JSONArray() ;
			StringTokenizer st = new StringTokenizer(rs.getString("name"));
			while (st.hasMoreTokens())
			       tokenArray.add( ""+st.nextElement());
			
			StringTokenizer stcat = new StringTokenizer(rs.getString("cat_name"));
			while (stcat.hasMoreTokens())
			       tokenArray.add(""+stcat.nextElement());
			
			
			tokenArray.add(""+rs.getInt("code"));
			
			obj.put("tokens", tokenArray );

			
			resJsonSend.add(obj) ;
		}
		LOGGER.debug("Response for Item list or Stock Report  "+resJsonSend.toJSONString());
		objtot.put("total_amount", ""+totalAmount);
		return resJsonSend ;
	}

  
	/*
	value : "name"
	tokens : [ "name" , "cat" , code ]
			 [ "MRF", "bat" , "" ]
	*/
	@Override
	protected String getSQL(RequestResponse req) {
		
		// calling function to get the filter  from the query string
		//queryStringParse(req.getParam("queryString")) ;
		
		StringBuilder  sb = new StringBuilder() ;
		sb.append("SELECT item_master.id , item_master.cost, item_master.category,item_master.code, item_master.name,category_master.cat_name ,");
		sb.append("item_master.modal,unit_master.name as units,item_master.rate ,discount_master.disc , tax_master.rate as rate1 ,item_master.taxes,");
		sb.append(" item_stock.stock FROM item_master INNER JOIN item_stock ");
		sb.append(" ON item_master.id=item_stock.itemid  ");
		sb.append(" INNER JOIN category_master ON ");
		sb.append(" item_master.category=category_master.category_id  INNER JOIN tax_master ON item_master.tax_slab = tax_master.id ");
		sb.append(" INNER JOIN discount_master  ON  item_master.discnt_percent = discount_master.id " );
		sb.append(" INNER JOIN unit_master ON item_master.units = unit_master.id ");		
		sb.append( " where branchcode ="+session.getBrCode() );		
		
	    
		// When type ahead string is specified then only , this will filter other wise full report will be given 
	  if ( req.getParam("type_ahead")!=null && !(req.getParam("type_ahead").isEmpty())  )   {
				sb.append(" AND  ( item_master.name like '"+req.getParam("type_ahead")+"%' OR " );
				sb.append(" item_master.code like '"+req.getParam("type_ahead")+"%' OR category_master.cat_name like '"+req.getParam("type_ahead")+"%')"  );
		}
		
		
		
		/*if ( req.getParam("category_name")!=null ) {
		sb.append(" AND cat_name='"+req.getParam("category_name").trim()+"'");
		}*/
		
		
		LOGGER.debug("SQL Query for list  of items [stock] "+sb.toString());
		//System.out.println(sb.toString());
		return sb.toString();
	}


	@Override
	protected String getReportSortField(RequestResponse req) {
		// TODO Auto-generated method stub
		return "";
	}


	@Override
	protected String getReportFilter(RequestResponse req) {

		// find out if a search string was given.
		// if yes, loop through all columns that have bSearchable = true
		// and then create a search sql expression and return.
		String filterString = qmap.get("sSearch");
		//System.out.println(filterString);
		if (filterString == null || filterString.isEmpty())
			return "";
		
		String sqlSearch = "";
		
		int numSearchCols = Integer.parseInt(qmap.get("iColumns"));
		boolean orFlag = false;
		for (int i=0; i<numSearchCols; i++) {
			String key = "bSearchable_" + i;
			boolean bSearch = qmap.get(key).equalsIgnoreCase("true")?true:false;
			if (bSearch) {
				if (!orFlag) {
					//sqlSearch += " OR ";
					orFlag = true;
				} else {
					sqlSearch += " OR ";
				}
				LOGGER.debug("Filter String        \n\n\n    "+filterString);
				if (filterString.trim().equals("undefined"))
					filterString = "" ;
				filterString =	filterString.contains("+") ? filterString.replace("+", " ") :filterString ;
				sqlSearch += " " + colsArray[i] + " LIKE '" +"%"+ filterString + "%'";
			}
		}
		
		
		return sqlSearch;
	}




	@Override
	protected String getOrderbyString(RequestResponse req) {
		// TODO Auto-generated method stub
		return "";
	}	

}
