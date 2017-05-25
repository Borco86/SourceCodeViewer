package com.tomaszborejko.sourcecodeviewer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.tomaszborejko.sourcecodeviewer.database.SourceCodesDatabaseOpenHelper;
import com.tomaszborejko.sourcecodeviewer.database.SourceCodesTableContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public static final String TEXT_VIEW_STATE = "TEXT_VIEW_STATE";
    @BindView(R.id.url_text_input_edit_text)
    TextInputEditText userInputEditText;
    @BindView(R.id.source_code_text_view)
    TextView sourceCodeTextView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private SourceCodesDatabaseOpenHelper sourceCodesDatabaseOpenHelper = new SourceCodesDatabaseOpenHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.source_code_download_button)
    void onDownloadButtonClick() {
        if (isOnline()) {
            if (isValidUrl()) {
                progressBar.setVisibility(View.VISIBLE);
                Ion.with(this)
                        .load(getUserInputUri())
                        .progressBar(progressBar)
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {
                                progressBar.setVisibility(View.GONE);
                                if (result != null) {

                                    sourceCodeTextView.setText(result);
                                    saveSourceCodeIntoDatabase();
                                    Toast.makeText(MainActivity.this, "Source Code loaded successfully", Toast.LENGTH_SHORT).show();
                                } else {

                                    Toast.makeText(MainActivity.this, "Page does not exist / not responding", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            } else {
                Toast.makeText(this, "Invalid URL address ", Toast.LENGTH_LONG).show();
            }
        } else {
            Cursor cursor = searchQuery(getUserInputUri(), sourceCodesDatabaseOpenHelper.getReadableDatabase());
            if(cursor!=null && cursor.getCount()>0) {

                cursor.moveToNext();
                String result = cursor.getString(1);
                sourceCodeTextView.setText(result);
                cursor.close();
                Toast.makeText(this, "Source code loaded from database", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
            }

        }

    }

    private void saveSourceCodeIntoDatabase() {
        SourceCodeSingleRecord singleRecord = new SourceCodeSingleRecord(getUserInputUri(), getTextViewContent());
        ContentValues contentValues = new ContentValues();
        contentValues.put(SourceCodesTableContract.COLUMN_WEBSITE_URL, singleRecord.getWebsiteUrl());
        contentValues.put(SourceCodesTableContract.COLUMN_SOURCE_CODE, singleRecord.getSourceCode());
        sourceCodesDatabaseOpenHelper.getWritableDatabase()
                .insert(SourceCodesTableContract.TABLE_NAME, null, contentValues);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TEXT_VIEW_STATE, getTextViewContent());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        sourceCodeTextView.setText(savedInstanceState.getString(TEXT_VIEW_STATE));
    }

    @NonNull
    private String getTextViewContent() {
        return sourceCodeTextView.getText().toString();
    }

    public String getUserInputUri() {
        return userInputEditText.getText().toString().toLowerCase().replaceAll("\\s+", "");
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public boolean isValidUrl() {
        return Patterns.WEB_URL.matcher(getUserInputUri()).matches();
    }

    public Cursor searchQuery(String userInputUrl, SQLiteDatabase db) {
        Cursor cursor = db.query(SourceCodesTableContract.TABLE_NAME,
                null
                , SourceCodesTableContract.COLUMN_WEBSITE_URL + " = ?", new String[]{
                        userInputUrl
                }, null, null, null);
        return cursor;
    }

}
