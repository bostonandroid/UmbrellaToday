package org.bostonandroid.umbrellatoday;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;

public class RepeatPreference extends ListPreference {

    private LinkedHashMap<CharSequence, Boolean> currentChoices = new LinkedHashMap<CharSequence, Boolean>();
    private LinkedHashMap<CharSequence, Boolean> newChoices = new LinkedHashMap<CharSequence, Boolean>();

    public RepeatPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setEntries(CharSequence[] entries) {
        super.setEntries(new String[] { "banana" });
        Log.d("blah", "hello I am here");

        for (int i = 0; i < entries.length; i++) {
            Log.d("here", entries[i].toString());
            currentChoices.put(entries[i], false);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult && !this.newChoices.isEmpty()) {
            this.currentChoices = this.newChoices;
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
            Log.d("blah", key.toString());
            //choices[i] = currentChoices.get(key);
            choices[i] = false;
        }

        builder.setMultiChoiceItems(entries, choices,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which,
                            boolean isChecked) {
                        CharSequence key = entries[which];
                        newChoices.put(key, isChecked);
                    }
                });
    }

    public void setChoices(List<CharSequence> choices) {
        CharSequence[] entries = getEntries();
        for (int i = 0; i < entries.length; i++) {
            CharSequence entry = entries[i];
            currentChoices.put(entry, choices.contains(entry));
        }
    }

    public List<CharSequence> getChoices() {
        return null;
    }
}
