/**
 * 
 */
package com.sridama.eztrack.bo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sridama.eztrack.utils.Utils;
import com.sridama.txngw.core.RequestResponse;

/**
 * @author Rizwan
 * 
 */
public class Invocie {

	private int    invoiceNo = 0;
	private String invoiceDate = "";
	private String interState = "";
	private String cform = "";
	private int paymentMode;
	private double vatFive = 0.0d;
	private double vatFrtn = 0.0d;
	private double cst = 0.0d;
	private double subTotal = 0.0d;
	private double additional = 0.0d;
	private double roundOffDisc = 0.0d;
	private double finalTotal = 0.0d;

	private double discount;

	private String goodsRecDate = "0000-00-00";
	private String purInvDate = "0000-00-00";
	private int purInvNo;
	private int dcNo;
	private int toBranch;

	private String action;

	private int custId = 0;
	private int brCode = 0;

	// this variable will be set after the sale Transaction is successful
	private int transId = 0;

	private String txnType;

	private int branch;

	// to decide whether to save , update or reject the invoice
	public String getAction() {
		return action;
	}

	// to make either a sale,purchase
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getTxnType() {
		return txnType;
	}

	public int getToBranch() {
		return toBranch;
	}

	public void setBrCode(int brCode) {
		this.brCode = brCode;
	}

	public void setCustId(int custId) {
		this.custId = custId;
	}

	public int getTransId() {
		return transId;
	}

	public int getInvoiceNo() {
		return invoiceNo;
	}

	/*
	 * to update the variables
	 */
	public Invocie(String json) {
		JSONParser parse = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parse.parse(json);
			if (obj.get("invoice_no") != null)
				this.invoiceNo = Integer.parseInt(obj.get("invoice_no")
						.toString());
			if (obj.get("invoice_date") != null)
				this.invoiceDate = obj.get("invoice_date").toString();
			if (obj.get("inter_state") != null)
				this.interState = obj.get("inter_state").toString();
			if (obj.get("cform") != null)
				this.cform = obj.get("cform").toString();
			if (obj.get("payment_mode") != null)
				this.paymentMode = Integer.parseInt(obj.get("payment_mode")
						.toString());
			if (obj.get("vat_five") != null)
				this.vatFive = Double.parseDouble(obj.get("vat_five")
						.toString());
			if (obj.get("vat_frtn") != null)
				this.vatFrtn = Double.parseDouble(obj.get("vat_frtn")
						.toString());
			if (obj.get("cst") != null)
				this.cst = Double.parseDouble(obj.get("cst").toString());
			if (obj.get("sub_total") != null)
				this.subTotal = Double.parseDouble(obj.get("sub_total")
						.toString());
			if (obj.get("additional") != null
					&& (!obj.get("additional").toString().isEmpty()))
				this.additional = Double.parseDouble(obj.get("additional")
						.toString());
			if (obj.get("round_off_disc") != null
					&& !(obj.get("round_off_disc").toString().isEmpty()))
				this.roundOffDisc = Double.parseDouble(obj
						.get("round_off_disc").toString());
			if (obj.get("final_total") != null)
				this.finalTotal = Double.parseDouble(obj.get("final_total")
						.toString());
			if (obj.get("pur_inv_date") != null)
				this.purInvDate = obj.get("pur_inv_date").toString();
			if (obj.get("pur_inv_no") != null && !obj.get("pur_inv_no").toString().isEmpty() )
				this.purInvNo = Integer.parseInt( obj.get("pur_inv_no")
						.toString());
			if (obj.get("to_branch") != null)
				this.toBranch = Integer.parseInt(obj.get("to_branch")
						.toString());
			if (obj.get("dc_no") != null)
				this.dcNo = Integer.parseInt(obj.get("dc_no").toString());
			if (obj.get("goods_received_date") != null)
				this.goodsRecDate = obj.get("goods_received_date").toString();
			if (obj.get("action") != null)
				this.action = obj.get("action").toString();
			//System.out.println(obj.get("branch"));
			if (obj.get("branch") != null && !obj.get("branch").equals(""))
				this.branch = Integer.parseInt(obj.get("branch").toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Saves the transaction to the data base for sale
	 */
	public void save(Connection con) throws SQLException {

		PreparedStatement preparedStatement = null;
		Statement stmt = null;
		ResultSet rs = null;

		/*
		 * txn_id txn_date txn_code party_id inter_state payment_type invoice_no
		 * grand_total sub_total rtotal transportation totalvat_five
		 * totalvat_fourteen discount branch_id cst cform pur_inv_date
		 * pur_invoice_no dc_no to_branch goods_receive_date status
		 */

		StringBuilder sb = new StringBuilder();
		try {
			sb.append("insert into item_txn ( txn_date , txn_code , party_id , inter_state , payment_type ,");
			sb.append("invoice_no , grand_total , sub_total , transportation , discount , branch_id , ");
			sb.append("pur_inv_date  , pur_invoice_no , goods_receive_date ,");
			sb.append(" cst , cform , status  )  values  (");
			sb.append("'" + Utils.getCurrentDateTime() + "','" + txnType
					+ "' , " + custId + " ");
			if (interState.equals("y"))
				sb.append(",1");
			else
				sb.append(",0");
			sb.append("," + paymentMode + "," + invoiceNo + ", " + finalTotal
					+ "," + subTotal + ", ");
			sb.append(additional + ", " + roundOffDisc + ", " + brCode + ", ");

			sb.append("'" + purInvDate + "'," + purInvNo + ",'" + goodsRecDate
					+ "',");

			sb.append(cst + ",");
			if (cform.equals("y"))
				sb.append("1,");
			else
				sb.append("0,"); // updating status as 1 for accept
			sb.append("1 )");

			//System.out.println(sb.toString());
			stmt = con.createStatement();
			stmt.execute(sb.toString());

			/*
			 * Fetching latest transaction ID from item transaction to update
			 * txn details
			 */
			stmt = con.createStatement();
			rs = stmt
					.executeQuery("select max(txn_id) from item_txn where branch_id="
							+ brCode + " AND txn_code='" + txnType + "'");
			while (rs.next()) {
				transId = rs.getInt(1);
			}

		} finally {
			// Closing all connections after the usage
			if (rs != null) rs.close();
			if (preparedStatement != null) preparedStatement.close();
			if (stmt != null) stmt.close();
		}
	}

	/*
	 * Updates the transaction to the data base for sale
	 */
	public void update(Connection con) throws SQLException {

		PreparedStatement preparedStatement = null;
		Statement stmt = null;
		ResultSet rs = null;

		/*
		 * txn_id txn_date txn_code party_id inter_state payment_type invoice_no
		 * grand_total sub_total rtotal transportation totalvat_five
		 * totalvat_fourteen discount branch_id cst cform pur_inv_date
		 * pur_invoice_no dc_no to_branch goods_receive_date state
		 */

		// invoice number cannot be changed as transaction is made across the
		// invoice
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("update item_txn set txn_date='"
					+ Utils.getCurrentDateTime() + "',");
			sb.append("party_id=" + custId + " , payment_type=" + paymentMode
					+ " ,");
			sb.append("grand_total=" + finalTotal + " , sub_total=" + subTotal
					+ " , transportation=" + additional + " ");
			sb.append(", discount=" + roundOffDisc);
			sb.append(", pur_invoice_no=" + purInvNo);
			sb.append(", pur_inv_date='" + purInvDate + "'");
			sb.append(", goods_receive_date='" + goodsRecDate + "'");
			if (interState.equals("y"))
				sb.append(",inter_state=1 , ");
			else
				sb.append(",inter_state=0 , ");
			if (cform.equals("y"))
				sb.append("cform=1 , ");
			else
				sb.append("cform=0 , ");
			sb.append("status=1");
			sb.append(" where branch_id=" + brCode + " AND invoice_no="
					+ invoiceNo);

			//System.out.println(sb.toString());
			stmt = con.createStatement();
			stmt.executeUpdate(sb.toString());

		} finally {
			// Closing all connections after the usage
			if (rs != null) rs.close();
			if (preparedStatement != null) preparedStatement.close();
			if (stmt != null) stmt.close();
		}
	}

	/*
	 * Reject the transaction to the data base for sale status column in
	 * item_txn is set to 0 , indicating the rejection
	 */
	public void reject(Connection con) throws SQLException {

		if ( branch != -1 )
			brCode = branch;

		Statement stmt = null;
		ResultSet rs = null;

		// invoice number cannot be changed as transaction is made across the
		// invoice
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("update item_txn set status=0 ");
			sb.append("where   branch_id=" + brCode + " AND invoice_no=" +
					+ invoiceNo +" AND txn_code='"+txnType+"'" );

			System.out.println("------>" + sb.toString());
			stmt = con.createStatement();
			stmt.executeUpdate(sb.toString());

		} finally {
			// Closing all connections after the usage
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
		}
	}

	/*
	 * this is used when updating the invoice based on branch, invoice and txn
	 * code as sale this gives the txn id for invoice items update this updates
	 * the invoice
	 */
	public void getTransactionId(Connection con) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;

		StringBuilder sb = new StringBuilder();
		try {

			sb.append(" select txn_id from ");
			sb.append(" item_txn where invoice_no=" + invoiceNo
					+ " and branch_id=" + brCode);
			sb.append(" and txn_code='" + txnType + "'");

			//System.out.println(sb.toString());
			stmt = con.createStatement();
			rs = stmt.executeQuery(sb.toString());
			if (rs.next())
				this.transId = rs.getInt("txn_id");

		} finally {
			// Closing all connections after the usage
			if (rs != null) rs.close();
			if (stmt != null) stmt.close();
		}

	}

	/*
	 * Updates the stock before updating or rejecting the
	 */
	public void updateStockRejectUpdate(Connection con) throws SQLException {
		Statement stmt = null;
		StringBuilder sb = new StringBuilder();

		sb.append(" update item_stock as item INNER JOIN ( select item, quantity ");
		sb.append(" from  txn_details where txnid =" + transId
				+ ")  as  details ON item.itemid = ");
		sb.append(" details.item set item.stock = item.stock + details.quantity where ");
		sb.append(" item.branchcode =" + brCode);

		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sb.toString());

		} finally {
			if (stmt != null) stmt.close();
		}
	}

	/*
	 * Deletes all the items of this transaction for this perticular txn id from
	 * invocie
	 */
	public void delete(Connection con) throws SQLException {
		Statement stmt = null;
		StringBuilder sb = new StringBuilder();
		sb.append("delete from  txn_details  where ");
		sb.append("txnid=" + transId);

		//System.out.println(sb.toString());

		try {
			stmt = con.createStatement();
			stmt.executeUpdate(sb.toString());
		} finally {
			if (stmt != null) stmt.close();
		}
	}

}
