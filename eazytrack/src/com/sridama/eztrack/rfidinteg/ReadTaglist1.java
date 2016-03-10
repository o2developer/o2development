package com.sridama.eztrack.rfidinteg;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sridama.eztrack.bo.SessionManager;
import com.sridama.eztrack.tcp.RFIDModel;
import com.sridama.eztrack.tcp.RFIDReader;
import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.txngw.core.RequestResponse;

public class ReadTaglist1
{
	public static Map tMap = new HashMap<String, RFIDReader>();
	private RequestResponse req = null;
	DataOutputStream outToServer, outToServer1;
	BufferedInputStream bis, bis1;
	InputStreamReader isr;
	Socket clientSocket;
	String tag = "";
	String tag_first = "";

	private SessionManager ses;

	public void setSession(final SessionManager ses)
	{
		this.ses = ses;
	}

	public ReadTaglist1(final RequestResponse req)
	{
		this.req = req;
	}

	public RequestResponse readTag() throws InterruptedException, IOException, ClassNotFoundException, SQLException, ParseException
	{
		final JSONObject finaljson = new JSONObject();
		clientSocket = new Socket("192.168.2.189", 6000);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		bis = new BufferedInputStream(clientSocket.getInputStream());
		outToServer1 = new DataOutputStream(clientSocket.getOutputStream());
		bis1 = new BufferedInputStream(clientSocket.getInputStream());
		System.out.println("ReadTagList");
		JSONObject obj = null;
		final JSONArray arr = new JSONArray();

		final byte[] sendData = { 04, 00, 01, (byte) 0xdb, 0x4b }; // 04 00 01 db 4b

		while (true)
		{
			Thread.sleep(100);
			outToServer.write(sendData);
			isr = new InputStreamReader(bis);
			final String length_in_hex = Integer.toHexString(isr.read());
			final int length_buffer = hex2decimal(length_in_hex);

			System.out.println("length of the buffer -------------" + length_buffer);

			final byte[] buffer_byte_array = new byte[length_buffer];
			int loop_length = 0;

			if (length_buffer != 5 && length_buffer != 242 && length_buffer != 61 && length_buffer != 251)
			{

				while (loop_length < buffer_byte_array.length)
				{
					if (loop_length == 0)
						System.out.print(length_in_hex);
					buffer_byte_array[loop_length] = (byte) isr.read();
					loop_length++;
				}

				String tag = "";
				System.out.println("buffer_byte_array.length" + buffer_byte_array.length);

				if (buffer_byte_array.length > 4)
				{ // to avoid array index out
					// of bounds exception

					if (buffer_byte_array[3] == 1 || buffer_byte_array.length < 15)
					{
						System.out.println(" buffer_byte_array[3] " + buffer_byte_array[3]);
						/*
						 * String s= new String(buffer_byte_array);
						 * System.out.println("tag value in string is"+s);
						 */

						for (int i = 5; i < buffer_byte_array.length - 2; i++)
						{
							System.out.println("gjh" + buffer_byte_array[i]);
							final String value = Integer.toHexString(buffer_byte_array[i]);
							final int abc = value.length();
							String value1 = value;

							if (value.length() == 8)
							{
								value1 = value.substring(value.length() - 2, value.length());
							}

							if (abc == 1)
							{
								value1 = "0" + value;
							}

							System.out.print(value1 + " ");

							tag_first += value1; // getting a tag id

						}
						obj = new JSONObject();
						obj.put("tag", tag_first);
						arr.add(obj);
						finaljson.put("tags", arr);
						if (!tMap.containsKey(tag_first))
						{
							System.out.println("entered");
							final RFIDModel read_obj = new RFIDModel();
							read_obj.setTag_id(tag_first);
							read_obj.setCount(1);
							read_obj.setStatus("valid");
							tMap.put(tag_first, read_obj);

						}
						else
						{
							final RFIDModel read_obj = (RFIDModel) tMap.get(tag_first.trim());
							read_obj.setTag_id(tag_first);
							read_obj.setCount(read_obj.getCount() + 1);
							read_obj.setStatus("valid");
							tMap.put(tag_first, read_obj);

						}

					}
					else
					{
						for (int i = 5; i < buffer_byte_array.length - 2; i++)
						{
							final String value = Integer.toHexString(buffer_byte_array[i]);
							final int abc = value.length();
							String value1 = value;

							if (value.length() == 8)
							{
								value1 = value.substring(value.length() - 2, value.length());
								System.out.println(value1);
							}

							if (abc == 1)
							{
								value1 = "0" + value;
							}

							System.out.print(value1 + " ");
							System.out.println(tag.length());
							if (((value1.equalsIgnoreCase("0C") && tag.length() + 2 == 24) && value1.equalsIgnoreCase("0C") || tag.length() + 2 == 24))
							{

								if (!tMap.containsKey(tag))
								{
									tag += value1;
									obj = new JSONObject();
									System.out.println("jkjktag " + tag);
									final RFIDModel read_obj = new RFIDModel();
									read_obj.setTag_id(tag);
									read_obj.setCount(1);
									read_obj.setStatus("valid");
									tMap.put(tag, read_obj);
									obj.put("tag", tag.toUpperCase());
									arr.add(obj);
									finaljson.put("tags", arr);

								}
								else
								{
									tag += value1;
									final RFIDModel read_obj = (RFIDModel) tMap.get(tag.trim());
									read_obj.setTag_id(tag);
									read_obj.setCount(read_obj.getCount() + 1);
									read_obj.setStatus("2.35 gms");
									tMap.put(tag, read_obj);

								}
								tag = "";
							}
							else
							{
								if (!(value1.equalsIgnoreCase("0c") && tag.length() == 0))
									tag += value1; // gettig a tag id
							}

						}

					} // End of else

				} // if loop to avoid null pointer

			}
			Statement stmt = null;
			final Connection con = JDBCHelper.getConnection();
			stmt = con.createStatement();
			final Statement insert = con.createStatement();

			final JSONParser parser = new JSONParser();
			final JSONArray obj2 = (JSONArray) parser.parse(finaljson.get("tags").toString());

			final JSONArray resJsonSend = new JSONArray();

			for (int i = 0; i < obj2.size(); i++)
			{

				System.out.println("--------------------------" + obj2.get(i));
				final JSONObject tempObj = (JSONObject) obj2.get(i);
				final StringBuilder sb = new StringBuilder();
				sb.append("SELECT item_master.id,item_master.cost, item_master.category,item_master.code, item_master.name,category_master.cat_name ,");
				sb.append("item_master.modal,item_master.units,item_master.rate ,item_master.discnt_percent , tax_master.rate as rate1 ,item_master.taxes,");
				sb.append(" item_stock.stock FROM item_master INNER JOIN item_stock ");
				sb.append(" ON item_master.id=item_stock.itemid  ");
				sb.append(" INNER JOIN category_master ON ");
				sb.append(" item_master.category=category_master.category_id  INNER JOIN tax_master ON item_master.tax_slab = tax_master.id ");
				sb.append(" where branchcode =" + ses.getBrCode() + " and  item_master.code = " + Integer.parseInt(tempObj.get("tag").toString().substring(0, 4)));
				System.out.println(sb.toString());
				final ResultSet rs = stmt.executeQuery(sb.toString());
				while (rs.next())
				{
					final JSONObject obj1 = new JSONObject();
					obj1.put("id", "" + rs.getInt("id"));
					obj1.put("name", rs.getString("name"));
					obj1.put("modal", rs.getString("modal"));
					obj1.put("units", rs.getString("units"));
					obj1.put("rate", rs.getFloat("rate"));
					obj1.put("cost", rs.getFloat("cost"));
					obj1.put("qty", "" + 1);
					obj1.put("disc_percent", rs.getDouble("discnt_percent"));

					obj1.put("catid", "" + rs.getInt("category"));
					obj1.put("vat_percent", "" + rs.getFloat("rate1")); // tax rate from tax_master
					obj1.put("code", "" + rs.getInt("code"));
					obj1.put("taxes", "" + rs.getFloat("rate1")); // tax rate from tax_master 

					resJsonSend.add(obj1);
				}
			}

			return createResponse(resJsonSend);
		} // End of while

	}

	public static int hex2decimal(String s)
	{
		final String digits = "0123456789ABDEF";
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

	private RequestResponse createResponse(final JSONObject o)
	{
		return new RequestResponse(o.toJSONString());
	}

	private RequestResponse createResponse(final JSONArray o)
	{
		return new RequestResponse(o.toJSONString());
	}

}
