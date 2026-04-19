package com.rakib.reward;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User_TransactionFragment extends Fragment {

    RecyclerView recyclerView;
    User_TransactionAdapter adapter;

    List<User_TransactionModel> list = new ArrayList<>();

    String url = "https://varadibo.net/reward/user_transaction_history.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadData();

        return view;
    }

    private void loadData() {

        SharedPreferences sp = requireActivity().getSharedPreferences("user", 0);
        String userId = sp.getString("id", "");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {

                        JSONObject obj = new JSONObject(response);

                        if (!obj.getBoolean("status")) {
                            Toast.makeText(getActivity(), "No data found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray arr = obj.getJSONArray("data");

                        list.clear();

                        for (int i = 0; i < arr.length(); i++) {

                            JSONObject o = arr.getJSONObject(i);

                            String type = o.optString("type", "");
                            String title = o.optString("reason", "No title");                            String amount = o.optString("amount", "0");
                            String date = o.optString("date", "");
                            String points = o.optString("points", "0");
                            String source = o.optString("source", "");

                            list.add(new User_TransactionModel(
                                    type,
                                    title,
                                    amount,
                                    source,
                                    date,
                                    points
                            ));
                        }

                        if (adapter == null) {
                            adapter = new User_TransactionAdapter(getActivity(), list);
                            recyclerView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Parse error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getActivity(), "Server error", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", userId);
                return map;
            }
        };

        Volley.newRequestQueue(getActivity()).add(request);
    }
}