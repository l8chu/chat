package com.example.chat;

import android.os.Parcel;
import android.os.Parcelable;

public class List implements Parcelable {
	String data1,data2;
	String data;
	public List(){}
	public List(Parcel in) {
		data = in.readString();
    }
	public String getData() {
	    return data;
	}
	public void setData(String data) {
	    this.data = data;
	}
	public static final Parcelable.Creator<List> CREATOR = 
		    new Parcelable.Creator<List>() {

		        @Override
		        public List createFromParcel(Parcel source) {
		            return new List(source);
		        }

		        @Override
		        public List[] newArray(int size) {
		            return new List[size];
		        }
		    };
	public void readFromParcel(Parcel in) {
		 data = in.readString();

       }
	 
	    
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(data);
	}


}
