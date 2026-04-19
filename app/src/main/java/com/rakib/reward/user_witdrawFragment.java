package com.rakib.reward;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class user_witdrawFragment extends Fragment {

    TextView tvBalance;
    EditText etPoints, etAccount;
    Spinner spMethod;
    Button btnSubmit;
    TextView tvMoney;
    double rate = 0.0;

    String url = "https://varadibo.net/reward/user_withdraw_request.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_witdraw, container, false);

        tvBalance = view.findViewById(R.id.tvBalance);
        etPoints = view.findViewById(R.id.etPoints);
        etAccount = view.findViewById(R.id.etAccount);
        spMethod = view.findViewById(R.id.spMethod);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        tvMoney = view.findViewById(R.id.tvMoney);

        loadBalance();
        String[] methods = {"bKash", "Nagad", "Rocket"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                methods
        );

        spMethod.setAdapter(adapter);
        btnSubmit.setOnClickListener(v -> submitWithdraw());
        etPoints.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String input = s.toString();

                if (!input.isEmpty() && rate > 0) {

                    try {
                        double points = Double.parseDouble(input);
                        double money = points * rate;

                        tvMoney.setText("৳ " + String.format("%.2f", money));

                    } catch (Exception e) {
                        tvMoney.setText("৳ 0.00");
                    }

                } else {
                    tvMoney.setText("৳ 0.00");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        return view;
    }

    private void loadBalance() {

        SharedPreferences sp = getActivity().getSharedPreferences("user", 0);
        String userId = sp.getString("id", "");

        StringRequest request = new StringRequest(Request.Method.POST,
                "https://varadibo.net/reward/user_dashboard.php",
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONObject data = obj.getJSONObject("data");

                        int total = data.getInt("total_points");
                        rate = data.getDouble("rate"); // 🔥 FIXED

                        tvBalance.setText("Balance: " + total + " Points");

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

    private void submitWithdraw() {

        SharedPreferences sp = getActivity().getSharedPreferences("user", 0);
        String userId = sp.getString("id", "");

        String pointsStr = etPoints.getText().toString().trim();
        String account = etAccount.getText().toString().trim();
        String method = spMethod.getSelectedItem().toString();

        // 🔥 VALIDATION
        if (pointsStr.isEmpty()) {
            etPoints.setError("Enter points");
            return;
        }

        if (account.isEmpty()) {
            etAccount.setError("Enter account number");
            return;
        }

        int points;

        try {
            points = Integer.parseInt(pointsStr);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Invalid points", Toast.LENGTH_SHORT).show();
            return;
        }

        if (points <= 0) {
            Toast.makeText(getActivity(), "Points must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false); // 🔥 prevent double click

        StringRequest request = new StringRequest(Request.Method.POST,
                url,
                response -> {

                    Log.e("Response",response);

                    btnSubmit.setEnabled(true); // enable again

                    try {
                        JSONObject obj = new JSONObject(response);

                        boolean status = obj.getBoolean("status");
                        String message = obj.getString("message");

                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                        if (status) {
                            // 🔥 clear fields after success
                            etPoints.setText("");
                            etAccount.setText("");
                            tvMoney.setText("৳ 0.00");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Response error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    btnSubmit.setEnabled(true);
                    Toast.makeText(getActivity(), "Server error", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> map = new HashMap<>();
                map.put("user_id", userId);
                map.put("points", String.valueOf(points));
                map.put("payment_method", method);
                map.put("account_number", account);

                return map;
            }
        };

        Volley.newRequestQueue(getActivity()).add(request);
    }
}
