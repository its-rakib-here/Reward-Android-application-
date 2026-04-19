package com.rakib.reward;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNav);

        // 🔥 default fragment
        loadFragment(new HomeFragment_user());

        bottomNav.setOnItemSelectedListener(item -> {

            if(item.getItemId() == R.id.nav_home){
                loadFragment(new HomeFragment_user());
                return true;
            }


            else if(item.getItemId() == R.id.nav_profile){
                loadFragment(new UserPofileFragment());
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.user_dashboard, fragment)
                .commit();
    }
}