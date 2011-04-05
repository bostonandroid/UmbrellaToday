package org.bostonandroid.umbrellatoday;

import java.util.Calendar;

import org.bostonandroid.timepreference.TimePreference;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NewAlertActivity extends PreferenceActivity {
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    addPreferencesFromResource(R.xml.alert);
    setContentView(R.layout.new_alert);
    
    Button nextButton = (Button)findViewById(R.id.save_alert);
    nextButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        saveAlert().
          onSuccess(new ValueRunner<SavedAlert>() {
            public void run(SavedAlert a) {
              Calendar nextAt = a.alertAt();
              if (a.isEnabled())
                Toast.makeText(NewAlertActivity.this, "Alert set for " + DateFormat.getDateFormat(NewAlertActivity.this).format(nextAt.getTime()) + " at " + DateFormat.getTimeFormat(NewAlertActivity.this).format(nextAt.getTime()), Toast.LENGTH_LONG).show();
              setResult(RESULT_OK);
              finish();
          }}).
          onFailure(new ValueRunner<Alert>() {
            public void run(Alert a) {
              Log.d("NewAlert", "failed to add alert: " + a.errorString());
              Toast.makeText(NewAlertActivity.this, "Alert save failed: "+a.errorString(), Toast.LENGTH_LONG).show();
          }});
      }
    });

    // Start the user off by making them pick a time
    getPreferenceScreen().onItemClick(null, null, 1, 0); // FIXME: look up position 
  }

  private AlertOrError saveAlert() {
    PreferenceManager pm = getPreferenceManager();
    Alert.Builder alertBuilder = new Alert.Builder();
    // TODO: this #save needs to be async.
    return alertBuilder.
      alertAt(((TimePreference)pm.findPreference("time")).getTime()).
      repeatDays(((RepeatPreference)pm.findPreference("repeat")).getChoices()).
      autolocate(((CheckBoxPreference)pm.findPreference("detect_location")).isChecked()).
      location(((EditTextPreference)pm.findPreference("location")).getText()).
      enable(((CheckBoxPreference)pm.findPreference("enable_alert")).isChecked()).
      save(this, new AlarmSetter(this));
  }
}
