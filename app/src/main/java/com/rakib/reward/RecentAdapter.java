package com.rakib.reward;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.VH> {

    Context context;
    List<User_TransactionModel> list;

    public RecentAdapter(Context context, List<User_TransactionModel> list) {
        this.context = context;
        this.list = list;
    }

    class VH extends RecyclerView.ViewHolder {
        TextView title, amount, date;

        public VH(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            amount = v.findViewById(R.id.amount);
            date = v.findViewById(R.id.date);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(context)
                .inflate(R.layout.item_recent, parent, false));
    }

    @Override
    public void onBindViewHolder(VH h, int i) {

        User_TransactionModel m = list.get(i);

        h.title.setText(m.title);

        if (m.type.equals("withdraw")) {
            h.amount.setText("-" + m.points + " pts (৳" + m.amount + ")");
            h.amount.setTextColor(Color.RED);
        } else {
            h.amount.setText("+" + m.points + " pts");
            h.amount.setTextColor(Color.GREEN);
        }

        h.date.setText(m.date);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}