package com.example.yuanweizhao.announcment.Setting;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;

import com.example.yuanweizhao.announcment.R;


/**
 * A simple {@link Fragment} subclass.
 * User will see this fragment by pressing setting button in navigation drawer
 * In this fragment user can edit/save their personal information that submit to event organizer
 */
public class SettingFragment extends Fragment {

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     *  Inflate the layout for this fragment
     *  And getting data from sharedpreference and auto fill in edittext view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        EditText firstName = (EditText) view.findViewById(R.id.setting_firstName);
        EditText lastName = (EditText) view.findViewById(R.id.setting_lastName);
        EditText organization = (EditText) view.findViewById(R.id.setting_organization);
        EditText email = (EditText) view.findViewById(R.id.setting_email);
        EditText phoneNumber = (EditText) view.findViewById(R.id.setting_phoneNumber);
        EditText range = (EditText) view.findViewById(R.id.setting_range);
        Switch aSwitch= (Switch) view.findViewById(R.id.switch1);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        firstName.setText(prefs.getString("firstName", ""));
        lastName.setText(prefs.getString("lastName", ""));
        organization.setText(prefs.getString("organization", ""));
        email.setText(prefs.getString("email", ""));
        phoneNumber.setText(prefs.getString("phoneNumber", ""));
        range.setText(prefs.getString("range",""));
        aSwitch.setChecked(prefs.getBoolean("notification",true));
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
