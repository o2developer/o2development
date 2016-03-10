package com.sridama.eztrack.tcp;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class RFIDReader extends Thread
{
	DataOutputStream outToServer2, outToServer1;
	BufferedInputStream bis2, bis1;
	Socket clientSocket1;

	private static Map tMap = new HashMap<String, RFIDReader>();
	private static Map tMap2 = new HashMap<String, RFIDReader>();

	private static RFIDModel read_obj = new RFIDModel();

	private Object lockobj;

	private static int my_chioice = -1;

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

	@Override
	public void run()
	{

		try
		{
			openConnection(); // opening a connection so as to avoid the null pointer exception
		}
		catch (final Exception e1)
		{
			e1.printStackTrace();
		}
		System.out.println("flag value in run " + my_chioice);
		if (my_chioice == 0)
		{
			my_chioice++;
			try
			{
				System.out.println(" ReadTagList ");
				ReadTagList();
			}
			catch (final Exception e)
			{
				System.out.println("Exception occured while reading tags " + e);
				e.printStackTrace();
			}

		}
		else if (my_chioice == 1)
		{
			try
			{
				System.out.println(" Update taglist ");

			}
			catch (final Exception e)
			{
				System.out.println("Exception occured while reading tags " + e);
				e.printStackTrace();
			}

		}
	}

	public void openConnection() throws Exception
	{
		clientSocket1 = new Socket("192.168.2.101", 6000);
		outToServer2 = new DataOutputStream(clientSocket1.getOutputStream());
		bis2 = new BufferedInputStream(clientSocket1.getInputStream());

	}

	public void ReadTagList() throws Exception
	{

		System.out.println("ReadTagList ");

		final byte[] sendData = { 04, 00, 01, (byte) 0xdb, 0x4b }; // 04 00 01
																	// db 4b

		loop: for (int tim = 0; tim < 1; tim++)
		{

			final TCPClient reader = new TCPClient();
			tMap2 = reader.readtags();
			System.out.println("tmap2 is" + tMap2.size());
			System.out.println("time is" + tim);
			openConnection();
			outToServer2.write(sendData);

			System.out.println("buffered input stream" + bis2);
			final String length_in_hex = Integer.toHexString(bis2.read());
			System.out.println("leng" + length_in_hex);
			int length_buffer = hex2decimal(length_in_hex);
			final byte[] buffer_byte_array = new byte[length_buffer];
			int loop_length = 0;

			while (loop_length < buffer_byte_array.length)
			{

				if (loop_length == 0)
					System.out.println("length in hex is" + length_in_hex);
				buffer_byte_array[loop_length] = (byte) bis2.read();
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
					System.out.println("entered here--------");
					final String value = "" + Integer.toHexString(buffer_byte_array[length_buffer]);
					System.out.println(value);
					user_bits += value.substring(6, 8);

				}
				else
					user_bits += Integer.toHexString(buffer_byte_array[length_buffer]);

			}
			System.out.println("bits is" + user_bits + "and size is" + user_bits.length());
			final String newstring = user_bits.replace("0cc", " ");
			System.out.println("user is" + newstring);
			final StringTokenizer tags = new StringTokenizer(newstring);
			System.out.println("no of tags are" + tags.countTokens());

			while (tags.hasMoreElements())
			{
				final String tag = tags.nextElement().toString();
				if (!tMap.containsKey(tag))
				{
					final RFIDModel read_obj = new RFIDModel();
					read_obj.setTag_id(tag);
					read_obj.setCount(1);
					System.out.println(tag.substring(5, tag.length()));
					read_obj.setLocation("bay " + tag.substring(0, 1));
					read_obj.setDirection("north West");
					System.out.println(read_obj.getLocation());
					if (tag.substring(5, tag.length()).equals("1111111111111111111"))
					{
						System.out.println("entered in sold");
						read_obj.setStatus("sold");
					}
					else
						read_obj.setStatus("valid");
					tMap.put(tag + "    North West ", read_obj);
				}
				else
				{
					final RFIDModel read_obj = (RFIDModel) tMap.get(tag.trim());
					read_obj.setTag_id(tag + "    North West ");
					read_obj.setCount(read_obj.getCount() + 1);
					System.out.println(tag.substring(5, tag.length()));
					read_obj.setLocation("bay " + tag.substring(0, 1));
					System.out.println(read_obj.getLocation());
					read_obj.setDirection("north east");
					if (tag.substring(5, tag.length()).equals("1111111111111111111"))
					{
						System.out.println("entered in sold");
						read_obj.setStatus("sold");
					}
					else
						read_obj.setStatus("valid");
					tMap.put(tag + "    North West ", read_obj);
				}
			}

		}

	}

	public Map<String, RFIDModel> getTagList()
	{
		System.out.println("before" + tMap.size());
		tMap.putAll(tMap2);
		System.out.println("after" + tMap.size());
		return tMap;
	}

	/*
	 *   To update the ui , by checking tags EPC inside the tag list map.
	 */

	public void UpdateTagList() throws Exception
	{

		final Crc1 crcObj = new Crc1();
		int epc_length = 0;
		String epc_length_string = "";
		String crc_string = "";
		int crc_length = 0;
		String tag_entry = "";
		String crc = "";

		while (true)
		{
			Thread.sleep(3000); // after every 3 seconds the list will get updated.

			if (tMap.isEmpty())
			{
				continue;
			}

			final Iterator iterator = tMap.entrySet().iterator();
			while (iterator.hasNext())
			{

				epc_length = 0;
				epc_length_string = "";
				crc_string = "";
				crc_length = 0;
				tag_entry = "";
				crc = "";

				final Map.Entry mapEntry = (Map.Entry) iterator.next();
				//System.out.println("The key is: " + mapEntry.getKey() + ",value is :" + mapEntry.getValue());

				tag_entry = mapEntry.getKey().toString().trim();

				epc_length = tag_entry.length() / 4; // The EPC length of an entire string 

				epc_length_string = Integer.toHexString(epc_length); // we have to form a string of hexadecimal so everything needs to be in hex string format

				if (epc_length < 16)
				{
					epc_length_string = "0" + epc_length_string;
				}

				crc_string = "0002" + epc_length_string + tag_entry + "01020400000000"; // this hardcoded values holds good for every tag
				crc_length = ((crc_string.length() / 2) + 2);
				String length_in_hex = Integer.toHexString(crc_length);
				if (crc_length < 16)
				{
					length_in_hex = "0" + length_in_hex; //if length is single digit , appending a zero
				}

				crc_string = length_in_hex + crc_string;

				final char[] c = hexStringToShortArray(crc_string); // "18000206e2003412dc0301194523134301020400000000"

				crc = crcObj.CRC16(c.length, c);

				crc_string = crc_string + crc;

				System.out.println("The crc buffer for each tag read :  " + crc_string);

				final byte[] tag_check_query = hexStringToByteArray(crc_string);

				System.out.println("The size of tag_check_query bye array is :  " + tag_check_query.length);

			}
		}
	}

	public void startRead(final RFIDReader readTagObject)
	{

		// RFIDReader readTagObject = new RFIDReader();
		tMap.clear();
		readTagObject.lockobj = new RFIDReader();
		readTagObject.my_chioice = 0;

		final Thread myThread[] = new Thread[2];
		try
		{
			for (int i = 0; i < 2; i++)
			{
				myThread[i] = new Thread(readTagObject);
				myThread[i].start();

			}
			for (int z = 0; z < 2; z++)
			{

				System.out.println("Inside the join method i value : " + z);
				myThread[z].join(300);
			}

		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * converts given hex string to a short array
	 * (ex: "0D0A" => {0x0D, 0x0A,})
	 * 
	 * @param str
	 * @return
	 */
	public char[] hexStringToShortArray(final String hex)
	{
		final char[] bytes = new char[(hex.length() / 2)];
		int j = 0;
		for (int i = 0; i < bytes.length; i++)
		{
			j = i * 2;
			final String hex_pair = hex.substring(j, j + 2);
			final char b = (char) (Integer.parseInt(hex_pair, 16));
			bytes[i] = b;
		}
		return bytes;
	}

	/**
	   * converts given hex string to a byte array
	   * (ex: "0D0A" => {0x0D, 0x0A,})
	   * 
	   * @param str
	   * @return
	   */
	public byte[] hexStringToByteArray(final String hex)
	{
		final byte[] bytes = new byte[(hex.length() / 2)];
		int j = 0;
		for (int i = 0; i < bytes.length; i++)
		{
			j = i * 2;
			final String hex_pair = hex.substring(j, j + 2);
			final byte b = (byte) (Integer.parseInt(hex_pair, 16));
			bytes[i] = b;
		}
		return bytes;
	}

	public static void main(final String args[])
	{

		final RFIDReader readTagObject = new RFIDReader();

		readTagObject.lockobj = new RFIDReader();
		readTagObject.my_chioice = 0;

		final Thread myThread[] = new Thread[2];
		try
		{

			for (int i = 0; i < 2; i++)
			{
				myThread[i] = new Thread(readTagObject);
				myThread[i].start();
			}
			for (int z = 0; z < 2; z++)
			{

				System.out.println("Inside the join method i value : " + z);
				myThread[z].join(300);
			}

		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}

		System.out.println(" Threads are running in the background ");
	} // End of main
} //End of class
