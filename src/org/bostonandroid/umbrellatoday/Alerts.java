package org.bostonandroid.umbrellatoday;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Alerts extends ListActivity {
  private CursorAdapter alertCursor;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.alerts);
    setListAdapter(alertCursor());
    LinearLayout addAlert = (LinearLayout) findViewById(R.id.add_alert);
    addAlert.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setClass(Alerts.this, NewAlert.class);
        startActivity(i);
      }
    });
  }
  
  private CursorAdapter alertCursor() {
    if (this.alertCursor == null)
      this.alertCursor = new SimpleCursorAdapter(
          this,
          android.R.layout.simple_list_item_1, Alert.all(this),
          new String[] { "alert_at" }, 
          new int[] { android.R.id.text1 });
    return this.alertCursor;
  }
  
  @Override
  protected void onListItemClick (ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Intent i = new Intent(Intent.ACTION_VIEW);
    Log.i("Alerts", "onListItemClick: cursor="+alertCursor().getCursor());
    Log.i("Alerts", "onListItemClick: moved="+alertCursor().getCursor().moveToPosition(position));
    Log.i("Alerts", "onListItemClick: v="+alertCursor().getCursor().getLong(0));
    i.putExtra("alert_id", alertCursor().getItemId(position));
    i.setClass(Alerts.this, EditAlert.class);
    startActivity(i);
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
