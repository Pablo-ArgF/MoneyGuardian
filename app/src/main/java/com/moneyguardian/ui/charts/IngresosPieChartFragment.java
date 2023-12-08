package com.moneyguardian.ui.charts;

import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.core.content.res.ResourcesCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.moneyguardian.R;
import com.moneyguardian.modelo.Gasto;
import com.moneyguardian.util.GastosUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IngresosPieChartFragment extends AbstractChartFragment {

    private LinearLayout msgNoIngresos;
    private PieChart chart;
    private List<PieEntry> entriesIngresos = new ArrayList<>();
    private Map<String, Float> acc = new HashMap<>();

    public static IngresosPieChartFragment newInstance(List<Gasto> param1) {
        IngresosPieChartFragment fragment = new IngresosPieChartFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(AbstractChartFragment.DATOS, new ArrayList<>(param1));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_ingresos_pie_chart, container, false);
        this.msgNoIngresos =(LinearLayout) root.findViewById(R.id.msg_no_ingresos);
        this.chart = root.findViewById(R.id.pieChartIngresos);
        return root;
    }

    @Override
    public void updateUI() {

        //this if configures the text colors to be the ones of the theme
        int color = 0;
        boolean couldGetColor = false;
        if (getContext()!= null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            TypedValue val = new TypedValue();
            getContext().getTheme().resolveAttribute(androidx.appcompat.R.attr.colorAccent,val,true);
            color = val.data;
            couldGetColor = true;
        }

        if(getContext() == null)
            return;

        float textSize = 12;


        entriesIngresos.clear();
        acc.clear();

        List<Gasto> array = datos.stream()
                .sorted((g, g1) -> g.getFechaCreacionAsDate().compareTo(g1.getFechaCreacionAsDate()))
                .collect(Collectors.toList());

        for(Gasto dato : array){
            if(dato.getBalance() > 0){
                if(acc.containsKey(dato.getCategoria()))
                    //- porque el balance es negativo
                    acc.put(dato.getCategoria(),acc.get(dato.getCategoria())  + dato.getBalance());
                else
                    acc.put(dato.getCategoria(),dato.getBalance());

            }
        }

        //we create the pieEntries
        for(String category : acc.keySet()){
            PieEntry entry = new PieEntry(acc.get(category), category);
            entriesIngresos.add(entry);
            //we indicate the icon for each cat
            entry.setIcon(getResources().getDrawable(GastosUtil.getImageFor(category)));
        }


        PieDataSet set = new PieDataSet(entriesIngresos,"");
        set.setValueTextSize(textSize);
        set.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        set.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        set.setColors(ColorTemplate.LIBERTY_COLORS);
        if(couldGetColor) {
            set.setValueTextColor(color); //value
            chart.setEntryLabelColor(color); //name of the cat
        }
        PieData data = new PieData(set);


        chart.getDescription().setText("");
        chart.setEntryLabelTextSize(textSize);

        chart.setData(data);
        chart.setCenterText(getResources().getString(R.string.center_pie_chart_ingresos));
        chart.setCenterTextSize(18);
        chart.animateXY(800,300);



        if(couldGetColor)
            chart.getLegend().setTextColor(color);
        chart.getLegend().setEnabled(false);



        chart.invalidate(); // refresh
    }

}