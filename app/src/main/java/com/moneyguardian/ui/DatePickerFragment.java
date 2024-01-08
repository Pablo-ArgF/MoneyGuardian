package com.moneyguardian.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/**
 * Este dialog se encarga de generar un date picker con la fecha mínima puesta a hoy
 */
public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener listener;
    private long minDate;
    private boolean isMinDateSet = false;


    public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setListener(listener);
        return fragment;
    }

    public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener, long minDate) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setListener(listener);
        fragment.setMinDate(minDate);
        return fragment;
    }

    private void setMinDate(long minDate) {
        this.minDate = minDate;
        this.isMinDateSet = true;
    }


    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), listener, year, month, day);
        // Ponemos la fecha mínima a hoy
        if (isMinDateSet) {
            datePicker.getDatePicker().setMinDate(this.minDate);
        }
        return datePicker;
    }

}
