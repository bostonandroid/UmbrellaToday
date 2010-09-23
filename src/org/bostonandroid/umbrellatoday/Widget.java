package org.bostonandroid.umbrellatoday;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider
{
	static String TAG = "UMBRELLA TODAY"; 
	
	@Override 
	public void onUpdate (Context context, AppWidgetManager manager, int[] ids)
	{
		context.startService(new Intent (context, UpdateService.class));
	}
	
	public static class UpdateService extends Service
	{
		@Override 
		public void onStart (Intent intent, int startId)
		{
			RemoteViews updateViews = buildUpdate (this);

			ComponentName widget = new ComponentName (this, Widget.class);
			AppWidgetManager man = AppWidgetManager.getInstance(this);
			man.updateAppWidget(widget, updateViews);
		}
		
		public RemoteViews buildUpdate (Context context)
		{
			//Resources res = context.getResources();
			Log.i(TAG, "Widget Updating");
			
			Time today = new Time ();
			today.setToNow();
			
			
			RemoteViews updateViews = new RemoteViews (context.getPackageName(), R.layout.widget_content);
			updateViews.setTextViewText(R.id.message,  "UT: " +today.minute +" : " +today.second);
	
			
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent (this, Alerts.class), 0);
			updateViews.setOnClickPendingIntent(R.id.message, pendingIntent);

			
			
			return updateViews;
		}
		
		@Override
		public IBinder onBind (Intent intent)
		{
			return null;
		}
	}
	
}
