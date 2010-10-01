package org.bostonandroid.umbrellatoday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBoot extends BroadcastReceiver {
  @Override
  public void onReceive(Context c, Intent i) {
    (new AlarmSetter(c)).run(); // FIXME: should be async
  }
}