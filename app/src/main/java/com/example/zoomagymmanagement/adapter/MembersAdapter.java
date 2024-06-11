package com.example.zoomagymmanagement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.admin.AdminMainActivity;
import com.example.zoomagymmanagement.admin.FitnessPlansActivity;
import com.example.zoomagymmanagement.admin.MembersGoalActivity;
import com.example.zoomagymmanagement.admin.MembersProfileActivity;
import com.example.zoomagymmanagement.member.ui.home.AttendanceHistoryActivity;
import com.example.zoomagymmanagement.model.GoalModel;
import com.example.zoomagymmanagement.model.HelperClass;
import com.example.zoomagymmanagement.model.OnItemClick;
import com.example.zoomagymmanagement.model.UserModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.Vh> {
    Context context;
    List<UserModel> list;

    public MembersAdapter(Context context, List<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_users, parent, false);
        return new Vh(view);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull Vh holder, @SuppressLint("RecyclerView") int position) {
        UserModel model = list.get(position);
        holder.tvName.setText(model.getName());
        Glide.with(holder.ivProfile).load(model.getImage()).into(holder.ivProfile);

        holder.tvViewProfile.setOnClickListener(v -> {
            Intent intent = new Intent(context, MembersProfileActivity.class);
            intent.putExtra("data", model);
            context.startActivity(intent);
        });

        holder.tvViewGoals.setOnClickListener(v -> {
            Intent intent = new Intent(context, MembersGoalActivity.class);
            intent.putExtra("data", model);
            context.startActivity(intent);
        });

        holder.tvViewAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(context, AttendanceHistoryActivity.class);
            intent.putExtra("from", "admin");
            intent.putExtra("data", model);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Vh extends RecyclerView.ViewHolder {
        TextView tvName, tvViewProfile, tvViewGoals, tvViewAttendance;
        CircleImageView ivProfile;

        public Vh(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvName = itemView.findViewById(R.id.tvName);
            tvViewProfile = itemView.findViewById(R.id.tvViewProfile);
            tvViewGoals = itemView.findViewById(R.id.tvViewGoals);
            tvViewAttendance = itemView.findViewById(R.id.tvViewAttendance);
        }
    }
}
