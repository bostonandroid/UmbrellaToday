package org.bostonandroid.umbrellatoday;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    public final static String TAG = "AlarmReceiver";
    private Context context;
    private Intent intent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Broadcast received");

        this.context = context;
        this.intent = intent;

        ReportRetriever r = new ReportRetriever(reportConsumer);

        r.execute(intent.getDataString());
    }

    private ReportConsumer reportConsumer = new ReportConsumer() {
        public void consumeReport(Report report) {
            if (report.getAnswer().equals("yes")) {
                NotificationManager notificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);

                int icon = R.drawable.notification_icon;
                long when = System.currentTimeMillis();

                Notification notification = new Notification(icon,
                        "UmbrellaToday wants your attention!", when);

                Intent notificationIntent = new Intent(Intent.ACTION_VIEW,
                        intent.getData());
                notificationIntent.setClass(context, UmbrellaForToday.class);
                PendingIntent contentIntent = PendingIntent.getActivity(
                        context, 0, notificationIntent, 0);

                notification.setLatestEventInfo(context, "UmbrellaToday",
                        "You should bring your Umbrella today!", contentIntent);

                notificationManager.notify(1, notification);
            }
        }
    };
}
