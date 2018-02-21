package com.example.david.newsapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static String SOURCE_KEY = "SOURCE";
    ArrayList<Source> sourceList;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sourceList = new ArrayList<Source>();


        if (isConnected()) {
            new GetDataAsync().execute("https://newsapi.org/v1/sources");
        }
        else {
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
        }


        pd = new ProgressDialog(this);
        pd.setMessage("Loading Sources");
        pd.show();

    }



    public void sendData(ArrayList<Source> sources) {

        sourceList = sources;
        pd.dismiss();
        Log.d("demo", "Sourcelist size" + sourceList.size());
        for (int i = 0; i < sourceList.size(); i++)
            Log.d("demo", "Firstname: " + sourceList.get(i).getName());

    }


    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    private class GetDataAsync extends AsyncTask<String, Void, ArrayList<Source>> {
        @Override
        protected ArrayList<Source> doInBackground(String... params) {
            HttpURLConnection connection = null;
            ArrayList<Source> result = new ArrayList<>();
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF-8");

                    JSONObject root = new JSONObject(json);
                    JSONArray sources = root.getJSONArray("sources");

                    for (int i = 0; i < sources.length(); i++) {
                        JSONObject sourceJson = sources.getJSONObject(i);
                        Source source = new Source();
                        source.setId(sourceJson.getString("id"));
                        source.setName(sourceJson.getString("name"));

                        result.add(source);
                    }

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Source> result) {
            if (result != null) {
                sourceList = result;

                LinearLayout parentLayout = (LinearLayout)findViewById(R.id.layout);
                LayoutInflater layoutInflater = getLayoutInflater();
                View view;

                for (int i = 0; i < sourceList.size(); i++) {
                    // Add the text layout to the parent layout
                    view = layoutInflater.inflate(R.layout.text_layout, parentLayout, false);

                    // In order to get the view we have to use the new view with text_layout in it
                    TextView textView = (TextView) view.findViewById(R.id.text);
                    textView.setText(sourceList.get(i).getName());

                    // Add the text view to the parent layout
                    parentLayout.addView(textView);

                    final Source source = sourceList.get(i);

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(MainActivity.this, NewsActivity.class);

                            i.putExtra(SOURCE_KEY, source);
                            startActivity(i);
                        }
                    });

                    }
                }

                Log.d("demo", result.toString());
                sendData(result);

            }


        }


    }


/*
    public void onClick(View v) {
        //Toast.makeText(this, "Its Clicking...!", Toast.LENGTH_SHORT).show();



        TextView textView = findViewById(v.getId());
        String name = textView.getText().toString();
        Source source = null;

        for (int i =0; i < sourceList.size(); i++) {
            if (name == sourceList.get(i).getName()) {
                source = sourceList.get(i);
            }
        }

        Intent i = new Intent(MainActivity.this, NewsActivity.class);
        i.putExtra(SOURCE_KEY, source);
        startActivity(i);

    }*/


// End MainActivity




