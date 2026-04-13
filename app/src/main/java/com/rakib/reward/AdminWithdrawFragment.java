package com.rakib.reward;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

public class AdminWithdrawFragment extends Fragment {

    TabLayout tabLayout;
    RecyclerView recyclerView;
    ImageView btnBack;

    String currentStatus = "pending";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_withdraw, container, false);

        // INIT VIEWS
        tabLayout = view.findViewById(R.id.tabLayout);
        recyclerView = view.findViewById(R.id.rvWithdraw);
        btnBack = view.findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // DEFAULT LOAD
        loadData(currentStatus);

        // TAB LISTENER
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if(tab.getPosition() == 0){
                    currentStatus = "pending";
                }
                else if(tab.getPosition() == 1){
                    currentStatus = "approved";
                }
                else {
                    currentStatus = "rejected";
                }

                loadData(currentStatus);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // BACK BUTTON
        btnBack.setOnClickListener(v -> {

            if(requireActivity().getSupportFragmentManager().getBackStackEntryCount() > 0){
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                requireActivity().finish();
            }
        });

        return view;
    }

    // 🔥 LOAD DATA BASED ON STATUS
    private void loadData(String status) {

        Toast.makeText(getContext(),
                "Loading: " + status,
                Toast.LENGTH_SHORT).show();

        // 👉 এখানে পরে API call বসবে
        // example:
        // GET withdraw_requests.php?status=pending
    }
}