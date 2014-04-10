package com.example.chat;

import com.example.chat.UsersDiscoveryService.LocalBinder;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class OnlineUsers extends Activity {
	private static final String TAG = "OnlineUsers";
	protected static final String EXTRA_CHATID = "MyChatID";
	protected static final String EXTRA_DESTIP = "DESTIP";
	protected static final String EXTRA_DESTCHATID = "DESTID";
	private static final String ROUTING_TABLE = "RoutingTable";
	private static final String CHATID_TABLE = "ChatIDTable";
	public static final int REQUEST_CHAT = 10; 
    public static final int REQUEST_ACK = 11;
    public static final int REPLY_BUSY = 13;
    public static final int CHAT_MESSAGE = 12;
	protected static final String BUNDLE_KEY_CHATID = "DialogBundleChatid";
	protected static final String BUNDLE_KEY_SOURCEIP = "DialogBundleSourceip";
	protected static final String BUNDLE_KEY_DESTIP = "DialogBundleDestip";
	protected static final String BUNDLE_KEY_MYIP = "DialogBundleMyip";
	protected static final String BUNDLE_KEY_MYCHATID = "DialogBundleMyChatid";
	public static ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
	SendThread sthread;
	UsersDiscoveryService discovery_service=null;
	Button btn_stop;
	boolean status = false;
	List<String> myuserlist;
	//TextView testview;
	String mychatID,myIP;
	Button btn_start;
	//Intent serviceintent;
	private List<User> UserList = new ArrayList<User>();
	static MsgForwardService mService;

    SharedPreferences routetable;
    SharedPreferences chatidtable; 
	private final Handler mhandler= new Handler(){
		public void handleMessage(Message msg){
		switch (msg.what){
		case REQUEST_CHAT:
			String sourceIP = (String)msg.obj;
			android.util.Log.i(TAG,"handler what is sourceIP: "+sourceIP);
			
			//SHOW DIALOG
			MyAlert myAlert = new MyAlert();
			Bundle args = new Bundle();
			args.putString(BUNDLE_KEY_MYCHATID, mychatID);
			args.putString(BUNDLE_KEY_MYIP, myIP);
			args.putString(BUNDLE_KEY_CHATID,chatidtable.getString(sourceIP, ""));//pass in chatid
			args.putString(BUNDLE_KEY_DESTIP, sourceIP);//pass in destination ip
			myAlert.setArguments(args);
			myAlert.show(getFragmentManager(), "My Alert");
			//IF user says yesSend REQUEST_ACK packet to person requesting
			//String ACK = myIP+" "++""+""+""+""
			android.util.Log.i(TAG,"In handler, requestCHAT");
			break;
		case REQUEST_ACK:
			String TAG5= "REQUESTACK";
			android.util.Log.i(TAG,"Got a RA message in handler");
			SharedPreferences accesschattable = getSharedPreferences(CHATID_TABLE, Context.MODE_PRIVATE);
			String sourceip = (String) msg.obj;
			Intent intent = new Intent(OnlineUsers.this,newMessageFwd.class);
			intent.putExtra(MyAlert.EXTRA_KEY_CHATID1, accesschattable.getString(sourceip, "Name"));
			android.util.Log.i(TAG5,accesschattable.getString(sourceip, "Name"));
			intent.putExtra(MyAlert.EXTRA_KEY_DESTIP1,sourceip);
			android.util.Log.i(TAG5,sourceip);
			intent.putExtra(MyAlert.EXTRA_KEY_MYCHATID1,mychatID);
			android.util.Log.i(TAG5, mychatID);
			intent.putExtra(MyAlert.EXTRA_KEY_MYIP1,myIP);
			android.util.Log.i(TAG5,myIP);
			mService.stop();
			startActivity(intent);
			break;
			
		case REPLY_BUSY:
			String fromip = (String) msg.obj;
			Toast.makeText(getApplicationContext(), chatidtable.getString(fromip, "")+" is currently busy", Toast.LENGTH_LONG).show();
			break;
		}
		
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		Log.create();
		getActionBar().hide(); //hide the action bar
		setContentView(R.layout.online_users);
		
		Bundle extras = getIntent().getExtras();
		mychatID = extras.getString(Menu.EXTRA_MYCHATID);
		
		bindMethod();
		setupStartbtn();
		setupStopbtn();
		mService = new MsgForwardService(OnlineUsers.this,mhandler);
		mService.start();
		registerClickCallBack();	
		sthread = new SendThread();
		sthread.start();
		
	}
	
	public void printDBtoLOG(){
		//print chat ID table
		SharedPreferences temp1= getSharedPreferences(CHATID_TABLE,Context.MODE_PRIVATE);
		
		Log.log("\nChatId Table:");
		Map<String,?> keys1 = temp1.getAll();
		for(Map.Entry<String,?> entry1 : keys1.entrySet()){
            Log.log(entry1.getKey() + " ----> " + 
                                   entry1.getValue().toString()+"\n");            
		}
		
	}
	
	private void populateUserList() {
		android.util.Log.i(TAG,"populateUserList point 1");
		int t=0;
		StringBuffer sb = new StringBuffer();
		ArrayList<String> temp = new ArrayList<String>();
		android.util.Log.i(TAG,"populateUserList point 2");
		for (int i = 0 ; i<myuserlist.size();i++){
		sb.append(myuserlist.get(i)); }
		android.util.Log.i(TAG,"populateUserList point 3");
		StringTokenizer st = new StringTokenizer(sb.toString(),"-");
        String[] tokens = new String[st.countTokens()];
        android.util.Log.i(TAG,"populateUserList point 4");
        while (st.hasMoreTokens()){
        	tokens[t++] = st.nextToken();
        }
        android.util.Log.i(TAG,"populateUserList point 5");
        switch (tokens.length){
        	case 1://only 1 user (yourself)
        		
        		String chatid1 = tokens[0].substring(tokens[0].indexOf(" ")+1,tokens[0].length());
        		String ipaddrss1 = tokens[0].substring(0,tokens[0].indexOf(" "));
        		myIP = ipaddrss1;
        		putintoDB(chatid1,ipaddrss1);
        		UserList.add(new User(myIP,chatid1,R.drawable.orange_smiley));
        		break;
        	case 2: //2 users
        		String chatid11 = tokens[0].substring(tokens[0].indexOf(" ")+1,tokens[0].length());
        		String ipaddrss11 = tokens[0].substring(0,tokens[0].indexOf(" "));
        		myIP = ipaddrss11;
        		UserList.add(new User(ipaddrss11,chatid11,R.drawable.orange_smiley));
        		String chatid22 = tokens[1].substring(tokens[1].indexOf(" ")+1,tokens[1].length());
        		String ipaddrss22 = tokens[1].substring(0,tokens[1].indexOf(" "));
        		UserList.add(new User(ipaddrss22,chatid22,R.drawable.black_white_smiley));
        		putintoDB(chatid11,ipaddrss11);putintoDB(chatid22,ipaddrss22);
        		break;
        	case 3://3 users
        		String chatid111 = tokens[0].substring(tokens[0].indexOf(" ")+1,tokens[0].length());
        		String ipaddrss111 = tokens[0].substring(0,tokens[0].indexOf(" "));
        		myIP = ipaddrss111;
        		UserList.add(new User(ipaddrss111,chatid111,R.drawable.orange_smiley));
        		String chatid222 = tokens[1].substring(tokens[1].indexOf(" ")+1,tokens[1].length());
        		String ipaddrss222 = tokens[1].substring(0,tokens[1].indexOf(" "));
        		UserList.add(new User(ipaddrss222,chatid222,R.drawable.black_white_smiley));
        		String chatid333 = tokens[2].substring(tokens[2].indexOf(" ")+1,tokens[2].length());
        		String ipaddrss333 = tokens[2].substring(0,tokens[2].indexOf(" "));
        		UserList.add(new User(ipaddrss333,chatid333,R.drawable.red_smiley));
        		putintoDB(chatid111,ipaddrss111);putintoDB(chatid222,ipaddrss222);putintoDB(chatid333,ipaddrss333);
        		break;
        }
        android.util.Log.i(TAG,"populateUserList point 6");
       
        
	}
	private void putintoDB(String chatid, String ipaddress) { //PUT INTO A SHARED PREFERENCES DATABASE
		chatidtable=getSharedPreferences(CHATID_TABLE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = chatidtable.edit();
		editor.putString(ipaddress,chatid);
		editor.commit();
	}

	private void populateListView() {
		android.util.Log.i(TAG,"populateListView point 1");
		ArrayAdapter<User> adapter = new MyListAdapter();
		android.util.Log.i(TAG,"populateListView point 2");
		ListView list = (ListView) findViewById(R.id.listView);
		android.util.Log.i(TAG,"populateListView point 3");
		list.setAdapter(adapter);
	}
	private void registerClickCallBack() {
		ListView list = (ListView)findViewById(R.id.listView);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View viewClicked,
					int position, long id) {
				User clickedUser = UserList.get(position);
				//SENDING REQUEST CHAT MESSAGE 
				//packet consists:
				//-------------------------------------
				//| source ip | dest ip | RC | Message|
				//-------------------------------------
				//RC = Request Chat
				//////////////////////////////////////
				android.util.Log.i(TAG,"Registered a click point 1");
				android.util.Log.i(TAG,"what is myIP: "+myIP);
				String requestChatMessage=myIP+" "+clickedUser.getIpAddress()+" "+"RC";
				//byte[] send = requestChatMessage.getBytes();
				//mService.write(send,clickedUser.getIpAddress());
				AddtoQueue(requestChatMessage);
				android.util.Log.i(TAG,"Registered a click point 2");
				//USE THE FOLLOWING CODE ELSEWHERE
				//Intent intent = new Intent(OnlineUsers.this,MsgForward.class);
				//intent.putExtra(EXTRA_CHATID, mychatID);
				//intent.putExtra(EXTRA_DESTCHATID, clickedUser.getChatID());
			   //intent.putExtra(EXTRA_DESTIP, clickedUser.getIpAddress());
				//stopService(serviceintent);
				//startActivity(intent);
				
			}
		});

	}
	
	private class MyListAdapter extends ArrayAdapter<User> {
		public MyListAdapter (){
			super(OnlineUsers.this, R.layout.item_view,UserList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//Make sure we have a view to work with (may have been given null)
			View itemView = convertView;
			if (itemView == null){
				itemView = getLayoutInflater().inflate(R.layout.item_view, parent,false);
			}
			//find the car to worth with
			android.util.Log.i(TAG,"MyListAdapter point 1");
			User currentUser = UserList.get(position);
			//fill the view
			android.util.Log.i(TAG,"MyListAdapter point 2");
			ImageView imageView = (ImageView) itemView.findViewById(R.id.item_icon);
			imageView.setImageResource(currentUser.getIconID());
			
			//ChatID
			TextView chatText = (TextView) itemView.findViewById(R.id.item_ChatID);
			chatText.setText(currentUser.getChatID());
			
			//IP
			TextView addrText = (TextView) itemView.findViewById(R.id.item_IPAddress);
			addrText.setText(""+currentUser.getIpAddress());

			return itemView;
		}
		
	}
	private void setupStartbtn() {
		btn_start = (Button)findViewById(R.id.bStart);
		btn_start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//serviceintent = new Intent (OnlineUsers.this,RoutingService.class);
				//startService(serviceintent);
				//android.util.Log.i(TAG,"Calling RoutingService");
				if (status){
				discovery_service.RequestTable();					
				
				Toast.makeText(getBaseContext(), "Wait atleast 5 seconds", Toast.LENGTH_SHORT).show();
				btn_start.setVisibility(View.INVISIBLE);
				btn_stop.setVisibility(View.VISIBLE);
				//TESTING
					
				discovery_service.StartReceivingandsending(mychatID);
				discovery_service.Refresh();
				Toast.makeText(getBaseContext(), "Starting...", Toast.LENGTH_SHORT).show();
				btn_start.setVisibility(View.INVISIBLE);
				btn_stop.setVisibility(View.VISIBLE);
				
				}
				else {
					Toast.makeText(getBaseContext(), "Service not ready to use yet", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}


	private void setupStopbtn() {
		btn_stop = (Button)findViewById(R.id.bStop);
		btn_stop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//
				discovery_service.getTable();	
				myuserlist = discovery_service.stopmysearch();
				unBindMethod();
				discovery_service.Close();
				populateUserList();
				populateListView();
				btn_stop.setVisibility(View.INVISIBLE);
				printDBtoLOG();
			}
		});
	}


	public void bindMethod(){
		android.util.Log.i(TAG,"inbindMethod()");
		Intent i = new Intent(this,UsersDiscoveryService.class);
		bindService(i, sc, Context.BIND_AUTO_CREATE);
		android.util.Log.i(TAG,"just called bindservice");
		status = true;
		Toast.makeText(getBaseContext(), "Service binded successfully", Toast.LENGTH_LONG).show();
		
	}
	
	public void unBindMethod(){
		if (status){
			unbindService(sc);
			Toast.makeText(getBaseContext(), "Service unbinded successfully", Toast.LENGTH_LONG).show();
			status = false;
		}
		else{
			Toast.makeText(getBaseContext(), "Service already unbinded", Toast.LENGTH_LONG).show();
		}
	}
	

	private ServiceConnection sc = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			android.util.Log.i(TAG,"Service connected");
			LocalBinder binder = (LocalBinder) service;
			discovery_service = binder.getService();
			status = true;
			
		}
	};
	//put objects into ConcurrentListQueue
	public static void AddtoQueue (String str){
		
		queue.add(str);
	}
	
	//Thread that polls queue and sends object to listening port
	private class SendThread extends Thread {
		DatagramSocket uniSocket;
		private int LISTENING_PORT1 = 11200; 
		private static final String TAG = "SendThread";
		private int UNI_PORT = 11250;
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
			while ((str = queue.poll())!=null){
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			}
			}
		}
	}
	
	public static class MyAlert extends DialogFragment {
		protected static final String EXTRA_KEY_CHATID1 = "Chatid1";
		protected static final String EXTRA_KEY_SOURCEIP1 = "Sourceip1";
		protected static final String EXTRA_KEY_DESTIP1 = "Destip1";
		protected static final String EXTRA_KEY_MYCHATID1 = "Mychatid1";
		protected static final String EXTRA_KEY_MYIP1 = "Myip1";
		private static final String TAG = "MyAlert";
		String chatid;
		String destip;
		String sourceip;
		String myip;
		String mychatid;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			chatid = getArguments().getString(OnlineUsers.BUNDLE_KEY_CHATID);
			destip = getArguments().getString(OnlineUsers.BUNDLE_KEY_DESTIP);
			myip = getArguments().getString(OnlineUsers.BUNDLE_KEY_MYIP);
			mychatid = getArguments().getString(OnlineUsers.BUNDLE_KEY_MYCHATID);
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Chat Notification");
			builder.setMessage(chatid+" wants to chat with you");
			builder.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(getActivity(), "Negative button was clicked", Toast.LENGTH_SHORT).show();
				}
			}); 
			builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(getActivity(), "Positive button was clicked", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent (getActivity().getBaseContext(),newMessageFwd.class);
					intent.putExtra(EXTRA_KEY_CHATID1, chatid);
					intent.putExtra(EXTRA_KEY_DESTIP1, destip);
					intent.putExtra(EXTRA_KEY_MYCHATID1,mychatid);
					intent.putExtra(EXTRA_KEY_MYIP1,myip);
					String sendACK =myip+" "+destip+" "+"RA";
					OnlineUsers.AddtoQueue(sendACK);
					mService.stop();
					startActivity(intent);
				}
			}); 
			Dialog dialog = builder.create();
			
			return dialog;
		}
		

	}


	@Override
	protected void onPause() {
		
		super.onPause();
	}

	@Override
	protected void onResume() {	
		super.onResume();
		//android.util.Log.i();
	}

	@Override
	protected void onStop() {
		
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}

}
