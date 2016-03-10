/**
 * 
 */
package com.sridama.eztrack.bo ;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author rv
 *
 */
public abstract class Report {

	
	
	protected SessionManager session = null;		//identifies the user /branch code associated with this session.  So reports can be filtered out accordingly.
	protected HashMap <String,String >  qmap =  new HashMap<String,String> () ;
	private final static Logger LOGGER = Logger.getLogger(Report.class.getName());
	protected float totalAmount ;  // this is for summong the column for stock report
	
	Report(SessionManager s) {
		this.session = s ;
	}
	
	/**
	 * Generic template algorithm for creating a report (uses template method pattern)
	 * by delegating specific tasks to child classes.
	 * 
	 * @return
	 */
	public RequestResponse generateReport(RequestResponse req) {
		
		Connection conn = null;
		Statement  stmt = null;
		ResultSet  rs   = null;

			try {
				conn = JDBCHelper.getConnection();
				stmt = conn.createStatement();
				String sql = getSQL(req);
				String sqlBeforeFilter = sql;
				
				
				/** parse the query params and push into a map for later use */
				if ( req.getParam("queryString")!=null )
				   queryStringParse(req.getParam("queryString"));
				
				// Add any filter clauses, if specified. 
				String filter =  getReportFilter(req);		// abstract method
				if (!filter.isEmpty())
					sql += " AND ("+ filter+") ";
				
				
				// Add sorting info, if specified.
				String sortField = getReportSortField(req);
				if (!sortField.isEmpty())
					sql += sortField;
				
				
				// Add order by statement , this will be different for each report
				String orderBy = getOrderbyString(req);
				   sql+= orderBy ;
				
				// Add range limit.
				String recRange = getReportRange(req);
				if (!recRange.isEmpty()) 
					sql += " limit " + recRange;
					//sql += " AND " + filter + " " + sortField + " " + (recRange.isEmpty()?"":"limit " + recRange);
				
				LOGGER.debug("SQL string after filter, sort and range transformations..." + sql);
				System.out.println("SQL string after filter, sort and range transformations..." + sql);
				rs = stmt.executeQuery(sql);
				JSONArray  json = toJSON(rs);
			
				// Additional steps for computing the total no. of records before | after applying search filters.
				// This is a data tables plugin requirement, so no escape from this :(
				int rowCountBeforeFilter = getRowCount(conn, sqlBeforeFilter);
				int rowCountAfterFilter = getRowCount(conn, sql);
				
				
				//for these report needs to be sent along with the  rowCountBeforeFilter  rowCountAfterFilter
				String[] reportOpcodes = {"1","42","23" ,"32"};
				
				if (Arrays.asList(reportOpcodes).contains( req.getParam("opcode").trim()))
				      return createResponse(json, rowCountBeforeFilter, rowCountAfterFilter);
				else 
					 return createResponse(json);
				
		
			} catch (ClassNotFoundException | IOException | SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.debug("Exception in Report catch Block "+e);
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
		return null;
	}

	
	/**
	 * Converts any given SQL query into a counting sql query and executes it.
	 * @param conn
	 * @param sqlBeforeFilter
	 * @return
	 */
	private int getRowCount(Connection conn, String sqlBeforeFilter) {
		
		// TODO Auto-generated method stub
		String countSQL = getCountQuery(sqlBeforeFilter);
		LOGGER.debug("SQL for count: " + countSQL);
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs= stmt.executeQuery(countSQL);
			//if (rs != null && rs.isBeforeFirst()) {
				rs.next();
				return rs.getInt(1);
			//}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0;
	}


	/**
	 * Converts a regular select query into a counting query,
	 * by substituting the fields list with "count(*)" expression.
	 * @param sqlBeforeFilter
	 * @return
	 */
	private String getCountQuery(String sql) {
		
		int replacePos = sql.toLowerCase().indexOf("from");
		if (replacePos == -1) return null;
		
		String s = "SELECT COUNT(*) " + sql.substring(replacePos);
		
		// if the string contains any "limit ..." clause, knock it off.
		int limitPos = s.toLowerCase().indexOf("limit");
		if (limitPos != -1 ) {
			s = s.substring(0, limitPos);
		}
		return s;
	}
	

	/**
	 * Returns an SQL string representation of "limit x,y" clause
	 * where x is the starting record and y is the no. of records 
	 * @param req
	 * @return
	 */
	protected  String getReportRange(RequestResponse req )  {
		//String queryString = req.getParam(queryString);

		String limitString = "";
		//System.out.println(qmap.get("iDisplayStart") + " : " +qmap.get("iDisplayLength"));
		if ( qmap.get("iDisplayStart")!=null ) {
			limitString += qmap.get("iDisplayStart") ;
					
			if ( qmap.get("iDisplayLength")!=null )   
				limitString += ","+qmap.get("iDisplayLength") ;
		}
		return limitString;
	}
		
	
	
	protected abstract String getReportSortField(RequestResponse req) ;

	protected abstract String getReportFilter(RequestResponse req) ;

	protected abstract JSONArray toJSON(ResultSet rs) throws SQLException;

	protected abstract String getSQL(RequestResponse req);
	
	
	protected abstract String getOrderbyString(RequestResponse req);
	
	/**
	 * Internal helper method that wraps a given object within a response
	 * object.
	 */
	private RequestResponse createResponse(JSONArray o) {
		return new RequestResponse(o.toJSONString());
	}
	
	/**
	 * Internal helper method that wraps a given object within a response
	 * object.
	 * @param rowCountAfterFilter 
	 * @param rowCountBeforeFilter 
	 */
	private RequestResponse createResponse(JSONArray a, int rowCountBeforeFilter, int rowCountAfterFilter) {
		
		JSONObject o = new JSONObject();
		o.put( "iTotalRecords",rowCountBeforeFilter);   // 
		o.put( "iTotalDisplayRecords", rowCountAfterFilter); 
		o.put( "columtot", totalAmount );
		LOGGER.debug("sEcho value from user interface  "+qmap.get("sEcho"));
		if (qmap.get("sEcho")!=null)
		       o.put("sEcho", Integer.parseInt(qmap.get("sEcho")));
		o.put("aaData", a);
		return new RequestResponse(o.toJSONString());
	}

	/*
	 *   Filter will be captured from the Query String
	 */
	private void queryStringParse( String queryString )  {
		String token = "" ;
		
		if (queryString == null) return;
		
		 StringTokenizer  st = new StringTokenizer( queryString , "&" );
		   while (st.hasMoreTokens()) {
			   token  = st.nextToken() ;
			   //LOGGER.debug("tokens "+token) ;
			   System.out.println(token);
			   StringTokenizer st1 = new StringTokenizer(token , "=") ;
			    	
			   String key = st1.hasMoreTokens()?st1.nextToken():"" ;
			   String val = st1.hasMoreTokens()?st1.nextToken():"" ;
			   if (!key.isEmpty())
			       qmap.put( key , val ) ;
		   }
	}


}
