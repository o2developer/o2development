package com.sridama.eztrack.tcp;

import java.util.ArrayList;

public class Test1
{

	public static void main( String args[])
	{

		 ArrayList<String> tag_list = new ArrayList<String>();

		 String a = "130111ce2044686821201a27fffd5a8c4a9";
		// String b = "200112c123456781234567812345679ce2044686821201a27fffd5a8a04a";
		 String b = "200112cfffd044686821fffd27fffd5fffdc1234567812345678123456794f";
		        b = "200112c123456781234567812345679ce2044686821201a27fffd5a8a04a";
		        b = "200112c305fb63ac1f3841ec2c6467c12345678123456781234567920395a";
		 		
		System.out.println(a.substring(5, a.indexOf('c')));
		System.out.println(b.substring(5, b.indexOf('c')));

		 String sub = b.substring(b.indexOf('c') + 1, b.length() - 4);
		String remaining = sub;
		System.out.println("rem str " + remaining);
		System.out.println("rem len " + remaining.length());

		while (remaining.length() > 24)
		{

			 String tag = remaining.substring(0, 24);

			remaining = sub.substring(25, sub.length());

			tag_list.add(tag);
			// System.exit(0);
		}

		tag_list.add(remaining);
		//String tag2 = 

		System.out.println(tag_list);
	}

}
