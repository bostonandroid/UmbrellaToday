package org.bostonandroid.umbrellatoday;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlertsDatabase extends SQLiteOpenHelper {
  public final static String dbName = "UmbrellaToday";
  public final static int dbVersion = 2;
  public AlertsDatabase(Context context) {
    super(context, dbName, null, dbVersion);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE alerts (_id SERIAL PRIMARY KEY, alert_at TIME, sunday BOOLEAN, monday BOOLEAN, tuesday BOOLEAN, wednesday BOOLEAN, thursday BOOLEAN, friday BOOLEAN, saturday BOOLEAN, location VARCHAR(255), autolocate BOOLEAN)");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (oldVersion < 2) {
      // sqlite doesn't support column renaming
      try {
        db.beginTransaction();
        db.execSQL("ALTER TABLE alerts RENAME TO tmp_alerts");
        db.execSQL("CREATE TABLE alerts (_id SERIAL PRIMARY KEY, alert_at TIME, sunday BOOLEAN, monday BOOLEAN, tuesday BOOLEAN, wednesday BOOLEAN, thursday BOOLEAN, friday BOOLEAN, saturday BOOLEAN, location VARCHAR(255), autolocate BOOLEAN)");
        db.execSQL("INSERT INTO alerts (_id, alert_at, sunday, monday, tuesday, wednesday, thursday, friday, saturday, location, autolocate) SELECT id, alert_at, sunday, monday, tuesday, wednesday, thursday, friday, saturday, location, autolocate FROM tmp_alerts");
        db.execSQL("DROP TABLE tmp_alerts");
        db.setTransactionSuccessful();
      } finally {
        db.endTransaction();
      }
    }
  }
}