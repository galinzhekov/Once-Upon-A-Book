package com.example.onceuponabook;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

public class OpenPDFActivity extends AppCompatActivity {
    private WebView mWebView;
    private PdfRenderer renderer;
    private PdfRenderer.Page currentPage;
    private ImageView ivPdf;
    private Button btnPrevPage, btnNextPage;
    private ParcelFileDescriptor parcelFileDescriptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_p_d_f);

//        mWebView = findViewById(R.id.wvPDF);
//        mWebView = new WebViewClient();
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.loadUrl("http://192.168.1.6/library/files/Lorem.pdf");

        ivPdf = findViewById(R.id.ivPdfFile);
        btnPrevPage = findViewById(R.id.btnPrevPage);
        btnNextPage = findViewById(R.id.btnNextPage);

        View.OnClickListener clickListener = (v -> {
           if (renderer != null && currentPage != null) {
               if (v == btnNextPage) {
                   renderPage(currentPage.getIndex() + 1);
               }
               else if (v == btnPrevPage) {
                   renderPage(currentPage.getIndex() - 1);
               }
           }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initRenderer();
        renderPage(0);
    }

    private void initRenderer() {
    }

    private void renderPage(int pageIndex) {
        if (currentPage != null) {
            currentPage.close();
        }

        currentPage = renderer.openPage(pageIndex);
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        ivPdf.setImageBitmap(bitmap);
    }
}