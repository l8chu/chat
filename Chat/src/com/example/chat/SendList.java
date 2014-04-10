package com.example.chat;
import java.util.ArrayList;


public class SendList {
	 private volatile static SendList sendList;
	    public static SendList data(){
	    if(sendList == null){
	        synchronized (SendList.class) {
	            if (sendList == null) {
	            	sendList = new SendList();
	            }
	        }
	    }
	    return sendList;
	    }  

	    public ArrayList<String> arrayList;  
}
