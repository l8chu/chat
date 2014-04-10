package com.example.chat;

public class User {
	private String chatID;
	private String ipAddress;
	private int iconID;
	
	public User(String ipAddress, String chatID, int iconID) {
		super();
		this.chatID = chatID;
		this.ipAddress = ipAddress;
		this.iconID = iconID;
	}
	public String getChatID() {
		return chatID;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public int getIconID() {
		return iconID;
	}
	
	

}
