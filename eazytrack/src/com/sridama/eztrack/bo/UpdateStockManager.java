package com.sridama.eztrack.bo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONObject;

import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.txngw.core.RequestResponse;

public class UpdateStockManager {

	private SessionManager session;
	private int txnId;
	private int dcNo;
	private String operation;
	private int fromBranch;

	public UpdateStockManager(SessionManager session) {
		this.session = session;
	}

	public RequestResponse updateStock(RequestResponse req) {
		if (req.getParam("txn_number") != null
				&& !req.getParam("txn_number").isEmpty())
			this.txnId = Integer.parseInt(req.getParam("txn_number"));
		if (req.getParam("dc_no") != null && !req.getParam("dc_no").isEmpty())
			this.dcNo = Integer.parseInt(req.getParam("dc_no"));
		if (req.getParam("from_branch") != null
				&& !req.getParam("from_branch").isEmpty())
			this.fromBranch = Integer.parseInt(req.getParam("from_branch"));
		if (req.getParam("operation") != null)
			this.operation = req.getParam("operation");

		Connection con = null;
		try {
			con = JDBCHelper.getConnection();
			con.setAutoCommit(false);
			if (operation.equalsIgnoreCase("reject")) {
				reject(con);
			} else if (operation.equalsIgnoreCase("accept")) {
				accept(con);
			}

			// commits the transaction when no exception is thrown
			con.commit();

			JSONObject res = new JSONObject();
			res.put("result", 0);
			res.put("desc", "Transaction Successful !");

			return createResponse(res);

		} catch (ClassNotFoundException e) {
			try {
				if (con != null) con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (IOException e) {
			try {
				if (con != null) con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (SQLException e) {
			try {
				if (con != null) con.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} 
//		finally {
//			try {
//				//if (con != null)con.close();
//			} catch (SQLException se) {
//				se.printStackTrace();
//			}
//		}

		return null;

	}

	public boolean reject(Connection con)  {
		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			updateStatus(con, "r");
		stmt = con.createStatement();
		String sql = "select sitemid , aqty from stock_txn_details where stxnid="
				+ txnId;
		rs = stmt.executeQuery(sql);
		while (rs.next()) {
			updateStockSentBranch(con, rs.getInt(1), rs.getInt(2));
		}
		return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;

	}

	public boolean accept(Connection con) {

		ResultSet rs = null;
		Statement stmt = null;
		try {
			updateStatus(con, "a");
		stmt = con.createStatement();
		String sql = "select sitemid , aqty from stock_txn_details where stxnid="
				+ txnId;
		rs = stmt.executeQuery(sql);
		while (rs.next()) {
			updateStock(con, rs.getInt(1), rs.getInt(2));
		}

		return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean updateStatus(Connection con, String status) /* throws SQLException */ {
		StringBuilder sqlAccept = new StringBuilder();
		sqlAccept.append("update stock_txn set status='" + status
				+ "' where txn_id=" + txnId + " and ");
		sqlAccept.append("from_branch=" + fromBranch + " and dc_no=" + dcNo);

		//System.out.println("\n\n\n\n" + sqlAccept.toString());
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sqlAccept.toString());
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/*
	 * updates the item_stock , removes that quantity from sent branch and
	 * credits that quantity to received branch
	 */
	public boolean updateStock(Connection con, int itemId, int qty) /* throws SQLException */ {

/*		StringBuilder sqlSentBranch = new StringBuilder();
		sqlSentBranch.append("update item_stock set stock=stock-" + qty
				+ " where ");
		sqlSentBranch.append("itemid=" + itemId + " and branchcode="
				+ fromBranch);*/
		Statement stmt = null;
		StringBuilder sqlReceivedBranch = new StringBuilder();
		try {
			if (checkIfItemExistsInBrach(con, itemId)) {
				sqlReceivedBranch.append("update item_stock set stock=stock+" + qty
						+ " where ");
				sqlReceivedBranch.append("itemid=" + itemId + " and branchcode="
						+ session.getBrCode());
			} else {
				sqlReceivedBranch
						.append("insert into item_stock(itemid,branchcode,stock)");
				sqlReceivedBranch.append("values (" + itemId + ","
						+ session.getBrCode() + "," + qty + ")");
			}
			//System.out.println( "\n" + sqlReceivedBranch);

			stmt = null;
			stmt = con.createStatement();
			//stmt.executeUpdate(sqlSentBranch.toString());
			stmt.executeUpdate(sqlReceivedBranch.toString());

			return true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	/*
	 * updates back the stock to from branch when rejection happens at received
	 * branch, if item is not present at received branch , creates the item 
	 */
	public boolean updateStockSentBranch(Connection con, int itemId, int qty) /* throws SQLException */ {

		StringBuilder sqlReceivedBranch = new StringBuilder();
		sqlReceivedBranch.append("update item_stock set stock=stock+" + qty
				+ " where ");
		sqlReceivedBranch.append("itemid=" + itemId + " and branchcode="
				+ fromBranch);

		//System.out.println("\n" + sqlReceivedBranch);

		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sqlReceivedBranch.toString());

			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	/*
	 * checks whether the transfered item is present in the received branch
	 * 
	 */
	public boolean checkIfItemExistsInBrach(Connection con, int itemId) /* throws SQLException */ {
		ResultSet rs = null;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			String sql = "select stock from item_stock where itemid=" + itemId
					+ " and branchcode=" + session.getBrCode();
			//System.out.println(sql);
			rs = stmt.executeQuery(sql);
			if (rs.isBeforeFirst())
				return true;
			else return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Internal helper method that wraps a given object within a response
	 * object.
	 */
	private RequestResponse createResponse(JSONObject o) {
		return new RequestResponse(o.toJSONString());
	}
}
