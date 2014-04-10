package com.example.chat;

import java.io.Serializable;
import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class ListParcelable implements Parcelable{
	private ArrayList<List> arrList = new ArrayList<List>();

	private String data = null;
	
	public String getStr() {
	    return data;
	}
	public void setStr(String data) {
	    this.data = data;
	}
	public ArrayList<List> getArrList() {
	    return arrList;
	}
	public void setArrList(ArrayList<List> arrList) {
	    this.arrList = arrList;
	}
	ListParcelable() {
	    // initialization
	    arrList = new ArrayList<List>();
	}
	public ListParcelable(Parcel in) {
	    data = in.readString();
	    in.readTypedList(arrList, List.CREATOR);
	}
	public int getSize(){
		return arrList.size();
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeString(data);
		arg0.writeTypedList(arrList);
	}
	public static final Parcelable.Creator<ListParcelable> CREATOR = new Parcelable.Creator<ListParcelable>() {

	    @Override
	    public ListParcelable createFromParcel(Parcel in) {
	        return new ListParcelable(in);
	    }

	    @Override
	    public ListParcelable[] newArray(int size) {
	        return new ListParcelable[size];
	    }
	};
}
