package com.example.chat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ContactList extends Activity{
	private static final String TAG = "ContactList";
	private ListView contactlist;
	static File root = new File(Environment.getExternalStorageDirectory(),"Log");
	String path = root.getAbsolutePath();
	private File filename = new File(root,"contact.txt");
	StringBuilder text = new StringBuilder();
	public ArrayList<String> list = new ArrayList<String>();
	private String array;
	private String newarray;
	private String[] ip_array;
	
	@Override
public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		contactlist = (ListView) findViewById(R.id.contact_in);	
		
//		try {
//			Scanner br = new Scanner(filename);
//			
//			String line;
//			String ip;
//		
//			while (br.hasNext()) {
//
//			ip = br.nextLine();
//			Log.log(ip);
//		    list.add(ip);
//			
//		}
//		} catch (IOException e) {}
		
//		  ArrayList<String> arraylist = new ArrayList<String>();  
//		  Bundle bundle = new Bundle();  
//		  bundle.getParcelableArrayList("arraylist");
	
		ArrayList<String> myArray = SendList.data().arrayList;
		//ArrayList<ArrayList<String>> collection;
		if (myArray != null){
			android.util.Log.i(TAG, "myArray == null");
		
		for(int i=0;i<myArray.size();i++){
		array = SendList.data().arrayList.get(i);
		}
		Log.log("newarray1 " + array.substring(1, array.length()-1));
		newarray = array.substring(1, array.length()-1);
		ip_array = newarray.split(",");
		for(int i=0;i<ip_array.length;i++){
		
//		Log.log("first ip_array " + ip_array[0]);
		Log.log(" ip_array " + i + ":" + ip_array[i].trim());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
              android.R.layout.simple_list_item_1, ip_array);
		contactlist.setAdapter(adapter);
		}
		
		//String[] newarray = array.split(" ");
		
//		ArrayList<String> myArray = SendList.data().arrayList;
//		Log.log("myArray " + myArray);
//		String array = new String();
//		String[] newarray = array.split(" ");
//		Log.log("array " + array);
//		for(int i=0;i<myArray.size();i++)
//			 array = SendList.data().arrayList.get(i).toString();
//		Log.log("newarray " + newarray[1].substring(0, newarray[1].length()-2));

//		Log.log("here1 " + array.substring(1, array.length()-1).split(" "));

//		Intent inte = this.getIntent(); 
//
//		ListParcelable object = inte.getParcelableExtra("key");

//		Intent in = getIntent();
//		ArrayList<String> list =(ArrayList<String>) in.getSerializableExtra("key");
		
//	    Log.log("over here" + object.getArrList().get(0).getData());
//	    
//		
//		String items[] = new String[object.getSize()];
//		Log.log("here?");
//		for(int i=0;i<object.getSize();i++){
//			items[i] = object.getArrList().get(i).getData();
//		}
		
		
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, myArray);
		
}
	
//public void onStart() {
//    super.onStart();
//     Log.log("contactlist start");
//}
//
//@Override
//public synchronized void onResume() {
//    super.onResume();
//     Log.log("contactlist resume");
//}
//public synchronized void onPause() {
//    super.onPause();
//    Log.log("contactlist PAUSE ");
//}
//public void onStop(){
//	super.onStop(); 
////finish();
//}
//@Override
//protected void onDestroy() {
//    Log.log("contactlist onDestroy");
//    super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.
//}
}
