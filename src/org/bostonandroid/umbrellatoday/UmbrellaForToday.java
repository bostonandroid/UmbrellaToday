package org.bostonandroid.umbrellatoday;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.TimePicker;

public class UmbrellaForToday extends Activity {
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

        ReportRetriever r = new ReportRetriever(new ReportConsumer() {
            public void consumeReport(Report report) {
                TextView tv = (TextView) findViewById(R.id.report);

                tv.setText(report.getAnswer());
                setTitle("Umbrella Today for " + report.getLocationName());
                dismissDialog(DIALOG_LOADING);
            }
        });

        r.execute(intent.getDataString());

        Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        Intent notificationIntent = new Intent(Intent.ACTION_VIEW, intent.getData());
        notificationIntent.setClass(UmbrellaForToday.this, AlarmService.class);

        mAlarmSender = PendingIntent.getService(UmbrellaForToday.this, 0,
                notificationIntent, 0);
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
            return true;
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
}
