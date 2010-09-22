package org.bostonandroid.umbrellatoday;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;

public class Alerts extends ListActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.alerts);
    setListAdapter(new SimpleCursorAdapter(this,
        android.R.layout.simple_list_item_1,
        Alert.all(this),
        new String[] {"alert_at"}, // A list of column names representing the data to bind to the UI.
        new int[] {android.R.id.text1})); // The views that should display column in the "from" parameter. These should all be TextViews. The first N views in this list are given the values of the first N columns in the from parameter.

    LinearLayout addAlert = (LinearLayout) findViewById(R.id.add_alert);
    addAlert.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setClass(Alerts.this, NewAlert.class);
        startActivity(i);
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.about_button:
      Intent intent = new Intent(Alerts.this, AboutUmbrellaToday.class);
      startActivity(intent);
      return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }
}
