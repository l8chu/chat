package com.example.chat;

import java.io.File;
import java.io.IOException;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
 
/**
 * This is the main Activity that displays the current chat session.
 */
public class BroadcastChat extends Activity {

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_READ 	= 1;
    public static final int MESSAGE_WRITE 	= 2;
    public static final int MESSAGE_TOAST 	= 3;

    // Key names received from the BroadcastChatService Handler
    public static final String TOAST = "toast";

    // Layout Views
    private ListView 	mConversationView;
    private EditText 	mOutEditText;
    private Button 		mSendButton;

    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Member object for the chat services
    private BroadcastChatService mChatService;
    private String IP;
    private String ip_own;
    private String name_own;
    private String name_from;
    private String name;
    private nameTask nameTask;
    private ipTask iptask;
    private String message;
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
    private final Handler mHandler = new Handler() {
    	
    	
        @Override
        public void handleMessage(Message msg) {
        	
        	Log.log("[handleMessage !!!!!!!!!!!! ]");
        	
            switch (msg.what) {
            
	            case MESSAGE_WRITE:
	            	
	                byte[] writeBuf = (byte[]) msg.obj;
	                // construct a string from the buffer
	                String writeMessage = new String(writeBuf);
	                mConversationArrayAdapter.add(writeMessage);
	                
	                
	                
	                break;
	            case MESSAGE_READ:
	                String readBuf = (String) msg.obj;
	                mConversationArrayAdapter.add(readBuf);
	                 
	                
	                
	                break;               
	            case MESSAGE_TOAST:
	                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
	                               Toast.LENGTH_SHORT).show();
	                break;
            }
        }
    };    
    
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
public class ipTask extends AsyncTask<Void, Void, String>{

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
       try {
		ip_own = getIP.getLocalAddress().toString();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
	
}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         Log.create();
         Log.log("------------Starting BroadcastChat activity----------");

        // Set up the window layout
        setContentView(R.layout.main);
        nameTask = new nameTask();
        nameTask.execute();
        iptask = new ipTask();
        iptask.execute();
        
    }

   
    public void onStart() {
        super.onStart();
         Log.log("------------Start BroadcastChat activity----------");

        setupChat();
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
         Log.log("------------Resume BroadcastChat activity----------");
        
        mChatService.start();
    }


    private void setupChat() {
        Log.log("-------------setupChat()--------------");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        mConversationView = (ListView) findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
  //      mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Log.log("[sendButton clicked]");
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message1 = name_own  + " from " + ip_own + ":" + view.getText().toString();
                sendMessage(message1);
                
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BroadcastChatService(this, mHandler, IP);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }
    
    
    public synchronized void onPause() {
        super.onPause();
        Log.log("------------Pause BroadcastChat activity----------");
    }


    public void onStop() {
        super.onStop();
        if (mChatService != null) mChatService.stop();
        Log.log("------------Stop BroadcastChat activity----------");
    }


    public void onDestroy() {
        super.onDestroy();
        // Stop the Broadcast chat services
        if (mChatService != null) mChatService.stop();
        Log.log("------------Destroy BroadcastChat activity----------");
    }
    
    

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
    	
    	Log.log("[sendMessage]");
    	 
        // Check that there's actually something to send
        if (message.length() > 0 ) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            Log.log("sendMessage is " + send);
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
            
        }
        
        
    }


    // The action listener for the EditText widget, to listen for the return key
//    private TextView.OnEditorActionListener mWriteListener =
//        new TextView.OnEditorActionListener() {
//        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
//            // If the action is a key-up event on the return key, send the message
//            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
//                String message = name_own  + " from " + ip_own + ":" + view.getText().toString();
//                sendMessage(message);
//            }
//            Log.log("END onEditorAction");
//            return true;
//        }
//    };
}