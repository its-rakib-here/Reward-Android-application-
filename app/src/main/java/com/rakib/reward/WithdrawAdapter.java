package com.rakib.reward;

import static android.app.PendingIntent.getActivity;

import static java.security.AccessController.getContext;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class WithdrawAdapter extends RecyclerView.Adapter<WithdrawAdapter.VH> {

    List<WithdrawRequest> list;
    WithdrawActionListener listener;

    public WithdrawAdapter(List<WithdrawRequest> list, WithdrawActionListener listener){
        this.list = list;
        this.listener = listener;
    }

    public void updateList(List<WithdrawRequest> newList){
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    // ✅ VIEW HOLDER CLASS
    public static class VH extends RecyclerView.ViewHolder {

        TextView name, phone, amount, points, account;
        Button approve, reject;

        public VH(View v){
            super(v);

            name = v.findViewById(R.id.tvUserName);
            phone = v.findViewById(R.id.tvPhone);
            amount = v.findViewById(R.id.tvAmount);
            points = v.findViewById(R.id.tvPoints);
            account = v.findViewById(R.id.tvAccount);

            approve = v.findViewById(R.id.btnApprove);
            reject = v.findViewById(R.id.btnReject);
        }
    }

    // ✅ 1. CREATE VIEW HOLDER (THIS WAS MISSING / WRONG)
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_withdraw, parent, false);

        return new VH(v);
    }

    // ✅ 2. BIND DATA
    @Override
    public void onBindViewHolder(VH h, int i) {

        WithdrawRequest item = list.get(i);

        h.name.setText(item.name);
        h.phone.setText(item.phone);
        h.amount.setText("Amount: " + item.amount);
        h.points.setText("Points: " + item.points);
        h.account.setText("Account: " + item.account);

        h.approve.setOnClickListener(v ->
                listener.onAction(Integer.parseInt(item.id), "approve"));

        h.reject.setOnClickListener(v ->
                listener.onAction(Integer.parseInt(item.id), "reject"));
    }

    // ✅ 3. SIZE
    @Override
    public int getItemCount() {
        return list.size();
    }
}


