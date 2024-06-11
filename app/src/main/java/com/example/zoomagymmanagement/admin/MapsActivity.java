package com.example.zoomagymmanagement.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.example.zoomagymmanagement.R;
import com.example.zoomagymmanagement.databinding.ActivityMapsBinding;
import com.example.zoomagymmanagement.databinding.ActivityRegisterBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    ActivityMapsBinding binding;
    String address = "";
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getExtras() != null){
            address = getIntent().getStringExtra("address");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        binding.ivBack.setOnClickListener(view -> finish());

    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // Geocode the address to get the coordinates
        GeocodingTask geocodingTask = new GeocodingTask();
        geocodingTask.execute(address);
    }

    private class GeocodingTask extends AsyncTask<String, Void, LatLng> {
        @Override
        protected LatLng doInBackground(String... params) {
            String address = params[0];
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            try {
                List<Address> addresses = geocoder.getFromLocationName(address, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address location = addresses.get(0);
                    return new LatLng(location.getLatitude(), location.getLongitude());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(LatLng locationLatLng) {
            if (locationLatLng != null) {
                // Add a marker at the specified location
                googleMap.addMarker(new MarkerOptions().position(locationLatLng).title(address));

                // Move the camera to the specified location and zoom in
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15));
            }
        }
    }

}