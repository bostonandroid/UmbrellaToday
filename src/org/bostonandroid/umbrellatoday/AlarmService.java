package org.bostonandroid.umbrellatoday;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmService extends IntentService {
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
  protected void onHandleIntent(Intent intent) {
    Alert.find(this, intent.getExtras().getLong("alarm_id")).perform(new ValueRunner<SavedAlert>() {
      public void run(SavedAlert alert) {
        alert.url(AlarmService.this).
          perform(new ValueRunner<String>() {
            public void run(String url) {
              new ReportRetriever(url).retrieveReport().perform(new ValueRunner<Report>() {
                public void run(Report report) {
                  String answer = report.getAnswer();
                  if (answer.equals("yes"))
                    showNotification("Rain");
                  else if (answer.equals("snow"))
                    showNotification("Snow");
                }});}}).
          orElse(new Runnable() {
            public void run() {
              showErrorNotification("Can't look up location data.");
            }});}});
  }

  void showNotification(String message) {
    String appName = getString(R.string.app_name);
    Notification notification = new Notification(
        R.drawable.notification_icon, appName, System
        .currentTimeMillis());
    notification.setLatestEventInfo(AlarmService.this, appName, message,
        PendingIntent.getActivity(this, 1, new Intent(this, Alerts.class), 0));
    notification.flags |= Notification.FLAG_AUTO_CANCEL;
    notificationManager.notify(1, notification);
  }
  
  private void showErrorNotification(String message) {
    showNotification(message);
  }
}
