package org.bostonandroid.umbrellatoday;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class EditAlert extends PreferenceActivity {
  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    addPreferencesFromResource(R.xml.new_alert);
    setContentView(R.layout.edit_alert);
    
    Long alert_id = (Long)getIntent().getExtras().get("alert_id");
    final Alert alert = Alert.find(this,alert_id);
    ((TimePreference)findPreference("time")).setTime(alert.alertAt());
    ((RepeatPreference)findPreference("repeat")).setChoices(alert.repeatDays());
    ((CheckBoxPreference)findPreference("detect_location")).setChecked(alert.isAutolocate());
    ((EditTextPreference)findPreference("location")).setText(alert.location());
    
    Button nextButton = (Button)findViewById(R.id.edit_alert);
    nextButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        updateAlert(alert).
          onSuccess(new EitherRunner<Alert>() {
            public void run(Alert a) {
              Calendar nextAlert = a.calculateAlert();
              Toast.makeText(getApplicationContext(),
                  "Alarm set for " + formatter().format(nextAlert.getTime()),
                  Toast.LENGTH_LONG).show();
              finish();
          }}).
          onFailure(new EitherRunner<Alert>() {
            public void run(Alert a) {
              Toast.makeText(getApplicationContext(),
                  "Alert update failed: "+a.errorString(),
                  Toast.LENGTH_LONG).show();
          }});
      }
    });
  }

  private static SimpleDateFormat formatter() {
      return new SimpleDateFormat("EEEE, MMMM d 'at' HH:mm");
  }

  private Either<Alert> updateAlert(Alert a) {
    PreferenceManager pm = getPreferenceManager();
    Alert.Updater alertUpdater = a.updater().
      alertAt(((TimePreference)pm.findPreference("time")).getTime()).
      repeatDays(((RepeatPreference)pm.findPreference("repeat")).getChoices()).
      autolocate(((CheckBoxPreference)pm.findPreference("detect_location")).isChecked()).
      location(((EditTextPreference)pm.findPreference("location")).getText());
    /// TODO: this #save needs to be async.
    return alertUpdater.update(getApplicationContext());
  }
}