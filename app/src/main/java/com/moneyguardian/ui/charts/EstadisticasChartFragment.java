package com.moneyguardian.ui.charts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.moneyguardian.R;


public class EstadisticasChartFragment extends AbstractChartFragment {

    @Override
    public void updateUI() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.root = inflater.inflate(R.layout.fragment_no_chart, container, false);

        return root;
    }
}