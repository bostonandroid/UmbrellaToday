package org.bostonandroid.umbrellatoday;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimePreference extends DialogPreference implements TimePicker.OnTimeChangedListener {
    protected String defaultValue;
   
    public TimePreference(Context context, AttributeSet attrs, int defStyle) {
      super(context, attrs, defStyle);
    }
    
    public TimePreference(Context context, AttributeSet attrs) {
      super(context, attrs);
    }
    
    @Override
    protected View onCreateDialogView() {
      TimePicker timePicker = new TimePicker(getContext());
      Calendar calendar = getTime();
      timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
      timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
      timePicker.setOnTimeChangedListener(this);
      return timePicker;
    }
    
    private Calendar getTime() {
      try {
        Date date = formatter().parse(defaultValue);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
      } catch (java.text.ParseException e) {
        e.printStackTrace();
        return new GregorianCalendar(1970, 1, 1);
      }
    }
    
    private SimpleDateFormat formatter() {
      return new SimpleDateFormat("HH:mm");
    }
    
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
      return a.getString(index);
    }
    
    @Override
    protected void onSetInitialValue(boolean restoreValue, Object def) {
        if (restoreValue) {
            defaultValue = getPersistedString(defaultValue);
        } else {
            String value = (String) def;
            defaultValue = value;
            persistString(value);
        }
    }
    
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
      Calendar selected = Calendar.getInstance();
      selected.set(Calendar.HOUR_OF_DAY, hourOfDay);
      selected.set(Calendar.MINUTE, minute);
      defaultValue = formatter().format(selected.getTime());
    }
  }