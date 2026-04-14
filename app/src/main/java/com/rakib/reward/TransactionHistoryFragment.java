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

        // back
        btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

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
    private void loadData(String search, String filter) {

        SharedPreferences sp = requireActivity().getSharedPreferences("user", 0);
        String adminId = sp.getString("id", "");
        String token = sp.getString("token", "");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    Log.e("TXN_RESPONSE", response);

                    try {

                        JSONObject obj = new JSONObject(response);

                        if (!obj.getBoolean("status")) {
                            Toast.makeText(getContext(),
                                    obj.optString("message"),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray arr = obj.getJSONArray("transactions");

                        List<TransactionModel> tempList = new ArrayList<>();

                        // =========================
                        // ALL USER SUMMARY UI
                        // =========================
                        if (userId == null || userId.isEmpty()) {

                            allUserContainer.setVisibility(View.VISIBLE);
                            allUserContainer.removeAllViews();

                            HashMap<String, JSONObject> lastMap = new HashMap<>();

                            for (int i = 0; i < arr.length(); i++) {

                                JSONObject o = arr.getJSONObject(i);
                                String uid = o.optString("user_id");

                                if (!lastMap.containsKey(uid)) {
                                    lastMap.put(uid, o);
                                }

                                tempList.add(new TransactionModel(
                                        o.optString("id"),
                                        o.optString("user_id"),
                                        o.optString("points"),
                                        o.optString("type"),
                                        o.optString("reason"),
                                        o.optString("created_at")
                                ));
                            }

                            // create summary cards
                            for (JSONObject o : lastMap.values()) {

                                View card = LayoutInflater.from(getContext())
                                        .inflate(R.layout.item_user_summary, allUserContainer, false);

                                TextView name = card.findViewById(R.id.tvName);
                                TextView type = card.findViewById(R.id.tvLastType);
                                TextView points = card.findViewById(R.id.tvLastPoints);
                                TextView date = card.findViewById(R.id.tvLastDate);

                                name.setText(o.optString("name"));
                                type.setText("Last: " + o.optString("type").toUpperCase());
                                points.setText("Points: " + o.optString("points"));
                                date.setText(o.optString("created_at"));

                                allUserContainer.addView(card);
                            }

                        } else {
                            allUserContainer.setVisibility(View.GONE);

                            for (int i = 0; i < arr.length(); i++) {

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
                        }

                        adapter.update(tempList);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(),
                                "Parse Error",
                                Toast.LENGTH_SHORT).show();
                    }

                },
                error -> {

                    String msg = "Unknown Error";

                    if (error.networkResponse != null) {
                        msg = "Code: " + error.networkResponse.statusCode;
                    }

                    Toast.makeText(getContext(),
                            "Network Error: " + msg,
                            Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> map = new HashMap<>();
                map.put("admin_id", adminId);
                map.put("token", token);
                map.put("search", search);
                map.put("filter", filter);
                map.put("user_id", userId);

                return map;
            }
        };

        request.setTag("TXN_API");
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