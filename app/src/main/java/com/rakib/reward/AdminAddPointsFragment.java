package com.rakib.reward;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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

public class AdminAddPointsFragment extends Fragment {

    EditText etPhone, etPoints, etNote;
    TextView tvUserName;

    String userId = "";
    String adminId;
    String findUserUrl = "https://varadibo.net/reward/find_user.php";
    String addPointsUrl = "https://varadibo.net/reward/add_points.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_add_points, container, false);

        etPhone = view.findViewById(R.id.etPhone);
        etPoints = view.findViewById(R.id.etPoints);
        etNote = view.findViewById(R.id.etNote);
        tvUserName = view.findViewById(R.id.tvUserName);
//        ImageView btnBack = view.findViewById(R.id.btnBack);
//
//        btnBack.setOnClickListener(v -> {
//            requireActivity().getSupportFragmentManager().popBackStack();
//        });
        SharedPreferences sp = getActivity().getSharedPreferences("user", 0);
         adminId = sp.getString("id", "");

        view.findViewById(R.id.btnSearch).setOnClickListener(v -> findUser());
        view.findViewById(R.id.btnSubmit).setOnClickListener(v -> addPoints());

        return view;
    }

    private void findUser() {

        String phone = etPhone.getText().toString().trim();

        if(TextUtils.isEmpty(phone)){
            Toast.makeText(getContext(), "Enter phone", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, findUserUrl,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        if(obj.getBoolean("status")){
                            userId = obj.getString("id");
                            String name = obj.getString("name");

                            tvUserName.setText("User: " + name);
                        }else{
                            tvUserName.setText("User not found");
                        }

                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show()
        ){
            protected Map<String,String> getParams(){
                Map<String,String> map = new HashMap<>();
                map.put("phone", phone);
                return map;
            }
        };

        Volley.newRequestQueue(getContext()).add(request);
    }

    private void addPoints() {

        String points = etPoints.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (userId.isEmpty()) {
            Toast.makeText(getContext(), "Find user first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(points)) {
            Toast.makeText(getContext(), "Enter points", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = tvUserName.getText().toString().replace("User: ", "");

        showConfirmDialog(name, points, note);
    }

    private void sendAddPointsRequest(String points, String note) {

        StringRequest request = new StringRequest(Request.Method.POST, addPointsUrl,
                response -> {
                    Log.e("Add point api response", response);
                },
                error -> Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show()
        ) {
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("user_id", userId);
                map.put("points", points);
                map.put("note", note);
                map.put("admin_id", adminId);
                return map;
            }
        };

        Volley.newRequestQueue(getContext()).add(request);
    }
    private void showConfirmDialog(String name, String points, String note) {

        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(getContext());

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_confirm_points, null);

        builder.setView(dialogView);

        androidx.appcompat.app.AlertDialog dialog = builder.create();

        TextView tvName = dialogView.findViewById(R.id.tvName);
        TextView tvPoints = dialogView.findViewById(R.id.tvPoints);
        TextView tvNote = dialogView.findViewById(R.id.tvNote);

        View btnCancel = dialogView.findViewById(R.id.btnCancel);
        View btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        tvName.setText("User: " + name);
        tvPoints.setText("Points: " + points);
        tvNote.setText("Note: " + note);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            sendAddPointsRequest(points, note);
        });

        dialog.show();
    }
}