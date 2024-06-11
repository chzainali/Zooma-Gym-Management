package com.example.zoomagymmanagement.admin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.adapter.FitnessPlansAdapter;
import com.example.zoomagymmanagement.databinding.ActivityFitnessPlansBinding;
import com.example.zoomagymmanagement.model.FitnessPlansModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FitnessPlansActivity extends AppCompatActivity {
    ActivityFitnessPlansBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefGoals;
    List<FitnessPlansModel> list = new ArrayList<>();
    FitnessPlansAdapter adapter;
    String from = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFitnessPlansBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getExtras() != null){
            from = getIntent().getStringExtra("from");
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefGoals = FirebaseDatabase.getInstance().getReference("FitnessPlans");

        if (from.equals("admin")){
            binding.ivAdd.setVisibility(View.VISIBLE);
        }else{
            binding.ivAdd.setVisibility(View.GONE);
        }

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FitnessPlansActivity.this, AddFitnessPlansActivity.class));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.show();
        dbRefGoals.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    progressDialog.dismiss();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            FitnessPlansModel model = ds.getValue(FitnessPlansModel.class);
                            list.add(model);
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }

                    setAdapter();

                    if (list.isEmpty()) {
                        binding.tvNoGoalFound.setVisibility(View.VISIBLE);
                        binding.rvGoals.setVisibility(View.GONE);
                    } else {
                        binding.tvNoGoalFound.setVisibility(View.GONE);
                        binding.rvGoals.setVisibility(View.VISIBLE);
                    }
                } else {
                    progressDialog.dismiss();
                    binding.tvNoGoalFound.setVisibility(View.VISIBLE);
                    binding.rvGoals.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(FitnessPlansActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setAdapter() {
        adapter = new FitnessPlansAdapter(this, list, pos -> {
            FitnessPlansModel model = list.get(pos);
            Intent intent = new Intent(FitnessPlansActivity.this, FitnessPlanDetailsActivity.class);
            intent.putExtra("data", model);
            intent.putExtra("from", from);
            startActivity(intent);
        });
        binding.rvGoals.setLayoutManager(new LinearLayoutManager(this));
        binding.rvGoals.setAdapter(adapter);
    }

}