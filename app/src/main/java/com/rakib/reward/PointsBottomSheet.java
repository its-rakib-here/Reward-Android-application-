package com.rakib.reward;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PointsBottomSheet extends BottomSheetDialogFragment {

    TextView tvUser;
    EditText etPoints, etNote;
    MaterialButton btnAdd, btnDeduct;

    String userId;

    public PointsBottomSheet(String userId, String userName){
        this.userId = userId;
        this.userName = userName;
    }

    String userName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_points_action, container, false);

        tvUser = view.findViewById(R.id.tvUser);
        etPoints = view.findViewById(R.id.etPoints);
        etNote = view.findViewById(R.id.etNote);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnDeduct = view.findViewById(R.id.btnDeduct);

        tvUser.setText("User: " + userName);

        btnAdd.setOnClickListener(v -> updatePoints("add"));
        btnDeduct.setOnClickListener(v -> updatePoints("deduct"));

        return view;
    }

    private void updatePoints(String type){

        String pointsStr = etPoints.getText().toString().trim();
        String reason = etNote.getText().toString().trim();

        if(pointsStr.isEmpty()){
            etPoints.setError("Enter points");
            return;
        }

        int points;

        try {
            points = Integer.parseInt(pointsStr);
        } catch (Exception e){
            etPoints.setError("Invalid number");
            return;
        }

        if(points <= 0){
            etPoints.setError("Points must be greater than 0");
            return;
        }

        SharedPreferences sp = requireActivity().getSharedPreferences("user", 0);
        String adminId = sp.getString("id", "");
        String token = sp.getString("token", "");

        String url = "https://varadibo.net/reward/update_points.php";

        StringRequest request = new StringRequest(Request.Method.POST,
                url,
                response -> {

                    try {

                        // 🔥 FULL RESPONSE LOG
                        Log.e("UPDATE_POINTS_RESPONSE", response);

                        JSONObject obj = new JSONObject(response);

                        boolean status = obj.optBoolean("status", false);
                        String message = obj.optString("message", "No message");

                        Log.e("UPDATE_POINTS_STATUS", String.valueOf(status));
                        Log.e("UPDATE_POINTS_MESSAGE", message);

                        if(status){
                            Toast.makeText(getContext(),
                                    message,
                                    Toast.LENGTH_SHORT).show();
                            dismiss();
                            Bundle result = new Bundle();
                            result.putString("user_id", userId);
                            result.putString("type", type);
                            result.putString("points", String.valueOf(points));

                            getParentFragmentManager().setFragmentResult(
                                    "POINT_UPDATED",
                                    result
                            );

                            dismiss();
                        } else {
                            Toast.makeText(getContext(),
                                    message,
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e){

                        Log.e("UPDATE_POINTS_PARSE_ERROR", e.toString());

                        Toast.makeText(getContext(),
                                "Parse Error",
                                Toast.LENGTH_SHORT).show();
                    }

                },
                error -> {

                    Log.e("UPDATE_POINTS_NETWORK_ERROR", error.toString());

                    Toast.makeText(getContext(),
                            "Network Error",
                            Toast.LENGTH_SHORT).show();
                }
        ){

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> map = new HashMap<>();

                map.put("admin_id", adminId);
                map.put("token", token);

                map.put("user_id", userId);
                map.put("points", String.valueOf(points));
                map.put("type", type); // add / deduct
                map.put("reason", reason);

                // 🔥 DEBUG LOG (request)
                Log.e("UPDATE_POINTS_REQUEST", map.toString());

                return map;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }
}