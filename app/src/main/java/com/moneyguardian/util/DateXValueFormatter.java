package com.moneyguardian.util;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This class is used to map the x values of graphs from milis to date
 */
public class DateXValueFormatter extends IndexAxisValueFormatter {
    @Override
    public String getFormattedValue(float value) {

        // Convert float value to date string
        // Convert from seconds back to milliseconds to format time  to show to the user
        long emissionsMilliSince1970Time = (long) value;
        DateFormat simple = new SimpleDateFormat("dd/MM/yy");
        // Show time in local version
        Date timeMilliseconds = new Date(emissionsMilliSince1970Time);

        return simple.format(timeMilliseconds);
    }
}
