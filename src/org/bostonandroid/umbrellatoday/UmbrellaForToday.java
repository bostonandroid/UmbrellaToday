package org.bostonandroid.umbrellatoday;

import android.app.Activity;
import android.os.Bundle;
import android.os.AsyncTask;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import android.widget.TextView;
import android.util.Log;
import org.apache.http.HttpEntity;
import android.content.Intent;
import android.util.Xml;
import android.sax.RootElement;
import android.sax.Element;
import android.sax.EndTextElementListener;
import java.io.InputStream;
import java.io.IOException;
import org.xml.sax.SAXException;

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
        String reportUrl = intent.getDataString();

        ReportRetriever r = new ReportRetriever();
        r.execute(reportUrl);
    }

    private class ReportRetriever extends AsyncTask<String, Void, Report> {
      @Override
      protected Report doInBackground(String... urls) {
        if (urls.length > 0) {
          return retrieveReport(urls[0]);
        }

        return null;
      }

      @Override
      protected void onPostExecute(Report report) {
        if (report != null) {
          TextView tv = (TextView)findViewById(R.id.report);
          tv.setText(report.getAnswer().toUpperCase());
          // TODO: Get activity title from strings.xml, also do something if location[name] is null
          setTitle("Umbrella Today for " + report.getLocationName());
        }
        else {
          finish();
        }
      }

      private Report retrieveReport(String uri) {
        final DefaultHttpClient client = new DefaultHttpClient();
        final HttpGet getRequest = new HttpGet(uri);

        HttpResponse response = null;

        try {
          response = client.execute(getRequest);
        } catch (IOException e) {
          getRequest.abort();
          return null;
        }

        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
          Log.w(TAG, "Error " + statusCode + " retrieving weather report.");
          return null;
        }

        final HttpEntity entity = response.getEntity();

        if (entity != null) {
          InputStream content = null;

          try {
            content = entity.getContent();
          } catch (IOException e) {
            return null;
          } catch (IllegalStateException e) {
            return null;
          }

          final Report report = new Report();

          // http://www.ibm.com/developerworks/library/x-android/
          RootElement root = new RootElement("forecast");
          root.getChild("answer").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
              report.setAnswer(body);
            }
          });
          Element location = root.getChild("location");
          location.getChild("name").setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
              report.setLocationName(body);
            }
          });

          try {
            Xml.parse(content, Xml.Encoding.UTF_8, root.getContentHandler());
          } catch (IOException e) {
            return null;
          } catch (SAXException e) {
            return null;
          }

          return report;
        }

        return null;
      }
    }
}
