package com.moneyguardian.ui.charts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.moneyguardian.R;
import com.moneyguardian.modelo.Gasto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractChartFragment extends Fragment {
    public static final String DATOS = "DATOS";
    protected View root;

    public static  enum Filter {ALL,ONE_MONTH,THREE_MONTHS,ONE_YEAR};

    List<Gasto> datos = new ArrayList<>();//contains currently filtered data
    List<Gasto> allData = new ArrayList<>(); //contains all data
    Filter currentFilter = Filter.ALL; //current selected filter

    public AbstractChartFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            datos = getArguments().getParcelableArrayList(DATOS);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //we reset the filter to Todos
        currentFilter = Filter.ALL;
        updateFilter(currentFilter);
        updateUI();
    }


    public void updateData(List<Gasto> g){
        this.allData = new ArrayList<>(g);
        updateFilter(currentFilter);
        updateUI();

    }

    public void updateFilter(Filter newFilter){
        //we update current filter
        currentFilter = newFilter;
        switch (newFilter){
            case ONE_MONTH:
                //solo los datos del ultimo mes pasan a datos
                datos = allData.stream()
                        .filter(gasto ->
                        {
                            Calendar c = Calendar.getInstance();
                            c.add(Calendar.MONTH, -1);
                            Date oneMonthAgo = c.getTime();
                            return gasto.getFechaCreacionAsDate().after(oneMonthAgo);
                        }).collect(Collectors.toList());
                break;
            case THREE_MONTHS:
                //solo los datos de los ultimos 3 meses
                datos = allData.stream()
                        .filter(gasto ->
                        {
                            Calendar c = Calendar.getInstance();
                            c.add(Calendar.MONTH, -3);
                            Date oneMonthAgo = c.getTime();
                            return gasto.getFechaCreacionAsDate().after(oneMonthAgo);
                        }).collect(Collectors.toList());
                break;
            case ONE_YEAR:
                //solo los datos del ultimo año
                datos = allData.stream()
                        .filter(gasto ->
                        {
                            Calendar c = Calendar.getInstance();
                            c.add(Calendar.MONTH, -12);
                            Date oneMonthAgo = c.getTime();
                            return gasto.getFechaCreacionAsDate().after(oneMonthAgo);
                        }).collect(Collectors.toList());
                break;
            case ALL:
            default:
                datos = new ArrayList<>(allData);
                break;
        }
        //we update the graph after the filter
        updateUI();
    }

    public abstract void updateUI();

}
