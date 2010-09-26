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
  
  private static final String[] DAYS_MAP = new String[8];

  static {
    DAYS_MAP[Calendar.SUNDAY] = "Sunday";
    DAYS_MAP[Calendar.MONDAY] = "Monday";
    DAYS_MAP[Calendar.TUESDAY] = "Tuesday";
    DAYS_MAP[Calendar.WEDNESDAY] = "Wednesday";
    DAYS_MAP[Calendar.THURSDAY] = "Thursday";
    DAYS_MAP[Calendar.FRIDAY] = "Friday";
    DAYS_MAP[Calendar.SATURDAY] = "Saturday";
  }

  public static Cursor all(Context c) {
    SQLiteDatabase db = UmbrellaTodayApplication.getAlertsDatabase(c).getReadableDatabase();
    Cursor x = db.query("alerts", null, null, null, null, null, null);
    return x;
  }
  
  public static Maybe<SavedAlert> find(Context context, long id) {
    return SavedAlert.find(context, id);
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
  
  public boolean isRepeating() {
    return this.monday || this.tuesday || this.wednesday || this.thursday || this.friday || this.saturday || this.sunday;
  }

  public Calendar alertAt() {
    if (isRepeating())
      if (repeatsFor(this.alertAt))
        if (wasAlertedAfterNow())
          return this.alertAt;
        else
          return alertAtRepeating(this.alertAt);
      else
        return alertAtRepeating(this.alertAt);
    else if (wasAlertedAfterNow())
      return nextDay(this.alertAt);
    else
      return this.alertAt;
  }
  
  private boolean repeatsFor(Calendar c) {
    return repeatDays().contains(DAYS_MAP[c.get(Calendar.DAY_OF_WEEK)]);
  }
  
  private boolean wasAlertedAfterNow() {
    Calendar now = new GregorianCalendar();
    return this.alertAt.getTimeInMillis() > now.getTimeInMillis();
  }

  public List<String> repeatDays() {
    String[] dayStrings = {
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
    "Sunday" };
    boolean[] dayChoices = {
        this.monday,
        this.tuesday,
        this.wednesday,
        this.thursday,
        this.friday,
        this.saturday,
        this.sunday };
    List<String> days = new ArrayList<String>();
    for (int i = 0; i < 7; i++) {
      if (dayChoices[i])
        days.add(dayStrings[i]);
    }
    return days;
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
    Calendar nextC = new GregorianCalendar();
    c.setTime(c.getTime());
    nextC.add(Calendar.DAY_OF_WEEK, 1);
    return nextC;
  }

  public boolean isAutolocate() {
    return this.autolocate;
  }
  
  public String location() {
    return this.location;
  }

  public static Maybe<SavedAlert> findNextAlert(Context context) {
    return SavedAlert.findNextAlert(Alert.all(context));
  }

  private AlertOrError save(Context c) {
    SQLiteDatabase db = UmbrellaTodayApplication.getAlertsDatabase(c).getReadableDatabase();
    try {
      long id = db.insertOrThrow("alerts", null, asContentValues());
      SavedAlert a = new SavedAlert(id, this);
      return new RightAlert(a);
    } catch (SQLException e) {
      Alert a = new Alert(alertAt, sunday, monday, tuesday, wednesday, thursday, friday, saturday, location, autolocate);
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
    return cv;
  }
  
  static SimpleDateFormat formatter() {
    return new SimpleDateFormat("kk:mm");
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
    
    public AlertOrError save(Context c) {
      return build().save(c);
    }
  }
}
