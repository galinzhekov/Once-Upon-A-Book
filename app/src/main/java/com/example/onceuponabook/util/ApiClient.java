package com.example.onceuponabook.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "http://192.168.1.6/library/";
    public static final String PASSWORD = "123";
    public static Retrofit retrofit = null;
    private static final String TAG = "MyActivity";

    public static Retrofit getApiClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60,TimeUnit.SECONDS).build();

        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("YYYY-MM-dd")
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create(gson)).build();
        }

        return retrofit;
    }
}
