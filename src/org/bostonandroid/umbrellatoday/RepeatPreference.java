package org.bostonandroid.umbrellatoday;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.widget.ListView;

public class RepeatPreference extends ListPreference {
    private List<String> currentChoices;
    private List<String> newChoices;

    public RepeatPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.currentChoices = new ArrayList<String>();
        this.newChoices = new ArrayList<String>();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            this.currentChoices = new ArrayList<String>(this.newChoices);
            setSummary(summary());
            this.newChoices.clear();
        }
    }

    private String summary() {
      String summary = TextUtils.join(",", this.currentChoices);
      if (summary == "")
        return defaultValue();
      else
        return summary;
    }

    protected String defaultValue() {
      return "Never";
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        super.onClick(dialogInterface, which);

        CharSequence[] entryValues = getEntryValues();
        AlertDialog alertDialog = (AlertDialog) getDialog();
        ListView listView = alertDialog.getListView();
        SparseBooleanArray a = listView.getCheckedItemPositions();

        for (int i = 0; i < entryValues.length; i++) {
            String entry = entryValues[i].toString();
            if (a.get(i)) {
                this.newChoices.add(entry);
            }
        }
    }

    public boolean[] getChoicesBoolean() {
        CharSequence[] entryValues = getEntryValues();
        boolean[] choices = new boolean[entryValues.length];
        for (int i = 0; i < entryValues.length; i++) {
            String key = entryValues[i].toString();
            choices[i] = currentChoices.contains(key);
        }
        return choices;
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        final CharSequence[] entryValues = getEntryValues();

        builder.setMultiChoiceItems(entryValues, getChoicesBoolean(),
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which,
                            boolean isChecked) {
                    }
                });

    }

    public void setChoices(List<String> choices) {
        currentChoices = choices;
    }

    public List<String> getChoices() {
        return currentChoices;
    }
}
