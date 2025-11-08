package com.example.prm392_final_prj.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout; // CHANGED: Import LinearLayout
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.adapter.AdminTourListAdapter;
import com.example.prm392_final_prj.entity.TourEntity;
import com.example.prm392_final_prj.repository.TourRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

// CHANGED: Removed unused import android.widget.TextView

public class ManageTourActivity extends NavigationBaseActivity implements AdminTourListAdapter.OnAdminTourItemClickListener {

    private RecyclerView recyclerView;
    private AdminTourListAdapter adapter;
    private TourRepository tourRepository;
    private LinearLayout llEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_tour);

        tourRepository = new TourRepository(getApplication());

        setupViews();
        setupRecyclerView();
        setupBottomNavigation(R.id.nav_home);

        FloatingActionButton fabAddTour = findViewById(R.id.fabAddTour);
        fabAddTour.setOnClickListener(v -> {
            Intent intent = new Intent(ManageTourActivity.this, AddTourActivity.class);
            startActivity(intent);
        });

        observeTours();
    }

    private void setupViews() {
        llEmptyState = findViewById(R.id.tvEmptyState);
        recyclerView = findViewById(R.id.tourRecyclerView);
    }

    private void setupRecyclerView() {
        adapter = new AdminTourListAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void observeTours() {
        tourRepository.getAllTours().observe(this, (List<TourEntity> tours) -> {
            if (tours != null) {
                adapter.setTours(tours);

                if (tours.isEmpty()) {
                    llEmptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    llEmptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onEditClick(TourEntity tour) {
        Intent intent = new Intent(this, EditTourActivity.class);
        intent.putExtra(EditTourActivity.EXTRA_TOUR_ID, tour.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(TourEntity tour) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Tour")
                .setMessage("Do you want to delete tour \"" + tour.getLocation() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    tourRepository.deleteTour(tour);
                    Toast.makeText(this, "Tour Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}