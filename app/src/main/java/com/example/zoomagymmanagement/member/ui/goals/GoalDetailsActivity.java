package com.example.zoomagymmanagement.member.ui.goals;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.databinding.ActivityGoalDetailsBinding;
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

public class GoalDetailsActivity extends AppCompatActivity {
    ActivityGoalDetailsBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefGoals;
    GoalModel goalModel;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoalDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getExtras() != null){
            goalModel = (GoalModel) getIntent().getSerializableExtra("data");
            if (goalModel != null){
                binding.tvType.setText(goalModel.getType());
                binding.tvTitle.setText(goalModel.getTitle());
                binding.tvTargetDuration.setText(goalModel.getTarget()+" mins");
                binding.tvAchievedDuration.setText(goalModel.getAchieved()+" mins");
                binding.tvDateTime.setText(goalModel.getTargetDate());
                setProgressChart(goalModel);
            }
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefGoals = FirebaseDatabase.getInstance().getReference("Goals");

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    // Method to set up and populate a pie chart representing goal progress
    public void setProgressChart(GoalModel goalModel) {
        // Disable chart description
        binding.pieChart.getDescription().setEnabled(false);

        // Set extra offsets for the pie chart
        binding.pieChart.setExtraOffsets(5, 10, 5, 5);

        // Set drag deceleration friction coefficient
        binding.pieChart.setDragDecelerationFrictionCoef(0.95f);

        // Enable drawing a hole in the center of the pie chart
        binding.pieChart.setDrawHoleEnabled(true);

        // Set the radius of the hole in the center of the pie chart
        binding.pieChart.setHoleRadius(90f);

        // Set the color of the hole in the center of the pie chart
        binding.pieChart.setHoleColor(getResources().getColor(R.color.white));

        // Set the radius of the transparent circle in the center of the pie chart
        binding.pieChart.setTransparentCircleRadius(0f);

        // Disable drawing entry labels on the pie chart
        binding.pieChart.setDrawEntryLabels(false);

        // Disable touch interaction with the pie chart
        binding.pieChart.setTouchEnabled(false);

        // Animate the pie chart
        binding.pieChart.animateY(1000, Easing.EaseInOutCubic);

        // Create an ArrayList to hold pie chart data values
        ArrayList<PieEntry> values = new ArrayList<>();

        // Calculate percentage achieved based on goal progress
        float achievedPercentage = calculatePercentage(Double.parseDouble(goalModel.getAchieved()), Double.parseDouble(goalModel.getTarget()));

        // Calculate remaining percentage
        float remainingPercentage = 100f - achievedPercentage;

        // Add achieved and pending percentages to the values ArrayList
        values.add(new PieEntry(achievedPercentage, "Achieved"));
        values.add(new PieEntry(remainingPercentage, "Pending"));

        // Create a PieDataSet with the values
        PieDataSet dataSet = new PieDataSet(values, "");

        // Set space between slices of the pie chart
        dataSet.setSliceSpace(3f);

        // Set selection shift for slices
        dataSet.setSelectionShift(0f);

        // Set colors for the pie chart slices
        dataSet.setColors(getResources().getColor(R.color.green), getResources().getColor(R.color.yellow));

        // Create PieData object with the dataSet
        PieData data = new PieData(dataSet);

        // Set text size for values displayed on the pie chart
        data.setValueTextSize(0f);

        // Set the data to the pie chart
        binding.pieChart.setData(data);

        // Set the achieved percentage value as text
        int percentageValue = Float.valueOf(achievedPercentage).intValue();
        String s = percentageValue + "% Achieved";
        binding.rate.setText(s);
    }

    // Method to calculate percentage based on used and total values
    private float calculatePercentage(double used, double total) {
        // Check if total is zero to avoid division by zero
        if (total == 0) {
            return 0f;
        } else {
            // Calculate and return the percentage
            return (float) (used * 100 / total);
        }
    }


}