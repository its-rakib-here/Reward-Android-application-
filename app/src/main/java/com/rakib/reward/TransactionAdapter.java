package com.rakib.reward;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.VH> {

    List<TransactionModel> list;

    public TransactionAdapter(List<TransactionModel> list){
        this.list = list;
    }

    public static class VH extends RecyclerView.ViewHolder{

        TextView tvType, tvAmount, tvPoints, tvReason, tvDate;
        TextView tvName;
        public VH(View v){
            super(v);

            tvType = v.findViewById(R.id.tvType);
            tvAmount = v.findViewById(R.id.tvAmount);
            tvPoints = v.findViewById(R.id.tvPoints);
            tvReason = v.findViewById(R.id.tvReason);
            tvDate = v.findViewById(R.id.tvDate);
            tvName = v.findViewById(R.id.tvName);

        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);

        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH h, int i) {

        TransactionModel item = list.get(i);

        String type = item.type != null ? item.type.toLowerCase() : "unknown";

        int points = 0;
        try {
            points = Integer.parseInt(item.points);
        } catch (Exception ignored){}

        // amount from API
        String amount = item.totall_amount;

        if(amount == null || amount.trim().isEmpty()){
            amount = "0.00";
        }

        h.tvType.setText(type.toUpperCase());
        h.tvName.setText(
                item.name != null ? item.name : "Unknown User"
        );
        switch (type){

            case "add":
                h.tvType.setBackgroundColor(Color.parseColor("#2E7D32"));
                h.tvPoints.setText("+ " + points + " Points");
                h.tvAmount.setText("৳ " + amount);
                h.tvAmount.setTextColor(Color.parseColor("#2E7D32"));
                break;

            case "deduct":
            case "withdraw":
                h.tvType.setBackgroundColor(Color.parseColor("#C62828"));
                h.tvPoints.setText("- " + points + " Points");
                h.tvAmount.setText("৳ " + amount);
                h.tvAmount.setTextColor(Color.parseColor("#C62828"));
                break;

            default:
                h.tvType.setBackgroundColor(Color.parseColor("#1565C0"));
                h.tvPoints.setText(points + " Points");
                h.tvAmount.setText("৳ " + amount);
                h.tvAmount.setTextColor(Color.parseColor("#1565C0"));
                break;
        }

        // reason
        h.tvReason.setText(
                (item.reason == null || item.reason.trim().isEmpty())
                        ? "No reason provided"
                        : item.reason
        );

        // date
        h.tvDate.setText(
                item.createdAt != null ? item.createdAt : "N/A"
        );
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    // =========================
    // UPDATE LIST
    // =========================
    public void update(List<TransactionModel> newList){
        if(newList == null) return;

        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }
}