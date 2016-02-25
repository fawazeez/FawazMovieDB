package com.adupp.administrator.fawazmoviedb;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fawaz on 2/22/2016.
 */
public class Utility {
    private static  String TAG = Utility.class.getSimpleName();
    static String formatDate(String sReleaseDate) {
      try
      {SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = format.parse(sReleaseDate);
        format = new SimpleDateFormat("MMM dd,yyyy");
        return format.format(newDate);
    }catch (ParseException e)
    {
        Log.e(TAG, "Date Parsing Error", e);
    }
        return null;
    }

}
