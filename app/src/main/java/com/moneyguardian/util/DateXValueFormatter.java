package com.moneyguardian.util;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * This class is used to map the x values of graphs from milis to date
 */
public class DateXValueFormatter extends ValueFormatter {
    private List<String> dates = new ArrayList<>();
    private final HashMap<Double,String> mapa = new HashMap<>();

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return dates.get((int) value);
    }

    public void addDate (String s){
        this.dates.add(s);
    }

    public static float dateToNumber(Date d){
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        String month = (c.get(Calendar.MONTH) +1)+"";
        if(month.length() != 2)
            month = "0"+month;
        String s = c.get(Calendar.YEAR)+"" + month + c.get(Calendar.DAY_OF_MONTH) + "";
        return Float.valueOf(s);
    }

    public static Date numberToDate(float value){
        int v = Math.round(value);
        String s = String.valueOf(v);
        //separamos el valor en dia mes y año
        int year = Integer.parseInt(s.substring(0,5));
        int month = Integer.parseInt(s.substring(5,7));
        int day = Integer.parseInt(s.substring(7,s.length()));
        Calendar c = Calendar.getInstance();
        c.set(year,month - 1 , day);
        return c.getTime();
    }
}
