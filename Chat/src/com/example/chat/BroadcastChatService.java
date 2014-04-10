
package com.example.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
//import android.util.Log;
import android.widget.Toast;

/**
 * This class does all the work for sending and receiving broadcast packets. 
 * It has a thread that listens for incoming packets.
 */
public class BroadcastChatService {

    // Member fields
    private  final Handler mHandler;
    private ComThread1 mConnectedThread;
    private String ip;
    private DatagramPacket mpacket;
    private getIP broadip;
    private boolean listening = true;
    private String space = " ";
    private String t;
	private static final int BCAST_PORT = 11111;
	private DatagramSocket mSocket ;
	private String myBcastIP;
    Context mContext ;
    /**
     * Constructor. Prepares a new Broadcast service.
     * @param context  The UI Activity Context
     * @param handler  A Handler to send messages back to the UI Activity
     */

    public BroadcastChatService(Context context, Handler handler, String IP) {
        //mAdapter = BluetoothAdapter.getDefaultAdapter();
    	mContext = context;
        mHandler = handler;
        this.ip = IP;
    }
    


	public String getIpAddress(){
    	Thread thread2 = new Thread(){
       	 public void run() {
    	 ip = mpacket.getAddress().toString();
       	 }
    	};
    	return ip;
    }
    
    /**
     * Start the chat service. Specifically start ComThread to begin 
     * listening incoming broadcast packets. 
     */
    public synchronized void start() {
         Log.log("start");
        
        mConnectedThread = new ComThread1();
        mConnectedThread.start();
    }


    /**
     * Stop thread
     */
    public synchronized void stop() {
         Log.log("stop");
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
    }


    public void write(final byte[] out) {

       // mConnectedThread.write(out);
    	
    	new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				writeMessage(out);
			}
    		
    	}).start();
    }

    
    /**
     * This thread handles all incoming and outgoing transmissions.
     */    
        public void writeMessage(byte[] buffer) {
        	Log.log("buffer is " + buffer);
           

            	String data = new String (buffer);

                try {
					mpacket = new DatagramPacket(data.getBytes(), data.length(), 
							InetAddress.getByName(myBcastIP), BCAST_PORT);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					android.util.Log.i("inside write", "exception during create mpacket");
				}
                
                try {
					mSocket.send(mpacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					android.util.Log.i("inside write", "exception during send mpacket");
				} 
                // Share the sent message back to the UI Activity
//                mHandler.obtainMessage(BroadcastChat.MESSAGE_WRITE, -1, -1, buffer)
//                        .sendToTarget();

            
        }
    
    private class ComThread1 extends Thread {
        
    	


    	
		//InetAddress myLocalIP ;
    	String myLocalIP;
        public ComThread1() {
        	
        	try { 
        		   //myBcastIP 	= "192.168.1.255";
        		   myBcastIP = getIP.getBroadcast().toString();
        		   Log.log("my bcast ip : "+myBcastIP);
        		   
        		   myLocalIP 	= getIP.getLocalAddress().toString();
        		   //myLocalIP = broadip.getIPaddress().toString();
        		   Log.log("my local ip : "+myLocalIP);
        		   
        		   mSocket 		= new DatagramSocket(BCAST_PORT); 
        		   mSocket.setBroadcast(true); 
//        		   lSocket 		= new DatagramSocket(Lis_Port); 
//        		   lSocket.setBroadcast(true); 
        		   
        	     } catch (IOException e) { 
        	    	 Log.log("Could not make socket"); 
        	     } 
        }
        

        public void run() {
        	
    		try {
    			
    			byte[] buf = new byte[1024]; 
    			
    			//Listen on socket to receive messages 
    			
    			while (true) { 
	    		    mpacket = new DatagramPacket(buf, buf.length); 
	    		    mSocket.receive(mpacket); 
	    		    Log.log("the sender or destination IP is " + mpacket.getAddress().toString());
	    		    
	    		    InetAddress remoteIP = mpacket.getAddress();
	    			if(remoteIP.equals(myLocalIP))
	    				break;
	    			
	    			String s = new String(mpacket.getData(), 0, mpacket.getLength()); 
	    			Log.log("Received response " + s); 
	    			
	    			// Send the obtained bytes to the UI Activity

	    				mHandler.obtainMessage(BroadcastChat.MESSAGE_READ,-1,-1, s)
		    			.sendToTarget();  
	    			
	    		  //  if(!(packet.getAddress().toString().equals(myLocalIP))){
    			
    			

//	    			while(listening){
//	    			lpacket = new DatagramPacket(buf, buf.length); 
//	    			lSocket.receive(lpacket);
//	    		  //  }
//	    			
//	    			Log.log("lpacket " + lpacket.getData());
////	    			String IP = packet.getAddress().toString();
////	    			Toast.makeText(context.getApplicationContext(), IP, Toast.LENGTH_SHORT).show();
//	    			
//	    			
//	    			String l = lpacket.getData().toString();
//	    			String[] array = l.split("\\s+");
//	    			//if(array[0].equals("Listening")){
//	    			Log.log("Received listening packet" + array);
//	    			//String ip = array[1];
//	    			Log.log("packet from" + ip);
//	    			//}
//	    			try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//	    			}
	    			            
    			}
			} catch (IOException e) {
    			e.printStackTrace();
			}
    		
        }

        
        /**
          * Write broadcast packet.
          */
//        public void write(byte[] buffer) {
//        	Log.log("buffer is " + buffer);
//           
//
//            	String data = new String (buffer);
//
//                try {
//					mpacket = new DatagramPacket(data.getBytes(), data.length(), 
//							InetAddress.getByName(myBcastIP), BCAST_PORT);
//				} catch (UnknownHostException e) {
//					// TODO Auto-generated catch block
//					android.util.Log.i("inside write", "exception during create mpacket");
//				}
//                
//                try {
//					mSocket.send(mpacket);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					android.util.Log.i("inside write", "exception during send mpacket");
//				} 
//                // Share the sent message back to the UI Activity
//                mHandler.obtainMessage(BroadcastChat.MESSAGE_WRITE, -1, -1, buffer)
//                        .sendToTarget();
//
//            
//        }

//            while(listening){
//            //String data = packetdata.listeningPacket();
//            String data = "Listening" + space + myLocalIP;	
//            DatagramPacket lpacket;
//            try {
//				lpacket = new DatagramPacket(data.getBytes(), data.length(),
//						InetAddress.getByName(myBcastIP), Lis_Port);
//				lSocket.send(lpacket);
//				
//			} catch (UnknownHostException e) {
//				// TODO Auto-generated catch block
//				Log.log("problem happened when create packet");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				Log.log("problem happened when send packet");
//			}
//            try {
//				Thread.sleep(10000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//            }
        	
            


        /** 
         * Calculate the broadcast IP we need to send the packet along. 
         */ 
//        private InetAddress getBroadcastAddress() throws IOException {
//          WifiManager mWifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//          
//          WifiInfo info = mWifi.getConnectionInfo();
//  		  if(D)Log.d(TAG,"\n\nWiFi Status: " + info.toString());
//  		
//    	  // DhcpInfo  is a simple object for retrieving the results of a DHCP request
//          DhcpInfo dhcp = mWifi.getDhcpInfo(); 
//          if (dhcp == null) { 
//            Log.d(TAG, "Could not get dhcp info"); 
//            return null; 
//          } 
//       
//          
//          int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask; 
//          byte[] quads = new byte[4]; 
//          for (int k = 0; k < 4; k++) 
//            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
//          
//          // Returns the InetAddress corresponding to the array of bytes. 
//          return InetAddress.getByAddress(quads);  // The high order byte is quads[0].
//        }  
        
        

        
        public void cancel() {
            try {
                
                mSocket.close();
            } catch (Exception e) {
                Log.log("close() of connect socket failed");
            }
        }
    }    
}
