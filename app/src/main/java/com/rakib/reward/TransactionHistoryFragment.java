package com.rakib.reward;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionHistoryFragment extends Fragment {

    RecyclerView recyclerView;
    TransactionAdapter adapter;
    List<TransactionModel> list = new ArrayList<>();

    LinearLayout allUserContainer;

    ImageView btnBack;
    EditText etSearch;
    Spinner spFilter;

    String selectedFilter = "all";
    String userId = "";

    String url = "https://varadibo.net/reward/get_transactions.php";

    RequestQueue requestQueue;

    Handler handler = new Handler();
    Runnable searchRunnable;

    List<String> filters = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_transaction_history, container, false);

        btnBack = view.findViewById(R.id.btnBack);
        etSearch = view.findViewById(R.id.etSearch);
        recyclerView = view.findViewById(R.id.rvHistory);
        spFilter = view.findViewById(R.id.spFilter);
        allUserContainer = view.findViewById(R.id.allUserContainer);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter(list);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(requireContext());

        // user id (optional)
        if (getArguments() != null) {
            userId = getArguments().getString("user_id", "");
        }

        // filters
        filters.add("all");
        filters.add("add");
        filters.add("deduct");
        filters.add("withdraw");

// 🔥 ADD THIS
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                filters
        );
        spFilter.setAdapter(spinnerAdapter);
        // back
        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );
        spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedFilter = filters.get(position);

                loadData(
                        etSearch.getText().toString().trim(),
                        selectedFilter
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        // search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () ->
                        loadData(s.toString().trim(), selectedFilter);

                handler.postDelayed(searchRunnable, 500);
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        loadData("", "all");

        return view;
    }

    // =========================
    // LOAD DATA
    // =========================
    private void loadData(String search, String filter){

        SharedPreferences sp = requireActivity().getSharedPreferences("user", 0);
        String adminId = sp.getString("id", "");
        String token = sp.getString("token", "");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    Log.e("TXN_RESPONSE", response);

                    try {

                        JSONObject obj = new JSONObject(response);

                        if(!obj.getBoolean("status")){
                            Toast.makeText(getContext(),
                                    obj.optString("message"),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String mode = obj.optString("mode");

                        JSONArray arr = obj.getJSONArray("transactions");

                        // 🔥 ALL USERS MODE
                        if(mode.equals("all")){

                            recyclerView.setVisibility(View.GONE);
                            allUserContainer.setVisibility(View.VISIBLE);

                            allUserContainer.removeAllViews();

                            for(int i=0;i<arr.length();i++){

                                JSONObject o = arr.getJSONObject(i);

                                View card = LayoutInflater.from(getContext())
                                        .inflate(R.layout.item_all_user, allUserContainer, false);

                                TextView tvName = card.findViewById(R.id.tvName);
                                TextView tvPhone = card.findViewById(R.id.tvPhone);
                                TextView tvLastTxn = card.findViewById(R.id.tvLastTxn);

                                tvName.setText(o.optString("name"));
                                tvPhone.setText(o.optString("phone"));

                                String type = o.optString("type");
                                String points = o.optString("points");

                                tvLastTxn.setText(type.toUpperCase() + " : " + points);

                                // 👉 click → open details
                                card.setOnClickListener(v -> {

                                    Bundle b = new Bundle();
                                    b.putString("user_id", o.optString("user_id"));

                                    TransactionHistoryFragment fragment = new TransactionHistoryFragment();
                                    fragment.setArguments(b);

                                    requireActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.adminFragmentContainer, fragment)
                                            .addToBackStack(null)
                                            .commit();
                                });

                                allUserContainer.addView(card);
                            }

                        }

                        // 🔥 SINGLE USER MODE
                        else{

                            allUserContainer.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            List<TransactionModel> tempList = new ArrayList<>();

                            for(int i=0;i<arr.length();i++){

                                JSONObject o = arr.getJSONObject(i);

                                tempList.add(new TransactionModel(
                                        o.optString("id"),
                                        o.optString("user_id"),
                                        o.optString("points"),
                                        o.optString("type"),
                                        o.optString("reason"),
                                        o.optString("created_at")
                                ));
                            }

                            adapter.update(tempList);
                        }

                    } catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(getContext(),
                                "Parse Error",
                                Toast.LENGTH_SHORT).show();
                    }

                },
                error -> {

                    String msg = "Unknown";

                    if(error.networkResponse != null){
                        msg = "Code: " + error.networkResponse.statusCode;
                    }

                    Toast.makeText(getContext(),
                            "Network Error: " + msg,
                            Toast.LENGTH_LONG).show();
                }
        ){
            @Override
            protected Map<String,String> getParams(){

                Map<String,String> map = new HashMap<>();
                map.put("admin_id", adminId);
                map.put("token", token);
                map.put("search", search);
                map.put("filter", filter);
                map.put("user_id", userId);

                return map;
            }
        };

        requestQueue.add(request);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (requestQueue != null) {
            requestQueue.cancelAll("TXN_API");
        }

        if (handler != null && searchRunnable != null) {
            handler.removeCallbacks(searchRunnable);
        }
    }
}