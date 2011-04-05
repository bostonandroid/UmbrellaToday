package org.bostonandroid.umbrellatoday;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class AlarmService extends WakefulIntentService {
  public final static String TAG = "AlarmService";

  private NotificationManager notificationManager;

  public AlarmService() {
    super("AlarmService");
  }

  @Override
  public void onCreate() {
    super.onCreate();
    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
  }

  @Override
  protected void doWakefulWork(Intent intent) {
    final long alarmId = intent.getExtras().getLong("alarm_id");
 
    Alert.find(this, alarmId).perform(new ValueRunner<SavedAlert>() {
      public void run(SavedAlert alert) {
        alert.url(AlarmService.this).
          perform(new ValueRunner<String>() {
            public void run(String url) {
              new ReportRetriever(url).retrieveReport().perform(new ValueRunner<Report>() {
                public void run(Report report) {
                  String answer = report.getAnswer();
                  if (answer.equals("yes"))
                    showNotification(R.drawable.weather_showers, "You may need your galoshes!", alarmId);
                  else if (answer.equals("snow"))
                    showNotification(R.drawable.weather_snow, "Bring a shovel!", alarmId);
                }});}}).
          orElse(new Runnable() {
            public void run() {
              showErrorNotification("Can't look up location data.", alarmId);
            }});

        if (!alert.isRepeating())
          alert.disable(AlarmService.this);
      }});
    
    new AlarmSetter(this).run();
  }
  
  private Notification buildNotification(int icon, String contentText, long alertId) {
    Notification notification = new Notification(icon, contentText, System.currentTimeMillis());
    notification.setLatestEventInfo(this, getString(R.string.app_name), contentText,
        PendingIntent.getActivity(this, 0, new Intent(this, EditAlertActivity.class).putExtra("alert_id", alertId), 0));
    notification.flags |= Notification.FLAG_AUTO_CANCEL;
    return notification;
  }

  void showNotification(int icon, String contentText, long alertId) {
    notificationManager.cancel((int)alertId); // FIXME: cancel and notify take an int, alertId is long
    notificationManager.notify((int)alertId, buildNotification(icon, contentText, alertId));
  }
  
  private void showErrorNotification(String contentText, long alarmId) {
    showNotification(R.drawable.weather_showers, contentText, alarmId); // FIXME: use thunder bolt icon
  }
}
