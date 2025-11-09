package com.example.prm392_final_prj.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.prm392_final_prj.mockdata.TourMockData;
import com.example.prm392_final_prj.repository.TourRepository;
import com.example.prm392_final_prj.utils.MapMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

public class MapScheduleActivity extends AppCompatActivity {
    public static final String EXTRA_TOUR_ID = "tour_id";

    private TourRepository tourRepository;
    private int tourId = -1;
    private boolean isMapLoaded = false; //flag

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

        loadSchedulesAndSetupMap();
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
                isMapLoaded = true; //flag

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