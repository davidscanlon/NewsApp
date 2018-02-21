package com.example.david.newsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class NewsActivity extends AppCompatActivity {


    Source source = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        if (getIntent() != null && getIntent().getExtras() != null) {
            source = getIntent().getExtras().getParcelable(MainActivity.SOURCE_KEY);
        }

        Toast.makeText(this, source.getName(), Toast.LENGTH_SHORT).show();
    }




}
