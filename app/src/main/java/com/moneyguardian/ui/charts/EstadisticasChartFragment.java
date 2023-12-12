package com.moneyguardian.ui.charts;

import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.moneyguardian.R;
import com.moneyguardian.modelo.Gasto;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class EstadisticasChartFragment extends AbstractChartFragment {

    private List<BarEntry> entriesIngresos = new ArrayList<>();
    private List<BarEntry> entriesGastos = new ArrayList<>();
    private BarChart chart;

    public static EstadisticasChartFragment newInstance(ArrayList<Gasto> param1) {
        EstadisticasChartFragment fragment = new EstadisticasChartFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(AbstractChartFragment.DATOS, new ArrayList<>(param1));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void updateUI() {
        if(getContext() == null)
            return; //avoid no attached to context exceptions
        if(datos.size() == 0)
            return; //avoids computation if no data to be shown


        float textSize = 12;
        //this if configures the text colors to be the ones of the theme
        int color = 0;
        boolean couldGetColor = false;
        if (getContext()!= null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            TypedValue val = new TypedValue();
            getContext().getTheme().resolveAttribute(androidx.appcompat.R.attr.colorAccent,val,true);
            color = val.data;
            couldGetColor = true;
        }

        //we transform the list of Gasto objects into a list of entry objects
        entriesIngresos.clear();
        entriesGastos.clear();

        List<Gasto> array = datos.stream()
                .sorted((g, g1) -> g.getFechaCreacionAsDate().compareTo(g1.getFechaCreacionAsDate()))
                .collect(Collectors.toList());
        
        //we want to group by month the expenses
        //we get an array of ALL the months between the first one and the last
        String[] months = getMonthRange(array.get(0).getFechaCreacionAsDate(), 
                array.get(array.size()-1).getFechaCreacionAsDate());
        //we initialize two arrays, one for ingresos and one for gastos
        float[] ingresos = new float[months.length];
        float[] gastos = new float[months.length];

        array.forEach(gasto -> {
            int index = getIndex(gasto.getFechaCreacionAsDate(), array.get(0).getFechaCreacionAsDate());
            if (gasto.getBalance() >= 0) {
                ingresos[index] += gasto.getBalance();
            } else {
                gastos[index] += -gasto.getBalance();
            }
        });

        //we create the entries
        for(int i = 0 ; i< months.length ; i++){
            entriesGastos.add(new BarEntry( i , gastos[i] ));
            entriesIngresos.add(new BarEntry( i , ingresos[i] ));
        }

        BarDataSet dataSetGastos = new BarDataSet(entriesGastos,getString(R.string.graph_legend_gastos));
        int colorRed = ContextCompat.getColor(getContext(),R.color.red);
        dataSetGastos.setColor(colorRed);
        dataSetGastos.setValueTextSize(textSize);

        BarDataSet dataSetIngresos = new BarDataSet(entriesIngresos,getString(R.string.graph_legend_ingresos));
        int colorGreen = ContextCompat.getColor(getContext(),R.color.green);
        dataSetIngresos.setColor(colorGreen);
        dataSetIngresos.setValueTextSize(textSize);

        float groupSpace = 0.0f;
        float barSpace = 0.05f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset

        BarData data = new BarData(dataSetGastos, dataSetIngresos);
        data.setDrawValues(false);
        data.setBarWidth(barWidth); // set the width of each bar

        chart.setData(data);

        chart.groupBars(0f
               , groupSpace, barSpace); // perform the "explicit" grouping

        chart.getDescription().setText("");

        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisRight().setAxisMinimum(0f);

        XAxis xAxis = chart.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(months.length);
        xAxis.setGranularity(1);
        xAxis.setTextSize(textSize);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        //chart.setVisibleXRangeMaximum(12); //we allow only 1 year max to be shown
        chart.animateY(750 );

        if(couldGetColor) {
            data.setValueTextColor(color);
            xAxis.setTextColor(color);
            chart.getAxisLeft().setTextColor(color);
            chart.getAxisRight().setTextColor(color);
            chart.setBorderColor(color);
            chart.getLegend().setTextColor(color);
        }

        chart.invalidate(); // refresh
    }

    private static int getIndex(Date date, Date arrayStartDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        calendar.setTime(arrayStartDate);
        int startYear = calendar.get(Calendar.YEAR);
        int startMonth = calendar.get(Calendar.MONTH) + 1;
        return (year - startYear) * 12 + month - startMonth ;
    }

    private String[] getMonthRange(Date start, Date end) {
        List<String> monthRange = new ArrayList<>();
        DateFormat formatter = new SimpleDateFormat("MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);

        while (!calendar.getTime().after(end)) {
            monthRange.add(formatter.format(calendar.getTime()));
            calendar.add(Calendar.MONTH, 1);
        }
        //we include the last month
        monthRange.add(formatter.format(calendar.getTime()));

        return monthRange.toArray(new String[0]);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.root = inflater.inflate(R.layout.fragment_estadisticas, container, false);
        chart = root.findViewById(R.id.barChart);
        chart.setDragEnabled(true);
        return root;
    }
}