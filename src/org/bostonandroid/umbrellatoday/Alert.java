package org.bostonandroid.umbrellatoday;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Alert {
  private Exception errorCanBeNull;
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
    return db.query("alerts", null, null, null, null, null, null);
  }
  
  protected Alert(Calendar alertAt, boolean sunday, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, String location, boolean autolocate) {
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
  
  public String errorString() {
    if (this.errorCanBeNull == null) {
      return "";
    } else {
      return this.errorCanBeNull.toString();
    }
  }
  
  private ContentValues asContentValues() {
    ContentValues cv = new ContentValues();
    cv.put("alertAt", formatter().format(this.alertAt));
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
  
  private SimpleDateFormat formatter() {
    return new SimpleDateFormat("kk:mm");
  }
  
  static public class Builder {
    private Calendar theTime;
    private List<String> theRepeatDays;
    private String theLocation;
    private boolean theAutolocate;

    public Builder() {
      super();
    }
    
    public Builder alertAt(Calendar time) {
      theTime = time;
      return this;
    }
    
    public Builder repeatDays(List<String> days) {
      theRepeatDays = days;
      return this;
    }
    
    public Builder location(String loc) {
      theLocation = loc;
      return this;
    }
    
    public Builder autolocate(boolean autogps) {
      theAutolocate = autogps;
      return this;
    }
    
    public Alert build() {
      return new Alert(theTime,
          theRepeatDays.contains("Sunday"),
          theRepeatDays.contains("Monday"),
          theRepeatDays.contains("Tuesday"),
          theRepeatDays.contains("Wednesday"),
          theRepeatDays.contains("Thursday"),
          theRepeatDays.contains("Friday"),
          theRepeatDays.contains("Saturday"),
          theLocation,
          theAutolocate);
    }
  }
}
