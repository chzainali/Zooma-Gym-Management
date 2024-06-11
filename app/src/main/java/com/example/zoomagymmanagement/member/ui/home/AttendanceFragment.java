package com.example.zoomagymmanagement.member.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.databinding.FragmentAddGoalBinding;
import com.example.zoomagymmanagement.databinding.FragmentAttendanceBinding;

public class AttendanceFragment extends Fragment {
    private FragmentAttendanceBinding binding;
    Animation rotate;

    public AttendanceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAttendanceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rotate = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate);
        binding.ivLogo.setAnimation(rotate);


        binding.ivBack.setOnClickListener(view1 -> Navigation.findNavController(binding.getRoot()).navigateUp());

        binding.cvTakeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireActivity(), TakeAttendanceActivity.class));
            }
        });

        binding.cvAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), AttendanceHistoryActivity.class);
                intent.putExtra("from", "user");
                startActivity(intent);
            }
        });

    }
}