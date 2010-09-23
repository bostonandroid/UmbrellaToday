package org.bostonandroid.umbrellatoday;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.widget.ListView;

public class RepeatPreference extends ListPreference {

    private List<String> currentChoices = new ArrayList<String>();
    private List<String> newChoices = new ArrayList<String>();

    public RepeatPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
        	this.currentChoices = new ArrayList<String>(this.newChoices);
            this.newChoices.clear();
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        super.onClick(dialogInterface, which);

        CharSequence[] entries = getEntries();
        AlertDialog alertDialog = (AlertDialog) getDialog();
        ListView listView = alertDialog.getListView();
        SparseBooleanArray a = listView.getCheckedItemPositions();

        for (int i = 0; i < entries.length; i++) {
            String entry = entries[i].toString();
            if (a.get(i)) {
                this.newChoices.add(entry);
            }
        }
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        final CharSequence[] entries = getEntries();
        //CharSequence[] entryValues = getEntryValues();

        boolean[] choices = new boolean[entries.length];

        for (int i = 0; i < entries.length; i++) {
            String key = entries[i].toString();
            choices[i] = currentChoices.contains(key);
        }

        builder.setMultiChoiceItems(entries, choices,
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
