package org.bostonandroid.umbrellatoday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    AlarmService.sendWakefulWork(context, new Intent(context,
        AlarmService.class).putExtras(intent.getExtras()));
  }
}
