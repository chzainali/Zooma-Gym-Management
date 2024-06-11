package com.example.zoomagymmanagement.stepcounter;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SharedPreferencesHelper {
    private static final String PREF_NAME = "StepCounterPrefs";
    private static final String KEY_STEPS = "steps";
    private static final String KEY_DATE_TEXT = "dateText";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public int getSteps() {
        isDifferentDay();
        return sharedPreferences.getInt(KEY_STEPS, 0);
    }

    public void saveSteps(int steps) {
        editor.putInt(KEY_STEPS, steps + getSteps());
        editor.apply();
        saveDateText(getCurrentDateText());
    }

    public String getDateText() {
        return sharedPreferences.getString(KEY_DATE_TEXT, "");
    }

    public void saveDateText(String dateText) {
        editor.putString(KEY_DATE_TEXT, dateText);
        editor.apply();
    }

    public boolean isDifferentDay() {
        String savedDateText = getDateText();
        String currentDateText = getCurrentDateText();
        if (savedDateText.isEmpty()){
            return false;
        }
        if (!currentDateText.equals(savedDateText)) {
            // If the current date is different from the saved date, reset steps to zero
            saveSteps(0);
            saveDateText(currentDateText); // Update saved date text
            return true;
        }
        return false;
    }

    public String getCurrentDateText() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }
}

