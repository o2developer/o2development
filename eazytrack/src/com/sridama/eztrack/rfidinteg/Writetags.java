package com.sridama.eztrack.rfidinteg;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sridama.eztrack.tcp.Crc1;
import com.sridama.txngw.core.RequestResponse;

public class Writetags
{
	private RequestResponse req = null;

	public Writetags(final RequestResponse req)
	{
		this.req = req;
	}

	public RequestResponse writeeachtag() throws UnknownHostException, IOException, ParseException
	{
		final JSONParser parser = new JSONParser();
		System.out.println("fhsjhfj" + req.getParam("request"));
		final JSONArray arr = (JSONArray) parser.parse(req.getParam("request"));
		System.out.println("json array fro ui is" + arr.toString());
		final RequestResponse res = null;
		final Socket clientSocket = new Socket("192.168.2.101", 6000);
		final DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		final BufferedInputStream bis = new BufferedInputStream(clientSocket.getInputStream());
		for (int len = 0; len < arr.size(); len++)
		{
			final JSONObject tempObj = (JSONObject) arr.get(len);
			final String oldepc = tempObj.get("eachtag").toString();
			final String newepc = tempObj.get("eachtag").toString().substring(0, 4) + "11111111111111111111";
			System.out.println("epc is" + newepc + "and its length is" + newepc.length());

			String epclength = "";
			final int lengthepc = newepc.length() / 4;
			if (lengthepc < 16)
			{
				epclength = "0" + lengthepc;
			}
			else
			{
				epclength = "" + lengthepc;
			}
			String intermediatewriteepc = "000306" + epclength + oldepc + "0102" + newepc + "000000000000";//used to calc length
			String intermediatewriteepc1 = "000306" + epclength + oldepc + "0102" + newepc + "00000000";//used to calc crc
			final String lenght_in_hexepc = Integer.toHexString(intermediatewriteepc.length() / 2);
			System.out.println(lenght_in_hexepc + intermediatewriteepc1);
			final char[] epcc = hexStringToShortArray(lenght_in_hexepc + intermediatewriteepc1);
			final Crc1 obj1 = new Crc1();
			final String calcepccrc = obj1.CRC16(epcc.length, epcc);
			intermediatewriteepc += calcepccrc;
			intermediatewriteepc1 += calcepccrc;
			final String final_dataepc = lenght_in_hexepc + intermediatewriteepc1;
			for (int i = 0; i < 3; i++)
			{
				try
				{
					Thread.sleep(100);
					System.out.println(final_dataepc);
					outToServer.write(hexStringToByteArray(final_dataepc));
				}
				catch (final Exception ex)
				{
					ex.printStackTrace();
					return res;
				}
			}
		}
		final JSONObject resobj = new JSONObject();
		resobj.put("desc", "tags writted successfully");
		return createResponse(resobj);
	}

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

	public char[] hexStringToShortArray(final String hex)
	{
		final char[] bytes = new char[(hex.length() / 2)];
		int j = 0;
		for (int i = 0; i < bytes.length; i++)
		{
			j = i * 2;
			final String hex_pair = hex.substring(j, j + 2);
			//System.out.println("char is"+hex_pair );
			final char b = (char) (Integer.parseInt(hex_pair, 16));
			bytes[i] = b;
		}
		return bytes;
	}

	private RequestResponse createResponse(final JSONArray o)
	{
		return new RequestResponse(o.toJSONString());
	}

	private RequestResponse createResponse(final JSONObject o)
	{
		return new RequestResponse(o.toJSONString());
	}
}
