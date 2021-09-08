package com.example.onceuponabook;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.onceuponabook.models.Author;
import com.example.onceuponabook.models.Category;
import com.example.onceuponabook.models.ServerResponse;
import com.example.onceuponabook.util.ApiClient;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UploadBookActivity extends AppCompatActivity implements View.OnClickListener {
    private int pageNumber = 0;

    private String pdfFileName;
    public static final int FILE_PICKER_REQUEST_CODE = 1;
    private String pdfPath;
    private Toolbar toolbar;
    private  String encodedPDF, encodedImage, mFileExtension, mImageExtension;
    private EditText etPdfName, etImageName, etTitle, etPrice, etDescription;
    private ImageButton mBackArrow;
    private Button mAddNewBook, btnFilePicker, btnImagePicker;
    private Call<Void> call;
    private Call<ServerResponse> callServerResponse;
    private Call<List<Author>> callListAuthor;
    private Call<List<Category>> callListCategory;
    private int userId;
    private ApiInterface apiInterface;
    private static final String TAG = "UploadBook";
    private List<String> mAuthors, mListCategories;
    private List<Author> mAuthorsIds;
    private List<Category> mCategories;
    private AutoCompleteTextView actvAuthors;
    private Spinner dropdown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_book);
        toolbar = findViewById(R.id.toolbar);
        etPdfName = findViewById(R.id.etPdfName);
        etImageName = findViewById(R.id.etImageName);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);
        mAddNewBook = findViewById(R.id.btnAddNewBook);
        setSupportActionBar(toolbar);
        btnFilePicker = findViewById(R.id.buttonUploadFile);
        btnImagePicker = findViewById(R.id.buttonUploadImage);
        actvAuthors = findViewById(R.id.etNewBookAuthor);
        etTitle = findViewById(R.id.etNewBookTitle);
        etPrice = findViewById(R.id.etNewBookPrice);
        etDescription = findViewById(R.id.etNewBookDescription);
        btnImagePicker.setOnClickListener(this);
        mBackArrow.setOnClickListener(this);
        btnFilePicker.setOnClickListener(this);
        mAddNewBook.setOnClickListener(this);
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        mAuthors = new ArrayList<>();
        mListCategories = new ArrayList<>();
        dropdown = findViewById(R.id.spinnerSelectCategory);
        userId = getIntent().getIntExtra("userId", 10);
        Log.v(TAG, String.valueOf(userId));

        callListCategory = apiInterface.getCategories();
        callListCategory.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {

                mCategories = response.body();
                for (Category oCategory : mCategories) {
                    mListCategories.add(oCategory.getCategory());
                }
                String[] array = mListCategories.toArray(new String[0]);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(UploadBookActivity.this,
                        android.R.layout.simple_list_item_1, array);
                dropdown.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.v(TAG, t.toString());
            }
        });

        callListAuthor = apiInterface.getAuthors();
        callListAuthor.enqueue(new Callback<List<Author>>() {
            @Override
            public void onResponse(Call<List<Author>> call, Response<List<Author>> response) {

                mAuthorsIds = response.body();
                for (Author oAuthor : mAuthorsIds) {
                    mAuthors.add(oAuthor.getAuthor());
                }
                String[] array = mAuthors.toArray(new String[0]);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(UploadBookActivity.this,
                        android.R.layout.simple_list_item_1, array);
                actvAuthors.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Author>> call, Throwable t) {
                Log.v(TAG, t.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        MenuItem option = menu.findItem(R.id.option);
        option.setVisible(false);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(800);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(UploadBookActivity.this, DefaultActivity.class);
                intent.putExtra("search_query", query);
                startActivity(intent);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 1) {
                imagePicker();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Permission Denied",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void imagePicker() {
        Intent intent = new Intent(UploadBookActivity.this,
                FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setShowImages(true)
                .setShowVideos(false)
                .enableImageCapture(true)
                .setMaxSelection(1)
                .setSkipZeroSizeFiles(true)
                .build());
        startActivityForResult(intent, 101);
    }

    private void launchPicker() {
        Intent intent = new Intent(UploadBookActivity.this, FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setShowFiles(true)
                .setSingleChoiceMode(true)
                .setSuffixes("pdf")
                .setShowImages(false)
                .setShowVideos(false)
                .build());
        startActivityForResult(intent, 102);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && resultCode == RESULT_OK) {
            ArrayList<MediaFile> mediaFiles = data.getParcelableArrayListExtra(
                    FilePickerActivity.MEDIA_FILES
            );
            String path = mediaFiles.get(0).getUri().getPath();
            Uri uriPath = mediaFiles.get(0).getUri();
            String fileName = getFileName(uriPath);
            pdfPath = path;
            switch (requestCode) {
                case 102:
                    try {
                        InputStream inputStream = UploadBookActivity.this.getContentResolver().openInputStream(uriPath);
                        if (inputStream != null) {
                            byte[] pdfInBytes = new byte[inputStream.available()];
                            inputStream.read(pdfInBytes);
                            encodedPDF = Base64.encodeToString(pdfInBytes, Base64.DEFAULT);

                            etPdfName.setText(fileName);
                            mFileExtension = getfileExtension(uriPath);
                            Toast.makeText(UploadBookActivity.this, "Picked file: " + path, Toast.LENGTH_LONG).show();
                            Toast.makeText(this, "Document Selected", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Document NOT Selected", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                    case 101:
                    try {
                        InputStream inputStream = UploadBookActivity.this.getContentResolver().openInputStream(uriPath);
                        if (inputStream != null) {
                            byte[] pdfInBytes = new byte[inputStream.available()];
                            inputStream.read(pdfInBytes);
                            encodedImage = Base64.encodeToString(pdfInBytes, Base64.DEFAULT);

                            mImageExtension = getfileExtension(uriPath);
                            etImageName.setText(fileName);
                            Toast.makeText(UploadBookActivity.this, "Picked file: " + path, Toast.LENGTH_LONG).show();
                            Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Image NOT Selected", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private void uploadDocument() {
        callServerResponse = apiInterface.addBook(
                ApiClient.PASSWORD,
                "add_book",
                ApiClient.BASE_URL,
                userId,
                encodedPDF,
                String.valueOf(etPdfName.getText()),
                mFileExtension,
                encodedImage,
                String.valueOf(etImageName.getText()),
                mImageExtension,
                String.valueOf(etTitle.getText()),
                String.valueOf(actvAuthors.getText()),
                String.valueOf(dropdown.getSelectedItem()),
                Double.parseDouble(String.valueOf(etPrice.getText())),
                String.valueOf(etDescription.getText())
                );
        callServerResponse.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
                if (serverResponse.getMessage() != null) {
                    Toast.makeText(UploadBookActivity.this, serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(UploadBookActivity.this, "The book wasn't uploaded.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.v(TAG, String.valueOf(t));
                Toast.makeText(UploadBookActivity.this,"Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getfileExtension(Uri uri) {
        String extension;
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension= mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back_arrow: {
                Intent intent4 = new Intent(UploadBookActivity.this, StartActivity.class);
                startActivity(intent4);
                break;
            }
            case R.id.buttonUploadFile: {
                launchPicker();
                break;
            }
            case R.id.buttonUploadImage: {
                if (ContextCompat.checkSelfPermission(UploadBookActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UploadBookActivity.this,
                            new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    imagePicker();
                }
                break;
            }
            case R.id.btnAddNewBook: {
                uploadDocument();
                break;
            }
        }
    }
}