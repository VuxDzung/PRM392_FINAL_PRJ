package com.example.prm392_final_prj.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.activities.TourBooking.BookingStatusActivity;
import com.example.prm392_final_prj.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;

public class MoreOptionsActivity extends NavigationBaseActivity {

    private MaterialCardView btnChangePassword, btnAboutUs, btnInformation, btnBookingStatus, btnLogout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_more_options);
        
        // Setup Bottom Navigation
        setupBottomNavigation(R.id.nav_more);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);

        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnAboutUs = findViewById(R.id.btnAboutUs);
        btnInformation = findViewById(R.id.btnInformation);
        btnBookingStatus = findViewById(R.id.btnBookingStatus);
        btnLogout = findViewById(R.id.btnLogout);

        btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        btnAboutUs.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutUsActivity.class));
        });

        btnInformation.setOnClickListener(v -> {
            startActivity(new Intent(this, InformationActivity.class));
        });

        btnBookingStatus.setOnClickListener(v -> {
            startActivity(new Intent(this, BookingStatusActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            // Clear session
            sessionManager.clear();
            
            // Redirect to Login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
