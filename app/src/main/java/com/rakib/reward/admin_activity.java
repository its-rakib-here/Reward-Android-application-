package com.rakib.reward;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class admin_activity extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);

        // 🔥 INIT BOTTOM NAV
        bottomNav = findViewById(R.id.bottomNav);

        // 🔥 DEFAULT FRAGMENT
        loadFragment(new AdminHomeFragment());

        // 🔥 NAVIGATION HANDLER
        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_admin_home) {
                loadFragment(new AdminHomeFragment());
                return true;
            }

            else if (id == R.id.nav_admin_users) {
               // loadFragment(new AdminUsersFragment());
                return true;
            }

            else if (id == R.id.nav_admin_withdraw) {
                loadFragment(new AdminWithdrawFragment());
                return true;
            }

            else if (id == R.id.nav_admin_add_points) {
                loadFragment(new AdminAddPointsFragment());
                return true;
            }

            else if (id == R.id.nav_admin_profile) {
              //  loadFragment(new AdminProfileFragment());
                return true;
            }

            return false;
        });
    }

    // 🔥 FRAGMENT LOADER
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.adminFragmentContainer, fragment)
                .commit();
    }
}