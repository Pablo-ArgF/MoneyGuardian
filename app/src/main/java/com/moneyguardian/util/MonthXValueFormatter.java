package com.moneyguardian.util;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is used to map the x values of graphs from milis to date
 */
public class MonthXValueFormatter extends ValueFormatter {

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        //from 2023.10 ---> 10/2023
        String s = String.valueOf(value);
        String[] parts = s.split("\\.");
        return parts[1]+ "/" + parts[0];
    }
}
