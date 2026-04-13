package com.rakib.reward;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class UserPofileFragment extends Fragment {

    TextView tvName, tvPhone;

    public UserPofileFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_pofile, container, false);

        tvName = view.findViewById(R.id.tvName);
        tvPhone = view.findViewById(R.id.tvPhone);

        // 🔐 LOAD USER DATA
        SharedPreferences sp = getActivity().getSharedPreferences("user", 0);

        String name = sp.getString("name", "");
        String phone = sp.getString("phone", "");

        tvName.setText(name);
        tvPhone.setText(phone);

        // 🔥 LOGOUT
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {

            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.apply();

            // 👉 redirect to login
            Intent intent = new Intent(getActivity(), login_activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}