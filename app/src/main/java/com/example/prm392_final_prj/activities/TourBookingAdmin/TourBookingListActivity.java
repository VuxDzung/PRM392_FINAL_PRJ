package com.example.prm392_final_prj.activities.TourBookingAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.activities.AdminNavBaseActivity;
import com.example.prm392_final_prj.activities.TourBooking.BookingStatusActivity;
import com.example.prm392_final_prj.adapter.TourBookingListAdapter;
import com.example.prm392_final_prj.entity.TourEntity;
import com.example.prm392_final_prj.repository.TourRepository;

import java.util.ArrayList;
import java.util.List;

public class TourBookingListActivity extends AdminNavBaseActivity {

    private List<TourEntity> tours = new ArrayList<>();

    private TourRepository tourRepository;

    private TourBookingListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tour_booking_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupBottomNavigation(R.id.nav_bookings);

        tourRepository = new TourRepository(getApplication());

        RecyclerView rvTours = findViewById(R.id.rv_tours);
        rvTours.setLayoutManager(new LinearLayoutManager(this));

        // Set adapter
        adapter = new TourBookingListAdapter(this, tours);

        rvTours.setAdapter(adapter);

        loadTours();
    }

    private void loadTours() {
        tourRepository.getAllTours().observe(this, toursList -> {
            TourBookingListActivity.this.tours.clear();
            TourBookingListActivity.this.tours.addAll(toursList);
            adapter.notifyDataSetChanged();
        });
    }
}