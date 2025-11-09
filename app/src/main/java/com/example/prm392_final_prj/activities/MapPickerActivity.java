package com.example.prm392_final_prj.activities;

import android.Manifest; // <-- THÊM IMPORT
import android.content.Intent;
import android.content.pm.PackageManager; // <-- THÊM IMPORT
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull; // <-- THÊM IMPORT
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat; // <-- THÊM IMPORT
import androidx.core.content.ContextCompat; // <-- THÊM IMPORT
import androidx.fragment.app.FragmentTransaction;
import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.dto.request.MapMarker;
import com.example.prm392_final_prj.fragment.GoogleMapsFragment;
import com.example.prm392_final_prj.utils.MapMode;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;

public class MapPickerActivity extends AppCompatActivity implements GoogleMapsFragment.MapInteractionListener {

    public static final String EXTRA_MODE = "EXTRA_MODE";
    public static final String EXTRA_MARKERS = "EXTRA_MARKERS";
    public static final String EXTRA_CURRENT_LAT = "EXTRA_CURRENT_LAT";
    public static final String EXTRA_CURRENT_LNG = "EXTRA_CURRENT_LNG";
    public static final String RESULT_LATITUDE = "RESULT_LATITUDE";
    public static final String RESULT_LONGITUDE = "RESULT_LONGITUDE";
    public static final String RESULT_MARKER_ID = "RESULT_MARKER_ID";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMapsFragment mapFragment;
    private Button btnConfirm;
    private MapMode mode;

    private ArrayList<MapMarker> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);

        btnConfirm = findViewById(R.id.btnConfirmLocation);
        btnConfirm.setEnabled(false);

        mode = (MapMode) getIntent().getSerializableExtra(EXTRA_MODE);
        if (mode == null) mode = MapMode.PICK_LOCATION;

        markers = getIntent().getParcelableArrayListExtra(EXTRA_MARKERS); // Gán vào biến thành viên

        double currentLat = getIntent().getDoubleExtra(EXTRA_CURRENT_LAT, 0);
        double currentLng = getIntent().getDoubleExtra(EXTRA_CURRENT_LNG, 0);
        if (currentLat != 0 && currentLng != 0) {
            if (markers == null) markers = new ArrayList<>();
            markers.add(new MapMarker("current", currentLat, currentLng, "Current Location"));
        }

        checkAndRequestLocationPermissions();

        setupConfirmButton();
    }

    private void checkAndRequestLocationPermissions() {
        boolean fineGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        boolean coarseGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        if (fineGranted || coarseGranted) {
            setupMapFragment(markers);
        } else {
            // Quyền chưa được cấp
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Người dùng đã đồng ý cấp quyền -> setup bản đồ
                setupMapFragment(markers);
            } else {
                Toast.makeText(this, "Need Location Access to continue", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void setupMapFragment(ArrayList<MapMarker> markersList) {
        if (mapFragment == null) {
            mapFragment = GoogleMapsFragment.newInstance(mode, markersList);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.mapFragmentContainer, mapFragment);
            transaction.commit();
        }
    }

    private void setupConfirmButton() {
        // ... (Giữ nguyên code của bạn) ...
        btnConfirm.setOnClickListener(v -> {
            Intent resultIntent = new Intent();

            if (mode == MapMode.PICK_LOCATION) {
                LatLng location = mapFragment.getPickedLocation();
                if (location != null) {
                    resultIntent.putExtra(RESULT_LATITUDE, location.latitude);
                    resultIntent.putExtra(RESULT_LONGITUDE, location.longitude);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
                }
            } else if (mode == MapMode.SELECT_EXISTING_MARKER) {
                String markerId = mapFragment.getSelectedMarkerId();
                if (markerId != null) {
                    resultIntent.putExtra(RESULT_MARKER_ID, markerId);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(this, "Please select a marker", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapInteraction() {
        btnConfirm.setEnabled(true);
    }
}