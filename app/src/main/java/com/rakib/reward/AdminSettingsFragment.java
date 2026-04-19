package com.rakib.reward;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class AdminSettingsFragment extends Fragment {

    EditText etRate;
    MaterialButton btnUpdate;

    String GET_URL = "https://varadibo.net/reward/get_point_rate.php";
    String UPDATE_URL = "https://varadibo.net/reward/update_point_rate.php";

    String adminId, token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_settings, container, false);

        etRate = view.findViewById(R.id.etRate);
        btnUpdate = view.findViewById(R.id.btnUpdate);

        SharedPreferences sp = requireActivity().getSharedPreferences("user", 0);
        adminId = sp.getString("id", "");
        token = sp.getString("token", "");

        loadRate();

        btnUpdate.setOnClickListener(v -> updateRate());

        return view;
    }

    // 🔥 GET CURRENT RATE
    private void loadRate() {

        StringRequest request = new StringRequest(Request.Method.GET, GET_URL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        if(obj.getBoolean("status")){
                            etRate.setText(obj.getString("rate"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {}
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }

    // 🔥 UPDATE RATE
    private void updateRate() {

        String rate = etRate.getText().toString().trim();

        if(rate.isEmpty()){
            Toast.makeText(getContext(), "Enter rate", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, UPDATE_URL,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        Toast.makeText(getContext(),
                                obj.getString("message"),
                                Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show()
        ){
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> map = new HashMap<>();
                map.put("admin_id", adminId);
                map.put("token", token);
                map.put("rate", rate);

                return map;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }
}