package com.rakib.reward;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class AdminHomeFragment extends Fragment {

    TextView tvUsers, tvPoints, tvWithdraw, tvPending;

    public AdminHomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_admin, container, false);

        tvUsers = view.findViewById(R.id.tvUsers);
        tvPoints = view.findViewById(R.id.tvPoints);
        tvWithdraw = view.findViewById(R.id.tvWithdraw);
        tvPending = view.findViewById(R.id.tvPending);

        // 🔥 TEMP DATA (API replace later)
        tvUsers.setText("120");
        tvPoints.setText("45000");
        tvWithdraw.setText("25");
        tvPending.setText("8");

        return view;
    }
}