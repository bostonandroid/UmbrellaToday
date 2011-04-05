package org.bostonandroid.umbrellatoday;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
  private boolean enabled;
  
  private static final String[] WEEKDAYS = {
    "Sunday",
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday" };

  public static Cursor all(Context c) {
    SQLiteDatabase db = UmbrellaTodayApplication.getAlertsDatabase(c).getReadableDatabase();
    Cursor x = db.query("alerts", null, null, null, null, null, null);
    return x;
  }

  public static Cursor enabled(Context c) {
    SQLiteDatabase db = UmbrellaTodayApplication.getAlertsDatabase(c).getReadableDatabase();
    Cursor x = db.query("alerts", null, "enabled", null, null, null, null);
    return x;
  }
  
  public static Maybe<SavedAlert> find(Context context, long id) {
    return SavedAlert.find(context, id);
  }

  public static Maybe<SavedAlert> find(Cursor c) {
    return SavedAlert.find(c);
  }

  public static boolean isEmpty(Context c) {
    Cursor cursor = all(c);
    cursor.moveToFirst();
    int count = cursor.getCount();
    cursor.close();
    Log.i("Alert", "alert count: "+count+"");
    return count == 0;
  }

  Alert(Calendar alertAt, boolean sunday, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, String location, boolean autolocate, boolean enabled) {
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
    this.enabled = enabled;
  }

  public boolean isRepeating() {
    return this.monday || this.tuesday || this.wednesday || this.thursday || this.friday || this.saturday || this.sunday;
  }

  public Calendar alertAt() {
    Calendar now = new GregorianCalendar();
    return alertAtAux(alertAtAbsolute(now), now);
  }

  private Calendar alertAtAbsolute(Calendar now) {
    Calendar alertAt = (Calendar) now.clone();
    alertAt.set(Calendar.HOUR_OF_DAY, this.alertAt.get(Calendar.HOUR_OF_DAY));
    alertAt.set(Calendar.MINUTE, this.alertAt.get(Calendar.MINUTE));
    alertAt.clear(Calendar.SECOND);
    alertAt.clear(Calendar.MILLISECOND);
    return alertAt;
  }

  private Calendar alertAtAux(Calendar alertAt, Calendar now) {
    if (isRepeating())
      if (repeatsFor(now))
        if (alertAt.after(now))
          return alertAt;
        else
          return alertAtRepeating(alertAt);
      else
        return alertAtRepeating(alertAt);
    else if (alertAt.after(now))
      return alertAt;
    else
      return nextDay(alertAt);
  }

  private boolean repeatsFor(Calendar c) {
    return repeatDays().contains(WEEKDAYS[c.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY]);
  }
    
  private Calendar alertAtRepeating(Calendar c) {
    return alertAtRepeatingAux(nextDay(c));
  }
  
  private Calendar alertAtRepeatingAux(Calendar c) {
    if (repeatsFor(c))
      return c;
    else
      return alertAtRepeatingAux(nextDay(c));
  }
  
  private Calendar nextDay(Calendar c) {
    Calendar nextC = (Calendar) c.clone();
    nextC.add(Calendar.DAY_OF_WEEK, 1);
    return nextC;
  }

  public List<String> repeatDays() {
    boolean[] selections = {
        this.sunday,
        this.monday,
        this.tuesday,
        this.wednesday,
        this.thursday,
        this.friday,
        this.saturday };
    List<String> days = new ArrayList<String>();
    for (int i = 0; i < 7; i++) {
      if (selections[i])
        days.add(WEEKDAYS[i]);
    }
    return days;
  }

  public boolean isAutolocate() {
    return this.autolocate;
  }
  
  public String location() {
    return this.location;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public static Maybe<SavedAlert> findNextAlert(Context context) {
    return SavedAlert.findNextAlert(Alert.enabled(context));
  }

  private AlertOrError save(Context c, Runnable f) {
    SQLiteDatabase db = UmbrellaTodayApplication.getAlertsDatabase(c).getReadableDatabase();
    try {
      long id = db.insertOrThrow("alerts", null, asContentValues());
      SavedAlert a = new SavedAlert(id, this);          
      f.run();
      return new RightAlert(a);
    } catch (SQLException e) {
      Alert a = new Alert(alertAt, sunday, monday, tuesday, wednesday, thursday, friday, saturday, location, autolocate, enabled);
      a.errorCanBeNull = e;
      return new LeftAlert(a);
    }
  }
  
  public String errorString() {
    if (this.errorCanBeNull == null) {
      return "";
    } else {
      return this.errorCanBeNull.toString();
    }
  }
  
  public ContentValues asContentValues() {
    ContentValues cv = new ContentValues();
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
    cv.put("enabled", this.enabled);
    return cv;
  }
  
  public static SimpleDateFormat formatter() {
    return new SimpleDateFormat("kk:mm");
  }

  static public class Builder {
    private Calendar time;
    private List<String> repeatDays;
    private String theLocation;
    private boolean isAutolocate;
    private boolean enabled;

    public Builder() {
      this.time = new GregorianCalendar(1970,01,01);
      this.repeatDays = new ArrayList<String>();
      this.theLocation = "";
      this.isAutolocate = false;
      this.enabled = false;
    }
    
    public Builder alertAt(Calendar time) {
      this.time = time;
      return this;
    }
    
    public Builder repeatDays(List<String> days) {
      this.repeatDays = days;
      return this;
    }
    
    public Builder location(String loc) {
      this.theLocation = loc;
      return this;
    }
    
    public Builder autolocate(boolean autogps) {
      this.isAutolocate = autogps;
      return this;
    }

    public Builder enable(boolean enabled) {
      this.enabled = enabled;
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
          isAutolocate,
          enabled);
    }
    
    public AlertOrError save(Context c, Runnable f) {
      return build().save(c, f);
    }
  }
}
