/**
 * for barcode image Generation
 */
package com.sridama.eztrack.barcode;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;

import com.sridama.txngw.core.RequestResponse;

import java.awt.geom.*;
import java.awt.font.*;
/**
 * @author Rizwan
 * 
 */
public class BarcodeGenerator { 
	
	
	//public RequestResponse  generateBarcode( RequestResponse req ) throws FontFormatException, IOException {
		
       public static void main (String args[]) throws FontFormatException, IOException {

		int width, height;
		File saveFile = new File("barcode_image.jpg");
		String format = new String("jpg");
		BufferedImage bi, biFiltered;
	
		width = 250;
		height = 50;

		String str = new String("12345");
		BufferedImage bufimg = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics graphicsobj = bufimg.createGraphics();

		File file1 = new File("resources/3of9.TTF");
		FileInputStream fin = new FileInputStream(file1);
		Font font = Font.createFont(Font.TRUETYPE_FONT, fin);
		Font font1 = font.deriveFont(46f);

		graphicsobj.setFont(font1);
		graphicsobj.setFont(font.getFont("3 of 9 Barcode"));
		graphicsobj.setColor(Color.WHITE);

		// Generate barcode image for the code *11111*
		graphicsobj.fillRect(1, 1, 248, 48);
		graphicsobj.setColor(Color.BLACK);
		((Graphics2D) graphicsobj).drawString("*10125*", 20, 30);

		ImageIO.write(bufimg, format, saveFile);

	
		//return null ;
		
		
		
	}
	
	
}
