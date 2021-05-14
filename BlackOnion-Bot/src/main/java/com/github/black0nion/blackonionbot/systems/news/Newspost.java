package com.github.black0nion.blackonionbot.systems.news;

import java.util.Date;

public class Newspost {
	
	public String title, content;
	public Date date;
	
	public Newspost(String title, String content, Date date) {
		this.title = title;
		this.content = content;
		this.date = date;
	}
}