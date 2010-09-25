package org.bostonandroid.umbrellatoday;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class UmbrellaTodayApplication extends Application {

    AlertsDatabase db;

    @Override
    public void onCreate() {
        Log.d("UmbrellaTodayApplication", "opening database");
        this.db = new AlertsDatabase(this);
    }

    @Override
    public void onTerminate() {
        Log.d("UmbrellaTodayApplication", "closing database");
        this.db.close();
    }

    public AlertsDatabase getAlertsDatabase() {
        return db;
    }
    
    public static AlertsDatabase getAlertsDatabase(Context c) {
        // FIXME: should probably check that c.getApplicationContext() is an UmbrellaTodayApplication
        return ((UmbrellaTodayApplication) c.getApplicationContext()).getAlertsDatabase();
    }
}
