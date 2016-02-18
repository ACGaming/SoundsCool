package com.dynious.soundscool.helper;

public class TagInfo 
{
	private String title, artist;
	
	public TagInfo()
	{
		
	}
	
	public TagInfo(String title, String artist)
	{
		this.title = title;
		this.artist = artist;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public void setArtist(String artist)
	{
		this.artist = artist;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getArtist()
	{
		return artist;
	}
}
