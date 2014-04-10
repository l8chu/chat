
package com.example.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Handler;
//import android.util.Log;

/**
 * This class does all the work for sending and receiving broadcast packets. 
 * It has a thread that listens for incoming packets.
 */
public class newMsgForwardService {

    // Member fields
	private static String TAG = "MsgForwardService";
	private static String TAG1 = "fthread";
    private final Handler fHandler;
    private int LISTENING_PORT1 = 11200; //PORTS
    private int UNI_PORT = 11201; //PORTS
    private fThread fConnectedThread;
    private String ip;
    private DatagramPacket fpacket;
    private getIP broadip;
    private boolean listening = true;
    private String space = " ";
    private String myBcastIP;
    private String myLocalIP;
    
    Context fContext ;
    String SourceIP;
    String DestIP;
    String MsgType; //RC = Request Chat //RA = Request ACK //CM = Chat Message
    String MsgReceived;
    InetAddress remoteIP;
    
    byte newfpacket[] = new byte[1024];
    
    /**
     * Constructor. Prepares a new Broadcast service.
     * @param context  The UI Activity Context
     * @param handler  A Handler to send messages back to the UI Activity
     */

    public newMsgForwardService(Context context1, Handler handler1) {
        //mAdapter = BluetoothAdapter.getDefaultAdapter();
    	fContext = context1;
        fHandler = handler1;

    }

    /**
     * Start the chat service. Specifically start ComThread to begin 
     * listening incoming broadcast packets. 
     */
    public synchronized void start() {
         Log.log("start");
        android.util.Log.i(TAG,"in Start()");
        fConnectedThread = new fThread();
        fConnectedThread.start();
        android.util.Log.i(TAG,"Started thread");
    }


    /**
     * Stop thread
     */
    public synchronized void stop() {
         Log.log("stop");
        if (fConnectedThread != null) {fConnectedThread.cancel(); fConnectedThread = null;}
    }


    public void write(byte[] out,String DestIP) {
    	
    	android.util.Log.i(TAG,"Got message: " + new String(out));
    	//fsthread.unicastWrite(out, DestIP);
        fConnectedThread.unicastWrite(out,DestIP);
    }

    /**
     * Thread for receiving
     */    
    private class fThread extends Thread {
        
    	private static final String TAG1 = "fthread";
		//private int FOR_PORT = Preferences.ForwardPort;
		//private int UNI_PORT = 11103;
    	DatagramSocket fSocket ;
    	DatagramSocket uniSocket;
    	//String myBcastIP;
		//InetAddress myLocalIP ;
    	//String myLocalIP;
        public fThread() {
        	
        	try { 
        		   //myBcastIP 	= "192.168.1.255";
        		   myBcastIP = getIP.getBroadcast().toString();
        		   android.util.Log.i(TAG1,"my bcast ip : "+myBcastIP);
        		   
        		   myLocalIP 	= getIP.getLocalAddress().toString();
        		   //myLocalIP = broadip.getIPaddress().toString();
        		   android.util.Log.i(TAG1,"my local ip : "+myLocalIP);
        		   
        		   fSocket 		= new DatagramSocket(LISTENING_PORT1); 
        		   fSocket.setBroadcast(false);
        		   
        	     } catch (IOException e) { 
        	    	 android.util.Log.i(TAG1,"could not make socket");
        	    	 
        	    	 Log.log("Could not make socket"); 
        	     } 
        }
        

        public void run() {
    		try {

    			//Listen on socket to receive messages 
    			
    			while (true) { 
    				android.util.Log.i(TAG1,"point1");
    				byte[] buf = new byte[1024]; 
    				fpacket = new DatagramPacket(buf, buf.length);
    				android.util.Log.i(TAG1,"point2");
	    		    fSocket.receive(fpacket); 
	    		    android.util.Log.i(TAG1,"point3");
	    		    android.util.Log.i(TAG1,"packet coming from:" + fpacket.getAddress().toString());
	    		    
	    		    remoteIP = fpacket.getAddress();
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
	    			android.util.Log.i(TAG1,"DestIP:"+DestIP);
	    			android.util.Log.i(TAG1,"MsgType: "+MsgType);
	    			if ((myLocalIP.trim()).equals(DestIP.trim())){	
	    				android.util.Log.i(TAG1,"myLocalIP = DestIP");
	    				if (MsgType.equalsIgnoreCase("RC")){
	    				fHandler.obtainMessage(OnlineUsers.REQUEST_CHAT, SourceIP)
		    			.sendToTarget();
    					Log.log("Accepting [RC] pactet from:"+ remoteIP.getHostAddress()+
    							"packet contents:["+s+"]");
	    				android.util.Log.i(TAG1,"point 5");
	    				}
	    				else if (MsgType.equalsIgnoreCase("RA")){
	    					android.util.Log.i(TAG1,"RA message point 6");
	    					fHandler.obtainMessage(OnlineUsers.REQUEST_ACK, SourceIP)
			    			.sendToTarget();
	    					Log.log("Accepting [RA] pactet from:"+ remoteIP.getHostAddress()+
	    							"packet contents:["+s+"]");
	    				}
	    				else if (MsgType.equalsIgnoreCase("CM")){
	    					fHandler.obtainMessage(OnlineUsers.CHAT_MESSAGE, MsgReceived)
			    			.sendToTarget();
	    					Log.log("Accepting [CM] pactet from:"+ remoteIP.getHostAddress()+
	    							"packet contents:["+s+"]");
	    					Log.log("What they say: "+MsgReceived);
	    				}
	    			}
	    			else if(!((myLocalIP.trim()).equals(DestIP.trim()))){ // FORWARD MESSAGE TO DEST IP
	    				if (MsgType.equalsIgnoreCase("RC")){
	    					newMessageFwd.AddtoQueue(s);
	    					Log.log("Forwarding [RC] packet from:"+ remoteIP.getHostAddress()+
	    							"packet contents:["+s+"]");
	    				}
	    				else if (MsgType.equalsIgnoreCase("RA")){
	    					Log.log("Forwarding [RA] packet from:"+ remoteIP.getHostAddress()+
	    							"packet contents:["+s+"]");
	    					newMessageFwd.AddtoQueue(s);
	    				}
	    				else if (MsgType.equalsIgnoreCase("CM")){
	    					Log.log("Forwarding [CM] packet from:"+ remoteIP.getHostAddress()+
	    							"packet contents:["+s+"]");
	    					newMessageFwd.AddtoQueue(s);
	    				}
	    			}
    			}
        }catch (Exception e){System.out.println("Exception!: "+e.getMessage());}
        }
        
        private void unicastWrite(byte[] newfpacket,String DestIP1) {
        	try {
            	String data = new String (newfpacket);
            	DatagramPacket toForward;
            	android.util.Log.i(TAG1,"inunicastWrite()");
            	android.util.Log.i(TAG1,"inunicastWrite(): DestIP1:  "+ DestIP1);
            	
					toForward = new DatagramPacket(data.getBytes(), data.length(), 
							InetAddress.getByName(DestIP1.trim()), LISTENING_PORT1);
					android.util.Log.i(TAG1,"inunicastWrite():did i send point 1? "+ DestIP1);
					uniSocket.setBroadcast(false);
					uniSocket.send(toForward);
					android.util.Log.i(TAG1,"inunicastWrite():did i send point 2 ? "+ DestIP1);
        	    }
				catch (UnknownHostException e1) {
					android.util.Log.i(TAG1,"Unknown host exception: "+e1.getMessage());
					e1.printStackTrace();
				 } catch (IOException e) {
					android.util.Log.i(TAG1,"Some IO exception: "+e.getMessage());
					e.printStackTrace();
				} catch (Exception e){android.util.Log.i(TAG1,"some exception");}             
		}

		private boolean ProcessPacket(String pckt) {
			
        	android.util.Log.i(TAG1,"in process packet");
        	boolean successful = true;
        	int t=0;
        	boolean unsuccessful = false;
        	//int ind = pckt.indexOf(' ');
            StringTokenizer st = new StringTokenizer(pckt," ");
            String[] tokens = new String[st.countTokens()];
          
            while (st.hasMoreTokens()){
            	tokens[t++] = st.nextToken();
            }
           // MsgReceived = new String[tokens.length - 2];
            StringBuffer tempholder = new StringBuffer();
            MsgReceived = "";
            SourceIP = tokens[0].trim();
			DestIP= tokens[1].trim();
			MsgType = tokens[2].trim();
			
			if (MsgType.equals("RC")){ //Request chat
				//DealWithRCType();
				
			}
			else if (MsgType.equals("RA")){ //Request ACKnowledgement
				
			}
			else if (MsgType.equals("CM")){//Chat message
			android.util.Log.i(TAG1, "length of tokens[]:"+tokens.length);
			for (int i =3;i<tokens.length;i++){
				tempholder.append(tokens[i] + " ");
				//MsgReceived += tokens[i]+ " ";
			}
			}
			MsgReceived = tempholder.toString();
			
			android.util.Log.i(TAG1,"in process packet SourceIP:"+ SourceIP);
			android.util.Log.i(TAG1,"in process packet destip:"+DestIP);
			android.util.Log.i(TAG1,"in process packet msg:"+ MsgReceived);
			
			if (isValidIpAddress(DestIP)){ 
				android.util.Log.i(TAG1,"packet successful");
				return successful;
			}
			return unsuccessful;
		}
		/**
         * Write broadcast packet.
      
         *//*
       public void write(byte[] buffer) {

           try {
           	String data = new String (buffer);
           	android.util.Log.i(TAG1,"inwrite()");
           	fpacket = new DatagramPacket(data.getBytes(), data.length(), 
               		InetAddress.getByName(myBcastIP), FOR_PORT);
           	
           	android.util.Log.i(TAG1,"inwrite() point 1");
               fSocket.send(fpacket); 
               android.util.Log.i(TAG1,"inwrite() data:"+fpacket.getData().toString());
               android.util.Log.i(TAG1,"inwrite() point 2");
               String onlymsg = getonlymsg(buffer);
               android.util.Log.i(TAG1, onlymsg);
               // Share the sent message back to the UI Activity
               android.util.Log.i(TAG1,"sending write to handler:"+onlymsg);
         // fHandler.obtainMessage(MsgForward.MESSAGE_WRITE1, -1, -1, onlymsg)
           //            .sendToTarget();
           } catch (Exception e) {
               Log.log("Exception during write");
           }            
       }
       */
        //needs to be reworked
        private String getonlymsg(byte[] buffer) {
			String onlymsg;
			String ipplusmsg = new String(buffer);
			android.util.Log.i(TAG1,"do i get here");
			int ind = ipplusmsg.indexOf(' ');
			onlymsg = ipplusmsg.substring(ind+1,ipplusmsg.length());
			return onlymsg;
		}


		public boolean isValidIpAddress(String addr) {
        	android.util.Log.i(TAG1,"inisValidIpAddress()");
        	android.util.Log.i(TAG1,"inisValidIpAddress addr:"+addr);
    		boolean valid = true;
    		boolean invalid = false;
    		String [] IPlist = {"192.168.1.101","192.168.1.102","192.168.1.105"};
    		if (addr!= null){
    		for (int i =0;i<IPlist.length;i++){
    			if (addr.trim().equals(IPlist[i])) return valid;
    		}
			
    		}
    		return invalid;
    	}
    /*  
        public boolean isValidIpAddress(String addr) {
    		boolean valid = true;    	       
    		try {
        		String[] octets = addr.split("\\.");
        		valid = (octets.length == 4);
    			for (int i = 0; (i < 4) && valid; i++) {
    				int val = Integer.parseInt(octets[i]);
    				if (val < 0 || val > 255) {
    					valid = false;
    				}
    			}
    		} catch (Exception e) {
    			valid = false;
    		}
    		
    		if (!valid) {
    		}
    		return valid;
    	}*/
        public void cancel() {
            try {
                
                fSocket.close();
            } catch (Exception e) {
                Log.log("close() of connect socket failed");
            }
        }
    }    
}
