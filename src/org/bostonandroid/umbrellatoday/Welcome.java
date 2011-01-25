package org.bostonandroid.umbrellatoday;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.View;

public class Welcome extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.welcome);

    Button addAlert = (Button) findViewById(R.id.add_alert_welcome);
    addAlert.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        startActivity(new Intent(Welcome.this, NewAlert.class));
      }
    });
  }
}
