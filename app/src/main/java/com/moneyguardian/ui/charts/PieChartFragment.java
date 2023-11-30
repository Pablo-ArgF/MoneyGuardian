package com.moneyguardian.ui.charts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.moneyguardian.R;
import com.moneyguardian.modelo.Gasto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PieChartFragment extends AbstractChartFragment {

    private PieChart chart;
    private List<PieEntry> entriesGastos = new ArrayList<>();
    private Map<String, Float> acc = new HashMap<>();

    public static PieChartFragment newInstance(List<Gasto> param1) {
        PieChartFragment fragment = new PieChartFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(AbstractChartFragment.DATOS, new ArrayList<>(param1));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        this.chart = root.findViewById(R.id.pieChart);
        chart.setTouchEnabled(false);
        return root;
    }

    @Override
    public void updateUI() {
        if(getContext() == null)
            return;

        float textSize = 12;

        entriesGastos.clear();
        acc.clear();

        List<Gasto> array = datos.stream()
                .sorted((g, g1) -> g.getFechaCreacionAsDate().compareTo(g1.getFechaCreacionAsDate()))
                .collect(Collectors.toList());

        for(Gasto dato : array){
            if(dato.getBalance() < 0){
                if(acc.containsKey(dato.getCategoria()))
                    //- porque el balance es negativo
                    acc.put(dato.getCategoria(),acc.get(dato.getCategoria())  - dato.getBalance());
                else
                    acc.put(dato.getCategoria(),-dato.getBalance());

            }
        }

        //we create the pieEntries
        for(String category : acc.keySet()){
            entriesGastos.add(new PieEntry(acc.get(category), category));

        }
        //TODO meter los iconos así
        //entriesGastos.get(0).setIcon(getResources().getDrawable(R.drawable.chart_icon));

        PieDataSet set = new PieDataSet(entriesGastos,"");
        set.setValueTextSize(textSize);
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData data = new PieData(set);

        chart.getDescription().setText("");
        chart.setEntryLabelTextSize(textSize);
        chart.setData(data);
        Legend legend = chart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setTextSize(textSize);
        legend.setWordWrapEnabled(true);
        chart.setCenterText(getResources().getString(R.string.center_pie_chart));
        chart.setCenterTextSize(18);
        chart.animateXY(800,300);
        chart.invalidate(); // refresh
    }

}