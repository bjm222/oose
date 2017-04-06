package com.example.yuanweizhao.announcment.AnnouncementUI.Server;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import java.util.Calendar;

/**
 * date picker fragment for user to pick specific date
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    final Calendar c = Calendar.getInstance();
    private int year = c.get(Calendar.YEAR);
    private int month = c.get(Calendar.MONTH);
    private int day = c.get(Calendar.DAY_OF_MONTH);

    /**
     * return the date picker dialog
     *
     * @param savedInstanceState
     * @return Dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    /**
     * set the date data according to user's input
     *
     * @param view
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return this.year;
    }

    public int getMonth() {
        return this.month + 1;
    }

    public int getDay() {
        return this.day;
    }
}
