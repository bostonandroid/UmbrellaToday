package org.bostonandroid.umbrellatoday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// Doesn't seem to work yet.
class OnBoot extends BroadcastReceiver {
  @Override
  public void onReceive(Context c, Intent i) {
    (new AlarmSetter(c)).run();
  }
}