package com.sridama.eztrack.tcp;

public class Test4 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String tag ="56000321040138f2000cfccc1f030011221122112211221122112211221122112211220000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
		char [] c =  hexStringToShortArray(tag);
		Crc1 obj = new Crc1();
		String calccrc = obj.CRC16(c.length, c);
		System.out.println(calccrc);

	}
	public static  char [] hexStringToShortArray( String hex) {
		char [] bytes = new char[(hex.length() / 2)];
		int j = 0;
		for ( int i=0; i < bytes.length ; i++ ) {
			j = i * 2;
			String hex_pair = hex.substring(j,j+2);
			char b = (char) (Integer.parseInt(hex_pair, 16) );
			bytes [i] =  b;
		}
		return bytes ;
	} 
	}

