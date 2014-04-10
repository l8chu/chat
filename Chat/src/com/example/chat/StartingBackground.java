package com.example.chat;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartingBackground extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		getActionBar().hide(); //hide the action bar
		setContentView(R.layout.background);
		
		Thread timer = new Thread(){
			public void run (){
				try{
					sleep(3000);//sleep for 3 seconds
				}catch(InterruptedException e){
					e.printStackTrace();
				}finally{
					Intent intent = new Intent(StartingBackground.this,Login.class);
					startActivity(intent);
				}
			}
		};
		timer.start();
	}
	
	

}
