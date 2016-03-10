package com.sridama.eztrack.bo ;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sridama.eztrack.utils.JDBCHelper;

public class FirstPdf  {
	
	// declaring local variables out side
private static  int partyid=0;
private static 	int txn_id =0;
private static 	String txn_date="";
private static 	String name="";
private static 	String address="";
private static 	String phone1="";
private static 	double Vattotal = 0;
private static 	double amttotal=0;
private static 	String cstno="";
private static 	long tinno = 0;
private static 	int payment_type = 0 ;

	
	
	
	public static int ori = 0 ;
	private  static Connection connection  ;             // = JDBCHelper.getConnection();
	
	ResultSet rsdata = null;
//	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	//String g=classLoader.getResource("/EzTrack18/WebContent/img/firstpdf.pdf").getPath();
 // private static String FILE = "E:/BackUp/Backup/New folder/new/EzTrack18/WebContent/FirstPdf.pdf";
  private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
      Font.BOLD);
  private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN , 12,
      Font.NORMAL, BaseColor.RED);
  private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
      Font.BOLD);
  private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
      Font.BOLD);
  private static Font small = new Font(Font.FontFamily.TIMES_ROMAN, 8);
  private static Font smallter = new Font(Font.FontFamily.TIMES_ROMAN, 9);
  private static Font tabdata = new Font(Font.FontFamily.TIMES_ROMAN, 11);
  //PdfTemplate total;
private static float roffDiscount;
private static float addtnlChrgs;
private static int qty;
private static  int qtySum = 0 ;
private  static float amountSum = 0.0f ;
  
  
  public FirstPdf() {
	  try {
		connection = JDBCHelper.getConnection();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
  
  
  public static void main(String[] args) {
    try {
    	//System.out.println("fsdbfd");
    	FirstPdf nn=new FirstPdf();
    	File f = new File("");
    	//System.out.println("Getting Absolute path "+f.getAbsolutePath());
    	nn.invoivepdf("220",f.getCanonicalPath(),1);   //E:/SPORTSLINE_RV_SIR/eazytrack
    	 File myFile = new File("E:/SPORTSLINE_RV_SIR/eazytrack/WebContent/FirstPdf.pdf");
         Desktop.getDesktop().open(myFile);
  /*    Document document = new Document();
      PdfWriter.getInstance(document, new FileOutputStream(FILE));
      document.open();
      addMetaData(document);
    addTitlePage(document);
    //  addContent(document);
      document.close();*/
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public void onEndPage(PdfWriter writer, Document document) {
      Rectangle rect = writer.getBoxSize("art");
      ColumnText.showTextAligned(writer.getDirectContent(),
              Element.ALIGN_LEFT, new Phrase(String.format("Page %d of", writer.getPageNumber())),
              (rect.getLeft() + rect.getRight()) / 2, rect.getBottom() - 18, 0);  
      } 

  // iText allows to add metadata to the PDF which can be viewed in your Adobe
  // Reader
  // under File -> Properties
  private static void addMetaData(Document document) {
    document.addTitle("Invoice"); 
    document.addSubject("Tax Invoice");
    document.addKeywords("Sports line, PDF, Sridama");
    document.addAuthor("Info@sridama");
    document.addCreator("info@sridama");
  }

  
  
  public static void updateData(int branchId,String invoiceno)   {
	  
	  
	  //System.out.println("********\n");
	  //System.out.println("brid : "+branchId+" invno : "+invoiceno);
	  
	    java.sql.ResultSet rs= null;
		java.sql.ResultSet rspartyid;
	
		
/*
*    Holds the values of the transaction from Database  for PDF printing.
*/
		try {
		Statement statement = connection.createStatement();
	
		StringBuilder sb = new StringBuilder();
		sb.append("select txn.txn_date,txn.party_id,txn.txn_id,client.name,client.address,client.phone1 , txn.payment_type  from item_txn txn ");
		sb.append("INNER JOIN client_master client ON ");
		sb.append("txn.party_id=client.cust_id where txn.branch_id="+branchId+" AND txn.invoice_no="+invoiceno);		
		
		//System.out.println(sb.toString());
		
		rspartyid = statement.executeQuery( sb.toString() );
	
		while (rspartyid.next()) {
			
		partyid = rspartyid.getInt("party_id");	
		txn_id= rspartyid.getInt("txn_id");	
		txn_date= rspartyid.getString("txn_date");
		name=rspartyid.getString("name");
		address=rspartyid.getString("address");
		phone1=rspartyid.getString("phone1");
		
		
		//
		payment_type=rspartyid.getInt("payment_type");
		
	//	System.out.println("party_id is"+partyid+"  "+payment_type);
		
		}

	} catch (SQLException e) {
		e.printStackTrace();
	}
  }
  
  private static void addTitlePage(Document document,String invoiceno, int branchId)
      throws DocumentException {
	  
   updateData(branchId, invoiceno) ;  // updates the values to class variables for easy access
if(ori == 1) 
{
	Paragraph prefaceori = new Paragraph();
    prefaceori.setAlignment(Element.ALIGN_RIGHT);
    prefaceori.add(new Paragraph("Orignal Copy",
    	      small));
    document.add(prefaceori);
}   
if(ori == 0) 
{
	Paragraph prefaceori = new Paragraph();
    prefaceori.setAlignment(Element.ALIGN_RIGHT);
    prefaceori.add(new Paragraph("Customer Copy", small));
    document.add(prefaceori);
    
}
    
    
	Paragraph preface = new Paragraph();
    preface.setAlignment(Element.ALIGN_CENTER);
    // We add one empty line

   /*
    * Adding the branch address as heading    
    */
    if (branchId==1) {
    	preface.add(new Paragraph("Sports Line,#14/5,QUEENS ROAD \n,NEXT TO SRI KRISHNA UPHAR ,BANGLORE-560052 \n TAX INVOICE",
    		      smallBold));
    		    
   } else if (branchId==2) {
	   preface.add(new Paragraph("Sports Line,DOMLUR, BANGALORE",smallBold));	
   }else if (branchId==3) {
	   preface.add(new Paragraph("Sports Line,GANGA NAGAR, BANGALORE",smallBold));	
   }else if (branchId==4) {
	   preface.add(new Paragraph("Sports Line,#14/5, MAHADEVA PURA, BANGALORE ",smallBold));
   }
    preface.add(new Paragraph("TIN NO-29660320234 CST NO - 0398547/7",
		      smallBold));
	
   
    addEmptyLine(preface,1);

    document.add(preface);
    
    PdfPTable table4 = new PdfPTable(1);
    table4.setWidthPercentage(525 / 5.23f);
    PdfPCell c5 = new PdfPCell(new Phrase("Invoice No : "+invoiceno));
    c5.setHorizontalAlignment(Element.ALIGN_LEFT);
    table4.addCell(c5);
    c5 = new PdfPCell(new Phrase("Dated  : " +txn_date));
    c5.setHorizontalAlignment(Element.ALIGN_LEFT);
    table4.addCell(c5);
/*    c5 = new PdfPCell(new Phrase("Delivery Note:" ));
    c5.setHorizontalAlignment(Element.ALIGN_LEFT);
    table4.addCell(c5);*/
    if (payment_type == 0) {
    c5 = new PdfPCell(new Phrase("Mode/Payment  : "+"cash" )); 
    } else if (payment_type == 2 ) {
    	c5 = new PdfPCell(new Phrase("Mode/Payment  : "+"card" ));	
    } else if (payment_type == 1)  {
    	c5 = new PdfPCell(new Phrase("Mode/Payment  : "+"cheque" ));
    }
    c5.setHorizontalAlignment(Element.ALIGN_LEFT);
    table4.addCell(c5);
        
    
    PdfPTable table3 = new PdfPTable(2);
    table3.setWidthPercentage(500 / 5.23f);
    table3.setWidths(new int[]{1,2});

    
    PdfPCell c4 = null ;
    c4 = new PdfPCell(new Phrase("Buyer  \n"+ name+ " \n" + address ));
    c4.setHorizontalAlignment(Element.ALIGN_LEFT);
    c4.setFixedHeight(72f);
    table3.addCell(c4);

    c4 = new PdfPCell();
    c4.addElement(table4);
    c4.setBorder(Rectangle.LEFT|Rectangle.RIGHT|Rectangle.TOP|Rectangle.BOTTOM);
     table3.addCell(c4);
  
    document.add(table3);
    
    
    Paragraph preface1 = new Paragraph();
    preface1.setAlignment(Element.ALIGN_LEFT);
   
    preface1.add(new Paragraph("", catFont));

    document.add(preface1);

    PdfPTable table1 = new PdfPTable(8);
    table1.setWidthPercentage(500 / 5.23f);
    table1.setWidths(new int[]{1, 2, 1,1,1,1,1,1});

// Item list array of an invoice
    String a[] = {"Sl NO","Item Name","Qty","MRP","Disc %","Disc Amt","Vat/Cst %","Amount"};
    PdfPCell c2 = null ;
    for (String title : a){
        c2 = new PdfPCell(new Phrase(title));
        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table1.addCell(c2);
    }
    table1.setHeaderRows(1);
    
    
    int i=1;
    java.sql.ResultSet rs1  ;
    java.sql.ResultSet rs =null ;
    String ItemName = "" ;
	try {
		
		Statement statement = connection.createStatement(),
				statement1 = connection.createStatement();
	
		StringBuilder sb = new StringBuilder();
		sb.append (" select  txn.item,txn.quantity,txn.discount,txn.amount,txn.rate,txn.dscnt_percent,txn.vat_percent,txn.catagory ");
		sb.append (", item.name from txn_details txn INNER JOIN item_master item ON txn.item=item.id  where txn.txnid="+txn_id );
		
		//System.out.println(sb.toString());
		rs1 = statement.executeQuery(sb.toString() );
		//System.out.println(txn_id);
			
		while (rs1.next()) {
				c2 = getPdfPCell("" + i);
				table1.addCell(c2);
				c2 = getPdfPCell(rs1.getString("name"));
				table1.addCell(c2);
				c2 = getPdfPCell(rs1.getString("quantity"));
				table1.addCell(c2);
				qtySum+= rs1.getInt("quantity"); // to get sum of quantity
				c2 = getPdfPCell(""+rs1.getFloat("rate"));
				table1.addCell(c2);
				c2 = getPdfPCell(""+rs1.getFloat("dscnt_percent"));
				table1.addCell(c2);
				c2 = getPdfPCell("" + rs1.getFloat("discount"));
				table1.addCell(c2);
				c2 = getPdfPCell("" + rs1.getFloat("vat_percent"));
				table1.addCell(c2);
				c2 = getPdfPCell("" + rs1.getFloat("amount"));
				table1.addCell(c2);
				amountSum += rs1.getFloat("amount") ;

				i++; // increment i for serial number
			}
	} catch (SQLException e) {
		e.printStackTrace();
	}
    
    
    document.add(table1);
    
    
    PdfPTable table9 = new PdfPTable(8);
    table9.setWidthPercentage(500 / 5.23f);
    table9.setWidths(new int[]{1, 2, 1,1,1,1,1,1});
	c2 = getPdfPCell("");
	c2.setBorder(Rectangle.BOTTOM | Rectangle.LEFT |Rectangle.RIGHT|Rectangle.TOP);
	table9.addCell(c2);
	
	c2 = getPdfPCell("");
	c2.setBorder(Rectangle.BOTTOM | Rectangle.LEFT |Rectangle.RIGHT|Rectangle.TOP);
	table9.addCell(c2);
	
	c2 = getPdfPCell(""+qtySum);
	c2.setBorder(Rectangle.BOTTOM | Rectangle.LEFT |Rectangle.RIGHT|Rectangle.TOP);
	table9.addCell(c2);
	qtySum = 0 ;
	
	c2 = getPdfPCell("");
	c2.setBorder(Rectangle.BOTTOM | Rectangle.LEFT |Rectangle.RIGHT|Rectangle.TOP);
	table9.addCell(c2);
	
	c2 = getPdfPCell("");
	c2.setBorder(Rectangle.BOTTOM | Rectangle.LEFT |Rectangle.RIGHT|Rectangle.TOP);
	table9.addCell(c2);
	
	c2 = getPdfPCell("");
	c2.setBorder(Rectangle.BOTTOM | Rectangle.LEFT |Rectangle.RIGHT|Rectangle.TOP);
	table9.addCell(c2);
	
	c2 = getPdfPCell("");
	c2.setBorder(Rectangle.BOTTOM | Rectangle.LEFT |Rectangle.RIGHT|Rectangle.TOP);
	table9.addCell(c2);
	
	c2 = getPdfPCell(""+amountSum);
	c2.setBorder(Rectangle.BOTTOM | Rectangle.LEFT |Rectangle.RIGHT|Rectangle.TOP);
	table9.addCell(c2);
	
	amountSum = 0.0f ;  // use and clear   check calculation mistake is coming in PDF
	
	document.add(table9) ;

    Statement statement = null  ;
 //   java.sql.ResultSet  rs = null  ;
	try {
		statement = connection.createStatement();
		StringBuilder sinv = new StringBuilder();
		sinv.append("select txn.pur_inv_date, txn.invoice_no, txn.txn_date ,txn.grand_total,txn.txn_id ,");
		sinv.append("txn.totalvat_fourteen ,txn.transportation , txn.discount , txn.totalvat_five,cust.name , cust.cst_no , cust.address,");
		sinv.append("cust.phone1 , cust.TIN , cust.cust_id  from item_txn txn INNER JOIN ");
		sinv.append("client_master cust ON  txn.party_id = cust.cust_id where txn.txn_code='s' and txn.invoice_no="+invoiceno);
		sinv.append(" AND txn.branch_id="+branchId );

		//System.out.println( sinv.toString() );
		rs = statement.executeQuery(sinv.toString());
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	finally {
			try {
			//	if (statement!=null) statement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	/*
	 * to have calculation of indivisual vat percentages
	 */
	PdfPCell c3 = null ;
	Statement stmt = null ;
	ResultSet rstax = null ;
	
	 try {
		while(rs.next())
		{
			cstno=rs.getString("cst_no");
			tinno=rs.getLong("TIN");
			Vattotal=rs.getDouble("totalvat_five")+rs.getDouble("totalvat_fourteen");
			amttotal=rs.getDouble("grand_total");
			addtnlChrgs = rs.getFloat("transportation");
			roffDiscount = rs.getFloat("discount");
		
			stmt = connection.createStatement();
			StringBuilder sbtax = new StringBuilder();
			sbtax.append("select vat_percent , sum(amount*(vat_percent/100)) from txn_details where txnid="+txn_id+" group by vat_percent");
		    rstax = stmt .executeQuery(sbtax.toString()); 
		    //System.out.println("vat classification query "+sbtax.toString()); 
		    
		} // end of while
	    
		PdfPTable table7 = new PdfPTable(1);
	    table7.setWidthPercentage(500 / 5.23f);

	    table7.setWidths(new int[]{3});

		double totVat = 0.0d ;
		    
			while (rstax.next()) {
				
				c3 = new PdfPCell(new Phrase(" vat/cst@" +rstax.getDouble(1)+  " : "+rstax.getDouble(2),smallBold));
				c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
			    c3.setBorder(Rectangle.BOTTOM | Rectangle.LEFT |Rectangle.RIGHT|Rectangle.TOP);
				table7.addCell(c3);
				totVat+=rstax.getDouble(2) ;// calculating sum of all taxes
			}  // End of while
		
			document.add(table7);
			
			/*
			 * to add the  the total amount column
			 */

		    PdfPTable table2 = new PdfPTable(2);
		    table2.setWidthPercentage(500 / 5.23f);

		    table2.setWidths(new int[]{1,3});

			
		    c3 = new PdfPCell(new Phrase("Total",smallBold));
			c3.setHorizontalAlignment(Element.ALIGN_LEFT);
			c3.setBorder(Rectangle.BOTTOM | Rectangle.LEFT|Rectangle.TOP);
			table2.addCell(c3);
			
		//	document.add(table2);
		    
		//	document.add(table2);
			c3 = new PdfPCell(new Phrase(""+amttotal,smallBold));
			c3.setHorizontalAlignment(Element.ALIGN_RIGHT);
			c3.setBorder(Rectangle.BOTTOM | Rectangle.RIGHT|Rectangle.TOP);
			table2.addCell(c3);
			
			document.add(table2);	

		
			PdfPTable table8 = new PdfPTable(1);
		    table8.setWidthPercentage(500 / 5.23f);

			// to add the total vat at the end
			c3 = new PdfPCell( new Phrase("Total Vat : "+totVat, smallBold) );
			c3.setBorder(Rectangle.BOTTOM| Rectangle.LEFT |Rectangle.RIGHT|Rectangle.TOP);
			table8.addCell(c3);
	
			document.add(table8);
			
			
		Paragraph preface3 = new Paragraph();
		preface3.setAlignment(Element.ALIGN_LEFT);
		// We add one empty line
		
		// Lets write a big header
		
 
		preface3.add(new Paragraph(""));
		document.add(preface3);
		Paragraph preface4 = new Paragraph();
		preface4.setAlignment(Element.ALIGN_LEFT);
		// We add one empty line
    	String worvat  = EnglishNumberToWords.convert((long)totVat);
		String wordtot = EnglishNumberToWords.convert((long)amttotal);
		// Lets write a big header
		//select txn.invoice_no, txn.txn_date ,txn.grand_total,txn.txn_id ,txn.totalvat_fourteen , txn.totalvat_five,cust.name,cust.cst_no,cust.address,cust.phone1,cust.TIN  from item_txn txn INNER JOIN client_master cust ON  txn.party_id = cust.cust_id where txn.txn_code='s' and txn.invoice_no="+k;
		

		preface4.add(new Paragraph("Amount Chargeable (in words)",small));
		preface4.add(new Paragraph("INR "+wordtot));
	
	/*	preface4.add(new Paragraph("Vat Chargeable (in words)",small));
		preface4.add(new Paragraph("INR "+worvat));
		
		
		preface4.add(new Paragraph("Company's VAT TIN : 29660320234",small));
		preface4.add(new Paragraph("Company's CST No. : 0398547/7",small));
		preface4.add(new Paragraph("Buyer's VAT TIN :"+tinno,small));
		preface4.add(new Paragraph("Buyer's CST No :"+cstno,small));
	*/	
		
		/* We declare that this invoice shows the actual price of
		 the goods described and that all particulars are true
		 and correct.*/
		 
		Chunk underline = new Chunk("Declaration \n");
		underline.setUnderline(0.1f, -2f); //0.1 thick, -2 y-location
		

		Chunk data = new Chunk(" We declare that this invoice shows the actual price of  the goods described and that all particulars are true  and correct.",small);
		 
		 addEmptyLine(preface4,1);
		 PdfPTable table6 = new PdfPTable(2);
		    table6.setWidthPercentage(500 / 5.23f);
		    table6.setWidths(new int[]{1,1});

		    // t.setBorderColor(BaseColor.GRAY);
		    // t.setPadding(4);
		    // t.setSpacing(4);
		    // t.setBorderWidth(1);

		    PdfPCell c6 = new PdfPCell(new Phrase("Customer's Seal and Signature"));
		    c6.setHorizontalAlignment(Element.ALIGN_LEFT);
		  
		    c6.setFixedHeight(50f);
		    table6.addCell(c6);

		    c6 = new PdfPCell(new Phrase("Authorised Signatory"));
		    c6.setFixedHeight(50f);
		 // c6.addElement(table6);
		    table6.addCell(c6);
		
		
		document.add(preface4);
		document.add(underline);
		document.add(data);
		document.add(table6);
		
	} catch (SQLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}  finally {
			try {
			//	if (rstax!=null) rstax.close() ;
			//	if (rs!=null) rs.close() ;
			//	if (stmt!=null) stmt.close() ;
			//	if (statement!=null) statement.close();
			String s=	"";
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}
    Paragraph preface5 = new Paragraph();
    preface5.setAlignment(Element.ALIGN_LEFT);
    // We add one empty line

    // Lets write a big header
 
    preface5.add(new Paragraph("Terms & conditions",smallter));
   
    // Will create: Report generated by: _name, _date
    preface5.add(new Paragraph("1. Goods once sold cannot be taken back or exchanged.",small));

    preface5.add(new Paragraph("2. We are not ressponsible for any transport delay or any other delay which is beyond our control",small));
     preface5.add(new Paragraph("3. Goods are inclucive of all taxes.",small));
    
    addEmptyLine(preface,5);
   
    document.add(preface5);
    
    Paragraph preface8 = new Paragraph();
    preface8.setAlignment(Element.ALIGN_CENTER);
    preface8.add(new Paragraph("SUBJECT TO BANGALORE JURISDICTION",small));
    addEmptyLine(preface5,12);
    preface8.setFirstLineIndent(55f);
    document.add(preface8);
    try {
    	
    	
    String FILE1 = "E:/EZTRACK/eazytrack/WebContent/Hydrangeas.jpg";
    	// String FILE1 = "E:/BackUp/Backup/New folder/new/EzTrack18/WebContent/img/fsd.png";
		Image image = Image.getInstance(FILE1);
	image.setAbsolutePosition(400f,170f);
		//document.add(image);
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

   
  }
  
  private static void addEmptyLine(Paragraph paragraph, int number) {
    for (int i = 0; i < number; i++) {
      paragraph.add(new Paragraph(" "));
    }
  }
  
  public void invoivepdf(String invoiceno,String path , int brCode)
 {
		Document document = new Document();
		try {
			//System.out.println("context path" + path);
			//System.out.println(path + "//" + invoiceno + ":" + brCode + ".pdf");
			//System.out.println("complete context path" + path + "/tmp/"
			//		+"inv_"+ invoiceno + "_" + brCode + ".pdf");
			PdfWriter.getInstance(document, new FileOutputStream(path + "/tmp/"
					+"inv_"+ invoiceno + "_" + brCode + ".pdf"));

		} catch (FileNotFoundException | DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		document.open();
		addMetaData(document);
		try {
			ori = 1;
			addTitlePage(document, invoiceno, brCode);
         //  System.gc();
		} catch (DocumentException e) {
			//System.out
			//		.println("Exception occured while writing to the PDF file"
			//				+ e);
			e.printStackTrace();
		}

		try {
			ori = 0;
			document.newPage();
			addTitlePage(document, invoiceno, brCode);
		  //   System.gc();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			//System.out
			//		.println("Exception occured while writing to the PDF file"
			//				+ e);
			e.printStackTrace();
		}
		document.close();
	}
  
  
  /*
   * returns the PdfPCell object provided a string
   */
	public static PdfPCell getPdfPCell(String phrase) {
		PdfPCell c2 = new PdfPCell(new Phrase(phrase, tabdata));
		c2.setHorizontalAlignment(Element.ALIGN_CENTER);
		c2.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
		return c2;
	}
  
	
	protected void finalize() throws Throwable {
	     try {
	 //       if (connection!=null) connection.close();        // close open files
	     } finally {
	         super.finalize();
	     }
	 }
} 