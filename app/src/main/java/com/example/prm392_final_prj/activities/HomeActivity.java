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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.adapter.TourListAdapter;
import com.example.prm392_final_prj.entity.TourEntity;
import com.example.prm392_final_prj.repository.TourRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class HomeActivity extends AppCompatActivity {
    private List<TourEntity> tourList = new ArrayList<>();
    private List<TourEntity> allTours = new ArrayList<>();
    private TourListAdapter adapter;
    private TourRepository repository;

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

    private static WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), HomeActivity::onApplyWindowInsets);

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
                    addMockDataForDemo();
                }

                updateDynamicSpinners(tours);
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
            return tour.availableSeat >= 1;
        } else if (currentSeatsFilter.equals("Đôi")) {
            return tour.availableSeat >= 2;
        } else if (currentSeatsFilter.equals("Gia đình")) {
            return tour.availableSeat >= 3;
        } else if (currentSeatsFilter.equals("Nhóm lớn")) {
            return tour.availableSeat >= 8;
        } else if (currentSeatsFilter.equals("Đoàn khách")) {
            return tour.availableSeat >= 30;
        }

        return false;
    }

    private void addMockDataForDemo() {
        allTours.clear();

        allTours.add(new TourEntity("Ha long Bay", "4 Days - 3 Nights", 120.34, "Ha Noi", "Ha Long", false, "Bus", null, 50, 20));
        allTours.add(new TourEntity("Da Nang", "2 Days - 1 Nights", 185.75, "Ha Noi", "Da Nang", true, "Airplane", null, 40, 15));
        allTours.add(new TourEntity("Sapa Mist Escape", "3 Days - 2 Nights", 95.00, "Ha Noi", "Sapa", false, "Bus", null, 30, 10)); // Dưới $100
        allTours.add(new TourEntity("Hanoi Old Quarter", "1 Day", 49.99, "Ha Noi", "Hanoi", false, "Walking", null, 15, 5)); // Dưới $100
        allTours.add(new TourEntity("Nha Trang Mini", "4 Days - 3 Nights", 250.00, "HCM City", "Nha Trang", true, "Airplane", null, 35, 12));


        allTours.add(new TourEntity("Central Heritage", "5 Days - 4 Nights", 320.50, "Da Nang", "Hue", true, "Airplane", null, 25, 8));
        allTours.add(new TourEntity("Da Lat Flower", "6 Days - 5 Nights", 410.75, "HCM City", "Da Lat", true, "Airplane", null, 20, 7));
        allTours.add(new TourEntity("Mekong River Tour", "7 Days - 6 Nights", 499.00, "HCM City", "Can Tho", false, "Cruise", null, 18, 5));


        allTours.add(new TourEntity("Vietnam Grand Tour", "10 Days - 9 Nights", 980.00, "Ha Noi", "HCM City", true, "Airplane", null, 12, 4)); // Trên $500
        allTours.add(new TourEntity("Southeast Asia", "14 Days - 13 Nights", 1500.00, "Ha Noi", "Bangkok", true, "Airplane", null, 10, 2)); // Trên $500
        allTours.add(new TourEntity("Highlands Trek", "8 Days - 7 Nights", 550.00, "HCM City", "Dalat", false, "Bus", null, 15, 6)); // Trên $500


        allTours.add(new TourEntity("Ninh Binh Karst", "Weekend Getaway", 150.00, "Ha Noi", "Ninh Binh", false, "Bus", null, 45, 20)); // Weekend Getaway
        allTours.add(new TourEntity("Vung Tau Relax", "Weekend Getaway", 180.00, "HCM City", "Vung Tau", false, "Car", null, 50, 25)); // Weekend Getaway

        adapter.setTourList(allTours);
        adapter.notifyDataSetChanged();
    }
    public void onSeeMoreClick(TourEntity tour) {
        Intent intent = new Intent(this, TourDetailActivity.class);

        intent.putExtra(TourDetailActivity.EXTRA_TOUR_ID, tour.id);
        intent.putExtra(TourDetailActivity.EXTRA_TOUR_LOCATION, tour.location);
        intent.putExtra(TourDetailActivity.EXTRA_TOUR_DURATION, tour.duration);
        intent.putExtra(TourDetailActivity.EXTRA_TOUR_TRANSPORT, tour.transport);

        intent.putExtra(TourDetailActivity.EXTRA_TOUR_PRICE, String.format(Locale.US, "$%.2f", tour.price));
        intent.putExtra(TourDetailActivity.EXTRA_TOUR_MAX_CAPACITY, tour.maxCapacity);
        intent.putExtra(TourDetailActivity.EXTRA_TOUR_AVAILABLE_SEAT, tour.availableSeat);

        String routeType = tour.airway ? "round-trip" : "one-way";
        String route = String.format("%s -> %s (%s)",
                tour.getDeparture(), tour.getDestination(), routeType);
        intent.putExtra(TourDetailActivity.EXTRA_TOUR_ROUTE, route);

        intent.putExtra(TourDetailActivity.EXTRA_TOUR_IMAGE_BYTES, tour.image);

        startActivity(intent);
    }
    public void onBookingClick(TourEntity tour) {
        Toast.makeText(this, "Booking: " + tour.getLocation(), Toast.LENGTH_SHORT).show();
    }
}