package org.bostonandroid.umbrellatoday;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Alert {
  public static Cursor all(Context c) {
    SQLiteDatabase db = new AlertsDatabase(c).getReadableDatabase();
    return db.query("alerts", null, null, null, null, null, null);
  }
  
  protected Alert(Calendar alertAt, boolean sunday, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, String location, boolean autolocate) {
  }
  
  public boolean save() {
    return false;
  }
  
  public String errorString() {
    return "";
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
