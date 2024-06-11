package com.example.zoomagymmanagement.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.adapter.FeedbackAdapter;
import com.example.zoomagymmanagement.adapter.MembersAdapter;
import com.example.zoomagymmanagement.databinding.ActivityFeedbackBinding;
import com.example.zoomagymmanagement.databinding.ActivityMembersBinding;
import com.example.zoomagymmanagement.model.FeedbackModel;
import com.example.zoomagymmanagement.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MembersActivity extends AppCompatActivity {
    ActivityMembersBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefUsers;
    List<UserModel> list = new ArrayList<>();
    MembersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMembersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users");

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        // Display a progress dialog to indicate loading process
        progressDialog.show();

        // Add a ValueEventListener to fetch data from Firebase Realtime Database
        dbRefUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // When data is changed in the database

                if (snapshot.exists()) {
                    // If data exists in the database

                    // Clear the list to avoid duplicate entries
                    list.clear();

                    // Dismiss the progress dialog
                    progressDialog.dismiss();

                    // Iterate through each snapshot of the database
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            // Retrieve UserModel object from the snapshot
                            UserModel model = ds.getValue(UserModel.class);

                            // Add UserModel object to the list
                            list.add(model);
                        } catch (DatabaseException e) {
                            // Catch any DatabaseException that may occur
                            e.printStackTrace();
                        }
                    }

                    // Set adapter for RecyclerView
                    setAdapter();

                    // Check if the list is empty
                    if (list.isEmpty()) {
                        // If the list is empty, show a message indicating no data found
                        binding.tvNoGoalFound.setVisibility(View.VISIBLE);
                        binding.rvGoals.setVisibility(View.GONE);
                    } else {
                        // If the list is not empty, hide the message and show RecyclerView
                        binding.tvNoGoalFound.setVisibility(View.GONE);
                        binding.rvGoals.setVisibility(View.VISIBLE);
                    }
                } else {
                    // If data doesn't exist in the database

                    // Dismiss the progress dialog
                    progressDialog.dismiss();

                    // Show a message indicating no data found
                    binding.tvNoGoalFound.setVisibility(View.VISIBLE);
                    binding.rvGoals.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // If onCancelled event occurs (e.g., due to network error)

                // Dismiss the progress dialog
                progressDialog.dismiss();

                // Show a toast with the error message
                Toast.makeText(MembersActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Method to set adapter for RecyclerView
    private void setAdapter() {
        // Create a new instance of MembersAdapter with context and list
        adapter = new MembersAdapter(this, list);

        // Set layout manager for RecyclerView (GridLayoutManager with 2 columns)
        binding.rvGoals.setLayoutManager(new GridLayoutManager(this, 2));

        // Set adapter for RecyclerView
        binding.rvGoals.setAdapter(adapter);
    }

}