package com.moneyguardian.ui.charts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.moneyguardian.R;
import com.moneyguardian.modelo.Gasto;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractChartFragment extends Fragment {
    public static final String DATOS = "DATOS";

    List<Gasto> datos = new ArrayList<>();
    int datasetSize;

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

    public void setDatasetSize(int size){
        this.datasetSize = size;
    }

    public void addData(Gasto g){
        this.datos.add(g);
        if(this.datos.size() == datasetSize)
            updateUI();
    }

    public abstract void updateUI();

    public abstract void reloadGraph();

}
