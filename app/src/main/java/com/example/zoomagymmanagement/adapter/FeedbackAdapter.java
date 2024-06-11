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
import com.example.zoomagymmanagement.model.FeedbackModel;
import com.example.zoomagymmanagement.model.FitnessPlansModel;
import com.example.zoomagymmanagement.model.OnItemClick;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.Vh> {
    Context context;
    List<FeedbackModel> list;
    OnItemClick onItemClick;

    public FeedbackAdapter(Context context, List<FeedbackModel> list, OnItemClick click) {
        this.context = context;
        this.list = list;
        this.onItemClick = click;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_feedback, parent, false);
        return new Vh(view);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull Vh holder, @SuppressLint("RecyclerView") int position) {
        FeedbackModel model = list.get(position);
        holder.tvTitle.setText(model.getSubject());
        holder.tvStatus.setText(model.getStatus());
        holder.tvDate.setText(model.getDateTime());
        if (model.getStatus().equalsIgnoreCase("Pending")){
            holder.tvStatus.setTextColor(context.getColor(R.color.yellow));
        }else{
            holder.tvStatus.setTextColor(context.getColor(R.color.green));
        }

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
        TextView tvTitle, tvStatus, tvDate;

        public Vh(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
