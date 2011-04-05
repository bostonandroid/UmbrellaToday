package org.bostonandroid.umbrellatoday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ResetAlarmReceiver extends BroadcastReceiver {
  private static final String TAG = "UmbrellaToday";
  
  @Override
  public void onReceive(Context c, Intent i) {
    Log.d(TAG, "Setting alarm");
    new AlarmSetter(c).run(); // FIXME: should be async
  }
}