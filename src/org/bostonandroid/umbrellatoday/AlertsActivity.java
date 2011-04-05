package org.bostonandroid.umbrellatoday;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class AlertsActivity extends ListActivity {
  private CursorAdapter alertCursorAdapter;
  private Cursor alertCursor;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    prettyBlank(new Runnable() {
      public void run() {
        setContentView(R.layout.alerts);

        setListAdapter(alertCursorAdapter());
        LinearLayout addAlert = (LinearLayout) findViewById(R.id.add_alert);
        addAlert.setOnClickListener(new View.OnClickListener() {
          public void onClick(View v) {
            startActivity(new Intent(AlertsActivity.this, NewAlertActivity.class));
          }
        });
        registerForContextMenu(getListView());
      }
    });
  }

  private void prettyBlank(Runnable f) {
    if (Alert.isEmpty(this)) {
      // if no alerts, display the welcome screen
      startActivity(new Intent(AlertsActivity.this, WelcomeActivity.class));
      finish();
    } else
      // we have alerts, set us up the bomb
      f.run();
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
    Alert.find(this, id).perform(new ValueRunner<SavedAlert>() {
      public void run(SavedAlert a) {
        a.delete(AlertsActivity.this, new AlarmSetter(AlertsActivity.this));
      }
    });
    prettyBlank(new Runnable() {
      public void run() {
        // FIXME: why doesn't this run automatically?
        alertCursor().requery();
      }
    });
  }

  private void editAlert(long id) {
    Intent i = new Intent(AlertsActivity.this, EditAlertActivity.class);
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
      this.alertCursorAdapter = new AlertCursorAdapter(this, alertCursor());
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
      Intent intent = new Intent(AlertsActivity.this, AboutActivity.class);
      startActivity(intent);
      return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }

  private class AlertCursorAdapter extends CursorAdapter {
    public AlertCursorAdapter(Context context, Cursor c) {
      super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
      LayoutInflater inflater = LayoutInflater.from(context);
      View ret = inflater.inflate(android.R.layout.simple_list_item_checked, parent, false);
      return ret;
    }

    // FIXME: orElse remove row from layout?
    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
      Alert.find(cursor).perform(
          new ValueRunner<SavedAlert>() {
            public void run(SavedAlert alert) {
              CheckedTextView tv = (CheckedTextView) view.findViewById(android.R.id.text1);
              tv.setText(DateFormat.getTimeFormat(context).format(alert.alertAt().getTime()));
              // FIXME: doesn't get unchecked when the alert fires off
              tv.setChecked(alert.isEnabled());
            }
          });
    }
  }
}
