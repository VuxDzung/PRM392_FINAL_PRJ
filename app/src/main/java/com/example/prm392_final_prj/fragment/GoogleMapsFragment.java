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

    private static final String ARG_MODE = "ARG_MODE";
    private static final String ARG_MARKERS = "ARG_MARKERS";

    private GoogleMap mMap;
    private MapMode mMode;
    private ArrayList<MapMarker> mMarkers;

    // Biến lưu trạng thái
    private Marker mPickedMarker;
    private LatLng mPickedLocation;
    private String mSelectedMarkerId;


    public interface MapInteractionListener {

        void onMapInteraction();
    }


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


    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.getUiSettings().setMapToolbarEnabled(false);


            addInitialMarkers();


            switch (mMode) {
                case VIEW:

                    break;
                case PICK_LOCATION:

                    setupPickLocationMode();
                    break;
                case SELECT_EXISTING_MARKER:

                    setupSelectMarkerMode();
                    break;
            }
        }
    };


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


    private void setupSelectMarkerMode() {
        mMap.setOnMarkerClickListener(marker -> {
            mSelectedMarkerId = (String) marker.getTag();
            marker.showInfoWindow();

            if (getActivity() instanceof MapInteractionListener) {
                ((MapInteractionListener) getActivity()).onMapInteraction();
            }
            return true;
        });
    }



    public LatLng getPickedLocation() {
        return mPickedLocation;
    }

    public String getSelectedMarkerId() {
        return mSelectedMarkerId;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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