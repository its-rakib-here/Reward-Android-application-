package com.rakib.reward;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.VH> {

    List<UserModel> list;
    Context context;
    OnUserClick listener;

    // 🔥 INTERFACE
    public interface OnUserClick {
        void onClick(UserModel user);
        void onDeleteClick(UserModel user, int position);
    }

    public UserAdapter(Context context, List<UserModel> list, OnUserClick listener){
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    public static class VH extends RecyclerView.ViewHolder {

        TextView name, phone, points;
        ImageView btnDelete;

        public VH(View v) {
            super(v);
            name = v.findViewById(R.id.tvName);
            phone = v.findViewById(R.id.tvPhone);
            points = v.findViewById(R.id.tvPoints);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH h, int i) {

        UserModel item = list.get(i);

        h.name.setText(item.getName());
        h.phone.setText(item.getPhone());
        h.points.setText("Points: " + item.getPoints());

        // 🔹 Normal Click
        h.itemView.setOnClickListener(v -> {
            if(listener != null){
                listener.onClick(item);
            }
        });

        // 🔥 DELETE CLICK
        h.btnDelete.setOnClickListener(v -> {
            if(listener != null){
                int pos = h.getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION){
                    listener.onDeleteClick(item, pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void update(List<UserModel> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    // 🔥 REMOVE ITEM
    public void removeItem(int position){
        list.remove(position);
        notifyItemRemoved(position);
    }
}