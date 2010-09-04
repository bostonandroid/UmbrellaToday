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
import android.location.LocationManager;
import android.location.Location;
import android.content.Context;
import android.location.Geocoder;
import java.util.List;
import android.location.Address;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.widget.Toast;
import java.util.Stack;

public class UmbrellaToday extends Activity
{
  public final static String TAG = "UmbrellaToday";

  static final int DIALOG_LOADING = 0;

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
              showDialog(DIALOG_LOADING);
              ResourceRetriever r = new ResourceRetriever();
              r.execute(locationText);
            }
          }
        });

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        String provider = LocationManager.NETWORK_PROVIDER;

        if (locationManager.isProviderEnabled(provider)) {
          showDialog(DIALOG_LOADING);

          Location lastKnownLocation = locationManager.getLastKnownLocation(provider);

          Address address = geocodeLocation(lastKnownLocation);

          if (address != null) {
            ResourceRetriever r = new ResourceRetriever();
            r.execute(address.getLocality() + ", " + address.getAdminArea());
          } else {
            dismissDialog(DIALOG_LOADING);
            Toast.makeText(UmbrellaToday.this, "Couldn't find location ...", Toast.LENGTH_SHORT).show();
          }
        }
    }

    private Address geocodeLocation(Location location) {
      double lat = location.getLatitude();
      double lon = location.getLongitude();

      Geocoder geocoder = new Geocoder(UmbrellaToday.this);

      List<Address> addressList = null;

      try {
        addressList = geocoder.getFromLocation(lat, lon, 1);
      } catch (IOException e) {
        return null;
      }

      if (!addressList.isEmpty()) {
        Address address = addressList.get(0);
        return address;
      }

      return null;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
      Dialog dialog;
      switch(id) {
      case DIALOG_LOADING:
        dialog = ProgressDialog.show(UmbrellaToday.this, "", "Loading. Please wait ...", false);
        break;
      default:
        dialog = null;
      }
      return dialog;
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
          dismissDialog(DIALOG_LOADING);
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
