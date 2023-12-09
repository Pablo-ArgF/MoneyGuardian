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
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.moneyguardian.R;
import com.moneyguardian.modelo.Gasto;
import com.moneyguardian.util.MonthXValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class EstadisticasChartFragment extends AbstractChartFragment {

    private Map<Float,float[]> mapGastos = new HashMap<>();
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
        }//TODO meter colores

        //we transform the list of Gasto objects into a list of entry objects
        entriesIngresos.clear();
        entriesGastos.clear();
        mapGastos.clear();

        List<Gasto> array = datos.stream()
                .sorted((g, g1) -> g.getFechaCreacionAsDate().compareTo(g1.getFechaCreacionAsDate()))
                .collect(Collectors.toList());

        array.forEach( gasto ->{
            float key = getXAxisRepresentation(gasto);
            if(mapGastos.containsKey(key)){
                float[] current = mapGastos.get(key);
                //we store in it the iformation
                if(gasto.getBalance()>= 0) {//if ingreso
                    current[1] = current[1] + gasto.getBalance();
                }
                else{//if gasto
                    current[0] = current[0] +  - gasto.getBalance();
                }
                mapGastos.put(key,current);
            }
            else{ //not registered key yet
                float[] current = new float[2];
                //we store in it the iformation
                if(gasto.getBalance()>= 0) {//if ingreso
                    current[1] = current[1] + gasto.getBalance();
                }
                else{//if gasto
                    current[0] = current[0] +  - gasto.getBalance();
                }
                mapGastos.put(key,current);
            }
        });
        float maxXAxis = Float.MIN_VALUE;
        float minXAxis = Float.MAX_VALUE;
        for(Map.Entry<Float, float[]> entry : mapGastos.entrySet()){
            if(entry.getKey() > maxXAxis)
                maxXAxis = entry.getKey();
            if(entry.getKey() < minXAxis)
                minXAxis = entry.getKey();

            entriesGastos.add(new BarEntry(
                    entry.getKey()
                    ,entry.getValue()[0]));
            entriesIngresos.add(new BarEntry(
                    entry.getKey()
                    ,entry.getValue()[1]));
        }



        BarDataSet dataSetGastos = new BarDataSet(entriesGastos,getString(R.string.graph_legend_gastos));
        int colorRed = ContextCompat.getColor(getContext(),R.color.red);
        dataSetGastos.setColor(colorRed);
        dataSetGastos.setValueTextSize(textSize);

        BarDataSet dataSetIngresos = new BarDataSet(entriesIngresos,getString(R.string.graph_legend_ingresos));
        int colorGreen = ContextCompat.getColor(getContext(),R.color.green);
        dataSetIngresos.setColor(colorGreen);
        dataSetIngresos.setValueTextSize(textSize);

        float groupSpace = 0.0005f;
        float barSpace = 0.00025f; // x2 dataset
        float barWidth = 0.0045f; // x2 dataset

        BarData data = new BarData(dataSetGastos, dataSetIngresos);
        data.setBarWidth(barWidth); // set the width of each bar

        chart.setData(data);
        chart.setFitBars(true);

        chart.groupBars(minXAxis
               , groupSpace, barSpace); // perform the "explicit" grouping

        chart.getDescription().setText("");

        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);

        XAxis xAxis = chart.getXAxis();
        //xAxis.setPosition(XAxis.XAxisPosition.TOP);
        //xAxis.setCenterAxisLabels(true);
        //xAxis.setValueFormatter(new MonthXValueFormatter());

        xAxis.setAxisMinimum(minXAxis);
        xAxis.setAxisMaximum(maxXAxis + (barSpace + barWidth) *2);

        chart.invalidate(); // refresh
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

    private float getXAxisRepresentation(Gasto g){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(g.getFechaCreacionAsDate());
        int month = (calendar.get(Calendar.MONTH)+1);

        return Float.parseFloat(
                calendar.get(Calendar.YEAR)+ "." + String.format("%02d", month)
        );
    }
}