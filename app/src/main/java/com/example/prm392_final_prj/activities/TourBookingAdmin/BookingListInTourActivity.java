package com.example.prm392_final_prj.activities.TourBookingAdmin;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.activities.TourBooking.BookingStatusActivity;
import com.example.prm392_final_prj.adapter.BookingListAdapter;
import com.example.prm392_final_prj.adapter.TourBookingListAdapter;
import com.example.prm392_final_prj.entity.BookingOrderEntity;
import com.example.prm392_final_prj.entity.TourEntity;
import com.example.prm392_final_prj.repository.BookingRepository;
import com.example.prm392_final_prj.repository.TourRepository;
import com.example.prm392_final_prj.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookingListInTourActivity extends AppCompatActivity {

    private int tourId;
    private List<BookingListAdapter.BookListData> filtered = new ArrayList<>();

    private BookingRepository bookingRepository;

    private UserRepository userRepository;

    private BookingListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_list_in_tour);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bookingRepository = new BookingRepository(getApplication());
        userRepository = new UserRepository(getApplication());

        // --- Get Intent Data ---
        tourId = getIntent().getIntExtra("tourId", -1);
        String tourName = getIntent().getStringExtra("tourName");
        String tourDate = getIntent().getStringExtra("tourDate");

        // --- UI Setup ---
        TextView tvTourName = findViewById(R.id.tv_tour_name);
        RecyclerView rvBookings = findViewById(R.id.rv_bookings);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));

        tvTourName.setText(tourName + " " + tourDate);

        mockBookings();
        adapter = new BookingListAdapter(this, filtered, bookingRepository);

        rvBookings.setAdapter(adapter);
    }

    private void mockBookings() {
        bookingRepository.getBookingsByTourId(tourId).observe(this, bookingList -> {
            userRepository.getAllUsers().observe(this, users -> {
                Map<Integer, String> userMap = users.stream()
                        .collect(Collectors.toMap(u -> u.id, u -> u.firstname + " " + u.lastname));

                filtered.clear();
                for (BookingOrderEntity b : bookingList) {
                    filtered.add(new BookingListAdapter.BookListData(b, userMap.get(b.userId)));
                }
                adapter.notifyDataSetChanged();
            });
//            var users = userRepository.getAll();
//            Map<Integer, String> userMap = users.stream()
//                    .collect(Collectors.toMap(u -> u.id, u -> u.firstname + " " + u.lastname));
//            BookingListInTourActivity.this.filtered.clear();
//            List<BookingListAdapter.BookListData> result = new ArrayList<>();
//            for(BookingOrderEntity b : bookingList){
//                var data = new BookingListAdapter.BookListData(b, userMap.get(b.userId));
//                result.add(data);
//            }
//            BookingListInTourActivity.this.filtered.addAll(result);
//            adapter.notifyDataSetChanged();
        });
    }
}