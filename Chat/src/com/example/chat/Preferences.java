package com.example.chat;
/*
 * Preferences.java
 *
 * Created on February 7, 2007
 *
 *@author Mashael Al-Sabah
 */

/**
 *Configurations and preferences.
 */
public class Preferences {   
    private Preferences(){}
    //HostIP can either be an IP in String format, or "null", if it is null, the bootstrapper will choose an interface to announce
    public static final String  HostIP= "null"; 
    //The port on which the File Server operates 
    public static final int FileServerPort = 5555;
    // The port on which the bootstrapper operates
    public static final int BootStrapperPort = 5353;
    // The port on which the Client and server operate
    public static final int ConnManagerport = 4444;
    //The multicast group used for bootstrapping
    public static final String multicastAddress =  "224.0.0.251";
    //The file database size
    public static final int FileDBsize = 100;
    //The duration of time between sending a Join multicast requests, specified in milliseconds
    public static final long MulticastTimer = 5000;
    //Join packet TTL, used if propagating multicast packets is required.
    public static final int JoinTTL = 1;
    //The overlay packet size in bytes, 40 bytes must be allowed for header
    public static final int OverlayPacketSize = 1024;
    //The size of the Query Cache
    public static final int CacheSize = 10;  
    //The duration of time between sending an LCS request and the next, specified in milliseconds
    public static final int LCSTimer = 30000;
    //The duration of time before the first execution of the LCSTimer object when a new neighbor si created
    public static final int LCSTimerOffset = 10000;
    //The duration of time between the same query retransmissions if a reply is not recieved,specified in milliseconds
    public static final int QueryTimer = 70000;
    //The RTT window size over which the average RTT is measured. 0 is a bad value 
    public static final int RTTwindowSize = 5;
    //Configure whether the multicast requests are to propagate or not
    public static final String PropagateMulticastRequests = "NO"; //nodes are set not to propagate the multicast requests
    //File Server is set to work when the Node object joins.
    public static final String StartFileServer = "YES";
    //The capacity setting used in MBR, can be changed through Node.UpdateCapacity();
    public static int Capacity = 1;
    //The power setting used in link coloring scheme, and can be changed through Node.UpdatePower();
    public static int Power = 1;   
    public static int BoardcastPort = 11111;
    public static int ListeningPort = 11112;
    public static int ForwardPort   = 11113;
}
