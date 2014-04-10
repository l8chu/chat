package com.example.chat;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;


public class UsersDiscoveryService extends Service {
	private static final String ROUTING_TABLE = "RoutingTable";
	private static final String TAG = "UsersDiscoveryService";
	private String myBcastIP;
	private String myLocalIP;
	private String DestIP;
	private String myChatID;
	private volatile List<String> list = new ArrayList<String>();// usertable;
	private int FOR_PORT = Preferences.ListeningPort;
	private int UNI_PORT = 11102;
	DatagramSocket fSocket ;
	fusersthread fthread;
	busersthread bthread;
	//for getting routing table from olsr
	private String host = "localhost";
	private int port = 2006;
	private String myRoutes;
	private static final String REQUEST_ROUTES	 	= "/rou";
	private TCPClientTask networktask;
	//DATABASE WITH ROUTING TABLE key->DestIP, value -> NextHop
	SharedPreferences sharedpreferences;
	
	private final IBinder mbinder = new LocalBinder();
	@Override
	public IBinder onBind(Intent intent) {
		android.util.Log.i(TAG,"Binding..");
		return mbinder;
	}
	
	public class LocalBinder extends Binder{
		public UsersDiscoveryService getService(){
			return UsersDiscoveryService.this;
		}
	}
	public void StartReceivingandsending(String myChatID){
		android.util.Log.i(TAG,"do i get here");
		this.myChatID = myChatID;
		//Start receiving
		fthread = new fusersthread();
		fthread.start();
		android.util.Log.i(TAG,"do i get here??");

	}
	
	public void RequestTable(){
		networktask = new TCPClientTask();
		networktask.execute();	
	}
	
	public void getTable(){
		byte[] cmd = REQUEST_ROUTES.getBytes();
		networktask.SendDataToNetwork(cmd);
	}
	
	public void Refresh(){
		//Thread to send broadcast packet about known peers
		android.util.Log.i(TAG,"in refresh()");
		bthread = new busersthread();
		bthread.start();
		android.util.Log.i(TAG,"Refresh do i get here??");
	}

	//method called by Online Users Activity on Refresh button clicked
	public List<String> stopmysearch(){
		//list.add(myLocalIP+" "+myChatID);
		return list; 
	}
	
	@SuppressWarnings("deprecation")
	public void Close(){
		android.util.Log.i(TAG,"inClose()");
		fthread.cancel();
		bthread.cancel();
		//To guarantee thread is terminated
		if (fthread!=null){
			fthread= null;
		}
		if (bthread!=null){
			bthread = null;
		}
	}
	
	private class TCPClientTask extends AsyncTask<Void, byte[], Boolean> {
		Socket socket; //Network Socket
		InputStream is; //Network Input Stream
		OutputStream os; //Network Output Stream
		byte[] buffer = new byte[4096];
		private String TAG2 = "TCPClientTask";
		StringBuffer tempBuffer = new StringBuffer();
		@Override
		protected void onPreExecute() {
			android.util.Log.i(TAG, "onPreExecute");
		}

		@Override
		protected Boolean doInBackground(Void... params) { //This runs on a different thread
			boolean result = false;
			try {
				// Connect to address
				android.util.Log.i(TAG, "doInBackground: Creating socket");
				SocketAddress sockaddr = new InetSocketAddress(host, port);
				android.util.Log.i(TAG2, "point1");
				socket = new Socket();
				socket.connect(sockaddr, 5000); //10 second connection timeout
				android.util.Log.i(TAG2, "point2");
				if (socket.isConnected()) {
					android.util.Log.i(TAG2, "socket is connected point3");
					is = socket.getInputStream();
					os = socket.getOutputStream();
					android.util.Log.i("AsyncTask", "doInBackground: Socket created, streams assigned");
					android.util.Log.i("AsyncTask", "doInBackground: Waiting for inital data...");
					int read;
					//This is blocking
					while((read = is.read(buffer, 0, 4096)) > 0 ) {
						byte[] tempdata = new byte[read];
						System.arraycopy(buffer, 0, tempdata, 0, read);
						
						publishProgress(tempdata);
						android.util.Log.i(TAG, "doInBackground: Got some data");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				android.util.Log.i(TAG, "doInBackground: IOException");
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
				android.util.Log.i(TAG, "doInBackground: Exception");
				result = true;
			} finally {
				try {
					is.close();
					os.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				android.util.Log.i(TAG, "doInBackground: Finished");
			}
			return result;
		}
		

		public boolean SendDataToNetwork(final byte[] cmd) { //You run this from the main thread.
			// Wait until socket is open and ready to use
			if (socket.isConnected()) {
				android.util.Log.i(TAG, "SendDataToNetwork: Writing received message to socket");
				new Thread(new Runnable() {
					public void run() {
						try {
							os.write(cmd);
						}
						catch (Exception e) {
							e.printStackTrace();
							android.util.Log.i(TAG, "SendDataToNetwork: Message send failed. Caught an exception");
						}
					}
				}
						).start();
				return true;
			}
			else
				android.util.Log.i(TAG, "SendDataToNetwork: Cannot send message. Socket is closed");

			return false;
		}

		@Override
		protected void onProgressUpdate(byte[]... values) {
			if (values.length > 0) {
				android.util.Log.i(TAG, "onProgressUpdate: " + values[0].length + " bytes received.");
				//testing purpose
				String str=null;
				try {
					str = new String(values[0], "UTF-8");
					tempBuffer.append(str);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
				android.util.Log.i(TAG,"onProgressUpdate: str: "+str);
				//appendToOutput(buffer);
			}
		}
		
		@Override
		protected void onCancelled() {
			android.util.Log.i(TAG, "Cancelled.");
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				android.util.Log.i(TAG, "onPostExecute: Completed with an Error.");

			} else {
				android.util.Log.i(TAG, "onPostExecute: Completed.");
				myRoutes=tempBuffer.toString(); 
				//ProcessRoutestoDatabase();
				android.util.Log.i(TAG, "myRoutes: "+myRoutes);
				ArrayList<String> Destip = new ArrayList<String>();
				ArrayList<String> Nexthop =new ArrayList<String>();
				String routeStr = myRoutes;
				String test = routeStr.substring(routeStr.indexOf("Table: Routes"));
				android.util.Log.i(TAG,"test: "+test);
				String [] routes = test.split("\n");
				String [] tokens=null;
				//Create a Pattern object
				//USING REGEX TO FIND IP + NEXT HOP
				String pattern = "(\\d{3}\\.\\d{3}\\.\\d\\.\\d{3})";
				Pattern r = Pattern.compile(pattern);
				Matcher m = r.matcher(routeStr);
				//PUT IP AND NEXT HOP INTO CORESSPONDING ARRAYS
				while (m.find()){
					int i =0;
						Destip.add(m.group());
						
					android.util.Log.i(TAG2,"m.group[]"+ m.group());

				}
				//CLEAR DATABASES LEFT OVER FROM LAST TIME USING
				
				sharedpreferences = getSharedPreferences(ROUTING_TABLE, Context.MODE_PRIVATE);
				sharedpreferences.edit().clear().commit();//clear all previous and old entries
				SharedPreferences.Editor editor = sharedpreferences.edit();
				for (int i=0;i<Destip.size();i++){//put IP and Nexthop into database (SharedPreferences: key -> dest. ip, value -> nexthop 
					editor.putString(Destip.get(i),Destip.get(i+1));
					editor.commit();
					//debugging purposes!
					//Log.log("DEST IP: "+Destip.get(i) + "NEXT HOP: "+Nexthop.get(i)); 
					android.util.Log.i(TAG2,"Destip["+i+"]:"+Destip.get(i));
					
					android.util.Log.i(TAG2,"Destip["+(i+1)+"]:"+Destip.get(i+1));
					i++;
				}
				//print ROUTE TABLE TO LOG FILE and screen for testing 
				SharedPreferences temp= getSharedPreferences(ROUTING_TABLE,Context.MODE_PRIVATE);
				Log.log("Routing Table:");
				Map<String,?> keys = temp.getAll();
				StringBuffer tempb= new StringBuffer();
				for(Map.Entry<String,?> entry : keys.entrySet()){
				            Log.log(entry.getKey() + " ----> " + 
				                                   entry.getValue().toString()+"\n");            
				            tempb.append(entry.getKey()+"---->"+entry.getValue()+"\n");
				
				}
				 
				Toast.makeText(getApplicationContext(), tempb.toString(), Toast.LENGTH_LONG).show();
				
				//print routing table to log file
				
			}
			Toast.makeText(getApplicationContext(), "Routing Table Retrieved", Toast.LENGTH_LONG).show();
		}

		//private void //ProcessRoutestoDatabase() {
		//}
	}	
	//Used in Online Users Layout use another thread for 
	public class fusersthread extends Thread{
	    	private static final String TAG1 = "fusersthread";

	    	DatagramSocket uniSocket;
	    	String theirChatID,theirIP;
	    	private DatagramPacket fpacket;
	    	
	    	 public fusersthread() {
	         	
	         	try { 
	         		   //myBcastIP 	= "192.168.1.255";
	         		   myBcastIP = getIP.getBroadcast().toString();
	         		   android.util.Log.i(TAG1,"my bcast ip : "+myBcastIP);
	         		   
	         		   myLocalIP 	= getIP.getLocalAddress().toString();
	         		   //myLocalIP = broadip.getIPaddress().toString();
	         		   android.util.Log.i(TAG1,"my local ip : "+myLocalIP);
	         		   
	         		   fSocket 		= new DatagramSocket(FOR_PORT); 
	         		  // fSocket.setBroadcast(true); 
//	         		   lSocket 		= new DatagramSocket(Lis_Port); 
//	         		   lSocket.setBroadcast(true); 
	         		   list.add("-"+myLocalIP+" "+myChatID);
	         	     } catch (IOException e) { 
	         	    	 android.util.Log.i(TAG,"Could not make socket"); 
	         	     } 
	         }
	    	public void run() {
        	
    		try {
    			
    			byte[] buf = new byte[1024]; 
    			
    			//Listen on socket to receive messages 
    			
    			while (true) { 
    				fpacket = new DatagramPacket(buf, buf.length); 
	    		    fSocket.receive(fpacket); 
	    		    android.util.Log.i(TAG1,"packet coming from:" + fpacket.getAddress().toString());
	    		    InetAddress remoteIP = fpacket.getAddress();
	    		    android.util.Log.i(TAG1,"point 1");
	    		    if(remoteIP.equals(myLocalIP))
	    				continue;
	    			//Process packet 
	    		    android.util.Log.i(TAG1,"point 2");
	    			String s = new String(fpacket.getData(), 0, fpacket.getLength());
	    			android.util.Log.i(TAG1,"s: "+s);
	    			//fHandler.obtainMessage(MsgForward.MESSAGE_TOAST, s);
	    			android.util.Log.i(TAG1,"point 3");
	    			if (!ProcessPacket(s)) //If packet corrupted return to while loop
	    				continue;
	    			//else if successful transmission then do...
	    			android.util.Log.i(TAG1,"point 4");
	    			android.util.Log.i(TAG1,"myLocalIP:"+myLocalIP);
	    			//send bcast packet
	    			// packet no errors Forward packet to others
	    				android.util.Log.i(TAG1,"point 6");
	    				//unicastWrite(newfpacket);
	    			android.util.Log.i(TAG1,"Received response " + s); 
         
    			}
			} catch (IOException e) {
    			e.printStackTrace();
			}
    		
        }		

			private boolean ProcessPacket(String pckt) {
			
        	android.util.Log.i(TAG1,"in process packet");
        	boolean successful = true;
        	int t=0;
        	boolean unsuccessful = false;

            StringTokenizer st = new StringTokenizer(pckt,"-");
            String[] tokens = new String[st.countTokens()];
          
            while (st.hasMoreTokens()){
            	tokens[t++] = st.nextToken();
            	
            }
            
			android.util.Log.i(TAG1, "length of tokens[]:"+tokens.length);
			for (int i=0;i<tokens.length;i++){

				list.add("-"+tokens[i]);

			}
			//make sure we remove duplicates in our userlisttable
			Set<String> s = new LinkedHashSet<String>(list);
			list = new ArrayList<String>(s);
			android.util.Log.i(TAG,"in packetprocessing: "+ list.toString());

			return true;
			}
	    	 public void cancel() {
	             try {
	                 
	                 fSocket.close();
	             } catch (Exception e) {
	                 android.util.Log.i(TAG,"close() of connect socket failed");
	             }
	         }	
	}
	
	public class busersthread extends Thread{
    	private static final String TAG1 = "busersthread";

    	DatagramSocket uniSocket;
    	String theirChatID,theirIP;
    	private DatagramPacket fpacket;
    	
    	 public busersthread() {

         }
    	public void run() {
    		Random randomnum = new Random();
    		String packet;
    		DatagramPacket bpacket;

    		try {
    			while(true){
    				Thread.sleep(randomnum.nextInt(2000));
                	//convertListtoString();
                	StringBuffer sb = new StringBuffer();
            		for (int i =0 ; i<list.size();i++){
            		sb.append(list.get(i));
            		}
            		packet = sb.toString();
                	//sendbuff = packet.getBytes();
            		android.util.Log.i(TAG1,"point 1");
                	bpacket = new DatagramPacket(packet.getBytes(), packet.length(), 
                    		InetAddress.getByName(myBcastIP), FOR_PORT);
                	android.util.Log.i(TAG1,"point 1.5");
                    fSocket.send(bpacket); 
                    android.util.Log.i(TAG1,"point 2");
    			}
                } catch (Exception e) {
                    Log.log("Exception during write");
                }
		}	
    	public void cancel() {
            try {
                
                fSocket.close();
            } catch (Exception e) {
                Log.log("close() of connect socket failed");
            }
        }	
	
	}	

}
