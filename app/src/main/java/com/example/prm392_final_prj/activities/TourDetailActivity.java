package com.example.prm392_final_prj.activities;

import android.content.Intent;
// SỬA: Import ImageUtils
import com.example.prm392_final_prj.utils.ImageUtils;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.entity.TourEntity;
import com.example.prm392_final_prj.mockdata.TourMockData;
import com.example.prm392_final_prj.repository.TourRepository;

import java.util.Locale;

public class TourDetailActivity extends NavigationBaseActivity {

    public static final String EXTRA_TOUR_ID = "tour_id";
    private TourRepository repository;

    // Mockdata for testing
    private TourMockData mockData = new TourMockData();
    private int tourId = -1;

    private ImageView detailImage;
    private TextView detailLocation;
    private TextView detailDuration;
    private TextView detailPrice;
    private TextView detailRoute;
    private TextView detailTransport;
    private TextView detailSeatsAvailable;

    private Button bookBtn;
    private Button scheduleBtn;
    private Button reviewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);

        //set up for bottom nav
        setupBottomNavigation(R.id.nav_home);

        repository = new TourRepository(getApplication());

        detailImage = findViewById(R.id.detail_tour_image);
        detailLocation = findViewById(R.id.detail_tour_location);
        detailDuration = findViewById(R.id.detail_duration);
        detailPrice = findViewById(R.id.detail_price);
        detailRoute = findViewById(R.id.detail_route);
        detailTransport = findViewById(R.id.detail_transport);
        detailSeatsAvailable = findViewById(R.id.detail_seats_available);

        if (getIntent().getExtras() != null) {
            tourId = getIntent().getIntExtra(EXTRA_TOUR_ID, -1);
            if (tourId != -1) {
                loadTourDetails(tourId);
            }
        }

        bookBtn = findViewById(R.id.btn_booking_to_cart);
        scheduleBtn = findViewById(R.id.btn_view_schedule);
        reviewBtn = findViewById(R.id.btn_give_feedback);

        bookBtn.setOnClickListener(v -> onBookingClick());
        scheduleBtn.setOnClickListener(v -> onScheduleClick());
        reviewBtn.setOnClickListener(v -> onReviewClick());
    }

    private void loadTourDetails(int id) {
        repository.getTourById(id).observe(this, new Observer<TourEntity>() {
            @Override
            public void onChanged(TourEntity tour) {
                if (tour != null) {
                    displayTourDetails(tour);
                } else {
                    // Mockdata for testing
                    var mockTour = mockData.getTourById(id);
                    if (mockTour != null) {
                        displayTourDetails(mockTour);
                    } else {
                        Toast.makeText(TourDetailActivity.this, "Tour not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void displayTourDetails(TourEntity tour) {
        detailLocation.setText(tour.location);
        detailDuration.setText(tour.duration);
        detailPrice.setText(String.format(Locale.US, "$%.2f", tour.price));
        detailTransport.setText(tour.transport);

        String routeType = tour.airway ? "round-trip" : "one-way";
        String route = String.format("%s -> %s %s",
                tour.departure, tour.destination, routeType);
        detailRoute.setText(route);


        detailSeatsAvailable.setText(String.format(Locale.US, "%d seats", tour.maxCapacity));

        ImageUtils.loadImageIntoView(tour.imagePath, detailImage, R.drawable.ic_launcher_background);
    }

    private void onReviewClick(){
        Intent intent = new Intent(this, FeedbackActivity.class);
        intent.putExtra(EXTRA_TOUR_ID, tourId);
        startActivity(intent);
    }

    private void onScheduleClick(){
        Intent intent = new Intent(this, MapScheduleActivity.class);
        intent.putExtra(EXTRA_TOUR_ID, tourId);
        startActivity(intent);
    }

    private void onBookingClick(){
        Toast.makeText(this, "Booking: " + detailLocation.getText(), Toast.LENGTH_SHORT).show();
    }
}