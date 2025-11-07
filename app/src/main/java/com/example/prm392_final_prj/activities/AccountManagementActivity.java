package com.example.prm392_final_prj.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.adapter.AccountListAdapter;
import com.example.prm392_final_prj.dao.AppDatabase;
import com.example.prm392_final_prj.entity.UserEntity;
import com.example.prm392_final_prj.repository.UserRepository;

import java.util.*;

public class AccountManagementActivity extends NavigationBaseActivity {
    private RecyclerView recyclerView;
    private AccountListAdapter adapter;
    private EditText searchBar;
    private UserRepository userRepository;
    private List<UserEntity> allUsers = new ArrayList<>();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        loadAllAccounts();
    }

    private void initView(){
        recyclerView = findViewById(R.id.accountRecyclerView);
        searchBar = findViewById(R.id.searchBar);
        userRepository = new UserRepository(getApplication());

        adapter = new AccountListAdapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> filterAccounts(s.toString());
                handler.postDelayed(searchRunnable, 300);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadAllAccounts() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<UserEntity> users = userRepository.getAll();
            runOnUiThread(() -> {
                allUsers.clear();
                allUsers.addAll(users);
                adapter.setAccountList(allUsers);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private void filterAccounts(String query) {
        List<UserEntity> filtered = new ArrayList<>();
        for (UserEntity u : allUsers) {
            if (u.getEmail().toLowerCase().contains(query.toLowerCase())
                    || u.getFirstname().toLowerCase().contains(query.toLowerCase())
                    || u.getLastname().toLowerCase().contains(query.toLowerCase())
                    || u.getPhone().contains(query)) {
                filtered.add(u);
            }
        }
        adapter.setAccountList(filtered);
        adapter.notifyDataSetChanged();
    }

    public void deleteUser(UserEntity user) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa tài khoản")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        AppDatabase.getDatabase(getApplication()).userDao().delete(user);
                        runOnUiThread(this::loadAllAccounts);
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}