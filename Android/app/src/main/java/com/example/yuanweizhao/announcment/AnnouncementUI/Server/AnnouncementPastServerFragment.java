package com.example.yuanweizhao.announcment.AnnouncementUI.Server;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.yuanweizhao.announcment.R;

import java.util.Calendar;

/**
 * Implement the past server fragment with set start/end date, get past buttons and server announcement list fragment
 */
public class AnnouncementPastServerFragment extends Fragment {
    private Button setStartButton, setEndButton, getPastButton;
    private int start_year, start_month, start_day, end_year, end_month, end_day;
    private DatePickerFragment startDatePickerDialog = new DatePickerFragment();
    private DatePickerFragment endDatePickerDialog = new DatePickerFragment();

    /**
     * set the start date and end date for current time
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Calendar c = Calendar.getInstance();
        start_year = c.get(Calendar.YEAR);
        start_month = c.get(Calendar.MONTH) + 1;
        start_day = c.get(Calendar.DAY_OF_MONTH);
        end_year = c.get(Calendar.YEAR);
        end_month = c.get(Calendar.MONTH) + 1;
        end_day = c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * set the view for the fragment
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentLayout = inflater.inflate(R.layout.fragment_announcement_past_server, container, false);
        setStartButton = (Button) fragmentLayout.findViewById(R.id.setStartButton);
        setEndButton = (Button) fragmentLayout.findViewById(R.id.setEndButton);
        getPastButton = (Button) fragmentLayout.findViewById(R.id.getPastButton);
        showDialogOnButtonClick();

        return fragmentLayout;
    }

    /**
     * Implement the set start/end date and get past buttons
     */
    public void showDialogOnButtonClick() {
        setStartButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startDatePickerDialog.show(getActivity().getFragmentManager(), "DatePicker");
                    }
                }
        );
        setEndButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        endDatePickerDialog.show(getActivity().getFragmentManager(), "DatePicker");
                    }
                }
        );
        getPastButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        start_year = startDatePickerDialog.getYear();
                        start_month = startDatePickerDialog.getMonth();
                        start_day = startDatePickerDialog.getDay();
                        end_year = endDatePickerDialog.getYear();
                        end_month = endDatePickerDialog.getMonth();
                        end_day = endDatePickerDialog.getDay();
                        Toast.makeText(getContext(),"From:" + start_year + "/" + start_month + "/" + start_day + "  " + "To:" + end_year + "/" + end_month + "/" + end_day, Toast.LENGTH_SHORT).show();

                        // pass start/end date data and create server announcement list fragment here
                        FragmentTransaction transaction;
                        transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        AnnouncementServerFragment announcementServerFragment = new AnnouncementServerFragment();;
                        Bundle arg = new Bundle();
                        arg.putInt("start_year", start_year);
                        arg.putInt("start_month", start_month);
                        arg.putInt("start_day", start_day);
                        arg.putInt("end_year", end_year);
                        arg.putInt("end_month", end_month);
                        arg.putInt("end_day", end_day);
                        arg.putBoolean("isPast", true);
                        announcementServerFragment.setArguments(arg);
                        transaction.replace(R.id.announcements_past_server_fragment_container, announcementServerFragment).commit();
                    }
                }
        );
    }
}
