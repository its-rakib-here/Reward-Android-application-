package com.rakib.reward;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class TransactionHistoryFragment extends Fragment {

    RecyclerView recyclerView;
    TransactionAdapter adapter;
    List<TransactionModel> list = new ArrayList<>();

    LinearLayout allUserContainer;
    EditText etSearch;
    Spinner spFilter;

    String selectedFilter = "all";
    String userId = "";

    String url = "https://varadibo.net/reward/get_transactions.php";

    RequestQueue requestQueue;
    Handler handler = new Handler();
    Runnable searchRunnable;

    List<String> filters = Arrays.asList("all", "add", "deduct", "withdraw");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_transaction_history, container, false);

        etSearch = view.findViewById(R.id.etSearch);
        recyclerView = view.findViewById(R.id.rvHistory);
        spFilter = view.findViewById(R.id.spFilter);
        allUserContainer = view.findViewById(R.id.allUserContainer);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TransactionAdapter(list);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(requireContext());

        if (getArguments() != null) {
            userId = getArguments().getString("user_id", "");
        }

        // =====================
        // SPINNER
        // =====================
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                filters
        );
        spFilter.setAdapter(spinAdapter);

        spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedFilter = filters.get(position);
                loadData(etSearch.getText().toString().trim(), selectedFilter);
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // =====================
        // SEARCH
        // =====================
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (searchRunnable != null) handler.removeCallbacks(searchRunnable);

                searchRunnable = () ->
                        loadData(s.toString().trim(), selectedFilter);

                handler.postDelayed(searchRunnable, 400);
            }

            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        loadData("", "all");

        return view;
    }

    // =====================
    // API CALL
    // =====================
    private void loadData(String search, String filter){

        SharedPreferences sp = requireActivity().getSharedPreferences("user", 0);
        String adminId = sp.getString("id", "");
        String token = sp.getString("token", "");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    Log.e("history response", response);

                    try {

                        JSONObject obj = new JSONObject(response);

                        if(!obj.getBoolean("status")) return;

                        JSONArray arr = obj.getJSONArray("transactions");

                        // 🔥 CLEAR OLD DATA
                        list.clear();

                        for (int i = 0; i < arr.length(); i++) {

                            JSONObject o = arr.getJSONObject(i);

                            list.add(new TransactionModel(
                                    o.optString("id"),
                                    o.optString("user_id"),
                                    o.optString("points"),
                                    o.optString("type"),
                                    o.optString("reason"),
                                    o.optString("created_at"),
                                    o.optString("name"),
                                    o.optString("phone"),
                                    o.optString("amount")   // ✅ FIX HERE
                            ));
                        }

                        // 🔥 ONLY ONE UPDATE
                        adapter.notifyDataSetChanged();

                    } catch (Exception e){
                        e.printStackTrace();
                        Log.e("TXN_PARSE", "Error parsing");
                    }

                },
                error -> Log.e("TXN", "Network Error")
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
}