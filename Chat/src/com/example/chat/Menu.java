package com.example.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class Menu extends Activity {
	private static final String TAG = "Menu";
	protected static final String EXTRA_MYCHATID = "mychatid";
	private DiscoverPeers type;
	String chatID;
	Button btn_broadcast;
	Button btn_onlineusers;
	Button btn_signout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActionBar().hide(); //hide the action bar
		Bundle extras = getIntent().getExtras();
		chatID = extras.getString(Login.EXTRA_KEY_CHATID);
		android.util.Log.i(TAG, "chatIDread: "+chatID);
		setContentView(R.layout.menu);
		setupBroadcastButton();
		setupOnlineUsersButton();
		setupSignoutButton();
		
		
	}


	private void setupSignoutButton() {
		btn_signout = (Button) findViewById(R.id.bSignout);
		btn_signout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}


	private void setupBroadcastButton() {
		btn_broadcast = (Button)findViewById(R.id.bBroadCastChat);
		btn_broadcast.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Menu.this,BroadcastChat.class);
				startActivity(intent);
			}
		});
	}


	private void setupOnlineUsersButton() {
		btn_onlineusers = (Button)findViewById(R.id.bOnlineUsers);
		btn_onlineusers.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Menu.this,OnlineUsers.class);
				intent.putExtra(EXTRA_MYCHATID, chatID);
				startActivity(intent);
			}
		});
	}

}

