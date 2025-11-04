package com.example.prm392_final_prj.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.dto.request.MapMarker;
import com.example.prm392_final_prj.utils.MapMode;
import com.example.prm392_final_prj.fragment.GoogleMapsFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * [SỬA ĐỔI]
 * Implement interface MapInteractionListener (phiên bản linh hoạt)
 */
public class MapPickerActivity extends AppCompatActivity implements GoogleMapsFragment.MapInteractionListener {

    // Key để Activity khác "gọi"
    public static final String ARG_MODE = "ARG_MODE";
    public static final String ARG_MARKERS = "ARG_MARKERS";

    // Key cho kết quả trả về
    public static final String RESULT_LATITUDE = "latitude";
    public static final String RESULT_LONGITUDE = "longitude";
    public static final String RESULT_MARKER_ID = "marker_id";

    private Button btnConfirmLocation;
    private TextView tvMapInstruction;
    private GoogleMapsFragment mapFragment;
    private MapMode mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sử dụng layout mới (xem file XML bên dưới)
        setContentView(R.layout.activity_map_picker);

        btnConfirmLocation = findViewById(R.id.btnConfirmLocation);
        tvMapInstruction = findViewById(R.id.tvMapInstruction);

        // 1. Đọc dữ liệu từ Intent
        mMode = (MapMode) getIntent().getSerializableExtra(ARG_MODE);
        ArrayList<MapMarker> markers = getIntent().getParcelableArrayListExtra(ARG_MARKERS);

        // Chế độ Picker phải là PICK_LOCATION hoặc SELECT_EXISTING_MARKER
        if (mMode == null || mMode == MapMode.VIEW) {
            Toast.makeText(this, "Lỗi: Chế độ Picker không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Cấu hình UI dựa trên chế độ
        setupUIForMode();

        // 3. Thêm Fragment một cách linh động
        if (savedInstanceState == null) {
            // Dùng newInstance để truyền tham số
            mapFragment = GoogleMapsFragment.newInstance(mMode, markers);
            getSupportFragmentManager().beginTransaction()
                    // Thêm Fragment vào "khung"
                    .replace(R.id.map_container, mapFragment)
                    .commit();
        } else {
            // Lấy lại tham chiếu nếu Activity bị xoay
            mapFragment = (GoogleMapsFragment) getSupportFragmentManager().findFragmentById(R.id.map_container);
        }

        // 4. Xử lý nút xác nhận
        btnConfirmLocation.setOnClickListener(v -> handleConfirm());
    }

    // Cấu hình văn bản hướng dẫn
    private void setupUIForMode() {
        if (mMode == MapMode.PICK_LOCATION) {
            tvMapInstruction.setText("Chạm vào bản đồ để chọn vị trí");
        } else if (mMode == MapMode.SELECT_EXISTING_MARKER) {
            tvMapInstruction.setText("Chạm vào một marker để chọn");
        }
    }

    // Xử lý khi bấm nút "Xác nhận"
    private void handleConfirm() {
        // Đảm bảo fragment đã được khởi tạo
        if (mapFragment == null) return;

        Intent resultIntent = new Intent();

        if (mMode == MapMode.PICK_LOCATION) {
            // Hỏi Fragment tọa độ đã chọn
            LatLng pickedLocation = mapFragment.getPickedLocation();
            if (pickedLocation != null) {
                resultIntent.putExtra(RESULT_LATITUDE, pickedLocation.latitude);
                resultIntent.putExtra(RESULT_LONGITUDE, pickedLocation.longitude);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        } else if (mMode == MapMode.SELECT_EXISTING_MARKER) {
            // Hỏi Fragment ID marker đã chọn
            String selectedId = mapFragment.getSelectedMarkerId();
            if (selectedId != null) {
                resultIntent.putExtra(RESULT_MARKER_ID, selectedId);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }
    }

    /**
     * [SỬA ĐỔI]
     * Implement hàm của MapInteractionListener
     * (thay vì OnLocationSelectedListener)
     */
    @Override
    public void onMapInteraction() {
        // Kích hoạt nút bấm khi Fragment báo có tương tác
        btnConfirmLocation.setEnabled(true);
    }
}