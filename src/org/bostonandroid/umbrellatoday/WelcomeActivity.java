package org.bostonandroid.umbrellatoday;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends Activity {
  private static final int NEW_ALERT = 1;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.welcome);

    Button addAlert = (Button) findViewById(R.id.add_alert_welcome);
    addAlert.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        startActivityForResult(new Intent(WelcomeActivity.this, NewAlertActivity.class), NEW_ALERT);
      }
    });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
    case NEW_ALERT:
      if (resultCode == RESULT_OK) {
        startActivity(new Intent(this, AlertsActivity.class));
        finish();
      }
      break;
    }
  }
}
