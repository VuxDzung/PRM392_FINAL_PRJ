package com.example.prm392_final_prj.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prm392_final_prj.R;
// SỬA: Thêm các import cần thiết
import com.example.prm392_final_prj.dto.request.MapMarker;
import com.example.prm392_final_prj.entity.TourScheduleEntity;
import com.example.prm392_final_prj.fragment.GoogleMapsFragment;
import com.example.prm392_final_prj.repository.TourRepository;
import com.example.prm392_final_prj.utils.MapMode;

import java.util.ArrayList;
import java.util.List;

public class MapScheduleActivity extends AppCompatActivity {

    // SỬA: Thêm hằng số để nhận Tour ID (giống TourDetailActivity)
    public static final String EXTRA_TOUR_ID = "tour_id";

    private TourRepository tourRepository;
    private int tourId = -1;
    private boolean isMapLoaded = false; // Flag để đảm bảo map chỉ load 1 lần

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // SỬA: Khởi tạo repository
        tourRepository = new TourRepository(getApplication());

        // SỬA: Lấy tourId từ Intent
        tourId = getIntent().getIntExtra(EXTRA_TOUR_ID, -1);

        if (tourId == -1) {
            Toast.makeText(this, "Invalid Tour ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // SỬA: Tải dữ liệu schedule và gắn Fragment
        loadSchedulesAndSetupMap();
    }

    /**
     * SỬA: Thêm hàm để tải schedules từ DB và thiết lập bản đồ
     */
    private void loadSchedulesAndSetupMap() {
        tourRepository.getSchedulesForTour(tourId).observe(this, schedules -> {

            // Chỉ load bản đồ 1 lần duy nhất khi có dữ liệu
            // và isMapLoaded == false
            if (!isMapLoaded && schedules != null && !schedules.isEmpty()) {
                isMapLoaded = true; // Đặt flag để không load lại

                // Bước 1: Chuyển đổi List<TourScheduleEntity> thành ArrayList<MapMarker>
                ArrayList<MapMarker> mapMarkers = new ArrayList<>();
                for (TourScheduleEntity schedule : schedules) {
                    // Chỉ thêm vào bản đồ nếu schedule có tọa độ hợp lệ
                    if (schedule.hasValidCoordinates()) {
                        mapMarkers.add(new MapMarker(
                                String.valueOf(schedule.getId()), // ID của marker
                                schedule.getLatitude(),           // Latitude
                                schedule.getLongitude(),          // Longitude
                                schedule.getPlace()               // Tiêu đề của marker
                        ));
                    }
                }

                // Nếu không có marker nào có tọa độ, thông báo và không làm gì
                if (mapMarkers.isEmpty()) {
                    Toast.makeText(this, "No schedules with locations to display", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Bước 2: Khởi tạo GoogleMapsFragment với chế độ VIEW và danh sách marker
                GoogleMapsFragment mapFragment = GoogleMapsFragment.newInstance(MapMode.VIEW, mapMarkers);

                // Bước 3: Thêm Fragment vào container trong file XML
                // *** LƯU Ý QUAN TRỌNG: ***
                // Bạn cần đảm bảo file R.layout.activity_map_schedule có một container
                // (ví dụ: FrameLayout hoặc FragmentContainerView) với ID là "map_container"
                // để code bên dưới hoạt động.
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.map_container, mapFragment)
                        .commit();
            }
        });
    }
}