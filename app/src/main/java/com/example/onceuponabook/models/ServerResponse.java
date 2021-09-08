package com.example.onceuponabook.models;

import com.google.gson.annotations.SerializedName;

public class ServerResponse {

    @SerializedName("message")
    String message;

    public String getMessage() {
        return message;
    }

}
