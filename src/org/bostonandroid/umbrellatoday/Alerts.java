package org.bostonandroid.umbrellatoday;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Alerts extends ListActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    String[] alerts = {"Add Alert"};
    
    setContentView(R.layout.alerts);
    setListAdapter(new ArrayAdapter(this, 
         android.R.layout.simple_list_item_1,
         alerts));
  }
  
  @Override
  protected void onListItemClick(ListView parent, View view, int position, long id) {
    super.onListItemClick(parent, view, position, id);
    if (position == 0) {
      Intent i = new Intent(Intent.ACTION_VIEW);
      i.setClass(Alerts.this, NewAlert.class);
      startActivity(i);
    }
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
