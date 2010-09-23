package org.bostonandroid.umbrellatoday;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimePreference extends DialogPreference implements TimePicker.OnTimeChangedListener {
    protected String defaultValue;
    private String changedValue;
    private TimePicker timePicker;

    public TimePreference(Context context, AttributeSet attrs, int defStyle) {
      super(context, attrs, defStyle);
    }
    
    public TimePreference(Context context, AttributeSet attrs) {
      super(context, attrs);
    }
    
    @Override
    protected View onCreateDialogView() {
      this.timePicker = new TimePicker(getContext());
      Calendar calendar = getTime();
      timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
      timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
      timePicker.setOnTimeChangedListener(this);
      return timePicker;
    }
    
    public Calendar getTime() {
        try {
            Date date = formatter().parse(defaultValue());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return defaultTime();
        }
    }
    
    public void setTime(Calendar t) {
      this.defaultValue = formatter().format(t.getTime());
    }
    
    public static SimpleDateFormat formatter() {
      return new SimpleDateFormat("HH:mm");
    }
    
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
      return a.getString(index);
    }
    
    @Override
    protected void onSetInitialValue(boolean restoreValue, Object def) {
        if (restoreValue) {
            this.defaultValue = getPersistedString(defaultValue());
        } else {
            String value = (String) def;
            this.defaultValue = value;
            persistString(value);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        if (isPersistent()) {
            return super.onSaveInstanceState();
        } else {
            return new SavedState(super.onSaveInstanceState());
        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(state);
        } else {
            super.onRestoreInstanceState(((SavedState) state).getSuperState());
            setDate(((SavedState) state).timeValue);
        }
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
      Calendar selected = Calendar.getInstance();
      selected.set(Calendar.HOUR_OF_DAY, hourOfDay);
      selected.set(Calendar.MINUTE, minute);
      this.changedValue = formatter().format(selected.getTime());
    }

    @Override
    protected void onDialogClosed(boolean shouldSave) {
        if (shouldSave && this.changedValue != null) {
            this.defaultValue = this.changedValue;
            this.changedValue = null;
            persistString(this.changedValue);
        }
    }
    
    public static Calendar defaultTime() {
        return new GregorianCalendar(1970, 0, 1);
    }
    
    public static String defaultTimeString() {
        return formatter().format(defaultTime().getTime());
    }

    private String defaultValue() {
        if (this.defaultValue == null) {
            this.defaultValue = defaultTimeString();
        }
        return this.defaultValue;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        timePicker.clearFocus();
        onTimeChanged(timePicker, timePicker.getCurrentHour(), timePicker
                .getCurrentMinute());
    }

    public static Date getDateFor(SharedPreferences preferences, String field) {
        return stringToDate(preferences.getString(field, defaultTimeString()));
    }

    /**
     * Produces the date the user has selected for the given preference, as a
     * calendar.
     * 
     * @param preferences
     *            the SharedPreferences to get the date from
     * @param name
     *            the name of the preference to get the date from
     * @return a Calendar that the user has selected
     */
    public static Calendar getCalendarFor(SharedPreferences preferences,
            String field) {
        Date date = getDateFor(preferences, field);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public void setDate(String dateString) {
        this.defaultValue = dateString;
    }

    private static Date stringToDate(String dateString) {
        try {
            return formatter().parse(dateString);
        } catch (ParseException e) {
            return defaultTime().getTime();
        }
    }

    private static class SavedState extends BaseSavedState {
        String timeValue;
        public SavedState(Parcel p) {
          super(p);
          this.timeValue = p.readString();
        }
        public SavedState(Parcelable p) {
          super(p);
        }
        
        @Override
        public void writeToParcel(Parcel out, int flags) {
          super.writeToParcel(out, flags);
          out.writeString(timeValue);
        }
      
        @SuppressWarnings("unused")
        public static final Parcelable.Creator<SavedState> CREATOR =
          new Parcelable.Creator<SavedState>() {
          public SavedState createFromParcel(Parcel in) {
            return new SavedState(in);
          }

          public SavedState[] newArray(int size) {
            return new SavedState[size];
          }
        };
    }
  }