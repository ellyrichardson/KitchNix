package com.example.darkestmidnight.lykeyfoods.activities.main_navigation;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.darkestmidnight.lykeyfoods.R;
import com.example.darkestmidnight.lykeyfoods.activities.main_navigation.bottom_navigation.HomeFragment;
import com.example.darkestmidnight.lykeyfoods.activities.main_navigation.bottom_navigation.MessagesFragment;
import com.example.darkestmidnight.lykeyfoods.activities.main_navigation.bottom_navigation.NotificationsFragment;
import com.example.darkestmidnight.lykeyfoods.activities.main_navigation.bottom_navigation.SettingsFragment;
import com.example.darkestmidnight.lykeyfoods.activities.main_navigation.top_navigation.ProfileFragment;
import com.example.darkestmidnight.lykeyfoods.activities.main_navigation.top_navigation.SearchBoxFragment;
import com.example.darkestmidnight.lykeyfoods.activities.main_navigation.users_info.UserFragment;
import com.example.darkestmidnight.lykeyfoods.helpers.async.GetSearchBoxResults;

public class MainNavigation extends AppCompatActivity {

    private BottomNavigationView mBottomNav;

    // fix main layout of navigation
    ImageButton uProfileBtn, searchBox;

    GetSearchBoxResults getSearcResults;

    EditText searchBoxText;

    Fragment frag = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation);

        // hide action bar
        if(this.getSupportActionBar()!=null) {
            this.getSupportActionBar().hide();
        }

        uProfileBtn = (ImageButton) findViewById(R.id.profileBtn);
        searchBox = (ImageButton) findViewById(R.id.searchFriendBtn);

        mBottomNav = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return true;
            }
        });

        uProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // goes to the Profile Fragment
                profileFragment();
            }
        });

        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // goes to the Search Box Fragment
                searchBoxResultsFragment();
            }
        });
    }

    private void profileFragment() {
        frag = ProfileFragment.newInstance("test1", "test2");
        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragmentContainer, frag, frag.getTag());
            ft.commit();
        }
    }

    private void searchBoxResultsFragment() {
        frag = SearchBoxFragment.newInstance("test1", "test2");
        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragmentContainer, frag, frag.getTag());
            ft.commit();
        }
    }

    // so it can be called from the adapter
    public void userFragment(Bundle args) {
        frag = UserFragment.newInstance("test1", args);
        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragmentContainer, frag, frag.getTag());
            ft.commit();
        }
    }

    private void selectFragment(MenuItem item) {
        // init corresponding fragment
        switch (item.getItemId()) {
            case R.id.nav_home_btn:
                frag = HomeFragment.newInstance("test1", "test2");
                break;
           /* case R.id.nav_message_btn:
                frag = MessagesFragment.newInstance("test1", "test2");
                break;*/
            case R.id.nav_notif_btn:
                frag = NotificationsFragment.newInstance("test1", "test2");
                break;
            case R.id.nav_settings_btn:
                frag = SettingsFragment.newInstance("test1", "test2");
                break;
        }

        // uncheck the other items
        for (int i = 0; i< mBottomNav.getMenu().size(); i++) {
            MenuItem menuItem = mBottomNav.getMenu().getItem(i);
            menuItem.setChecked(menuItem.getItemId() == item.getItemId());
        }

        // sets the fragment to show
        if (frag != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragmentContainer, frag, frag.getTag());
            ft.commit();
        }
    }
}
