package com.example.prm392_final_prj.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.adapter.NotificationAdapter;
import com.example.prm392_final_prj.entity.NotificationEntity;
import com.example.prm392_final_prj.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.*;

public class NotificationActivity extends NavigationBaseActivity {
    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private NotificationRepository repository;
    private int currentUserId = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        setupBottomNavigation(R.id.nav_notifications);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        repository = new NotificationRepository(getApplication());

        rvNotifications = findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(this, repository);
        rvNotifications.setAdapter(adapter);

        loadNotifications();
    }
    private void loadNotifications() {
        repository.getNotificationsForUser(currentUserId).observe(this, notifications -> {
            adapter.setNotiList(notifications);
        });
    }
}