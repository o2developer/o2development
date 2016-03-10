package com.sridama.eztrack.tcp;

public class RFIDModel
{

	private String tag_id;
	private String status;
	private int count;
	private String location;
	private String direction;

	public String getDirection()
	{
		return direction;
	}

	public void setDirection(final String direction)
	{
		this.direction = direction;
	}

	public String getTag_id()
	{
		return tag_id;
	}

	public void setTag_id(final String tag_id)
	{
		this.tag_id = tag_id;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(final String status)
	{
		this.status = status;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(final int count)
	{
		this.count = count;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(final String location)
	{
		this.location = location;
	}

}
