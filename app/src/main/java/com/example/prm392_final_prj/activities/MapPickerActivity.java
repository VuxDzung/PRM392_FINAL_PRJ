package com.example.prm392_final_prj.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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

    private GoogleMapsFragment mapFragment;
    private Button btnConfirm;
    private MapMode mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);

        btnConfirm = findViewById(R.id.btnConfirmLocation);
        btnConfirm.setEnabled(false);

        mode = (MapMode) getIntent().getSerializableExtra(EXTRA_MODE);
        if (mode == null) mode = MapMode.PICK_LOCATION;

        ArrayList<MapMarker> markers = getIntent().getParcelableArrayListExtra(EXTRA_MARKERS);

        double currentLat = getIntent().getDoubleExtra(EXTRA_CURRENT_LAT, 0);
        double currentLng = getIntent().getDoubleExtra(EXTRA_CURRENT_LNG, 0);
        if (currentLat != 0 && currentLng != 0) {
            if (markers == null) markers = new ArrayList<>();
            markers.add(new MapMarker("current", currentLat, currentLng, "Vị trí hiện tại"));
        }

        setupMapFragment(markers);
        setupConfirmButton();
    }

    private void setupMapFragment(ArrayList<MapMarker> markers) {
        mapFragment = GoogleMapsFragment.newInstance(mode, markers);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mapFragmentContainer, mapFragment);
        transaction.commit();
    }

    private void setupConfirmButton() {
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
                    Toast.makeText(this, "Vui lòng chọn vị trí trên bản đồ", Toast.LENGTH_SHORT).show();
                }
            } else if (mode == MapMode.SELECT_EXISTING_MARKER) {
                String markerId = mapFragment.getSelectedMarkerId();
                if (markerId != null) {
                    resultIntent.putExtra(RESULT_MARKER_ID, markerId);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(this, "Vui lòng chọn một địa điểm", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapInteraction() {
        btnConfirm.setEnabled(true);
    }
}