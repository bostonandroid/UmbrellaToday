package org.bostonandroid.umbrellatoday;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class AlarmService extends Service {
    public final static String TAG = "AlarmReceiver";
    private NotificationManager notificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onStart(final Intent intent, int startId) {
        ReportConsumer consumer = new AlarmServiceReportConsumer(intent);
        ReportRetriever r = new ReportRetriever(consumer);
        r.execute(intent.getDataString());
    }

    private void showNotification(String message, PendingIntent contentIntent) {
        Notification notification = new Notification(
                R.drawable.notification_icon, "Umbrella Today", System
                        .currentTimeMillis());

        notification.setLatestEventInfo(AlarmService.this, "UmbrellaToday",
                message, contentIntent);

        notificationManager.notify(1, notification);
    }

    private class AlarmServiceReportConsumer implements ReportConsumer {
        private Intent intent;

        AlarmServiceReportConsumer(Intent intent) {
            this.intent = intent;
        }

        public void consumeReport(Report report) {
            if (report.getAnswer().equals("yes")) {
                PendingIntent contentIntent = notificationIntent(intent);
                showNotification("You should bring your Umbrella today", contentIntent);
            }
        }
        
        private PendingIntent notificationIntent(Intent intent) {
            Intent notificationIntent = new Intent(Intent.ACTION_VIEW, intent.getData());
            notificationIntent.setClass(AlarmService.this, UmbrellaForToday.class);

            PendingIntent contentIntent = PendingIntent.getActivity(
                    AlarmService.this, 0, notificationIntent, 0);

            return contentIntent;
        }
    }
}
