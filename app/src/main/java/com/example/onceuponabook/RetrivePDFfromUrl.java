package com.example.onceuponabook;

import android.os.AsyncTask;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// create an async task class for loading pdf file from URL.
public class RetrivePDFfromUrl extends AsyncTask<String, Void, InputStream> {
    PDFView mPdfView;

    public RetrivePDFfromUrl(PDFView mPdfView) {
        this.mPdfView = mPdfView;
    }

    @Override
    protected InputStream doInBackground(String... strings) {
        InputStream inputStream = null;
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return inputStream;
    }

    @Override
    protected void onPostExecute(InputStream inputStream) {
        mPdfView.fromStream(inputStream).load();
    }
}
