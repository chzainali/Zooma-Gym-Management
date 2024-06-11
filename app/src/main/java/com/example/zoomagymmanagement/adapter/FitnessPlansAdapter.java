package com.example.zoomagymmanagement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.model.FitnessPlansModel;
import com.example.zoomagymmanagement.model.GoalModel;
import com.example.zoomagymmanagement.model.OnItemClick;

import java.util.List;

public class FitnessPlansAdapter extends RecyclerView.Adapter<FitnessPlansAdapter.Vh> {
    Context context;
    List<FitnessPlansModel> list;
    OnItemClick onItemClick;

    public FitnessPlansAdapter(Context context, List<FitnessPlansModel> list, OnItemClick click) {
        this.context = context;
        this.list = list;
        this.onItemClick = click;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_plans, parent, false);
        return new Vh(view);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull Vh holder, @SuppressLint("RecyclerView") int position) {
        FitnessPlansModel model = list.get(position);
        holder.tvTitle.setText(model.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Vh extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public Vh(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }
}
