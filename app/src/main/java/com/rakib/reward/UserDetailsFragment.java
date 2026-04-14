package com.rakib.reward;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class UserDetailsFragment extends Fragment {

    TextView tvName, tvPhone, tvStatus, tvPoints;
    MaterialButton btnupdatePoint, btnDeductPoints, btnHistory;
    ImageView btnBack;

    String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_user_details, container, false);

        // INIT VIEWS
        tvName = view.findViewById(R.id.tvName);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvPoints = view.findViewById(R.id.tvPoints);

        btnupdatePoint = view.findViewById(R.id.btnupdatePoint);
        btnHistory = view.findViewById(R.id.btnHistory);

        btnBack = view.findViewById(R.id.btnBack);

        getParentFragmentManager().setFragmentResultListener(
                "POINT_UPDATED",
                this,
                (requestKey, result) -> {

                    String userId = result.getString("user_id");

                    // 🔥 refresh UI or API call
                    loadUserDetails(userId);

                }
        );
        // GET DATA FROM BUNDLE
        Bundle b = getArguments();

        if (b != null) {
            userId = b.getString("id");
            tvName.setText(b.getString("name"));
            tvPhone.setText(b.getString("phone"));
            tvPoints.setText(b.getString("points"));
        }

        // 🔙 BACK ACTION
        btnBack.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });

        // ➕ ADD POINTS
        btnupdatePoint.setOnClickListener(v -> {
            new PointsBottomSheet(userId, b.getString(("name")))
                    .show(getParentFragmentManager(), "points");
        });

        // ➖ DEDUCT POINTS
//        btnDeductPoints.setOnClickListener(v -> {
//            Toast.makeText(getContext(), "Deduct Points Clicked", Toast.LENGTH_SHORT).show();
//        });

        // 📜 HISTORY
        btnHistory.setOnClickListener(v -> {

            Fragment fragment = new TransactionHistoryFragment(); // ✅ create

            Bundle bundle = new Bundle();
            bundle.putString("user_id", userId); // optional (user specific history)

            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.adminFragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });


        return view;
    }

    private void loadUserDetails(String userId){

        String url = "https://varadibo.net/reward/get_user_details.php";

        StringRequest request = new StringRequest(Request.Method.POST,
                url,
                response -> {

                    try {

                        JSONObject obj = new JSONObject(response);

                        if(obj.getBoolean("status")){

                            JSONObject user = obj.getJSONObject("user");

                            tvName.setText(user.getString("name"));
                            tvPhone.setText(user.getString("phone"));
                            tvPoints.setText(user.getString("points"));

                        }

                    } catch (Exception e){
                        e.printStackTrace();
                    }

                },
                error -> {}
        ){
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> map = new HashMap<>();
                map.put("user_id", userId);

                return map;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }
}