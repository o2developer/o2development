/**
 * 
 */
package com.sridama.eztrack.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;


import com.sridama.eztrack.bo.SessionManager;
import com.sridama.eztrack.utils.Utils;

/**
 * @author rvnath
 *
 */
public class SessionFilter implements Filter {

	private ArrayList<String> urlsList = null;
	private final static Logger LOGGER = Logger.getLogger(SessionFilter.class.getName());
	private final static int HTTP_UNAUTHORIZED = 401;
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain f) throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		boolean isRequestAllowed = false;
		LOGGER.debug("Executng Filter class...");
		
		String url = req.getServletPath();
		//System.out.println("[" + url + "]");
		
		if (urlsList.contains(url)) {
			LOGGER.debug("Allowing access for avoid-url [" + url + "]..." );
			isRequestAllowed = true;
		}
		else if (url.matches(".*(css|jpg|png|gif|js)")) {
			isRequestAllowed = true;
		}

		// allow login request to pass through..
		else if ( (req.getParameter("opcode") != null) && req.getParameter("opcode").equals("0") || 
				req.getServletPath().trim().equals("/login.html")) {
			isRequestAllowed = true;
//			f.doFilter(req, res);
		}
		else {	// Check if the session is valid.
			SessionManager sessionObj = new SessionManager(req);
			if (sessionObj.checkSession()) {
				//Utils.sendResponse(response, Utils.getInvalidSessionResponse());
				//return ;
				isRequestAllowed = true;
			}
		}
		
		
		// TODO
		// if condition for license check
		// if conditon fails redirect to license expiry page
		
		if (!isRequestAllowed) {
			//HttpSession session = req.getSession(false);
			//if (session == null) {
				
				// Hmm. Invalid session! 
				// If the call is coming from a normal request (i.e. not an ajax request),
				// then simply redirect to login page.
				// Or else, if ajax request, respond with 401/403 HTTP error
				boolean isAjax = req.getHeader("X-Requested-With") == null?false:req.getHeader("X-Requested-With").equals("XMLHttpRequest");
				if (!isAjax) {
					LOGGER.debug("Redirecting to login page...");
					RequestDispatcher dispatcher = req.getRequestDispatcher("login.html");
					dispatcher.forward(req, res);
				} else {
					//System.out.println("Session timed out.  Sending 401");
					res.setStatus(HTTP_UNAUTHORIZED);
					Utils.sendResponse(res, null);
				}
			//}
			//else {
				//f.doFilter(req, res);
				//isRequestAllowed = true;
			//}
		} else {
			LOGGER.debug("Filter passes through. Continuing with the chain...");
			f.doFilter(req, res);
		}
	}

	
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig config) throws ServletException {
		 String urls = config.getInitParameter("avoid-urls");
	        StringTokenizer token = new StringTokenizer(urls, ",");
	 
	        urlsList = new ArrayList<String>();
	        while (token.hasMoreTokens()) {
	            urlsList.add(token.nextToken().trim());
	        }
	        
	        //System.out.println("Printing the items in avoid url:");
//	        for (String url: urlsList) {
//	        	System.out.println("[" + url + "]");
//	        }
	}

}
