package com.rakib.reward;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminUsersFragment extends Fragment {

    RecyclerView recyclerView;
    UserAdapter adapter;
    List<UserModel> list = new ArrayList<>();
    ShimmerFrameLayout shimmer;
    EditText etSearch;

    RequestQueue requestQueue;

    String url = "https://varadibo.net/reward/get_users.php";

    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_users, container, false);

        recyclerView = view.findViewById(R.id.rvUsers);
        shimmer = view.findViewById(R.id.shimmerLayout);
        etSearch = view.findViewById(R.id.etSearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new UserAdapter(requireContext(), list, user -> {

            Bundle bundle = new Bundle();
            bundle.putString("id", user.getId());
            bundle.putString("name", user.getName());
            bundle.putString("phone", user.getPhone());
            bundle.putString("points", user.getPoints());

            Fragment fragment = new UserDetailsFragment();
            fragment.setArguments(bundle);


            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.adminFragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(requireContext());

        // 🔥 shimmer start
        shimmer.startShimmer();
        shimmer.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        // 🔥 SEARCH LISTENER (DEBOUNCE)
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // cancel previous
                if(searchRunnable != null){
                    searchHandler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> loadUsers(s.toString().trim());

                searchHandler.postDelayed(searchRunnable, 500); // 🔥 500ms delay
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        loadUsers(""); // initial load

        return view;
    }

    private void loadUsers(String search) {

        SharedPreferences sp = requireActivity().getSharedPreferences("user", 0);

        String adminId = sp.getString("id", "");
        String token = sp.getString("token", "");

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    try {
                        Log.e("getuser", response);

                        JSONObject obj = new JSONObject(response);

                        if (!obj.getBoolean("status")) {
                            stopLoading();
                            Toast.makeText(requireContext(),
                                    obj.optString("message", "Failed"),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray arr = obj.getJSONArray("users");

                        List<UserModel> tempList = new ArrayList<>();

                        for (int i = 0; i < arr.length(); i++) {

                            JSONObject o = arr.getJSONObject(i);

                            String name = o.optString("name");
                            String phone = o.optString("phone");

                            if (name.isEmpty()) name = "No Name";
                            if (phone.isEmpty()) phone = "No Phone";

                            // skip invalid row
                            if(name.equals("No Name") && phone.equals("No Phone")) continue;

                            tempList.add(new UserModel(
                                    o.optString("id"),
                                    name,
                                    phone,
                                    o.optString("points")
                            ));
                        }

                        stopLoading();

                        adapter.update(tempList);

                    } catch (Exception e) {

                        stopLoading();
                        e.printStackTrace();

                        Toast.makeText(requireContext(),
                                "Parse Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                },
                error -> {
                    stopLoading();
                    Toast.makeText(requireContext(),
                            "Network Error",
                            Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> map = new HashMap<>();
                map.put("admin_id", adminId);
                map.put("token", token);
                map.put("search", search); // ✅ FIXED

                return map;
            }
        };

        request.setTag("USER_API"); // 🔥 important
        requestQueue.add(request);
    }

    private void stopLoading(){
        shimmer.stopShimmer();
        shimmer.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (requestQueue != null) {
            requestQueue.cancelAll("USER_API"); // ✅ proper cancel
        }

        if(searchHandler != null && searchRunnable != null){
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}