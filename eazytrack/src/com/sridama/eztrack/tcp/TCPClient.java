package com.sridama.eztrack.tcp;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

class TCPClient
{
	public static ArrayList<String> tag_list = new ArrayList<String>();
	private static Map tMap = new HashMap<String, RFIDReader>();
	static DataOutputStream outToServer;
	DataOutputStream outToServer1;
	static BufferedInputStream bis;
	BufferedInputStream bis1;
	static Socket clientSocket;
	private static RFIDModel read_obj = new RFIDModel();

	public static int hex2decimal(String s)
	{
		final String digits = "0123456789ABCDEF";
		s = s.toUpperCase();
		int val = 0;
		for (int i = 0; i < s.length(); i++)
		{
			final char c = s.charAt(i);
			final int d = digits.indexOf(c);
			val = 16 * val + d;
		}
		return val;
	}

	public Map readtags() throws Exception
	{
		tMap.clear();
		clientSocket = new Socket("192.168.2.101", 6000);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		bis = new BufferedInputStream(clientSocket.getInputStream());

		System.out.println("ReadTagList ");

		final byte[] sendData = { 04, 00, 01, (byte) 0xdb, 0x4b }; // 04 00 01
																	// db 4b

		loop: for (int tim = 0; tim < 1; tim++)
		{
			System.out.println("time is" + tim);
			outToServer.write(sendData);
			System.out.println("buffered input stream" + bis);
			final String length_in_hex = Integer.toHexString(bis.read());
			System.out.println("leng" + length_in_hex);
			int length_buffer = hex2decimal(length_in_hex);
			final byte[] buffer_byte_array = new byte[length_buffer];
			int loop_length = 0;

			while (loop_length < buffer_byte_array.length)
			{

				if (loop_length == 0)
					System.out.println("length in hex is" + length_in_hex);
				buffer_byte_array[loop_length] = (byte) bis.read();
				loop_length++;

			}

			System.out.println("buffer_byte_array.length" + buffer_byte_array.length);
			String user_bits = "";
			for (length_buffer = 5; length_buffer < buffer_byte_array.length - 2; length_buffer++)
			{
				if (Integer.toHexString(buffer_byte_array[length_buffer]).length() == 1)
				{
					user_bits += "0";
					user_bits += Integer.toHexString(buffer_byte_array[length_buffer]);
				}
				if (Integer.toHexString(buffer_byte_array[length_buffer]).length() == 8)
				{
					System.out.println("entered--------");
					final String value = "" + Integer.toHexString(buffer_byte_array[length_buffer]);
					System.out.println(value);
					user_bits += value.substring(6, 8);

				}
				else
					user_bits += Integer.toHexString(buffer_byte_array[length_buffer]);

			}
			System.out.println("bits is at 2nd reader" + user_bits + "and size is" + user_bits.length());
			final String newstring = user_bits.replace("0cc", " ");
			System.out.println("user is at 2nd reader" + newstring);
			final StringTokenizer tags = new StringTokenizer(newstring);
			System.out.println("no of tags are" + tags.countTokens());

			while (tags.hasMoreElements())
			{
				final String tag = tags.nextElement().toString();
				if (!tMap.containsKey(tag))
				{
					final RFIDModel read_obj = new RFIDModel();
					read_obj.setTag_id(tag + "    North East ");
					read_obj.setCount(1);
					System.out.println(tag.substring(5, tag.length()));
					read_obj.setLocation("bay " + tag.substring(0, 1));
					System.out.println(read_obj.getLocation());
					if (tag.substring(5, tag.length()).equals("1111111111111111111"))
					{
						System.out.println("entered in sold");
						read_obj.setStatus("sold");
					}
					else
						read_obj.setStatus("valid");
					tMap.put(tag + "    North East ", read_obj);
				}
				else
				{
					final RFIDModel read_obj = (RFIDModel) tMap.get(tag.trim());
					read_obj.setTag_id(tag + "    North East ");
					read_obj.setCount(read_obj.getCount() + 1);
					System.out.println(tag.substring(5, tag.length()));
					read_obj.setLocation("bay " + tag.substring(0, 1));
					System.out.println(read_obj.getLocation());
					if (tag.substring(5, tag.length()).equals("1111111111111111111"))
					{
						System.out.println("entered in sold");
						read_obj.setStatus("sold");
					}
					else
						read_obj.setStatus("valid");
					tMap.put(tag + "    North East ", read_obj);
				}
			}

		}
		clientSocket.close();
		outToServer.close();
		bis.close();
		return tMap;

	}
}
