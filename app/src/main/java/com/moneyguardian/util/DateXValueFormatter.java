package com.moneyguardian.util;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class is used to map the x values of graphs from milis to date
 */
public class DateXValueFormatter extends ValueFormatter {

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        Date d = new Date((long) value);
        return new SimpleDateFormat("dd/MM/YY").format(d);
    }
}
