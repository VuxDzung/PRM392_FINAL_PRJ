package com.example.prm392_final_prj.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.adapter.ScheduleEditAdapter;
import com.example.prm392_final_prj.entity.TourEntity;
import com.example.prm392_final_prj.entity.TourScheduleEntity;
import com.example.prm392_final_prj.repository.TourRepository;
// SỬA: Thêm import ImageUtils
import com.example.prm392_final_prj.utils.ImageUtils;
import com.example.prm392_final_prj.utils.MapMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AddTourActivity extends NavigationBaseActivity implements ScheduleEditAdapter.OnMapPickListener {

    private EditText etLocation, etDeparture, etDestination, etDuration,
            etPrice, etTransport, etMaxCapacity;
    private Button btnConfirmSave, btnSelectImage;
    private ImageView ivTourImagePreview;

    private TourRepository tourRepository;
    private TourEntity mCurrentTour;
    private String mCurrentPhotoPath = null;
    private RecyclerView scheduleRecyclerView;
    private ScheduleEditAdapter scheduleAdapter;
    private Button btnAddSchedule;
    private List<TourScheduleEntity> schedules = new ArrayList<>();
    private int currentPickingPosition = -1;
    private final ActivityResultLauncher<String> mImagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    handleImagePick(uri);
                }
            }
    );
    private final ActivityResultLauncher<Intent> mapPickerLauncher = registerForActivityResult(
            // ... (code launcher giữ nguyên) ...
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    double latitude = result.getData().getDoubleExtra(MapPickerActivity.RESULT_LATITUDE, 0);
                    double longitude = result.getData().getDoubleExtra(MapPickerActivity.RESULT_LONGITUDE, 0);

                    if (currentPickingPosition >= 0 && scheduleAdapter != null) {
                        scheduleAdapter.updateScheduleCoordinates(currentPickingPosition, latitude, longitude);
                        currentPickingPosition = -1;
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ... (code onCreate giữ nguyên) ...
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tour);

        tourRepository = new TourRepository(getApplication());
        mCurrentTour = new TourEntity();

        bindViews();
        setupScheduleRecyclerView();
        setupBottomNavigation(R.id.nav_home);

        btnConfirmSave.setOnClickListener(v -> saveTour());

        btnSelectImage.setOnClickListener(v -> {
            mImagePickerLauncher.launch("image/*");
        });

        btnAddSchedule.setOnClickListener(v -> {
            TourScheduleEntity newSchedule = new TourScheduleEntity();
            scheduleAdapter.addItem(newSchedule);
        });
    }

    private void bindViews() {
        // ... (code bindViews giữ nguyên) ...
        etLocation = findViewById(R.id.etLocation);
        etDeparture = findViewById(R.id.etDeparture);
        etDestination = findViewById(R.id.etDestination);
        etDuration = findViewById(R.id.etDuration);
        etPrice = findViewById(R.id.etPrice);
        etTransport = findViewById(R.id.etTransport);
        etMaxCapacity = findViewById(R.id.etMaxCapacity);
        btnConfirmSave = findViewById(R.id.btnConfirmSave);
        ivTourImagePreview = findViewById(R.id.ivTourImagePreview);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        scheduleRecyclerView = findViewById(R.id.scheduleEditRecyclerView);
        btnAddSchedule = findViewById(R.id.btnAddSchedule);
    }

    private void setupScheduleRecyclerView() {
        // ... (code setupScheduleRecyclerView giữ nguyên) ...
        scheduleAdapter = new ScheduleEditAdapter(schedules, this);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scheduleRecyclerView.setAdapter(scheduleAdapter);
        scheduleRecyclerView.setNestedScrollingEnabled(false);
    }


    private void handleImagePick(Uri uri) {
        try {

            if (mCurrentPhotoPath != null) {
                ImageUtils.deleteImage(mCurrentPhotoPath);
            }

            mCurrentPhotoPath = ImageUtils.saveTourImage(getApplicationContext(), uri);

            if (mCurrentPhotoPath != null) {

                Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                ivTourImagePreview.setImageBitmap(bitmap);
                ivTourImagePreview.setVisibility(View.VISIBLE);
            } else {
                throw new Exception("ImageUtils failed to save image.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            mCurrentPhotoPath = null;
        }
    }

    private void saveTour() {
        btnConfirmSave.setEnabled(false);

        if (etLocation.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a location name", Toast.LENGTH_SHORT).show();
            btnConfirmSave.setEnabled(true);
            return;
        }

        mCurrentTour.setLocation(etLocation.getText().toString());
        mCurrentTour.setDeparture(etDeparture.getText().toString());
        mCurrentTour.setDestination(etDestination.getText().toString());
        mCurrentTour.setDuration(etDuration.getText().toString());
        mCurrentTour.setTransport(etTransport.getText().toString());
        mCurrentTour.setImagePath(mCurrentPhotoPath);

        try {
            mCurrentTour.setPrice(Double.parseDouble(etPrice.getText().toString()));
        } catch (NumberFormatException e) {
            mCurrentTour.setPrice(0);
        }

        try {
            mCurrentTour.setMaxCapacity(Integer.parseInt(etMaxCapacity.getText().toString()));
        } catch (NumberFormatException e) {
            mCurrentTour.setMaxCapacity(0);
        }

        List<TourScheduleEntity> updatedSchedules = scheduleAdapter.getSchedules();

        tourRepository.insertTour(mCurrentTour, (newTourId) -> {
            for (TourScheduleEntity schedule : updatedSchedules) {
                schedule.setTourId((int) newTourId);
                tourRepository.insertTourSchedule(schedule);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Tour Created Successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    public void onPickLocationClick(int position, TourScheduleEntity schedule) {
        currentPickingPosition = position;
        Intent intent = new Intent(this, MapPickerActivity.class);
        intent.putExtra(MapPickerActivity.EXTRA_MODE, MapMode.PICK_LOCATION);

        if (schedule.hasValidCoordinates()) {
            intent.putExtra(MapPickerActivity.EXTRA_CURRENT_LAT, schedule.getLatitude());
            intent.putExtra(MapPickerActivity.EXTRA_CURRENT_LNG, schedule.getLongitude());
        }
        mapPickerLauncher.launch(intent);
    }
}