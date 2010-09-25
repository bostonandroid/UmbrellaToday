package org.bostonandroid.umbrellatoday;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
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
  
  private static final HashMap<String, Integer> DAYS_MAP = initializeDaysMap();

  private static final HashMap<String, Integer> initializeDaysMap() {
    HashMap<String, Integer> daysMap = new HashMap<String, Integer>();

    daysMap.put("Monday", Calendar.MONDAY);
    daysMap.put("Tuesday", Calendar.TUESDAY);
    daysMap.put("Wednesday", Calendar.WEDNESDAY);
    daysMap.put("Thursday", Calendar.THURSDAY);
    daysMap.put("Friday", Calendar.FRIDAY);
    daysMap.put("Saturday", Calendar.SATURDAY);
    daysMap.put("Sunday", Calendar.SUNDAY);

    return daysMap;
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
    int hour = this.alertAt.get(Calendar.HOUR_OF_DAY);
    int minute = this.alertAt.get(Calendar.MINUTE);

    Calendar c = new GregorianCalendar();
    c.setTimeInMillis(System.currentTimeMillis());

    if (hour < c.get(Calendar.HOUR_OF_DAY) ||
          hour == c.get(Calendar.HOUR_OF_DAY)
          && minute <= c.get(Calendar.MINUTE))
      c.add(Calendar.DAY_OF_WEEK, 1);

    c.set(Calendar.HOUR_OF_DAY, hour);
    c.set(Calendar.MINUTE, minute);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);

    if (isRepeating())
      return alertAtRepeating(c);
    else
      return c;
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

  public Calendar alertAtRepeating(Calendar c) {
    List<String> days = repeatDays();

    int currentDayOfWeek = c.get(Calendar.DAY_OF_WEEK);

    Iterator<String> iterator = days.iterator();
    int minDayOfWeek = c.getActualMaximum(Calendar.DAY_OF_WEEK);

    while (iterator.hasNext()) {
      String iteratorValue = iterator.next();
      int selectedDayOfWeek = DAYS_MAP.get(iteratorValue);
      if (selectedDayOfWeek <= currentDayOfWeek)
        minDayOfWeek = selectedDayOfWeek;
    }

    if (minDayOfWeek < currentDayOfWeek)
      c.add(Calendar.WEEK_OF_YEAR, 1);

    c.set(Calendar.DAY_OF_WEEK, minDayOfWeek);

    return c;
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
