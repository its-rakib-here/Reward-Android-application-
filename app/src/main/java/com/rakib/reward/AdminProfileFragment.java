package com.rakib.reward;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AdminProfileFragment extends Fragment {

    TextView tvName, tvPhone, tvRole, btnLogout, btnEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_profile, container, false);

        tvName = view.findViewById(R.id.tvName);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvRole = view.findViewById(R.id.tvRole);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEdit = view.findViewById(R.id.btnEdit);

        // 🔐 Get Data
        SharedPreferences sp = requireActivity().getSharedPreferences("user", 0);

        String name = sp.getString("name", "Admin");
        String phone = sp.getString("phone", "");
        String role = sp.getString("role", "admin");

        // 🔥 Set Data
        tvName.setText(name);
        tvPhone.setText(phone);
        tvRole.setText(role.toUpperCase());

        // 🚪 LOGOUT
        btnLogout.setOnClickListener(v -> {

            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(getActivity(), login_activity.class));
            requireActivity().finish();
        });

        // ✏️ EDIT (Future)
        btnEdit.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Coming Soon 😎", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}