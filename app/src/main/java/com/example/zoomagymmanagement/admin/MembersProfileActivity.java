package com.example.zoomagymmanagement.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.databinding.ActivityMembersBinding;
import com.example.zoomagymmanagement.databinding.ActivityMembersProfileBinding;
import com.example.zoomagymmanagement.model.HelperClass;
import com.example.zoomagymmanagement.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MembersProfileActivity extends AppCompatActivity {
    ActivityMembersProfileBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefUsers;
    UserModel previousModel;
    String memberShip = "";
    

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMembersProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        if (getIntent().getExtras() != null){
            previousModel = (UserModel) getIntent().getSerializableExtra("data");
            if (previousModel != null){
                if (!previousModel.getImage().isEmpty()){
                    Glide.with(binding.getRoot()).load(previousModel.getImage()).into(binding.ivProfile);
                }
                if (!previousModel.getName().isEmpty()){
                    binding.tvName.setText(previousModel.getName());
                }else{
                    binding.tvName.setText("N/A");
                }

                if (!previousModel.getEmail().isEmpty()){
                    binding.tvEmail.setText(previousModel.getEmail());
                }else{
                    binding.tvEmail.setText("N/A");
                }

                if (!previousModel.getPhone().isEmpty()){
                    binding.tvPhone.setText(previousModel.getPhone());
                }else{
                    binding.tvPhone.setText("N/A");
                }

                if (!previousModel.getAddress().isEmpty()){
                    binding.tvAddress.setText(previousModel.getAddress());
                }else{
                    binding.tvAddress.setText("N/A");
                }

                if (!previousModel.getBmi().isEmpty()){
                    binding.tvBMI.setText(previousModel.getBmi()+" BMI");
                }else{
                    binding.tvBMI.setText("N/A");
                }

                if (!previousModel.getAge().isEmpty()){
                    binding.tvAge.setText(previousModel.getAge() +" Years");
                }else{
                    binding.tvAge.setText("N/A");
                }

                if (!previousModel.getGender().isEmpty()){
                    binding.tvGender.setText(previousModel.getGender());
                }else{
                    binding.tvGender.setText("N/A");
                }

                if (!previousModel.getWeight().isEmpty()){
                    binding.tvWeight.setText(previousModel.getWeight() + " kg weight");
                }else{
                    binding.tvWeight.setText("N/A");
                }

                if (!previousModel.getMembership().isEmpty()){
                    memberShip = previousModel.getMembership();
                    if (memberShip.equals("Not Active")){
                        binding.tvMemberShip.setText(previousModel.getMembership() +" Member");
                    }else{
                        binding.tvMemberShip.setText(previousModel.getMembership() +" Membership");
                    }
                }else{
                    binding.tvMemberShip.setText("N/A");
                }
            }
        }
        
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
        
        binding.tvMemberShip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMemberShipDialog();
            }
        });

        binding.tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!previousModel.getAddress().isEmpty()){
                    Intent intent = new Intent(MembersProfileActivity.this, MapsActivity.class);
                    intent.putExtra("address", previousModel.getAddress());
                    startActivity(intent);
                }
            }
        });
        
    }

    @SuppressLint("SetTextI18n")
    public void showMemberShipDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.item_membership);

        Button btnMonthly =  dialog.findViewById(R.id.btnMonthly);
        Button btnYearly =  dialog.findViewById(R.id.btnYearly);
        Button btnNone =  dialog.findViewById(R.id.btnNone);
        ImageView ivCross = dialog.findViewById(R.id.ivCross);

        ivCross.setOnClickListener(v -> dialog.dismiss());

        btnMonthly.setOnClickListener(v -> changeMemberShip("Monthly", dialog));
        
        btnYearly.setOnClickListener(v -> changeMemberShip("Yearly", dialog));

        btnNone.setOnClickListener(v -> changeMemberShip("Not Active", dialog));

        dialog.show();

    }

    @SuppressLint("SetTextI18n")
    private void changeMemberShip(String membership, Dialog dialog) {
        
        progressDialog.show();
        Map<String, Object> update = new HashMap<>();
        update.put("membership", membership);
        dbRefUsers.child(previousModel.getId()).updateChildren(update).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            showMessage("Updated Successfully");
            memberShip = membership;
            if (membership.contains("Not Active")){
                binding.tvMemberShip.setText(membership +" Member");
            }else{
                binding.tvMemberShip.setText(membership +" Membership");
            }
            dialog.dismiss();
            
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            showMessage(e.getLocalizedMessage());
        });
        
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}