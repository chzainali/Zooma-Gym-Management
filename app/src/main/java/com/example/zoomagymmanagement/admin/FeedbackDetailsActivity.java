package com.example.zoomagymmanagement.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.databinding.ActivityFeedbackDetailsBinding;
import com.example.zoomagymmanagement.databinding.ActivityFitnessPlanDetailsBinding;
import com.example.zoomagymmanagement.member.MemberMainActivity;
import com.example.zoomagymmanagement.member.ui.profile.EditProfileActivity;
import com.example.zoomagymmanagement.model.FeedbackModel;
import com.example.zoomagymmanagement.model.FitnessPlansModel;
import com.example.zoomagymmanagement.model.HelperClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FeedbackDetailsActivity extends AppCompatActivity {
    ActivityFeedbackDetailsBinding binding;
    FeedbackModel feedbackModel;
    String from = "";
    DatabaseReference dbRefFeedback;
    ProgressDialog progressDialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedbackDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        auth = FirebaseAuth.getInstance();
        dbRefFeedback = FirebaseDatabase.getInstance().getReference("Feedback");

        if (getIntent().getExtras() != null){
            feedbackModel = (FeedbackModel) getIntent().getSerializableExtra("data");
            from = getIntent().getStringExtra("from");
            if (feedbackModel != null){
                Glide.with(this).load(feedbackModel.getImage()).into(binding.ivFeedback);
                binding.tvSubject.setText(feedbackModel.getSubject());
                binding.tvDetails.setText(feedbackModel.getDetails());
                binding.etComments.setText(feedbackModel.getAdminReply());
            }

            if (from.equals("admin")){
                binding.btnDelete.setVisibility(View.GONE);
                binding.tvReplyLabel.setVisibility(View.VISIBLE);
                binding.cvEtReply.setVisibility(View.VISIBLE);
            }else{
                if (feedbackModel.getAdminReply().equals("")){
                    binding.tvReplyLabel.setVisibility(View.GONE);
                    binding.cvEtReply.setVisibility(View.GONE);
                }else{
                    binding.tvReplyLabel.setVisibility(View.VISIBLE);
                    binding.cvEtReply.setVisibility(View.VISIBLE);
                    binding.btnAddComment.setVisibility(View.GONE);
                    binding.etComments.setFocusable(false);
                    binding.etComments.setEnabled(false);

                }
                binding.btnDelete.setVisibility(View.VISIBLE);
            }

        }

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (feedbackModel != null){
                    dbRefFeedback.child(feedbackModel.getFeedbackId()).removeValue();
                    Toast.makeText(FeedbackDetailsActivity.this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        binding.btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reply = binding.etComments.getText().toString();
                if (reply.isEmpty()) {
                    Toast.makeText(FeedbackDetailsActivity.this, "Please write reply first", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    Map<String, Object> update = new HashMap<>();
                    update.put("adminReply", reply);
                    update.put("status", "Replied");
                    dbRefFeedback.child(feedbackModel.getFeedbackId()).updateChildren(update).addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        Toast.makeText(FeedbackDetailsActivity.this, "Replied Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(FeedbackDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
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