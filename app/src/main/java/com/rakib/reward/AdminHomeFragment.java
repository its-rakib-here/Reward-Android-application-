package com.rakib.reward;

import static android.content.Context.MODE_PRIVATE;

import android.animation.ValueAnimator;
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

    TextView tvPoints, tvWithdraw, tvPending, tvAdminName, tvSystemMoney;
    MaterialCardView btnAddPoints, btnWithdraw, updatePointValue;
    String url = "https://varadibo.net/reward/admin_dashboard.php";
    String lastUpdate = "";

    RequestQueue requestQueue;
    Handler handler = new Handler();
    Runnable refreshRunnable;

    public AdminHomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_admin, container, false);

        // 🔥 INIT (MATCH WITH XML)
        tvPoints = view.findViewById(R.id.tvPoints);
        tvWithdraw = view.findViewById(R.id.tvWithdraw);
        tvPending = view.findViewById(R.id.tvPending);
        tvAdminName = view.findViewById(R.id.tvAdminName);
        tvSystemMoney = view.findViewById(R.id.tvSystemMoney);

        btnAddPoints = view.findViewById(R.id.btnAddPoints);
        btnWithdraw = view.findViewById(R.id.btnWithdraw);
        updatePointValue = view.findViewById(R.id.updatePointValue);

        requestQueue = Volley.newRequestQueue(requireContext());

        // 🔥 ADMIN NAME
        SharedPreferences sp = requireActivity().getSharedPreferences("user",MODE_PRIVATE);
        String name = sp.getString("name", "Admin");
        tvAdminName.setText("Hello, " + name + " 👋");

        // 🔥 LOAD DATA
        loadDashboard();
        startLiveUpdate();

        // 🔥 NAVIGATION
        btnAddPoints.setOnClickListener(v -> {
            Fragment fragment = new AdminAddPointsFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.adminFragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        btnWithdraw.setOnClickListener(v -> {
            Fragment fragment = new AdminWithdrawFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.adminFragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        updatePointValue.setOnClickListener(v -> {
            Fragment fragment = new AdminSettingsFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.adminFragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    // =========================
    // 🔥 LOAD DASHBOARD DATA
    // =========================
    private void loadDashboard(){

        SharedPreferences sp = requireActivity().getSharedPreferences("user", 0);
        String adminId = sp.getString("id", "");
        String token = sp.getString("token", "");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    try {

                        JSONObject obj = new JSONObject(response);
                         Log.e("totall amoutnt",response);
                        if(!obj.getBoolean("status")) return;

                        String newUpdate = obj.optString("last_update");

                        if(newUpdate.equals(lastUpdate)) return;

                        lastUpdate = newUpdate;

                        JSONObject data = obj.getJSONObject("data");

                        int points = data.optInt("total_points");
                        int withdraw = data.optInt("total_withdraw");
                        int pending = data.optInt("pending_requests");
                        double systemMoney = data.optDouble("total_system_money", 0);

                        tvSystemMoney.setText("৳ " + String.format("%.2f", systemMoney));
                        animateText(tvPoints, points);
                        animateText(tvWithdraw, withdraw);
                        animateText(tvPending, pending);

                        // 🔥 MONEY FORMAT
//                        tvSystemMoney.setText("৳ " + String.format("%.2f", systemMoney));

                    } catch (Exception e){
                        e.printStackTrace();
                    }

                },
                error -> {}
        ){
            @Override
            protected Map<String,String> getParams(){

                Map<String,String> map = new HashMap<>();
                map.put("admin_id", adminId);
                map.put("token", token);

                return map;
            }
        };

        requestQueue.add(request);
    }

    // =========================
    // 🔥 ANIMATION (IMPROVED)
    // =========================
    private void animateText(TextView tv, int value){

        int start = 0;

        try {
            start = Integer.parseInt(tv.getText().toString());
        } catch (Exception ignored){}

        ValueAnimator animator = ValueAnimator.ofInt(start, value);
        animator.setDuration(500);

        animator.addUpdateListener(animation ->
                tv.setText(String.valueOf(animation.getAnimatedValue()))
        );

        animator.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(handler != null && refreshRunnable != null){
            handler.removeCallbacks(refreshRunnable);
        }
    }

    private void startLiveUpdate(){

        refreshRunnable = new Runnable() {
            @Override
            public void run() {

                loadDashboard();
                handler.postDelayed(this, 10000);

            }
        };

        handler.post(refreshRunnable);
    }
}