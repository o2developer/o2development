/**
 * 
 */
package com.sridama.eztrack.bo;

/**
 * @author Rizwan
 *
 */
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

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.sridama.eztrack.utils.JDBCHelper;

public class PDFStockTransfer {
	private  Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
		      Font.BOLD);
		  private  Font redFont = new Font(Font.FontFamily.TIMES_ROMAN , 12,
		      Font.NORMAL, BaseColor.RED);
		  private  Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
		      Font.BOLD);
		  private  Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
		      Font.BOLD);
		  private  Font small = new Font(Font.FontFamily.TIMES_ROMAN, 8);
		  private  Font smallter = new Font(Font.FontFamily.TIMES_ROMAN, 9);
		  private  Font tabdata = new Font(Font.FontFamily.TIMES_ROMAN, 11);
		  
		  private int ori ;
		  private String branchAddress ;
		  private String txnDate ;
		  private int txnId ;
		  private float totalAmount;
		  private int totalQty ;
		  private String esugam ;
		  private String vehicleNo ;
		  
		  public void invoivepdf( String dcNo,String path , int brCode ,int txnId)
		  {
			  this.txnId = txnId ;
		 		Document document = new Document();
		 		try {
		 			//System.out.println("context path" + path);
		 			//System.out.println(path + "//" + dcNo + ":" + brCode + ".pdf");
		 			//System.out.println("complete context path" + path + "/tmp/"
		 			//		+"dc_"+ dcNo + "_" + brCode + ".pdf");
		 			PdfWriter.getInstance(document, new FileOutputStream(path + "/tmp/"
		 					+"dc_"+ dcNo + "_" + brCode + ".pdf"));

		 		} catch (FileNotFoundException | DocumentException e) {
		 			// TODO Auto-generated catch block
		 			e.printStackTrace();
		 		}
		 		document.open();
		 		addMetaData(document);
		 		/*try {
		 			ori = 1;
		 			addMainContent(document, dcNo, brCode);

		 		} catch (DocumentException e) {
		 			System.out
		 					.println("Exception occured while writing to the PDF file"
		 							+ e);
		 			e.printStackTrace();
		 		}*/

		 		try {
		 			ori = 0;
		 			document.newPage();
		 			addMainContent(document, dcNo, brCode);
		 		} catch (DocumentException e) {
		 			// TODO Auto-generated catch block
		 			//System.out
		 			//		.println("Exception occured while writing to the PDF file"
		 			//				+ e);
		 			e.printStackTrace();
		 		}
		 		document.close();
		 	}
		  
		  
		// iText allows to add metadata to the PDF which can be viewed in your Adobe
		  // Reader
		  // under File -> Properties
		  private  void addMetaData(Document document) {
		    document.addTitle("Invoice"); 
		    document.addSubject("Tax Invoice");
		    document.addKeywords("Sports line, PDF, Sridama");
		    document.addAuthor("Info@sridama");
		    document.addCreator("info@sridama");
		  }
		  
		  private void addMainContent(Document document,String dcNo, int branchId)
			      throws DocumentException {
			  
			  updateData(branchId, dcNo) ;  // updates the values to class variables for easy access
			  if(ori == 1) 
			  {
			  	Paragraph prefaceori = new Paragraph();
			      prefaceori.setAlignment(Element.ALIGN_RIGHT);
			      prefaceori.add(new Paragraph("Orignal Copy",
			      	      small));
			      //document.add(prefaceori);
			  }   
			  if(ori == 0) 
			  {
			  	Paragraph prefaceori = new Paragraph();
			      prefaceori.setAlignment(Element.ALIGN_RIGHT);
			      prefaceori.add(new Paragraph("Customer Copy",
			      	      small));
			     // document.add(prefaceori);
			      
			  }
			  //adding branch address as a heading 
			  Paragraph preface = new Paragraph();
			  document.add( addBranchAddress(branchId,preface) );
			  
			  PdfPTable table4 = new PdfPTable(1);
			    table4.setWidthPercentage(525 / 5.23f);
			    PdfPCell c5 = new PdfPCell(new Phrase("DC No : "+dcNo));
			    c5.setHorizontalAlignment(Element.ALIGN_LEFT);
			    table4.addCell(c5);
			    c5 = new PdfPCell(new Phrase("Dated  : " +txnDate));   //txnDate
			    c5.setHorizontalAlignment(Element.ALIGN_LEFT);
			    table4.addCell(c5);
			    c5 = new PdfPCell(new Phrase("Esugam  : "+esugam ));   //Esugam number from Front end
			    c5.setHorizontalAlignment(Element.ALIGN_LEFT);
			    table4.addCell(c5);
			    c5 = new PdfPCell(new Phrase("Vehicle No  : "+vehicleNo ));   //Vehicle number from Front end
			    c5.setHorizontalAlignment(Element.ALIGN_LEFT);
			    table4.addCell(c5);
			
			    PdfPTable table3 = new PdfPTable(2);
			    table3.setWidthPercentage(500 / 5.23f);
			    table3.setWidths(new int[]{1,2});

			    
			    PdfPCell c4 = null ;
			    c4 = new PdfPCell(new Phrase("To Branch  \n"+ branchAddress ));
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

			    PdfPTable table1 = new PdfPTable(7);
			    table1.setWidthPercentage(500 / 5.23f);
			    table1.setWidths(new int[]{1, 2, 1,1,1,1,1});

			// Item list array of an invoice
			    String a[] = {"Sl NO","Item Name", "Qty","Units","Cost Price","MRP","Amount"};
			    PdfPCell c2 = null ;
			    for (String title : a){
			        c2 = new PdfPCell(new Phrase(title));
			        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
			        table1.addCell(c2);
			    }
			    table1.setHeaderRows(1);
			    
			    
			    int i=1;
			    java.sql.ResultSet rs1 = null,rs=null;
			    String ItemName = "" ;
			    Connection con = null ;
				try {
					con = JDBCHelper.getConnection() ;	
					Statement statement = con.createStatement(),
							statement1  = con.createStatement();
				
					StringBuilder sb = new StringBuilder();
					sb.append("select txn.sitemid,txn.aqty,txn.catagory ");
					sb.append(", item.name,item.cost,item.units,item.rate from stock_txn_details txn INNER JOIN " );
					sb.append("item_master item ON txn.sitemid=item.id  where txn.stxnid="+txnId );
					//System.out.println(sb.toString());
					rs1 = statement.executeQuery(sb.toString() );
						
					while (rs1.next()) {
							c2 = getPdfPCell("" + i,"right");
							table1.addCell(c2);
							c2 = getPdfPCell(rs1.getString("name"),"left");
							table1.addCell(c2);
							totalQty+= rs1.getInt("aqty") ;
							c2 = getPdfPCell(rs1.getString("aqty"),"right");
							table1.addCell(c2);
							c2 = getPdfPCell(rs1.getString("units"),"left");
							table1.addCell(c2);
							c2 = getPdfPCell(rs1.getString("cost"),"right");
							table1.addCell(c2);
							c2 = getPdfPCell(rs1.getString("rate"),"right");
							table1.addCell(c2);
							totalAmount+=rs1.getFloat("cost") * rs1.getInt("aqty");
							c2 = getPdfPCell(""+( String.format("%.02f", ( rs1.getFloat("cost") * rs1.getInt("aqty")) ) ), "right");
							table1.addCell(c2);
							i++; // increment i for serial number
						}
					

				} catch (SQLException e) {
					e.printStackTrace();
				}catch (ClassNotFoundException e) {
					e.printStackTrace() ;
				}catch (IOException e ) {
					e.printStackTrace() ;
				}
				finally {
					try {
						if (rs!=null) rs.close();
						if (rs1!=null) rs1.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			    
			    
			    document.add(table1);

			    PdfPTable table8 = new PdfPTable(7);
			    table8.setWidthPercentage(500 / 5.23f);
			    table8.setWidths(new int[]{1, 2, 1,1,1,1,1});
			    
			    for (int loop= 1 ; loop < 8 ; loop++) {
			    	if (loop==1) {
			         c2 = getPdfPCellTotal("Total",1);
			    	}else if(loop==3) {
				    	 c2 = getPdfPCellTotal(""+totalQty,3);	
				    }else if(loop==7) {
			    	 c2 = getPdfPCellTotal(""+(String.format("%.02f",totalAmount)),7);	
			    	}else {
			    	 c2 = getPdfPCellTotal("",-1);
			    	}
				table8.addCell(c2);
			    }
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
			    //	String worvat  = EnglishNumberToWords.convert((long)totVat);
					String wordtot = EnglishNumberToWords.convert((long)totalAmount);
					// Lets write a big header
					//select txn.invoice_no, txn.txn_date ,txn.grand_total,txn.txn_id ,txn.totalvat_fourteen , txn.totalvat_five,cust.name,cust.cst_no,cust.address,cust.phone1,cust.TIN  from item_txn txn INNER JOIN client_master cust ON  txn.party_id = cust.cust_id where txn.txn_code='s' and txn.invoice_no="+k;
					

					preface4.add(new Paragraph("Total Amount (in words)",small));
			    	preface4.add(new Paragraph("INR "+wordtot));
				
					 
					Chunk underline = new Chunk("For Sports Line \n");
					underline.setUnderline(0.1f, -2f); //0.1 thick, -2 y-location
					

					Chunk data = new Chunk(" We declare that this invoice shows the actual price of " +
							" the goods described and that all particulars are true  and correct.",small);
					 
					 addEmptyLine(preface4,1);
					 /*PdfPTable table6 = new PdfPTable(1);
					    table6.setWidthPercentage(500 / 5.23f);
					    table6.setWidths(new int[]{1});

					    PdfPCell c6 = new PdfPCell(new Phrase("Authorised Signatory"));
					    c6.setFixedHeight(30f);
					 // c6.addElement(table6);
					    table6.addCell(c6);
*/					    
					    Paragraph signature = new Paragraph("\nAuthorised Signatory");
					    signature.add(new Paragraph());
					
					 document.add(preface4);
					document.add(underline);
					//document.add(data);
					document.add(signature);
					
			
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
			   
			   // document.add(preface5);
			    
			    Paragraph preface8 = new Paragraph();
			    preface8.setAlignment(Element.ALIGN_CENTER);
			    preface8.add(new Paragraph("SUBJECT TO BANGALORE JURISDICTION",small));
			    addEmptyLine(preface5,12);
			    preface8.setFirstLineIndent(55f);
			  //  document.add(preface8);
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
		  
		  /*
		   * updates the variables for access 
		   */
		  private void updateData(int branchId, String dcNo) {
			
			  StringBuilder sb = new StringBuilder() ;
			  sb.append("select txn.txn_date,txn.esugam , txn.vehicle_no ,txn.to_branch,branch.br_address from stock_txn txn ");
			  sb.append("INNER JOIN branch_master branch ON txn.to_branch=branch.br_code ");
			  sb.append("where txn.txn_id="+txnId );
			  Connection con =null ;
			  Statement stmt = null ;
			  ResultSet rs =null ;
			try {
				con = JDBCHelper.getConnection();
				stmt = con.createStatement() ;
				rs = stmt.executeQuery(sb.toString()) ;
				//System.out.println(sb.toString());
			while (rs.next()) {
					this.txnDate = rs.getString(1);
				    this.esugam = rs.getString(2);
				    this.vehicleNo = rs.getString(3);
				    this.branchAddress  = rs.getString(5) ; 
			}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (rs!=null) rs.close();
					if (stmt!=null) stmt.close();
					//if (con != null) con.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
			}
		}


		private Paragraph  addBranchAddress(int branchId,Paragraph preface) {
			
			  preface.setAlignment(Element.ALIGN_CENTER);
			   /*
			    * Adding the branch address as heading    
			    */
			  preface.add(new Paragraph("SPORTS LINE",
				      smallBold));
			    if (branchId==1) {
			    	preface.add(new Paragraph("#14/5,QUEENS ROAD \n,NEXT TO SRI KRISHNA UPHAR ," +
			    			"BANGLORE-560052 \n  DELIVERY CHALLAN",
			    		      smallBold));
			    		    
			   } else if (branchId==2) {
				   preface.add(new Paragraph(" DOMLUR, BANGALORE",smallBold));	
			   }else if (branchId==3) {
				   preface.add(new Paragraph("  GANGA NAGAR, BANGALORE",smallBold));	
			   }else if (branchId==4) {
				   preface.add(new Paragraph(" MAHADEVA PURA, BANGALORE ",smallBold));
			   }
			    preface.add(new Paragraph("TIN NO-29660320234 CST NO - 0398547/7",
					      smallBold));
				
			   
			    addEmptyLine(preface,1);

			    return preface;
		  }
		  
		  private static void addEmptyLine(Paragraph paragraph, int number) {
			    for (int i = 0; i < number; i++) {
			      paragraph.add(new Paragraph(" "));
			    }
			  }
		  
		  /*
		   * returns the PdfPCell object provided a string
		   */
			public  PdfPCell getPdfPCell(String phrase , String align) {
				PdfPCell c2 = new PdfPCell(new Phrase(phrase, tabdata));
				if(align.equals("center"))
				c2.setHorizontalAlignment(Element.ALIGN_CENTER);
				else if (align.equals("right"))
					c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
				else if (align.equals("left"))
					c2.setHorizontalAlignment(Element.ALIGN_LEFT);
				c2.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
				return c2;
			}
			
			  /*
			   * returns the PdfPCell object for total amount 
			   */
				public  PdfPCell getPdfPCellTotal(String phrase,int type) {
					PdfPCell c2 = new PdfPCell(new Phrase(phrase, tabdata));
					c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					if(type==1) {
					   c2.setBorder( Rectangle.LEFT|Rectangle.BOTTOM);
					} else if(type==3 || type==7 ){
					   c2.setBorder( Rectangle.RIGHT|Rectangle.BOTTOM|Rectangle.LEFT);
					   c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
					}else 
					   c2.setBorder( Rectangle.BOTTOM);
					
					return c2;
				}
		  

}
