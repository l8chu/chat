package com.example.chat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DiscoverPeers extends Activity implements Runnable{

	public BroadcastChatService bcasts;
	public Button start;
	public Button exit;
	public String t;
	private Thread myThread;
	boolean running;
	private String myBcastIP;
	private DatagramPacket packetSend;
	private DatagramPacket packetList;
	private boolean listening = true;
    private String space = " ";
	private int Port = Preferences.ListeningPort;
	private String ldata;
	private DatagramSocket socket ;
	private String myLocalIP;
	private String data1, data2;
	private Handler hand = new Handler();
	private String[] array;
	static File root = new File(Environment.getExternalStorageDirectory(),"Log");
	private String path = root.getAbsolutePath();
	private File filename = new File(root,"contact.txt");
	private StringBuilder text = new StringBuilder();
	private String ip;
	private ArrayList<List> arrList = new ArrayList<List>();
	
	private String msg;
	private String msg1;
	private String name_own;
	private String name;
	private nameTask nametask;
	private boolean listen = true;
	private int packetcount = 0;
	private String packetRec;
	private ArrayList<String> recdata = new ArrayList<String>();
	
	private static TextView log_dis;
	private final Handler handler = new Handler();
	public String getName(){
		File root = Environment.getExternalStorageDirectory();
		File myfile = new File(root,"chatid");
		
		for (File f : myfile.listFiles()) {
		    if (f.isFile()){
		    	name = f.getName();
		    }
		        // Do your stuff
		}
		return name;
	}
	public class nameTask extends AsyncTask<Void, Void, String>{

		  @Override
	    protected void onPreExecute(){
	    super.onPreExecute();
	    }
		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		 @Override
	   protected void onPostExecute(String result) {
	       super.onPostExecute(result);
	       name_own = getName();
	   }
		
	}
//	public class listenpacketTask extends AsyncTask<Void, Void, String>{
//
//		  @Override
//	      protected void onPreExecute(){
//	      super.onPreExecute();
//	      }
//		@Override
//		protected String doInBackground(Void... arg0) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//		 @Override
//	     protected void onPostExecute(String result) {
//	         super.onPostExecute(result);
//	         msg = packetdata.createlisteningPacket();
//	     }
//		
//	}
protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discoverpeers);
		
		log_dis = (TextView) findViewById(R.id.log);
		log_dis.setMovementMethod(new ScrollingMovementMethod());
		log_dis.append("strating discoverPeers activity...");
		log_dis.append(" \n");
		log_dis.append(" \n");
		hand.postDelayed(run, 5000);
		
		nametask = new nameTask();
		nametask.execute();
//		listenpacketask = new listenpacketTask();
//		listenpacketask.execute();
}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		startThread();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		stopThread();
	}
    public void onStop() {
        super.onStop();
        stopRepeatingTask();
        socket.close();
    }
	public void stopThread(){
		running = false;
		boolean retry = true;
		while(retry){
			try {
				myThread.join();
				retry = false;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	public void startThread(){
		running = true;
		myThread = new Thread(this);
		myThread.start();
	}
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private int UPDATE_INTERVAL = 5000;
	
	Runnable run = new Runnable() {
@Override
public void run() {
	// TODO Auto-generated method stub
	Log.log("am I getting here");
	
    UdpListen(); // this action have to be in UI thread
    mHandler.postDelayed(this, UPDATE_INTERVAL);
	}
};
void startRepeatingTask() {
    run.run(); 
  }

  void stopRepeatingTask() {
    mHandler.removeCallbacks(run);
  }



public void UdpListen(){
	try {
		myBcastIP = getIP.getBroadcast().toString();
		myLocalIP 	= getIP.getLocalAddress().toString();
		 Log.log("2.my bcast ip : "+myBcastIP);
		 Log.log("2.my local ip : "+myLocalIP);
		 
		 log_dis.append("my local ip :    " + myLocalIP);
		 log_dis.append(" \n");
		 log_dis.append(" \n");
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	try {
		
		socket = new DatagramSocket(Port);
		socket.setBroadcast(true); 
	} catch (SocketException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	byte[] dataSend = new byte[100];
    byte[] dataRead = new byte[100];

    log_dis.append("creating dataPacket to send...");
    log_dis.append(" \n");
    log_dis.append(" \n");

    msg1 = "Listening" + space + myLocalIP + space + name_own;
    
    dataSend = msg1.getBytes();
    log_dis.append("the dataPacket to be sent is     " + msg1);
    log_dis.append(" \n");
    log_dis.append(" \n");

    Log.log("datasend is " + msg1 + dataSend);
    log_dis.append("creating sending data...");
    log_dis.append(" \n");
    log_dis.append(" \n");

    log_dis.append("datasend is  : " + msg1);
    log_dis.append(" \n");
    log_dis.append(" \n");

    byte[] buf = new byte[1024];
    try {
    	log_dis.append("creating DatagramPacket...");
    	log_dis.append(" \n");
    	log_dis.append(" \n");

    	packetSend = new DatagramPacket(dataSend, dataSend.length, InetAddress.getByName(myBcastIP), Port);
		Log.log("packetSend is " + packetSend.getData().toString());

    	packetList = new DatagramPacket(buf, buf.length);
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		log_dis.append("problem happened when creating DatagramPacket...");
		log_dis.append(" \n");
		log_dis.append(" \n");

	}
    
    try {
    	log_dis.append("sending packet...");   	
    	log_dis.append(" \n");
    	log_dis.append(" \n");

    	socket.send(packetSend);

	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		log_dis.append("problem happened when sending packet...");
		log_dis.append(" \n");
		log_dis.append(" \n");

	}
    String iplist = new String();
    String namelist = new String();
  try {

		if(packetList != null){
			log_dis.append("receiving packet...");
			log_dis.append(" \n");
			log_dis.append(" \n");

			
			socket.receive(packetList);
			
			log_dis.append("the packet coming from      " + packetList.getAddress().toString());
			log_dis.append(" \n");
			log_dis.append(" \n");
			
			
		    packetRec = new String(packetList.getData());
		    
		    
		    array = packetRec.split(space);
		    Log.log( "Received: "+packetRec);
		    Log.log("data 1 is " + array[0]);
		    Log.log("data 2 is " + array[1]);
		    Log.log("data 3 is " + array[2]);
		    log_dis.append("the packet received, data  is     " + array[0]);
		    log_dis.append(" \n");
		    log_dis.append(" \n");

		    log_dis.append("the packet received, ip form is     " + array[1]);
		    log_dis.append(" \n");
		    log_dis.append(" \n");

	    	log_dis.append("the packet received, name from is     " + array[2]);
	    	log_dis.append(" \n");
	    	log_dis.append(" \n");

		    iplist =  new String(array[1]);
//		    recdata.add(iplist);
//		    log_dis.append("adding ip to contactlist       " + iplist);
//		    log_dis.append(" \n");
//		    log_dis.append(" \n");

		    
		    if(!iplist.equals(packetList.getAddress().toString())){
		    	log_dis.append("the ip of the packet is not equal my own ip");
		    	log_dis.append(" \n");
			    log_dis.append(" \n");

		    	if(!recdata.contains(iplist)){
		    		log_dis.append("the ip of the packet is not contained in my ip list");
			    	log_dis.append(" \n");
				    log_dis.append(" \n");
				    iplist =  new String(array[1]);
				    namelist = new String(array[2]);
				    log_dis.append("adding this ip my ip list");
			    	log_dis.append(" \n");
				    log_dis.append(" \n");
				    recdata.add(iplist);
				    log_dis.append("adding ip to contactlist       " + iplist);
				    log_dis.append(" \n");
				    log_dis.append(" \n");
		    if(recdata.contains(myLocalIP)){
		    	log_dis.append("the ip list contain my own ip");
		    	log_dis.append(" \n");
			    log_dis.append(" \n");
			    
			    log_dis.append("remove my own ip from ip list");
		    	log_dis.append(" \n");
			    log_dis.append(" \n");
			    
		    	recdata.remove(myLocalIP);
		    }
		    }
			
//		    else{
//		    	log_dis.append("keep receiving the packet");
//		    	log_dis.append(" \n");
//			    log_dis.append(" \n");
//		    	socket.receive(packetList);
//			    packetRec = new String(packetList.getData());
//		    }
		    }
			
//		    else{
//		    	log_dis.append("keep receiving the packet");
//		    	log_dis.append(" \n");
//			    log_dis.append(" \n");
//		    	socket.receive(packetList);
//			    packetRec = new String(packetList.getData());
//		    }


			
			Log.log("the sender or destination IP is " + packetList.getAddress().toString());
			

		}
		else{
			Log.log("null packet");
			log_dis.append("null packet");
			log_dis.append(" \n");
			log_dis.append(" \n");

		}

	    SendList.data().arrayList = new ArrayList<String>();
	    //SendList.data().arrayList.add(namelist);
	    SendList.data().arrayList.add(recdata.toString());
	    Log.log("recdata " + recdata);
//	    log_dis.append("sending ip");
//	    log_dis.append("\n");
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  
  

  



  
//  ArrayList<String> list = new ArrayList<String>();
//  list.add(array[1]);
//  Intent i = new Intent(DiscoverPeers.this,ContactList.class);
//  i.putExtra("key", list);
  	
//  arrList.add(new List());
//  arrList.get(0).setData(arrlist);
//
//  ListParcelable object = new ListParcelable();
//  object.setArrList(arrList);
//
//  Intent intent = new Intent(DiscoverPeers.this,ContactList.class);
//  intent.putExtra("key", object);
  
  
 //     startActivity(intent);
      
  

//try {
//	
//	Scanner br = new Scanner(filename);
//	String line;
//	if(filename.length() == 0){
//		Log.list(array[1]);
//	}
//	else{ 
//	
//	while (br.hasNext()) {
//	ip = br.nextLine();
//	if(!ip.equals(array[1])){
//		Log.list(ip);
//	}
//	}
//}	
//} catch (FileNotFoundException e) {
//	// TODO Auto-generated catch block
//	e.printStackTrace();
//} catch (IOException e) {
//	// TODO Auto-generated catch block
//	e.printStackTrace();
//}


}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public int describeContents() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	public void readFromParcel(Parcel in) {
//        data1 = in.readString();
//        data2 = in.readString();
//
//      }
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//		// TODO Auto-generated method stub
//		dest.writeString(data1);
//		dest.writeString(data2);
//	}
}



