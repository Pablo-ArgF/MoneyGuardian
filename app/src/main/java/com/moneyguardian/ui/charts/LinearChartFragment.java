package com.moneyguardian.ui.charts;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.moneyguardian.R;
import com.moneyguardian.modelo.Gasto;
import com.moneyguardian.util.DateXValueFormatter;

import java.util.ArrayList;
import java.util.List;

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


        //format the xaxis to accept dates
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new DateXValueFormatter());
        xAxis.setLabelCount(datos.size());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);


        return root;
    }

    @Override
    public void updateUI() {
        //we transform the list of Gasto objects into a list of entry objects
        List<Entry> entriesIngresos = new ArrayList<>();
        List<Entry> entriesGastos = new ArrayList<>();
        datos.forEach(dato ->{
            if(dato.getBalance() > 0)
                entriesIngresos.add(new Entry(dato.getFechaCreacionAsDate().getTime(),dato.getBalance()));
            else{
                if (dato.getBalance()< 0)
                    entriesGastos.add(new Entry(dato.getFechaCreacionAsDate().getTime(),dato.getBalance()));
            }
        });
        LineData lineData = new LineData();
        LineDataSet datasetGastos = new LineDataSet(entriesGastos,getString(R.string.graph_legend_gastos));
        lineData.addDataSet(datasetGastos);
        LineDataSet datasetIngresos = new LineDataSet(entriesIngresos,getString(R.string.graph_legend_ingresos));
        lineData.addDataSet(datasetIngresos);

        chart.setData(lineData);
        chart.invalidate();
    }
}