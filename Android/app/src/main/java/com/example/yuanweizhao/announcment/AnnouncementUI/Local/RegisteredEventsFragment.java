package com.example.yuanweizhao.announcment.AnnouncementUI.Local;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yuanweizhao.announcment.MainActivity;
import com.example.yuanweizhao.announcment.R;


/**
 * Implement the tab layout fragment for local registered events
 */
public class RegisteredEventsFragment extends Fragment {

    /**
     * constructor
     */
    public RegisteredEventsFragment() {
        // Required empty public constructor
    }

    /**
     * implement when create
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * implement the logic for tab layout between upcoming events and past events
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
        View view = inflater.inflate(R.layout.fragment_registered_events, container, false);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.registered_events_tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Upcoming"));
        tabLayout.addTab(tabLayout.newTab().setText("Past"));

        // initialized the tag layout with upcoming events
        MainActivity.setLocalAnnouncementType("Upcoming");
        FragmentTransaction transaction;
        transaction = getActivity().getSupportFragmentManager().beginTransaction();
        AnnouncementLocalFragment announcementLocalFragment = new AnnouncementLocalFragment();
        transaction.replace(R.id.registered_events_container, announcementLocalFragment).commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction transaction;
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                AnnouncementLocalFragment announcementLocalFragment;
                switch (tab.getPosition()) {
                    case 0:
                        MainActivity.setLocalAnnouncementType("Upcoming");
                        announcementLocalFragment = new AnnouncementLocalFragment();
                        transaction.replace(R.id.registered_events_container, announcementLocalFragment).commit();
                        break;
                    case 1:
                        MainActivity.setLocalAnnouncementType("Past");
                        announcementLocalFragment = new AnnouncementLocalFragment();
                        transaction.replace(R.id.registered_events_container, announcementLocalFragment).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

}
