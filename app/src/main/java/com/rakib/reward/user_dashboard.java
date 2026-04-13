package com.rakib.reward;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class user_dashboard extends AppCompatActivity {

    TextView tvTotalPoints, tvTodayPoints, tvWithdraw;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_dashboard), (v, insets) -> {
//
//            Insets statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars());
//
//            // শুধু top padding দাও (bottom না)
//            v.setPadding(
//                    v.getPaddingLeft(),
//                    statusBar.top,
//                    v.getPaddingRight(),
//                    v.getPaddingBottom()
//            );
//
//            return insets;
//        });
        setContentView(R.layout.activity_user_dashboard);

        // 🔹 INIT VIEWS
        tvTotalPoints = findViewById(R.id.tvTotalPoints);
        tvTodayPoints = findViewById(R.id.tvTodayPoints);
        tvWithdraw = findViewById(R.id.tvWithdraw);
        bottomNav = findViewById(R.id.bottomNav);

        // 🔐 LOAD SESSION DATA
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String name = sp.getString("name", "");

        // 👉 Example (later API দিয়ে replace করবা)
        tvTotalPoints.setText("120");
        tvTodayPoints.setText("20");
        tvWithdraw.setText("No Request");

        // 🔥 BOTTOM NAVIGATION HANDLE
        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if(id == R.id.nav_home){
                return true;
            }

            else if(id == R.id.nav_history){
                //startActivity(new Intent(this, HistoryActivity.class));
                return true;
            }

            else if(id == R.id.nav_withdraw){
                //startActivity(new Intent(this, WithdrawActivity.class));
                return true;
            }

            else if(id == R.id.nav_profile){
                //startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }

            return false;
        });

        // 🔥 BUTTON CLICK (Quick Actions)
        findViewById(R.id.btnWithdraw).setOnClickListener(v -> {
            //startActivity(new Intent(this, WithdrawActivity.class));
        });

        findViewById(R.id.btnHistory).setOnClickListener(v -> {
            //startActivity(new Intent(this, HistoryActivity.class));
        });

    }
}