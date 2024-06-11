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
import com.example.zoomagymmanagement.model.CodesModel;
import com.example.zoomagymmanagement.model.FeedbackModel;
import com.example.zoomagymmanagement.model.OnItemClick;

import java.util.List;

public class CodesAdapter extends RecyclerView.Adapter<CodesAdapter.Vh> {
    Context context;
    List<CodesModel> list;
    OnItemClick onItemClick;

    public CodesAdapter(Context context, List<CodesModel> list, OnItemClick click) {
        this.context = context;
        this.list = list;
        this.onItemClick = click;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_codes, parent, false);
        return new Vh(view);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull Vh holder, @SuppressLint("RecyclerView") int position) {
        CodesModel model = list.get(position);
        holder.tvCode.setText(model.getCode());
        holder.tvStatus.setText(model.getStatus());
        holder.tvDateTime.setText("Created at: "+model.getCreatedDate());
        if (model.getStatus().equalsIgnoreCase("Available")){
            holder.tvStatus.setTextColor(context.getColor(R.color.green));
        }else{
            holder.tvStatus.setTextColor(context.getColor(R.color.yellow));
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
        TextView tvCode, tvStatus, tvDateTime;

        public Vh(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
        }
    }
}
