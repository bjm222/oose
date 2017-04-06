package com.example.yuanweizhao.announcment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yuanweizhao.announcment.GeofenceAPI.GeofenceController;
import com.example.yuanweizhao.announcment.Map.myMapFragment;
import com.example.yuanweizhao.announcment.Setting.SettingFragment;
import com.example.yuanweizhao.announcment.AnnouncementUI.Local.RegisteredEventsFragment;
import com.example.yuanweizhao.announcment.AnnouncementUI.Server.AllAnnouncementsFragment;

/**
 * This main activity for the App to control the AnnouncementUI, map and setting part
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Fragment mapFragment;
    private Fragment settingFragment;
    private static String localAnnouncementType = "Upcoming";

    /**
     * When activity first created, set intial view map fragment
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set toolbar in the top
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // three line button
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                View hView = navigationView.getHeaderView(0);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                TextView nav_first = (TextView)hView.findViewById(R.id.nav_firstname);
                nav_first.setText(prefs.getString("firstName", "New User"));
                TextView nav_last = (TextView)hView.findViewById(R.id.nav_lastname);
                nav_last.setText(prefs.getString("lastName", "Change your name at setting~"));
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // setting fragment
        settingFragment = new SettingFragment();

        // map fragment
        FragmentManager fm = getSupportFragmentManager();
        mapFragment = new myMapFragment();
        fm.beginTransaction().replace(R.id.app_bar_main_fragment_container, mapFragment).commit();
        getSupportActionBar().setTitle("Surrounding Events");
        getSupportActionBar().setElevation(0);

        // navigation drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @SuppressWarnings("StatementWithEmptyBody")

    /**
     * Implement navigation drawer's logic
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentTransaction transaction;

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_surrounding_events) {
            // Map fragment
            transaction = getSupportFragmentManager().beginTransaction();
            mapFragment = new myMapFragment();
            transaction.replace(R.id.app_bar_main_fragment_container, mapFragment).commit();
            getSupportActionBar().setTitle("Surrounding Events");

        } else if (id == R.id.nav_all_announcements) {
            // All announcements fragment
            AllAnnouncementsFragment allAnnouncementsFragment = new AllAnnouncementsFragment();
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.app_bar_main_fragment_container, allAnnouncementsFragment).commit();
            getSupportActionBar().setTitle("All Announcements");

        } else if (id == R.id.nav_registered_events) {
            // Registered Events fragment
            RegisteredEventsFragment registeredEventsFragment = new RegisteredEventsFragment();
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.app_bar_main_fragment_container, registeredEventsFragment).commit();
            getSupportActionBar().setTitle("Registered Events");

        } else if(id == R.id.nav_setting) {
            // Setting fragment
            transaction = getSupportFragmentManager().beginTransaction();
            settingFragment = new SettingFragment();
            transaction.replace(R.id.app_bar_main_fragment_container,settingFragment).commit();
            getSupportActionBar().setTitle("Setting");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * This is the call back function for save button in setting fragment
     * @param v
     */
    public void onClickSave(View v)
    {
        EditText firstName = (EditText) findViewById(R.id.setting_firstName);
        EditText lastName = (EditText) findViewById(R.id.setting_lastName);
        EditText organization = (EditText) findViewById(R.id.setting_organization);
        EditText email = (EditText) findViewById(R.id.setting_email);
        EditText phoneNumber = (EditText) findViewById(R.id.setting_phoneNumber);
        EditText range = (EditText) findViewById(R.id.setting_range);
        Switch mySwitch= (Switch) findViewById(R.id.switch1);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if(!firstName.getText().toString().isEmpty() && firstName.getText().toString().length() !=  0) {
            editor.putString("firstName", String.valueOf(firstName.getText()));
        }
        if(!lastName.getText().toString().isEmpty() && lastName.getText().toString().length() != 0) {
            editor.putString("lastName", String.valueOf(lastName.getText()));
        }
        editor.putString("organization", String.valueOf(organization.getText()));
        editor.putString("email", String.valueOf(email.getText()));
        editor.putString("phoneNumber", String.valueOf(phoneNumber.getText()));
        if(!range.getText().toString().isEmpty() && range.getText().toString().length() != 0) {
            editor.putString("range", String.valueOf(range.getText()));
        }
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    Log.i("DEBUG","ON");
                }else{
                    Log.i("DEBUG","OFF");
                }
            }
        });

        //check the current state before we display the screen
        if(mySwitch.isChecked()){
            editor.putBoolean("notification", true);
            Log.i("DEBUG","pushplease");

        }
        else {
            GeofenceController.getInstance().stopGoogleApiClient();
            editor.putBoolean("notification", false);
        }
        editor.commit();
        Toast.makeText(this, "Successfully Saved.", Toast.LENGTH_LONG).show();


    }
    public static String getLocalAnnouncementType() {
        return MainActivity.localAnnouncementType;
    }

    public static void setLocalAnnouncementType(String localAnnouncementType) {
        MainActivity.localAnnouncementType = localAnnouncementType;
    }
}
