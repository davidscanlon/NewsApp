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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {

    ArrayList<Article> articleList;
    Source source = null;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);


        if (getIntent() != null && getIntent().getExtras() != null) {
            source = getIntent().getExtras().getParcelable(MainActivity.SOURCE_KEY);
        }

        setTitle(source.getName());

        if (isConnected()) {
            new NewsActivity.GetDataAsync().execute("https://newsapi.org/v1/articles?source=bbc-news&apiKey=5f542679d794477ca463b2dc67ecc354");
        } else {
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
        }

        pd = new ProgressDialog(this);
        pd.setMessage("Loading Stories...");
        pd.show();


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


    private class GetDataAsync extends AsyncTask<String, Void, ArrayList<Article>> {
        @Override
        protected ArrayList<Article> doInBackground(String... params) {
            HttpURLConnection connection = null;
            ArrayList<Article> result = new ArrayList<>();
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF-8");

                    JSONObject root = new JSONObject(json);
                    JSONArray articles = root.getJSONArray("articles");

                    for (int i = 0; i < articles.length(); i++) {
                        JSONObject articleJson = articles.getJSONObject(i);
                        Article article = new Article();
                        article.setAuthor(articleJson.getString("author"));
                        article.setTitle(articleJson.getString("title"));
                        article.setUrl(articleJson.getString("url"));
                        article.setUrlToImage(articleJson.getString("urlToImage"));
                        article.setPublishedAt(articleJson.getString("publishedAt"));

                        result.add(article);
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
        protected void onPostExecute(ArrayList<Article> result) {
            if (result != null) {
                articleList = result;

                LinearLayout parentLayout = (LinearLayout) findViewById(R.id.newsLayout);
                LayoutInflater layoutInflater = getLayoutInflater();
                View view;

                for (int i = 0; i < articleList.size(); i++) {
                    // Add the text layout to the parent layout
                    view = layoutInflater.inflate(R.layout.news_article, parentLayout, false);

                    // In order to get the view we have to use the new view with text_layout in it
                    //RelativeLayout relativeLayout = findViewById(R.id.article_layout);

                    //TextView textView = (TextView) view.findViewById(R.id.article_author);
                    //textView.setText(articleList.get(i).getAuthor());
                    //TextView textView2 = (TextView) view.findViewById(R.id.article_date);
                    //textView.setText(articleList.get(i).getPublishedAt());
                    TextView textView3 = (TextView) view.findViewById(R.id.article_title);
                    textView3.setText(articleList.get(i).getTitle());

                    // Add the text view to the parent layout

                    parentLayout.addView(textView3);


                }
            }

            sendData(result);

        }

        public void sendData(ArrayList<Article> articles) {

            articleList = articles;
            pd.dismiss();

        }


    }
}// end NewsActivity
