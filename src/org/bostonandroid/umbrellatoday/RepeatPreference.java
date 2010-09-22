package org.bostonandroid.umbrellatoday;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;

public class RepeatPreference extends ListPreference {

    private List<String> currentChoices = new ArrayList<String>();
    private List<String> newChoices = new ArrayList<String>();

    public RepeatPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
        	this.currentChoices.addAll(this.newChoices);
            this.newChoices.clear();
        }
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        final CharSequence[] entries = getEntries();
        //CharSequence[] entryValues = getEntryValues();

        boolean[] choices = new boolean[entries.length];

        for (int i = 0; i < entries.length; i++) {
            CharSequence key = entries[i];
            choices[i] = currentChoices.contains(key);
        }

        builder.setMultiChoiceItems(entries, choices,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which,
                            boolean isChecked) {
                        String key = entries[which].toString();
                        if (isChecked) {
                        	Log.d("blah", "adding " + key);
                        	newChoices.add(key);
                        } else {
							// do something
                        }
                    }
                });
    }

    public void setChoices(List<String> choices) {
        currentChoices = choices;
    }

    public List<String> getChoices() {
    	Log.d("blah", currentChoices.size() + "");
        return currentChoices;
    }
}
