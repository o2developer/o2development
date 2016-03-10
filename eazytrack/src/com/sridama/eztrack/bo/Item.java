package com.sridama.eztrack.bo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sridama.eztrack.controller.EzTrackServlet;
import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.eztrack.utils.Utils;
import com.sridama.txngw.core.RequestResponse;

public class Item {
	private final static Logger LOGGER = Logger.getLogger(Item.class
			.getName());
	private int id;
	private String name;
	private int code;
	private int category;
	private int units;
	private float rate;
	private int opBalance;    // same value will be updated to stock of item_stock
	
	private JSONArray opBalances ;
	private float cost;
//	private float taxes;
	private int disPercentage ;
	private int taxSlab ;
private RequestResponse req;
	private SessionManager ses ;
	
	/*
	 * initializing session object using a constructor
	 */
	public Item(SessionManager ses ) {
		this.ses = ses ;
	}
	
	/* 
	 * constructor to initialize the variables for item creation
	 */
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
		//System.out.println(!req.getParam("tax_slab").trim().equals("-1"));
		if (req.getParam("tax_slab")!=null && !req.getParam("tax_slab").isEmpty() && !req.getParam("tax_slab").equals("-1")){
			this.taxSlab = Integer.parseInt(req.getParam("tax_slab"));
		}else if (req.getParam("tax_slab").equals("-1")){
		TaxSlab tax = new TaxSlab(Float.parseFloat(req.getParam("tax_slab_val")));
		tax.save();
		this.taxSlab = tax.getId() ;
	  }
			
	}
	
	/*
	 * saves the item to database
	 */
	public RequestResponse  save()  {
		JSONObject obj = new JSONObject();
		// You can use LAST_INSERT_ID() to retrieve the ID you just inserted 
		StringBuilder sb = new StringBuilder();
		sb.append ("insert into  item_master (name , code , category, units, ");
		sb.append(" rate  ,cost ,discnt_percent,tax_slab,last_modified_by,last_modified_date ) ");
		sb.append("values ('"+name+"',"+code+","+category+","+units+",") ;
		sb.append( rate+","+cost+","+disPercentage+","+taxSlab) ;
		sb.append(",'"+ses.getName()+"','"+Utils.getCurrentDateTime()+"')");
		//System.out.println(sb.toString());
		
		StringBuilder sb1 = new StringBuilder();
		sb1.append ("insert into  item_stock ( itemid , branchcode , stock,op_bal ) values ");
		//System.out.println("JSON String Array "+opBalances.toJSONString());
		Iterator<String> iterator = opBalances.iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			//System.out.println(iterator.next());
			JSONParser parser = new JSONParser();
			JSONObject json = null ;
			try {
				json = (JSONObject) parser.parse(o.toString());
				sb1.append("(last_insert_id(),"+json.get("br_code").toString()+"," );
				if (!json.get("stock").toString().isEmpty() ) {
				sb1.append(json.get("stock").toString()+",") ;
				sb1.append(json.get("stock").toString()+")") ; // op_bal will be equal to stock  
				} else {
				sb1.append("0,") ;	//appending 0 when string found empty from ui
				sb1.append("0)") ; // '.' opening balance will be equal to initial stock
				}
				if (iterator.hasNext())
					sb1.append(",");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		//System.out.println(sb1.toString());  
		LOGGER.debug("SQL QUERY TO Insert The item "+sb.toString()+"\n item stock "+sb1.toString());	
		System.out.println("dad  --->"+sb.toString()+"//"+sb1.toString());
		if (isItemExistsAlready())  {
			obj.put("result_code", "-1");
		    obj.put("desc", "Item Name Already Exists ") ;
		    LOGGER.debug("ITEM ALREADY EXISTS IN DATABASE "+name+"hence response is"+obj.toJSONString());	
		return createResponse(obj) ;   
		}
		
		Connection con = null ;
		Statement stmt = null;
		
		try {
			   con = JDBCHelper.getConnection();

				stmt = con.createStatement();
				
				stmt.execute(sb.toString());
				stmt.execute(sb1.toString()) ;
				
				updateItemCode() ;  // increments the item code by one
				obj.put("result_code", "0");
				obj.put("desc", "Item created successfully") ;
			
				LOGGER.debug("Item Creation is successfull "+name+"Response is "+obj.toJSONString());
				return createResponse(obj) ;
			
				
				
				}catch(SQLException e) {
					e.printStackTrace() ;
				}catch(ClassNotFoundException e ) {
					e.printStackTrace() ;
				}catch (IOException e) {
					e.printStackTrace() ;
				} finally {
					
						try {
							if (stmt!=null) stmt.close() ;
							//if (con!=null) con.close();
							
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}

		return null ;
	}
	
	
	/*
	 *  check if item already exists in the database under the same category
	 */
	
    private boolean isItemExistsAlready() {
		
		StringBuilder sb = new StringBuilder();
		sb.append (" select * from item_master where name='"+name+"'");

		Connection con = null ;
		Statement stmt = null;
		ResultSet rs = null ;
		JSONObject obj = new JSONObject();
		try {
			    con = JDBCHelper.getConnection();
				stmt = con.createStatement();
				rs = stmt.executeQuery(sb.toString());
				
				if (rs.isBeforeFirst())
					return true ;
				
				return false ;
				}catch(SQLException e) {
					e.printStackTrace() ;
				}catch(ClassNotFoundException e ) {
					e.printStackTrace() ;
				}catch (IOException e) {
					e.printStackTrace() ;
				} finally {
					
						try {
							//if (con!=null) con.close();
							if (stmt!=null) stmt.close() ;
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}

		return true ;
	} 
	
	/*
	 * deletes the item from database based on id
	 */
	public RequestResponse delete() {

		String sql1 = "delete from item_stock where  itemid="+id+" and branchcode="+ses.getBrCode();
		String sql  = "delete from item_master where  id="+id;

		JSONObject obj = new JSONObject() ;

		Connection con = null;
		Statement stmt = null;
		try {
			con = JDBCHelper.getConnection();
			stmt = con.createStatement();
			stmt.execute(sql1) ;
			stmt.execute(sql);

			obj.put("result_code", "0");
			obj.put("desc", "Entry Deleted Successfully") ;
			return createResponse(obj);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				//if (con != null) con.close();
				if (stmt != null) stmt.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null ;
	}
	
	
	/*
	 * updates item code to give next item code for next item
	 */
	private void updateItemCode() {
		String  sql = " update invoice_numbers set item_code = item_code+1 where branch=1";
		Connection con = null;
		Statement stmt = null;
		try {
			con = JDBCHelper.getConnection();
			stmt = con.createStatement();
			stmt.executeUpdate(sql) ;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				//if (con != null) con.close();
				if (stmt != null) stmt.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Internal helper method that wraps a given object within a response
	 * object.
	 */
	private RequestResponse createResponse(JSONObject o) {
		return new RequestResponse(o.toJSONString());
	}

	
	/*
	 * Updation of item 
	 */
	
	public RequestResponse updateItem()  {
		
		StringBuilder sb = new StringBuilder();
		sb.append ("update item_master set name='"+name+"', code="+code+",");
		sb.append ("category="+category+", units="+units+" , rate="+rate+",") ;
		sb.append("cost="+cost+", discnt_percent="+disPercentage+",");
		sb.append("last_modified_by='"+ses.getName()+"',last_modified_date='"+Utils.getCurrentDateTime()+"',");
		sb.append("tax_slab="+taxSlab+" where id="+id) ;
		System.out.println(sb.toString()); 
		
		
		Iterator<String> iterator = opBalances.iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			JSONParser parser = new JSONParser();
			JSONObject json = null ;
			try {
				json = (JSONObject) parser.parse( o.toString());
				updateItemStock(json,req);  // updates item stock
			
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		Connection con = null ;
		Statement stmt = null;
		JSONObject obj = new JSONObject();
		try {
			   con = JDBCHelper.getConnection();
				stmt = con.createStatement();
				stmt.executeUpdate(sb.toString());
			//	stmt.executeUpdate(sb1.toString());
				  
				obj.put("result_code", "0");
				obj.put("desc", "Item updated successfully") ;
			
				return createResponse(obj) ;
				
				}catch(SQLException e) {
					e.printStackTrace() ;
				}catch(ClassNotFoundException e ) {
					e.printStackTrace() ;
				}catch (IOException e) {
					e.printStackTrace() ;
				} finally {
					
						try {
							//if (con!=null) con.close();
							if (stmt!=null) stmt.close() ;
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}

		return null;
	}

	
	
	
	
	public RequestResponse generateReport(RequestResponse req) {
		
		StringBuilder sb = new StringBuilder();
		sb.append ("update item_master set name='"+name+"', code="+code+",");
		sb.append ("category="+category+", units="+units+" , rate="+rate+",") ;
		sb.append("cost="+cost+",id ="+req.getParam("item_id")+", discnt_percent="+disPercentage+",");
		sb.append("last_modified_by='"+ses.getName()+"',last_modified_date='"+Utils.getCurrentDateTime()+"',");
		sb.append("tax_slab="+taxSlab+" where id="+req.getParam("item_id")) ;
		System.out.println(sb.toString());  
		
	//	StringBuilder sb1 = new StringBuilder();
		/*sb1.append ("update item_stock set itemid="+req.getParam("item_id")+" , ");*/
		
		//System.out.println(sb1.toString());  
		
		Iterator<String> iterator = opBalances.iterator();
		while (iterator.hasNext()) {
			Object o = iterator.next();
			JSONParser parser = new JSONParser();
			JSONObject json = null ;
			try {
				json = (JSONObject) parser.parse( o.toString());
				updateItemStock(json,req);  // updates item stock
			
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		/*sb1.append(" where itemid="+req.getParam("item_id")+"");*/
		
		
		System.out.println("Items  updatee "+sb.toString());
		Connection con = null ;
		Statement stmt = null;
		JSONObject obj = new JSONObject();
		try {
			   con = JDBCHelper.getConnection();
				stmt = con.createStatement();
				stmt.executeUpdate(sb.toString());
			//	stmt.executeUpdate(sb1.toString());
				  
				obj.put("result_code", "0");
				obj.put("desc", "Item updated successfully") ;
			
				return createResponse(obj) ;
				
				}catch(SQLException e) {
					e.printStackTrace() ;
				}catch(ClassNotFoundException e ) {
					e.printStackTrace() ;
				}catch (IOException e) {
					e.printStackTrace() ;
				} finally {
					
						try {
							//if (con!=null) con.close();
							if (stmt!=null) stmt.close() ;
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}

		return null;
	}
	/*
	 * Updates the item stock for different branches  itemid="+req.getParam("item_id")+"
	 */
	private void updateItemStock( JSONObject obj , RequestResponse req){
		Connection con = null ;
		Statement stmt = null;
		
		String sql = "update item_stock  set  stock="+obj.get("stock")+"  where  branchcode="+obj.get("br_code")+" AND itemid="+id ;
		 
			try {
				   con = JDBCHelper.getConnection();
					stmt = con.createStatement();
					stmt.executeUpdate(sql);
					  
				
					}catch(SQLException e) {
						e.printStackTrace() ;
					}catch(ClassNotFoundException e ) {
						e.printStackTrace() ;
					}catch (IOException e) {
						e.printStackTrace() ;
					} finally {
						
							try {
								//if (con!=null) con.close();
								if (stmt!=null) stmt.close() ;
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}

		 
	}
}
