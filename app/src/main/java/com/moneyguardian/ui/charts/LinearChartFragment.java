package com.moneyguardian.ui.charts;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.moneyguardian.R;
import com.moneyguardian.modelo.Gasto;
import com.moneyguardian.util.DateXValueFormatter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class LinearChartFragment extends AbstractChartFragment {

    private LineChart chart;

    public static LinearChartFragment newInstance(List<Gasto> param1) {
        LinearChartFragment fragment = new LinearChartFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(AbstractChartFragment.DATOS, new ArrayList<>(param1));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_linear_chart, container, false);

        this.chart = root.findViewById(R.id.lineChart);
        chart.setTouchEnabled(false);
        return root;
    }

    @Override
    public void updateUI() {
        float textSize = 12;
        DateXValueFormatter xAxisFormater = new DateXValueFormatter();

        //we transform the list of Gasto objects into a list of entry objects
        List<Entry> entriesIngresos = new ArrayList<>();
        List<Entry> entriesGastos = new ArrayList<>();
        float counter = 0;
        List<Gasto> array = datos.stream()
                .sorted((g, g1) -> g.getFechaCreacionAsDate().compareTo(g1.getFechaCreacionAsDate()))
                .collect(Collectors.toList());
        for(Gasto dato : array){
            xAxisFormater.addDate(dato.getFechaCreacion());
            if(dato.getBalance() >= 0)
                entriesIngresos.add(new Entry(counter++,dato.getBalance()));
            else{
                entriesGastos.add(new Entry(counter++,dato.getBalance()));
            }
        }
        LineData lineData = new LineData();

        LineDataSet datasetGastos = new LineDataSet(entriesGastos,getString(R.string.graph_legend_gastos));
        datasetGastos.setColor(ContextCompat.getColor(getContext(),R.color.red));
        datasetGastos.setCircleColor(ContextCompat.getColor(getContext(),R.color.red));
        lineData.addDataSet(datasetGastos);

        LineDataSet datasetIngresos = new LineDataSet(entriesIngresos,getString(R.string.graph_legend_ingresos));
        datasetIngresos.setColor(ContextCompat.getColor(getContext(),R.color.green));
        datasetIngresos.setCircleColor(ContextCompat.getColor(getContext(),R.color.green));
        lineData.addDataSet(datasetIngresos);

        lineData.setValueTextSize(textSize);

        //format the xaxis to accept dates
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(xAxisFormater);
        xAxis.setLabelCount(datos.size());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(textSize);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setLabelCount(array.size());

        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);

        //format the Yaxis
        chart.getAxisLeft().setTextSize(textSize);
        chart.getAxisRight().setEnabled(false);


        Description description = new Description();
        description.setText(getString(R.string.description_line_graph));

        chart.setMaxVisibleValueCount(7);
        chart.setDescription(description);
        chart.setData(lineData);
        chart.invalidate();
    }
}