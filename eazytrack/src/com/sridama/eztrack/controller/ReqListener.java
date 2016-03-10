package com.sridama.eztrack.controller;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import com.sridama.eztrack.utils.JDBCHelper;

public class ReqListener implements ServletRequestListener {

	@Override
	public void requestDestroyed(ServletRequestEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Removing connection...");
		JDBCHelper.removeConnection();
	}

	@Override
	public void requestInitialized(ServletRequestEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
