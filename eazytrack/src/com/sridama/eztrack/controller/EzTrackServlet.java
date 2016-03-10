package com.sridama.eztrack.controller ;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.simple.parser.ParseException;

import com.sridama.eztrack.bo.BranchList;
import com.sridama.eztrack.bo.Customer;
import com.sridama.eztrack.bo.CustomersReport;
import com.sridama.eztrack.bo.DcNoStockTransfer;
import com.sridama.eztrack.bo.DiscountSlab;
import com.sridama.eztrack.bo.DiscountSlabList;
import com.sridama.eztrack.bo.FirstPdf;
import com.sridama.eztrack.bo.InvoiceNoListSale;
import com.sridama.eztrack.bo.InvoiceNoPurchase;
import com.sridama.eztrack.bo.InvoiceNoSale;
import com.sridama.eztrack.bo.Item;
import com.sridama.eztrack.bo.ItemEdit;
import com.sridama.eztrack.bo.ItemStockDetailedReport;
import com.sridama.eztrack.bo.NextItemCode;
import com.sridama.eztrack.bo.PurchaseTxn;
import com.sridama.eztrack.bo.PurchasesReport;
import com.sridama.eztrack.bo.Report;
import com.sridama.eztrack.bo.SaleEditInvoiceDetails;
import com.sridama.eztrack.bo.SaleReport;
import com.sridama.eztrack.bo.SaleTxn;
import com.sridama.eztrack.bo.SaleTxnDetails;
import com.sridama.eztrack.bo.SessionManager;
import com.sridama.eztrack.bo.StockManager;
import com.sridama.eztrack.bo.StockReport;
import com.sridama.eztrack.bo.StockTransDcDetails;
import com.sridama.eztrack.bo.StockTransReport;
import com.sridama.eztrack.bo.StockTransferReport;
import com.sridama.eztrack.bo.TaxSlab;
import com.sridama.eztrack.bo.TaxSlabList;
import com.sridama.eztrack.bo.Units;
import com.sridama.eztrack.bo.UnitsList;
import com.sridama.eztrack.bo.UpdateStockManager;
import com.sridama.eztrack.bo.User;
import com.sridama.eztrack.bo.UsersList;
import com.sridama.eztrack.dao.CategoriesGW;
import com.sridama.eztrack.bo.PDFPrintSaleReport;
import com.sridama.eztrack.rfidinteg.ReadTaglist;
import com.sridama.eztrack.rfidinteg.Writetags;
import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.eztrack.utils.Utils;
import com.sridama.eztrack.xl.ExportReportPurchase;
import com.sridama.eztrack.xl.ExportReportSale;
import com.sridama.eztrack.xl.ExportReportStockTransfer;
import com.sridama.txngw.core.RequestResponse;
//import javax.servlet.annotation.WebServlet;

/**
 * Servlet implementation class EzTrackServlet
 */
public class EzTrackServlet extends HttpServlet {
	private final static Logger LOGGER = Logger.getLogger(EzTrackServlet.class
			.getName());
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EzTrackServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init(ServletConfig config) throws ServletException {
		System.out.println("Log4JInitServlet is initializing log4j");
		String log4jLocation = config
				.getInitParameter("log4j-properties-location");
		// to make linux compatable \\ for linux and / for windows
		String delimeter = config
				.getInitParameter("delimeter");

		ServletContext sc = config.getServletContext();

		if (log4jLocation == null) {
			System.err
					.println("*** No log4j-properties-location init param, so initializing log4j with BasicConfigurator");
			BasicConfigurator.configure();
		} else {
			String webAppPath = sc.getRealPath(delimeter);
			String log4jProp = webAppPath + log4jLocation;
			System.setProperty("app.root", webAppPath + "logs");

			System.out.println(webAppPath + "logs");

			File logFile = new File(log4jProp);
			if (logFile.exists()) {
				System.out.println("Initializing log4j with: " + log4jProp);
				PropertyConfigurator.configure(log4jProp);
			} else {
				//System.err
						//println("*** "
						//		+ log4jProp
						//		+ " file not found, so initializing log4j with BasicConfigurator");
				BasicConfigurator.configure();
			}
		}
		super.init(config);
		try {
			JDBCHelper.init();
	 	} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}


	protected void doPost(HttpServletRequest request ,
			 HttpServletResponse response ) throws ServletException, IOException {

		ServletContext servletContext = request.getSession().getServletContext();
		String contextPath = servletContext.getRealPath("/");
		
		LOGGER.debug("context path" +contextPath ) ;
		
		
		RequestResponse req = new RequestResponse(request.getParameter("data"));
		req.setParam("contextPath", contextPath) ;
		req.setParam("queryString", request.getQueryString());

		// Extract opcode from the request.
		int opCode = Integer.parseInt( request.getParameter("opcode") );
		
		req.setParam("opcode" , request.getParameter("opcode") );
		

		//System.out.println("opcode "+opCode);
		Customer cust = null ;
		Item item = null ;
		User user = null ;
		LOGGER.debug("opcode coming from UI"+opCode);
		
		/*
		 * checking for license 
		 * if not valid should ask for next license file
		 */
		//License lice = new License();
		/*if (!lice.licenseValid()) {
			LOGGER.debug("License Expired Hence Existing");
			SendResponse(response, Utils.getInvalidLicenseResponse());
			return ;
		}
		*/
		
//		SessionManager sessionObj = null;
//		if (opCode != 0) {
//			sessionObj = new SessionManager(request);
//			if (!sessionObj.checkSession()) {
//				Utils.sendResponse(response, Utils.getInvalidSessionResponse());
//				return ;
//			}
//		}
		
		SessionManager sessionObj = new SessionManager(request);
		
		RequestResponse res =null;
		switch (opCode) {
		case 0:
			doLogin(request, response);
			return ;
		case -1:
			doLogOut(request, response);
			return ;
		case 1:	// Sales Report
			Report rpt = new SaleReport(sessionObj);
			LOGGER.debug("Calling Sales Report method ") ;
			req.setParam("queryString", request.getQueryString() ) ;
			LOGGER.debug("Query String  Sales Report ------------- >"+request.getQueryString());
			if (request.getParameter("type_ahead")!=null)
			     req.setParam("type_ahead", request.getParameter("type_ahead"));
			 res = rpt.generateReport( req );
			break;
			
		case 2 : // Customers report
			Report rptCust = new CustomersReport(sessionObj);
			if (request.getParameter("type_ahead")!=null)
			     req.setParam("type_ahead", request.getParameter("type_ahead"));
			res = rptCust.generateReport ( req );
			break ;

		case 3 : // Stock report
			Report rptStock = new StockReport(sessionObj);
			LOGGER.debug("Calling Stock report method opcode 3 "+req.getParam("type_ahead"));
			req.setParam("queryString", request.getQueryString() ) ;
			if (request.getParameter("type_ahead")!=null)
			     req.setParam("type_ahead", request.getParameter("type_ahead"));
			res = rptStock.generateReport( req );
			break ;

			
		case 4 : // Next Invoice Number Sale	
			Report invNoSale = new InvoiceNoSale(sessionObj);
			res = invNoSale.generateReport(req);
			break ;
			
		case 5 : // Next Invoice Number Purchase 
			Report invNoPur = new InvoiceNoPurchase(sessionObj);
			res = invNoPur.generateReport(req);
			break ;
			
		case 6 : // Making Sale transaction 
			LOGGER.debug("Data from the User interface "+request.getParameter("data"));
			SaleTxn sale  = new SaleTxn(sessionObj);
			res = sale.saleReq(req);
			break ; 
			
		case 7 : // Customer Creation 
			LOGGER.debug("Data from the User interface for customer creation "+request.getParameter("data"));
			cust  = new Customer(req);
			res = cust.save() ;
			break ;
			
		case 8 : // Customer Updation 
			LOGGER.debug("Data from the User interface for customer updation "+request.getParameter("data"));
		    cust  = new Customer(req);
			res = cust.update() ;
			break ;	
			
		case 9 : // Customer Deletion
			LOGGER.debug("Data from the User interface for customer deletion "+request.getParameter("data"));
			cust  = new Customer(req);
			res = cust.delete() ;
			break ;		
			
		case 10 : // Item Creation 
			LOGGER.debug("Data from the User interface for item creation "+request.getParameter("data"));
		    item  = new Item(sessionObj);
			item.loadValues(req);
			res = item.save() ;
			break ;
			
		case 11 : //  Item Updation 
			LOGGER.debug("Data from the User interface for item updation "+request.getParameter("data"));
		    item  = new Item(sessionObj);
			item.loadValues(req);
			if (request.getParameter("type_ahead")!=null)
			     req.setParam("type_ahead", request.getParameter("type_ahead"));
			res = item.updateItem();
			break;	
			

		case 12 : //  Item Deletion 
			LOGGER.debug("Data from the User interface for item deletion "+request.getParameter("data"));
		    item  = new Item(sessionObj);
			item.loadValues(req);
			res = item.delete() ;
			break ;
			
		case 13 : // Gives the next dc number for stock transfer 
			Report dcNoStockTransfer = new DcNoStockTransfer(sessionObj);
			res = dcNoStockTransfer.generateReport(req);
			break ;
			
		case 14 : // Stock Transfer
			StockManager stock = new StockManager(sessionObj);
			res = stock.transferStock(req) ;
			break ;
			

		case 15 : // List of items received at a branch after stock transfer 
			Report streport = new StockTransferReport(sessionObj);
			res =  streport.generateReport( req );
			break ;	
			
		case 16 : // discounts percentages slab
			Report disslab = new DiscountSlabList(sessionObj);
			//res =  disslab.generateReport(new RequestResponse(request.getParameter("data")));
			res =  disslab.generateReport(req);
			break ;	
			

		case 17 : // tax slab  list
			Report taxslab = new TaxSlabList(sessionObj);
			//res =  taxslab.generateReport(new RequestResponse(request.getParameter("data")));
			res =  taxslab.generateReport(req);
			break ;
	
		case 18 : // stock incoming accept and reject operation for stock transfer
			UpdateStockManager updatestock = new UpdateStockManager(sessionObj);
		    res = updatestock.updateStock(req) ; 
			break ;
		
		case 19 : // branch list 
			Report branchlist = new BranchList(sessionObj);
			//res =  branchlist.generateReport(new RequestResponse(request.getParameter("data")));
			res =  branchlist.generateReport(req);
			break ;	
			
		case 20 : // category list
			try {
				res = new RequestResponse(CategoriesGW.getCategories());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break ;	
			
		case 21 : // Make Purchase transaction 
			LOGGER.debug("Data from the User interface for purchase transaction "+request.getParameter("data"));
			PurchaseTxn pur  = new PurchaseTxn(sessionObj);
			res = pur.purReq(req) ;
			break ;
			
		case 22 : // Next item code 
			LOGGER.debug("Data from the User interface for purchase transaction "+request.getParameter("data"));
			NextItemCode nitemcode  = new NextItemCode(sessionObj);
			req.setParam("queryString", request.getQueryString());
			res = nitemcode.generateReport(req);
			break ;

		case 23 :	// Purchases Report
			 LOGGER.debug("Data from the User interface for purchase reports "+request.getParameter("data"));
			Report prpt = new PurchasesReport(sessionObj);
			req.setParam("queryString", request.getQueryString() ) ;
			if (request.getParameter("type_ahead")!=null)
			     req.setParam("type_ahead", request.getParameter("type_ahead"));
			 res = prpt.generateReport(req);
			
			break;
			
		case 24 :	// Units List
			Report urpt = new UnitsList(sessionObj);
			 //res = urpt.generateReport(new RequestResponse(request.getParameter("data")));
			res = urpt.generateReport(req);
			break;	
			
		case 25 :	// User Creation
			LOGGER.debug("Data from the User interface for user creation "+request.getParameter("data"));
		    user  = new User(req,sessionObj);
			res = user.save() ;
			break ;
			
		case 26 :	// User Updation
			LOGGER.debug("Data from the User interface for user updaiton "+request.getParameter("data"));
		    user  = new User( req,sessionObj);
			res = user.update() ;
			break ;	
			
		case 27 :	// User Deletion
			LOGGER.debug("Data from the User interface for user detetion "+request.getParameter("data"));
		    user  = new User(req,sessionObj);
			res = user.delete() ;
			break ;	
			
		case 28 :	// Sale Invoice number list
			LOGGER.debug("Data from the User interface for user detetion "+request.getParameter("data"));
			Report inrpt = new InvoiceNoListSale(sessionObj);
			 res = inrpt.generateReport(req);
			break;	
			
		case 29 : // invoice item details  
			Report invlist = new SaleTxnDetails(sessionObj);
			res =  invlist.generateReport(req);
			break ;		
			
		case 30 : // total invoice details with item level details for edit  
			Report sedtinv = new SaleEditInvoiceDetails(sessionObj);
			res =  sedtinv.generateReport( req);
			break ;
		
		case 31 : // stock item report detailed
			ItemStockDetailedReport  irep = new ItemStockDetailedReport(sessionObj);
			res = irep.getStockReport( req );
			break ;		
			
		case 32 : // Stock Transfer Report  
			Report steport = new StockTransReport(sessionObj);
			req.setParam("queryString", request.getQueryString());
			res =  steport.generateReport(req);
			break ;	
			
		case 33 : //  Stock Transfer Item Level Details
			Report stidrp = new StockTransDcDetails(sessionObj);
			res =  stidrp.generateReport( req);
			break ;
			
		case 34 : //  Export to XL option for sales Report
			LOGGER.debug(" Exporting to XL "+request.getParameter("data"));
			ExportReportSale exp =  new ExportReportSale(sessionObj);
		    res = exp.generateXl(req); 
			break ;
			
		case 35 : //  Users List
			Report usrrpt =  new UsersList(sessionObj);
			 res = usrrpt.generateReport(req);  
			break ;		
			
		case 36 : //  Export to XL option for purchases report
			LOGGER.debug(" Exporting to XL "+request.getParameter("data"));
			ExportReportPurchase expxl =  new ExportReportPurchase(sessionObj);
		    res = expxl.generateXl(req); 
			break ;	
			
		case 37 : //  Export to XL option for stock trans report
			LOGGER.debug(" Exporting to XL "+request.getParameter("data"));
			ExportReportStockTransfer exptr =  new ExportReportStockTransfer(sessionObj);
		    res = exptr.generateXl(req); 
			break ;	

			
		case 38 :	// unit Deletion
			LOGGER.debug("Data from the User interface for user detetion "+request.getParameter("data"));
			Units   unit1 = new Units( req ,sessionObj);
			res = unit1.delete() ;
			break ;	
		case 39 :	// Tax Updation
			LOGGER.debug("Data from the User interface for user detetion "+request.getParameter("data"));
			TaxSlab tax = new TaxSlab( req ,sessionObj);
			res = tax.update();
			//System.out.println("response is"+res);
			break ;	
		case 40 :	// Discount Update 
			LOGGER.debug("Data from the User interface for user detetion "+request.getParameter("data"));
			DiscountSlab Disc = new DiscountSlab( req,sessionObj);
			res = Disc.update();
			break ;	
			
		case 41 :	// unit Updation
			LOGGER.debug("Data from the User interface for user updaiton "+request.getParameter("data"));
		 Units   unit  = new Units(  req , sessionObj);
			res = unit.update() ;
			break ;	
			
		case 42 : // Stock report
			Report rptStock2 = new StockReport(sessionObj);
			LOGGER.debug("Calling Stock report method  opcode 42 "+ req.getParam("type_ahead"));
			if (request.getParameter("type_ahead")!=null)
			     req.setParam("type_ahead", request.getParameter("type_ahead"));
			res = rptStock2 .generateReport( req );
			break ;	
			
		case 43: // total invoice details with item level details for edit  
			final Report edititem = new ItemEdit(sessionObj);
			LOGGER.debug("Calling Stock Update method ");
			System.out.println("item Id "+req.getParam("item_id"));
			if (request.getParameter("type_ahead")!=null)
			     req.setParam("type_ahead", request.getParameter("type_ahead"));
			res = edititem.generateReport( req );
			break ;
			
		case 44 : // Generation of PDF on a fly for reporting
			LOGGER.debug("Creation of PDF document on a fly "+request.getParameter("data"));
			PDFPrintSaleReport pdf = new PDFPrintSaleReport(sessionObj);
			res = pdf.generatePDF(req);
			break ;	
			
		case 45:
			final ReadTaglist wt = new ReadTaglist(req);
			wt.setSession(sessionObj);
			try
			{
				res = wt.readTag();
			}
			catch (final Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final String json = res.getParam("request");
			System.out.println("jhgjhg n" + json);
			break;
		case 46:
			System.out.println("request from ui is" + request.getParameter("data"));
			final Writetags wrt = new Writetags(new RequestResponse(request.getParameter("data")));
			try
			{
				res = wrt.writeeachtag();
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
			break;	
				
		default : 
		}
		LOGGER.debug("sending response json here");
		Utils.sendResponse(response, res.getParam("request"));
	}

	/**
	 * Performs login authentication check for a given request.
	 * 
	 * @param request
	 */
	private void doLogin(HttpServletRequest request,
			HttpServletResponse response) {

		// Extract the JSON string containing login credentials.
		String json = request.getParameter("data");
		LOGGER.debug("User Request "+json);
	
		try {
			User user = new User(json);
			if (user.authenticate()) {
				SessionManager ses = new SessionManager(user) ;
				ses.createSession();
				response = ses.getCookieResponse(response, "validate");
				Utils.sendResponse(response, Utils.getSuccessResponse());
			}
				LOGGER.debug("Response  "+Utils.getSuccessResponse());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/*
	 * Logs out from the current session, and makes the cookies empty
	 * Removes the entry from the database , and clears the cookie
	 */
	private void doLogOut(HttpServletRequest request,
			HttpServletResponse response) {
		SessionManager session = new SessionManager(request) ;
		session.invalidateSession();
		response = session.getCookieResponse(response, "invalidate");
		Utils.sendResponse(response, Utils.getSuccessResponse());
	}
	
	@Override
	public void destroy() {
		
	}
	
}
