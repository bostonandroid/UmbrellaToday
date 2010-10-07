package org.bostonandroid.umbrellatoday;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlertsDatabase extends SQLiteOpenHelper {
  public final static String dbName = "UmbrellaToday";
  public final static int dbVersion = 4;
  public AlertsDatabase(Context context) {
    super(context, dbName, null, dbVersion);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE alerts (_id INTEGER PRIMARY KEY, alert_at TIME, sunday BOOLEAN, monday BOOLEAN, tuesday BOOLEAN, wednesday BOOLEAN, thursday BOOLEAN, friday BOOLEAN, saturday BOOLEAN, location VARCHAR(255), autolocate BOOLEAN, enabled BOOLEAN)");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      try {
          db.beginTransaction();
          db.execSQL("DROP TABLE alerts");
          db.execSQL("CREATE TABLE alerts (_id INTEGER PRIMARY KEY, alert_at TIME, sunday BOOLEAN, monday BOOLEAN, tuesday BOOLEAN, wednesday BOOLEAN, thursday BOOLEAN, friday BOOLEAN, saturday BOOLEAN, location VARCHAR(255), autolocate BOOLEAN, enabled BOOLEAN)");
          db.setTransactionSuccessful();
      } finally {
          db.endTransaction();
      }
  }
}