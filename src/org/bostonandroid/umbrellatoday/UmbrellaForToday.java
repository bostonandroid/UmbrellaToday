package org.bostonandroid.umbrellatoday;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.TimePicker;

public class UmbrellaForToday extends Activity
{
    public final static String TAG = "UmbrellaForToday";
    static final int DIALOG_LOADING = 0;
    private int mHour;
    private int mMinute;
    static final int TIME_DIALOG_ID = 1;
    private PendingIntent mAlarmSender;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today);

        showDialog(DIALOG_LOADING);

        Intent intent = getIntent();
        String reportUrl = intent.getDataString();

        ReportRetriever r = new ReportRetriever();
        r.execute(reportUrl);
        
        Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        
        mAlarmSender = PendingIntent.getBroadcast(UmbrellaForToday.this, 0,
                new Intent(UmbrellaForToday.this, AlarmReceiver.class), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.today_menu, menu);
      return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.about_button:
            Intent intent = new Intent(UmbrellaForToday.this,
                    UmbrellaToday.class);
            startActivity(intent);
            return true;
        case R.id.set_alarm_button:
            showDialog(TIME_DIALOG_ID);
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void setAlarm() {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
        int nowMinute = calendar.get(Calendar.MINUTE);

        if (mHour < nowHour || mHour == nowHour && mMinute <= nowMinute) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, mHour);
        calendar.set(Calendar.MINUTE, mMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                mAlarmSender);
    }

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;
            setAlarm();
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        switch (id) {
        case DIALOG_LOADING:
            dialog = ProgressDialog.show(UmbrellaForToday.this, "",
                    "Loading. Please wait ...", false);
            break;
        case TIME_DIALOG_ID:
            dialog = new TimePickerDialog(this, mTimeSetListener, mHour,
                    mMinute, false);
            break;
        default:
            dialog = null;
        }
        return dialog;
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
          dismissDialog(DIALOG_LOADING);
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
