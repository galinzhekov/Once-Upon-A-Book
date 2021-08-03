package com.example.onceuponabook.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {
    @SerializedName("user_id")
    private int user_id;

    @SerializedName("name")
    private String name;

    @SerializedName("position_id")
    private int position_id;

    @SerializedName("position")
    private String position;

    @SerializedName("email")
    private String email;

    @SerializedName("wallet")
    private double wallet;

    @SerializedName("location")
    private String location;

    @SerializedName("age")
    private int age;

    public User(int user_id, String name, int position_id, String position, String email, double wallet, String location, int age) {
        this.user_id = user_id;
        this.name = name;
        this.position_id = position_id;
        this.position = position;
        this.email = email;
        this.wallet = wallet;
        this.location = location;
        this.age = age;
    }

    protected User(Parcel in) {
        user_id = in.readInt();
        name = in.readString();
        position_id = in.readInt();
        position = in.readString();
        email = in.readString();
        wallet = in.readDouble();
        location = in.readString();
        age = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
    }

    public int getPosition_id() {
        return position_id;
    }

    public String getPosition() {
        return position;
    }

    public String getEmail() {
        return email;
    }

    public double getWallet() {
        return wallet;
    }

    public String getLocation() {
        return location;
    }

    public int getAge() {
        return age;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(user_id);
        dest.writeString(name);
        dest.writeInt(position_id);
        dest.writeString(position);
        dest.writeString(email);
        dest.writeDouble(wallet);
        dest.writeString(location);
        dest.writeInt(age);
    }
}
