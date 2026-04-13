package com.rakib.reward;

import android.content.Intent;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment_user extends Fragment {

    TextView tvTotalPoints, tvTodayPoints, tvWithdraw;

    String url = "https://yourdomain.com/dashboard.php"; // 🔥 change this

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_user, container, false);

        // 🔥 INIT VIEWS
        tvTotalPoints = view.findViewById(R.id.tvTotalPoints);
        tvTodayPoints = view.findViewById(R.id.tvTodayPoints);
        tvWithdraw = view.findViewById(R.id.tvWithdraw);


        // 🔥 LOAD DATA FROM API
        loadDashboardData();

        // 🔥 BUTTON CLICK (Withdraw)
        view.findViewById(R.id.btnWithdraw).setOnClickListener(v -> {
            //startActivity(new Intent(getActivity(), WithdrawActivity.class));
        });

        // 🔥 BUTTON CLICK (History)
        view.findViewById(R.id.btnHistory).setOnClickListener(v -> {
           // startActivity(new Intent(getActivity(), HistoryActivity.class));
        });

        // 🔥 BOTTOM NAVIGATION


        return view;
    }

    // 🔥 API CALL
    private void loadDashboardData() {

        SharedPreferences sp = getActivity().getSharedPreferences("user", 0);
        String userId = sp.getString("id", "");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {

                        JSONObject obj = new JSONObject(response);

                        if(obj.getBoolean("status")){

                            JSONObject data = obj.getJSONObject("data");

                            String total = data.getString("total_points");
                            String today = data.getString("today_points");
                            String withdraw = data.getString("withdraw_status");

                            tvTotalPoints.setText(total);
                            tvTodayPoints.setText(today);
                            tvWithdraw.setText(withdraw);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Parse error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getActivity(), "Server error", Toast.LENGTH_SHORT).show()
        ){
            @Override
            protected Map<String, String> getParams(){

                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }
}