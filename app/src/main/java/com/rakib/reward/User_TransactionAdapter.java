package com.rakib.reward;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class User_TransactionAdapter extends RecyclerView.Adapter<User_TransactionAdapter.ViewHolder> {

    Context context;
    List<User_TransactionModel> list;

    public User_TransactionAdapter(Context context, List<User_TransactionModel> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, points, amount, date, status;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.txnTitle);
            points = itemView.findViewById(R.id.txnPoints);
            amount = itemView.findViewById(R.id.txnAmount);
            date = itemView.findViewById(R.id.txnDate);
            status = itemView.findViewById(R.id.txnStatus);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context)
                .inflate(R.layout.user_transection_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {

        User_TransactionModel m = list.get(position);

        h.title.setText(m.title);
        h.date.setText(m.date);
        h.status.setText(m.action.toUpperCase());

        if (m.type.equals("withdraw")) {

            h.points.setText("-" + m.points + " pts");
            h.amount.setText("৳ " + m.amount);

            h.points.setTextColor(Color.parseColor("#D32F2F"));
            h.amount.setTextColor(Color.parseColor("#D32F2F"));
            h.status.setTextColor(Color.parseColor("#D32F2F"));

        } else {

            h.points.setText("+" + m.points + " pts");
            h.amount.setText("৳ " + m.amount);

            h.points.setTextColor(Color.parseColor("#2E7D32"));
            h.amount.setTextColor(Color.parseColor("#1565C0"));
            h.status.setTextColor(Color.parseColor("#2E7D32"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}