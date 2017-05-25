package com.tomaszborejko.sourcecodeviewer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.source_code_download_button)
    void onDownloadButtonClick() {
        String uri = userInputEditText.getText().toString().toLowerCase();
        if(isOnline()) {
            //  if (Patterns.WEB_URL.matcher(uri).matches()) {
            Ion.with(this).load(uri).asString().setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String result) {
                    sourceCodeTextView.setText(result);
                    Toast.makeText(MainActivity.this, "Source Code loaded successfully", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
        //}else{
       //     Toast.makeText(this, "Invalid URL address ", Toast.LENGTH_LONG).show();
       // }

    }
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_activity_option_save) {
            //TODO Save current into database
        } else if (item.getItemId() == R.id.main_activity_option_browse) {
            //TODO Browse saved source codes
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TEXT_VIEW_STATE, sourceCodeTextView.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        sourceCodeTextView.setText(savedInstanceState.getString(TEXT_VIEW_STATE));
    }
}
