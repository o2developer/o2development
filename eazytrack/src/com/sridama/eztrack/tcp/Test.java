package com.sridama.eztrack.tcp;

import java.util.Arrays;

public class Test {
	
	public static void main(String args[]) {
		
		Test t = new Test() ;
		
		 String crc1 = "18000206e2003412dc0301194523134301020400000000" ; 
		 String string = "\u0041\u0042\u0045";
		 
		/* System.out.println(string.length());
		 
		 char a[] = new char[string.length()] ;
		 a = string.toCharArray();
		 
		for (int i=0 ; i < string.length() ; i++ ){
			System.out.print(a[i]  );
		}*/
		 
		 
		StringBuilder sb = new StringBuilder();
		
		byte[] sendData = { 0x04, 0x00, 0x01, (byte) 0xdb, 0x4b }; 
		
		
		 
		for (int m=0 ; m < sendData.length ; m++) {
		String byte2string =  String.format("%02x" , sendData[m] );
		sb.append(byte2string);
		//System.out.println(byte2string);
		}

		System.out.println(" sb.toString() "+sb.toString());
		//	byte[] sendData e2 00 44 68 68 02 00 68 05 20ac d9 b0
		
		byte a[] = t.hexStringToByteArray2 ( sb.toString() );
		
		for (int l = 0 ; l<a.length ; l++){
			System.out.println(a[l]);
		}
		
		
	}
	
	

    /**
    * Convert a String in 'hex' to a byte[] array.
    * @param hex - String
    * @return bytes - byte[] buffer
    * 
    * See variation on this in hexStringToByteArray
    * Not sure which is the best - to be tested
    */
    public static final byte [] hexStringToByteArray2(final String hex) {
            byte [] bytes = new byte[(hex.length() / 2)];
            int j = 0;
            for ( int i=0; i < bytes.length ; i++ ) {
                    j = i * 2;
                    String hex_pair = hex.substring(j,j+2);
                    byte b = (byte) (Integer.parseInt(hex_pair, 16) & 0xFF);
                    bytes [i] = b;
                    System.out.println(hex_pair);
            }
            return bytes;
    } 
    
    public static final char [] hexStringToCharArray2(final String hex) {
        char [] bytes = new char[(hex.length() / 2)];
        int j = 0;
        for ( int i=0; i < bytes.length ; i++ ) {
                j = i * 2;
                String hex_pair = hex.substring(j,j+2);
                byte b = (byte) (Integer.parseInt(hex_pair, 16) & 0xFF);
                bytes [i] = (char) b;
             //   System.out.println(hex_pair);
        }
        return bytes;
} 
	
	
	  public static int hex2decimal(String s) {
	        String digits = "0123456789ABCDEF";
	        s = s.toUpperCase();
	        int val = 0;
	        for (int i = 0; i < s.length(); i++) {
	            char c = s.charAt(i);
	            int d = digits.indexOf(c);
	            val = 16*val + d;
	        }
	        return val;
	    }

}
