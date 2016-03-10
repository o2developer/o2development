/**
 * 
 */
package com.sridama.eztrack.bo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sridama.eztrack.bean.ItemTxn;
import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.txngw.core.RequestResponse;

/**
 *  @author Rizwan
 * 	from date opening balance , and the current stock needs to be found 
 */
public class ItemStockDetailedReport {

	private SessionManager session ;
	
	private int opBal ;
	
	private ArrayList<ItemTxn>  txnList  = new ArrayList<ItemTxn>() ;
	
	
	public ItemStockDetailedReport(SessionManager session) {
		this.session = session ;
	}
	
	/*
	 * to get  the opening balance of the year
	 */
	private void origOpBal(ResultSet rs) throws SQLException  {
		if (rs.next())
			this.opBal = rs.getInt(1) ;
		
		//System.out.println("Original Opening Balance "+opBal );
	}
	
	/*
	 *   computes the opening balance
	 */
	private void computeOpBalance(ResultSet rs ) throws SQLException {
		
	  while ( rs.next() )  {
		  if (rs.getString("txn_code").equals("s"))
			    opBal -= rs.getInt("qty") ;
		  else if (rs.getString("txn_code").equals("p"))
			    opBal += rs.getInt("qty");
	  } // End of while	
	}
	
	
	/*
	 *  computes the Opening Balance
	 *  if branch is from the 0
	 */
	private void computeOpBalance(ResultSet rs,int type ) throws SQLException {
	  if ( rs.next() )  {
		           if (type == 1 )
		        	   opBal += rs.getInt(1);
		           else 
		        	   opBal -= rs.getInt(1);
		 	  } // End of while	
	}

	
	
	private void getOpBal(RequestResponse req) {

		Connection conn = null ;
		Statement stmt = null ;
		ResultSet rs = null ;

		try {
			conn = JDBCHelper.getConnection();
			stmt = conn.createStatement();
			
		// TODO to get opening balance as on date
		StringBuilder sb = new StringBuilder();
		sb.append("select  txn_code , sum(quantity) as qty  from txn_details INNER JOIN item_txn ON ");
		sb.append(" txn_details.txnid = item_txn.txn_id  where item="+req.getParam("item_id")+" AND branch_id="+session.getBrCode());
		if ( req.getParam("fdate")!=null && !req.getParam("fdate").isEmpty() )
		   sb.append(" AND DATE(txn_date) <='"+req.getParam("fdate")+"'  ") ;
		sb.append("  group by txn_code");

		//System.out.println( "Opening Balance Query "+  sb.toString() );
		
		rs = stmt.executeQuery( sb.toString());
        computeOpBalance ( rs ) ;
		
		
		StringBuilder sb1 = new StringBuilder();
		//stock transfer in to this branch
		sb1.append("select  sum(aqty) as qty from stock_txn_details INNER JOIN  stock_txn ON " );
		sb1.append("stock_txn_details.stxnid = stock_txn.txn_id where to_branch ="+session.getBrCode());
		sb1.append(" AND sitemid="+req.getParam("item_id")+"");
		if ( req.getParam("fdate")!=null && !req.getParam("fdate").isEmpty() )
			   sb1.append(" AND DATE(txn_date) <='"+req.getParam("fdate")+"'  ") ;
		
		//System.out.println(sb1.toString());
		rs = stmt.executeQuery(sb1.toString());
        computeOpBalance ( rs,1 ) ;
		
			
		StringBuilder sb2 = new StringBuilder();
		//stock transfer from this branch
		sb2.append("select sum(aqty) as qty from stock_txn_details INNER JOIN  stock_txn ON " );
		sb2.append("stock_txn_details.stxnid = stock_txn.txn_id where from_branch ="+session.getBrCode());
		sb2.append(" AND sitemid="+req.getParam("item_id")+"");
		if ( req.getParam("fdate")!=null && !req.getParam("fdate").isEmpty() )
			   sb2.append(" AND DATE(txn_date) <='"+req.getParam("fdate")+"'  ") ;
	
		
		rs = stmt.executeQuery(sb2.toString());
        computeOpBalance ( rs,0 ) ;    // 0 indicates stock is transfered from this branch 
	
		} catch (ClassNotFoundException | IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
				//if (conn != null) conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
	/*
	 *   This is to give the Response 
	 */
	public RequestResponse getStockReport ( RequestResponse req ) {
	
		Connection conn = null ;
		Statement stmt = null ;
		ResultSet rs = null ;
		 
		try {
			conn = JDBCHelper.getConnection();
			stmt = conn.createStatement();
			
			
		// getting opening balance of the year	
	    String s =  " select op_bal from  item_stock where itemid=" + req.getParam("item_id");
		       s+=  " AND  branchcode="+session.getBrCode() ;
		       rs = stmt.executeQuery(s) ;
		 origOpBal(rs);
		
		
		// from date is not null
		if ( req.getParam("fdate")!=null ) 
				getOpBal( req );
		
		
		// Response sending object
		JSONObject  resObj = new JSONObject() ;
	    resObj.put("op_bal",opBal );
		
		
		StringBuilder sb = new StringBuilder ();
		sb.append ("select a.item , a.quantity, b.txn_code , b.txn_date , b.branch_id from  txn_details a INNER JOIN "); 
	    sb.append ("item_txn b ON a.txnid =  b.txn_id  where  b.branch_id ="+session.getBrCode());
	    
	    if (req.getParam("item_id")!=null && !req.getParam("item_id").isEmpty())
	         sb.append( " AND item = "+req.getParam("item_id")+"  ") ;
	    if (req.getParam("fdate")!=null && !req.getParam("fdate").isEmpty())
	    	sb.append( " AND txn_date >= '"+req.getParam("fdate")+"'" ) ; 
	    if (req.getParam("tdate")!=null && !req.getParam("tdate").isEmpty())
	    	sb.append( " AND txn_date <='"+req.getParam("tdate")+"'" );
	    
	    rs = stmt.executeQuery(sb.toString());
	    prepareCollectionObject(rs);
	    
	    StringBuilder sb1 =  new StringBuilder() ;
	    sb1.append("select a.sitemid , a.aqty , b.txn_date  from stock_txn_details a INNER JOIN ");
	    sb1.append("stock_txn b  ON  a.stxnid =  b.txn_id where b.from_branch ="+session.getBrCode());
	    if (req.getParam("item_id")!=null && !req.getParam("item_id").isEmpty())
	    	sb1.append(" AND a.sitemid ="+req.getParam("item_id"));
	    if (req.getParam("fdate")!=null && !req.getParam("fdate").isEmpty())
	    	sb1.append( " AND txn_date >= '"+req.getParam("fdate")+"'"); 
	    if (req.getParam("tdate")!=null && !req.getParam("tdate").isEmpty())
	    	sb1.append( " AND txn_date <='"+req.getParam("tdate")+"'" );
	    
	    rs = stmt.executeQuery(sb1.toString());
	    prepareCollectionObject(rs,0);   // stock transfer from branch , needs to be debited
	    
	    StringBuilder sb2 =  new StringBuilder() ;
	    sb2.append("select a.sitemid , a.aqty , b.txn_date  from stock_txn_details a INNER JOIN ");
	    sb2.append("stock_txn b  ON  a.stxnid =  b.txn_id where b.to_branch ="+session.getBrCode());
	    if (req.getParam("item_id")!=null && !req.getParam("item_id").isEmpty())
	    	sb2.append(" AND a.sitemid ="+req.getParam("item_id"));
	    if (req.getParam("fdate")!=null && !req.getParam("fdate").isEmpty())
	    	sb1.append( " AND txn_date >='"+req.getParam("fdate")+"'" );
	    if (req.getParam("tdate")!=null && !req.getParam("tdate").isEmpty())
	    	sb1.append( " AND txn_date <='"+req.getParam("tdate")+"'" );
	    
	    //System.out.println( sb2.toString() );
	    
	    rs = stmt.executeQuery(sb2.toString());
	    prepareCollectionObject(rs, 1 ); // stock transfer to the branch hence needs to be credited
			
	    
	    //System.out.println( txnList.size() );
	    JSONArray reSarray = new JSONArray() ; 
	    resObj.put("op_bal",opBal );
	    
	    
	    Collections.sort(txnList);
	    
	    for (int i = 0 ;  i < txnList.size() ; i++ ) {
	    	ItemTxn o =  txnList.get(i) ;
	    	   JSONObject obj = new JSONObject();
	    	   obj.put("sl_no", o.getSlNo() ) ;
	    	   obj.put("date", o.getDate());
	    	   obj.put("desc", o.getDesc());
	    	   obj.put("addition", o.getAddition());
	    	   obj.put("reduction", o.getReduction());
	    	   obj.put("balance", o.getBalance() );
	    	   reSarray.add(obj) ;
	    }
		// sending response
	    resObj.put("details" , reSarray );
			
	    return createResponse( resObj );
	
		} catch (ClassNotFoundException | IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
				//if (conn != null) conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    
		return null ;
	}
	
	private int  loop ;
	// Updates for all the transactions , based on add or reduce stock will be updated  
	private void prepareCollectionObject(ResultSet rs ) throws SQLException {
    // TODO update the list with objects so that we can sort and prepare JSON
		while(rs.next())  {
			ItemTxn obj = new ItemTxn() ;
			obj.setSlNo(++loop);
			obj.setDate(rs.getString("txn_date"));
			if (rs.getString("txn_code").equals("s"))
			   {
				 	obj.setAddition(0) ;
				 	obj.setReduction(rs.getInt("quantity"));
				 	obj.setDesc("item has been soled to customer ") ;
				 	obj.setBalance( opBal - rs.getInt("quantity") );
				 	opBal -= rs.getInt("quantity") ;
			   } else {
				   	obj.setAddition(rs.getInt("quantity")) ;
				   	obj.setReduction(0);
				   	obj.setDesc("Item has been purchased") ;
				   	obj.setBalance( opBal + rs.getInt("quantity"));  //opBal + rs.getInt("quantity")
					opBal += rs.getInt("quantity") ;
			   }
			txnList .add(obj) ;
		}
    }   
	
	/*
	 * Updates for all the stock transfers
	 * Based on add or reduce stock will be updated  
	 */
	private void prepareCollectionObject(ResultSet rs , int type ) throws SQLException {
	// TODO update the list with objects so that we can sort and prepare JSON
		while(rs.next())  {
			ItemTxn obj = new ItemTxn() ;
			obj.setSlNo(++loop);
			obj.setDate(rs.getString("txn_date"));
			if (type == 1)  {
			    obj.setAddition(0) ;
			    obj.setReduction(rs.getInt("aqty"));
			    obj.setDesc("Item has been transfered to branch ") ;
			    obj.setBalance(opBal + rs.getInt("aqty"));
			    opBal += rs.getInt("aqty") ;
			} else   { 
				obj.setAddition(rs.getInt("aqty")) ;
				obj.setReduction(0);
			    obj.setDesc("item has been transfered from branch") ;
			    obj.setBalance(opBal - rs.getInt("aqty"));
			    opBal -=  rs.getInt("aqty") ;
			}
			txnList .add(obj) ;
		}
	}
	
	/**
	 * Internal helper method that wraps a given object within a response
	 * object.
	 */
	private RequestResponse createResponse(JSONObject o) {
		return new RequestResponse(o.toJSONString());
	}
	
}