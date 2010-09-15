package org.bostonandroid.umbrellatoday;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Alerts extends ListActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    String[] alerts = {};
    
    setContentView(R.layout.alerts);
    setListAdapter(new ArrayAdapter(this, 
         android.R.layout.simple_list_item_1,
         alerts));

    LinearLayout addAlert = (LinearLayout)findViewById(R.id.add_alert);
    addAlert.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setClass(Alerts.this, NewAlert.class);
        startActivity(i);
      }
    });
  }
}
