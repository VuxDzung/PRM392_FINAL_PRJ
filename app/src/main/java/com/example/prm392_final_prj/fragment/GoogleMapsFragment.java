package com.example.prm392_final_prj.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.prm392_final_prj.R;
import com.example.prm392_final_prj.dto.request.MapMarker;
import com.example.prm392_final_prj.utils.MapMode;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class GoogleMapsFragment extends Fragment {

    // Key để truyền tham số
    private static final String ARG_MODE = "ARG_MODE";
    private static final String ARG_MARKERS = "ARG_MARKERS";

    private GoogleMap mMap;
    private MapMode mMode;
    private ArrayList<MapMarker> mMarkers;

    // Biến lưu trạng thái
    private Marker mPickedMarker;       // Marker cho chế độ PICK_LOCATION
    private LatLng mPickedLocation;     // Tọa độ cho chế độ PICK_LOCATION
    private String mSelectedMarkerId;   // ID cho chế độ SELECT_EXISTING_MARKER

    // Interface để giao tiếp ngược lên Activity
    public interface MapInteractionListener {
        // Báo cho Activity biết 1 tương tác (chọn điểm/chọn marker) đã xảy ra
        void onMapInteraction();
    }

    /**
     * Factory Method - Cách chuẩn để tạo Fragment với tham số.
     */
    public static GoogleMapsFragment newInstance(MapMode mode, @Nullable ArrayList<MapMarker> markers) {
        GoogleMapsFragment fragment = new GoogleMapsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MODE, mode);
        if (markers != null) {
            args.putParcelableArrayList(ARG_MARKERS, markers);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMode = (MapMode) getArguments().getSerializable(ARG_MODE);
            mMarkers = getArguments().getParcelableArrayList(ARG_MARKERS);
        }
        if (mMode == null) mMode = MapMode.VIEW;
        if (mMarkers == null) mMarkers = new ArrayList<>();
    }

    /**
     * Logic chính sẽ nằm trong onMapReady, dựa trên mMode
     */
    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.getUiSettings().setMapToolbarEnabled(false);

            // 1. Thêm tất cả marker được truyền vào
            addInitialMarkers();

            // 2. Cấu hình tương tác dựa trên chế độ
            switch (mMode) {
                case VIEW:
                    // Chế độ VIEW: Không làm gì thêm.
                    break;
                case PICK_LOCATION:
                    // Chế độ PICK: Cho phép chọn 1 điểm mới
                    setupPickLocationMode();
                    break;
                case SELECT_EXISTING_MARKER:
                    // Chế độ SELECT: Cho phép chọn 1 marker có sẵn
                    setupSelectMarkerMode();
                    break;
            }
        }
    };

    // Hàm thêm các marker ban đầu và zoom camera
    private void addInitialMarkers() {
        if (mMarkers.isEmpty()) {
            LatLng hanoi = new LatLng(21.028511, 105.804817);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hanoi, 10));
            return;
        }

        if (mMarkers.size() == 1) {
            MapMarker marker = mMarkers.get(0);
            LatLng pos = new LatLng(marker.getLatitude(), marker.getLongitude());
            mMap.addMarker(new MarkerOptions().position(pos).title(marker.getTitle())).setTag(marker.getId());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
        } else {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (MapMarker marker : mMarkers) {
                LatLng pos = new LatLng(marker.getLatitude(), marker.getLongitude());
                mMap.addMarker(new MarkerOptions().position(pos).title(marker.getTitle())).setTag(marker.getId());
                builder.include(pos);
            }
            // Zoom để thấy tất cả marker
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100)); // 100px padding
        }
    }

    // Cài đặt cho chế độ PICK_LOCATION
    private void setupPickLocationMode() {
        mMap.setOnMapClickListener(latLng -> {
            mPickedLocation = latLng;
            if (mPickedMarker == null) {
                mPickedMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Vị trí đã chọn"));
            } else {
                mPickedMarker.setPosition(latLng);
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            // Báo cho Activity mẹ (MapPickerActivity) để kích hoạt nút "Confirm"
            if (getActivity() instanceof MapInteractionListener) {
                ((MapInteractionListener) getActivity()).onMapInteraction();
            }
        });
    }

    // Cài đặt cho chế độ SELECT_EXISTING_MARKER
    private void setupSelectMarkerMode() {
        mMap.setOnMarkerClickListener(marker -> {
            mSelectedMarkerId = (String) marker.getTag(); // Lưu lại ID của marker đã chọn
            marker.showInfoWindow();

            // Báo cho Activity mẹ (MapPickerActivity) để kích hoạt nút "Confirm"
            if (getActivity() instanceof MapInteractionListener) {
                ((MapInteractionListener) getActivity()).onMapInteraction();
            }
            return true; // true = đã xử lý, không di chuyển camera tự động
        });
    }

    // --- Các hàm Public để Activity lấy kết quả ---

    public LatLng getPickedLocation() {
        return mPickedLocation; // Trả về tọa độ (cho PICK_LOCATION)
    }

    public String getSelectedMarkerId() {
        return mSelectedMarkerId; // Trả về ID (cho SELECT_EXISTING_MARKER)
    }

    // --- Các hàm vòng đời (giữ nguyên) ---
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Dùng layout fragment_google_maps (file XML bạn đã cung cấp)
        return inflater.inflate(R.layout.fragment_google_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}