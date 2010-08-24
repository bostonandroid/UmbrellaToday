package org.bostonandroid.umbrellatoday;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.net.Uri;
import android.util.Log;
import android.os.AsyncTask;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import android.content.Intent;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import org.apache.http.Header;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

public class UmbrellaToday extends Activity
{
  public final static String TAG = "UmbrellaToday";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final Button goButton = (Button)findViewById(R.id.button);
        final EditText location = (EditText)findViewById(R.id.location);

        goButton.setOnClickListener(new View.OnClickListener() {
          public void onClick(View v) {
            String locationText = location.getText().toString();
            if (locationText.length() > 0) {
              ResourceRetriever r = new ResourceRetriever();
              r.execute(locationText);
            }
          }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.main_menu, menu);
      return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      case R.id.about_button:
        Intent intent = new Intent(UmbrellaToday.this, AboutUmbrellaToday.class);
        startActivity(intent);
       return true;
      default:
        return super.onOptionsItemSelected(item);
      }
    }

    private class ResourceRetriever extends AsyncTask<String, Void, Uri> {
      @Override
      protected Uri doInBackground(String... locations) {
        if (locations.length > 0) {
          return retrieveResource(locations[0]);
        }

        return null;
      }

      @Override
      protected void onPostExecute(Uri weatherUri) {
        if (weatherUri != null) {
          Intent intent = new Intent(Intent.ACTION_VIEW, weatherUri);
          intent.setClass(UmbrellaToday.this, UmbrellaForToday.class);

          UmbrellaToday.this.startActivity(intent);
        }
      }

      private Uri retrieveResource(String location) {
        final DefaultHttpClient client = new DefaultHttpClient();
        final HttpPost postRequest = new HttpPost(forecastsUrl());

        postRequest.addHeader("Accept", "text/xml");
        postRequest.addHeader("Content-Type", "text/xml");

        StringEntity requestEntity = null;

        try {
          requestEntity = new StringEntity("<forecast><location-name>"+location+"</location-name></forecast>");
        } catch (UnsupportedEncodingException e) {
          postRequest.abort();
          return null;
        }

        postRequest.setEntity(requestEntity);

        HttpResponse response = null;

        try {
          response = client.execute(postRequest);
        } catch (IOException e) {
          postRequest.abort();
          return null;
        }

        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_CREATED) {
          Log.w(TAG, "Error " + statusCode + " retrieving resource.");
          return null;
        }

        Header redirectLocation = response.getFirstHeader("Location");
        if (redirectLocation != null) {
          return Uri.parse(redirectLocation.getValue() + ".xml");
        }

        return null;
      }

      private String forecastsUrl() {
        return "http://umbrellatoday.com/forecasts";
      }
    }
}
