package com.example.chat;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import android.os.Build.VERSION;


public class getIP {

	public static String getBroadcast(){
	    String found_bcast_address=null;
	     System.setProperty("java.net.preferIPv4Stack", "true"); 
	        try
	        {
	          Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces();
	          while (niEnum.hasMoreElements())
	          {
	            NetworkInterface ni = niEnum.nextElement();
	            if(!ni.isLoopback()){
	                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses())
	                {

	                  found_bcast_address = interfaceAddress.getBroadcast().toString();
	                  found_bcast_address = found_bcast_address.substring(1);

	                }
	            }
	          }
	        }
	        catch (SocketException e)
	        {
	          e.printStackTrace();
	        }

	        return found_bcast_address;
	}

    public static String getLocalAddress()throws IOException {
    	
//		try {
//		    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
//		        NetworkInterface intf = en.nextElement();
//		        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
//		            InetAddress inetAddress = enumIpAddr.nextElement();
//		            if (!inetAddress.isSiteLocalAddress()) {
//		                //return inetAddress.getHostAddress().toString();
//		            	return inetAddress;
//		            }
//		        }
//		    }
//		} catch (SocketException ex) {
//		    Log.log(ex.toString());
//		}
    	String IP = "";
        try{
            
            	 //IP  = InetAddress.getLocalHost().getHostAddress();
            	
            	  try {
            	   Enumeration<NetworkInterface> enumNetworkInterfaces = 
            	     NetworkInterface.getNetworkInterfaces();
            	   while(enumNetworkInterfaces.hasMoreElements()){
            	    NetworkInterface networkInterface = 
            	      enumNetworkInterfaces.nextElement();
            	    Enumeration<InetAddress> enumInetAddress = 
            	      networkInterface.getInetAddresses();
            	    while(enumInetAddress.hasMoreElements()){
            	     InetAddress inetAddress = enumInetAddress.nextElement();
            	     
            	     if(inetAddress.isSiteLocalAddress()){
            	      IP = inetAddress.getHostAddress(); 
            	      break;
            	     }
            	     
            	     
            	    }
            	    
            	   }
            	   
            	  } catch (SocketException e) {
            	   // TODO Auto-generated catch block
            	   e.printStackTrace();
            	   IP += "Something Wrong! " + e.toString() + "\n";
            	  }
            

            
        }catch (Exception e){
        	e.printStackTrace();
        }
        
        return IP;
    
		//return null;
    }
    
}
