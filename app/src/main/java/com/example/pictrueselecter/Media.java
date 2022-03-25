package com.example.pictrueselecter;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Media implements Comparable<Media>, Parcelable {
    String path;
    String name;
    String time;//时间戳
    String date;//日期
    boolean hasLocation;//地址
    boolean isSelected;
    boolean isDate;//是日期条目
    Uri uri;

    public Media(String path, String name, String time, String date, boolean hasLocation, boolean isSelected) {
        this.path = path;
        this.name = name;
        this.time = time;
        this.date = date;
        this.isSelected = isSelected;
        this.hasLocation = hasLocation;
    }

    public Media(String date, boolean isSelected, boolean isDate) {
        this.date = date;
        this.isSelected = isSelected;
        this.isDate = isDate;
    }

    @Override
    public int compareTo(Media o) {
        return Integer.parseInt(o.time) - Integer.parseInt(this.time);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.name);
        dest.writeString(this.time);
        dest.writeString(this.date);
        dest.writeByte(this.hasLocation ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isDate ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.path = source.readString();
        this.name = source.readString();
        this.time = source.readString();
        this.date = source.readString();
        this.hasLocation = source.readByte() != 0;
        this.isSelected = source.readByte() != 0;
        this.isDate = source.readByte() != 0;
    }

    protected Media(Parcel in) {
        this.path = in.readString();
        this.name = in.readString();
        this.time = in.readString();
        this.date = in.readString();
        this.hasLocation = in.readByte() != 0;
        this.isSelected = in.readByte() != 0;
        this.isDate = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Media> CREATOR = new Parcelable.Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
}
