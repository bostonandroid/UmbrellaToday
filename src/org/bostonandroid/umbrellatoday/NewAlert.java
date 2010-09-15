package org.bostonandroid.umbrellatoday;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.Button;

public class NewAlert extends PreferenceActivity {
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    addPreferencesFromResource(R.xml.new_alert);
    setContentView(R.layout.new_alert);
    
    Button nextButton = (Button)findViewById(R.id.save_alert);
    nextButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setClass(NewAlert.this, Alerts.class);
        startActivity(i);
      }
    });
  }
}
