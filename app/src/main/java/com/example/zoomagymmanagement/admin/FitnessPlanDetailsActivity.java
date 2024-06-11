package com.example.zoomagymmanagement.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.databinding.ActivityAddFitnessPlansBinding;
import com.example.zoomagymmanagement.databinding.ActivityFitnessPlanDetailsBinding;
import com.example.zoomagymmanagement.databinding.ActivityFitnessPlansBinding;
import com.example.zoomagymmanagement.model.FitnessPlansModel;
import com.example.zoomagymmanagement.model.GoalModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FitnessPlanDetailsActivity extends AppCompatActivity {
    ActivityFitnessPlanDetailsBinding binding;
    FitnessPlansModel fitnessPlansModel;
    String from = "";
    DatabaseReference dbRefFitnessPlans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFitnessPlanDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbRefFitnessPlans = FirebaseDatabase.getInstance().getReference("FitnessPlans");

        if (getIntent().getExtras() != null){
            fitnessPlansModel = (FitnessPlansModel) getIntent().getSerializableExtra("data");
            from = getIntent().getStringExtra("from");
            if (fitnessPlansModel != null){
                Glide.with(this).load(fitnessPlansModel.getImage()).into(binding.ivPlan);
                binding.tvTitle.setText(fitnessPlansModel.getTitle());
                binding.tvDetails.setText(fitnessPlansModel.getDetails());
            }

            if (from.equals("admin")){
                binding.btnDelete.setVisibility(View.VISIBLE);
            }else{
                binding.btnDelete.setVisibility(View.GONE);
            }

        }

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fitnessPlansModel != null){
                    dbRefFitnessPlans.child(fitnessPlansModel.getPlanId()).removeValue();
                    Toast.makeText(FitnessPlanDetailsActivity.this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}