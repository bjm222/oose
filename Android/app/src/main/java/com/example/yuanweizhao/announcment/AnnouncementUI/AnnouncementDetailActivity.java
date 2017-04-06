package com.example.yuanweizhao.announcment.AnnouncementUI;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.yuanweizhao.announcment.AnnouncementUI.Local.AnnouncementDetailLocalFragment;
import com.example.yuanweizhao.announcment.AnnouncementUI.Server.AnnouncementDetailServerFragment;
import com.example.yuanweizhao.announcment.R;

/**
 * an activity to control the switch between local and server announcement fragment
 */
public class AnnouncementDetailActivity extends AppCompatActivity {

    /**
     * call the function to add fragment to the layout container
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_detail);

        createAndAddFragment();
    }

    /**
     * create announcement detail fragment and add it to the right container
     */
    private void createAndAddFragment(){
        Intent intent = getIntent();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // show different fragment according to announcement type
        switch (intent.getExtras().getString("ANNOUNCEMENT_TYPE")) {
            case "server":
                AnnouncementDetailServerFragment AnnouncementDetailServerFragment = new AnnouncementDetailServerFragment();
                fragmentTransaction.add(R.id.activity_announcement_detail_fragment_container, AnnouncementDetailServerFragment,"AnnouncementDetailServerFragment");
                break;
            case "local":
                AnnouncementDetailLocalFragment AnnouncementDetailLocalFragment = new AnnouncementDetailLocalFragment();
                fragmentTransaction.add(R.id.activity_announcement_detail_fragment_container, AnnouncementDetailLocalFragment,"AnnouncementDetailLocalFragment");
                break;
        }
        setTitle("Announcement Detail");
        fragmentTransaction.commit();
    }

    /**
     * set the back button
     *
     * @param item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
