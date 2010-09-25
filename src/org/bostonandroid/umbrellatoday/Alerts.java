package org.bostonandroid.umbrellatoday;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Alerts extends ListActivity {
  private CursorAdapter alertCursorAdapter;
  private Cursor alertCursor;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.alerts);
    setListAdapter(alertCursorAdapter());
    LinearLayout addAlert = (LinearLayout) findViewById(R.id.add_alert);
    addAlert.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        Intent i = new Intent(Alerts.this, NewAlert.class);
        startActivity(i);
      }
    });
    registerForContextMenu(getListView());
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.context_menu, menu);
  }
  
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    switch (item.getItemId()) {
    case R.id.edit:
      editAlert(info.id);
      return true;
    case R.id.delete:
      deleteAlert(info.id);
      return true;
    default:
      return super.onContextItemSelected(item);
    }
  }

    private void deleteAlert(long id) {
        Alert.find(this, id).delete(this);
    }

    private void editAlert(long id) {
        Intent i = new Intent(Alerts.this, EditAlert.class);
        i.putExtra("alert_id", id);
        startActivity(i);
    }

  private Cursor alertCursor() {
      if (this.alertCursor == null) {
          this.alertCursor = Alert.all(this);
          startManagingCursor(this.alertCursor);
      }
      return this.alertCursor;
  }

  private CursorAdapter alertCursorAdapter() {
    if (this.alertCursorAdapter == null)
      this.alertCursorAdapter = new SimpleCursorAdapter(
          this,
          android.R.layout.simple_list_item_1, alertCursor(),
          new String[] { "alert_at" }, 
          new int[] { android.R.id.text1 });
    return this.alertCursorAdapter;
  }

  @Override
  protected void onListItemClick (ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Log.i("Alerts", "onListItemClick: cursor="+alertCursor());
    Log.i("Alerts", "onListItemClick: moved="+alertCursor().moveToPosition(position));
    Log.i("Alerts", "onListItemClick: v="+alertCursor().getLong(0));
    editAlert(alertCursorAdapter().getItemId(position));
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
