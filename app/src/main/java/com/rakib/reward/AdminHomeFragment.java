package com.rakib.reward;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AdminHomeFragment extends Fragment {

    TextView tvPoints, tvTotalUsers, tvPending, tvAdminName, tvWithdraw, tvSystemMoney;
    MaterialCardView btnAddPoints, btnWithdraw, updatePointValue;

    String url = "https://varadibo.net/reward/admin_dashboard.php";
    String lastUpdate = "";

    RequestQueue requestQueue;
    Handler handler = new Handler();
    Runnable refreshRunnable;

    public AdminHomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_admin, container, false);

        // INIT UI
        tvPoints = view.findViewById(R.id.tvPoints);
        tvTotalUsers = view.findViewById(R.id.tvTotalUsers);
        tvPending = view.findViewById(R.id.tvPending);
        tvWithdraw = view.findViewById(R.id.tvWithdraw);
        tvSystemMoney = view.findViewById(R.id.tvSystemMoney);
        tvAdminName = view.findViewById(R.id.tvAdminName);

        btnAddPoints = view.findViewById(R.id.btnAddPoints);
        btnWithdraw = view.findViewById(R.id.btnWithdraw);
        updatePointValue = view.findViewById(R.id.updatePointValue);

        requestQueue = Volley.newRequestQueue(requireContext());

        // ADMIN NAME
        SharedPreferences sp = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
        String name = sp.getString("name", "Admin");
        tvAdminName.setText("Hello, " + name + " 👋");

        // LOAD DATA
        loadDashboard();
        startLiveUpdate();

        // NAVIGATION
        btnAddPoints.setOnClickListener(v ->
                openFragment(new AdminAddPointsFragment())
        );

        btnWithdraw.setOnClickListener(v ->
                openFragment(new AdminWithdrawFragment())
        );

        updatePointValue.setOnClickListener(v ->
                openFragment(new AdminSettingsFragment())
        );

        return view;
    }

    // =========================
    // OPEN FRAGMENT HELPER
    // =========================
    private void openFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.adminFragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    // =========================
    // DASHBOARD API
    // =========================
    private void loadDashboard() {

        SharedPreferences sp = requireActivity().getSharedPreferences("user", MODE_PRIVATE);
        String adminId = sp.getString("id", "");
        String token = sp.getString("token", "");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    try {

                        Log.e("DASHBOARD_RESPONSE", response);

                        JSONObject obj = new JSONObject(response);

                        if (!obj.optBoolean("status", false)) return;

                        JSONObject data = obj.getJSONObject("data");

                        int totalUsers = data.optInt("total_users", 0);
                        int points = data.optInt("total_points", 0);
                        int withdraw = data.optInt("total_withdraw", 0);
                        int pending = data.optInt("pending_requests", 0);
                        double systemMoney = data.optDouble("total_system_money", 0.0);

                        // 🔥 DIRECT UI UPDATE (NO runOnUiThread needed)
                        tvTotalUsers.setText(String.valueOf(totalUsers));
                        tvPoints.setText(String.valueOf(points));
                        tvWithdraw.setText(String.valueOf(withdraw));
                        tvPending.setText(String.valueOf(pending));
                        tvSystemMoney.setText("৳ " + String.format("%.2f", systemMoney));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                },
                error -> Log.e("DASHBOARD_ERROR", error.toString())
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> map = new HashMap<>();
                map.put("admin_id", adminId);
                map.put("token", token);

                return map;
            }
        };

        requestQueue.add(request);
    }
    // =========================
    // AUTO REFRESH
    // =========================
    private void startLiveUpdate() {

        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadDashboard();
                handler.postDelayed(this, 10000);
            }
        };

        handler.post(refreshRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (handler != null && refreshRunnable != null) {
            handler.removeCallbacks(refreshRunnable);
        }
    }
}