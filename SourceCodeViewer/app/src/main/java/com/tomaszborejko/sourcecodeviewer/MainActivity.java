package com.tomaszborejko.sourcecodeviewer;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.tomaszborejko.sourcecodeviewer.database.SourceCodesDatabaseOpenHelper;
import com.tomaszborejko.sourcecodeviewer.database.SourceCodesTableContract;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public static final String TEXT_VIEW_STATE = "TEXT_VIEW_STATE";
    @BindView(R.id.url_text_input_edit_text)
    TextInputEditText userInputEditText;
    @BindView(R.id.source_code_download_button)
    Button downloadButton;
    //TODO Delete later, if not needed
    @BindView(R.id.source_code_text_view)
    TextView sourceCodeTextView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private SourceCodesDatabaseOpenHelper sourceCodesDatabaseOpenHelper;

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
                                if (result != null) {
                                    progressBar.setVisibility(View.GONE);
                                    sourceCodeTextView.setText(result);
                                    Toast.makeText(MainActivity.this, "Source Code loaded successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, "Page does not exist / not responding", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            } else {
                Toast.makeText(this, "Invalid URL address ", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_activity_option_save) {
            SourceCodeSingleRecord singleRecord = new SourceCodeSingleRecord(getUserInputUri(),getTextViewContent());
            ContentValues contentValues = new ContentValues();
            contentValues.put(SourceCodesTableContract.COLUMN_WEBSITE_URL, singleRecord.getWebsiteUrl());
            contentValues.put(SourceCodesTableContract.COLUMN_SOURCE_CODE, singleRecord.getSourceCode());
            sourceCodesDatabaseOpenHelper.getWritableDatabase()
                    .insert(SourceCodesTableContract.TABLE_NAME, null, contentValues);

        } else if (item.getItemId() == R.id.main_activity_option_browse) {
            Intent intent = new Intent(this, SourceCodesListActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
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
}
