/**
 * 
 */
package com.sridama.eztrack.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Rizwan Holds the data model for all the item txn level
 */
public class ItemTxn implements Comparable<ItemTxn> {

	private int slNo;
	private String date;
	private String desc;
	private int addition;
	private int reduction;
	private int balance;

	public int getSlNo() {
		return slNo;
	}

	public void setSlNo(int slNo) {
		this.slNo = slNo;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getAddition() {
		return addition;
	}

	public void setAddition(int addition) {
		this.addition = addition;
	}

	public int getReduction() {
		return reduction;
	}

	public void setReduction(int reduction) {
		this.reduction = reduction;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	/*
	 *  Implementing comparable interface for sorting based on dates return of
	 *  this will give the sorted collection -1 or - more less 0 equal 1 or more
	 *  more there out put specifies the order
	 */
	@Override
	public int compareTo(ItemTxn obj) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// ascending order
		// return this.getDate() - compareQuantity;
		try {
			return sdf.parse(this.getDate()).compareTo(
					sdf.parse(((ItemTxn) obj).getDate()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// descending order
		// return compareQuantity - this.quantity;
		return 0;
	}
}
