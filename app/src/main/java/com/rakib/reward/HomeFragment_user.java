package com.rakib.reward;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment_user extends Fragment {

    TextView tvTotalPoints, tvTodayPoints, tvWithdraw, tvMoney;

    String url = "https://varadibo.net/reward/user_dashboard.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_user, container, false);

        // 🔥 INIT
        tvTotalPoints = view.findViewById(R.id.tvTotalPoints);
        tvTodayPoints = view.findViewById(R.id.tvTodayPoints);
        tvWithdraw = view.findViewById(R.id.tvWithdraw);
        tvMoney = view.findViewById(R.id.tvMoney);

        // 🔥 LOAD DATA
        loadDashboardData();

        // 🔥 BUTTONS
        view.findViewById(R.id.btnWithdraw).setOnClickListener(v -> {

            Fragment withdrawFragment = new user_witdrawFragment();

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.user_dashboard, withdrawFragment) // তোমার container id
                    .addToBackStack(null)
                    .commit();
        });

//        view.findViewById(R.id.btnHistory).setOnClickListener(v ->
//
//
//                Toast.makeText(getActivity(), "History Clicked", Toast.LENGTH_SHORT).show()
//        );

        view.findViewById(R.id.btnHistory).setOnClickListener(v -> {

            Fragment transactionFragment = new User_TransactionFragment();

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.user_dashboard, transactionFragment) // তোমার container id
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void loadDashboardData() {

        SharedPreferences sp = getActivity().getSharedPreferences("user", 0);
        String userId = sp.getString("id", "");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {

                        JSONObject obj = new JSONObject(response);

                        if (!obj.getBoolean("status")) {
                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject data = obj.getJSONObject("data");

                        int totalPoints = data.getInt("total_points");
                        int todayPoints = data.getInt("today_points");
                        String withdraw = data.getString("withdraw_status");
                        double money = data.getDouble("money");

                        // 🔥 UI UPDATE
                        tvTotalPoints.setText(totalPoints + " Points");
                        tvTodayPoints.setText(String.valueOf(todayPoints));
                        tvWithdraw.setText(withdraw);
                        tvMoney.setText("৳ " + String.format("%.2f", money));

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Parse error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getActivity(), "Server error", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);

                return params;
            }
        };

        Volley.newRequestQueue(getActivity()).add(request);
    }
}