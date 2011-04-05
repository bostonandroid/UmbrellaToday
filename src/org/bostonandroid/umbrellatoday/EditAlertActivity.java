package org.bostonandroid.umbrellatoday;

import java.util.Calendar;

import org.bostonandroid.timepreference.TimePreference;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class EditAlertActivity extends PreferenceActivity {
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    addPreferencesFromResource(R.xml.alert);
    setContentView(R.layout.edit_alert);
    
    long alert_id = getIntent().getExtras().getLong("alert_id");
    Alert.find(this,alert_id).perform(new ValueRunner<SavedAlert>() {
      public void run(SavedAlert alert) {
        final SavedAlert a = alert; // for the onClick
        ((CheckBoxPreference)findPreference("enable_alert")).setChecked(alert.isEnabled());
        ((TimePreference)findPreference("time")).setTime(TimePreference.formatter().format(alert.alertAt().getTime()));
        ((TimePreference)findPreference("time")).setSummary(TimePreference.summaryFormatter(EditAlertActivity.this).format(alert.alertAt().getTime()));
        ((RepeatPreference)findPreference("repeat")).setChoices(alert.repeatDays());
        ((CheckBoxPreference)findPreference("detect_location")).setChecked(alert.isAutolocate());
        ((EditTextPreference)findPreference("location")).setText(alert.location());
        
        Button nextButton = (Button)findViewById(R.id.update_alert);
        nextButton.setOnClickListener(new View.OnClickListener() {
          public void onClick(View v) {
            updateAlert(a).
              onSuccess(new ValueRunner<SavedAlert>() {
                public void run(SavedAlert a) {
                  Calendar nextAt = a.alertAt();
                  if (a.isEnabled())
                    Toast.makeText(EditAlertActivity.this, "Alert set for " + DateFormat.getDateFormat(EditAlertActivity.this).format(nextAt.getTime()) + " at " + DateFormat.getTimeFormat(EditAlertActivity.this).format(nextAt.getTime()), Toast.LENGTH_LONG).show();
                  finish();
              }}).
              onFailure(new ValueRunner<SavedAlert>() {
                public void run(SavedAlert a) {
                  Toast.makeText(getApplicationContext(),
                      "Alert update failed: "+a.errorString(),
                      Toast.LENGTH_LONG).show();
              }});
          }
        });
      }});
  }

  private Either<SavedAlert> updateAlert(SavedAlert a) {
    PreferenceManager pm = getPreferenceManager();
    SavedAlert.Updater alertUpdater = a.updater().
      alertAt(((TimePreference)pm.findPreference("time")).getTime()).
      repeatDays(((RepeatPreference)pm.findPreference("repeat")).getChoices()).
      autolocate(((CheckBoxPreference)pm.findPreference("detect_location")).isChecked()).
      location(((EditTextPreference)pm.findPreference("location")).getText()).
      enable(((CheckBoxPreference)pm.findPreference("enable_alert")).isChecked());
    /// TODO: this #save needs to be async.
    return alertUpdater.update(this, new AlarmSetter(this));
  }
}
