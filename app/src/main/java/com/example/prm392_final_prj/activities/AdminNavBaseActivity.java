package com.example.prm392_final_prj.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.activities.TourBookingAdmin.TourBookingListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminNavBaseActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupBottomNavigation(int currentItemId) {
        bottomNavigationView = findViewById(R.id.admin_bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(currentItemId);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.nav_analytics) {
                intent = new Intent(this, AnalyticsActivity.class);
            } else if (itemId == R.id.nav_accounts) {
                intent = new Intent(this, AccountManagementActivity.class);
            } else if (itemId == R.id.nav_tours) {

            } else if (itemId == R.id.nav_bookings) {
                intent = new Intent(this, TourBookingListActivity.class);
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                return true;
            }

            return false;
        });
    }
}