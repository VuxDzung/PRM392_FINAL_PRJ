package com.example.prm392_final_prj.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.adapter.TourScheduleAdapter;
import com.example.prm392_final_prj.dto.request.MapMarker;
import com.example.prm392_final_prj.entity.TourScheduleEntity;
import com.example.prm392_final_prj.fragment.GoogleMapsFragment;
import com.example.prm392_final_prj.mockdata.ScheduleMockData;
import com.example.prm392_final_prj.repository.TourRepository;
import com.example.prm392_final_prj.utils.MapMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

public class MapScheduleActivity extends AppCompatActivity {
    public static final String EXTRA_TOUR_ID = "tour_id";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private TourRepository tourRepository;
    private int tourId = -1;
    private boolean isMapLoaded = false;

    // Mockdata for testing
    private ScheduleMockData mockData = new ScheduleMockData();

    private RecyclerView recyclerViewSchedule;
    private TourScheduleAdapter scheduleAdapter;
    private GoogleMapsFragment mapFragment;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tourRepository = new TourRepository(getApplication());

        tourId = getIntent().getIntExtra(EXTRA_TOUR_ID, -1);

        if (tourId == -1) {
            Toast.makeText(this, "Invalid Tour ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerViewSchedule = findViewById(R.id.recycler_view_schedule);
        scheduleAdapter = new TourScheduleAdapter(this);
        recyclerViewSchedule.setAdapter(scheduleAdapter);
        recyclerViewSchedule.setLayoutManager(new LinearLayoutManager(this));

        LinearLayout bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setPeekHeight(300);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        checkAndRequestLocationPermissions();

    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = NavUtils.getParentActivityIntent(this);

        if (intent != null) {
            intent.putExtra(TourDetailActivity.EXTRA_TOUR_ID, tourId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NavUtils.navigateUpTo(this, intent);
        } else {
            finish();
        }

        return true;
    }


    private void checkAndRequestLocationPermissions() {
        boolean fineGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
        boolean coarseGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        if (fineGranted || coarseGranted) {
            loadSchedulesAndSetupMap();
        } else {
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
            boolean fineGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;
            boolean coarseGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED;

            if (fineGranted || coarseGranted) {
                loadSchedulesAndSetupMap();
            } else {
                Toast.makeText(this, "Need Location Access to display map", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    private void loadSchedulesAndSetupMap() {
        tourRepository.getSchedulesForTour(tourId).observe(this, schedules -> {
            if (schedules == null || schedules.isEmpty()) {
                schedules = mockData.getSchedulesForTour(tourId);
//                Toast.makeText(this, "No schedules found for this tour", Toast.LENGTH_SHORT).show();
//                return;
            }

            if (scheduleAdapter != null) {
                scheduleAdapter.setSchedules(schedules);
            }

            if (!isMapLoaded) {
                isMapLoaded = true;

                ArrayList<MapMarker> mapMarkers = new ArrayList<>();
                for (TourScheduleEntity schedule : schedules) {
                    if (schedule.hasValidCoordinates()) {
                        mapMarkers.add(new MapMarker(
                                String.valueOf(schedule.getId()),
                                schedule.getLatitude(),
                                schedule.getLongitude(),
                                schedule.getPlace()
                        ));
                    }
                }

                if (mapMarkers.isEmpty()) {
                    Toast.makeText(this, "No schedules with locations to display", Toast.LENGTH_SHORT).show();
                    return;
                }

                mapFragment = GoogleMapsFragment.newInstance(MapMode.VIEW, mapMarkers);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.map_container, mapFragment)
                        .commit();
            }
        });
    }

    public void onScheduleClick(double latitude, double longitude) {
        if (mapFragment != null) {
            mapFragment.moveCameraTo(latitude, longitude, 16f);
        }

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
}