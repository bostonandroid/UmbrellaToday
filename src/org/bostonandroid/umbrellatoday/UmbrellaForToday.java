package org.bostonandroid.umbrellatoday;

import android.app.Activity;
import android.os.Bundle;
import android.os.AsyncTask;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import android.net.Uri;
import android.widget.TextView;
import android.util.Log;
import org.apache.http.HttpEntity;
import java.io.ByteArrayOutputStream;
import android.content.Intent;

public class UmbrellaForToday extends Activity
{
  public final static String TAG = "UmbrellaForToday";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today);

        Intent intent = getIntent();
        Uri reportUri = intent.getData();

        ReportRetriever r = new ReportRetriever();
        r.execute(reportUri);
    }

    private class ReportRetriever extends AsyncTask<Uri, Void, String> {
      protected String doInBackground(Uri... uris) {
        return retrieveReport(uris[0]);
      }

      protected void onPostExecute(String report) {
        Log.d(TAG, report);
        TextView tv = (TextView)findViewById(R.id.report);
        tv.setText(report);
      }

      private String retrieveReport(Uri uri) {
        final DefaultHttpClient client = new DefaultHttpClient();
        final HttpGet getRequest = new HttpGet(uri.toString());

        try {
          HttpResponse response = client.execute(getRequest);

          final int statusCode = response.getStatusLine().getStatusCode();
          if (statusCode != HttpStatus.SC_OK) {
            Log.w(TAG, "Error " + statusCode + " while retrieving contacts.");
            return null;
          }

          ByteArrayOutputStream ostream = new ByteArrayOutputStream();
          response.getEntity().writeTo(ostream);
          return ostream.toString();

        } catch (Exception e) {
          getRequest.abort();
        }
        return null;
      }
    }
}
