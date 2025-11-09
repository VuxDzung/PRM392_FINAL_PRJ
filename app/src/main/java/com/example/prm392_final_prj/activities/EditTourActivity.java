package com.example.prm392_final_prj.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.prm392_final_prj.dao.AppDatabase;
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

public class EditTourActivity extends NavigationBaseActivity implements ScheduleEditAdapter.OnMapPickListener {

    public static final String EXTRA_TOUR_ID = "EXTRA_TOUR_ID";
    private static final int DEFAULT_TOUR_ID = -1;

    private EditText etLocation, etDeparture, etDestination, etDuration,
            etPrice, etTransport, etMaxCapacity;
    private Button btnConfirmSave, btnSelectImage, btnAddSchedule;
    private ImageView ivTourImagePreview;
    private RecyclerView scheduleRecyclerView;

    private TourRepository tourRepository;
    private int mTourId = DEFAULT_TOUR_ID;
    private TourEntity mCurrentTour;
    private String mCurrentPhotoPath = null;

    private ScheduleEditAdapter scheduleAdapter;
    private List<TourScheduleEntity> schedules = new ArrayList<>();
    private int currentPickingPosition = -1;
    private boolean isSaving = false;
    private final ActivityResultLauncher<String> mImagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> { if (uri != null) handleImagePick(uri); }
    );
    private final ActivityResultLauncher<Intent> mapPickerLauncher = registerForActivityResult(
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tour);

        tourRepository = new TourRepository(getApplication());
        bindViews();
        setupScheduleRecyclerView();
        setupBottomNavigation(R.id.nav_home);

        mTourId = getIntent().getIntExtra(EXTRA_TOUR_ID, DEFAULT_TOUR_ID);
        if (mTourId == DEFAULT_TOUR_ID) {
            Toast.makeText(this, "Error: Tour ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadTourData();
        btnConfirmSave.setText("Save Changes");
        btnConfirmSave.setOnClickListener(v -> saveTour());
        btnSelectImage.setOnClickListener(v -> mImagePickerLauncher.launch("image/*"));
        btnAddSchedule.setOnClickListener(v -> scheduleAdapter.addItem(new TourScheduleEntity()));
    }

    private void bindViews() {
        etLocation = findViewById(R.id.etLocation);
        etDeparture = findViewById(R.id.etDeparture);
        etDestination = findViewById(R.id.etDestination);
        etDuration = findViewById(R.id.etDuration);
        etPrice = findViewById(R.id.etPrice);
        etTransport = findViewById(R.id.etTransport);
        etMaxCapacity = findViewById(R.id.etMaxCapacity);
        btnConfirmSave = findViewById(R.id.btnConfirmSave);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        ivTourImagePreview = findViewById(R.id.ivTourImagePreview);
        scheduleRecyclerView = findViewById(R.id.scheduleEditRecyclerView);
        btnAddSchedule = findViewById(R.id.btnAddSchedule);
    }

    private void setupScheduleRecyclerView() {
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
                ivTourImagePreview.setVisibility(ImageView.VISIBLE);
            } else {
                throw new Exception("ImageUtils failed to save image.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();

        }
    }

    private void loadTourData() {
        tourRepository.getTourById(mTourId).observe(this, tour -> {
            if (tour != null) {
                mCurrentTour = tour;
                // Lấy đường dẫn ảnh hiện tại từ DB
                mCurrentPhotoPath = tour.getImagePath();
                populateUi(tour);
            }
        });

        tourRepository.getSchedulesForTour(mTourId).observe(this, schedulesList -> {
            if (isSaving) return;
            if (schedulesList != null) {
                schedules.clear();
                schedules.addAll(schedulesList);
                scheduleAdapter.setSchedules(schedulesList);
            }
        });
    }


    private void populateUi(TourEntity tour) {
        etLocation.setText(tour.getLocation());
        etDeparture.setText(tour.getDeparture());
        etDestination.setText(tour.getDestination());
        etDuration.setText(tour.getDuration());
        etPrice.setText(String.valueOf(tour.getPrice()));
        etTransport.setText(tour.getTransport());
        etMaxCapacity.setText(String.valueOf(tour.getMaxCapacity()));

        // if (mCurrentPhotoPath != null && !mCurrentPhotoPath.isEmpty()) {
        //    Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        //    ivTourImagePreview.setImageBitmap(bitmap);
        //    ivTourImagePreview.setVisibility(ImageView.VISIBLE);
        // } else {
        //    ivTourImagePreview.setVisibility(ImageView.GONE);
        // }


        ImageUtils.loadImageIntoView(mCurrentPhotoPath, ivTourImagePreview, R.drawable.ic_launcher_background);
    }

    private void saveTour() {
        if (mCurrentTour == null) return;

        if (scheduleRecyclerView.getFocusedChild() != null)
            scheduleRecyclerView.getFocusedChild().clearFocus();

        isSaving = true;
        btnConfirmSave.setEnabled(false);

        mCurrentTour.setLocation(etLocation.getText().toString());
        mCurrentTour.setDeparture(etDeparture.getText().toString());
        mCurrentTour.setDestination(etDestination.getText().toString());
        mCurrentTour.setDuration(etDuration.getText().toString());
        mCurrentTour.setTransport(etTransport.getText().toString());

        mCurrentTour.setImagePath(mCurrentPhotoPath);

        try {
            mCurrentTour.setPrice(Double.parseDouble(etPrice.getText().toString()));
        } catch (NumberFormatException ignored) {}
        try {
            mCurrentTour.setMaxCapacity(Integer.parseInt(etMaxCapacity.getText().toString()));
        } catch (NumberFormatException ignored) {}

        scheduleAdapter.notifyDataSetChanged();
        List<TourScheduleEntity> updatedSchedules = scheduleAdapter.getSchedules();

        AppDatabase.databaseWriteExecutor.execute(() -> {
            tourRepository.updateTour(mCurrentTour);
            tourRepository.syncSchedulesForTour(mTourId, updatedSchedules);

            runOnUiThread(() -> {
                isSaving = false;
                Toast.makeText(this, "Tour Updated Successfully", Toast.LENGTH_SHORT).show();
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