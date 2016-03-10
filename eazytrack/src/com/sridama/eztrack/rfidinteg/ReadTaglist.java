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
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sridama.eztrack.bo.SessionManager;
import com.sridama.eztrack.tcp.RFIDReader;
import com.sridama.eztrack.utils.JDBCHelper;
import com.sridama.txngw.core.RequestResponse;

public class ReadTaglist
{
	public static Map tMap = new HashMap<String, RFIDReader>();
	private RequestResponse req = null;
	DataOutputStream outToServer, outToServer1;
	BufferedInputStream bis, bis1;
	InputStreamReader isr;
	Socket clientSocket;
	String tag = "";
	String tag_first = "";
	int exc, tim;
	final JSONArray resJsonSend = new JSONArray();
	final JSONObject finaljson = new JSONObject();
	private SessionManager ses;

	public void setSession(final SessionManager ses)
	{
		this.ses = ses;
	}

	public ReadTaglist(final RequestResponse req)
	{
		this.req = req;
	}

	@SuppressWarnings("finally")
	public RequestResponse readTag() throws SQLException, ClassNotFoundException, IOException, ParseException
	{
		try
		{
			clientSocket = new Socket("192.168.2.101", 6000);
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			bis = new BufferedInputStream(clientSocket.getInputStream());
			outToServer1 = new DataOutputStream(clientSocket.getOutputStream());
			bis1 = new BufferedInputStream(clientSocket.getInputStream());
			clientSocket.setSoTimeout(1000);
			System.out.println("ReadTagList");
			JSONObject obj = null;
			final JSONArray arr = new JSONArray();
			final byte[] sendData = { 04, 00, 01, (byte) 0xdb, 0x4b }; // 04 00 01 db 4b

			loop: for (tim = 0; tim < 1; tim++)
			{
				Thread.sleep(100);
				System.out.println("time is" + tim);
				outToServer.write(sendData);
				isr = new InputStreamReader(bis);
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
				System.out.println("bits is" + user_bits + "and size is" + user_bits.length());
				final String newstring = user_bits.replace("0cc", " ");
				System.out.println("user is" + newstring);
				final StringTokenizer tags = new StringTokenizer(newstring);
				System.out.println("no of tgs are" + tags.countTokens());
				while (tags.hasMoreElements())
				{

					obj = new JSONObject();
					final String temptag = (String) tags.nextElement();
					System.out.println("tag is" + temptag);
					obj.put("tag", temptag);
					if (!arr.contains(obj))
					{
						System.out.println("no of times");
						exc = 0;
						arr.add(obj);
						finaljson.put("tags", arr);
					}

				}
			}
		}
		catch (final Exception ex)
		{
			exc = 1;
			System.out.println("in this exception");
			ex.printStackTrace();

		}
		finally
		{
			if (tim == 0)
			{
				final JSONObject jobj = new JSONObject();
				jobj.put("desc", "no tag in the range");
				return createResponse(jobj);

			}
			System.out.println("ex is" + exc);
			final Connection con = JDBCHelper.getConnection();
			final Statement stmt = con.createStatement();
			final JSONParser parser = new JSONParser();
			final JSONArray obj2 = (JSONArray) parser.parse(finaljson.get("tags").toString());
			System.out.println("no of tags are--------------------------------------" + obj2.size());
			final JSONArray resJsonSend = new JSONArray();

			for (int i = 0; i < obj2.size(); i++)
			{

				System.out.println("--------------------------" + obj2.get(i));
				final JSONObject tempObj = (JSONObject) obj2.get(i);
				System.out.println("sub string is" + tempObj.get("tag").toString().substring(5, tempObj.get("tag").toString().length()));
				if (!tempObj.get("tag").toString().substring(5, tempObj.get("tag").toString().length()).equals("1111111111111111111"))
				{
					final StringBuilder sb = new StringBuilder();
					sb.append("SELECT item_master.id,item_master.cost, item_master.category,item_master.code, item_master.name,category_master.cat_name ,");
					sb.append("item_master.modal,item_master.units,item_master.rate ,item_master.discnt_percent , tax_master.rate as rate1 ,item_master.taxes,");
					sb.append(" item_stock.stock FROM item_master INNER JOIN item_stock ");
					sb.append(" ON item_master.id=item_stock.itemid  ");
					sb.append(" INNER JOIN category_master ON ");
					sb.append(" item_master.category=category_master.category_id  INNER JOIN tax_master ON item_master.tax_slab = tax_master.id ");
					sb.append(" where branchcode =" + 1 + " and  item_master.code = " + Integer.parseInt(tempObj.get("tag").toString().substring(0, 4)));
					System.out.println(sb.toString());
					final ResultSet rs = stmt.executeQuery(sb.toString());
					while (rs.next())
					{
						final JSONObject obj1 = new JSONObject();
						obj1.put("tag", tempObj.get("tag").toString());
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
						System.out.println(obj1);
						if (!resJsonSend.contains(obj1))
						{
							System.out.println("enetered");
							resJsonSend.add(obj1);

						}

					}
				}
			}

			return createResponse(resJsonSend);
		}
		// End of while

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
