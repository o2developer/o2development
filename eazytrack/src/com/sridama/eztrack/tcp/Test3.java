package com.sridama.eztrack.tcp;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class Test3 {
	
	public static void main (String args[]) {
		

		byte b= (byte)  0xe2;
				char c= (char)(b&0xff);
				System.out.println(c);
				
				char a[] = {c} ;
		
		
		Test3  test = new Test3() ;
		String ourString = "e2003412dc03011945231343" ;
		
	//	18000206  e2003412dc03011945231343 01 02 04 00000000   265e
		
		 char buffer1[]={ 0x18, 0x00, 0x02 ,0x06, 0xe2,0x00,0x34,0x12,0xdc,0x03,0x01,0x19,0x45,0x23,0x13,0x43,0x01,0x02,0x04,0x00,0x00,0x00,0x00};
		
		 //System.out.println(buffer1[2]);
//		byte b[] = test.hexStringToByteArray( "18000206e2003412dc0301194523134301020400000000" ) ;
		/*System.out.println(b.length);
		System.out.println(test.bytesToStringUTFNIO(b));*/
		System.out.println(String.valueOf(buffer1));
		
		String hex = "dc" ;
		System.out.println((byte)Integer.parseInt(hex, 16) );
		
		System.out.println(Integer.toHexString(-36));
	     		
		
	}
 	
    public static final char [] hexStringToCharArray2(final String hex) {
        char [] bytes = new char[(hex.length() / 2)];
        int j = 0;
        for ( int i=0; i < bytes.length ; i++ ) {
                j = i * 2;
                String hex_pair = hex.substring(j,j+2);
                byte b = (byte) (Integer.parseInt(hex_pair, 16) & 0xFF);
                bytes [i] = (char) b;
            //    bytes [i] = 
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
	  
	  /**
	   * converts given hex string to a byte array
	   * (ex: "0D0A" => {0x0D, 0x0A,})
	   * 
	   * @param str
	   * @return
	   */
	  public static final byte[] hexStringToByteArray(String str)
	  {
	    int i = 0;
	    byte[] results = new byte[str.length() / 2];
	    for (int k = 0; k < str.length();)
	    {
	      results[i] = (byte) (Character.digit(str.charAt(k++), 16) << 8);
	      results[i] += (byte)(Character.digit(str.charAt(k++), 16));
	      i++;
	    } 
	    return results;
	  }
	  
	  public static String bytesToStringUTFNIO(byte[] bytes) {
		  CharBuffer cBuffer = ByteBuffer.wrap(bytes).asCharBuffer();
		  return cBuffer.toString();
		 }
}
