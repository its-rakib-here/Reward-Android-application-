package com.rakib.reward;

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

        TextView tvType, tvPoints, tvReason, tvDate;

        public VH(View v){
            super(v);
            tvType = v.findViewById(R.id.tvType);
            tvPoints = v.findViewById(R.id.tvPoints);
            tvReason = v.findViewById(R.id.tvReason);
            tvDate = v.findViewById(R.id.tvDate);
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

        // TYPE
        h.tvType.setText(item.type.toUpperCase());

        // POINT STYLE
        if(item.type.equalsIgnoreCase("add")){
            h.tvPoints.setText("+ " + item.points);
        }
        else if(item.type.equalsIgnoreCase("deduct") || item.type.equalsIgnoreCase("withdraw")){
            h.tvPoints.setText("- " + item.points);
        }
        else {
            h.tvPoints.setText(item.points);
        }

        // REASON
        h.tvReason.setText(item.reason);

        // DATE
        h.tvDate.setText(item.createdAt);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void update(List<TransactionModel> newList){
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }
}