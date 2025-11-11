package com.example.prm392_final_prj.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;


import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.activities.TourBooking.TourPurchaseActivity;
import com.example.prm392_final_prj.adapter.TourListAdapter;
import com.example.prm392_final_prj.entity.TourEntity;
import com.example.prm392_final_prj.mockdata.TourMockData;
import com.example.prm392_final_prj.repository.TourRepository;

import java.util.ArrayList;
import java.text.Normalizer;
import java.util.regex.Pattern;
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
    private FrameLayout micIconContainer;

    private String currentPriceFilter = "Mức giá";
    private String currentDurationFilter = "Loại hình";
    private String currentDepartureFilter = "Địa điểm đi";
    private String currentTransportFilter = "Phương tiện";
    private String currentAirwayFilter = "Loại chuyến bay";
    private String currentSeatsFilter = "Số lượng khách";

    private static final int RECORD_AUDIO_PERMISSION_REQUEST_CODE = 201;


    private final ActivityResultLauncher<Intent> voiceSearchResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (results != null && !results.isEmpty()) {
                        String spokenText = results.get(0);
                        searchEditText.setText(spokenText);
                        applyFilters();
                    }
                } else {
                    Toast.makeText(this, "Không nhận dạng được giọng nói.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupBottomNavigation(R.id.nav_home);

        RecyclerView recyclerView = findViewById(R.id.tour_recycler_view);
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

        micIconContainer = findViewById(R.id.mic_icon_container);

        micIconContainer.setOnClickListener(v -> {
            checkAndRequestAudioPermission();
        });

        setupSpinnerListeners();

        repository.getAllTours().observe(this, tours -> {
            HomeActivity.this.allTours = tours;

            //add fake data for testing
            if (tours == null || tours.isEmpty()) {
                allTours = mockData.getAllTours();
                for (TourEntity tour : allTours) {
                    //fix insert fake data
                    repository.insertTour(tour, null);
                }
            }

            updateDynamicSpinners(allTours);
            applyFilters();
        });
    }

    private void checkAndRequestAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            startVoiceSearch();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceSearch();
            } else {
                Toast.makeText(this, "Cần cấp quyền ghi âm để sử dụng tính năng này.", Toast.LENGTH_SHORT).show();
            }
        }
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

        String rawSearchText = searchEditText.getText().toString();
        String normalizedSearchText = normalizeString(rawSearchText);

        List<TourEntity> filteredList = allTours.stream()
                .filter(this::filterByPrice)
                .filter(this::filterByDuration)
                .filter(this::filterByDeparture)
                .filter(this::filterByTransport)
                .filter(this::filterByAirway)
                .filter(this::filterBySeats)
                .filter(tour -> filterBySearch(tour, normalizedSearchText))
                .collect(Collectors.toList());

        tourList.clear();
        tourList.addAll(filteredList);
        adapter.setTourList(tourList);
        adapter.notifyDataSetChanged();
    }

    private String normalizeString(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        String temp = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").toLowerCase().replaceAll("\\s+", "");
    }

    private boolean filterBySearch(TourEntity tour, String searchText) {
        if (searchText.isEmpty()) {
            return true;
        }

        String normalizedSearchText = normalizeString(searchText);

        String normalizedLocation = normalizeString(tour.location);
        String normalizedDestination = normalizeString(tour.destination);

        return normalizedLocation.contains(normalizedSearchText) ||
                normalizedDestination.contains(normalizedSearchText);
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

    private void startVoiceSearch() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN");

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Nói địa điểm hoặc tên tour cần tìm...");

        try {
            voiceSearchResultLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Thiết bị không hỗ trợ ghi âm giọng nói.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSeeMoreClick(TourEntity tour) {
        Intent intent = new Intent(this, TourDetailActivity.class);
        intent.putExtra(TourDetailActivity.EXTRA_TOUR_ID, tour.id);

        startActivity(intent);
    }
    public void onBookingClick(TourEntity tour) {
        Intent intent = new Intent(this, TourPurchaseActivity.class);
        intent.putExtra(TourDetailActivity.EXTRA_TOUR_ID, tour.id);

        startActivity(intent);
    }
}