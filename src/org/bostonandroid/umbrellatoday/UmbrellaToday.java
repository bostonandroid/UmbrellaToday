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
import java.util.List;
import java.util.ArrayList;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;
import android.content.Intent;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import org.apache.http.Header;

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
            // ask UT for the resource to load to get the data for the given input
            // call UmbrellaTodayLocation with the resource

            getUmbrellaTodayResource(location.getText().toString());
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
      // Handle item selection
      switch (item.getItemId()) {
      case R.id.about:
        Intent intent = new Intent(UmbrellaToday.this, AboutUmbrellaToday.class);
        startActivity(intent);
       return true;
      default:
        return super.onOptionsItemSelected(item);
      }
    }

    private void getUmbrellaTodayResource(String location) {
      UmbrellaTodayResourceRetriever utr = new UmbrellaTodayResourceRetriever();
      utr.execute(location);
    }

    private class UmbrellaTodayResourceRetriever extends AsyncTask<String, Void, Uri> {
      @Override
      protected Uri doInBackground(String... locations) {
        String weatherUri = postLocationToUmbrellaToday(locations[0]);
        Log.d(TAG, weatherUri);
        return Uri.parse(weatherUri);
      }

      @Override
      protected void onPostExecute(Uri weatherUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, weatherUri);
        intent.setClass(UmbrellaToday.this, UmbrellaForToday.class);

        UmbrellaToday.this.startActivity(intent);
      }

      private String postLocationToUmbrellaToday(String location) {
        final DefaultHttpClient client = new DefaultHttpClient();
        final HttpPost postRequest = new HttpPost(forecastsUrl());

        postRequest.addHeader("Accept", "text/xml");
        postRequest.addHeader("Content-Type", "text/xml");

        try {
          postRequest.setEntity(new StringEntity("<forecast><location-name>"+location+"</location-name></forecast>"));
          HttpResponse response = client.execute(postRequest);
          final int statusCode = response.getStatusLine().getStatusCode();
          if (statusCode != HttpStatus.SC_CREATED) {
            Log.w(TAG, "Error " + statusCode + " retrieving UmbrellaToday resource.");
            return null;
          }

          Header redirectLocation = response.getFirstHeader("Location");
          if (redirectLocation != null) {
            return redirectLocation.getValue() + ".xml";
          }

        } catch (Exception e) {
          postRequest.abort();
        }
        return null;
      }

      private String forecastsUrl() {
        return "http://umbrellatoday.com/forecasts";
      }
    }
}
