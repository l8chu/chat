package com.example.chat;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;



import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MsgForward extends Activity{
	private Button send;
	private TextView msg;
	private String ip;
	private String DESTID, DESTIP;
	private String message_to_send;
	private MsgForwardService FChatService = null;
	private EditText et;
	private DatagramPacket fpacket;
	private DatagramSocket fSocket;
	private static final String TAG1 = "fthread";
	 private Handler fHandler;
	 private int UNI_PORT = 11103;
	    Context fContext ;
	    String MsgReceived;
	    String DestIP;
	    String theirChatID;
	    byte newfpacket[] = new byte[1024];
 	DatagramSocket uniSocket;
	//private byte[] buf = new byte[1024];
	public static final int MESSAGE_ERROR = 7;
	public static final int MESSAGE_WRITE1 = 1;
	public static final int MESSAGE_READ=5;
	public static final int MESSAGE_TOAST=8;
	private int FOR_PORT = Preferences.ForwardPort;
    // Key names received from the BroadcastChatService Handler
    public static final String TOAST1 = "toast1";
    private String name;
    private String chatID;
    private String myLocalIP,myBcastIP;
	private static final String TAG = "MsgForward";
	private final Handler handler = new Handler(){
    	public void handleMessage(Message msg_to_ap){
    		switch(msg_to_ap.what){
    		case MESSAGE_READ:
    			Toast.makeText(getBaseContext(),"In Handler:", Toast.LENGTH_SHORT).show();
    			String to_app = (String) msg_to_ap.obj;
    			Toast.makeText(getBaseContext(),"In Handler String:" + to_app, Toast.LENGTH_SHORT).show();    	
    			msg.append("\n" + to_app);
    			break;
    		case MESSAGE_WRITE1:
    			byte[] whatisent = (byte[]) msg_to_ap.obj;
    			String wht = new String(whatisent);
    			msg.append("\n"+chatID+": "+wht);
    			break;
    		case MESSAGE_ERROR:
    			Toast.makeText(getBaseContext(),"Unsuccessful transmission! please resend message", Toast.LENGTH_SHORT).show();
    			msg.append("\nUnsuccessful transmission! please resend message");
    			break;
    		case MESSAGE_TOAST:
    			String toastmsg = (String) msg_to_ap.obj;
    			Toast.makeText(getBaseContext(), toastmsg, Toast.LENGTH_SHORT).show();
    		//	Toast.makeText(getBaseContext(),, Toast.LENGTH_SHORT).show();
    			
    			break;
    		}
    	}
    };
    
    
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

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	    	super.onCreate(savedInstanceState);
	        //Get chatid
	        Bundle extras = getIntent().getExtras();
	        chatID=extras.getString(OnlineUsers.EXTRA_CHATID);
	        DESTID = extras.getString(OnlineUsers.EXTRA_DESTCHATID);
	        DESTIP = extras.getString(OnlineUsers.EXTRA_DESTIP);
	        // Set up the window layout
	        setContentView(R.layout.msg_forward);
	        msg = (TextView) findViewById(R.id.msg_in);
	        msg.setSingleLine(false);
	        msg.setMovementMethod(new ScrollingMovementMethod());
	        
	        android.util.Log.i(TAG, "In onCreate");

	        send = (Button) findViewById(R.id.send_button);
	        
	        setupChat();
	     
	    }
	    private void setupChat() {
	    	android.util.Log.i(TAG,"in setupChat");
	    	TextView MYIPdisplay = (TextView) findViewById(R.id.textmyID);
	    	TextView DESTIDdisplay = (TextView) findViewById(R.id.textDestID);
	    	TextView DESTIPdisplay = (TextView) findViewById(R.id.textDestIP);
	    	MYIPdisplay.setText(chatID+"(you)");
	    	DESTIDdisplay.setText(DESTID);
	    	DESTIPdisplay.setText(DESTIP);
	    	et = (EditText) findViewById(R.id.msg_out);
	    	
	    	   send.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
							String message = et.getText().toString();
							//msgfordisplaying = message;
							msg.append("\n" + chatID+"(You): "+message+"\n" );
							et.setText("");
							android.util.Log.i(TAG,"in setupChat DESTIP:"+DESTIP);
							message_to_send = chatID + " "+ DESTIP + " " + message;
							android.util.Log.i(TAG,"in setupChat sendpckt:"+message_to_send);
							sendMessage(message_to_send);
							Log.i(TAG,"message:" + message + ip);
							//msg.append("the Msg sent is     " + message + "  to the ip address    " + ip);
							//msg.append(" \n");
							//msg.append(" \n");
				           	String data = new String (message_to_send);
				           	android.util.Log.i(TAG1,"inwrite()");
				           	
							try {
								fpacket = new DatagramPacket(data.getBytes(), data.length(), 
								  InetAddress.getByName(myBcastIP), FOR_PORT);
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				           	
				           	android.util.Log.i(TAG1,"inwrite() point 1");
				               try {
								fSocket.send(fpacket);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
						
					}
		        });
	    	   //FChatService = new MsgForwardService(this, handler);
		}
		private void sendMessage(String message) {
	    	
			android.util.Log.i(TAG,"sendmessage()");
	    	
	        // Check that there's actually something to send
	        if (message.length() > 0 ) {
	            // Get the message bytes and tell the BluetoothChatService to write
	            byte[] send = message.getBytes();
	            //FChatService.write(send);
	        }
	    }
	    @Override
	    public synchronized void onResume() {
	        super.onResume();
	        Log.i(TAG, "+ ON RESUME +");
	        
	        //FChatService.start();
	    }
	    public synchronized void onPause() {
	        super.onPause();
	        Log.i(TAG, "- ON PAUSE -");
	    }


	    public void onStop() {
	        super.onStop();
	        //if (FChatService != null) FChatService.stop();
	        Log.i(TAG, "-- ON STOP --");
	    }


	    public void onDestroy() {
	        super.onDestroy();
	        // Stop the Broadcast chat services
	     //   if (FChatService != null) FChatService.stop();
	        Log.i(TAG, "--- ON DESTROY ---");
	    }
}
