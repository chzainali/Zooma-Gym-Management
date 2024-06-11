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
import com.example.zoomagymmanagement.model.GoalModel;
import com.example.zoomagymmanagement.model.OnItemClick;

import java.util.List;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.Vh> {
    Context context;
    List<GoalModel> list;
    OnItemClick onItemClick;

    public GoalsAdapter(Context context, List<GoalModel> list, OnItemClick click) {
        this.context = context;
        this.list = list;
        this.onItemClick = click;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_goals, parent, false);
        return new Vh(view);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull Vh holder, @SuppressLint("RecyclerView") int position) {
        GoalModel model = list.get(position);
        holder.tvType.setText(model.getType());
        holder.tvStatus.setText(model.getStatus());
        holder.tvTarget.setText("Duration: "+model.getTarget()+" mins");
        if (model.getStatus().equalsIgnoreCase("Pending")){
            holder.tvStatus.setTextColor(context.getColor(R.color.yellow));
        }else{
            holder.tvStatus.setTextColor(context.getColor(R.color.green));
        }
        holder.tvDateTime.setText("Target Date: "+model.getTargetDate());

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
        TextView tvType, tvTarget, tvStatus, tvDateTime;

        public Vh(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            tvTarget = itemView.findViewById(R.id.tvTarget);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
        }
    }
}
