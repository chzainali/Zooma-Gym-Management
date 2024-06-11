package com.example.zoomagymmanagement.member.ui.goals;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.databinding.ActivityGoalDetailsBinding;
import com.example.zoomagymmanagement.databinding.FragmentGoalDetailsBinding;
import com.example.zoomagymmanagement.databinding.FragmentGoalsBinding;
import com.example.zoomagymmanagement.model.GoalModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GoalDetailsFragment extends Fragment {
    FragmentGoalDetailsBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefGoals;
    GoalModel goalModel;

    public GoalDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            goalModel = (GoalModel) getArguments().getSerializable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGoalDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefGoals = FirebaseDatabase.getInstance().getReference("Goals");

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(binding.getRoot()).navigateUp();
            }
        });

        binding.btnDelete.setOnClickListener(view1 -> {
            if (goalModel != null){
                dbRefGoals.child(goalModel.getGoalId()).removeValue();
                Toast.makeText(requireContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(binding.getRoot()).navigateUp();
            }
        });

        binding.btnUpdate.setOnClickListener(view1 -> {
            if (goalModel != null){
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", goalModel);
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_goalDetailsFragment_to_addGoalFragment, bundle);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.show();
        dbRefGoals.child(goalModel.getGoalId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    progressDialog.dismiss();
                    GoalModel model = snapshot.getValue(GoalModel.class);
                    binding.tvType.setText(model.getType());
                    binding.tvTitle.setText(model.getTitle());
                    binding.tvTargetDuration.setText(model.getTarget()+" mins");
                    binding.tvAchievedDuration.setText(model.getAchieved()+" mins");
                    binding.tvDateTime.setText(model.getTargetDate());
                    setProgressChart(model);
                }else{
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(requireActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void setProgressChart(GoalModel goalModel) {
        binding.pieChart.getDescription().setEnabled(false);

        binding.pieChart.setExtraOffsets(5, 10, 5, 5);
        binding.pieChart.setDragDecelerationFrictionCoef(0.95f);

        binding.pieChart.setDrawHoleEnabled(true);
        binding.pieChart.setHoleRadius(90f);
        binding.pieChart.setHoleColor(getResources().getColor(R.color.white));
        binding.pieChart.setTransparentCircleRadius(0f);
        binding.pieChart.setDrawEntryLabels(false);
        binding.pieChart.setTouchEnabled(false);

        binding.pieChart.animateY(1000, Easing.EaseInOutCubic);

        ArrayList<PieEntry> values = new ArrayList<>();
        // Calculate percentage achieved
        float achievedPercentage = calculatePercentage(Double.parseDouble(goalModel.getAchieved()), Double.parseDouble(goalModel.getTarget()));
        float remainingPercentage = 100f - achievedPercentage;

        values.add(new PieEntry(achievedPercentage, "Achieved"));
        values.add(new PieEntry(remainingPercentage, "Pending"));

        PieDataSet dataSet = new PieDataSet(values, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(0f);
        dataSet.setColors(getResources().getColor(R.color.green), getResources().getColor(R.color.yellow));

        PieData data = new PieData(dataSet);
        data.setValueTextSize(0f);

        binding.pieChart.setData(data);

        int percentageValue = Float.valueOf(achievedPercentage).intValue();
        String s = percentageValue + "% Achieved";
        binding.rate.setText(s);
    }

    private float calculatePercentage(double used, double total) {
        if (total == 0) {
            return 0f;
        } else {
            return (float) (used * 100 / total);
        }
    }

}