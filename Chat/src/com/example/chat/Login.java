package com.example.chat;



import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.Timer;

public class Login extends Activity implements OnClickListener{
	protected static final String EXTRA_KEY_CHATID = "CHATID";
	private static final String ROUTING_TABLE = "RoutingTable";
	private static final String CHATID_TABLE = "ChatIDTable";

	private Button ok;
	private TextView messagebox;
	private EditText mEditTextContent;
	//FileOutputStream fos = null;
	String chatid;

	public String getChatID(){
		return chatid;
	}

	
	protected void onCreate(Bundle savedInstanceState) {
	    
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginscreen);
		SharedPreferences CHATTABLE;
	    SharedPreferences ROUTETABLE;

		//CLEAR CHATID TABLE
		CHATTABLE = getSharedPreferences(CHATID_TABLE, Context.MODE_PRIVATE);
		CHATTABLE.edit().clear().commit();
		
		ok = (Button) findViewById(R.id.OKbutton);
		mEditTextContent = (EditText) findViewById(R.id.chatid);	
		
	//	thread.start();
	                //   listenthread.start();
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(v.getId() == R.id.OKbutton){
					chatid = mEditTextContent.getText().toString();
					Toast.makeText(getApplicationContext(), chatid, Toast.LENGTH_SHORT).show();
					if(chatid.length() > 0){
						//File r = android.os.Environment.getExternalStorageDirectory();
						//File dir = new File (r.getAbsolutePath());
						File root = new File(Environment.getExternalStorageDirectory(),"chatid");
					try {
							//fos = openFileOutput(chatid, MODE_PRIVATE);
						if(!root.exists()){
							root.mkdir();
						}
						File myfile = new File(root,chatid);
						
						if(!myfile.exists()){
							myfile.createNewFile();
						}
						FileOutputStream fos;
						try{
							fos = new FileOutputStream(myfile);
						}catch (Exception e) {}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
					FileInputStream fis;
					String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
				//	try {
						//fis=openFileInput(chatid);	
						File fis1 = new File(dir,chatid);
					    if(fis1!=null){
					    	Toast.makeText(getApplicationContext(), "Chat ID Created", Toast.LENGTH_SHORT).show();

					    	//fis.close();
					    }	
					    else{
					    	Toast.makeText(getApplicationContext(), "Chat ID failed to create", Toast.LENGTH_SHORT).show();
					    }


					Intent openMainActivity = new Intent("com.example.chat.MENU");
					openMainActivity.putExtra(EXTRA_KEY_CHATID, chatid);
					startActivity(openMainActivity);
			    }
					
				
		
		});

}
	
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

}