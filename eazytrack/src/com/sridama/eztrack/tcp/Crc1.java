package com.sridama.eztrack.tcp;

public class Crc1 {
	
static char buffer[]={0x24,0x00,0x03,0x06,0x06,0x12,0x34,0x56,0x78,0x12,0x34,0x56,0x78,0x12,0x34,0x56,0x79,0x01,0x02,0x12,0x34,0x56,0x78,0x12,0x34,0x56,0x78,0x12,0x34,0x56,0x78,0x00,0x00,0x00,0x00};	

static String crc ="2400030606203412c3119452313430102203412c311945231347111100000000"; //32

static String crc1 = "18000206e2003412dc0301194523134301020400000000" ; 

static int bufint[] = { 0x18, 0x00, 0x02 ,0x06, 0xe2,0x00,0x34,0x12,0xdc,0x03,0x01,0x19,0x45,0x23,0x13,0x43,0x01,0x02,0x04,0x00,0x00,0x00,0x00};

static char buffer1[]={ 0x18, 0x00, 0x02 ,0x06, 0xe2,0x00,0x34,0x12,0xdc,0x03,0x01,0x19,0x45,0x23,0x13,0x43,0x01,0x02,0x04,0x00,0x00,0x00,0x00};

static char buffer2[]=new char[23];

public static void main ( String args[] )
{
	//System.out.println(buffer.length);
	
	for (int m=0 ;m < bufint.length ; m++) {
		buffer2[m]=(char) bufint[m];
	}
	Crc1 obj = new Crc1();
//	obj.CRC16(23,buffer2);
	String s = "";
}

	
public String CRC16 (int dataLength , char [] buffer )
{
	
	// buffer = crc1.getBytes();
	// dataLength = buffer.length ;
	 
	 for (int i = 0 ; i < dataLength ; i++ ) {
		 //System.out.println(Integer.toHexString( (int)buffer[i]  ) );
		 if (i==4){
			 
		 }
			// System.out.println(buffer[i] & 0xff);
			 
	 }
	 
	 int  polynomial = 0x8408 ;
	 int CheckSum;
     int j;
     char lowCRC;
     char highCRC;
     short i;
     
     CheckSum = 0xffff;
     
     for (j=0; j<dataLength; j++)
     {
    
       CheckSum = CheckSum^(int)buffer[j];
       for(i=8;i>0;i--)
         if( ((CheckSum)&0x0001)!=0)
         CheckSum = (CheckSum>>1)^ polynomial ;
         else
         CheckSum>>=1;
     }
     
   /*  System.out.println(CheckSum);
     System.out.println(Integer.toHexString(CheckSum));
     highCRC = (char) (CheckSum>>8);
     System.out.println(Integer.toHexString(CheckSum>>8)); //"highCRC=%x\r\n",
     CheckSum<<=8;
     lowCRC = (char) (CheckSum>>=8);
     System.out.println(Integer.toHexString(CheckSum<<8)); //"lowCRC=%x\r\n",
*/	     
    
    // System.out.println("The left and right  crc values are : ");
     
     byte[] byteStr = new byte[4];
     
     byteStr[0] = (byte) ((CheckSum & 0x000000ff));
	 byteStr[1] = (byte) ((CheckSum & 0x0000ff00) >>> 8);

//	 System.out.printf("%02X\n%02X", byteStr[0],byteStr[1]);
	 
	 String as =  String.format("%02X%02X", byteStr[0],byteStr[1]);
	 
	// System.out.println("\n"+as);
	 
    return as  ;
     
}

}
