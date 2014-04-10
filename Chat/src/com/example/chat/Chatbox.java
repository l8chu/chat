package com.example.chat;

public class Chatbox {
	private String chatID;
	private String Message;

	
	public Chatbox(String chatID, String message) {
		super();
		this.chatID = chatID;
		this.Message = message;
	}
	public String getChatID() {
		return chatID;
	}
	public void setChatID(String chatID) {
		this.chatID = chatID;
	}
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}
	
	
}
