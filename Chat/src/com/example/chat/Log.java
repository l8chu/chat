/*
 * Created on Apr 9, 2007
 *
 * @author Mashael Al-Sabah
 */
package com.example.chat;
import java.io.*;

import android.os.Environment;

public class Log {

		static File root = new File(Environment.getExternalStorageDirectory(),"Log");
        static File file1;
          static File file;
          static File contact;
          
          static FileOutputStream FOS1;
          static FileOutputStream FOS;
          static FileOutputStream contactlist;
 	public Log(){  }
	public static  void create(){
		try{
			if(!root.exists()){
				root.mkdir();
			}
			file= new File(root, "logfile.txt"); 
			if(!file.exists()){
						file.createNewFile();
			}
			file1= new File(root, "chatlog.txt");
			if(!file1.exists()){
				file1.createNewFile();
			}
			contact= new File(root, "contact.txt"); 
			if(!contact.exists()){
				contact.createNewFile();
			}
                        FOS = new FileOutputStream(file);                       
                        FOS1 = new FileOutputStream(file1);
                        contactlist = new FileOutputStream(contact);
		}catch(Exception e){}
	}
	
        
	public static  void log(String s){
		try{
                        FOS.write('\n');
			FOS.write(s.getBytes());
			FOS.flush();
		
		}catch(Exception e){}
	}
        
        
	public  static void chat(String s1){
		try{
                        FOS1.write('\n');
			FOS1.write(s1.getBytes());
		
		}catch(Exception e){}
	}
	public  static void close(){
            try{
                FOS.close();
                FOS1.close();
            }catch(Exception e){}
        }
	public static  void list(String s){
		try{
			contactlist.write('\n');
			contactlist.write(s.getBytes());
			contactlist.flush();
		
		}catch(Exception e){}
	}
}
