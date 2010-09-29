package org.bostonandroid.umbrellatoday;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

class SavedAlert {
  private Alert alert;
  private long id;
  private SQLException errorCanBeNull;
  
  public static Maybe<SavedAlert> find(Context context, long id) {
    Log.i("Alert", "find: id = "+id);
    SQLiteDatabase db = UmbrellaTodayApplication.getAlertsDatabase(context).getReadableDatabase();
    Cursor c = db.rawQuery("SELECT * FROM alerts WHERE _id = ?", new String[] {id+""});
    Maybe<SavedAlert> ma;
    if (c.getCount() > 0) {
      c.moveToFirst();
      Log.i("Alert", "find: c="+c);
      ma = new Just<SavedAlert>(new SavedAlert(c.getLong(0),
          new Alert(
          stringToCalendar(c.getString(1)),
          c.getInt(2) == 1,
          c.getInt(3) == 1,
          c.getInt(4) == 1,
          c.getInt(5) == 1,
          c.getInt(6) == 1,
          c.getInt(7) == 1,
          c.getInt(8) == 1,
          c.getString(9),
          c.getInt(10) == 1)));
      c.close();
    } else {
      Log.i("Alert", "find: c.count=0");
      ma =  new Nothing<SavedAlert>();
    }
    c.close();
    return ma;
  }
  
  SavedAlert(long i, Alert a) {
    this.alert = a;
    this.id = i;
  }
  
  private Either<SavedAlert> update(Context c, Calendar alertAt, boolean sunday, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, String location, boolean autolocate, Runnable f) {
    SavedAlert a = new SavedAlert(this.id, new Alert(alertAt, sunday, monday, tuesday, wednesday, thursday, friday, saturday, location, autolocate));
    SQLiteDatabase db = UmbrellaTodayApplication.getAlertsDatabase(c).getReadableDatabase();
    try {
      db.replaceOrThrow("alerts", null, a.asContentValues());
      f.run();
      return new Right<SavedAlert>(a);
    } catch (SQLException e) {
      a.errorCanBeNull = e;
      return new Left<SavedAlert>(a);
    }
  }
  
  private ContentValues asContentValues() {
    ContentValues cv = this.alert.asContentValues();
    cv.put("_id", this.id);
    return cv;
  }
  
  // FIXME: This does not belong here.
  private static Calendar stringToCalendar(String s) {
    try {
      Date d = Alert.formatter().parse(s);
      Calendar c = Calendar.getInstance();
      c.setTime(d);
      return c;
    } catch (ParseException e) {
      return new GregorianCalendar(1970,01,01);
    }
  }
  
  public static Maybe<SavedAlert> findNextAlert(Cursor cursor) {
    return findNextAlert(cursor, Long.MAX_VALUE, new Nothing<SavedAlert>());
  }

  private static Maybe<SavedAlert> findNextAlert(Cursor cursor, Long minTime, Maybe<SavedAlert> nextAlert) {
    if (cursor.moveToNext()) {
      SavedAlert alert = new SavedAlert(cursor.getLong(0),
          new Alert(stringToCalendar(cursor.getString(1)),
              cursor.getInt(2) == 1,
              cursor.getInt(3) == 1,
              cursor.getInt(4) == 1,
              cursor.getInt(5) == 1,
              cursor.getInt(6) == 1,
              cursor.getInt(7) == 1,
              cursor.getInt(8) == 1,
              cursor.getString(9),
              cursor.getInt(10) == 1));
      Long alertTime = alert.alertAt().getTimeInMillis();
      if (alertTime < minTime)
        return findNextAlert(cursor, alertTime, new Just<SavedAlert>(alert));
      else
        return findNextAlert(cursor, minTime, nextAlert);
    } else {
      cursor.close();
      return nextAlert;
    }
  }
  
  public long id() {
    return this.id;
  }
  
  public List<String> repeatDays() {
    return this.alert.repeatDays();
  }
  
  public boolean isAutolocate() {
    return this.alert.isAutolocate();
  }
  
  public String location() {
    return this.alert.location();
  }
  
  public Calendar alertAt() {
    return this.alert.alertAt();
  }

  public boolean delete(Context c, Runnable f) {
    SQLiteDatabase db = UmbrellaTodayApplication.getAlertsDatabase(c).getWritableDatabase();
    try {
      db.delete("alerts", "_id=?", new String[] { Long.toString(this.id) });
      f.run();
      return true;
    } catch (SQLException e) {
      this.errorCanBeNull = e;
      return false;
    }
  }
  
  public String errorString() {
    if (this.errorCanBeNull == null) {
      return "";
    } else {
      return this.errorCanBeNull.toString();
    }
  }
  
  public Maybe<String> url(Context c) {
    return new LocationUrlController(c, this).url();
  }
  
  public SavedAlert.Updater updater() {
    return new SavedAlert.Updater(this);
  }
  
  static public class Updater {
    private Calendar time;
    private List<String> repeatDays;
    private String theLocation;
    private boolean isAutolocate;
    private SavedAlert alert;

    public Updater(SavedAlert a) {
      this.alert = a;
      this.time = new GregorianCalendar(1970,01,01);
      this.repeatDays = new ArrayList<String>();
      this.theLocation = "";
      this.isAutolocate = false;
    }
    
    public Updater alertAt(Calendar time) {
      this.time = time;
      return this;
    }
    
    public Updater repeatDays(List<String> days) {
      repeatDays = days;
      return this;
    }
    
    public Updater location(String loc) {
      theLocation = loc;
      return this;
    }
    
    public Updater autolocate(boolean autogps) {
      isAutolocate = autogps;
      return this;
    }
    
    public Either<SavedAlert> update(Context c, Runnable f) {
      return this.alert.update(c,
          time,
          repeatDays.contains("Sunday"),
          repeatDays.contains("Monday"),
          repeatDays.contains("Tuesday"),
          repeatDays.contains("Wednesday"),
          repeatDays.contains("Thursday"),
          repeatDays.contains("Friday"),
          repeatDays.contains("Saturday"),
          theLocation,
          isAutolocate,
          f);
    }
  }
}