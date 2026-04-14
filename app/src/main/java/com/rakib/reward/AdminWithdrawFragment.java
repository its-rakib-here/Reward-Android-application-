package com.rakib.reward;

import static java.security.AccessController.getContext;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminWithdrawFragment extends Fragment {

    TabLayout tabLayout;
    RecyclerView recyclerView;
    ImageView btnBack;
    private String lastUpdate = "";

    private Handler handler = new Handler();
    private Runnable runnable;
    String currentStatus = "pending";
    SharedPreferences sp ; ;

    String adminId ;
    String token ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_withdraw, container, false);

        // INIT VIEWS
        tabLayout = view.findViewById(R.id.tabLayout);
        recyclerView = view.findViewById(R.id.rvWithdraw);
        btnBack = view.findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        sp=getActivity().getSharedPreferences("user", 0);
        adminId = sp.getString("id", "");
        token = sp.getString("token", "");

        // DEFAULT LOAD
        loadData(currentStatus);


        // TAB LISTENER
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                // 🔥 ALWAYS STOP OLD LOOP FIRST
                stopSmartRefresh();

                if(tab.getPosition() == 0){
                    currentStatus = "pending";
                }
                else if(tab.getPosition() == 1){
                    currentStatus = "approved";
                }
                else {
                    currentStatus = "rejected";
                }

                // 🔥 load once immediately
                loadData(currentStatus);

                // 🔥 start ONE clean loop
                startSmartRefresh(currentStatus);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // BACK BUTTON
        btnBack.setOnClickListener(v -> {

            if(requireActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                requireActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        stopSmartRefresh();

        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
    // 🔥 LOAD DATA BASED ON STATUS
    private void loadData(String status) {

        if (!isAdded()) return; // 🔥 fragment safe check

        String url = "https://varadibo.net/reward/get_withdraw_all.php";

        List<WithdrawRequest> list = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    try {
                        Log.e("request", response);

                        JSONObject obj = new JSONObject(response);

                        if (!obj.getBoolean("status")) {
                            Toast.makeText(requireContext(),
                                    obj.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONObject data = obj.getJSONObject("data");
                        JSONArray arr = data.getJSONArray(status);

                        list.clear();

                        for (int i = 0; i < arr.length(); i++) {

                            JSONObject o = arr.getJSONObject(i);

                            list.add(new WithdrawRequest(
                                    o.optString("id"),
                                    o.optString("name"),
                                    o.optString("phone"),
                                    o.optString("amount"),
                                    o.optString("points"),
                                    o.optString("account_number"),
                                    o.optString("status")
                            ));
                        }

                        if (recyclerView.getAdapter() == null) {
                            recyclerView.setAdapter(
                                    new WithdrawAdapter(list, this::processRequest)
                            );
                        } else {
                            ((WithdrawAdapter) recyclerView.getAdapter()).updateList(list);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                },
                error -> Toast.makeText(requireContext(),
                        "Network Error",
                        Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> map = new HashMap<>();
                map.put("admin_id", adminId);
                map.put("token", token);
                return map;
            }
        };

        // 🔥 SAFE CONTEXT HERE
        RequestQueue queue = Volley.newRequestQueue(requireContext().getApplicationContext());
        queue.add(request);
    }


    private void updateWithdraw(String id, String status){

        SharedPreferences sp = getActivity().getSharedPreferences("user", 0);

        String adminId = sp.getString("id", "");
        String token = sp.getString("token", "");

        StringRequest request = new StringRequest(Request.Method.POST,
                "https://varadibo.net/reward/update_withdraw.php",
                response -> {
                    Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT).show();
                    loadData(currentStatus);
                },
                error -> Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show()
        ){
            protected Map<String,String> getParams(){
                Map<String,String> map = new HashMap<>();
                map.put("request_id", id);
                map.put("status", status);
                map.put("admin_id", adminId);
                map.put("token", token);
                return map;
            }
        };

        Volley.newRequestQueue(getContext()).add(request);
    }



    private void startSmartRefresh(String status) {

        stopSmartRefresh();

        runnable = new Runnable() {
            @Override
            public void run() {

                if (!isAdded()) return; // 🔥 IMPORTANT

                loadData(status);

                handler.postDelayed(this, 20000);
            }
        };

        handler.postDelayed(runnable, 20000);
    }

    private void checkUpdates(String status) {

        String url = "https://varadibo.net/reward/get_withdraw_all.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    try {

                        JSONObject obj = new JSONObject(response);

                        String newUpdate = obj.getString("last_update");

                        // 🔥 ONLY UPDATE IF CHANGED
                        if (!newUpdate.equals(lastUpdate)) {

                            lastUpdate = newUpdate;

                            JSONObject data = obj.getJSONObject("data");
                            JSONArray arr = data.getJSONArray(status);

//                            updateList(arr); // your adapter update
                            ((WithdrawAdapter) recyclerView.getAdapter()).updateList((List<WithdrawRequest>) arr);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                },
                error -> {}
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("admin_id", adminId);
                map.put("token", token);
                return map;
            }
        };

        Volley.newRequestQueue(getContext()).add(request);
    }
    private void stopSmartRefresh() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }








    private void processRequest(int requestId, String action){

        String url = "https://varadibo.net/reward/process_withdraw.php";

        SharedPreferences sp = getActivity().getSharedPreferences("user", 0);
        String adminId = sp.getString("id", "");
        String token = sp.getString("token", "");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    try {
                        JSONObject obj = new JSONObject(response);

                        Toast.makeText(getContext(),
                                obj.getString("message"),
                                Toast.LENGTH_SHORT).show();

                        if(obj.getBoolean("status")){
                            loadData(currentStatus); // refresh list
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                },
                error -> Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show()
        ){
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> map = new HashMap<>();
                map.put("admin_id", adminId);
                map.put("token", token);
                map.put("request_id", String.valueOf(requestId));
                map.put("action", action);

                return map;
            }
        };

        Volley.newRequestQueue(getContext()).add(request);
    }



}