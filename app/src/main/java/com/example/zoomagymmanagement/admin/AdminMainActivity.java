package com.example.zoomagymmanagement.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.auth.LoginActivity;
import com.example.zoomagymmanagement.databinding.ActivityAdminMainBinding;
import com.example.zoomagymmanagement.databinding.ActivityLoginBinding;
import com.example.zoomagymmanagement.member.ui.home.TakeAttendanceActivity;
import com.example.zoomagymmanagement.model.GymCapacityModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminMainActivity extends AppCompatActivity {
    ActivityAdminMainBinding binding;
    Animation rotate;
    ProgressDialog progressDialog;
    DatabaseReference dbRefGymCapacitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        binding.ivLogo.setAnimation(rotate);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        dbRefGymCapacitor = FirebaseDatabase.getInstance().getReference("GymCapacitor");

        binding.ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AdminMainActivity.this, LoginActivity.class));
                finishAffinity();
            }
        });

        binding.cvFitnessPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMainActivity.this, FitnessPlansActivity.class);
                intent.putExtra("from", "admin");
                startActivity(intent);
            }
        });

        binding.cvMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMainActivity.this, MembersActivity.class);
                startActivity(intent);
            }
        });

        binding.cvManageQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMainActivity.this, QrCodesActivity.class);
                startActivity(intent);
            }
        });

        binding.cvFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminMainActivity.this, FeedbackActivity.class);
                intent.putExtra("from", "admin");
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        getGymCapacity();

    }

    private void getGymCapacity() {
        progressDialog.show();
        dbRefGymCapacitor.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    GymCapacityModel model = snapshot.getValue(GymCapacityModel.class);
                    binding.tvCapacity.setText(model.getCapacity());
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(AdminMainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}