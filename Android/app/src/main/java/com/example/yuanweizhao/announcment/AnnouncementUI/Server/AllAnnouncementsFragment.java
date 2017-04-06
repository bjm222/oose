package com.example.yuanweizhao.announcment.AnnouncementUI.Server;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yuanweizhao.announcment.R;


/**
 * Implement the tab layout fragment for server announcements
 */
public class AllAnnouncementsFragment extends Fragment {

    /**
     * constructor
     */
    public AllAnnouncementsFragment() {
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
     * implement the logic for tab layout between today and past announcements
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
        View view = inflater.inflate(R.layout.fragment_all_announcements, container, false);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.all_announcements_tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Today"));
        tabLayout.addTab(tabLayout.newTab().setText("Past"));

        // initialized the tag layout with today announcements
        FragmentTransaction transaction;
        transaction = getActivity().getSupportFragmentManager().beginTransaction();
        AnnouncementServerFragment announcementServerFragment = new AnnouncementServerFragment();
        transaction.replace(R.id.all_announcements_fragment_container, announcementServerFragment).commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction transaction;
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                switch (tab.getPosition()) {
                    case 0:
                        Log.d("case0", tab.getPosition() + "");
                        AnnouncementServerFragment announcementServerFragment = new AnnouncementServerFragment();
                        transaction.replace(R.id.all_announcements_fragment_container, announcementServerFragment).commit();
                        break;
                    case 1:
                        Log.d("case1", tab.getPosition() + "");
                        AnnouncementPastServerFragment announcementPastServerFragment = new AnnouncementPastServerFragment();
                        transaction.replace(R.id.all_announcements_fragment_container, announcementPastServerFragment).commit();
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
