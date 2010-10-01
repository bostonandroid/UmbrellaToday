package org.bostonandroid.umbrellatoday;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

class AlarmSetter implements Runnable {
  private Context context;

  public AlarmSetter(Context c) {
    this.context = c;
  }
  
  public void run() {
    Alert.findNextAlert(context()).perform(new ValueRunner<SavedAlert>() {
      public void run(SavedAlert a) {
        alarmManager().set(AlarmManager.RTC_WAKEUP, a.alertAt().getTimeInMillis(), makePendingIntent(makeIntent(a)));
      }
    }).orElse(new Runnable() {
      public void run() {
        alarmManager().cancel(makePendingIntent(makeIntent()));
      }
    });
  }

  private Context context() {
    return this.context;
  }

  private AlarmManager alarmManager() {
    return (AlarmManager)context().getSystemService(Context.ALARM_SERVICE);
  }

  private PendingIntent makePendingIntent(Intent i) {
    return PendingIntent.getBroadcast(context(), 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
  }
  
  private Intent makeIntent(SavedAlert a) {
    return makeIntent().putExtra("alarm_id", a.id());
  }
  
  private Intent makeIntent() {
    return new Intent(context(), AlarmReceiver.class);
  }
}