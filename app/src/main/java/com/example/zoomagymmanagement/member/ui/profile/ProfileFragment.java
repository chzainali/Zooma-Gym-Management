package com.example.zoomagymmanagement.member.ui.profile;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.zoomagymmanagement.admin.AdminMainActivity;
import com.example.zoomagymmanagement.admin.MapsActivity;
import com.example.zoomagymmanagement.admin.MembersProfileActivity;
import com.example.zoomagymmanagement.auth.LoginActivity;
import com.example.zoomagymmanagement.databinding.FragmentProfileBinding;
import com.example.zoomagymmanagement.model.HelperClass;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.ivEdit.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
            intent.putExtra("from", "profile");
            startActivity(intent);
        });

        binding.tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!HelperClass.users.getAddress().isEmpty()){
                    Intent intent = new Intent(requireContext(), MapsActivity.class);
                    intent.putExtra("address", HelperClass.users.getAddress());
                    startActivity(intent);
                }
            }
        });

        binding.ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(requireContext(), LoginActivity.class));
                requireActivity().finishAffinity();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (HelperClass.users != null){
            if (!HelperClass.users.getImage().isEmpty()){
                Glide.with(binding.getRoot()).load(HelperClass.users.getImage()).into(binding.ivProfile);
            }
            if (!HelperClass.users.getName().isEmpty()){
                binding.tvName.setText(HelperClass.users.getName());
            }else{
                binding.tvName.setText("N/A");
            }

            if (!HelperClass.users.getEmail().isEmpty()){
                binding.tvEmail.setText(HelperClass.users.getEmail());
            }else{
                binding.tvEmail.setText("N/A");
            }

            if (!HelperClass.users.getPhone().isEmpty()){
                binding.tvPhone.setText(HelperClass.users.getPhone());
            }else{
                binding.tvPhone.setText("N/A");
            }

            if (!HelperClass.users.getAddress().isEmpty()){
                binding.tvAddress.setText(HelperClass.users.getAddress());
            }else{
                binding.tvAddress.setText("N/A");
            }

            if (!HelperClass.users.getBmi().isEmpty()){
                binding.tvBMI.setText(HelperClass.users.getBmi()+" BMI");
            }else{
                binding.tvBMI.setText("N/A");
            }

            if (!HelperClass.users.getAge().isEmpty()){
                binding.tvAge.setText(HelperClass.users.getAge() +" Years");
            }else{
                binding.tvAge.setText("N/A");
            }

            if (!HelperClass.users.getGender().isEmpty()){
                binding.tvGender.setText(HelperClass.users.getGender());
            }else{
                binding.tvGender.setText("N/A");
            }

            if (!HelperClass.users.getWeight().isEmpty()){
                binding.tvWeight.setText(HelperClass.users.getWeight() + " kg weight");
            }else{
                binding.tvWeight.setText("N/A");
            }

            if (!HelperClass.users.getMembership().isEmpty()){
                binding.tvMemberShip.setText(HelperClass.users.getMembership() +" Membership");
            }else{
                binding.tvMemberShip.setText("N/A");
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}