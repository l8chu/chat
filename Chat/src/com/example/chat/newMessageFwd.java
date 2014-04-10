package com.example.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.example.chat.UsersDiscoveryService.LocalBinder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class newMessageFwd extends Activity {
	protected static final String EXTRA_KEY_CHATID1 = "Chatid1";
	protected static final String EXTRA_KEY_SOURCEIP1 = "Sourceip1";
	protected static final String EXTRA_KEY_DESTIP1 = "Destip1";
	protected static final String EXTRA_KEY_MYCHATID1 = "Mychatid1";
	protected static final String EXTRA_KEY_MYIP1 = "Myip1";
	private static final String TAG = "newMessageFwd";
	private static final String ROUTING_TABLE = "RoutingTable";
	private static final String CHATID_TABLE = "ChatIDTable";
	public static final int REQUEST_CHAT = 10; 
    public static final int REQUEST_ACK = 11;
	public static final int CHAT_MESSAGE = 12;
	newMsgForwardService mService;
	boolean status = false;
	UsersDiscoveryService discovery_service=null;
	SendThread sthread;
    SharedPreferences routetable;
	SharedPreferences chatidtable;
	private List<Chatbox> MsgList = new ArrayList<Chatbox>();
	String whatISay;
	private String myIP;
	private String mychatID;
	private String theirIP;
	private String theirchatID;
	public static ConcurrentLinkedQueue<String> queue1 = new ConcurrentLinkedQueue<String>();
	///////////////////////////////////////
	Button btn_send;
	EditText txt_msg_in;
	ListView txt_box;
	TextView txt_idBar;
	private final Handler mhandler= new Handler(){
		public void handleMessage(Message msg){
		switch (msg.what){
		case REQUEST_CHAT:
			String sourceIP = (String)msg.obj;
			String BusyPkct = myIP+ " " + sourceIP + " "+"RB";
			AddtoQueue(BusyPkct);
			break;
		case REQUEST_ACK:
			android.util.Log.i(TAG,"Got a RA message in handler");
			break;
		
		case CHAT_MESSAGE:
			android.util.Log.i(TAG,"Got a CM message in handler");
			String whatTheySay = (String) msg.obj;
			//txt_box.append("\n"+whatTheySay);
			android.util.Log.i(TAG,"whathteySay: "+whatTheySay);
			addtoChatbox(theirchatID,whatTheySay);
			populateListView();
			break;
		}
		}

	};
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_msg_fwd);
		android.util.Log.i(TAG,"point1");
		//setupChat();
		Bundle extras = getIntent().getExtras();
		android.util.Log.i(TAG,"point2");
		myIP = extras.getString(EXTRA_KEY_MYIP1);
		mychatID = extras.getString(EXTRA_KEY_MYCHATID1);
		theirIP = extras.getString(EXTRA_KEY_DESTIP1);
		theirchatID= extras.getString(EXTRA_KEY_CHATID1);
		android.util.Log.i(TAG,"MYip:"+myIP);
		android.util.Log.i(TAG,"MYchatID:"+mychatID);
		android.util.Log.i(TAG,"theirIP:"+theirIP);
		android.util.Log.i(TAG,"theirchatID:"+theirchatID);
		setupChat();
		
		routetable = getSharedPreferences(ROUTING_TABLE, Context.MODE_PRIVATE);
		chatidtable = getSharedPreferences(CHATID_TABLE,Context.MODE_PRIVATE);
		mService = new newMsgForwardService(this,mhandler);
		mService.start();
		sthread = new SendThread();
		sthread.start();
	}
	
	private void setupChat() {
		android.util.Log.i(TAG,"point1.5");
		btn_send = (Button) findViewById(R.id.btn_send);
		txt_msg_in = (EditText) findViewById(R.id.txt_msgIN);
		txt_box = (ListView) findViewById(R.id.list_chatMessage);
		txt_idBar = (TextView) findViewById(R.id.txt_idBar);
		android.util.Log.i(TAG,"point2.5");
		txt_idBar.setText(theirchatID);
		android.util.Log.i(TAG,"point3.5");
		btn_send.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				String msg = txt_msg_in.getText().toString();
				addtoChatbox("me",msg);
				populateListView();
				String pckt = myIP+" "+theirIP+" "+"CM"+" "+msg;
				AddtoQueue(pckt);
				txt_msg_in.setText("");
				
			}
		});
	}

	public static void AddtoQueue(String pckt) {
		queue1.add(pckt);
	}
	
	
	private void populateListView() {
		android.util.Log.i(TAG,"populateListView point 1");
		ArrayAdapter<Chatbox> adapter1 = new MyListAdapter();
		ListView list = (ListView) findViewById(R.id.list_chatMessage);
		android.util.Log.i(TAG,"populateListView point 3");
		list.setAdapter(adapter1);
	}
	
//populate chat list
	private void addtoChatbox(String chatID, String msg) {
		MsgList.add(new Chatbox(chatID,msg));
	}
	

	
	//Sets the ListView item to Msg bubble
	private class MyListAdapter extends ArrayAdapter<Chatbox> {
		public MyListAdapter (){
			super(newMessageFwd.this, R.layout.item_view,MsgList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//Make sure we have a view to work with (may have been given null)
			View itemView = convertView;
			if (itemView == null){
				itemView = getLayoutInflater().inflate(R.layout.chatitemview, parent,false);
			}
			//find the Chatbox to worth with
			android.util.Log.i(TAG,"MyListAdapter point 1");
			Chatbox currentChatBox = MsgList.get(position);
			//fill the view
			android.util.Log.i(TAG,"MyListAdapter point 2");			
			//ChatID
			TextView ChatID = (TextView) itemView.findViewById(R.id.txt_chatid);
			ChatID.setText(""+currentChatBox.getChatID());
			
			//Message
			TextView Msg = (TextView) itemView.findViewById(R.id.txt_msg);
			Msg.setText(""+currentChatBox.getMessage());

			return itemView;
		}
		
	}
	
	//Thread that polls queue and sends object to listening port
		private class SendThread extends Thread {
			DatagramSocket uniSocket;
			private int LISTENING_PORT1 = 11200; 
			private static final String TAG = "SendThread";
			private int UNI_PORT = 11255;
			public SendThread(){
				try {
					android.util.Log.i(TAG,"Creating Socket! point 1");
					uniSocket = new DatagramSocket(UNI_PORT);
					android.util.Log.i(TAG,"Creating Socket! point 2");
					uniSocket.setBroadcast(false);
					android.util.Log.i(TAG,"Creating Socket! point 3");
				} catch (SocketException e) {
					android.util.Log.i(TAG,"SocketException occurred: "+e.getMessage());
					e.printStackTrace();
				}
			}
			@Override
			public void run() {
				String destIP;
				String str;
				
				while (true){
				while ((str = queue1.poll())!=null){
					int t=0;
					android.util.Log.i(TAG,"got something from queue");
					android.util.Log.i(TAG,"String object = "+str);
				   StringTokenizer st = new StringTokenizer(str," ");
		            String[] tokens = new String[st.countTokens()];	          
		            while (st.hasMoreTokens()){
		            	tokens[t++] = st.nextToken();	            	
		            }
		            destIP = tokens[1];
		            routetable=getSharedPreferences(ROUTING_TABLE, Context.MODE_PRIVATE);
		            android.util.Log.i(TAG,"destIP:" + destIP);
		            android.util.Log.i(TAG,"destIP next hop is: "+routetable.getString(destIP, ""));
				try {
					android.util.Log.i(TAG,"before creaeting datagrampacket");
					DatagramPacket sendpk = new DatagramPacket(str.getBytes(),
												str.length(),
												InetAddress.getByName(routetable.getString(destIP, destIP/*default value*/)),
												LISTENING_PORT1);
					android.util.Log.i(TAG,"is sendpk null?: "+ sendpk);	
					Log.log("Sending packet["+str+"] to IPaddr: "+routetable.getString(destIP, destIP));
					uniSocket.send(sendpk);
					android.util.Log.i(TAG,"Did i send");
					Thread.sleep(250);
				} catch (UnknownHostException e) {
					android.util.Log.i(TAG,"UnkownHostException");
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
				}
				}
			}
		}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

}
