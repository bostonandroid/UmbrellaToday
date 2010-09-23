package org.bostonandroid.umbrellatoday;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class Alert {
  private Exception errorCanBeNull;
  private long id;
  private Calendar alertAt;
  private boolean sunday;
  private boolean monday;
  private boolean tuesday;
  private boolean wednesday;
  private boolean thursday;
  private boolean friday; 
  private boolean saturday;
  private String location;
  private boolean autolocate;
  
  public static Cursor all(Context c) {
    SQLiteDatabase db = new AlertsDatabase(c).getReadableDatabase();
    Cursor x = db.query("alerts", null, null, null, null, null, null);
    return x;
  }
  
  public static Alert find(Context context, long id) {
    Log.i("Alert", "find: id = "+id);
    SQLiteDatabase db = new AlertsDatabase(context).getReadableDatabase();
    Cursor c = db.rawQuery("SELECT * FROM alerts WHERE _id = ?", new String[] {id+""});
    if (c.getCount() > 0) {
      c.moveToFirst();
      Log.i("Alert", "find: c="+c);
      return new Alert(c.getLong(0),
          stringToCalendar(c.getString(1)),
          c.getInt(2) == 1,
          c.getInt(3) == 1,
          c.getInt(4) == 1,
          c.getInt(5) == 1,
          c.getInt(6) == 1,
          c.getInt(7) == 1,
          c.getInt(8) == 1,
          c.getString(9),
          c.getInt(10) == 1);
    } else {
      Log.i("Alert", "find: c.count=0");
      return null;
    }
  }
  
  protected Alert(Calendar alertAt, boolean sunday, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, String location, boolean autolocate) {
    init(-1, alertAt, sunday, monday, tuesday, wednesday, thursday, friday, saturday, location, autolocate);
  }

  private Alert(long id, Calendar alertAt, boolean sunday, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, String location, boolean autolocate) {
    init(id, alertAt, sunday, monday, tuesday, wednesday, thursday, friday, saturday, location, autolocate);
  }
  
  private void init(long id, Calendar alertAt, boolean sunday, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, String location, boolean autolocate) {
    this.id = id;
    this.alertAt = alertAt;
    this.sunday = sunday;
    this.monday = monday;
    this.tuesday = tuesday;
    this.wednesday = wednesday;
    this.thursday = thursday;
    this.friday = friday;
    this.saturday = saturday;
    this.location = location;
    this.autolocate = autolocate;
  }
  
  public Calendar alertAt() {
    return this.alertAt;
  }
  public List<String> repeatDays() {
	  List<String> days = new ArrayList<String>();
	  if (this.monday) {
		  days.add("Monday");
	  }
	  if (this.tuesday){
		  days.add("Tuesday");
	  }
	  if (this.wednesday) {
		  days.add("Wednesday");
	  }
	  if (this.thursday) {
		  days.add("Thursday");
	  }
	  if (this.friday) {
		  days.add("Friday");
	  }
	  if (this.saturday){
		  days.add("Saturday");
	  }
	  if (this.sunday) {
		  days.add("Sunday");
	  }
	  return days;
  }
  public boolean isAutolocate() {
    return this.autolocate;
  }
  public String location() {
    return this.location;
  }
  
  public boolean save(Context c) {
    try {
      SQLiteDatabase db = new AlertsDatabase(c).getWritableDatabase();
      db.insertOrThrow("alerts", null, asContentValues());
      return true;
    } catch (SQLException e) {
      this.errorCanBeNull = e;
      return false;
    }
  }
  
  private Either<Alert> update(Context c, Calendar alertAt, boolean sunday, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, String location, boolean autolocate) {
    Alert a = new Alert(alertAt, sunday, monday, tuesday, wednesday, thursday, friday, saturday, location, autolocate);
    a.id = this.id;
    try {
      SQLiteDatabase db = new AlertsDatabase(c).getWritableDatabase();
      db.replaceOrThrow("alerts", null, a.asContentValues());
      return new Right<Alert>(a);
    } catch (SQLException e) {
      a.errorCanBeNull = e;
      return new Left<Alert>(a);
    }
  }
  
  public String errorString() {
    if (this.errorCanBeNull == null) {
      return "";
    } else {
      return this.errorCanBeNull.toString();
    }
  }
  
  private ContentValues asContentValues() {
    ContentValues cv = new ContentValues();
    cv.put("_id", this.id);
    cv.put("alert_at", formatter().format(this.alertAt.getTime()));
    cv.put("sunday", this.sunday);
    cv.put("monday", this.monday);
    cv.put("tuesday", this.tuesday);
    cv.put("wednesday", this.wednesday);
    cv.put("thursday", this.thursday);
    cv.put("friday", this.friday);
    cv.put("saturday", this.saturday);
    cv.put("location", this.location);
    cv.put("autolocate", this.autolocate);
    return cv;
  }
  
  private static SimpleDateFormat formatter() {
    return new SimpleDateFormat("kk:mm");
  }
  
  // This does not belong here.
  private static Calendar stringToCalendar(String s) {
    try {
      Date d = formatter().parse(s);
      Calendar c = Calendar.getInstance();
      c.setTime(d);
      return c;
    } catch (ParseException e) {
      return new GregorianCalendar(1970,01,01);
    }
  }
  
  public Alert.Updater updater() {
    return new Alert.Updater(this);
  }
  
  static public class Builder {
    private Calendar time;
    private List<String> repeatDays;
    private String theLocation;
    private boolean isAutolocate;

    public Builder() {
      this.time = new GregorianCalendar(1970,01,01);
      this.repeatDays = new ArrayList<String>();
      this.theLocation = "";
      this.isAutolocate = false;
    }
    
    public Builder alertAt(Calendar time) {
      this.time = time;
      return this;
    }
    
    public Builder repeatDays(List<String> days) {
      repeatDays = days;
      return this;
    }
    
    public Builder location(String loc) {
      theLocation = loc;
      return this;
    }
    
    public Builder autolocate(boolean autogps) {
      isAutolocate = autogps;
      return this;
    }
    
    public Alert build() {
      return new Alert(time,
          repeatDays.contains("Sunday"),
          repeatDays.contains("Monday"),
          repeatDays.contains("Tuesday"),
          repeatDays.contains("Wednesday"),
          repeatDays.contains("Thursday"),
          repeatDays.contains("Friday"),
          repeatDays.contains("Saturday"),
          theLocation,
          isAutolocate);
    }
  }
  
  static public class Updater {
    private Calendar time;
    private List<String> repeatDays;
    private String theLocation;
    private boolean isAutolocate;
    private Alert alert;

    public Updater(Alert a) {
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
      Log.d("blegh", repeatDays.contains("Sunday") + "");
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
    
    public Either<Alert> update(Context c) {
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
          isAutolocate);
    }
  }
}
