package com.example.prm392_final_prj.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_final_prj.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class NavigationBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    protected void setupBottomNavigation(int currentNavItemId) {
        BottomNavigationView navView = findViewById(R.id.bottom_navigation_view);

        if (navView != null) {
            navView.setSelectedItemId(currentNavItemId);

            navView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();


                Intent intent = null;

                if (itemId == R.id.nav_home) {
                    intent = new Intent(this, HomeActivity.class);
                } else if (itemId == R.id.nav_profile) {

                    intent = new Intent(this, UserProfileActivity.class);
                } else if (itemId == R.id.nav_notifications) {

                    return true;
                } else if (itemId == R.id.nav_more) {
                    intent = new Intent(this, MoreOptionsActivity.class);
                }

                if (intent != null) {

                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                }
                return true;
            });
        }
    }
}