package com.example.onceuponabook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;

import com.example.onceuponabook.models.Book;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PdfActivity extends AppCompatActivity {
    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        pdfView = findViewById(R.id.idPDFView);
        Book book = getIntent().getParcelableExtra("selected_book");
        String pdfUrl = book.getProduct();
        new RetrivePDFfromUrl(pdfView).execute(pdfUrl);
    }
}