package com.adupp.administrator.fawazmoviedb;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fawaz on 2/22/2016.
 */
public class Utility {
    private static  String TAG = Utility.class.getSimpleName();
    public static String getPreferredSort(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.option_sort_key),
                context.getString(R.string.option_sort_popularity_value));
    }
    static String formatDate(String sReleaseDate) {
        if (!sReleaseDate.isEmpty()) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date newDate = format.parse(sReleaseDate);
                format = new SimpleDateFormat("MMM dd,yyyy");
                return format.format(newDate);
            } catch (ParseException e) {
                Log.e(TAG, "Date Parsing Error", e);
            }
        }
        return null;
    }

}
