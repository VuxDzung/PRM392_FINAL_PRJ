package com.example.prm392_final_prj.activities.TourBooking;

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
import com.example.prm392_final_prj.activities.HomeActivity;
import com.example.prm392_final_prj.activities.NavigationBaseActivity;
import com.example.prm392_final_prj.adapter.BookingAdapter;
import com.example.prm392_final_prj.entity.BookingOrderEntity;
import com.example.prm392_final_prj.entity.TourEntity;
import com.example.prm392_final_prj.repository.BookingRepository;
import com.example.prm392_final_prj.repository.TourRepository;
import com.example.prm392_final_prj.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class BookingStatusActivity extends NavigationBaseActivity {

    private RecyclerView recyclerView;
    private View emptyStateContainer;
    private BookingAdapter adapter;
    private List<BookingOrderEntity> bookingList = new ArrayList<>();

    private List<TourEntity> tours = new ArrayList<>();
    private BookingRepository bookingRepository;
    private TourRepository tourRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_status);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupBottomNavigation(R.id.nav_more);

        bookingRepository = new BookingRepository(getApplication());
        tourRepository = new TourRepository(getApplication());
        sessionManager = new SessionManager(this);

        recyclerView = findViewById(R.id.bookings_recycler_view);
        emptyStateContainer = findViewById(R.id.empty_state_container);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookingAdapter(bookingList, tours, this);
        recyclerView.setAdapter(adapter);

        loadBookings();
    }

    private void loadBookings() {
        var userId = sessionManager.getUserId();
        bookingRepository.getBookingsForUser(userId).observe(this, bookings -> {
            BookingStatusActivity.this.bookingList.clear();
            BookingStatusActivity.this.bookingList.addAll(bookings);

            tourRepository.getAllTours().observe(this, toursList -> {
                BookingStatusActivity.this.tours.clear();
                BookingStatusActivity.this.tours.addAll(toursList);

                if (bookingList.isEmpty()) {
                    emptyStateContainer.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyStateContainer.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            });
        });
    }
}