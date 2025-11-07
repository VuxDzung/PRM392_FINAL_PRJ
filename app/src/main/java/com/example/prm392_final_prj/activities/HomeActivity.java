package com.example.prm392_final_prj.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.adapter.TourListAdapter;
import com.example.prm392_final_prj.entity.TourEntity;
import com.example.prm392_final_prj.mockdata.TourMockData;
import com.example.prm392_final_prj.repository.TourRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HomeActivity extends NavigationBaseActivity {
    private List<TourEntity> tourList = new ArrayList<>();
    private List<TourEntity> allTours = new ArrayList<>();
    private TourListAdapter adapter;
    private TourRepository repository;
    // Mockdata for testing
    private TourMockData mockData = new TourMockData();

    //spinner
    private Spinner spinnerPriceRange;
    private Spinner spinnerTripDuration;
    private Spinner spinnerDeparture;
    private Spinner spinnerTransport;
    private Spinner spinnerAirway;
    private Spinner spinnerSeats;
    private EditText searchEditText;
    private FrameLayout searchIconContainer;

    //filter current data
    private String currentPriceFilter = "Mức giá";
    private String currentDurationFilter = "Loại hình";
    private String currentDepartureFilter = "Địa điểm đi";
    private String currentTransportFilter = "Phương tiện";
    private String currentAirwayFilter = "Loại chuyến bay";
    private String currentSeatsFilter = "Số lượng khách";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //set up for bottom nav
        setupBottomNavigation(R.id.nav_home);

        RecyclerView recyclerView = findViewById(R.id.tour_recycler_view); // ID từ activity_home.xml
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TourListAdapter(tourList, this);
        recyclerView.setAdapter(adapter);

        repository = new TourRepository(getApplication());

        spinnerPriceRange = findViewById(R.id.spinner_price_range);
        spinnerTripDuration = findViewById(R.id.spinner_trip_duration);
        spinnerDeparture = findViewById(R.id.spinner_departure);
        spinnerTransport = findViewById(R.id.spinner_transport);
        spinnerAirway = findViewById(R.id.spinner_airway);
        spinnerSeats = findViewById(R.id.spinner_seats);

        searchEditText = findViewById(R.id.search_edit_text);
        searchIconContainer = findViewById(R.id.search_icon_container);

        searchIconContainer.setOnClickListener(v -> {
            applyFilters();
        });

        setupSpinnerListeners();

        repository.getAllTours().observe(this, new Observer<List<TourEntity>>() {
            @Override
            public void onChanged(List<TourEntity> tours) {
                HomeActivity.this.allTours = tours;

                //add fake data for testing
                if (tours == null || tours.isEmpty()) {
                    allTours = mockData.getAllTours();
                }

                updateDynamicSpinners(allTours);
                applyFilters();

            }
        });
    }

    private void updateDynamicSpinners(List<TourEntity> tours) {
        // 1. Departure
        Set<String> departures = tours.stream().map(t -> t.departure).collect(Collectors.toSet());
        List<String> departureOptions = new ArrayList<>(departures);
        departureOptions.add(0, "Địa điểm đi");
        ArrayAdapter<String> departureAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departureOptions);
        departureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeparture.setAdapter(departureAdapter);

        // 2. Transport
        Set<String> transports = tours.stream().map(t -> t.transport).collect(Collectors.toSet());
        List<String> transportOptions = new ArrayList<>(transports);
        transportOptions.add(0, "Phương tiện");
        ArrayAdapter<String> transportAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, transportOptions);
        transportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTransport.setAdapter(transportAdapter);
    }

    private void setupSpinnerListeners() {
        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent == spinnerPriceRange) currentPriceFilter = parent.getItemAtPosition(position).toString();
                else if (parent == spinnerTripDuration) currentDurationFilter = parent.getItemAtPosition(position).toString();
                else if (parent == spinnerDeparture) currentDepartureFilter = parent.getItemAtPosition(position).toString();
                else if (parent == spinnerTransport) currentTransportFilter = parent.getItemAtPosition(position).toString();
                else if (parent == spinnerAirway) currentAirwayFilter = parent.getItemAtPosition(position).toString();
                else if (parent == spinnerSeats) currentSeatsFilter = parent.getItemAtPosition(position).toString();

                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerPriceRange.setOnItemSelectedListener(filterListener);
        spinnerTripDuration.setOnItemSelectedListener(filterListener);
        spinnerDeparture.setOnItemSelectedListener(filterListener);
        spinnerTransport.setOnItemSelectedListener(filterListener);
        spinnerAirway.setOnItemSelectedListener(filterListener);
        spinnerSeats.setOnItemSelectedListener(filterListener);
    }

    private void applyFilters() {
        if (allTours.isEmpty()) return;

        String searchText = searchEditText.getText().toString().trim().toLowerCase();

        List<TourEntity> filteredList = allTours.stream()
                .filter(this::filterByPrice)
                .filter(this::filterByDuration)
                .filter(this::filterByDeparture)
                .filter(this::filterByTransport)
                .filter(this::filterByAirway)
                .filter(this::filterBySeats)
                .filter(tour -> filterBySearch(tour, searchText))
                .collect(Collectors.toList());

        tourList.clear();
        tourList.addAll(filteredList);
        adapter.setTourList(tourList);
        adapter.notifyDataSetChanged();
    }

    private boolean filterBySearch(TourEntity tour, String searchText) {
        if (searchText.isEmpty()) {
            return true;
        }

        String location = tour.location != null ? tour.location.toLowerCase() : "";
        String departure = tour.departure != null ? tour.departure.toLowerCase() : "";
        String destination = tour.destination != null ? tour.destination.toLowerCase() : "";

        return location.contains(searchText) ||
                departure.contains(searchText) ||
                destination.contains(searchText);
    }

    private boolean filterByPrice(TourEntity tour) {
        if (currentPriceFilter.equals("Mức giá")) {
            return true;
        }
        double price = tour.getPrice();

        if (currentPriceFilter.equals("Dưới $100")) {
            return price < 100;
        } else if (currentPriceFilter.equals("Trên $500")) {
            return price > 500;
        } else if (currentPriceFilter.contains("-")) {
            try {
                String range = currentPriceFilter.replace("$", "").trim();
                String[] parts = range.split(" - ");
                double min = Double.parseDouble(parts[0]);
                double max = Double.parseDouble(parts[1]);
                return price >= min && price <= max;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    private boolean filterByDuration(TourEntity tour) {
        if (currentDurationFilter.equals("Loại hình")) {
            return true;
        }
        else{
            var duration = calculateDuration(tour.getDuration());
            if(currentDurationFilter.equals("Short trip (1-3 Ngày)")){
                return duration <= 3;
            }else if(currentDurationFilter.equals("Mid-range trip (4-6 Ngày)")){
                return duration > 3 && duration <= 6;
            }else if (currentDurationFilter.equals("Long trip (Hơn 6 Ngày)")){
                return duration > 6;
            }else return true;
        }
    }
    private double calculateDuration(String duration) {
        try {
            String range = duration.replaceAll("[^\\d-]", "").trim();
            String[] parts = range.split("-");
            double min = Double.parseDouble(parts[0]);
            double max = parts.length > 1 ? Double.parseDouble(parts[1]) : min;
            return (max + min) / 2;
        } catch (Exception e){
            return 0;
        }
    }

    private boolean filterByDeparture(TourEntity tour) {
        if (currentDepartureFilter.equals("Địa điểm đi")) {
            return true;
        }
        return tour.departure.equals(currentDepartureFilter);
    }

    private boolean filterByTransport(TourEntity emulateTour) {
        if (currentTransportFilter.equals("Phương tiện")) {
            return true;
        }
        return emulateTour.transport.equals(currentTransportFilter);
    }

    private boolean filterByAirway(TourEntity tour) {
        if (currentAirwayFilter.equals("Loại chuyến")) {
            return true;
        }
        if (currentAirwayFilter.equals("Hai chiều")) {
            return tour.airway;
        }
        if (currentAirwayFilter.equals("Một chiều")) {
            return !tour.airway;
        }
        return false;
    }

    private boolean filterBySeats(TourEntity tour) {
        if (currentSeatsFilter.equals("Lượng khách")) {
            return true;
        }

        if (currentSeatsFilter.equals("Đơn")) {
            return tour.maxCapacity >= 1;
        } else if (currentSeatsFilter.equals("Đôi")) {
            return tour.maxCapacity >= 2;
        } else if (currentSeatsFilter.equals("Gia đình")) {
            return tour.maxCapacity >= 3;
        } else if (currentSeatsFilter.equals("Nhóm lớn")) {
            return tour.maxCapacity >= 8;
        } else if (currentSeatsFilter.equals("Đoàn khách")) {
            return tour.maxCapacity >= 30;
        }

        return false;
    }

    public void onSeeMoreClick(TourEntity tour) {
        Intent intent = new Intent(this, TourDetailActivity.class);
        intent.putExtra(TourDetailActivity.EXTRA_TOUR_ID, tour.id);

        startActivity(intent);
    }
    public void onBookingClick(TourEntity tour) {
        Toast.makeText(this, "Booking: " + tour.getLocation(), Toast.LENGTH_SHORT).show();
    }
}